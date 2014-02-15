package org.headstar.scheelite;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Created by per on 15/02/14.
 */
public class ImmutableStateTree<T, U> extends StateTree<T, U> {

    private final Map<State<T, U>, State<T, U>> map;

    public ImmutableStateTree(Map<State<T, U>, State<T, U>> map) {
        this.map = Maps.newHashMap(map);
    }

    @Override
    protected Map<State<T, U>, State<T, U>> getMap() {
        return map;
    }
}
