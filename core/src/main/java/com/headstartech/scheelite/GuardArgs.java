package com.headstartech.scheelite;

import com.google.common.base.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Container for arguments to a <code>Guard</code>.
 *
 * @param <T> context type
 *
 * @see Guard
 */
public class GuardArgs<T> {

    private final T context;
    private final Optional<?> event;

    public GuardArgs(T context, Optional<?> event) {
        this.context = checkNotNull(context);
        this.event = checkNotNull(event);
    }

    public T getEntity() {
        return context;
    }

    public Optional<?> getEvent() {
        return event;
    }
}
