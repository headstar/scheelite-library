package com.headstartech.scheelite;

import com.google.common.base.Optional;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This class provides a skeletal implementation of the {@link TransitionMap} interface.
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

    @Override
    public Set<Transition<T, U>> getTransitions() {
        return Sets.newHashSet(getTransitionsFromMap().values());
    }

    @Override
    public Set<InitialTransition<T, U>> getInitialTransitions() {
        return Sets.newHashSet(getInitialTransitionsFromMap().values());
    }

    protected Multimap<State<T, U>, Transition<T, U>> createTransitionsFromMap(Set<Transition<T, U>> transitions) {
        Multimap<State<T, U>, Transition<T, U>> map = ArrayListMultimap.create();
        for (Transition<T, U> transition : transitions) {
            map.put(transition.getFromState(), transition);
        }
        return map;
    }

    protected Map<State<T, U>, InitialTransition<T, U>> createInitialTransitionsFromMap(Set<InitialTransition<T, U>> transitions) {
        Map<State<T, U>, InitialTransition<T, U>> map = Maps.newHashMap();
        for (InitialTransition<T, U> transition : transitions) {
            map.put(transition.getFromState(), transition);
        }
        return map;
    }
}
