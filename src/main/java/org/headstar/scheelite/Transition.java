package org.headstar.scheelite;

import com.google.common.base.Optional;

public interface Transition<T, U> {

    Object getFromState();
    Object getToState();
    Optional<Action<T, U>> getAction();
    Guard<T, U>  getGuard();

}
