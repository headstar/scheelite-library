package org.headstar.scheelite;

/**
 * Encapsulates the action to be executed at the initial transition from a super state to a sub state.
 * @param <T> entity type
 *
 * @see org.headstar.scheelite.InitialTransition
 */
public interface InitialAction<T> {

    String getName();

    void execute(T entity);
}
