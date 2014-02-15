package org.headstar.scheelite;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.*;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

public class StateMachineBuilder<T extends Entity<U>, U> {

    private State<T, U> startState;
    private Set<State<T, U>> states;
    private final Set<State<T, U>> simpleStates;
    private final Set<State<T, U>> superStates;
    private final Set<State<T, U>> subStates;
    private final Set<Transition<T, U>> transitions;
    private final Set<InitialTransition<T, U>> initialTransitions;
    private final Map<State<T, U>, State<T, U>> subStateSuperStateMap; // sub state -> super state

    private MultipleTransitionsTriggeredResolver<T, U> multipleTransitionsTriggeredResolver;

    public static <T extends Entity<U>, U> StateMachineBuilder<T, U> newBuilder() {
        return new StateMachineBuilder<T, U>();
    }

    private StateMachineBuilder() {
        states = Sets.newHashSet();
        simpleStates = Sets.newHashSet();
        superStates = Sets.newHashSet();
        subStates = Sets.newHashSet();
        subStateSuperStateMap = Maps.newHashMap();
        transitions = Sets.newHashSet();
        initialTransitions = Sets.newHashSet();
        multipleTransitionsTriggeredResolver = new ThrowExceptionResolver<T, U>();
    }

    public StateMachineBuilder<T, U> withStartState(State<T, U> state) {
        Preconditions.checkState(this.startState == null, "start state was already set to %s", this.startState);
        Preconditions.checkState(!subStateSuperStateMap.containsKey(state), "state already added as sub state: %s", state);
        this.startState = Preconditions.checkNotNull(state);

        validateState(state);
        this.startState = state;
        return this;
    }

    public StateMachineBuilder<T, U> withSimpleState(State<T, U> state) {
        Preconditions.checkNotNull(state);
        Preconditions.checkState(!simpleStates.contains(state), "state already added as simple state: %s", state);
        Preconditions.checkState(!superStates.contains(state), "state already added as super state: %s", state);

        validateState(state);
        simpleStates.add(state);
        return this;
    }

    public StateMachineBuilder<T, U> withCompositeState(State<T, U> state, InitialAction<T> initialAction,
                                                        State<T, U> defaultSubState, State<T, U>... subStates) {
        Preconditions.checkNotNull(initialAction);
        return withCompositeState(state, Optional.of(initialAction), defaultSubState, subStates);
    }

    public StateMachineBuilder<T, U> withCompositeState(State<T, U> state, State<T, U> defaultSubState, State<T, U>... subStates) {
        return withCompositeState(state, Optional.<InitialAction<T>>absent(), defaultSubState, subStates);
    }

    private StateMachineBuilder<T, U> withCompositeState(State<T, U> superState, Optional<? extends InitialAction<T>> initialAction,
                                                         State<T, U> defaultSubState, State<T, U>... subStates) {
        Preconditions.checkNotNull(superState);
        Preconditions.checkNotNull(initialAction);
        Preconditions.checkNotNull(defaultSubState);
        Preconditions.checkState(!subStateSuperStateMap.containsKey(defaultSubState), "state already added as sub state: %s", defaultSubState);
        for (State<T, U> state : subStates) {
            Preconditions.checkNotNull(state);
            Preconditions.checkState(!subStateSuperStateMap.containsKey(state), "state already added as sub state: %s", state);
        }
        Preconditions.checkState(!superStates.contains(superState), "state already added as super state: %s", superState);
        Preconditions.checkState(!simpleStates.contains(superState), "state already added as simple state: %s", superState);

        validateState(superState);
        validateState(defaultSubState);
        for (State<T, U> state : subStates) {
            validateState(state);
        }

        superStates.add(superState);
        this.subStates.add(defaultSubState);
        subStateSuperStateMap.put(defaultSubState, superState);
        for (State<T, U> state : subStates) {
            this.subStates.add(state);
            subStateSuperStateMap.put(state, superState);
        }

        initialTransitions.add(new InitialTransition<T, U>(superState, defaultSubState, initialAction));

        return this;
    }

    public StateMachineBuilder<T, U> withTransition(State<T, U> fromState, State<T, U> toState, Guard<T> guard, Action<T> action) {
        return withTransition(fromState, toState, Optional.<Guard<T>>of(guard), Optional.<Action<T>>of(action));
    }

