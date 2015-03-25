package com.headstartech.scheelite;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;

/**
 * Guard predicate enabling transitions.
 *
 * @param <T> entity type
 */
public interface Guard<T> {

    /**
     * Evaluates a guard condition
     *
     * @param context
     * @param event
     * @return <code>true</code> if the guard condition is true, <code>false</code> otherwise
     */
    boolean evaluate(T context, Optional<?> event) throws Exception;

}
