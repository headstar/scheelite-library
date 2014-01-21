package org.headstar.scheelite;

public interface Action<T> {

    String getName();

    void execute(T entity, Object event);
}
