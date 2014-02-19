package org.headstar.scheelite;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

public class StateMachineImpl<T extends Entity<U>, U> implements StateMachine<T> {

    private static final Logger logger = LoggerFactory.getLogger(StateMachineImpl.class);

    private final StateTree<T, U> stateTree;
    private final TransitionMap<T, U> transitionMap;
    private final MultipleTransitionsTriggeredResolver<T, U> multipleTransitionsTriggeredResolver;
    private final int maxInvocationCount;

    protected StateMachineImpl(StateMachineBuilder<T, U> builder) {
        this.stateTree = new ImmutableStateTree<T, U>(builder.getStateTree().getMap());
        this.transitionMap = new ImmutableTransitionMap<T, U>(builder.getTransitions(), builder.getDefaultTransitions());
        this.multipleTransitionsTriggeredResolver = builder.getMultipleTransitionsTriggeredResolver();
        this.maxInvocationCount = builder.getMaxTransitions();
    }

    private void handleEvent(State<T, U> sourceState, T entity, Optional<?> eventOpt) {
        if (eventOpt.isPresent()) {
            Object event = eventOpt.get();
            boolean eventHandled = false;
            Optional<State<T, U>> stateOpt = Optional.of(sourceState);
            do {
                State<T, U> state = stateOpt.get();
                logger.debug("handling event: entity={}, state={}, event={}", entity.getEntityId(), state.getId(), event);
                eventHandled = state.onEvent(entity, event);
                stateOpt = stateTree.getParent(state);
            } while (!eventHandled && stateOpt.isPresent());
        }
    }

    private boolean process(T entity, Optional<?> eventOpt, int invocationCount) {
        checkNotNull(entity);
        checkNotNull(eventOpt);

        if(invocationCount > maxInvocationCount) {
            throw new MaxTransitionsException();
        }

        // get current state
        U stateIdentifier = entity.getStateId();
        if (stateIdentifier == null) {
            throw new InvalidStateIdException(String.format("stateId is null: entity=%s", entity.getEntityId()));
        }

        Optional<State<T, U>> currentStateOpt = stateTree.getState(stateIdentifier);
        if (!currentStateOpt.isPresent()) {
            throw new InvalidStateIdException(String.format("no state found for stateId: stateId=%s", stateIdentifier));
        }
        State<T, U> currentState = currentStateOpt.get();

        // handle event
        handleEvent(currentState, entity, eventOpt);

        // process triggered transition (if any)
        Optional<Transition<T, U>> triggeredTransitionOpt = getTriggeredTransition(currentState, entity, eventOpt);
        if (triggeredTransitionOpt.isPresent()) {
            Transition<T, U> triggeredTransition = triggeredTransitionOpt.get();
            logger.debug("transition triggered: entity={}, state={}, transition={}, transitionType={}", entity.getEntityId(), currentState.getId(), triggeredTransition.getName(), triggeredTransition.getTransitionType().name());

            State<T, U> mainSourceState = triggeredTransition.getFromState();
            State<T, U> mainTargetState = triggeredTransition.getToState();

            // get lowest common ancestor (LCA) for main source state and main target state
            Optional<State<T, U>> lowestCommonAncestor = stateTree.getLowestCommonAncestor(mainSourceState, mainTargetState);

            // exit sources states
            List<State<T, U>> sourceStates = getSourceStates(currentState, mainSourceState, mainTargetState, lowestCommonAncestor, triggeredTransition.getTransitionType());
            for (State<T, U> state : sourceStates) {
                logger.debug("exiting state: entity={}, state={}", entity.getEntityId(), state.getId());
                state.onExit(entity);
            }

            // execute transition action (if any)
            Optional<? extends Action<T>> actionOpt = triggeredTransition.getAction();
            if (actionOpt.isPresent()) {
                Action<T> action = actionOpt.get();
                logger.debug("executing action: entity={}, action={}", entity.getEntityId(), action.getName());
                action.execute(entity, eventOpt);
            }

            // enter target states
            List<State<T, U>> targetStates = getTargetStates(mainSourceState, mainTargetState, lowestCommonAncestor, triggeredTransition.getTransitionType());
            for (State<T, U> state : targetStates) {
                logger.debug("entering state: entity={}, state={}", entity.getEntityId(), state.getId());
                state.onEntry(entity);
            }

            // handle default transitions
            handleDefaultTransitions(Optional.of(mainTargetState), entity);

            return true;
        }

        return false;
    }

