package com.headstartech.scheelite.diagram;

import com.headstartech.scheelite.Guard;
import com.headstartech.scheelite.State;
import com.headstartech.scheelite.diagram.annotations.Diagram;

/**
 * Created by per on 8/14/15.
 */
public class DefaultDiagramLabelProducer implements DiagramLabelProducer {

    @Override
    public <T, U> String getLabelForState(State<T, U> state) {
        Diagram label = state.getClass().getAnnotation(Diagram.class);
        if(label != null) {
            return label.value();
        } else {
            return state.getId().toString();
        }
    }

    @Override
    public String getLabelForTriggerEvent(Class<?> triggerEventClass) {
        Diagram label = triggerEventClass.getAnnotation(Diagram.class);
        if(label != null) {
            return label.value();
        } else {
            return triggerEventClass.getSimpleName();
        }
    }

    @Override
    public String getLabelForGuard(Guard<?> guard) {
        Diagram label = guard.getClass().getAnnotation(Diagram.class);
        if(label != null) {
            return label.value();
        } else {
            return guard.getClass().getSimpleName();
        }
    }
}
