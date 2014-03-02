package org.headstar.scheelite;

import com.google.common.base.Optional;

/**
 * Created by per on 02/03/14.
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
