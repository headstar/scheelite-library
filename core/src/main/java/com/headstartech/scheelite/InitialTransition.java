package com.headstartech.scheelite;

import com.google.common.base.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Encapsulates the initial transition to be taken when entering a composite state (including the implicit root state).
 *
 * @param <T> entity type
 * @param <U> state id type
 *
 * @see InitialAction
 * @see State
 */
final class InitialTransition<T, U> {

    private final State<T, U> fromState;
    private final State<T, U> toState;
    private final Optional<? extends InitialAction<T>> action;

    InitialTransition(State<T, U> fromState, State<T, U> toState, InitialAction<T> action) {
        this.fromState = checkNotNull(fromState);
        this.toState = checkNotNull(toState);
        this.action = Optional.fromNullable(action);
    }

    State<T, U> getToState() {
        return toState;
    }

    State<T, U> getFromState() {
        return fromState;
    }

    Optional<? extends InitialAction<T>> getAction() {
        return action;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InitialTransition that = (InitialTransition) o;

        if (!action.equals(that.action)) return false;
        if (!fromState.equals(that.fromState)) return false;
        if (!toState.equals(that.toState)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = fromState.hashCode();
        result = 31 * result + toState.hashCode();
        result = 31 * result + action.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s->%s", fromState.getId(), toState.getId()));
        return sb.toString();
    }

}
