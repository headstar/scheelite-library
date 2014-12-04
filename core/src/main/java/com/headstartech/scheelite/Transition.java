package com.headstartech.scheelite;

import com.google.common.base.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * @param <T> entity type
 * @param <U> state id type
 */
public class Transition<T, U> {

    private final State<T, U> fromState;
    private final State<T, U> toState;
    private final Optional<? extends Action<T>> action;
    private final Optional<? extends Guard<T>> guard;
    private final Optional<Class<?>> triggerEventClass;
    private final TransitionType transitionType;

    Transition(State<T, U> fromState, State<T, U> toState, TransitionType transitionType, Optional<Class<?>> triggerEventClass, Optional<? extends Guard<T>> guard, Optional<? extends Action<T>> action) {
        this.transitionType = checkNotNull(transitionType);
        this.fromState = checkNotNull(fromState);
        this.toState = checkNotNull(toState);
        this.triggerEventClass = checkNotNull(triggerEventClass);
        this.action = checkNotNull(action);
        this.guard = checkNotNull(guard);
    }

    public TransitionType getTransitionType() {
        return transitionType;
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

    public Optional<Class<?>> getTriggerEventClass() {
        return triggerEventClass;
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
        if (transitionType != that.transitionType) return false;
        if (!triggerEventClass.equals(that.triggerEventClass)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = fromState.hashCode();
        result = 31 * result + toState.hashCode();
        result = 31 * result + action.hashCode();
        result = 31 * result + guard.hashCode();
        result = 31 * result + triggerEventClass.hashCode();
        result = 31 * result + transitionType.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s->%s", fromState.getId(), toState.getId()));
        if(triggerEventClass.isPresent()) {
            sb.append(String.format(" %s", triggerEventClass.get().getSimpleName()));
        }
        if(guard.isPresent()) {
            sb.append(String.format("[%s]", guard.get()));
        }
        return sb.toString();
    }
}
