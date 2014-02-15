package org.headstar.scheelite;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * Created by per on 15/02/14.
 */
public class ImmutableStateTree<T, U> extends StateTree<T, U> {

    private final ImmutableMap<State<T, U>, State<T, U>> map;

    public ImmutableStateTree(Map<State<T, U>, State<T, U>> map) {
        this.map = ImmutableMap.copyOf(map);
    }

    @Override
    protected Map<State<T, U>, State<T, U>> getMap() {
        return map;
    }
}
