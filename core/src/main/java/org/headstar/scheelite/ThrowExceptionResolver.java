package org.headstar.scheelite;

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

/**
 * Created by Per on 2014-01-17.
 */
public class ThrowExceptionResolver<T, U> implements MultipleTransitionsTriggeredResolver<T, U> {

    @Override
    public Transition<T, U> resolve(T entity, Object event, Collection<Transition<T, U>> transitions) {
        throw new IllegalStateException(String.format("multiple transitions triggered: " +
                "entity=[%s], transitions=[%s]", entity, transitions));
    }
}
