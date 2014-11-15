package com.headstartech.scheelite;

import com.google.common.collect.Maps;

import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Mutable implementation of the {@link StateTree} interface.
 */
class MutableStateTree<T, U> extends AbstractStateTree<T,U> {

    private final Map<State<T, U>, State<T, U>> map;

    public MutableStateTree() {
        this.map = Maps.newHashMap();
        map.put(rootState, null);
    }

    public void addState(State<T, U> state) {
        checkNotNull(state);
        if(!exists(state)) {
            addState(state, rootState);
        }
    }

    public void addState(State<T, U> state, State<T, U> superState) {
        checkNotNull(state);
        checkNotNull(superState);
        map.put(state, superState);
        addState(superState);
    }


    @Override
    protected Map<State<T, U>, State<T, U>> getMap() {
        return map;
    }
}
