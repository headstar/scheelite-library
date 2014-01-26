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
    private final ImmutableMap<U, State<T, U>> states;  // state id -> state
    private final ImmutableSet<Transition<T, U>> transitions;
    private final ImmutableMultimap<U, Transition<T, U>> transitionsFromState; // state id -> transitions from state
    private final MultipleTransitionsTriggeredResolver<T, U> multipleTransitionsTriggeredResolver;

    protected DefaultStateMachine(StateMachineBuilder<T, U> builder) {
        this.states = createStatesMap(builder.getStates());
        this.transitions = ImmutableSet.copyOf(builder.getTransitions());
        this.transitionsFromState = createTransitionsFromMap(builder.getTransitions());
        this.multipleTransitionsTriggeredResolver = builder.getMultipleTransitionsTriggeredResolver();
    }

    @Override
    public void process(T entity, Object event) {
        checkNotNull(entity);
        checkNotNull(event);

        // get current state
        U stateIdentifier = entity.getState();
        if (stateIdentifier == null) {
            throw new IllegalStateException(String.format("stateIdentifier is null"));
        }

        State<T, U> sourceState = getState(stateIdentifier);

        // handle event
        logger.debug("handling event: entity={}, event={}, state={}", entity.getId(), event, sourceState.getId());
        sourceState.onEvent(entity, event);

        // process triggered transition (if any)
        Optional<Transition<T, U>> triggeredTransitionOpt = getTriggeredTransition(stateIdentifier, entity, event);
        if (triggeredTransitionOpt.isPresent()) {
            Transition<T, U> triggeredTransition = triggeredTransitionOpt.get();
            logger.debug("transition triggered: entity={}, transition={}", entity.getId(), triggeredTransition.getName());

            // get target state
            State<T, U> targetState = states.get(triggeredTransition.getToState());
            if (targetState == null) {
                throw new IllegalStateException(String.format("target state unknown: state=%s", triggeredTransition.getToState()));
            }

            // get lowest common ancestor (LCA) between current state and next state
            Optional<State<T, U>> lowestCommonAncestor = getLowestCommonAncestor(sourceState, targetState);

            // exit states up until, but not including LCA
            Optional<State<T, U>> exitStateOpt = Optional.of(sourceState);
            do {
                State<T, U> exitState = exitStateOpt.get();
                logger.debug("exiting state: entity={}, state={}", entity.getId(), exitState.getId());
                exitState.onExit(entity);
                exitStateOpt = exitState.getSuperState().isPresent() ? Optional.of(getState(exitState.getSuperState().get())) : STATE_ABSENT;
            } while(!exitStateOpt.equals(lowestCommonAncestor));

            // execute transition action (if any)
            Optional<? extends Action<T>> actionOpt = triggeredTransition.getAction();
            if (actionOpt.isPresent()) {
                Action<T> action = actionOpt.get();
                logger.debug("executing action: entity={}, action={}", entity.getId(), action.getName());
                action.execute(entity, event);
            }

            // enter target state
            List<State<T, U>> statesToEnter = getPathFromSuperState(lowestCommonAncestor, targetState);
            for(State<T, U> s : statesToEnter) {
                logger.debug("entering state: entity={}, state={}", entity.getId(), targetState.getId());
                s.onEntry(entity);
            }

            // 'drill' down to sub states
            State<T, U> endState = targetState;
            Optional<InitialTransition<T, U>> initialTransition = targetState.getInitialTransition();
            while(initialTransition.isPresent()) {
                InitialTransition<T, U> it = initialTransition.get();
                if(it.getAction().isPresent()) {
                    InitialAction<T> action = it.getAction().get();
                    action.equals(entity);
                }
                endState = getState(it.getToState());
                endState.onEntry(entity);
                initialTransition = endState.getInitialTransition();
            }

            // update entity
            entity.setState(endState.getId());
       }
    }

    protected State<T, U> getState(U stateId) {
        State<T, U> state = states.get(stateId);
        if(state ==  null) {
            throw new IllegalStateException(String.format("state unknown: state=%s", stateId));
        }
        return state;
    }

    protected Optional<State<T, U>> getLowestCommonAncestor(State<T, U> stateA, State<T, U>  stateB) {
        List<State<T, U>> fromAToRoot = getPathFromSuperState(STATE_ABSENT, stateA);
        Collections.reverse(fromAToRoot);
        List<State<T, U>> fromBToRoot = getPathFromSuperState(STATE_ABSENT, stateB);
        Collections.reverse(fromBToRoot);

        Optional<State<T, U>> result = STATE_ABSENT;
        Iterator<State<T, U>> iter = fromBToRoot.iterator();
        while(iter.hasNext()) {
            if(fromAToRoot.contains(iter.next())) {
                if(iter.hasNext()) {
                    result = Optional.of(iter.next());
                }
            }
        }
        return result;
    }

    protected List<State<T, U>> getPathFromSuperState(Optional<State<T, U>> superState, State<T, U> subState) {
        List<State<T, U>> res = Lists.newArrayList();
        Optional<State<T, U>> state = Optional.of(subState);
        do {
            res.add(0, state.get());
            Optional<U> next = state.get().getSuperState();
            state = next.isPresent() ? Optional.of(getState(next.get())) : STATE_ABSENT;
        } while(!state.equals(superState));

        return res;
    }

    protected ImmutableMap<U, State<T, U>> createStatesMap(Set<State<T, U>> states) {
        Map<U, State<T, U>> map = Maps.newHashMap();
        for (State<T, U> state : states) {
            map.put(state.getId(), state);
        }
        return new ImmutableMap.Builder<U, State<T, U>>().putAll(map).build();
    }

    protected ImmutableMultimap<U, Transition<T, U>> createTransitionsFromMap(Set<Transition<T, U>> transitions) {
        Multimap<U, Transition<T, U>> map = ArrayListMultimap.create();
        for (Transition<T, U> transition : transitions) {
            map.put(transition.getFromState(), transition);
        }
        return new ImmutableMultimap.Builder<U, Transition<T, U>>().putAll(map).build();
    }

    protected Optional<Transition<T, U>> getTriggeredTransition(U stateIdentifier, T entity, Object event) {
        Collection<Transition<T, U>> transitionsFromCurrentState = transitionsFromState.get(stateIdentifier);

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
        private final Object event;

        private GuardIsAccepting(T entity, Object event) {
            this.entity = entity;
            this.event = event;
        }

        @Override
        public boolean apply(Transition<T, U> input) {
            if(input.getGuard().isPresent()) {
                return input.getGuard().get().accept(entity, event);
            } else {
                return true;
            }
        }
    }
}
