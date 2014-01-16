package org.headstar.scheelite;

public interface Action<T> {

    void execute(T entity, Object event);
}
