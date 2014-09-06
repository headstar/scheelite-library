package com.headstartech.scheelite;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Immutable implementation of the {@link StateTree} interface.
 */
class ImmutableStateTree<T, U> extends AbstractStateTree<T, U> {

    private final Map<State<T, U>, State<T, U>> map;

    public ImmutableStateTree(MutableStateTree<T, U> stateTree) {
        // TODO: use Collections immutable map
        this.map = Maps.newHashMap(stateTree.getMap());
    }

    @Override
    protected Map<State<T, U>, State<T, U>> getMap() {
        return map;
    }
}
