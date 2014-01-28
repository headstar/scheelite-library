package org.headstar.scheelite;

import com.google.common.base.Optional;

public interface Action<T> {

    String getName();

    void execute(T entity, Optional<?> event);
}
