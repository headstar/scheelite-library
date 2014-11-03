package com.headstartech.scheelite;

import com.google.common.base.Optional;

/**
 * Encapsulates an action to be executed at state transitions.
 *
 * @param <T> entity type
 */
public interface Action<T> {

    /**
     * Name of action.
     * Note, will be removed in next major version.
     * @return
     */
    @Deprecated
    String getName();

    /**
     * Called when action is executed.
     *
     * @param entity
     * @param event
     */
    void execute(T entity, Optional<?> event);
}
