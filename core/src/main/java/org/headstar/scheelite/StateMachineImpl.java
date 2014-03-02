package org.headstar.scheelite;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class StateMachineImpl<T, U> implements StateMachine<T, U> {

    private static final Logger logger = LoggerFactory.getLogger(StateMachineImpl.class);

    private final StateTree<T, U> stateTree;
    private final TransitionMap<T, U> transitionMap;
    private final MultipleTransitionsTriggeredResolver<T, U> multipleTransitionsTriggeredResolver;
    private final int maxTransitionsPerEvent;

    protected StateMachineImpl(StateMachineBuilder<T, U> builder) {
        this.stateTree = new ImmutableStateTree<T, U>(builder.getStateTree());
        this.transitionMap = new ImmutableTransitionMap<T, U>(builder.getTransitionMap());
        this.multipleTransitionsTriggeredResolver = builder.getMultipleTransitionsTriggeredResolver();
        this.maxTransitionsPerEvent = builder.getMaxTransitionsPerEvent();
    }

    private void handleEvent(State<T, U> sourceState, T entity, Optional<?> eventOpt) {
        if (eventOpt.isPresent()) {
            Object event = eventOpt.get();
            boolean eventHandled = false;
            Optional<State<T, U>> stateOpt = Optional.of(sourceState);
            do {
                State<T, U> state = stateOpt.get();
                logger.debug("handling event: entity={}, state={}, event={}", entity, state.getId(), event);
                eventHandled = state.onEvent(entity, event);
                stateOpt = stateTree.getParent(state);
            } while (!eventHandled && stateOpt.isPresent());
        }
    }

    private ProcessEventResult<U> process(T entity, U stateId, Optional<?> eventOpt, int transitionCount) {
        checkNotNull(entity);
        checkNotNull(stateId);
        checkNotNull(eventOpt);

        if(transitionCount >= maxTransitionsPerEvent) {
            throw new MaxTransitionsPerEventException();
        }

        Optional<State<T, U>> currentStateOpt = stateTree.getState(stateId);
        if (!currentStateOpt.isPresent()) {
            throw new InvalidStateIdException(String.format("no state found for stateId: stateId=%s", stateId));
        }
        State<T, U> currentState = currentStateOpt.get();

        // handle event
        handleEvent(currentState, entity, eventOpt);

        // processEvent triggered transition (if any)
        Optional<Transition<T, U>> triggeredTransitionOpt = getTriggeredTransition(currentState, entity, eventOpt);
        if (triggeredTransitionOpt.isPresent()) {
            Transition<T, U> triggeredTransition = triggeredTransitionOpt.get();
            logger.debug("transition triggered: entity={}, state={}, transition={}, transitionType={}", entity, currentState.getId(), triggeredTransition, triggeredTransition.getTransitionType().name());

            State<T, U> mainSourceState = triggeredTransition.getFromState();
            State<T, U> mainTargetState = triggeredTransition.getToState();

            // get lowest common ancestor (LCA) for main source state and main target state
            Optional<State<T, U>> lowestCommonAncestor = stateTree.getLowestCommonAncestor(mainSourceState, mainTargetState);

            // exit sources states
            List<State<T, U>> sourceStates = getSourceStates(currentState, mainSourceState, mainTargetState, lowestCommonAncestor, triggeredTransition.getTransitionType());
            for (State<T, U> state : sourceStates) {
                logger.debug("exiting state: entity={}, state={}", entity, state.getId());
                state.onExit(entity);
            }

            // execute transition action (if any)
            Optional<? extends Action<T>> actionOpt = triggeredTransition.getAction();
            if (actionOpt.isPresent()) {
                Action<T> action = actionOpt.get();
                logger.debug("executing action: entity={}, action={}", entity, action.getName());
                action.execute(entity, eventOpt);
            }

            // enter target states
            List<State<T, U>> targetStates = getTargetStates(mainSourceState, mainTargetState, lowestCommonAncestor, triggeredTransition.getTransitionType());
            for (State<T, U> state : targetStates) {
                logger.debug("entering state: entity={}, state={}", entity, state.getId());
                state.onEntry(entity);
            }

            // handle default transitions
            U nextStateId = handleDefaultTransitions(Optional.of(mainTargetState), entity);

            return new ProcessEventResult<U>(true, nextStateId);
        } else {
            return new ProcessEventResult<U>(false, currentState.getId());
        }
    }

    @Override
    public U processInitialTransition(T entity) {
        return handleInitialTransition(entity);
    }

    @Override
    public U processEvent(T entity, U stateId, Object event) {
        checkNotNull(entity);
        checkNotNull(stateId);
        checkNotNull(event);

        int transitionCount = 0;
        ProcessEventResult<U> res = process(entity, stateId, Optional.of(event), transitionCount++);
        while(res.isContinueProcessing()) {
            res = process(entity, res.getNextStateId(), Optional.absent(), transitionCount++);
        }
        return res.getNextStateId();
    }

    private U handleInitialTransition(T entity) {
        return handleDefaultTransitions(Optional.<State<T, U>>absent(), entity);
    }

    private U handleDefaultTransitions(Optional<State<T, U>> startState, T entity) {
        Optional<InitialTransition<T, U>> initialTransitionOpt;
        State<T, U> endState = null;
        if (startState.isPresent()) {
            endState = startState.get();
            initialTransitionOpt = transitionMap.getInitialTransitionFromState(endState);
        } else {
            initialTransitionOpt = Optional.of(transitionMap.getInitialTransitionFromRoot());
        }
        while (initialTransitionOpt.isPresent()) {
            InitialTransition<T, U> it = initialTransitionOpt.get();
            logger.debug("default transition: transition={}", it);
            if (it.getAction().isPresent()) {
                InitialAction<T> action = it.getAction().get();
                logger.debug("executing default action: entity={}, action={}", entity, action.getName());
                action.execute(entity);
            }
            endState = it.getToState();
            logger.debug("entering state: entity={}, state={}", entity, endState.getId());
            endState.onEntry(entity);
            initialTransitionOpt = transitionMap.getInitialTransitionFromState(endState);
        }


        return endState.getId();
    }

    private List<State<T, U>> getSourceStates(State<T, U> currentState, State<T, U> mainSourceState, State<T, U> mainTargetState,
                                              Optional<State<T, U>> lowestCommonAncestorOpt, TransitionType transitionType) {
        List<State<T, U>> res = stateTree.getPathBetween(currentState, lowestCommonAncestorOpt);
        if (lowestCommonAncestorOpt.isPresent()) {
            State<T, U> lowestCommonAncestor = lowestCommonAncestorOpt.get();
            if (TransitionType.EXTERNAL.equals(transitionType) &&
                    (mainSourceState.equals(lowestCommonAncestor) || mainTargetState.equals(lowestCommonAncestor))) {
                res.add(lowestCommonAncestor);
            }
        }
        return res;
    }

    private List<State<T, U>> getTargetStates(State<T, U> mainSourceState, State<T, U> mainTargetState,
                                              Optional<State<T, U>> lowestCommonAncestorOpt, TransitionType transitionType) {
        List<State<T, U>> res = stateTree.getPathBetween(mainTargetState, lowestCommonAncestorOpt);
        if (lowestCommonAncestorOpt.isPresent()) {
            State<T, U> lowestCommonAncestor = lowestCommonAncestorOpt.get();
            if (TransitionType.EXTERNAL.equals(transitionType) &&
                    (mainSourceState.equals(lowestCommonAncestor) || mainTargetState.equals(lowestCommonAncestor))) {
                res.add(lowestCommonAncestor);
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

        List<Transition<T, U>> triggeredTransitions = Lists.newArrayList(Iterables.filter(transitions, new TransitionGuardIsAccepting<T, U>(entity, event)));
        if (triggeredTransitions.isEmpty()) {
            return Optional.absent();
        } else if (triggeredTransitions.size() == 1) {
            return Optional.of(triggeredTransitions.get(0));
        } else {
            return Optional.of(multipleTransitionsTriggeredResolver.resolve(entity, event, triggeredTransitions));
        }
    }

    private static class TransitionGuardIsAccepting<T, U> implements Predicate<Transition<T, U>> {

        private final GuardArgs<T> guardArgs;

        private TransitionGuardIsAccepting(T entity, Optional<?> event) {
            this.guardArgs = new GuardArgs<T>(entity, event);
        }

        @Override
        public boolean apply(Transition<T, U> input) {
            if (input.getGuard().isPresent()) {
                return input.getGuard().get().apply(guardArgs);
            } else {
                // no guard present
                return true;
            }
        }
    }

    private static class ProcessEventResult<U> {
        private final boolean continueProcessing;
        private final U nextStateId;

        private ProcessEventResult(boolean continueProcessing, U nextStateId) {
            this.continueProcessing = continueProcessing;
            this.nextStateId = nextStateId;
        }

        public boolean isContinueProcessing() {
            return continueProcessing;
        }

        public U getNextStateId() {
            return nextStateId;
        }
    }
}
