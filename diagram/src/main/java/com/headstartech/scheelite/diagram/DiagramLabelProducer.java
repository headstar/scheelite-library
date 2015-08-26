package com.headstartech.scheelite.diagram;

import com.headstartech.scheelite.Guard;
import com.headstartech.scheelite.State;

/**
 * Created by per on 8/14/15.
 */
public interface DiagramLabelProducer {

    <T, U> String getLabelForState(State<T, U> state);

    String getLabelForTriggerEvent(Class<?> triggerEventClass);

    String getLabelForGuard(Guard<?> guard);
}
