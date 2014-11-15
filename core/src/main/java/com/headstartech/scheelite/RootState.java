package com.headstartech.scheelite;

/**
 * Class representing the implicit root state of every state machine.
 */
final class RootState<T, U> extends StateAdapter<T, U> {

    private final U id = null;

    @Override
    public U getId() {
        return id;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RootState)) return false;

        RootState rootState = (RootState) o;

        if (id != null ? !id.equals(rootState.id) : rootState.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
