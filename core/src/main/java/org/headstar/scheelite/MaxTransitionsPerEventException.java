package org.headstar.scheelite;

/**
 * Thrown to indicate that the maximum number of transitions per event was exceeded.
 *
 * @see org.headstar.scheelite.StateMachine#processEvent(Object, Object, Object)
 */
public class MaxTransitionsPerEventException extends RuntimeException {

    private static final long serialVersionUID = 1L;
}
