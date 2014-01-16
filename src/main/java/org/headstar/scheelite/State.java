package org.headstar.scheelite;

public interface State<T, U> {

    StateIdentifier getIdentifier();

    void onEntry(T entity, U context);
    void onExit(T entity, U context);
    void onEvent(T entity, U context, Object event);
}
