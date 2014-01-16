package org.headstar.scheelite;

import com.google.common.base.Optional;

public interface Transition<T> {

    Object getFromState();
    Object getToState();
    Optional<? extends Action<T>> getAction();
    Guard<T>  getGuard();

}
