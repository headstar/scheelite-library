package org.headstar.scheelite;

public interface Action<T, U> {

    void execute(T entity, U context, Object event);
}
