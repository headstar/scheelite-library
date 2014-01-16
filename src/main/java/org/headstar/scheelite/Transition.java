package org.headstar.scheelite;

import com.google.common.base.Optional;

public interface Transition<T, U> {

    StateIdentifier getInputStateId();
    StateIdentifier getOutputStateId();
    Optional<Action<T, U>> getAction();
    Guard<T, U>  getGuard();


}
