package com.headstartech.scheelite;

import com.google.common.base.Optional;

/**
 * Encapsulates an action to be executed at state transitions.
 *
 * @param <T> context type
 */
public interface Action<T> {

    /**
     * Called when action is executed.
     *
     * @param context the context
     * @param event the current event (if any)
     */
    void execute(T context, Optional<?> event) throws Exception;
}
