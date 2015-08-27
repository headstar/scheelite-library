package com.headstartech.scheelite.diagram;

import com.headstartech.scheelite.*;
import com.headstartech.scheelite.diagram.annotations.Diagram;

import java.util.List;

/**
 * Produces labels for state, trigger events and guards.
 */
public class DefaultDiagramLabelProducer implements DiagramLabelProducer {

    private final boolean triggerEventLabelsEnabled;
    private final boolean guardLabelsEnabled;

    public DefaultDiagramLabelProducer(boolean triggerEventLabelsEnabled, boolean guardLabelsEnabled) {
        this.triggerEventLabelsEnabled = triggerEventLabelsEnabled;
        this.guardLabelsEnabled = guardLabelsEnabled;
    }

    public DefaultDiagramLabelProducer() {
        this(true, true);
    }

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
        if(triggerEventLabelsEnabled) {
            Diagram label = triggerEventClass.getAnnotation(Diagram.class);
            if (label != null) {
                return label.value();
            } else {
                return triggerEventClass.getSimpleName();
            }
        } else {
            return null;
        }
    }

    @Override
    public String getLabelForGuard(Guard<?> guard) {
        if(guardLabelsEnabled) {
            return getLabelForGuard(guard, 0);
        } else {
            return null;
        }
    }

    private String getLabelForGuard(Guard<?> guard, int depth) {
        if(AndGuard.class.isInstance(guard)) {
            List<? extends Guard<?>> components = ((AndGuard<?>) guard).getComponents();
            return joinComponents(components, " && ", depth);
        } else if(OrGuard.class.isInstance(guard)) {
            List<? extends Guard<?>> components = ((OrGuard<?>) guard).getComponents();
            return joinComponents(components, " || ", depth);
        } else if(NotGuard.class.isInstance(guard)) {
            Guard<?> component = ((NotGuard<?>) guard).getComponent();
            return String.format("!%s", getLabelForGuard(component, depth));
        } else {
            Diagram label = guard.getClass().getAnnotation(Diagram.class);
            if (label != null) {
                return label.value();
            } else {
                return guard.getClass().getSimpleName();
            }
        }
    }

    private String joinComponents(List<? extends Guard<?>> components, String joinString, int depth) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for(Guard<?> g : components) {
            if(first) {
                first = false;
            } else {
                sb.append(joinString);
            }
            sb.append(getLabelForGuard(g, depth + 1));
        }
        String result = sb.toString();
        if(depth == 0) {
            return result;
        } else {
            return String.format("(%s)", result);
        }
    }
}
