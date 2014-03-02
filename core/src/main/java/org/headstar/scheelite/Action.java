package org.headstar.scheelite;

import com.google.common.base.Optional;

/**
 * Encapsulates the action to be executed at state transitions.
 *
 * @param <T> entity type
 */
public interface Action<T> {

    String getName();

    void execute(T entity, Optional<?> event);
}
