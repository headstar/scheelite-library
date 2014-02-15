package org.headstar.scheelite;

import com.google.common.base.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

class Transition<T, U> {

    private final State<T, U> fromState;
    private final State<T, U> toState;
    private final Optional<? extends Action<T>> action;
    private final Optional<? extends Guard<T>> guard;
    private final TransitionType transitionType;
    private final String name;

    Transition(State<T, U> fromState, State<T, U> toState, Optional<? extends Action<T>> action, Optional<? extends Guard<T>> guard, TransitionType transitionType) {
        this.transitionType = checkNotNull(transitionType);
        this.fromState = checkNotNull(fromState);
        this.toState = checkNotNull(toState);
        this.action = checkNotNull(action);
        this.guard = checkNotNull(guard);
        this.name = createName();
    }

    Transition(State<T, U> fromState, State<T, U> toState, Optional<? extends Action<T>> action, TransitionType transitionType) {
        this(fromState, toState, action, Optional.<Guard<T>>absent(), transitionType);
    }

    Transition(State<T, U> fromState, State<T, U> toState, TransitionType transitionType) {
        this(fromState, toState, Optional.<Action<T>>absent(), Optional.<Guard<T>>absent(), transitionType);
    }

    String getName() {
        return name;
    }

    private String createName() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s-TO-%s", fromState.getId(), toState.getId()));
        if(guard.isPresent()) {
            sb.append(String.format("[%s]", guard.get().getName()));
        }
        return sb.toString();
    }

    TransitionType getTransitionType() {
        return transitionType;
    }

    State<T, U> getFromState() {
        return fromState;
    }

    State<T, U> getToState() {
        return toState;
    }

    Optional<? extends Action<T>> getAction() {
        return action;
    }

    Optional<? extends Guard<T>> getGuard() {
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
