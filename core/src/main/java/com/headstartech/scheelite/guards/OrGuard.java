package com.headstartech.scheelite.guards;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.headstartech.scheelite.Guard;

import java.util.List;

/**
 * Logical OR guard.
 */
public class OrGuard<T> implements Guard<T> {

    private static final Joiner COMMA_JOINER = Joiner.on(',');

    private final List<? extends Guard<? super T>> components;

    public OrGuard(List<? extends Guard<? super T>> components) {
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

    public List<? extends Guard<? super T>> getComponents() {
        return Lists.newArrayList(components);  // defensive copy
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
