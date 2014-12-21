package com.headstartech.scheelite;

/**
 * Transition types.
 *
 * EXTERNAL - a transition where the main source state is always exited and the main target state is always entered.
 * LOCAL -  a transition which doesn’t cause exit from and reentry to the main source state if the main target state is a substate of the main source state.
 * Also, a local transition doesn’t cause exit from and reentry to the main target state if the main target is a superstate of the main source state
 * INITIAL - default transition from a super state to a substate when the super state is entered.
 *
 */
public enum TransitionType {
    EXTERNAL, LOCAL, INITIAL
}
