package org.headstar.scheelite;

public interface StateMachine<T, U> {

    Object process(Object stateIdentifier, T entity, U context, Object event);
}