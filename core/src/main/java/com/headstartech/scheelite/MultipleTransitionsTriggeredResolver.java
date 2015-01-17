package com.headstartech.scheelite;

import com.google.common.base.Optional;

import java.util.Collection;
import java.util.List;

/**
 * Encapsulates the action to be taken when multiple transitions have been triggered.
 *
 * @param <T> entity type
 * @param <U> state id type
 *
 */
public interface MultipleTransitionsTriggeredResolver<T, U> {

    /**
     * Resolves a situation where multiple transitions have triggered.
     *
     * @param stateId State id of current state.
     * @param entity Entity
     * @param event Event triggering the transitions.
     * @param transitions Triggered transitions, ordered according to source state's distance from the root (furthest away first).
     * @return The transition to execute
     */
    Transition<T, U> resolve(U stateId, T entity, Optional<?> event, List<Transition<T, U>> transitions);
}
