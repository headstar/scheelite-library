package org.headstar.scheelite;

/**
 *
 * @param <T> entity type
 * @param <U> state id type
 *
 */
public interface StateMachine<T, U> {

    /**
     * Processes the initial transition from the implicit root state.
     *
     * @param entity
     * @return id of the state after the initial transition
     *
     * @see org.headstar.scheelite.State
     */
    U processInitialTransition(T entity);

    /**
     * Processes the given event.
     *
     * @param entity
     * @param stateId id of the current state
     * @param event
     * @return id of the next state
     *
     * @see org.headstar.scheelite.State
     */
    U processEvent(T entity, U stateId, Object event);
}