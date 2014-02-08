package org.headstar.scheelite;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

public class DefaultStateMachine<T extends Entity<U>, U> implements StateMachine<T> {

    private static final Logger logger = LoggerFactory.getLogger(DefaultStateMachine.class);

    private final Optional<State<T, U>> STATE_ABSENT = Optional.<State<T, U>>absent();
    private final Optional<State<T, U>> ROOT_STATE = STATE_ABSENT;

    private final ImmutableMap<U, State<T, U>> states;  // state id -> state
    private final ImmutableSet<Transition<T, U>> transitions;
    private final ImmutableMultimap<State<T, U>, Transition<T, U>> transitionsFromState; // state -> transitions from state
    private final ImmutableMap<State<T, U>, InitialTransition<T, U>> initialTransitionsFromState; // state -> transitions from state

    private final ImmutableMap<State<T, U>, State<T, U>> subStateSuperStateMap;
    private final MultipleTransitionsTriggeredResolver<T, U> multipleTransitionsTriggeredResolver;

    protected DefaultStateMachine(StateMachineBuilder<T, U> builder) {
        this.states = createStatesMap(builder.getStates());
        this.transitions = ImmutableSet.copyOf(builder.getTransitions());
        this.transitionsFromState = createTransitionsFromMap(builder.getTransitions());
        this.initialTransitionsFromState = createInitialTransitionsFromMap(builder.getInitialTransitions());
        this.multipleTransitionsTriggeredResolver = builder.getMultipleTransitionsTriggeredResolver();
        this.subStateSuperStateMap = ImmutableMap.copyOf(builder.getSubStateSuperStateMap());
    }

    private void handleEvent(State<T, U> sourceState, T entity, Optional<?> eventOpt) {
        if (eventOpt.isPresent()) {
            Object event = eventOpt.get();
            boolean eventHandled = false;
            Optional<State<T, U>> stateOpt = Optional.of(sourceState);
            do {
                State<T, U> state = stateOpt.get();
                logger.debug("handling event: entity={}, event={}, state={}", entity.getId(), event, state.getId());
                eventHandled = state.onEvent(entity, event);
                stateOpt = getSuperState(state);
            } while (!eventHandled && stateOpt.isPresent());
        }
    }

    private void process(T entity, Optional<?> eventOpt) {
        checkNotNull(entity);
        checkNotNull(eventOpt);

        // get current state
        U stateIdentifier = entity.getState();
        if (stateIdentifier == null) {
            throw new IllegalStateException(String.format("stateIdentifier is null"));
        }

        State<T, U> currentState = getState(stateIdentifier);

        // handle event
        handleEvent(currentState, entity, eventOpt);

        // process triggered transition (if any)
        Optional<Transition<T, U>> triggeredTransitionOpt = getTriggeredTransition(currentState, entity, eventOpt);
        if (triggeredTransitionOpt.isPresent()) {
            Transition<T, U> triggeredTransition = triggeredTransitionOpt.get();
            logger.debug("transition triggered: entity={}, transition={}", entity.getId(), triggeredTransition.getName());

            State<T, U> mainSourceState = triggeredTransition.getFromState();
            State<T, U> mainTargetState = triggeredTransition.getToState();

            // get lowest common ancestor (LCA) for main source state and main target state
            Optional<State<T, U>> lowestCommonAncestor = getLowestCommonAncestor(mainSourceState, mainTargetState);

            // exit states up until, but not including LCA
            Optional<State<T, U>> exitStateOpt = Optional.of(currentState);
            do {
                State<T, U> exitState = exitStateOpt.get();
                logger.debug("exiting state: entity={}, state={}", entity.getId(), exitState.getId());
                exitState.onExit(entity);
                exitStateOpt = getSuperState(exitState);
            } while (!exitStateOpt.equals(lowestCommonAncestor));

            // execute transition action (if any)
            Optional<? extends Action<T>> actionOpt = triggeredTransition.getAction();
            if (actionOpt.isPresent()) {
                Action<T> action = actionOpt.get();
                logger.debug("executing action: entity={}, action={}", entity.getId(), action.getName());
                action.execute(entity, eventOpt);
            }

            // enter target state
            List<State<T, U>> statesToEnter = getPathFromSuperState(lowestCommonAncestor, mainTargetState);
            for (State<T, U> s : statesToEnter) {
                logger.debug("entering state: entity={}, state={}", entity.getId(), mainTargetState.getId());
                s.onEntry(entity);
            }

            // 'drill' down to sub states
            State<T, U> endState = mainTargetState;
            Optional<? extends InitialTransition<T, U>> initialTransitionOpt = getInitialTransition(endState);
            while (initialTransitionOpt.isPresent()) {
                InitialTransition<T, U> it = initialTransitionOpt.get();
                if (it.getAction().isPresent()) {
                    InitialAction<T> action = it.getAction().get();
                    logger.debug("executing initial action: entity={}, action={}", entity.getId(), action.getName());
                    action.execute(entity);
                }
                endState = it.getToState();
                logger.debug("entering state: entity={}, state={}", entity.getId(), endState.getId());
                endState.onEntry(entity);
                initialTransitionOpt = getInitialTransition(endState);
            }

            // update entity
            entity.setState(endState.getId());

            process(entity, Optional.absent());
        }

    }

