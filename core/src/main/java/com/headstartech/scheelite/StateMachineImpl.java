package com.headstartech.scheelite;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

class StateMachineImpl<T, U> implements StateMachine<T, U> {

    private static final Logger logger = LoggerFactory.getLogger(StateMachineImpl.class);

    private final StateTree<T, U> stateTree;
    private final TransitionMap<T, U> transitionMap;
    private final MultipleTransitionsTriggeredResolver<T, U> multipleTransitionsTriggeredResolver;
    private final StateMachineConfiguration<T, U> configuration;
    private final int maxTransitionsPerEvent;

    protected StateMachineImpl(StateMachineBuilder<T, U> builder) {
        this.stateTree = new ImmutableStateTree<T, U>(builder.getStateTree());
        this.transitionMap = new ImmutableTransitionMap<T, U>(builder.getTransitionMap());
        this.multipleTransitionsTriggeredResolver = builder.getMultipleTransitionsTriggeredResolver();
        this.maxTransitionsPerEvent = builder.getMaxTransitionsPerEvent();
        this.configuration = new StateMachineConfiguration<T, U>(stateTree, transitionMap);
    }

    @Override
    public StateMachineConfiguration<T, U> getConfiguration() {
        return configuration;
    }

    @Override
    public U start(T context) throws ExecutionException {
        return handleInitialTransition(context);
    }

    @Override
    public U processEvent(T context, U stateId, Object event) throws ExecutionException {
        checkNotNull(context);
        checkNotNull(stateId);
        checkNotNull(event);

        int transitionCount = 0;
        ProcessEventResult<U> res = process(context, stateId, Optional.of(event), transitionCount++);
        while (res.isContinueProcessing()) {
            Optional<Object> nextEvent = Optional.absent();
            U nextStateId = res.getNextStateId();
            State<T, U> nextState = stateTree.getState(nextStateId).get();
            Optional<State<T, U>> nextStateParentOpt = stateTree.getParent(nextState);
            if(nextState instanceof FinalState && nextStateParentOpt.isPresent()) {
                nextEvent = Optional.<Object>of(new CompositeStateCompleted<U>(nextState.getId()));
            }
            res = process(context, res.getNextStateId(), nextEvent, transitionCount++);
        }
        return res.getNextStateId();
    }

    private void handleEvent(State<T, U> sourceState, T context, Optional<?> eventOpt) throws ExecutionException {
        if (eventOpt.isPresent()) {
            Object event = eventOpt.get();
            boolean eventHandled = false;
            Optional<State<T, U>> stateOpt = Optional.of(sourceState);
            do {
                State<T, U> state = stateOpt.get();
                logger.debug("handling event: context={}, state={}, event={}", context, state.getId(), event);
                try {
                    eventHandled = state.onEvent(context, event);
                } catch(Exception e) {
                    throw new ExecutionException(e);
                }
                stateOpt = stateTree.getParent(state);
            } while (!eventHandled && stateOpt.isPresent() && !stateOpt.get().equals(stateTree.getRootState()));
        }
    }

    private ProcessEventResult<U> process(T context, U stateId, Optional<?> eventOpt, int transitionCount) throws ExecutionException {
        checkNotNull(context);
        checkNotNull(stateId);
        checkNotNull(eventOpt);

        if (transitionCount >= maxTransitionsPerEvent) {
            throw new MaxTransitionsPerEventException();
        }

        Optional<State<T, U>> currentStateOpt = stateTree.getState(stateId);
        if (!currentStateOpt.isPresent()) {
            throw new UnknownStateIdException(String.format("no state found for stateId: stateId=%s", stateId));
        }
        State<T, U> currentState = currentStateOpt.get();

        // handle event
        handleEvent(currentState, context, eventOpt);

        // processEvent triggered transition (if any)
        Optional<Transition<T, U>> triggeredTransitionOpt = getTriggeredTransition(currentState, context, eventOpt);
        if (triggeredTransitionOpt.isPresent()) {
            Transition<T, U> triggeredTransition = triggeredTransitionOpt.get();
            logger.debug("transition triggered: context={}, state={}, transition={}, transitionType={}", context, currentState.getId(), triggeredTransition, triggeredTransition.getTransitionType().name());

            State<T, U> mainSourceState = triggeredTransition.getMainSourceState();
            State<T, U> mainTargetState = triggeredTransition.getMainTargetState();

            // get lowest common ancestor (LCA) for main source state and main target state
            State<T, U> lowestCommonAncestor = stateTree.getLowestCommonAncestor(mainSourceState, mainTargetState);

            // exit sources states
            List<State<T, U>> sourceStates = getSourceStates(currentState, mainSourceState, mainTargetState, lowestCommonAncestor, triggeredTransition.getTransitionType());
            for (State<T, U> state : sourceStates) {
                logger.debug("exiting state: context={}, state={}", context, state.getId());
                try {
                    state.onExit(context);
                } catch(Exception e) {
                    throw new ExecutionException(e);
                }
            }

            // execute transition action (if any)
            Optional<? extends Action<T>> actionOpt = triggeredTransition.getAction();
            if (actionOpt.isPresent()) {
                Action<T> action = actionOpt.get();
                if(logger.isDebugEnabled()) {
                    logger.debug("executing action: context={}, action={}", context, getActionName(action));
                }
                try {
                    action.execute(context, eventOpt);
                } catch(Exception e) {
                    throw new ExecutionException(e);
                }
            }

            // enter target states
            List<State<T, U>> targetStates = getTargetStates(mainSourceState, mainTargetState, lowestCommonAncestor, triggeredTransition.getTransitionType());
            for (State<T, U> state : targetStates) {
                logger.debug("entering state: context={}, state={}", context, state.getId());
                try {
                    state.onEntry(context);
                } catch(Exception e) {
                    throw new ExecutionException(e);
                }
            }

            // handle initial transitions
            U nextStateId = handleInitialTransitions(mainTargetState, context);

            return new ProcessEventResult<U>(true, nextStateId);
        } else {
            return new ProcessEventResult<U>(false, currentState.getId());
        }
    }
    
