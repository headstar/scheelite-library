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
        Multimap<Object, State<T, U>> stateMap = ArrayListMultimap.create();
        for(State<T, U> state : allStates) {
            if(state.getIdentifier() == null) {
                throw new IllegalStateException(String.format("state identifier cannot be null: state=[%s]", state));
            }
            stateMap.put(state.getIdentifier(), state);
        }

        // check state identifiers are unique
        for(State<T, U> state : allStates) {
            if(stateMap.get(state.getIdentifier()).size() > 1) {
                throw new IllegalStateException(String.format("state identifier not unique: identifier=[%s]", state.getIdentifier()));
            }
        }

        // check so no state equals some other state
        for(State<T, U> outer : allStates) {
            for(State<T, U> inner: allStates) {
                if(outer != inner && outer.equals(inner))  {
                    throw new IllegalStateException(String.format("state cannot be equal to another state: stateA=[%s], stateB=[%s]", outer, inner));
                }
            }
        }

        Set<State<T, U>> states = Sets.newHashSet(allStates);

        // check transitions are valid
        for(Transition<T, U> transition : inputTransitions) {
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

        Set<Transition<T, U>> transitions = Sets.newHashSet(inputTransitions);

        checkAllStatesAreReachableFromStartState(startState, states, transitions);

        return new DefaultStateMachine(states, transitions);
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