    @Override
    public void initialTransition(T entity) {
        handleInitialTransition(entity);
    }

    @Override
    public void process(T entity, Object event) {
        checkNotNull(entity);
        checkNotNull(event);

        int invocationCount = 0;
        boolean cont = process(entity, Optional.of(event), ++invocationCount);
        while(cont) {
            cont = process(entity, Optional.absent(), ++invocationCount);
        }
    }

    private void handleInitialTransition(T entity) {
        handleDefaultTransitions(Optional.<State<T, U>>absent(), entity);
    }

    private void handleDefaultTransitions(Optional<State<T, U>> startState, T entity) {
        Optional<DefaultTransition<T, U>> initialTransitionOpt;
        State<T, U> endState = null;
        if (startState.isPresent()) {
            endState = startState.get();
            initialTransitionOpt = transitionMap.getDefaultTransitionFromState(endState);
        } else {
            initialTransitionOpt = Optional.of(transitionMap.getDefaultTransitionFromRoot());
        }
        while (initialTransitionOpt.isPresent()) {
            DefaultTransition<T, U> it = initialTransitionOpt.get();
            logger.debug("default transition: transition={}", it.getName());
            if (it.getAction().isPresent()) {
                DefaultAction<T> action = it.getAction().get();
                logger.debug("executing default action: entity={}, action={}", entity.getEntityId(), action.getName());
                action.execute(entity);
            }
            endState = it.getToState();
            logger.debug("entering state: entity={}, state={}", entity.getEntityId(), endState.getId());
            endState.onEntry(entity);
            initialTransitionOpt = transitionMap.getDefaultTransitionFromState(endState);
        }

        // update entity
        entity.setStateId(endState.getId());
    }

    private List<State<T, U>> getSourceStates(State<T, U> currentState, State<T, U> mainSourceState, State<T, U> mainTargetState,
                                              Optional<State<T, U>> lowestCommonAncestorOpt, TransitionType transitionType) {
        List<State<T, U>> res = stateTree.getPathBetween(currentState, lowestCommonAncestorOpt);
        if (lowestCommonAncestorOpt.isPresent()) {
            State<T, U> lowestCommonAncestor = lowestCommonAncestorOpt.get();
            if (TransitionType.LOCAL.equals(transitionType) &&
                    (mainSourceState.equals(lowestCommonAncestor) || mainTargetState.equals(lowestCommonAncestor))) {
                res.remove(lowestCommonAncestor);
            }
        }
        return res;
    }

    private List<State<T, U>> getTargetStates(State<T, U> mainSourceState, State<T, U> mainTargetState,
                                              Optional<State<T, U>> lowestCommonAncestorOpt, TransitionType transitionType) {
        List<State<T, U>> res = stateTree.getPathBetween(mainTargetState, lowestCommonAncestorOpt);
        if (lowestCommonAncestorOpt.isPresent()) {
            State<T, U> lowestCommonAncestor = lowestCommonAncestorOpt.get();
            if (TransitionType.LOCAL.equals(transitionType) &&
                    (mainSourceState.equals(lowestCommonAncestor) || mainTargetState.equals(lowestCommonAncestor))) {
                res.remove(lowestCommonAncestor);
            }
        }
        Collections.reverse(res);
        return res;
    }

    private Optional<Transition<T, U>> getTriggeredTransition(State<T, U> currentState, T entity, Optional<?> event) {
        List<State<T, U>> fromCurrentStateToRoot = stateTree.getPathToRootState(currentState);
        Collection<Transition<T, U>> transitions = Lists.newArrayList();
        for (State<T, U> state : fromCurrentStateToRoot) {
            transitions.addAll(transitionMap.getTransitionsFromState(state));
        }

        List<Transition<T, U>> triggeredTransitions = Lists.newArrayList(Iterables.filter(transitions, new GuardIsAccepting<T, U>(entity, event)));
        if (triggeredTransitions.isEmpty()) {
            return Optional.absent();
        } else if (triggeredTransitions.size() == 1) {
            return Optional.of(triggeredTransitions.get(0));
        } else {
            return Optional.of(multipleTransitionsTriggeredResolver.resolve(entity, event, triggeredTransitions));
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
