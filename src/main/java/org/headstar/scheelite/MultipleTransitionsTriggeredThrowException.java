package org.headstar.scheelite;

import java.util.Collection;

/**
 * Created by Per on 2014-01-17.
 */
public class MultipleTransitionsTriggeredThrowException<T, U> implements MultipleTransitionsTriggeredPolicy<T, U> {

    @Override
    public Transition<T, U> triggeredTransitions(U stateIdentifier, T entity, Object event,
                                                 Collection<Transition<T, U>> transitions) {
        throw new IllegalStateException(String.format("multiple transitions triggered: stateIdentifier=[%s]" +
                ", entity=[%s], event=[%s], transitions=[%s]", stateIdentifier, entity, event, transitions));
    }
}
