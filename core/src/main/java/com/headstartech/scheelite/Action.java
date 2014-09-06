package com.headstartech.scheelite;

import com.google.common.base.Optional;

/**
 * Encapsulates an action to be executed at state transitions.
 *
 * @param <T> entity type
 */
public interface Action<T> {

    String getName();

    void execute(T entity, Optional<?> event);
}
