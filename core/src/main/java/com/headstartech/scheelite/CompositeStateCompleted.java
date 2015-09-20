package com.headstartech.scheelite;

/**
 * Event generated when a composite state is completed.
 */
public final class CompositeStateCompleted<U> {

    final U stateId;

    public CompositeStateCompleted(U stateId) {
        this.stateId = stateId;
    }

    public U getStateId() {
        return stateId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompositeStateCompleted<?> that = (CompositeStateCompleted<?>) o;

        return !(stateId != null ? !stateId.equals(that.stateId) : that.stateId != null);

    }

    @Override
    public int hashCode() {
        return stateId != null ? stateId.hashCode() : 0;
    }
}
