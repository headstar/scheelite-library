package org.headstar.scheelite;

public abstract class StateAdapter<T, U> implements State<T, U> {

    @Override
    public void onEntry(T entity, U context) {
        // do nothing
    }

    @Override
    public void onExit(T entity, U context) {
        // do nothing
    }

    @Override
    public void onEvent(T entity, U context, Object event) {
        // do nothing
    }
}