    @Override
    public void process(T entity, Object event) {
        checkNotNull(entity);
        checkNotNull(event);

        process(entity, Optional.of(event));
    }

    protected State<T, U> getState(U stateId) {
        State<T, U> state = states.get(stateId);
        if (state == null) {
            throw new IllegalStateException(String.format("state unknown: state=%s", stateId));
        }
        return state;
    }

    protected Optional<State<T, U>> getLowestCommonAncestor(State<T, U> stateA, State<T, U> stateB) {
        List<State<T, U>> fromAToRoot = getPathFromSubState(stateA, ROOT_STATE);
        List<State<T, U>> fromBToRoot = getPathFromSubState(stateB, ROOT_STATE);

        Optional<State<T, U>> result = ROOT_STATE;
        Iterator<State<T, U>> iter = fromBToRoot.iterator();
        while (iter.hasNext()) {
            State<T, U> nextState = iter.next();
            if (fromAToRoot.contains(nextState)) {
                result = Optional.of(nextState);
            }
        }
        return result;
    }

    protected List<State<T, U>> getPathFromSuperState(Optional<State<T, U>> superState, State<T, U> subState) {
        List<State<T, U>> res = getPathFromSubState(subState, superState);
        Collections.reverse(res);
        return res;
    }


    protected List<State<T, U>> getPathFromSubState(State<T, U> subState, Optional<State<T, U>> superState) {
        List<State<T, U>> res = Lists.newArrayList();
        Optional<State<T, U>> stateOpt = Optional.of(subState);
        do {
            State<T, U> state = stateOpt.get();
            res.add(state);
            stateOpt = getSuperState(state);
        } while (!stateOpt.equals(superState));

        return res;
    }

    protected Optional<State<T, U>> getSuperState(State<T, U> state) {
        State<T, U> superState = subStateSuperStateMap.get(state);
        return Optional.<State<T, U>>fromNullable(superState);
    }

    protected Optional<InitialTransition<T, U>> getInitialTransition(State<T, U> state) {
        InitialTransition<T, U> transition = initialTransitionsFromState.get(state);
        return Optional.<InitialTransition<T, U>>fromNullable(transition);
    }


    protected ImmutableMap<U, State<T, U>> createStatesMap(Set<State<T, U>> states) {
        Map<U, State<T, U>> map = Maps.newHashMap();
        for (State<T, U> state : states) {
            map.put(state.getId(), state);
        }
        return new ImmutableMap.Builder<U, State<T, U>>().putAll(map).build();
    }

    protected ImmutableMultimap<State<T, U>, Transition<T, U>> createTransitionsFromMap(Set<Transition<T, U>> transitions) {
        Multimap<State<T, U>, Transition<T, U>> map = ArrayListMultimap.create();
        for (Transition<T, U> transition : transitions) {
            map.put(transition.getFromState(), transition);
        }
        return new ImmutableMultimap.Builder<State<T, U>, Transition<T, U>>().putAll(map).build();
    }

    protected ImmutableMap<State<T, U>, InitialTransition<T, U>> createInitialTransitionsFromMap(Set<InitialTransition<T, U>> transitions) {
        Map<State<T, U>, InitialTransition<T, U>> map = Maps.newHashMap();
        for (InitialTransition<T, U> transition : transitions) {
            map.put(transition.getFromState(), transition);
        }
        return new ImmutableMap.Builder<State<T, U>, InitialTransition<T, U>>().putAll(map).build();
    }


    protected Optional<Transition<T, U>> getTriggeredTransition(State<T, U> state, T entity, Optional<?> event) {
        Collection<Transition<T, U>> transitionsFromCurrentState = transitionsFromState.get(state);

        Collection<Transition<T, U>> triggeredTransitions = Lists.newArrayList(Iterables.filter(transitionsFromCurrentState,
                new GuardIsAccepting<T, U>(entity, event)));
        if (triggeredTransitions.isEmpty()) {
            return Optional.absent();
        } else if (triggeredTransitions.size() == 1) {
            return Optional.of(triggeredTransitions.iterator().next());
        } else {
            return Optional.of(multipleTransitionsTriggeredResolver
                    .resolve(entity, event, triggeredTransitions));
        }
    }

    private static class GuardIsAccepting<T, U> implements Predicate<Transition<T, U>> {

        private final T entity;
        private final Optional<?> event;

        private GuardIsAccepting(T entity, Optional<?> event) {
            this.entity = entity;
            this.event = event;
        }

        @Override
        public boolean apply(Transition<T, U> input) {
            if (input.getGuard().isPresent()) {
                return input.getGuard().get().accept(entity, event);
            } else {
                return true;
            }
        }
    }
}
