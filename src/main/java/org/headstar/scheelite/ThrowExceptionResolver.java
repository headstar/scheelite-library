package org.headstar.scheelite;

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

/**
 * Created by Per on 2014-01-17.
 */
public class ThrowExceptionResolver<T extends Entity<U>, U> implements MultipleTransitionsTriggeredResolver<T, U> {

    @Override
    public Transition<T, U> resolve(T entity, Object event, Collection<Transition<T, U>> transitions) {
        List<String> transitionNames = Lists.newArrayList();
        for(Transition<T, U> t : transitions) {
            transitionNames.add(t.getName());
        }
        throw new IllegalStateException(String.format("multiple transitions triggered: " +
                "entity=[%s], transitions=[%s]", entity.getId(), transitionNames));
    }
}
