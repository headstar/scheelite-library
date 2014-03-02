package org.headstar.scheelite;

import java.util.Collection;

/**
 * Encapsulates the action to be taken when multiple transitions have been triggered.
 *
 * @param <T> entity type
 * @param <U> state id type
 *
 */
public interface MultipleTransitionsTriggeredResolver<T, U> {

    Transition<T, U> resolve(T entity, Object event, Collection<Transition<T, U>> transitions);
}
