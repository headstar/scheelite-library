package org.headstar.scheelite;

import com.google.common.base.Optional;

public interface Transition<T, U> {

    U getFromState();

    U getToState();

    Optional<? extends Action<T>> getAction();

    Guard<T> getGuard();

}
