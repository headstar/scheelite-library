package com.headstartech.scheelite;

import com.google.common.base.Optional;

import java.util.Collection;

/**
 * Default policy when building a state machine, throws an <code>IllegalStateException</code>.
 *
 * @param <T> entity type
 * @param <U> state id type
 *
 * @see StateMachineBuilder
 * @see Transition
 */
public class ThrowExceptionResolver<T, U> implements MultipleTransitionsTriggeredResolver<T, U> {

    @Override
    public Transition<T, U> resolve(T entity, Optional<?> event, Collection<Transition<T, U>> transitions) {
        throw new IllegalStateException(String.format("multiple transitions triggered: " +
                "entity=[%s], transitions=[%s]", entity, transitions));
    }
}
