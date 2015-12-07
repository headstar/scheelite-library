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
 * @param <T> entity type
 * @param <U> state id type
 */
public class StateMachineBuilder<T, U> {

    private static int MAX_TRANSITIONS_PER_EVENT_DEFAULT = 50;

    private MutableStateTree<T, U> stateTree;
    private MutableTransitionMap<T, U> transitionMap;
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

    public StateMachineBuilder<T, U> withCompositeState(State<T, U> state, InitialAction<T> initialAction,
                                                        State<T, U> defaultSubState, State<T, U>... subStates) {
        Preconditions.checkNotNull(initialAction);
        return withCompositeState(state, Optional.of(initialAction), defaultSubState, subStates);
    }

    public StateMachineBuilder<T, U> withCompositeState(State<T, U> state, State<T, U> defaultSubState, State<T, U>... subStates) {
        return withCompositeState(state, Optional.<InitialAction<T>>absent(), defaultSubState, subStates);
    }

    private StateMachineBuilder<T, U> withCompositeState(State<T, U> superState, Optional<? extends InitialAction<T>> defaultAction,
                                                         State<T, U> defaultSubState, State<T, U>... subStates) {
        Preconditions.checkNotNull(superState);
        Preconditions.checkNotNull(defaultAction);
        Preconditions.checkNotNull(defaultSubState);
        for (State<T, U> state : subStates) {
            Preconditions.checkNotNull(state);
        }

        Preconditions.checkState(!stateTree.isAncestorOf(defaultSubState, superState), "state super state of supplied super state: %s", defaultSubState);
        for (State<T, U> state : subStates) {
            Preconditions.checkState(!stateTree.isAncestorOf(state, superState), "state super state of supplied super state: %s", state);
        }

        validateState(superState);
        validateState(defaultSubState);
        for (State<T, U> state : subStates) {
            validateState(state);
        }

        stateTree.addState(superState);
        stateTree.addState(defaultSubState, superState);
        for (State<T, U> state : subStates) {
            stateTree.addState(state, superState);
        }

        transitionMap.addInitialTransition(new InitialTransition<T, U>(Optional.of(superState), defaultSubState, defaultAction));
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

    public StateMachineBuilder<T, U> withTransition(State<T, U> fromState, State<T, U> toState, Class<?> triggerEventClass) {
        return withTransition(fromState, toState, Optional.<Class<?>>of(triggerEventClass), Optional.<Guard<T>>absent(), Optional.<Action<T>>absent());
    }

    public StateMachineBuilder<T, U> withTransition(State<T, U> fromState, State<T, U> toState, Class<?> triggerEventClass, Guard<T> guard) {
        return withTransition(fromState, toState, Optional.<Class<?>>of(triggerEventClass), Optional.<Guard<T>>of(guard), Optional.<Action<T>>absent());
    }

    public StateMachineBuilder<T, U> withTransition(State<T, U> fromState, State<T, U> toState, Class<?> triggerEventClass, Action<T> action) {
        return withTransition(fromState, toState, Optional.<Class<?>>of(triggerEventClass), Optional.<Guard<T>>absent(), Optional.<Action<T>>of(action));
    }

    public StateMachineBuilder<T, U> withTransition(State<T, U> fromState, State<T, U> toState, Class<?> triggerEventClass, Guard<T> guard, Action<T> action) {
        return withTransition(fromState, toState, Optional.<Class<?>>of(triggerEventClass), Optional.<Guard<T>>of(guard), Optional.<Action<T>>of(action));
    }

    public StateMachineBuilder<T, U> withTransition(State<T, U> fromState, State<T, U> toState, Guard<T> guard, Action<T> action) {
        return withTransition(fromState, toState, Optional.<Class<?>>absent(), Optional.<Guard<T>>of(guard), Optional.<Action<T>>of(action));
    }

    public StateMachineBuilder<T, U> withTransition(State<T, U> fromState, State<T, U> toState, Guard<T> guard) {
        return withTransition(fromState, toState, Optional.<Class<?>>absent(), Optional.<Guard<T>>of(guard), Optional.<Action<T>>absent());
    }

    public StateMachineBuilder<T, U> withTransition(State<T, U> fromState, State<T, U> toState, Action<T> action) {
        return withTransition(fromState, toState, Optional.<Class<?>>absent(), Optional.<Guard<T>>absent(), Optional.<Action<T>>of(action));
    }

    public StateMachineBuilder<T, U> withTransition(State<T, U> fromState, State<T, U> toState) {
        return withTransition(fromState, toState, Optional.<Class<?>>absent(), Optional.<Guard<T>>absent(), Optional.<Action<T>>absent());
    }

    private StateMachineBuilder<T, U> withTransition(State<T, U> fromState, State<T, U> toState, Optional<Class<?>> triggerEventClass,
                                                    Optional<? extends Guard<T>> guard, Optional<? extends Action<T>> action) {
        return withTransition(new Transition<T, U>(fromState, toState, TransitionType.EXTERNAL, triggerEventClass, guard, action));
    }

    public StateMachineBuilder<T, U> withLocalTransition(State<T, U> fromState, State<T, U> toState, Class<?> triggerEventClass) {
        return withLocalTransition(fromState, toState, Optional.<Class<?>>of(triggerEventClass), Optional.<Guard<T>>absent(), Optional.<Action<T>>absent());
    }

    public StateMachineBuilder<T, U> withLocalTransition(State<T, U> fromState, State<T, U> toState, Class<?> triggerEventClass, Guard<T> guard) {
        return withLocalTransition(fromState, toState, Optional.<Class<?>>of(triggerEventClass), Optional.<Guard<T>>of(guard), Optional.<Action<T>>absent());
    }

    public StateMachineBuilder<T, U> withLocalTransition(State<T, U> fromState, State<T, U> toState, Class<?> triggerEventClass, Action<T> action) {
        return withLocalTransition(fromState, toState, Optional.<Class<?>>of(triggerEventClass), Optional.<Guard<T>>absent(), Optional.<Action<T>>of(action));
    }

    public StateMachineBuilder<T, U> withLocalTransition(State<T, U> fromState, State<T, U> toState, Class<?> triggerEventClass, Guard<T> guard, Action<T> action) {
        return withLocalTransition(fromState, toState, Optional.<Class<?>>of(triggerEventClass), Optional.<Guard<T>>of(guard), Optional.<Action<T>>of(action));
    }

    public StateMachineBuilder<T, U> withLocalTransition(State<T, U> fromState, State<T, U> toState, Guard<T> guard, Action<T> action) {
        return withLocalTransition(fromState, toState, Optional.<Class<?>>absent(), Optional.<Guard<T>>of(guard), Optional.<Action<T>>of(action));
    }

    public StateMachineBuilder<T, U> withLocalTransition(State<T, U> fromState, State<T, U> toState, Guard<T> guard) {
        return withLocalTransition(fromState, toState, Optional.<Class<?>>absent(), Optional.<Guard<T>>of(guard), Optional.<Action<T>>absent());
    }

    public StateMachineBuilder<T, U> withLocalTransition(State<T, U> fromState, State<T, U> toState, Action<T> action) {
        return withLocalTransition(fromState, toState, Optional.<Class<?>>absent(), Optional.<Guard<T>>absent(), Optional.<Action<T>>of(action));
    }

    public StateMachineBuilder<T, U> withLocalTransition(State<T, U> fromState, State<T, U> toState) {
        return withLocalTransition(fromState, toState, Optional.<Class<?>>absent(), Optional.<Guard<T>>absent(), Optional.<Action<T>>absent());
    }

    private StateMachineBuilder<T, U> withLocalTransition(State<T, U> fromState, State<T, U> toState, Optional<Class<?>> triggerEventClass,
                                                     Optional<? extends Guard<T>> guard, Optional<? extends Action<T>> action) {
        return withTransition(new Transition<T, U>(fromState, toState, TransitionType.LOCAL, triggerEventClass, guard, action));
    }

    private StateMachineBuilder<T, U> withTransition(Transition<T, U> transition) {
        Preconditions.checkNotNull(transition);

        stateTree.addState(transition.getFromState());
        stateTree.addState(transition.getToState());
        transitionMap.addTransition(transition);
        return this;
    }

    public StateMachineBuilder<T, U> withInitialTransition(State<T, U> toState) {
        return withInitialTransition(toState, Optional.<InitialAction<T>>absent());
    }

    public StateMachineBuilder<T, U> withInitialTransition(State<T, U> toState, InitialAction<T> action) {
        return withInitialTransition(toState, Optional.of(action));
    }


    private StateMachineBuilder<T, U> withInitialTransition(State<T, U> toState, Optional<? extends InitialAction<T>> actionOpt) {
        Preconditions.checkNotNull(toState);
        Preconditions.checkNotNull(actionOpt);

        validateState(toState);

        InitialTransition<T, U> initialTransition = new InitialTransition<T, U>(Optional.<State<T, U>>absent(), toState, actionOpt);
        transitionMap.addInitialTransition(initialTransition);
        stateTree.addState(initialTransition.getToState());
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
        if (transitionMap.getInitialTransitionFromRoot() == null) {
            throw new IllegalStateException("no initial transition added");
        }

        if(stateTree.isChild(transitionMap.getInitialTransitionFromRoot().getToState())) {
            throw new IllegalStateException(String.format("initial transition toState cannot be a child: toState=%s", transitionMap.getInitialTransitionFromRoot().getToState()));
        }

        Set<State<T, U>> states = stateTree.getStates();

        // check state id equals and state equals relation
        checkStateEquals();

        // check local transitions are valid
        checkLocalTransitions();

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
            // if transition is local, source state and target state must be descendants
            if(transition.getTransitionType().equals(TransitionType.LOCAL)
                    && !isRelated(transition.getFromState(), transition.getToState())) {
                throw new IllegalStateException(String.format("states must be related in local transition: fromState=[%s], toState=[%s]", transition.getFromState(), transition.getToState()));
            }
        }
    }

    private boolean isRelated(State<T, U> a, State<T, U> b) {
        return stateTree.isDescendantOf(a, b) || stateTree.isDescendantOf(b, a);
    }


}
