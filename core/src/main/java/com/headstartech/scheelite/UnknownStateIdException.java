package com.headstartech.scheelite;

/**
 * Thrown to indicate that an unknown state id was supplied to the state machine.
 *
 * @see StateMachine#processEvent(Object, Object, Object)
 */

public class UnknownStateIdException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UnknownStateIdException(String message) {
        super(message);
    }

}
