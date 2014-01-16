package org.headstar.scheelite;

import com.google.common.collect.*;

import java.util.*;

public class StateMachineBuilder<T, U> {

    private State<T, U> startState;
    private final List<State<T, U>> inputStates;
    private final List<Transition<T, U>> inputTransitions;

    public StateMachineBuilder() {
        inputStates = Lists.newArrayList();
        inputTransitions = Lists.newArrayList();
    }

    public StateMachineBuilder<T, U> addStartState(State<T, U> state) {
        if(startState != null) {
            throw new IllegalStateException("start state already added");
        }
        startState = state;
        return this;
    }

    public StateMachineBuilder<T, U> addState(State<T, U> state) {
        inputStates.add(state);
        return this;
    }

    public StateMachineBuilder<T, U> addTransition(Transition<T, U> transition) {
        inputTransitions.add(transition);
        return this;
    }

    public StateMachine build() {

        // check we have a start state
        if(startState == null) {
            throw new IllegalStateException("no start state added");
        }

        List<State<T, U>> allStates = Lists.newArrayList(inputStates);
        allStates.add(startState);
        Multimap<StateIdentifier, State<T, U>> stateMap = ArrayListMultimap.create();
        for(State<T, U> state : allStates) {
            stateMap.put(state.getIdentifier(), state);
        }

        // check state identifiers are unique
        for(State<T, U> state : allStates) {
            if(stateMap.get(state.getIdentifier()).size() > 1) {
                throw new IllegalStateException(String.format("state identifier not unique: identifier=%s", state.getIdentifier()));
            }
        }

        // check so no state equals some other state
        for(State<T, U> outer : allStates) {
            for(State<T, U> inner: allStates) {
                if(outer != inner && outer.equals(inner))  {
                    throw new IllegalStateException(String.format("state cannot be equal to another state: stateA=%s, stateB=%s", outer, inner));
                }
            }
        }

        Set<State<T, U>> states = Sets.newHashSet(allStates);

        // check transitions are valid
        for(Transition<T, U> transition : inputTransitions) {
            if(transition.getAction() == null) {
                throw new IllegalStateException(String.format("transition action cannot be null: transition=%s", transition));
            }
            if(transition.getGuard()== null) {
                throw new IllegalStateException(String.format("transition guard cannot be null: transition=%s", transition));
            }
            if(transition.getInputStateId() == null) {
                throw new IllegalStateException(String.format("transition input state id cannot be null: transition=%s", transition));
            }
            if(transition.getOutputStateId() == null) {
                throw new IllegalStateException(String.format("transition output state id cannot be null: transition=%s", transition));
            }
            if(!stateMap.containsKey(transition.getInputStateId())) {
                throw new IllegalStateException(String.format("transition input state id unknown: transition=%s, inputStateId=%s", transition, transition.getInputStateId()));
            }
            if(!stateMap.containsKey(transition.getOutputStateId())) {
                throw new IllegalStateException(String.format("transition output state id unknown: transition=%s, outputStateId=%s", transition, transition.getOutputStateId()));
            }
        }

        Set<Transition<T, U>> transitions = Sets.newHashSet(inputTransitions);

        checkAllStatesAreReachableFromStartState(startState, states, transitions);

        return new DefaultStateMachine(states, transitions);
    }

    protected void checkAllStatesAreReachableFromStartState(State<T, U> startState, Set<State<T, U>> allStates, Set<Transition<T, U>> transitions) {

        Set<StateIdentifier> visited = Sets.newHashSet();

        Queue<StateIdentifier> queue = new ArrayDeque<StateIdentifier>();
        queue.add(startState.getIdentifier());
        visited.add(startState.getIdentifier());

        Multimap<StateIdentifier, StateIdentifier> edges = getEdges(transitions);

        while(!queue.isEmpty())  {
            StateIdentifier w = queue.remove();
            Collection<StateIdentifier> neighbours = edges.get(w);
            for(StateIdentifier vertex : neighbours) {
                if(!visited.contains(vertex)) {
                    visited.add(vertex);
                    queue.add(vertex);
                }
            }
        }

        Set<StateIdentifier> allStateIdentifiers = Sets.newHashSet();
        for(State<T, U> state : allStates) {
            allStateIdentifiers.add(state.getIdentifier());
        }
        if(!visited.equals(allStates)) {
            Set<StateIdentifier> notVisited = Sets.difference(allStateIdentifiers, visited);
            throw new IllegalStateException(String.format("unreachable states from start node: %s", notVisited));
        }
    }


    protected Multimap<StateIdentifier, StateIdentifier> getEdges(Set<Transition<T, U>> transitions) {
        Multimap<StateIdentifier, StateIdentifier> edges = HashMultimap.create();
        for(Transition<T, U> transition : transitions) {
            edges.put(transition.getInputStateId(), transition.getOutputStateId());
        }
        return edges;
    }


    private static class Node {

        private StateIdentifier value;
        private Set<Node> neighbours;

        Node(StateIdentifier value) {
            this.value = value;
            neighbours = Sets.newHashSet();
        }

        void addNeighbour(Node n) {
            neighbours.add(n);
        }

        Set<Node> getNeighbours() {
            return neighbours;
        }


    }

}
