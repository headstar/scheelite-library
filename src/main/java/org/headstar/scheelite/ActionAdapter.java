package org.headstar.scheelite;

public abstract class ActionAdapter<T> implements Action<T>  {

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
}