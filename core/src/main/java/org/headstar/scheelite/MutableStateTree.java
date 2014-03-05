package org.headstar.scheelite;

import com.google.common.collect.Maps;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Mutable implementation of the {@link org.headstar.scheelite.StateTree} interface.
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
        if(!exists(state)) {
            map.put(state, parentState);
        } else {
           if(isChild(state)) {
               if(!isChildOf(state, parentState)) {
                   throw new IllegalArgumentException(String.format("state already has a parent: state=%s, parentState=%s", state.getId(), parentState.getId()));
               }
           } else {
               map.put(state, parentState);
           }
        }
    }


    @Override
    protected Map<State<T, U>, State<T, U>> getMap() {
        return map;
    }
}
