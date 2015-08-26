package com.headstartech.scheelite.diagram.plantuml;

import com.headstartech.scheelite.StateMachineConfiguration;
import com.headstartech.scheelite.diagram.DefaultDiagramLabelProducer;
import com.headstartech.scheelite.diagram.DiagramLabelProducer;
import com.headstartech.scheelite.diagram.StateTreeTraverser;
import com.headstartech.scheelite.diagram.StateTreeVisitor;

import java.io.PrintWriter;

/**
 * Writes a PlantUML state diagram description given a {@linkplain com.headstartech.scheelite.StateMachineConfiguration}.
 */
public class PlantUMLStateDiagramWriter {

    public <T, U> void writeDiagram(StateMachineConfiguration<T, U> configuration, PrintWriter writer) {
        writeDiagram(configuration, writer, new DefaultDiagramLabelProducer());
    }


    public <T, U> void writeDiagram(StateMachineConfiguration<T, U> configuration, PrintWriter writer, DiagramLabelProducer labelProducer) {
        StateTreeTraverser<T, U> treeTraverser = new StateTreeTraverser<T, U>(configuration);
        StateTreeVisitor<T, U> visitor = new PlantUMLVisitor<T, U>(writer, labelProducer);
        treeTraverser.accept(visitor);
    }
}
