package com.headstartech.scheelite;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This class provides a skeletal implementation of the {@link StateTree} interface.
 */
abstract class AbstractStateTree<T, U> implements StateTree<T, U> {

    protected abstract Map<State<T, U>, State<T, U>> getMap();
    protected final State<T, U> rootState = new RootState<T, U>();

    @Override
    public Set<State<T, U>> getStates() {
        Set<State<T, U>> res = Sets.newHashSet(getMap().keySet());
        res.remove(rootState);
        return res;
    }

    @Override
    public Optional<State<T, U>> getState(U id) {
        for(State<T, U> state : getStates()) {
            if(state.getId().equals(id)) {
                return Optional.of(state);
            }
        }
        return Optional.absent();
    }

    @Override
    public boolean exists(State<T, U> a) {
        checkNotNull(a);
        Map<State<T, U>, State<T, U>> map = getMap();
        return map.containsKey(a);
    }

    @Override
    public Optional<State<T,U>> getParent(State<T, U> a) {
        checkNotNull(a);
        Map<State<T, U>, State<T, U>> map = getMap();
        State<T, U> value = map.get(a);
        return Optional.fromNullable(value);
    }

    @Override
    public boolean isParent(State<T, U> a) {
        checkNotNull(a);
        Map<State<T, U>, State<T, U>> map = getMap();
        return map.values().contains(a);
    }

    @Override
    public boolean isAncestorOf(State<T, U> a, State<T, U> b) {
        checkNotNull(a);
        checkNotNull(b);
        List<State<T, U>> ancestorsOfB = getAncestors(b);
        return ancestorsOfB.contains(a);
    }

    @Override
    public boolean isDescendantOf(State<T, U> a, State<T, U> b) {
        checkNotNull(a);
        checkNotNull(b);
        List<State<T, U>> ancestorsOfA = getAncestors(a);
        return ancestorsOfA.contains(b);
    }


    @Override
    public State<T, U> getLowestCommonAncestor(State<T, U> a, State<T, U> b) {
        checkNotNull(a);
        checkNotNull(b);
        List<State<T, U>> aToRoot = getPathToAncestor(a, rootState, true);
        List<State<T, U>> bToRoot = getPathToAncestor(b, rootState, true);

        for(State<T, U> bIter : bToRoot) {
            if(aToRoot.contains(bIter)) {
                return bIter;
            }
        }
        return rootState;
    }

    @Override
    public List<State<T, U>> getPathToAncestor(State<T, U> a, State<T, U> b, boolean includeAncestor) {
        checkNotNull(a);
        checkNotNull(b);

        if(a.equals(b)) {
            return Lists.newArrayList();
        }

        List<State<T, U>> res = Lists.newArrayList();
        Optional<State<T, U>> current = Optional.of(a);
        while(true) {
            res.add(current.get());
            Optional<State<T, U>> next = getParent(current.get());
            if(next.isPresent()) {
                if(next.get().equals(b)) {
                    if(includeAncestor) {
                        res.add(b);
                    }
                    return res;
                } else {
                    current = next;
                }
            } else {
                throw new IllegalArgumentException(String.format("b not an ancestor of a: a=%s, b=%s", a, b));
            }
        }
    }

    private List<State<T, U>> getAncestors(State<T, U> state) {
        checkNotNull(state);
        List<State<T, U>> res = Lists.newArrayList();
        State<T, U> current = state;
        do {
            res.add(current);
            Optional<State<T, U>> parent = getParent(current);
            if(parent.isPresent()) {
                current = parent.get();
            } else {
                return res;
            }
        } while (true);
    }

    @Override
    public State<T, U> getRootState() {
        return rootState;
    }
}
