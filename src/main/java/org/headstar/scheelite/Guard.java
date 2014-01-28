package org.headstar.scheelite;

import com.google.common.base.Optional;

public interface Guard<T> {

    String getName();

    boolean accept(T entity, Optional<?> event);
}
