package org.headstar.scheelite;

import com.google.common.base.Optional;

/**
 * Created by Per on 2014-01-25.
 */
public abstract class StateAdapter<T, U> implements State<T, U> {

    @Override
    public void onEntry(T entity) {

    }

    @Override
    public void onExit(T entity) {

    }

    @Override
    public void onEvent(T entity, Object event) {

    }
}
