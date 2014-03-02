package org.headstar.scheelite;

/**
 * State adapter class.
 *
 * @param <T> entity type
 * @param <U> state id type
 *
 * @see org.headstar.scheelite.State
 */
public abstract class StateAdapter<T, U> implements State<T, U> {

    @Override
    public void onEntry(T entity) {
        // nothing done here
    }

    @Override
    public void onExit(T entity) {
        // nothing done here
    }

    @Override
    public boolean onEvent(T entity, Object event) {
        // event not handled here
        return false;
    }
}
