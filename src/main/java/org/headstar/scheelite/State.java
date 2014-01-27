package org.headstar.scheelite;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableCollection;

import java.util.Collection;

public interface State<T, U> {

    U getId();

    Optional<U> getSuperState();

    Optional<InitialTransition<T, U>> getInitialTransition();

    void onEntry(T entity);

    void onExit(T entity);

    boolean onEvent(T entity, Object event);
}
