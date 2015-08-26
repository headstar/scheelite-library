package com.headstartech.scheelite.diagram;

import com.headstartech.scheelite.State;
import com.headstartech.scheelite.Transition;

/**
 * Created by per on 8/12/15.
 */
public interface StateTreeVisitor<T, U> {

    void visitStateStart(State<T, U> state);

    void visitTransition(Transition<T, U> t);

    void visitStateEnd(State<T, U> state);

}
