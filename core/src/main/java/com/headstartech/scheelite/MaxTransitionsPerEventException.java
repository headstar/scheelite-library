package com.headstartech.scheelite;

/**
 * Thrown to indicate that the maximum number of transitions per event was exceeded.
 *
 * @see StateMachine#processEvent
 */
public class MaxTransitionsPerEventException extends RuntimeException {

    private static final long serialVersionUID = 1L;
}
