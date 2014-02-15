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

    private final ImmutableMultimap<State<T, U>, Transition<T, U>> transitionsFromState; // state -> transitions from state
    private final ImmutableMap<State<T, U>, InitialTransition<T, U>> initialTransitionsFromState; // state -> transitions from state


    private final StateTree<T, U> stateTree;
    private final MultipleTransitionsTriggeredResolver<T, U> multipleTransitionsTriggeredResolver;

    protected DefaultStateMachine(StateMachineBuilder<T, U> builder) {
        this.transitionsFromState = createTransitionsFromMap(builder.getTransitions());
        this.initialTransitionsFromState = createInitialTransitionsFromMap(builder.getInitialTransitions());
        this.multipleTransitionsTriggeredResolver = builder.getMultipleTransitionsTriggeredResolver();
        this.stateTree = new ImmutableStateTree<>(builder.getStateTree().getMap());
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
                stateOpt = stateTree.getParent(state);
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

        State<T, U> currentState = stateTree.getState(stateIdentifier);

        // handle event
        handleEvent(currentState, entity, eventOpt);

        // process triggered transition (if any)
        Optional<Transition<T, U>> triggeredTransitionOpt = getTriggeredTransition(currentState, entity, eventOpt);
        if (triggeredTransitionOpt.isPresent()) {
            Transition<T, U> triggeredTransition = triggeredTransitionOpt.get();
            logger.debug("transition triggered: entity={}, transition={}, transitionType={}", entity.getId(), triggeredTransition.getName(), triggeredTransition.getTransitionType().name());

            State<T, U> mainSourceState = triggeredTransition.getFromState();
            State<T, U> mainTargetState = triggeredTransition.getToState();

            // get lowest common ancestor (LCA) for main source state and main target state
            Optional<State<T, U>> lowestCommonAncestor = stateTree.getLowestCommonAncestor(mainSourceState, mainTargetState);

            // exit sources states
            List<State<T, U>> sourceStates = getSourceStates(currentState, mainSourceState, mainTargetState, lowestCommonAncestor, triggeredTransition.getTransitionType());
            for(State<T,U> state : sourceStates) {
                logger.debug("exiting state: entity={}, state={}", entity.getId(), state.getId());
                state.onExit(entity);
            }

            // execute transition action (if any)
            Optional<? extends Action<T>> actionOpt = triggeredTransition.getAction();
            if (actionOpt.isPresent()) {
                Action<T> action = actionOpt.get();
                logger.debug("executing action: entity={}, action={}", entity.getId(), action.getName());
                action.execute(entity, eventOpt);
            }

            // enter target states
            List<State<T, U>> targetStates = getTargetStates(mainSourceState, mainTargetState, lowestCommonAncestor, triggeredTransition.getTransitionType());
            for (State<T, U> state : targetStates) {
                logger.debug("entering state: entity={}, state={}", entity.getId(), state.getId());
                state.onEntry(entity);
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

    private List<State<T,U>> getSourceStates(State<T,U> currentState, State<T,U> mainSourceState, State<T,U> mainTargetState,
                                             Optional<State<T, U>> lowestCommonAncestorOpt, TransitionType transitionType) {
        List<State<T, U>> res = stateTree.getPathBetween(currentState, lowestCommonAncestorOpt);
        if(lowestCommonAncestorOpt.isPresent()) {
            State<T,U> lowestCommonAncestor = lowestCommonAncestorOpt.get();
            if(TransitionType.LOCAL.equals(transitionType) &&
                    (mainSourceState.equals(lowestCommonAncestor) || mainTargetState.equals(lowestCommonAncestor))) {
                res.remove(lowestCommonAncestor);
            }
        }
        return res;
    }

    private List<State<T,U>> getTargetStates(State<T,U> mainSourceState, State<T,U> mainTargetState,
                                             Optional<State<T, U>> lowestCommonAncestorOpt, TransitionType transitionType) {
        List<State<T, U>> res = stateTree.getPathBetween(mainTargetState, lowestCommonAncestorOpt);
        if(lowestCommonAncestorOpt.isPresent()) {
            State<T,U> lowestCommonAncestor = lowestCommonAncestorOpt.get();
            if(TransitionType.LOCAL.equals(transitionType) &&
                    (mainSourceState.equals(lowestCommonAncestor) || mainTargetState.equals(lowestCommonAncestor))) {
                res.remove(lowestCommonAncestor);
            }
        }
        Collections.reverse(res);
        return res;
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


    protected Optional<Transition<T, U>> getTriggeredTransition(State<T, U> currentState, T entity, Optional<?> event) {
        List<State<T, U>> fromCurrentStateToRoot = stateTree.getPathToRootState(currentState);
        Collection<Transition<T, U>> transitions = Lists.newArrayList();
        for(State<T, U> state : fromCurrentStateToRoot) {
            transitions.addAll(transitionsFromState.get(state));
        }

        List<Transition<T, U>> triggeredTransitions = Lists.newArrayList(Iterables.filter(transitions, new GuardIsAccepting<T, U>(entity, event)));
        if (triggeredTransitions.isEmpty()) {
            return Optional.absent();
        } else if (triggeredTransitions.size() == 1) {
            return Optional.of(triggeredTransitions.get(0));
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
