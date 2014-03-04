package org.headstar.scheelite;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Static utility methods pertaining to <code>Guard</code> instances.
 */
public class Guards {

    private Guards() {}

    public static <T> Guard<T> of(Predicate<GuardArgs<T>> pred) {
        return new GuardPredicate<T>(pred);
    }

    public static <T> Guard<T> and(Guard<T>... components) {
        return of(Predicates.<GuardArgs<T>>and(components));
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
}
