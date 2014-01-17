package org.headstar.scheelite;

import java.util.Collection;

/**
 * Created by Per on 2014-01-17.
 */
public interface MultipleTransitionsTriggeredPolicy<T, U> {

    Transition<T, U> triggeredTransitions(U stateIdentifier, T entity, Object event, Collection<Transition<T, U>> transitions);
}
