package org.headstar.scheelite;

import com.google.common.base.Optional;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by per on 19/02/14.
 */
abstract class AbstractTransitionMap<T, U> implements TransitionMap<T, U> {

    protected abstract Multimap<State<T, U>, Transition<T, U>> getTransitionsFromMap();
    protected abstract Map<State<T, U>, InitialTransition<T, U>> getInitialTransitionsFromMap();

    @Override
    public Collection<Transition<T, U>> getTransitionsFromState(State<T, U> state) {
        checkNotNull(state);
        return getTransitionsFromMap().get(state);
    }

    @Override
    public Optional<InitialTransition<T, U>> getInitialTransitionFromState(State<T, U> state) {
        checkNotNull(state);
        return Optional.fromNullable(getInitialTransitionsFromMap().get(state));
    }

    protected Multimap<State<T, U>, Transition<T, U>> createTransitionsFromMap(Set<Transition<T, U>> transitions) {
        Multimap<State<T, U>, Transition<T, U>> map = ArrayListMultimap.create();
        for (Transition<T, U> transition : transitions) {
            map.put(transition.getFromState(), transition);
        }
        return map;
    }

    protected Map<State<T, U>, InitialTransition<T, U>> createDefaultTransitionsFromMap(Set<InitialTransition<T, U>> transitions) {
        Map<State<T, U>, InitialTransition<T, U>> map = Maps.newHashMap();
        for (InitialTransition<T, U> transition : transitions) {
            if (transition.getFromState().isPresent()) {
                map.put(transition.getFromState().get(), transition);
            }
        }
        return map;
    }

    protected InitialTransition<T, U> getInitialTransitionFromRoot(Set<InitialTransition<T, U>> transitions) {
        for (InitialTransition<T, U> transition : transitions) {
            if (!transition.getFromState().isPresent()) {
                return transition;
            }
        }
        throw new IllegalStateException("no initial transition found");
    }
}
