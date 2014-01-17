package org.headstar.scheelite;

public interface StateMachine<T> {

    void process(T entity, Object event);
}