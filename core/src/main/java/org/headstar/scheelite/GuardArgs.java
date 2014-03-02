package org.headstar.scheelite;

import com.google.common.base.Optional;

/**
 * Container for arguments to a guard.
 * @param <T> entity type
 *
 * @see org.headstar.scheelite.Guard
 */
public class GuardArgs<T> {

    private final T entity;
    private final Optional<?> event;

    public GuardArgs(T entity, Optional<?> event) {
        this.entity = entity;
        this.event = event;
    }

    public T getEntity() {
        return entity;
    }

    public Optional<?> getEvent() {
        return event;
    }
}
