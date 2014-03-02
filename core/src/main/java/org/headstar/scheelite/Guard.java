package org.headstar.scheelite;

import com.google.common.base.Predicate;

public interface Guard<T> extends Predicate<GuardArgs<T>> {

}
