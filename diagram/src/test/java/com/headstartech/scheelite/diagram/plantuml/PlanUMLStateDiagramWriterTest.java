package com.headstartech.scheelite.diagram.plantuml;

import com.headstartech.scheelite.Guards;
import com.headstartech.scheelite.StateMachine;
import com.headstartech.scheelite.StateMachineConfiguration;
import com.headstartech.scheelite.diagram.DefaultDiagramLabelProducer;
import com.headstartech.scheelite.diagram.DiagramLabelProducer;
import net.sourceforge.plantuml.SourceStringReader;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.testng.Assert.assertNotNull;

/**
 * Created by per on 8/12/15.
 */
public class PlanUMLStateDiagramWriterTest extends TestBase {

    @Test
    public void test() throws IOException {
        // given
        TestBase.StateA a = new TestBase.StateA();
        TestState b = new TestState(StateId.B);
        TestState c = new TestState(StateId.C);
        TestState d = new TestState(StateId.D);
        TestBase.TestFinalState e = new TestBase.TestFinalState(StateId.E);
        TestBase.TestFinalState f = new TestBase.TestFinalState(StateId.F);
        TestState g = new TestState(StateId.G);

        FirstTestGuard firstTestGuard = new FirstTestGuard();
        SecondTestGuard secondTestGuard = new SecondTestGuard();
        TestBase.TestAction testAction = new TestBase.TestAction();

        StateMachine<TestEntity, StateId> sm = builder.withInitialTransition(a)
                .withCompositeState(b, c, d, e)
                .withTransition(a, b, TestBase.TestEventY.class, Guards.and(Guards.not(firstTestGuard),
                        Guards.or(secondTestGuard, firstTestGuard)))
                .withTransition(a, f, TestBase.TestEventX.class)
                .withTransition(c, d)
                .withTransition(d, e)
                .withLocalTransition(d, b, TestBase.TestEventX.class, firstTestGuard, testAction)
                .withCompositeStateCompletedTransition(e, g)
                .build();
        StateMachineConfiguration<TestEntity, StateId> conf = sm.getConfiguration();

        PlantUMLStateDiagramWriter diagramWriter = new PlantUMLStateDiagramWriter();
        DiagramLabelProducer diagramLabelProducer = new DefaultDiagramLabelProducer(true, false);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        diagramWriter.writeDiagram(conf, pw, diagramLabelProducer);
        pw.flush();

        System.out.print(sw.toString());

        SourceStringReader reader = new SourceStringReader(sw.toString());
        final ByteArrayOutputStream os = new ByteArrayOutputStream();

        // when
        String desc = reader.generateImage(os);

        // then
        assertNotNull(desc);
    }
}
