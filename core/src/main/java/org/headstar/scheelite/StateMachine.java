package org.headstar.scheelite;

public interface StateMachine<T extends Entity<?>> {

    void initialTransition(T entity);
    void processEvent(T entity, Object event);
}