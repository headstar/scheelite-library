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

    MutableTransitionMap() {
        transitions = Sets.newHashSet();
    }

    void addTransition(Transition<T, U> transition) {
        checkNotNull(transition);
        transitions.add(transition);
    }

    @Override
    protected Multimap<State<T, U>, Transition<T, U>> getTransitionsFromMap() {
        return createTransitionsFromMap(transitions);
    }

    @Override
    protected Map<State<T, U>, Transition<T, U>> getInitialTransitionsFromMap() {
        return createInitialTransitionsFromMap(transitions);
    }

}
