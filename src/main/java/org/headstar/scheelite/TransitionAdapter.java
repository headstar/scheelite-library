package org.headstar.scheelite;

import com.google.common.base.Optional;

public class TransitionAdapter<T, U> implements Transition<T, U> {

    private final U inputStateId;
    private final U outputStateId;
    private final Action<T> action;
    private final Guard<T> guard;

    public TransitionAdapter(U inputStateId, U outputStateId, Action<T> action, Guard<T> guard) {
        this.inputStateId = inputStateId;
        this.outputStateId = outputStateId;
        this.action = action;
        this.guard = guard;
    }

    public TransitionAdapter(U inputStateId, U outputStateId) {
        this(inputStateId, outputStateId, null, new AlwaysAcceptsGuard<T>());
    }

    @Override
    public U getFromState() {
        return inputStateId;
    }

    @Override
    public U getToState() {
        return outputStateId;
    }

    @Override
    public Optional<Action<T>> getAction() {
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

        if (!inputStateId.equals(that.inputStateId)) return false;
        if (!outputStateId.equals(that.outputStateId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = inputStateId.hashCode();
        result = 31 * result + outputStateId.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TransitionAdapter [");
        sb.append("inputStateId=").append(inputStateId);
        sb.append(", outputStateId=").append(outputStateId);
        sb.append(']');
        return sb.toString();
    }
}
