package com.headstartech.scheelite.diagram.plantuml;

import com.google.common.base.Optional;
import com.headstartech.scheelite.FinalState;
import com.headstartech.scheelite.State;
import com.headstartech.scheelite.Transition;
import com.headstartech.scheelite.TransitionType;
import com.headstartech.scheelite.diagram.DiagramLabelProducer;
import com.headstartech.scheelite.diagram.StateTreeVisitor;

import java.io.PrintWriter;

/**
 * Created by per on 8/12/15.
 */
class PlantUMLVisitor<T, U> implements StateTreeVisitor<T, U> {

    private final PrintWriter printWriter;
    private final DiagramLabelProducer diagramLabelProducer;
    int depth = 0;

    public PlantUMLVisitor(PrintWriter printWriter, DiagramLabelProducer diagramLabelProducer) {
        this.printWriter = printWriter;
        this.diagramLabelProducer = diagramLabelProducer;
    }

    @Override
    public void visitStateStart(State<T, U> state) {
        if (depth > 0) {
            printWriter.println();
            printIndentation();
            printWriter.println(buildStateStart(state));
        } else {
            printWriter.println("@startuml");
        }
        depth++;
    }

    @Override
    public void visitTransition(Transition<T, U> t) {
        printIndentation();
        if (t.getTransitionType().equals(TransitionType.INITIAL)) {
            printWriter.println(buildInitialTransition(t.getMainTargetState()));
        } else {
            printWriter.println(buildTransition(t));
        }
    }

    @Override
    public void visitStateEnd(State<T, U> state) {
        depth--;
        printIndentation();
        if (depth > 0) {
            printWriter.println(buildStateEnd());
            if(state instanceof FinalState) {
                printWriter.println(buildFinalTransition(state));
            }
        } else {
            printWriter.println("@enduml");
        }
    }

    private void printIndentation() {
        for(int i=0; i<depth-1; ++i) {
            printWriter.print("    ");
        }
    }

    private <T, U> String buildStateStart(State<T, U> state) {
        return String.format("state %s {", getStateLabel(state));
    }

    private <T, U> String buildStateEnd() {
        return String.format("}");
    }

    private <T, U> String buildTransition(Transition<T, U> transition) {
        return buildTransition(getStateLabel(transition.getMainSourceState()), getStateLabel(transition.getMainTargetState()), transition.getTriggerEventClass());
    }

    private <T, U> String buildInitialTransition(State<T, U> target) {
        return buildTransition("[*]", getStateLabel(target));
    }

    private <T, U> String buildFinalTransition(State<T, U> source) {
        return buildTransition(getStateLabel(source), "[*]");
    }

    private String buildTransition(String source, String target) {
        return buildTransition(source, target, Optional.<Class<?>>absent());
    }

    private String buildTransition(String source, String target, Optional<Class<?>> triggerEventClass) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s --> %s", source, target));
        if(triggerEventClass.isPresent()) {
            sb.append(String.format(" : %s", getEventLabel(triggerEventClass.get())));
        }
        return sb.toString();
    }

    private <T, U> String getStateLabel(State<T, U> state) {
        return diagramLabelProducer.getLabelForState(state);
    }

    private <T, U> String getEventLabel(Class<?> triggerEventClass) {
        return diagramLabelProducer.getLabelForTriggerEvent(triggerEventClass);
    }

}
