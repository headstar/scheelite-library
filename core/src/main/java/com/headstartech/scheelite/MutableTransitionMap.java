package com.headstartech.scheelite;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Mutable implementation of the {@link TransitionMap} interface.
 */
class MutableTransitionMap <T, U> extends AbstractTransitionMap<T, U> {

    private final Set<Transition<T, U>> transitions;
    private final Set<InitialTransition<T, U>> initialTransitions;

    MutableTransitionMap() {
        transitions = Sets.newHashSet();
        initialTransitions = Sets.newHashSet();
    }

    void addTransition(Transition<T, U> transition) {
        checkNotNull(transition);
        transitions.add(transition);
    }

    void addInitialTransition(InitialTransition<T, U> initialTransition) {
        checkNotNull(initialTransition);
        initialTransitions.add(initialTransition);
    }

    @Override
    protected Multimap<State<T, U>, Transition<T, U>> getTransitionsFromMap() {
        return createTransitionsFromMap(transitions);
    }

    @Override
    protected Map<State<T, U>, InitialTransition<T, U>> getInitialTransitionsFromMap() {
        return createInitialTransitionsFromMap(initialTransitions);
    }

}
