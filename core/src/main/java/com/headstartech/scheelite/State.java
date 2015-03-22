package com.headstartech.scheelite;

/**
 * Defines a state in the state machine.
 *
 * @param <T> entity type
 * @param <U> state id type
 *
 * @see StateMachine
 */
public interface State<T, U> {

    /**
     * Gets the id of the state
     * @return the id of the state
     */
    U getId();

    /**
     * Called by the state machine when the state is entered.
     * @param entity
     *
     * @throws java.lang.Exception
     */
    void onEntry(T entity) throws Exception;

    /**
     * Called by the state machine when the state is exited.
     * @param entity
     *
     * @throws java.lang.Exception
     */
    void onExit(T entity) throws  Exception;

    /**
     * Called by the state machine when processing an event.
     * @param entity
     * @param event
     * @return <code>false</code> if the event should be passed on to a super state (if any), <code>true</code> otherwise.
     *
     * @throws java.lang.Exception
     */
    boolean onEvent(T entity, Object event) throws Exception;
}
