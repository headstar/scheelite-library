package org.headstar.scheelite;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

public class StateMachineBuilder<T extends Entity<U>, U> {

    private State<T, U> startState;
    private final Set<State<T, U>> states;
    private final Set<Transition<T, U>> transitions;
    private MultipleTransitionsTriggeredPolicy<T, U> multipleTransitionsTriggeredPolicy;

    public static <T extends Entity<U>, U> StateMachineBuilder<T, U> newBuilder() {
        return new StateMachineBuilder<T, U>();
    }

    private StateMachineBuilder() {
        states = Sets.newHashSet();
        transitions = Sets.newHashSet();
        multipleTransitionsTriggeredPolicy = new MultipleTransitionsTriggeredThrowException<T, U>();
    }

    public StateMachineBuilder<T, U> withStartState(State<T, U> state) {
        Preconditions.checkState(this.startState == null, "start state was already set to %s", this.startState);
        Preconditions.checkState(!states.contains(state), "state already added %s", state);
        this.startState = Preconditions.checkNotNull(state);

        validateState(state);
        this.startState = state;
        states.add(state);
        return this;
    }

    public StateMachineBuilder<T, U> withState(State<T, U> state) {
        Preconditions.checkNotNull(state);
        Preconditions.checkState(!states.contains(state), "state already added %s", state);

        validateState(state);
        states.add(state);
        return this;
    }

    public StateMachineBuilder<T, U> withTransition(Transition<T, U> transition) {
        Preconditions.checkState(!transitions.contains(transition), "transition already added %s", transition);
        Preconditions.checkNotNull(transition);

        validateTransition(transition);
        transitions.add(transition);
        return this;
    }

    public StateMachineBuilder<T, U> withMultipleTransitionsTriggerPolicy(
            MultipleTransitionsTriggeredPolicy<T, U> multipleTransitionsTriggeredPolicy) {
        Preconditions.checkNotNull(multipleTransitionsTriggeredPolicy, "transition cannot be null");

        this.multipleTransitionsTriggeredPolicy = multipleTransitionsTriggeredPolicy;
        return this;
    }

    public StateMachine<T> build() {

        // check we have a start state
        if (startState == null) {
            throw new IllegalStateException("no start state added");
        }

        // check state id equals and state equals relation
        checkStateEquals(states);

        // check transitions are valid
        checkTransitionsToAndFromStates(states, transitions);

        // check all states are reachable from the start state
        checkAllStatesAreReachableFromStartState(startState, states, transitions);

        return new DefaultStateMachine<T, U>(this);
    }

    Set<State<T, U>> getStates() {
        return states;
    }

    Set<Transition<T, U>> getTransitions() {
        return transitions;
    }

    MultipleTransitionsTriggeredPolicy<T, U> getMultipleTransitionsTriggeredPolicy() {
        return multipleTransitionsTriggeredPolicy;
    }

    protected void validateTransition(Transition<T, U> transition) {
        if (transition.getAction() == null) {
            throw new IllegalStateException(String.format("transition action cannot be null: transition=[%s]", transition));
        }
        if (transition.getGuard() == null) {
            throw new IllegalStateException(String.format("transition guard cannot be null: transition=[%s]", transition));
        }
        if (transition.getFromState() == null) {
            throw new IllegalStateException(String.format("transition fromState cannot be null: transition=[%s]", transition));
        }
        if (transition.getToState() == null) {
            throw new IllegalStateException(String.format("transition toState cannot be null: transition=[%s]", transition));
        }

    }

    protected void validateState(State<T, U> state) {
        if (state.getId() == null) {
            throw new IllegalArgumentException(String.format("state identifier cannot be null: state=[%s]", state));
        }
    }

    protected void checkStateEquals(Set<State<T, U>> states) {
        for (State<T, U> outer : this.states) {
            for (State<T, U> inner : this.states) {
                if (!(outer.getId().equals(inner.getId()) == outer.equals(inner))) {
                    throw new IllegalStateException(String.format("states equals not valid: states=[]", outer, inner));
                }
            }
        }
    }

    protected void checkTransitionsToAndFromStates(Set<State<T, U>> states, Set<Transition<T, U>> transitions) {

        Set<Object> stateIdentifiers = collectStateIdentifiers(states);

        for (Transition<T, U> transition : transitions) {
            if (!stateIdentifiers.contains(transition.getFromState())) {
                throw new IllegalStateException(String.format("transition fromState unknown: fromState=[%s]", transition.getFromState()));
            }
            if (!stateIdentifiers.contains(transition.getToState())) {
                throw new IllegalStateException(String.format("transition toState unknown: toState=[%s]", transition.getToState()));
            }
        }
    }

    protected Set<Object> collectStateIdentifiers(Set<State<T, U>> states) {
        Set<Object> identifiers = Sets.newHashSet();
        for (State<T, U> state : this.states) {
            identifiers.add(state.getId());
        }

        return identifiers;
    }

    protected void checkAllStatesAreReachableFromStartState(State<T, U> startState, Set<State<T, U>> allStates, Set<Transition<T, U>> transitions) {

        Set<Object> visited = Sets.newHashSet();

        Queue<Object> queue = new ArrayDeque<Object>();
        queue.add(startState.getId());
        visited.add(startState.getId());

        Multimap<Object, Object> edges = getEdges(transitions);

        while (!queue.isEmpty()) {
            Object w = queue.remove();
            Collection<Object> neighbours = edges.get(w);
            for (Object vertex : neighbours) {
                if (!visited.contains(vertex)) {
                    visited.add(vertex);
                    queue.add(vertex);
                }
            }
        }

        Set<Object> allStateIdentifiers = Sets.newHashSet();
        for (State<T, U> state : allStates) {
            allStateIdentifiers.add(state.getId());
        }
        Set<Object> notVisited = Sets.difference(allStateIdentifiers, visited);
        if (!notVisited.isEmpty()) {
            throw new IllegalStateException(String.format("states unreachable from start state: states=[%s]", notVisited));
        }
    }

    protected Multimap<Object, Object> getEdges(Set<Transition<T, U>> transitions) {
        Multimap<Object, Object> edges = HashMultimap.create();
        for (Transition<T, U> transition : transitions) {
            edges.put(transition.getFromState(), transition.getToState());
        }
        return edges;
    }

}
