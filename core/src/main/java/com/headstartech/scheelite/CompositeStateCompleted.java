package com.headstartech.scheelite;

/**
 * Event generated when a composite state is completed.
 */
public final class CompositeStateCompleted<U> {

    final U finalStateId;

    public CompositeStateCompleted(U finalStateId) {
        this.finalStateId = finalStateId;
    }

    public U getFinalStateId() {
        return finalStateId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompositeStateCompleted<?> that = (CompositeStateCompleted<?>) o;

        return !(finalStateId != null ? !finalStateId.equals(that.finalStateId) : that.finalStateId != null);

    }

    @Override
    public int hashCode() {
        return finalStateId != null ? finalStateId.hashCode() : 0;
    }
}
