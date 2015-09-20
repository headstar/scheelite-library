package com.headstartech.scheelite;

/**
 * Represents a final state.
 * No transitions from the state are allowed.
 * No events propagated to super state.
 * Nothing done on exit (can exit the final state if it's a substate).
 *
 * @param <T> context type
 * @param <U> state id type
 *
 * @see State
 */
public abstract class FinalState<T, U> extends StateAdapter<T, U> {

    @Override
    public final void onExit(T context) {
        // do nothing
    }

    @Override
    public final boolean onEvent(T context, Object event) {
        // event not propagated
        return true;
    }
}
