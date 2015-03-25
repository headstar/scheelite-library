package com.headstartech.scheelite;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Static utility methods pertaining to <code>Guard</code> instances.
 */
public class Guards {

    private static final Joiner COMMA_JOINER = Joiner.on(',');

    private Guards() {
    }

    public static <T> Guard<T> and(final Guard<T>... components) {
        return new AndGuard<T>(defensiveCopy(components));
    }

    public static <T> Guard<T> or(final Guard<T>... components) {
        return new OrGuard<T>(defensiveCopy(components));
    }

    public static <T> Guard<T> not(final Guard<T> component) {
       return new NotGuard<T>(component);
    }

    // Guards with a lot of inspiration from com.google.common.base.Predicates

    private static class AndGuard<T> implements Guard<T> {
        private final List<? extends Guard<? super T>> components;

        private AndGuard(List<? extends Guard<? super T>> components) {
            this.components = components;
        }

        @Override
        public boolean evaluate(T context, Optional<?> event) throws Exception {
            for (int i = 0; i < components.size(); i++) {
                if (!components.get(i).evaluate(context, event)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            // add a random number to avoid collisions with OrGuard
            return components.hashCode() + 0x12472c2c;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof AndGuard) {
                AndGuard<?> that = (AndGuard<?>) obj;
                return components.equals(that.components);
            }
            return false;
        }

        @Override
        public String toString() {
            return "Guards.and(" + COMMA_JOINER.join(components) + ")";
        }
    }

    private static class OrGuard<T> implements Guard<T> {
        private final List<? extends Guard<? super T>> components;

        private OrGuard(List<? extends Guard<? super T>> components) {
            this.components = components;
        }

        @Override
        public boolean evaluate(T context, Optional<?> event) throws Exception {
            // Avoid using the Iterator to avoid generating garbage (issue 820).
            for (int i = 0; i < components.size(); i++) {
                if (components.get(i).evaluate(context, event)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public int hashCode() {
            // add a random number to avoid collisions with AndGuard
            return components.hashCode() + 0x053c91cf;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof OrGuard) {
                OrGuard<?> that = (OrGuard<?>) obj;
                return components.equals(that.components);
            }
            return false;
        }

        @Override
        public String toString() {
            return "Guards.or(" + COMMA_JOINER.join(components) + ")";
        }
    }

    private static class NotGuard<T> implements Guard<T> {
        final Guard<T> guard;

        NotGuard(Guard<T> guard) {
            this.guard = checkNotNull(guard);
        }

        @Override
        public boolean evaluate(T context, Optional<?> event) throws Exception {
            return !guard.evaluate(context, event);
        }

        @Override
        public int hashCode() {
            return ~guard.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof NotGuard) {
                NotGuard<?> that = (NotGuard<?>) obj;
                return guard.equals(that.guard);
            }
            return false;
        }

        @Override
        public String toString() {
            return "Guards.not(" + guard.toString() + ")";
        }
    }

    private static <T> List<T> defensiveCopy(T... array) {
        return defensiveCopy(Arrays.asList(array));
    }

    private static <T> List<T> defensiveCopy(Iterable<T> iterable) {
        ArrayList<T> list = new ArrayList<T>();
        for (T element : iterable) {
            list.add(checkNotNull(element));
        }
        return list;
    }

}
