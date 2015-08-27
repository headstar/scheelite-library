package com.headstartech.scheelite;

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Static utility methods pertaining to <code>Guard</code> instances.
 */
public class Guards {

    private Guards() {
    }

    /**
     * Returns a guard that evaluates to {@code true} if each of its
     * components evaluates to {@code true}.
     *
     * @param components
     * @param <T>
     * @return
     */
    public static <T> Guard<T> and(final Guard<T>... components) {
        return new AndGuard<T>(defensiveCopy(components));
    }

    /**
     * Returns a predicate that evaluates to {@code true} if any one of its
     * components evaluates to {@code true}.
     *
     * @param components
     * @param <T>
     * @return
     */
    public static <T> Guard<T> or(final Guard<T>... components) {
        return new OrGuard<T>(defensiveCopy(components));
    }

    /**
     * Returns a predicate that evaluates to {@code true} if the given guard
     * evaluates to {@code false}.
     *
     * @param component
     * @param <T>
     * @return
     */
    public static <T> Guard<T> not(final Guard<T> component) {
       return new NotGuard<T>(component);
    }

    // Guards with a lot of inspiration from com.google.common.base.Predicates

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
