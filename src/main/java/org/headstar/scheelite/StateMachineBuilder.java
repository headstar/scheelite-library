package org.headstar.scheelite;

import com.google.common.base.Preconditions;
import com.google.common.collect.*;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

public class StateMachineBuilder<T, U> {

    private State<T,U> startState;
    private final Set<State<T,U>> states;
    private final Set<Transition<T, U>> transitions;
    private EntityMutator<T, U> entityMutator;
    private MultipleTransitionsTriggeredPolicy<T, U> multipleTransitionsTriggeredPolicy;

    public StateMachineBuilder() {
        states = Sets.newHashSet();
        transitions = Sets.newHashSet();
        multipleTransitionsTriggeredPolicy = new MultipleTransitionsTriggeredThrowException<T, U>();
    }

    public StateMachineBuilder<T, U> withEntityMutator(EntityMutator<T, U> entityMutator) {
        Preconditions.checkNotNull(entityMutator, "entity mutator cannot be null");
        this.entityMutator = entityMutator;
        return this;
    }

    public StateMachineBuilder<T, U> withStartState(State<T, U> state) {
        Preconditions.checkNotNull(state, "state cannot be null");

        if(startState != null) {
            throw new IllegalArgumentException(String.format("start state already added: state=[%s]", state));
        }
        if(states.contains(state)) {
            throw new IllegalArgumentException(String.format("state already added: state=[%s]", state));
        }

        validateState(state);
        startState = state;
        states.add(state);
        return this;
    }

    public StateMachineBuilder<T, U> withState(State<T, U> state) {
        Preconditions.checkNotNull(state, "state cannot be null");

        if(states.contains(state)) {
            throw new IllegalArgumentException(String.format("state already added: state=[%s]", state));
        }
        validateState(state);
        states.add(state);
        return this;
    }

    public StateMachineBuilder<T, U> withTransition(Transition<T, U> transition) {
        Preconditions.checkNotNull(transition, "transition cannot be null");

        if(transitions.contains(transition)) {
            throw new IllegalArgumentException(String.format("transition already added: transition=[%s]", transition));
        }
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
        if(startState == null) {
            throw new IllegalStateException("no start state added");
        }

        // check we have a entity mutator
        if(entityMutator == null) {
            throw new IllegalStateException("no entity mutator added");
        }

        // check state id equals and state equals relation
        checkStateEquals(states);

        // check transitions are valid
        checkTransitionsToAndFromStates(states, transitions);

        // check all states are reachable from the start state
        checkAllStatesAreReachableFromStartState(startState, states, transitions);

        return new DefaultStateMachine<T, U>(states, transitions, entityMutator, multipleTransitionsTriggeredPolicy);
    }

    protected void validateTransition(Transition<T, U> transition) {
        if(transition.getAction() == null) {
            throw new IllegalStateException(String.format("transition action cannot be null: transition=[%s]", transition));
        }
        if(transition.getGuard()== null) {
            throw new IllegalStateException(String.format("transition guard cannot be null: transition=[%s]", transition));
        }
        if(transition.getFromState() == null) {
            throw new IllegalStateException(String.format("transition fromState cannot be null: transition=[%s]", transition));
        }
        if(transition.getToState() == null) {
            throw new IllegalStateException(String.format("transition toState cannot be null: transition=[%s]", transition));
        }

    }

    protected void validateState(State<T, U> state) {
        if(state.getIdentifier() == null) {
            throw new IllegalArgumentException(String.format("state identifier cannot be null: state=[%s]", state));
        }
    }

    protected void checkStateEquals(Set<State<T, U>> states) {
        for(State<T, U> outer : this.states) {
            for(State<T, U> inner: this.states) {
                if(!(outer.getIdentifier().equals(inner.getIdentifier()) == outer.equals(inner))) {
                    throw new IllegalStateException(String.format("states equals not valid: states=[]", Arrays.asList(outer, inner)));
                }
            }
        }
    }

    protected void checkTransitionsToAndFromStates(Set<State<T, U>> states, Set<Transition<T, U>> transitions) {

        Set<Object> stateIdentifiers = collectStateIdentifiers(states);

        for(Transition<T, U> transition : transitions) {
            if(!stateIdentifiers.contains(transition.getFromState())) {
                throw new IllegalStateException(String.format("transition fromState unknown: fromState=[%s]", transition.getFromState()));
            }
            if(!stateIdentifiers.contains(transition.getToState())) {
                throw new IllegalStateException(String.format("transition toState unknown: toState=[%s]", transition.getToState()));
            }
        }
    }

    protected Set<Object> collectStateIdentifiers(Set<State<T, U>> states) {
        Set<Object> identifiers = Sets.newHashSet();
        for(State<T, U> state : this.states) {
            identifiers.add(state.getIdentifier());
        }

        return identifiers;
    }

    protected void checkAllStatesAreReachableFromStartState(State<T, U> startState, Set<State<T, U>> allStates, Set<Transition<T, U>> transitions) {

        Set<Object> visited = Sets.newHashSet();

        Queue<Object> queue = new ArrayDeque<Object>();
        queue.add(startState.getIdentifier());
        visited.add(startState.getIdentifier());

        Multimap<Object, Object> edges = getEdges(transitions);

        while(!queue.isEmpty())  {
            Object w = queue.remove();
            Collection<Object> neighbours = edges.get(w);
            for(Object vertex : neighbours) {
                if(!visited.contains(vertex)) {
                    visited.add(vertex);
                    queue.add(vertex);
                }
            }
        }

        Set<Object> allStateIdentifiers = Sets.newHashSet();
        for(State<T, U> state : allStates) {
            allStateIdentifiers.add(state.getIdentifier());
        }
        Set<Object> notVisited = Sets.difference(allStateIdentifiers, visited);
        if(!notVisited.isEmpty()) {
            throw new IllegalStateException(String.format("states unreachable from start state: states=[%s]", notVisited));
        }
    }

    protected Multimap<Object, Object> getEdges(Set<Transition<T, U>> transitions) {
        Multimap<Object, Object> edges = HashMultimap.create();
        for(Transition<T, U> transition : transitions) {
            edges.put(transition.getFromState(), transition.getToState());
        }
        return edges;
    }

}
