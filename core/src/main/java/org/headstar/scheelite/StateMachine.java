package org.headstar.scheelite;

public interface StateMachine<T, U> {
    U processInitialTransition(T entity);
    U processEvent(T entity, U stateId, Object event);
}