package org.headstar.scheelite;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

import java.util.Map;
import java.util.Set;

/**
 * Created by per on 19/02/14.
 */
public class ImmutableTransitionMap<T, U> extends AbstractTransitionMap<T, U> {

    private final ImmutableMultimap<State<T, U>, Transition<T, U>> transitions;
    private final ImmutableMap<State<T, U>, DefaultTransition<T, U>> defaultTransitions;
    private final DefaultTransition<T, U> initialTransition;

    public ImmutableTransitionMap(Set<Transition<T, U>> transitions, Set<DefaultTransition<T, U>> defaultTransitions) {
        this.transitions = ImmutableMultimap.copyOf(createTransitionsFromMap(transitions));
        this.defaultTransitions = ImmutableMap.copyOf(createDefaultTransitionsFromMap(defaultTransitions));
        this.initialTransition = getInitialTransition(defaultTransitions);
    }


    @Override
    protected Multimap<State<T, U>, Transition<T, U>> getTransitionsFromMap() {
        return transitions;
    }

    @Override
    protected Map<State<T, U>, DefaultTransition<T, U>> getDefaultTransitionsFromMap() {
        return defaultTransitions;
    }

    @Override
    public DefaultTransition<T, U> getDefaultTransitionFromRoot() {
        return initialTransition;
    }
}
