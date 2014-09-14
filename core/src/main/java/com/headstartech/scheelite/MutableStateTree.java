package com.headstartech.scheelite;

import com.google.common.collect.Maps;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Mutable implementation of the {@link StateTree} interface.
 */
class MutableStateTree<T, U> extends AbstractStateTree<T,U> {

    private final Map<State<T, U>, State<T, U>> map;

    public MutableStateTree() {
        this.map = Maps.newHashMap();
    }

    public void addState(State<T, U> state) {
        checkNotNull(state);
        if(!exists(state)) {
            map.put(state, NO_PARENT);
        }
    }

    public void addState(State<T, U> state, State<T, U> parentState) {
        checkNotNull(state);
        checkNotNull(parentState);
        map.put(state, parentState);
    }


    @Override
    protected Map<State<T, U>, State<T, U>> getMap() {
        return map;
    }
}
