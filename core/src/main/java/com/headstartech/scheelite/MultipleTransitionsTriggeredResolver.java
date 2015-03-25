package com.headstartech.scheelite;

import com.google.common.base.Optional;

import java.util.List;

/**
 * Encapsulates the action to be taken when multiple transitions have been triggered.
 *
 * @param <T> context type
 * @param <U> state id type
 *
 */
public interface MultipleTransitionsTriggeredResolver<T, U> {

    /**
     * Resolves a situation where multiple transitions have triggered.
     *
     * @param stateId state id of current state.
     * @param context the context
     * @param event event triggering the transitions.
     * @param transitions triggered transitions, ordered according to source state's distance from the root (furthest away first).
     * @return the transition to execute
     *
     * @throws java.lang.Exception
     */
    Transition<T, U> resolve(U stateId, T context, Optional<?> event, List<Transition<T, U>> transitions) throws Exception;
}
