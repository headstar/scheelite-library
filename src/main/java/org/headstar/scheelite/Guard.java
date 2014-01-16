package org.headstar.scheelite;

public interface Guard<T> {

    boolean accept(T entity, Object event);
}
