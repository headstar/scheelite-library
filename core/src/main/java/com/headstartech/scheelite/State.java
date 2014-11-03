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
     */
    void onEntry(T entity);

    /**
     * Called by the state machine when the state is exited.
     * @param entity
     */
    void onExit(T entity);

    /**
     * Called by the state machine when processing an event.
     * @param entity
     * @param event
     * @return <code>true</code> if the event should be passed on to a super state (if any), <code>false</code> otherwise.
     */
    boolean onEvent(T entity, Object event);
}
