package com.headstartech.scheelite;

import com.google.common.base.Optional;

/**
 * Guard predicate enabling transitions.
 *
 * @param <T> context type
 */
public interface Guard<T> {

    /**
     * Evaluates a guard condition
     *
     * @param context the context
     * @param event the current event (if any)
     * @return <code>true</code> if the guard condition is true, <code>false</code> otherwise
     */
    boolean evaluate(T context, Optional<?> event) throws Exception;

}
