package com.headstartech.scheelite;

import com.google.common.base.Optional;

/**
 * Created by per on 9/6/15.
 */
class CompositeStateCompletedGuard<T, U> implements Guard<T> {

    private final U stateId;

    public CompositeStateCompletedGuard(U stateId) {
        this.stateId = stateId;
    }

    @Override
    public boolean evaluate(T context, Optional<?> event) throws Exception {
        if(event.isPresent() && event.get() instanceof CompositeStateCompleted) {
            CompositeStateCompleted<U> stateCompleted = (CompositeStateCompleted<U>) event.get();
            if(stateCompleted.getStateId().equals(stateId)) {
                return true;
            }
         }
        return false;
    }
}
