package org.headstar.scheelite;

public interface EntityMutator<T, U> {

    U getStateIdentifier(T entity);

    void setStateIdentifier(T entity, U identifier);
}
