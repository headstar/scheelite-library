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

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransitionAdapter that = (TransitionAdapter) o;

        if (!fromState.equals(that.fromState)) return false;
        if (!toState.equals(that.toState)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = fromState.hashCode();
        result = 31 * result + toState.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TransitionAdapter [");
        sb.append("fromState=").append(fromState);
        sb.append(", toState=").append(toState);
        sb.append(']');
        return sb.toString();
    }
}