    public StateMachineBuilder<T, U> withTransition(State<T, U> fromState, State<T, U> toState, Guard<T> guard) {
        checkNotNull(guard);
        return withTransition(fromState, toState, Optional.<Guard<T>>of(guard), Optional.<Action<T>>absent());
    }

    public StateMachineBuilder<T, U> withTransition(State<T, U> fromState, State<T, U> toState, Action<T> action) {
        checkNotNull(action);
        return withTransition(fromState, toState, Optional.<Guard<T>>absent(), Optional.<Action<T>>of(action));
    }

    public StateMachineBuilder<T, U> withTransition(State<T, U> fromState, State<T, U> toState) {
        return withTransition(fromState, toState, Optional.<Guard<T>>absent(), Optional.<Action<T>>absent());
    }

    public StateMachineBuilder<T, U> withTransition(State<T, U> fromState, State<T, U> toState,
                                                    Optional<? extends Guard<T>> guard, Optional<? extends Action<T>> action) {
        return withTransition(new Transition<T, U>(fromState, toState, action, guard, TransitionType.EXTERNAL));
    }

    public StateMachineBuilder<T, U> withLocalTransition(State<T, U> fromState, State<T, U> toState, Guard<T> guard, Action<T> action) {
        return withLocalTransition(fromState, toState, Optional.<Guard<T>>of(guard), Optional.<Action<T>>of(action));
    }

    public StateMachineBuilder<T, U> withLocalTransition(State<T, U> fromState, State<T, U> toState,
                                                         Optional<? extends Guard<T>> guard, Optional<? extends Action<T>> action) {
        return withTransition(new Transition<T, U>(fromState, toState, action, guard, TransitionType.LOCAL));
    }


    public StateMachineBuilder<T, U> withTransition(Transition<T, U> transition) {
        Preconditions.checkState(!transitions.contains(transition), "transition already added %s", transition);
        Preconditions.checkNotNull(transition);

        validateTransition(transition);
        transitions.add(transition);
        return this;
    }



    /*
    public StateMachineBuilder<T, U> withInitialTransition(InitialTransition<T, U> initialTransition) {
        Preconditions.checkNotNull(initialTransition);
        validateInitialTransition(initialTransition);
        Preconditions.checkState(!initialTransitionsFromState.contains(initialTransition.getFromState()),
                "initial transition from %s already added %s", initialTransition.getFromState(), initialTransition);

        initialTransitionsFromState.add(initialTransition.getFromState());
        initialTransitions.add(initialTransition);
        return this;
    }
      */

    public StateMachineBuilder<T, U> withMultipleTransitionsTriggerPolicy(
            MultipleTransitionsTriggeredResolver<T, U> multipleTransitionsTriggeredResolver) {
        Preconditions.checkNotNull(multipleTransitionsTriggeredResolver, "transition cannot be null");

        this.multipleTransitionsTriggeredResolver = multipleTransitionsTriggeredResolver;
        return this;
    }

    public StateMachine<T> build() {

        // check we have a start state
        if (startState == null) {
            throw new IllegalStateException("no start state added");
        }

        states = Sets.newHashSet(Sets.newHashSet(Sets.union(simpleStates, Sets.union(superStates, subStates))));
        states.add(startState);

        // check state id equals and state equals relation
        checkStateEquals(states);

        // check transitions are valid
        checkTransitionsToAndFromStates(states, transitions, initialTransitions);

        // check all states are reachable from the start state
        checkAllStatesAreReachableFromStartState(startState, states, transitions, initialTransitions);

        return new DefaultStateMachine<T, U>(this);
    }

    Set<State<T, U>> getStates() {
        return states;
    }

    Set<Transition<T, U>> getTransitions() {
        return transitions;
    }

    Map<State<T, U>, State<T, U>> getSubStateSuperStateMap() {
        return subStateSuperStateMap;
    }

    Set<InitialTransition<T, U>> getInitialTransitions() {
        return initialTransitions;
    }

