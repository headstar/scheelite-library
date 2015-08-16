package com.headstartech.scheelite.diagram.plantuml;

import com.headstartech.scheelite.StateMachine;
import com.headstartech.scheelite.StateMachineConfiguration;
import org.testng.annotations.Test;

import java.io.PrintWriter;
import java.util.Arrays;

/**
 * Created by per on 8/12/15.
 */
public class PlanUMLStateDiagramWriterTest extends TestBase {

    @Test
    public void test() {
        // given
        TestBase.StateA a = new TestBase.StateA();
        TestBase.StateB b = new TestBase.StateB();
        TestBase.StateC c = new TestBase.StateC();
        TestBase.StateD d = new TestBase.StateD();
        TestBase.TestFinalState e = new TestBase.TestFinalState();

        TestBase.TestGuard testGuard = new TestBase.TestGuard();
        TestBase.TestAction testAction = new TestBase.TestAction();

        StateMachine<TestEntity, StateId> sm = builder.withInitialTransition(a)
                .withCompositeState(b, c, d)
                .withTransition(a, b, TestBase.TestEventY.class)
                .withTransition(a, e, TestBase.TestEventX.class)
                .withTransition(c, d)
                .withLocalTransition(d, b, TestBase.TestEventX.class, testGuard, testAction)
                .build();
        StateMachineConfiguration<TestEntity, StateId> conf = sm.getConfiguration();

        // when
        PlantUMLStateDiagramWriter diagramWriter = new PlantUMLStateDiagramWriter();
        PrintWriter pw = new PrintWriter(System.out);
        diagramWriter.writeDiagram(conf, pw);
        pw.flush();

    }
}
