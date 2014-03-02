package org.headstar.scheelite;

import com.google.common.base.Optional;

import java.util.Collection;

/**
 *
 * @param <T> entity type
 * @param <U> state id type
 */
interface TransitionMap<T, U> {

    Collection<Transition<T,U>> getTransitionsFromState(State<T,U> state);

    Optional<InitialTransition<T, U>> getInitialTransitionFromState(State<T, U> state);

    InitialTransition<T, U> getInitialTransitionFromRoot();

}
