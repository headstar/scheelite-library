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
     * @param stateId state id of current state
     * @param entity entity
     * @param event event triggering the transitions
     * @param transitions triggered transitions
     * @return the transition to execute
     */
    Transition<T, U> resolve(U stateId, T entity, Optional<?> event, List<Transition<T, U>> transitions);
}
