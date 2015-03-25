package com.headstartech.scheelite;

/**
 *
 * @param <T> context type
 * @param <U> state id type
 *
 */
public interface StateMachine<T, U> {

    /**
     * Starts the state machine for the given context by processing the initial transition from the implicit root state.
     *
     * @param context the context
     * @return id of the state after the initial transition
     * @throws java.lang.Exception
     *
     * @see State
     */
    U start(T context) throws Exception;

    /**
     * Processes the given event.
     *
     * @param context the context
     * @param stateId id of the current state
     * @param event the current event
     * @return id of the next state
     * @throws java.lang.Exception
     *
     * @see State
     */
    U processEvent(T context, U stateId, Object event) throws Exception;

    /**
     * Gets the configuration of the state machine.
     *
     * @return the configuration
     */
    StateMachineConfiguration<T, U> getConfiguration();
}