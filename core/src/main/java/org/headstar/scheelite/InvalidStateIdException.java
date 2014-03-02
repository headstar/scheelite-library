package org.headstar.scheelite;

/**
 * Thrown to indicate that an unknown state id was supplied to the state machine.
 *
 * @see org.headstar.scheelite.StateMachine#processEvent(Object, Object, Object)
 */

public class InvalidStateIdException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidStateIdException(String message) {
        super(message);
    }

}
