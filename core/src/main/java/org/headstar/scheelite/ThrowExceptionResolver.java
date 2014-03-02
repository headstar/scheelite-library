package org.headstar.scheelite;

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

/**
 * Default policy when building a state machine,throws an <code>IllegalStateException</code>.
 *
 * @param <T> entity type
 * @param <U> state id type
 *
 * @see org.headstar.scheelite.StateMachineBuilder
 * @see org.headstar.scheelite.Transition
 */
public class ThrowExceptionResolver<T, U> implements MultipleTransitionsTriggeredResolver<T, U> {

    @Override
    public Transition<T, U> resolve(T entity, Object event, Collection<Transition<T, U>> transitions) {
        throw new IllegalStateException(String.format("multiple transitions triggered: " +
                "entity=[%s], transitions=[%s]", entity, transitions));
    }
}
