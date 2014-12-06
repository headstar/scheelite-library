package com.headstartech.scheelite.test;

import com.headstartech.scheelite.State;
import com.headstartech.scheelite.StateMachine;
import com.headstartech.scheelite.StateMachineConfiguration;
import com.headstartech.scheelite.Transition;
import org.testng.annotations.Test;

import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Created by per on 12/6/14.
 */
public class StateMachineConfigurationTest extends TestBase {

    @Test
    public void testConfiguration() {

        // given
        TestState a = new TestState(StateId.A);
        TestState b = new TestState(StateId.B);
        TestState c = new TestState(StateId.C);
        TestState d = new TestState(StateId.D);

        // when
        StateMachine<TestEntity, StateId> sm = builder.withInitialTransition(a)
                .withCompositeState(b, c, d)
                .withTransition(a, b)
                .withTransition(c, d)
                .build();
        StateMachineConfiguration<TestEntity, StateId> conf = sm.getConfiguration();

        // then
        assertNotNull(conf);
        Set<State<TestEntity, StateId>> states = conf.getStates();
        assertEquals(states.size(), 4);
        assertTrue(states.contains(a));
        assertTrue(states.contains(b));
        assertTrue(states.contains(c));
        assertTrue(states.contains(d));

        Set<Transition<TestEntity, StateId>> transitions = conf.getTransitions();
        assertEquals(transitions.size(), 4);
    }
}
