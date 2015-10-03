package com.headstartech.scheelite;

import com.google.common.base.Optional;

/**
 * Created by per on 9/6/15.
 */
class CompositeStateCompletedGuard<T, U> implements Guard<T> {

    private final U finalStateId;

    public CompositeStateCompletedGuard(U finalStateId) {
        this.finalStateId = finalStateId;
    }

    @Override
    public boolean evaluate(T context, Optional<?> event) throws Exception {
        if(event.isPresent() && event.get() instanceof CompositeStateCompleted) {
            CompositeStateCompleted<U> stateCompleted = (CompositeStateCompleted<U>) event.get();
            if(stateCompleted.getFinalStateId().equals(finalStateId)) {
                return true;
            }
         }
        return false;
    }
}
