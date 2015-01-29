package com.headstartech.scheelite;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Static utility methods pertaining to <code>Guard</code> instances.
 */
public class Guards {

    private static final Joiner COMMA_JOINER = Joiner.on(',');

    private Guards() {}

    public static <T> Guard<T> and(final Guard<T>... components) {
        return of(Predicates.<GuardArgs<T>>and(components), new GuardDescription() {
            @Override
            public String getDescription() {
                return "Guards.and(" + COMMA_JOINER.join(components)  + ")";
            }
        });
    }

    public static <T> Guard<T> or(final Guard<T>... components) {
        return of(Predicates.<GuardArgs<T>>or(components), new GuardDescription() {
            @Override
            public String getDescription() {
                return "Guards.or(" + COMMA_JOINER.join(components)  + ")";
            }
        });
    }

    public static <T> Guard<T> not(final Guard<T> component) {
        return of(Predicates.<GuardArgs<T>>not(component), new GuardDescription() {
            @Override
            public String getDescription() {
                return "Guards.not(" + component + ")";
            }
        });
    }


    public static <T> Guard<T> of(Predicate<GuardArgs<T>> pred) {
        return new GuardPredicate<T>(pred, null);
    }

    private static <T> Guard<T> of(Predicate<GuardArgs<T>> pred, GuardDescription guardDescription) {
        return new GuardPredicate<T>(pred, guardDescription);
    }

    private static interface GuardDescription {
        String getDescription();
    }

    private static class GuardPredicate<T> implements Guard<T> {

        private final Predicate<GuardArgs<T>> pred;
        private final GuardDescription guardDescription;

        private GuardPredicate(Predicate<GuardArgs<T>> pred, GuardDescription guardDescription) {
            checkNotNull(pred);
            this.pred = pred;
            this.guardDescription = guardDescription;
        }

        @Override
        public boolean apply(GuardArgs<T> input) {
            return pred.apply(input);
        }

        @Override
        public String toString() {
            if(guardDescription != null) {
                return guardDescription.getDescription();
            } else {
                return pred.toString();
            }
        }
    }
}
