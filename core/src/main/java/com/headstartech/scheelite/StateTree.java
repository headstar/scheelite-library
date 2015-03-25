package com.headstartech.scheelite;

import com.google.common.base.Optional;

import java.util.List;
import java.util.Set;

/**
 *
 * @param <T> context type
 * @param <U> state id type
 */
interface StateTree<T, U> {

    State<T, U> getRootState();

    Set<State<T, U>> getStates();

    Optional<State<T, U>> getState(U id);

    boolean exists(State<T, U> a);

    public boolean isParent(State<T, U> a);

    Optional<State<T, U>> getParent(State<T, U> a);

    boolean isAncestorOf(State<T, U> a, State<T, U> b);

    boolean isDescendantOf(State<T, U> a, State<T, U> b);

    State<T, U> getLowestCommonAncestor(State<T, U> a, State<T, U> b);

    public List<State<T, U>> getPathToAncestor(State<T, U> a, State<T, U> b, boolean includeAncestor);
}
