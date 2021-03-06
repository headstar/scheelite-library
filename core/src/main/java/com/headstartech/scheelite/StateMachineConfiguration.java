package com.headstartech.scheelite;

import com.google.common.base.Optional;

import java.util.Set;

/**
 * The configuration of the state machine (states and their super state, transitions and initial transitions).
 *
 * @since 2.0
 */
public class StateMachineConfiguration<T, U> {

    private final StateTree<T, U> stateTree;
    private final TransitionMap<T, U> transitionMap;

    StateMachineConfiguration(StateTree<T, U> stateTree, TransitionMap<T, U> transitionMap) {
        this.stateTree = stateTree;
        this.transitionMap = transitionMap;
    }

    /**
     * Gets the states in the state machine. The root state is not included (see {@linkplain #getRootState()}).
     *
     * @return the states
     *
     * @see com.headstartech.scheelite.State
     */
    public Set<State<T, U>> getStates() { return stateTree.getStates(); }

    /**
     * Gets the transitions in the state machine.
     *
     * @return the transitions
     *
     * @see com.headstartech.scheelite.Transition
     */
    public Set<Transition<T, U>> getTransitions() { return transitionMap.getTransitions(); }

    /**
     * Gets the super state of the specified state.
     *
     * @param state
     * @return the super state or <code>null</code> if the argument is the root state
     */
    public State<T, U> getSuperState(State<T, U> state) {
        Optional<State<T, U>> superState = stateTree.getParent(state);
        if(superState.isPresent()) {
            return superState.get();
        } else {
            return null;
        }
    }

    /**
     * Gets the implicit root state.
     *
     * The id of the root state is <code>null</code>.
     *
     * @return the root state.
     */
    public State<T, U> getRootState() { return stateTree.getRootState(); }

}
