package com.headstartech.scheelite;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Queue;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * <p>A builder of {@link StateMachine} instances.
 *
 * <p>Usage example: <pre>   {@code
 *
 *  StateMachine<CalculatorEntity, CalculatorState> fsm = StateMachineBuilder.<CalculatorEntity, CalculatorState>newBuilder()
 *      .withInitialTransition(onState)
 *      .withCompositeState(onState, initState, operand1State, operand2State, opEnteredState, resultState)
 *      .withTransition(initState, operand1State, eventInstanceOf(DigitEvent.class))
 *      .withTransition(operand1State, opEnteredState, eventInstanceOf(OperationEvent.class))
 *      .withTransition(opEnteredState, operand2State, eventInstanceOf(DigitEvent.class))
 *      .withTransition(operand2State, resultState, eventInstanceOf(ResultEvent.class))
 *      .withTransition(resultState, operand1State, eventInstanceOf(DigitEvent.class))
 *      .withTransition(resultState, opEnteredState, eventInstanceOf(OperationEvent.class))
 *      .withTransition(onState, offState, eventInstanceOf(OffEvent.class))
 *      .build();}</pre>
 *
 * @param <T> context type
 * @param <U> state id type
 */
public class StateMachineBuilder<T, U> {

    private static int MAX_TRANSITIONS_PER_EVENT_DEFAULT = 50;

    private final MutableStateTree<T, U> stateTree;
    private final MutableTransitionMap<T, U> transitionMap;
    private MultipleTransitionsTriggeredResolver<T, U> multipleTransitionsTriggeredResolver;
    private int maxTransitionsPerEvent = MAX_TRANSITIONS_PER_EVENT_DEFAULT;

    public static <T, U> StateMachineBuilder<T, U> newBuilder() {
        return new StateMachineBuilder<T, U>();
    }

    private StateMachineBuilder() {
        stateTree = new MutableStateTree<T, U>();
        transitionMap = new MutableTransitionMap<T, U>();
        multipleTransitionsTriggeredResolver = new ThrowExceptionResolver<T, U>();
    }

    public StateMachineBuilder<T, U> withCompositeState(State<T, U> state, State<T, U> defaultSubState, State<T, U>... subStates) {
        return withCompositeState(state, null, defaultSubState, subStates);
    }

    public StateMachineBuilder<T, U> withCompositeState(State<T, U> superState, Action<T> initialAction,
                                                         State<T, U> defaultSubState, State<T, U>... subStates) {
        Preconditions.checkNotNull(superState);
        Preconditions.checkNotNull(defaultSubState);
        for (State<T, U> state : subStates) {
            Preconditions.checkNotNull(state);
        }

        if(superState instanceof FinalState) {
            throw new IllegalArgumentException("superState cannot be a final state");
        }

        Preconditions.checkState(!stateTree.isAncestorOf(defaultSubState, superState), "default substate is super state/equal to supplied super state: %s", defaultSubState);
        for (State<T, U> substate : subStates) {
            Preconditions.checkState(!stateTree.isAncestorOf(substate, superState), "substate is super state/equal to supplied super state: %s", substate);
        }

        validateState(superState);
        validateState(defaultSubState);
        for (State<T, U> state : subStates) {
            validateState(state);
        }

        stateTree.addState(superState);
        stateTree.addState(defaultSubState, superState);
        for (State<T, U> substate : subStates) {
            stateTree.addState(substate, superState);
        }

        withInitialTransition(superState, defaultSubState, initialAction);
        return this;
    }

    public StateMachineBuilder<T, U> withMaxTransitions(int maxTransitions) {
        checkArgument(maxTransitions > 0, "maxTransitionsPerEvent must be > 0");
        this.maxTransitionsPerEvent = maxTransitions;
        return this;
    }

    int getMaxTransitionsPerEvent() {
        return maxTransitionsPerEvent;
    }

    public StateMachineBuilder<T, U> withTransition(State<T, U> mainSourceState, State<T, U> mainTargetState, Class<?> triggerEventClass) {
        return withTransition(mainSourceState, mainTargetState, triggerEventClass, null, null);
    }

    public StateMachineBuilder<T, U> withTransition(State<T, U> mainSourceState, State<T, U> mainTargetState, Class<?> triggerEventClass, Guard<T> guard) {
        return withTransition(mainSourceState, mainTargetState, triggerEventClass, guard, null);
    }

    public StateMachineBuilder<T, U> withTransition(State<T, U> mainSourceState, State<T, U> mainTargetState, Class<?> triggerEventClass, Action<T> action) {
        return withTransition(mainSourceState, mainTargetState, triggerEventClass, null, action);
    }

