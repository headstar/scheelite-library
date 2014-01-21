package org.headstar.scheelite;

public interface Entity<U> {

    Object getId();

    U getStateId();
    void setStateId(U id);

}
