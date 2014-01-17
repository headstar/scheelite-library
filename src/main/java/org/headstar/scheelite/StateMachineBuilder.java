package org.headstar.scheelite;

import com.google.common.collect.*;

import java.util.*;

public class StateMachineBuilder<T> {

    private State<T> startState;
    private final List<State<T>> inputStates;
    private final List<Transition<T>> inputTransitions;

    public StateMachineBuilder() {
        inputStates = Lists.newArrayList();
        inputTransitions = Lists.newArrayList();
    }

    public StateMachineBuilder<T> withStartState(State<T> state) {
        if(startState != null) {
            throw new IllegalStateException("start state already added");
        }
        startState = state;
        return this;
    }

    public StateMachineBuilder<T> withState(State<T> state) {
        inputStates.add(state);
        return this;
    }

    public StateMachineBuilder<T> withTransition(Transition<T> transition) {
        inputTransitions.add(transition);
        return this;
    }

    public StateMachine build() {

        // check we have a start state
        if(startState == null) {
            throw new IllegalStateException("no start state added");
        }

        List<State<T>> allStates = Lists.newArrayList(inputStates);
        allStates.add(startState);
        Multimap<Object, State<T>> stateMap = ArrayListMultimap.create();
        for(State<T> state : allStates) {
            if(state.getIdentifier() == null) {
                throw new IllegalStateException(String.format("state identifier cannot be null: state=[%s]", state));
            }
            stateMap.put(state.getIdentifier(), state);
        }

        // check state identifiers are unique
        for(State<T> state : allStates) {
            if(stateMap.get(state.getIdentifier()).size() > 1) {
                throw new IllegalStateException(String.format("state identifier not unique: identifier=[%s]", state.getIdentifier()));
            }
        }

        // check so no state equals some other state
        for(State<T> outer : allStates) {
            for(State<T> inner: allStates) {
                if(outer != inner && outer.equals(inner))  {
                    throw new IllegalStateException(String.format("state cannot be equal to another state: stateA=[%s], stateB=[%s]", outer, inner));
                }
            }
        }

        Set<State<T>> states = Sets.newHashSet(allStates);

        // check transitions are valid
        for(Transition<T> transition : inputTransitions) {
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
            if(!stateMap.containsKey(transition.getFromState())) {
                throw new IllegalStateException(String.format("transition fromState unknown: fromState=[%s]", transition.getFromState()));
            }
            if(!stateMap.containsKey(transition.getToState())) {
                throw new IllegalStateException(String.format("transition toState unknown: toState=[%s]", transition.getToState()));
            }
        }

        Set<Transition<T>> transitions = Sets.newHashSet(inputTransitions);

        checkAllStatesAreReachableFromStartState(startState, states, transitions);

        return new DefaultStateMachine(states, transitions);
    }

    protected void checkAllStatesAreReachableFromStartState(State<T> startState, Set<State<T>> allStates, Set<Transition<T>> transitions) {

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
        for(State<T> state : allStates) {
            allStateIdentifiers.add(state.getIdentifier());
        }
        Set<Object> notVisited = Sets.difference(allStateIdentifiers, visited);
        if(!notVisited.isEmpty()) {
            throw new IllegalStateException(String.format("states unreachable from start state: states=[%s]", notVisited));
        }
    }


    protected Multimap<Object, Object> getEdges(Set<Transition<T>> transitions) {
        Multimap<Object, Object> edges = HashMultimap.create();
        for(Transition<T> transition : transitions) {
            edges.put(transition.getFromState(), transition.getToState());
        }
        return edges;
    }

}
