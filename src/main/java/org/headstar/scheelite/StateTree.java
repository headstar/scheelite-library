package org.headstar.scheelite;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by per on 15/02/14.
 */
public abstract class StateTree<T, U>  {

    protected final State<T, U> NO_PARENT = null;
    private final Optional<State<T, U>> ROOT_STATE = Optional.absent();

    protected abstract Map<State<T, U>, State<T, U>> getMap();

    public Set<State<T, U>> getStates() {
        return getMap().keySet();
    }

    public Optional<State<T, U>> getState(U id) {
        checkNotNull(id);
        for(State<T, U> state : getStates()) {
            if(state.getId().equals(id)) {
                return Optional.of(state);
            }
        }
        return Optional.absent();
    }

    public boolean exists(State<T, U> a) {
        checkNotNull(a);
        Map<State<T, U>, State<T, U>> map = getMap();
        return map.containsKey(a);
    }

    public boolean isChild(State<T, U> a) {
        checkNotNull(a);
        return getParent(a).isPresent();
    }

    public Optional<State<T,U>> getParent(State<T, U> a) {
        checkNotNull(a);
        Map<State<T, U>, State<T, U>> map = getMap();
        State<T, U> value = map.get(a);
        return Optional.fromNullable(value);
    }

    public boolean isChildOf(State<T, U> a, State<T, U> b) {
        checkNotNull(a);
        checkNotNull(b);
        Map<State<T, U>, State<T, U>> map = getMap();
        State<T, U> value = map.get(a);
        return value != null && value.equals(b);
    }

    public boolean isParentOf(State<T, U> a, State<T, U> b) {
        checkNotNull(a);
        checkNotNull(b);
        return isChildOf(b, a);
    }

    public boolean isAncestorOf(State<T, U> a, State<T, U> b) {
        checkNotNull(a);
        checkNotNull(b);
        List<State<T, U>> bToRoot = getPathToRootState(b);
        return bToRoot.contains(a);
    }

    public boolean isDescendantOf(State<T, U> a, State<T, U> b) {
        checkNotNull(a);
        checkNotNull(b);
        List<State<T, U>> aToRoot = getPathToRootState(a);
        return aToRoot.contains(b);
    }


    public Optional<State<T, U>> getLowestCommonAncestor(State<T, U> a, State<T, U> b) {
        checkNotNull(a);
        checkNotNull(b);
        List<State<T, U>> aToRoot = getPathToRootState(a);
        List<State<T, U>> bToRoot = getPathToRootState(b);

        Optional<State<T, U>> result = ROOT_STATE;
        Iterator<State<T, U>> iter = bToRoot.iterator();
        while (iter.hasNext()) {
            State<T, U> nextState = iter.next();
            if (aToRoot.contains(nextState)) {
                result = Optional.of(nextState);
            }
        }
        return result;
    }

    public List<State<T, U>> getPathBetween(State<T, U> a, Optional<State<T, U>> bOpt) {
        checkNotNull(a);
        checkNotNull(bOpt);

        List<State<T, U>> aToRoot = getPathToRootState(a);
        if(bOpt.equals(ROOT_STATE)) {
            return aToRoot;
        } else {
            State<T, U> b = bOpt.get();
            int index = aToRoot.indexOf(b);
            if(index == -1) {
                throw new IllegalArgumentException(String.format("b not an ancestor of a: a=%s, b=%s", a, b));
            } else {
                List<State<T, U>> res = aToRoot.subList(0, index + 1);
                return res;
            }
        }
    }

    public List<State<T, U>> getPathToRootState(State<T, U> state) {
        checkNotNull(state);

        List<State<T, U>> res = Lists.newArrayList();
        Optional<State<T, U>> stateOpt = Optional.of(state);
        do {
            State<T, U> s = stateOpt.get();
            res.add(s);
            stateOpt = getParent(s);
        } while (!stateOpt.equals(ROOT_STATE));
        return res;
    }

}
