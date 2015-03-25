package com.headstartech.scheelite;

/**
 * Interface for a state machine. A state machine is created using the {@linkplain com.headstartech.scheelite.StateMachineBuilder}.
 *
 * The user provided context is passed to the states, actions and guards (treated as an opaque object).
 *
 * @param <T> context type
 * @param <U> state id type
 *
 * @see com.headstartech.scheelite.StateMachineBuilder
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