    private U handleInitialTransition(T context) throws ExecutionException {
        return handleInitialTransitions(stateTree.getRootState(), context);
    }

    private U handleInitialTransitions(State<T, U> startState, T context) throws ExecutionException {
        State<T, U> currentState = startState;
        Optional<Transition<T, U>> initialTransitionOpt = transitionMap.getInitialTransitionFromState(currentState);
        while (initialTransitionOpt.isPresent()) {
            Transition<T, U> it = initialTransitionOpt.get();
            logger.debug("initial transition: transition={}", it);
            if (it.getAction().isPresent()) {
                Action<T> action = it.getAction().get();
                if(logger.isDebugEnabled()) {
                    logger.debug("executing action for initial transition: context={}, action={}", context, getActionName(action));
                }
                try {
                    action.execute(context, Optional.absent());
                } catch(Exception e) {
                    throw new ExecutionException(e);
                }
            }
            currentState = it.getMainTargetState();
            logger.debug("entering state: context={}, state={}", context, currentState.getId());
            try {
                currentState.onEntry(context);
            } catch(Exception e) {
                throw new ExecutionException(e);
            }
            initialTransitionOpt = transitionMap.getInitialTransitionFromState(currentState);
        }


        return currentState.getId();
    }

    private List<State<T, U>> getSourceStates(State<T, U> currentState, State<T, U> mainSourceState, State<T, U> mainTargetState,
                                              State<T, U> lowestCommonAncestor, TransitionType transitionType) {
        List<State<T, U>> res = stateTree.getPathToAncestor(currentState, lowestCommonAncestor, false);
        if (TransitionType.EXTERNAL.equals(transitionType) &&
                (mainSourceState.equals(lowestCommonAncestor) || mainTargetState.equals(lowestCommonAncestor))) {
            res.add(lowestCommonAncestor);
        }
        return res;
    }

    private List<State<T, U>> getTargetStates(State<T, U> mainSourceState, State<T, U> mainTargetState,
                                              State<T, U> lowestCommonAncestor, TransitionType transitionType) {
        List<State<T, U>> res = stateTree.getPathToAncestor(mainTargetState, lowestCommonAncestor, false);
        if (TransitionType.EXTERNAL.equals(transitionType) &&
                (mainSourceState.equals(lowestCommonAncestor) || mainTargetState.equals(lowestCommonAncestor))) {
            res.add(lowestCommonAncestor);
        }
        Collections.reverse(res);
        return res;
    }

    private Optional<Transition<T, U>> getTriggeredTransition(State<T, U> currentState, T context, Optional<?> event) throws ExecutionException {
        List<State<T, U>> fromCurrentStateToRoot = stateTree.getPathToAncestor(currentState, stateTree.getRootState(), false);
        List<Transition<T, U>> transitions = Lists.newArrayList();
        for (State<T, U> state : fromCurrentStateToRoot) {
            for(Transition<T, U> t : transitionMap.getTransitionsFromState(state)) {
                if(!t.getTransitionType().equals(TransitionType.INITIAL)) {
                    transitions.add(t);
                }
            }
        }

        List<Transition<T, U>> triggeredTransitions = filterTransitions(transitions, context, event);
        if (triggeredTransitions.isEmpty()) {
            return Optional.absent();
        } else if (triggeredTransitions.size() == 1) {
            return Optional.of(triggeredTransitions.get(0));
        } else {
            try {
                return Optional.of(multipleTransitionsTriggeredResolver.resolve(currentState.getId(), context, event, triggeredTransitions));
            } catch(Exception e) {
                throw new ExecutionException(e);
            }
        }
    }

    List<Transition<T, U>> filterTransitions(Collection<Transition<T, U>> transitions, T context, Optional<?> event) throws ExecutionException {
        List<Transition<T, U>> res = Lists.newArrayList();

        for(Transition<T, U> t : transitions) {
            if(isTransitionTriggered(t, context, event)) {
                res.add(t);
            }
        }

        return res;
    }

    private boolean isTransitionTriggered(Transition<T, U> t, T context, Optional<?> event) throws ExecutionException {
        if (t.getTriggerEventClass().isPresent()) {
            Class<?> triggerEventClass = t.getTriggerEventClass().get();
            if (event.isPresent()) {
                if (!triggerEventClass.isInstance(event.get())) {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            if(event.isPresent()) {
                return false;
            }
        }
        if (t.getGuard().isPresent()) {
            try {
                return t.getGuard().get().evaluate(context, event);
            } catch(Exception e) {
                throw new ExecutionException(e);
            }
        } else {
            // no guard present
            return true;
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

    private String getActionName(Action<T> action) {
        return action.getClass().getName();
    }

}
