package org.headstar.scheelite;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableCollection;

import java.util.Collection;

public interface State<T, U> {

    U getId();

    Optional<? extends State<T, U>> getSuperState();

    ImmutableCollection<? extends State<T, U>> getSubStates();

    void onEntry(T entity);

    void onExit(T entity);

    void onEvent(T entity, Object event);
}
