package org.headstar.scheelite;

public class AlwaysAcceptsGuard<T> extends GuardAdapter<T> {

    @Override
    public boolean accept(T entity, Object event) {
        return true;
    }
}
