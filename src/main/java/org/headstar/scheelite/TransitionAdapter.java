package org.headstar.scheelite;

import com.google.common.base.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

public class TransitionAdapter<T, U> implements Transition<T, U> {

    private final U fromState;
    private final U toState;
    private final Optional<? extends Action<T>> action;
    private final Optional<? extends Guard<T>> guard;

    public TransitionAdapter(U fromState, U toState, Optional<? extends Action<T>> action, Optional<? extends Guard<T>> guard) {
        this.fromState = checkNotNull(fromState);
        this.toState = checkNotNull(toState);
        this.action = checkNotNull(action);
        this.guard = checkNotNull(guard);
    }

    public TransitionAdapter(U fromState, U toState) {
        this(fromState, toState, Optional.<Action<T>>absent(), Optional.<Guard<T>>absent());
    }

    @Override
    public String getName() {
        return String.format("%s-TO-%s", fromState, toState);
    }

    @Override
    public U getFromState() {
        return fromState;
    }

    @Override
    public U getToState() {
        return toState;
    }

    @Override
    public Optional<? extends Action<T>> getAction() {
        return action;
    }

    @Override
    public Optional<? extends Guard<T>> getGuard() {
        return guard;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransitionAdapter that = (TransitionAdapter) o;

        if (!action.equals(that.action)) return false;
        if (!fromState.equals(that.fromState)) return false;
        if (!guard.equals(that.guard)) return false;
        if (!toState.equals(that.toState)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = fromState.hashCode();
        result = 31 * result + toState.hashCode();
        result = 31 * result + action.hashCode();
        result = 31 * result + guard.hashCode();
        return result;
    }
}
