package com.headstartech.scheelite;

/**
 *
 * @param <T> entity type
 * @param <U> state id type
 *
 */
public interface StateMachine<T, U> {

    /**
     * Starts the state machine for the given entity by processing the initial transition from the implicit root state.
     *
     * @param entity
     * @return id of the state after the initial transition
     * @throws java.lang.Exception
     *
     * @see State
     */
    U start(T entity) throws Exception;

    /**
     * Processes the given event.
     *
     * @param entity
     * @param stateId id of the current state
     * @param event
     * @return id of the next state
     * @throws java.lang.Exception
     *
     * @see State
     */
    U processEvent(T entity, U stateId, Object event) throws Exception;

    /**
     * Gets the configuration of the state machine.
     *
     * @return the configuration
     */
    StateMachineConfiguration<T, U> getConfiguration();
}