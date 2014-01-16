package org.headstar.scheelite;

public interface StateMachine<T, U> {

    StateIdentifier process(StateIdentifier stateIdentifier, T entity, U context, Object event);
}