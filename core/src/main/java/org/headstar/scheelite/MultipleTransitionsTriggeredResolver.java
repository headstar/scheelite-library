package org.headstar.scheelite;

import java.util.Collection;

/**
 * Created by Per on 2014-01-17.
 */
public interface MultipleTransitionsTriggeredResolver<T, U> {

    Transition<T, U> resolve(T entity, Object event, Collection<Transition<T, U>> transitions);
}
