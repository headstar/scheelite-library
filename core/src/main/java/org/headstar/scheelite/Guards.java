package org.headstar.scheelite;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by per on 02/03/14.
 */
public class Guards {

    public static <T> Guard<T> of(Predicate<GuardArgs<T>> pred) {
        return new GuardPredicate<T>(pred);
    }

    public static <T> Guard<T> and(Guard<T>... components) {
        return of(Predicates.and(components));
    }

    public static <T> Guard<T> eventInstanceOf(Class<?> clazz) {
        return new EventInstanceOf<T>(clazz);
    }

    private static class GuardPredicate<T> implements Guard<T> {

        private final Predicate<GuardArgs<T>> pred;

        private GuardPredicate(Predicate<GuardArgs<T>> pred) {
            checkNotNull(pred);
            this.pred = pred;
        }

        @Override
        public boolean apply(GuardArgs<T> input) {
            return pred.apply(input);
        }
    }

    private static class EventInstanceOf<T> implements Guard<T> {

        private final Class<?> clazz;

        EventInstanceOf(Class<?> clazz) {
            checkNotNull(clazz);
            this.clazz = clazz;
        }

        @Override
        public String toString() {
            return clazz.getSimpleName();
        }

        @Override
        public boolean apply(GuardArgs<T> input) {
            return input.getEvent().isPresent() && clazz.isInstance(input.getEvent().get());
        }
    }
}
