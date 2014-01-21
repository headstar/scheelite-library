package org.headstar.scheelite;

public interface Guard<T> {

    String getName();

    boolean accept(T entity, Object event);
}
