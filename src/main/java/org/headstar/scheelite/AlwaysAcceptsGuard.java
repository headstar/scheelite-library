package org.headstar.scheelite;

public class AlwaysAcceptsGuard<T, U> implements Guard<T, U> {

    @Override
    public boolean accept(T entity, U context, Object event) {
        return true;
    }
}
