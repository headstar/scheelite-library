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

    StateMachineConfiguration(StateTree<T, U> stateTree, TransitionMap<T, U> transitionMap) {
        this.stateTree = stateTree;
        this.transitionMap = transitionMap;
    }

    /**
     * Gets the states in the state machine
     *
     * @return the states
     *
     * @see com.headstartech.scheelite.State
     */
    public Set<State<T, U>> getStates() { return stateTree.getStates(); }

    /**
     * Gets the transitions in the state machine
     *
     * @return the transitions
     *
     * @see com.headstartech.scheelite.Transition
     */
    public Set<Transition<T, U>> getTransitions() { return transitionMap.getTransitions(); }

    /**
     * Gets the initial transition in the state machine
     *
     * @return the initial transitions
     *
     * @see com.headstartech.scheelite.Transition
     */
    public Set<InitialTransition<T, U>> getInitialTransitions() { return transitionMap.getInitialTransitions(); }


    /**
     * Gets the super state of the specified {@link com.headstartech.scheelite.State}.
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

    public State<T, U> getRootState() { return stateTree.getRootState(); }

}
