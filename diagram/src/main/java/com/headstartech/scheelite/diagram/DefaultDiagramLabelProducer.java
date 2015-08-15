package com.headstartech.scheelite.diagram;

import com.headstartech.scheelite.State;

/**
 * Created by per on 8/14/15.
 */
public class DefaultDiagramLabelProducer implements DiagramLabelProducer {

    @Override
    public <T, U> String getLabelForState(State<T, U> state) {
        return state.getId().toString();
    }

    @Override
    public String getLabelForTriggerEvent(Class<?> triggerEventClass) {
        return triggerEventClass.getSimpleName();
    }

}
