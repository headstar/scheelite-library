package com.headstartech.scheelite;

import com.google.common.base.Predicate;

/**
 * Guard predicate enabling transitions.
 * @param <T> entity type
 */
public interface Guard<T> extends Predicate<GuardArgs<T>> {

}
