package com.headstartech.scheelite.guards;

import com.google.common.base.Optional;
import com.headstartech.scheelite.Guard;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Logical NOT guard.
 */
public class NotGuard<T> implements Guard<T> {
    final Guard<T> component;

    public NotGuard(Guard<T> component) {
        this.component = checkNotNull(component);
    }

    @Override
    public boolean evaluate(T context, Optional<?> event) throws Exception {
        return !component.evaluate(context, event);
    }

    public Guard<T> getComponent() {
        return component;
    }

    @Override
    public int hashCode() {
        return ~component.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NotGuard) {
            NotGuard<?> that = (NotGuard<?>) obj;
            return component.equals(that.component);
        }
        return false;
    }

    @Override
    public String toString() {
        return "Guards.not(" + component.toString() + ")";
    }
}
