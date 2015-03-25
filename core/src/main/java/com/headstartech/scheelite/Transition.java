package com.headstartech.scheelite;

import com.google.common.base.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A transition in the state machine.
 *
 * @param <T> context type
 * @param <U> state id type
 *
 */
public class Transition<T, U> {

    private final State<T, U> mainSourceState;
    private final State<T, U> mainTargetState;
    private final Optional<Action<T> >action;
    private final Optional<Guard<T>> guard;
    private final Optional<Class<?>> triggerEventClass;
    private final TransitionType transitionType;


    public static <T,U> InitialBuilder<T, U> initialBuilder(State<T, U> mainSourceState, State<T, U> mainTargetState) {
        return new InitialBuilder<T, U>(mainSourceState, mainTargetState);
    }

    public static <T,U> ExternalOrLocalBuilder<T, U> externalBuilder(State<T, U> mainSourceState, State<T, U> mainTargetState) {
        return new ExternalOrLocalBuilder<T, U>(mainSourceState, mainTargetState, TransitionType.EXTERNAL);
    }

    public static <T,U> ExternalOrLocalBuilder<T, U> localBuilder(State<T, U> mainSourceState, State<T, U> mainTargetState) {
        return new ExternalOrLocalBuilder<T, U>(mainSourceState, mainTargetState, TransitionType.LOCAL);
    }

    private static <T,U> Transition<T, U> create(State<T, U> mainSourceState, State<T, U> mainTargetState, TransitionType transitionType,
                                          Class<?> triggerEventClass, Guard<T> guard, Action<T> action) {
        return new Transition<T, U>(mainSourceState, mainTargetState, transitionType, triggerEventClass, guard, action);
    }

    public static class InitialBuilder<T, U> {

        private final State<T, U> mainSourceState;
        private final State<T, U> mainTargetState;
        private Action<T>  action;

        public InitialBuilder(State<T, U> mainSourceState, State<T, U> mainTargetState) {
            this.mainSourceState = mainSourceState;
            this.mainTargetState = mainTargetState;
        }

        public InitialBuilder<T, U> withAction(Action<T> action) {
            this.action = action;
            return this;
        }

        public Transition<T, U> build() {
            return create(mainSourceState, mainTargetState, TransitionType.INITIAL, null, null, action);
        }
    }

    public static class ExternalOrLocalBuilder<T, U> {

        private final State<T, U> mainSourceState;
        private final State<T, U> mainTargetState;
        private final TransitionType transitionType;
        private Class<?> triggerEventClass;
        private Guard<T> guard;
        private Action<T>  action;

        public ExternalOrLocalBuilder(State<T, U> mainSourceState, State<T, U> mainTargetState, TransitionType transitionType) {
            this.mainSourceState = mainSourceState;
            this.mainTargetState = mainTargetState;
            this.transitionType = transitionType;
        }

        public ExternalOrLocalBuilder<T, U> withTriggerEventClass(Class<?> triggerEventClass) {
            this.triggerEventClass = triggerEventClass;
            return this;
        }

        public ExternalOrLocalBuilder<T, U> withGuard(Guard<T> guard) {
            this.guard = guard;
            return this;
        }

        public ExternalOrLocalBuilder<T, U> withAction(Action<T> action) {
            this.action = action;
            return this;
        }

        public Transition<T, U> build() {
            return create(mainSourceState, mainTargetState, transitionType, triggerEventClass, guard, action);
        }
    }


    Transition(State<T, U> mainSourceState, State<T, U> mainTargetState, TransitionType transitionType, Class<?> triggerEventClass, Guard<T> guard, Action<T> action) {
        this.transitionType = checkNotNull(transitionType);
        this.mainSourceState = checkNotNull(mainSourceState);
        this.mainTargetState = checkNotNull(mainTargetState);
        this.triggerEventClass = Optional.<Class<?>>fromNullable(triggerEventClass);
        this.action = Optional.fromNullable(action);
        this.guard = Optional.fromNullable(guard);
        checkArgument(!TransitionType.INITIAL.equals(this.transitionType) || triggerEventClass == null, "triggerEventClass must be null for initial transition");
        checkArgument(!TransitionType.INITIAL.equals(this.transitionType) || guard == null, "guard must be null for initial transition");

    }

    /**
     * Gets the {@link com.headstartech.scheelite.TransitionType}.
     *
     * @return
     */
    public TransitionType getTransitionType() {
        return transitionType;
    }

    /**
     * Gets the main source {@link com.headstartech.scheelite.State}.
     * @return
     */
    public State<T, U> getMainSourceState() {
        return mainSourceState;
    }


    /**
     * Gets the main target {@link com.headstartech.scheelite.State}.
     *
     * @return
     */
    public State<T, U> getMainTargetState() {
        return mainTargetState;
    }

    /**
     * Gets the transition {@link com.headstartech.scheelite.Action}.
     *
     * @return
     */
    public Optional<? extends Action<T>> getAction() {
        return action;
    }

    /**
     * Gets the transition {@link com.headstartech.scheelite.Guard}.
     *
     *
     * @return the guard or {@code Optional.absent()} if there is no guard. If the transition type is {@code TransitionType.INITIAL}. {@code Optional.absent()}
     * will be returned.
     */
    public Optional<? extends Guard<T>> getGuard() {
        return guard;
    }

    /**
     * Gets the event class triggering the transition.
     *
     * @return the event class or {@code Optional.absent()} if there is no event class. If the transition type is {@code TransitionType.INITIAL}. {@code Optional.absent()}
     * will be returned.
     */
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
        if (!mainSourceState.equals(that.mainSourceState)) return false;
        if (!guard.equals(that.guard)) return false;
        if (!mainTargetState.equals(that.mainTargetState)) return false;
        if (transitionType != that.transitionType) return false;
        if (!triggerEventClass.equals(that.triggerEventClass)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mainSourceState.hashCode();
        result = 31 * result + mainTargetState.hashCode();
        result = 31 * result + action.hashCode();
        result = 31 * result + guard.hashCode();
        result = 31 * result + triggerEventClass.hashCode();
        result = 31 * result + transitionType.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s->%s", mainSourceState.getId(), mainTargetState.getId()));
        if(triggerEventClass.isPresent()) {
            sb.append(String.format(" %s", triggerEventClass.get().getSimpleName()));
        }
        if(guard.isPresent()) {
            sb.append(String.format("[%s]", guard.get()));
        }
        return sb.toString();
    }
}
