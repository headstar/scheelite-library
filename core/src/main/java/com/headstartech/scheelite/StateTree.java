package com.headstartech.scheelite;

import com.google.common.base.Optional;

import java.util.List;
import java.util.Set;

/**
 *
 * @param <T> entity type
 * @param <U> state id type
 */
interface StateTree<T, U> {
    Set<State<T, U>> getStates();

    Optional<State<T, U>> getState(U id);

    boolean exists(State<T, U> a);

    boolean isChild(State<T, U> a);

    public boolean isParent(State<T, U> a);

    Optional<State<T, U>> getParent(State<T, U> a);

    boolean isChildOf(State<T, U> a, State<T, U> b);

    boolean isParentOf(State<T, U> a, State<T, U> b);

    boolean isAncestorOf(State<T, U> a, State<T, U> b);

    boolean isDescendantOf(State<T, U> a, State<T, U> b);

    Optional<State<T, U>> getLowestCommonAncestor(State<T, U> a, State<T, U> b);

    List<State<T, U>> getPathBetween(State<T, U> a, Optional<State<T, U>> bOpt);

    List<State<T, U>> getPathToRootState(State<T, U> state);
}
