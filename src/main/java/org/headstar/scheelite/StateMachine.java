package org.headstar.scheelite;

public interface StateMachine<T extends Entity<?>> {

    void initialTransition(T entity);
    void process(T entity, Object event);
}