    public StateMachineBuilder<T, U> withTransition(State<T, U> mainSourceState, State<T, U> mainTargetState, Guard<T> guard, Action<T> action) {
        return withTransition(mainSourceState, mainTargetState, null, guard, action);
    }

    public StateMachineBuilder<T, U> withTransition(State<T, U> mainSourceState, State<T, U> mainTargetState, Guard<T> guard) {
        return withTransition(mainSourceState, mainTargetState, null, guard, null);
    }

    public StateMachineBuilder<T, U> withTransition(State<T, U> mainSourceState, State<T, U> mainTargetState, Action<T> action) {
        return withTransition(mainSourceState, mainTargetState, null, null, action);
    }

    public StateMachineBuilder<T, U> withTransition(State<T, U> mainSourceState, State<T, U> mainTargetState) {
        return withTransition(mainSourceState, mainTargetState, null, null, null);
    }

    public StateMachineBuilder<T, U> withTransition(State<T, U> mainSourceState, State<T, U> mainTargetState, Class<?> triggerEventClass,
                                                    Guard<T> guard, Action<T> action) {
        return withTransition(new Transition<T, U>(mainSourceState, mainTargetState, TransitionType.EXTERNAL, triggerEventClass, guard, action));
    }

    public StateMachineBuilder<T, U> withLocalTransition(State<T, U> mainSourceState, State<T, U> mainTargetState, Class<?> triggerEventClass) {
        return withLocalTransition(mainSourceState, mainTargetState, triggerEventClass, null, null);
    }

    public StateMachineBuilder<T, U> withLocalTransition(State<T, U> mainSourceState, State<T, U> mainTargetState, Class<?> triggerEventClass, Guard<T> guard) {
        return withLocalTransition(mainSourceState, mainTargetState, triggerEventClass, guard, null);
    }

    public StateMachineBuilder<T, U> withLocalTransition(State<T, U> mainSourceState, State<T, U> mainTargetState, Class<?> triggerEventClass, Action<T> action) {
        return withLocalTransition(mainSourceState, mainTargetState, triggerEventClass, null, action);
    }

    public StateMachineBuilder<T, U> withLocalTransition(State<T, U> mainSourceState, State<T, U> mainTargetState, Guard<T> guard, Action<T> action) {
        return withLocalTransition(mainSourceState, mainTargetState, null, guard, action);
    }

    public StateMachineBuilder<T, U> withLocalTransition(State<T, U> mainSourceState, State<T, U> mainTargetState, Guard<T> guard) {
        return withLocalTransition(mainSourceState, mainTargetState, null, guard, null);
    }

    public StateMachineBuilder<T, U> withLocalTransition(State<T, U> mainSourceState, State<T, U> mainTargetState, Action<T> action) {
        return withLocalTransition(mainSourceState, mainTargetState, null, null, action);
    }

    public StateMachineBuilder<T, U> withLocalTransition(State<T, U> mainSourceState, State<T, U> mainTargetState) {
        return withLocalTransition(mainSourceState, mainTargetState, null, null, null);
    }

    public StateMachineBuilder<T, U> withLocalTransition(State<T, U> mainSourceState, State<T, U> mainTargetState, Class<?> triggerEventClass,
                                                     Guard<T> guard, Action<T> action) {
        return withTransition(new Transition<T, U>(mainSourceState, mainTargetState, TransitionType.LOCAL, triggerEventClass, guard, action));
    }

    private StateMachineBuilder<T, U> withTransition(Transition<T, U> transition) {
        Preconditions.checkNotNull(transition);

        if(transition.getMainSourceState() instanceof FinalState) {
            throw new IllegalArgumentException("main source state of transition cannot be a final state");
        }

        stateTree.addState(transition.getMainSourceState());
        stateTree.addState(transition.getMainTargetState());
        transitionMap.addTransition(transition);
        return this;
    }

    public StateMachineBuilder<T, U> withInitialTransition(State<T, U> mainTargetState) {
        return withInitialTransition(mainTargetState, null);
    }

    public StateMachineBuilder<T, U> withInitialTransition(State<T, U> mainTargetState, Action<T> action) {
        return withInitialTransition(stateTree.getRootState(), mainTargetState, action);
    }

    private StateMachineBuilder<T, U> withInitialTransition(State<T, U> mainSourceState, State<T, U> mainTargetState, Action<T> action) {
        Preconditions.checkNotNull(mainSourceState);
        Preconditions.checkNotNull(mainTargetState);

        validateState(mainTargetState);

        Transition<T, U> transition = new Transition<T, U>(mainSourceState, mainTargetState, TransitionType.INITIAL, null, null, action);
        transitionMap.addTransition(transition);
        stateTree.addState(mainTargetState);
        return this;
    }

