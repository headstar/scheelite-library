package org.headstar.scheelite;

import com.google.common.base.Optional;

public class TransitionAdapter<T, U> implements Transition<T, U> {

    private final U fromState;
    private final U toState;
    private final Action<T> action;
    private final Guard<T> guard;

    public TransitionAdapter(U fromState, U toState, Action<T> action, Guard<T> guard) {
        this.fromState = fromState;
        this.toState = toState;
        this.action = action;
        this.guard = guard;
    }

    public TransitionAdapter(U fromState, U toState) {
        this(fromState, toState, null, new AlwaysAcceptsGuard<T>());
    }

    @Override
    public String getName() {
        return String.format("%s-TO-%s.%s", fromState, toState, guard.getName());
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
        return Optional.of(action);
    }

    @Override
    public Guard<T> getGuard() {
        return guard;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransitionAdapter that = (TransitionAdapter) o;

        if (action != null ? !action.equals(that.action) : that.action != null) return false;
        if (fromState != null ? !fromState.equals(that.fromState) : that.fromState != null) return false;
        if (guard != null ? !guard.equals(that.guard) : that.guard != null) return false;
        if (toState != null ? !toState.equals(that.toState) : that.toState != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = fromState != null ? fromState.hashCode() : 0;
        result = 31 * result + (toState != null ? toState.hashCode() : 0);
        result = 31 * result + (action != null ? action.hashCode() : 0);
        result = 31 * result + (guard != null ? guard.hashCode() : 0);
        return result;
    }
}
