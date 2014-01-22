package org.headstar.scheelite;

public interface Entity<U> {

    Object getId();

    U getState();
    void setState(U id);

}
