package com.headstartech.scheelite;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import java.util.Map;

/**
 * Immutable implementation of the {@link TransitionMap} interface.
 */
class ImmutableTransitionMap<T, U> extends AbstractTransitionMap<T, U> {

    private final ImmutableMultimap<State<T, U>, Transition<T, U>> transitions;
    private final ImmutableMap<State<T, U>, InitialTransition<T, U>> defaultTransitions;
    private final InitialTransition<T, U> initialTransition;

    public ImmutableTransitionMap(MutableTransitionMap<T, U> transitionMap) {
        this.transitions = ImmutableMultimap.copyOf(transitionMap.getTransitionsFromMap());
        this.defaultTransitions = ImmutableMap.copyOf(transitionMap.getInitialTransitionsFromMap());
        this.initialTransition = transitionMap.getInitialTransitionFromRoot();
    }


    @Override
    protected Multimap<State<T, U>, Transition<T, U>> getTransitionsFromMap() {
        return transitions;
    }

    @Override
    protected Map<State<T, U>, InitialTransition<T, U>> getInitialTransitionsFromMap() {
        return defaultTransitions;
    }

    @Override
    public InitialTransition<T, U> getInitialTransitionFromRoot() {
        return initialTransition;
    }
}
