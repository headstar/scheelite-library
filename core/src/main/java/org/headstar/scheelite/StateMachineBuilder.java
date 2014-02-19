package org.headstar.scheelite;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.*;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class StateMachineBuilder<T extends Entity<U>, U> {

    private static int MAX_TRANSITIONS_DEFAULT = 100;

    private MutableStateTree<T, U> stateTree;
    private final Set<Transition<T, U>> transitions;
    private final Set<DefaultTransition<T, U>> defaultTransitions;
    private MultipleTransitionsTriggeredResolver<T, U> multipleTransitionsTriggeredResolver;
    private DefaultTransition<T, U> initialTransition;
    private int maxTransitions = MAX_TRANSITIONS_DEFAULT;

    public static <T extends Entity<U>, U> StateMachineBuilder<T, U> newBuilder() {
        return new StateMachineBuilder<T, U>();
    }

    private StateMachineBuilder() {
        stateTree = new MutableStateTree<T, U>();
        transitions = Sets.newHashSet();
        defaultTransitions = Sets.newHashSet();
        multipleTransitionsTriggeredResolver = new ThrowExceptionResolver<T, U>();
    }

    public final StateMachineBuilder<T, U> withCompositeState(State<T, U> state, DefaultAction<T> defaultAction,
                                                        State<T, U> defaultSubState, State<T, U>... subStates) {
        Preconditions.checkNotNull(defaultAction);
        return withCompositeState(state, Optional.of(defaultAction), defaultSubState, subStates);
    }

    public final StateMachineBuilder<T, U> withCompositeState(State<T, U> state, State<T, U> defaultSubState, State<T, U>... subStates) {
        return withCompositeState(state, Optional.<DefaultAction<T>>absent(), defaultSubState, subStates);
    }

    private final StateMachineBuilder<T, U> withCompositeState(State<T, U> superState, Optional<? extends DefaultAction<T>> defaultAction,
                                                         State<T, U> defaultSubState, State<T, U>... subStates) {
        Preconditions.checkNotNull(superState);
        Preconditions.checkNotNull(defaultAction);
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

        defaultTransitions.add(new DefaultTransition<T, U>(Optional.of(superState), defaultSubState, defaultAction));
        return this;
    }

    public StateMachineBuilder<T, U> withMaxTransitions(int maxTransitions) {
        checkArgument(maxTransitions > 0, "maxTransitions must be > 0");
        this.maxTransitions = maxTransitions;
        return this;
    }

    public int getMaxTransitions() {
        return maxTransitions;
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
        return withInitialTransition(toState, Optional.<DefaultAction<T>>absent());
    }

    public StateMachineBuilder<T, U> withInitialTransition(State<T, U> toState, DefaultAction<T> action) {
        return withInitialTransition(toState, Optional.of(action));
    }


    private StateMachineBuilder<T, U> withInitialTransition(State<T, U> toState, Optional<? extends DefaultAction<T>> actionOpt) {
        Preconditions.checkState(initialTransition == null, "initial transition already set");
        Preconditions.checkNotNull(toState);
        Preconditions.checkNotNull(actionOpt);

        validateState(toState);

        initialTransition = new DefaultTransition<T, U>(Optional.<State<T, U>>absent(), toState, actionOpt);
        defaultTransitions.add(initialTransition);
        stateTree.addState(initialTransition.getToState());
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
        if (initialTransition == null) {
            throw new IllegalStateException("no initial transition added");
        }

        if(stateTree.isChild(initialTransition.getToState())) {
            throw new IllegalStateException(String.format("initial transition toState cannot be a child: toState=%s", initialTransition.getToState()));
        }

        Set<State<T, U>> states = stateTree.getStates();

        // check state id equals and state equals relation
        checkStateEquals(states);

        // check transitions are valid
        checkTransitionsToAndFromStates(states, transitions, defaultTransitions);

        // check all states are reachable from the start state
        checkAllStatesAreReachableFromStartState(initialTransition.getToState(), states, transitions, defaultTransitions);

        return new StateMachineImpl<T, U>(this);
    }

    AbstractStateTree<T, U> getStateTree() { return stateTree; }

    Set<Transition<T, U>> getTransitions() {
        return transitions;
    }

    Set<DefaultTransition<T, U>> getDefaultTransitions() {
        return defaultTransitions;
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

    protected void validateInitialTransition(DefaultTransition<T, U> defaultTransition) {
        if (defaultTransition.getAction() == null) {
            throw new IllegalStateException(String.format("initial transition action cannot be null: transition=[%s]", defaultTransition));
        }
        if (defaultTransition.getFromState() == null) {
            throw new IllegalStateException(String.format("initial transition fromState cannot be null: transition=[%s]", defaultTransition));
        }
        if (defaultTransition.getToState() == null) {
            throw new IllegalStateException(String.format("initial transition toState cannot be null: transition=[%s]", defaultTransition));
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

    protected void checkTransitionsToAndFromStates(Set<State<T, U>> states, Set<Transition<T, U>> transitions, Set<DefaultTransition<T, U>> defaultTransitions) {

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

    protected void checkAllStatesAreReachableFromStartState(State<T, U> startState, Set<State<T, U>> allStates, Set<Transition<T, U>> transitions, Set<DefaultTransition<T, U>> defaultTransitions) {

        Set<State<T, U>> visited = Sets.newHashSet();

        Queue<State<T, U>> queue = new ArrayDeque<State<T, U>>();
        queue.add(startState);
        visited.add(startState);

        Multimap<State<T, U>, State<T, U>> edges = getEdges(transitions, defaultTransitions);

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

    protected Multimap<State<T, U>, State<T, U>> getEdges(Set<Transition<T, U>> transitions, Set<DefaultTransition<T, U>> defaultTransitions) {
        Multimap<State<T, U>, State<T, U>> edges = HashMultimap.create();
        for (Transition<T, U> transition : transitions) {
            edges.put(transition.getFromState(), transition.getToState());
        }

        for (DefaultTransition<T, U> transition : defaultTransitions) {
            if(transition.getFromState().isPresent()) {
                edges.put(transition.getFromState().get(), transition.getToState());
            }
        }

        return edges;
    }

}