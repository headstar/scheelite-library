package com.headstartech.scheelite;

import com.google.common.base.Optional;

import java.util.Collection;
import java.util.Set;

/**
 *
 * @param <T> entity type
 * @param <U> state id type
 */
interface TransitionMap<T, U> {

    Set<Transition<T,U>> getTransitions();

    Collection<Transition<T,U>> getTransitionsFromState(State<T,U> state);

    Optional<Transition<T, U>> getInitialTransitionFromState(State<T, U> state);

}
