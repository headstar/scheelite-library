package org.headstar.scheelite;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.*;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

public class StateMachineBuilder<T extends Entity<U>, U> {

    private MutableStateTree<T, U> stateTree;
    private final Set<Transition<T, U>> transitions;
    private final Set<InitialTransition<T, U>> initialTransitions;
    private MultipleTransitionsTriggeredResolver<T, U> multipleTransitionsTriggeredResolver;
    private InitialTransition<T, U> topLevelInitialTransition;

    public static <T extends Entity<U>, U> StateMachineBuilder<T, U> newBuilder() {
        return new StateMachineBuilder<T, U>();
    }

    private StateMachineBuilder() {
        stateTree = new MutableStateTree<T, U>();
        transitions = Sets.newHashSet();
        initialTransitions = Sets.newHashSet();
        multipleTransitionsTriggeredResolver = new ThrowExceptionResolver<T, U>();
    }

    @SafeVarargs
    public final StateMachineBuilder<T, U> withCompositeState(State<T, U> state, InitialAction<T> initialAction,
                                                        State<T, U> defaultSubState, State<T, U>... subStates) {
        Preconditions.checkNotNull(initialAction);
        return withCompositeState(state, Optional.of(initialAction), defaultSubState, subStates);
    }

    @SafeVarargs
    public final StateMachineBuilder<T, U> withCompositeState(State<T, U> state, State<T, U> defaultSubState, State<T, U>... subStates) {
        return withCompositeState(state, Optional.<InitialAction<T>>absent(), defaultSubState, subStates);
    }

    @SafeVarargs
    private final StateMachineBuilder<T, U> withCompositeState(State<T, U> superState, Optional<? extends InitialAction<T>> initialAction,
                                                         State<T, U> defaultSubState, State<T, U>... subStates) {
        Preconditions.checkNotNull(superState);
        Preconditions.checkNotNull(initialAction);
        Preconditions.checkNotNull(defaultSubState);
        for (State<T, U> state : subStates) {
            Preconditions.checkNotNull(state);
        }

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

        initialTransitions.add(new InitialTransition<T, U>(Optional.of(superState), defaultSubState, initialAction));
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
        stateTree.addState(transition.getFromState());
        stateTree.addState(transition.getToState());
        transitions.add(transition);
        return this;
    }

    public StateMachineBuilder<T, U> withInitialTransition(State<T, U> toState) {
        return withInitialTransition(toState, Optional.<InitialAction<T>>absent());
    }

    public StateMachineBuilder<T, U> withInitialTransition(State<T, U> toState, InitialAction<T> action) {
        return withInitialTransition(toState, Optional.of(action));
    }


    private StateMachineBuilder<T, U> withInitialTransition(State<T, U> toState, Optional<? extends InitialAction<T>> actionOpt) {
        Preconditions.checkState(topLevelInitialTransition == null, "initial transition already set");
        Preconditions.checkNotNull(toState);
        Preconditions.checkNotNull(actionOpt);

        validateState(toState);

        topLevelInitialTransition = new InitialTransition<T, U>(Optional.<State<T, U>>absent(), toState, actionOpt);
        initialTransitions.add(topLevelInitialTransition);
        stateTree.addState(topLevelInitialTransition.getToState());
        return this;
    }

    public StateMachineBuilder<T, U> withMultipleTransitionsTriggerPolicy(
            MultipleTransitionsTriggeredResolver<T, U> multipleTransitionsTriggeredResolver) {
        Preconditions.checkNotNull(multipleTransitionsTriggeredResolver);

        this.multipleTransitionsTriggeredResolver = multipleTransitionsTriggeredResolver;
        return this;
    }

    public StateMachine<T> build() {

        // check we have a top level initial transition state
        if (topLevelInitialTransition == null) {
            throw new IllegalStateException("no initial transition added");
        }

        if(stateTree.isChild(topLevelInitialTransition.getToState())) {
            throw new IllegalStateException(String.format("initial transition toState cannot be a child: toState=%s", topLevelInitialTransition.getToState()));
        }

        Set<State<T, U>> states = stateTree.getStates();

        // check state id equals and state equals relation
        checkStateEquals(states);

        // check transitions are valid
        checkTransitionsToAndFromStates(states, transitions, initialTransitions);

        // check all states are reachable from the start state
        checkAllStatesAreReachableFromStartState(topLevelInitialTransition.getToState(), states, transitions, initialTransitions);

        return new DefaultStateMachine<T, U>(this);
    }

    StateTree<T, U> getStateTree() { return stateTree; }

    Set<Transition<T, U>> getTransitions() {
        return transitions;
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
        for (State<T, U> outer : states) {
            for (State<T, U> inner : states) {
                if (!(outer.getId().equals(inner.getId()) == outer.equals(inner))) {
                    throw new IllegalStateException(String.format("states equals not valid: states=[]", states));
                }
            }
        }
    }

    protected void checkTransitionsToAndFromStates(Set<State<T, U>> states, Set<Transition<T, U>> transitions, Set<InitialTransition<T, U>> initialTransitions) {

        for (Transition<T, U> transition : transitions) {
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

    protected void checkAllStatesAreReachableFromStartState(State<T, U> startState, Set<State<T, U>> allStates, Set<Transition<T, U>> transitions, Set<InitialTransition<T, U>> initialTransitions) {

        Set<State<T, U>> visited = Sets.newHashSet();

        Queue<State<T, U>> queue = new ArrayDeque<State<T, U>>();
        queue.add(startState);
        visited.add(startState);

        Multimap<State<T, U>, State<T, U>> edges = getEdges(transitions, initialTransitions);

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

        Set<State<T, U>> notVisited = Sets.difference(allStates, visited);
        if (!notVisited.isEmpty()) {
            throw new IllegalStateException(String.format("states not reachable from start state: startState=[%s] states=[%s]", startState,
                    notVisited));
        }
    }

    protected Multimap<State<T, U>, State<T, U>> getEdges(Set<Transition<T, U>> transitions, Set<InitialTransition<T, U>> initialTransitions) {
        Multimap<State<T, U>, State<T, U>> edges = HashMultimap.create();
        for (Transition<T, U> transition : transitions) {
            edges.put(transition.getFromState(), transition.getToState());
        }

        for (InitialTransition<T, U> transition : initialTransitions) {
            if(transition.getFromState().isPresent()) {
                edges.put(transition.getFromState().get(), transition.getToState());
            }
        }

        return edges;
    }

}
