package org.headstar.scheelite;

public abstract class GuardAdapter<T> implements Guard<T> {

    @Override
    public String getName() {
        return this.getClass().getName();
    }
}
