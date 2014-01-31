package org.headstar.scheelite;

import com.google.common.base.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

public class Transition<T, U> {

    private final State<T, U> fromState;
    private final State<T, U> toState;
    private final Optional<? extends Action<T>> action;
    private final Optional<? extends Guard<T>> guard;
    private final String name;

    public Transition(State<T, U> fromState, State<T, U> toState, Optional<? extends Action<T>> action, Optional<? extends Guard<T>> guard) {
        this.fromState = checkNotNull(fromState);
        this.toState = checkNotNull(toState);
        this.action = checkNotNull(action);
        this.guard = checkNotNull(guard);
        this.name = createName();
    }
    public Transition(State<T, U> fromState, State<T, U> toState, Optional<? extends Action<T>> action) {
        this(fromState, toState, action, Optional.<Guard<T>>absent());
    }

    public Transition(State<T, U> fromState, State<T, U> toState) {
        this(fromState, toState, Optional.<Action<T>>absent(), Optional.<Guard<T>>absent());
    }

    public String getName() {
        return name;
    }

    protected String createName() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s-TO-%s", fromState.getId(), toState.getId()));
        if(guard.isPresent()) {
            sb.append(String.format("[%s]", guard.get().getName()));
        }
        return sb.toString();
    }

    public State<T, U> getFromState() {
        return fromState;
    }

    public State<T, U> getToState() {
        return toState;
    }

    public Optional<? extends Action<T>> getAction() {
        return action;
    }

    public Optional<? extends Guard<T>> getGuard() {
        return guard;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transition that = (Transition) o;

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
