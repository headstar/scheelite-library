package org.headstar.scheelite;

public interface StateMachine<T> {

    Object process(Object stateId, T entity, Object event);
}