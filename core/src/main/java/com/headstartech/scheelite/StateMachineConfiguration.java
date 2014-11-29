package com.headstartech.scheelite;

import com.google.common.base.Optional;

import java.util.Map;
import java.util.Set;

/**
 * The configuration of the state machine (states and their super state, transitions and initial transitions).
 */
public class StateMachineConfiguration<T, U> {

    private final StateTree<T, U> stateTree;
    private final TransitionMap<T, U> transitionMap;

    public StateMachineConfiguration(StateTree<T, U> stateTree, TransitionMap<T, U> transitionMap) {
        this.stateTree = stateTree;
        this.transitionMap = transitionMap;
    }

    public Set<State<T, U>> getStates() { return stateTree.getStates(); }

    public Set<Transition<T, U>> getTransitions() { return transitionMap.getTransitions(); }

    public Set<InitialTransition<T, U>> getInitialTransitions() { return null; }

    public State<T, U> getSuperState(State<T, U> state) {
        Optional<State<T, U>> superState = stateTree.getParent(state);
        if(superState.isPresent()) {
            return superState.get();
        } else {
            return null;
        }
    }

    public State<T, U> getRootState() { return stateTree.getRootState(); }

}
