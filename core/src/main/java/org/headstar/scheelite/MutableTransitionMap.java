package org.headstar.scheelite;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by per on 20/02/14.
 */
class MutableTransitionMap <T, U> extends AbstractTransitionMap<T, U> {

    private final Set<Transition<T, U>> transitions;
    private final Set<DefaultTransition<T, U>> defaultTransitions;
    private DefaultTransition<T, U> initialTransition;

    MutableTransitionMap() {
        transitions = Sets.newHashSet();
        defaultTransitions = Sets.newHashSet();
    }

    Set<Transition<T, U>> getTransitions() {
        return transitions;
    }

    Set<DefaultTransition<T, U>> getDefaultTransitions() {
        return defaultTransitions;
    }

    void addTransition(Transition<T, U> transition) {
        checkNotNull(transition);
        transitions.add(transition);
    }

    void addDefaultTransition(DefaultTransition<T, U> defaultTransition) {
        checkNotNull(defaultTransition);
        defaultTransitions.add(defaultTransition);
    }

    void setDefaultTransitionFromRoot(DefaultTransition<T, U> defaultTransitionFromRoot) {
        checkNotNull(defaultTransitionFromRoot);
        this.initialTransition = defaultTransitionFromRoot;
    }

    @Override
    protected Multimap<State<T, U>, Transition<T, U>> getTransitionsFromMap() {
        return createTransitionsFromMap(transitions);
    }

    @Override
    protected Map<State<T, U>, DefaultTransition<T, U>> getDefaultTransitionsFromMap() {
        return createDefaultTransitionsFromMap(defaultTransitions);
    }

    @Override
    public DefaultTransition<T, U> getDefaultTransitionFromRoot() {
        return getInitialTransition(defaultTransitions);
    }
}