    public StateMachineBuilder<T, U> withMultipleTransitionsTriggerPolicy(
            MultipleTransitionsTriggeredResolver<T, U> multipleTransitionsTriggeredResolver) {
        Preconditions.checkNotNull(multipleTransitionsTriggeredResolver);
        this.multipleTransitionsTriggeredResolver = multipleTransitionsTriggeredResolver;
        return this;
    }

    public StateMachine<T, U> build() {

        // check we have a top level initial transition state
        if (!transitionMap.getInitialTransitionsFromMap().containsKey(stateTree.getRootState())) {
            throw new IllegalStateException("no initial transition from root state added");
        }

        Transition<T, U> initialTransitionFromRoot = transitionMap.getInitialTransitionsFromMap().get(stateTree.getRootState());
        Optional<State<T, U>> toStateParent = stateTree.getParent(initialTransitionFromRoot.getMainTargetState());
        if(!toStateParent.isPresent() || !(toStateParent.get().equals(stateTree.getRootState()))) {
            throw new IllegalStateException(String.format("super state of initial transition mainTargetState must be root state: mainTargetState=%s", initialTransitionFromRoot.getMainTargetState()));
        }

        Set<State<T, U>> states = stateTree.getStates();

        // check state id equals and state equals relation
        checkStateEquals();

        // check local transitions are valid
        checkLocalTransitions();

        // check all states are reachable from the start state
        checkAllStatesAreReachableFromRootState();

        return new StateMachineImpl<T, U>(this);
    }

    MutableStateTree<T, U> getStateTree() { return stateTree; }

    MutableTransitionMap<T, U> getTransitionMap() { return transitionMap; }

    MultipleTransitionsTriggeredResolver<T, U> getMultipleTransitionsTriggeredResolver() {
        return multipleTransitionsTriggeredResolver;
    }

    private void validateState(State<T, U> state) {
        if (state.getId() == null) {
            throw new IllegalArgumentException(String.format("state identifier cannot be null: state=[%s]", state));
        }
    }

    private void checkStateEquals() {
        Set<State<T, U>> states = stateTree.getStates();
        for (State<T, U> outer : states) {
            for (State<T, U> inner : states) {
                if (!(outer.getId().equals(inner.getId()) == outer.equals(inner))) {
                    throw new IllegalStateException(String.format("states equals not valid: states=[]", states));
                }
            }
        }
    }

    private void checkLocalTransitions() {
        for (Transition<T, U> transition : transitionMap.getTransitions()) {
            // if transition is local, source state and target state must descendants
            if(transition.getTransitionType().equals(TransitionType.LOCAL)
                    && !isRelated(transition.getMainSourceState(), transition.getMainTargetState())) {
                throw new IllegalStateException(String.format("states must be related in local transition: mainSourceState=[%s], mainTargetState=[%s]", transition.getMainSourceState(), transition.getMainTargetState()));
            }
        }
    }

    private boolean isRelated(State<T, U> a, State<T, U> b) {
        return a.equals(b) || stateTree.isDescendantOf(a, b) || stateTree.isDescendantOf(b, a);
    }

    private void checkAllStatesAreReachableFromRootState() {

        State<T, U> startState = stateTree.getRootState();

        Set<State<T, U>> visited = Sets.newHashSet();
        Queue<State<T, U>> queue = new ArrayDeque<State<T, U>>();
        queue.add(startState);
        visited.add(startState);

        Multimap<State<T, U>, State<T, U>> edges = getEdges();

        while (!queue.isEmpty()) {
            State<T, U> w = queue.remove();
            Collection<State<T, U>> neighbours = edges.get(w);
            for (State<T, U> vertex : neighbours) {
                if (!visited.contains(vertex)) {
                    visited.add(vertex);
                    queue.add(vertex);
                }
            }
        }

        Set<State<T, U>> leafs = Sets.newHashSet(Iterables.filter(stateTree.getStates(), new NotParentPredicate()));
        Set<State<T, U>> notVisited = Sets.difference(leafs, visited);
        if (!notVisited.isEmpty()) {
            throw new IllegalStateException(String.format("states not reachable from start state: startState=[%s] states=[%s]", startState,
                    notVisited));
        }
    }

    private Multimap<State<T, U>, State<T, U>> getEdges() {
        Multimap<State<T, U>, State<T, U>> edges = HashMultimap.create();
        for (Transition<T, U> transition : transitionMap.getTransitions()) {
            edges.put(transition.getMainSourceState(), transition.getMainTargetState());
        }
        return edges;
    }

    private class NotParentPredicate implements Predicate<State<T,U>> {
        @Override
        public boolean apply(State<T, U> input) {
            return !stateTree.isParent(input);
        }
    }

}
