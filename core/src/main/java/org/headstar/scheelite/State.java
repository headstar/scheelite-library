package org.headstar.scheelite;

public interface State<T, U> {

    U getId();
    void onEntry(T entity);
    void onExit(T entity);
    boolean onEvent(T entity, Object event);
}
