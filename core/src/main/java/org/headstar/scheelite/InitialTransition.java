package org.headstar.scheelite;

import com.google.common.base.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Per on 2014-01-24.
 */
class InitialTransition<T, U> {

    private final Optional<State<T, U>> fromState;
    private final State<T, U> toState;
    private final Optional<? extends InitialAction<T>> action;

    InitialTransition(Optional<State<T, U>> fromState, State<T, U> toState, Optional<? extends InitialAction<T>> action) {
        this.fromState = checkNotNull(fromState);
        this.toState = checkNotNull(toState);
        this.action = checkNotNull(action);
    }

    State<T, U> getToState() {
        return toState;
    }

    Optional<State<T, U>> getFromState() {
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
        if(fromState.isPresent()) {
            sb.append(String.format("%s->%s", fromState.get().getId(), toState.getId()));
        } else {
            sb.append(String.format("->%s", toState.getId()));
        }
        return sb.toString();
    }

}
