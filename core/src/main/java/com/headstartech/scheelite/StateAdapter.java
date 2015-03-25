package com.headstartech.scheelite;

/**
 * State adapter class.
 *
 * @param <T> context type
 * @param <U> state id type
 *
 * @see State
 */
public abstract class StateAdapter<T, U> implements State<T, U> {

    @Override
    public void onEntry(T context) {
        // nothing done here
    }

    @Override
    public void onExit(T context) {
        // nothing done here
    }

    @Override
    public boolean onEvent(T context, Object event) {
        // event not handled here
        return false;
    }
}
