package com.headstartech.scheelite;

import com.google.common.base.Predicate;

/**
 * Guard predicate enabling transitions.
 *
 * The argument to com.google.common.base.Predicate#apply(T) is never <code>null</code>.
 *
 * @param <T> entity type
 */
public interface Guard<T> extends Predicate<GuardArgs<T>> {

}
