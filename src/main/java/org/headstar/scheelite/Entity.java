package org.headstar.scheelite;

public interface Entity<U> {

    String getEntityId();

    U getStateId();
    void setStateId(U id);

}
