package org.headstar.scheelite;

public interface State<T, U> {

    U getId();

    void onEntry(T entity);

    void onExit(T entity);

    void onEvent(T entity, Object event);
}
