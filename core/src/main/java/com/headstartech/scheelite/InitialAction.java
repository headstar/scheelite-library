package com.headstartech.scheelite;

/**
 * Encapsulates an action to be executed at the initial transition from a super state to a sub state.
 * @param <T> entity type
 *
 * @see InitialTransition
 */
public interface InitialAction<T> {

    /**
     * Name of initial action.
     * Not used anymore, will be removed in next major version.
     * @return
     */
    @Deprecated
    String getName();

    /**
     * Called when action is executed.
     *
     * @param entity
     */
    void execute(T entity);
}
