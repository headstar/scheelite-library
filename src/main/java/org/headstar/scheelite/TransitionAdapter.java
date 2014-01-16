package org.headstar.scheelite;

import com.google.common.base.Optional;

public class TransitionAdapter<T, U> implements Transition<T, U> {

    private final Object inputStateId;
    private final Object outputStateId;
    private final Action<T, U> action;
    private final Guard<T, U> guard;

    public TransitionAdapter(Object inputStateId, Object outputStateId, Action<T, U> action, Guard<T, U> guard) {
        this.inputStateId = inputStateId;
        this.outputStateId = outputStateId;
        this.action = action;
        this.guard = guard;
    }

    public TransitionAdapter(Object inputStateId, Object outputStateId) {
        this(inputStateId, outputStateId, null, new AlwaysAcceptsGuard<T, U>());
    }

    @Override
    public Object getFromState() {
        return inputStateId;
    }

    @Override
    public Object getToState() {
        return outputStateId;
    }

    @Override
    public Optional<Action<T, U>> getAction() {
        return Optional.of(action);
    }

    @Override
    public Guard<T, U> getGuard() {
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