    MultipleTransitionsTriggeredResolver<T, U> getMultipleTransitionsTriggeredResolver() {
        return multipleTransitionsTriggeredResolver;
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
        if (transition.getTransitionType() == null) {
            throw new IllegalStateException(String.format("transition type cannot be null: transition=[%s]", transition));
        }
    }

    protected void validateInitialTransition(InitialTransition<T, U> initialTransition) {
        if (initialTransition.getAction() == null) {
            throw new IllegalStateException(String.format("initial transition action cannot be null: transition=[%s]", initialTransition));
        }
        if (initialTransition.getFromState() == null) {
            throw new IllegalStateException(String.format("initial transition fromState cannot be null: transition=[%s]", initialTransition));
        }
        if (initialTransition.getToState() == null) {
            throw new IllegalStateException(String.format("initial transition toState cannot be null: transition=[%s]", initialTransition));
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
                    throw new IllegalStateException(String.format("states equals not valid: states=[]", states));
                }
            }
        }
    }

    protected void checkTransitionsToAndFromStates(Set<State<T, U>> states, Set<Transition<T, U>> transitions, Set<InitialTransition<T, U>> initialTransitions) {

        for (Transition<T, U> transition : transitions) {
            if (!states.contains(transition.getFromState())) {
                throw new IllegalStateException(String.format("transition fromState unknown: fromState=[%s]", transition.getFromState()));
            }
            if (!states.contains(transition.getToState())) {
                throw new IllegalStateException(String.format("transition toState unknown: toState=[%s]", transition.getToState()));
            }
            // if transition is local, source state and target state must be descendants
            if(transition.getTransitionType().equals(TransitionType.LOCAL)
                    && !isRelated(transition.getFromState(), transition.getToState())) {
                throw new IllegalStateException(String.format("states must be related in local transition: fromState=[%s], toState=[%s]", transition.getFromState(), transition.getToState()));
            }
        }

        for (InitialTransition<T, U> initialTransition : initialTransitions) {
            if (!states.contains(initialTransition.getFromState())) {
                throw new IllegalStateException(String.format("initial transition fromState unknown: fromState=[%s]", initialTransition.getFromState()));
            }
            if (!states.contains(initialTransition.getToState())) {
                throw new IllegalStateException(String.format("initial transition toState unknown: toState=[%s]", initialTransition.getToState()));
            }
        }
    }

    private boolean isRelated(State<T, U> a, State<T, U> b) {
        Set<State<T, U>> aToRoot = getPathToRoot(a);
        Set<State<T, U>> bToRoot = getPathToRoot(b);
        return aToRoot.contains(b) || bToRoot.contains(a);
    }

    private Set<State<T, U>> getPathToRoot(State<T,U> a) {
        checkNotNull(a);

        Set<State<T, U>> res = Sets.newHashSet();
        Optional<State<T, U>> stateOpt = Optional.of(a);
        do {
            State<T, U> state = stateOpt.get();
            res.add(state);
            stateOpt = Optional.fromNullable(subStateSuperStateMap.get(state));
        } while (stateOpt.isPresent());
        return res;
    }

    protected void checkAllStatesAreReachableFromStartState(State<T, U> startState, Set<State<T, U>> allStates, Set<Transition<T, U>> transitions, Set<InitialTransition<T, U>> initialTransitions) {

        Set<Object> visited = Sets.newHashSet();

        Queue<Object> queue = new ArrayDeque<Object>();
        queue.add(startState);
        visited.add(startState);

        Multimap<Object, Object> edges = getEdges(transitions, initialTransitions);

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

        Set<State<T, U>> notVisited = Sets.difference(allStates, visited);
        if (!notVisited.isEmpty()) {
            throw new IllegalStateException(String.format("states unreachable from start state: startState=[%s] states=[%s]", startState,
                    notVisited));
        }
    }

    protected Multimap<Object, Object> getEdges(Set<Transition<T, U>> transitions, Set<InitialTransition<T, U>> initialTransitions) {
        Multimap<Object, Object> edges = HashMultimap.create();
        for (Transition<T, U> transition : transitions) {
            edges.put(transition.getFromState(), transition.getToState());
        }

        for (InitialTransition<T, U> transition : initialTransitions) {
            edges.put(transition.getFromState(), transition.getToState());
        }

        return edges;
    }

}
