package com.headstartech.scheelite;

import com.google.common.base.Optional;

import java.util.List;

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
    public Transition<T, U> resolve(U stateId, T entity, Optional<?> event, List<Transition<T, U>> transitions) {
        throw new IllegalStateException(String.format("multiple transitions triggered: " +
                "state=[%s], entity=[%s], transitions=[%s]", stateId, entity, transitions));
    }
}
