package org.headstar.scheelite;

public interface Guard<T, U> {

    boolean accept(T entity, U context, Object event);
}
