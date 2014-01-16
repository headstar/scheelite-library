package org.headstar.scheelite;

public class AlwaysAcceptsGuard<T> implements Guard<T> {

    @Override
    public boolean accept(T entity, Object event) {
        return true;
    }
}
