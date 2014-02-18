package org.headstar.scheelite;

/**
 * Created by Per on 2014-01-25.
 */
public abstract class StateAdapter<T, U> implements State<T, U> {

    @Override
    public void onEntry(T entity) {

    }

    @Override
    public void onExit(T entity) {

    }

    @Override
    public boolean onEvent(T entity, Object event) {
        return false;
    }
}
