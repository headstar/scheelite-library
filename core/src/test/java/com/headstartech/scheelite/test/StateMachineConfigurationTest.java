package com.headstartech.scheelite.test;

import com.headstartech.scheelite.*;
import org.testng.annotations.Test;

import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Created by per on 12/6/14.
 */
public class StateMachineConfigurationTest extends TestBase {

    @SuppressWarnings("unchecked")
    @Test
    public void testConfiguration() {

        // given
        TestState a = new TestState(StateId.A);
        TestState b = new TestState(StateId.B);
        TestState c = new TestState(StateId.C);
        TestState d = new TestState(StateId.D);

        TestGuard testGuard = new TestGuard();
        TestAction testAction = new TestAction();

        // when
        StateMachine<TestEntity, StateId> sm = builder.withInitialTransition(a)
                .withCompositeState(b, c, d)
                .withTransition(a, b)
                .withTransition(c, d)
                .withLocalTransition(d, b, TestEventX.class, testGuard, testAction)
                .build();
        StateMachineConfiguration<TestEntity, StateId> conf = sm.getConfiguration();

        // then
        assertNotNull(conf);
        Set<State<TestEntity, StateId>> states = conf.getStates();
        assertEquals(4, states.size());
        assertTrue(states.contains(a));
        assertTrue(states.contains(b));
        assertTrue(states.contains(c));
        assertTrue(states.contains(d));
        assertEquals(conf.getSuperState(a), conf.getRootState());
        assertEquals(conf.getSuperState(b), conf.getRootState());
        assertEquals(conf.getSuperState(c), b);
        assertEquals(conf.getSuperState(d), b);

        Set<Transition<TestEntity, StateId>> transitions = conf.getTransitions();
        assertEquals(5, transitions.size());
        assertTrue(transitions.contains(new Transition<TestEntity, StateId>(conf.getRootState(), a, TransitionType.INITIAL, null, null, null)));
        assertTrue(transitions.contains(new Transition<TestEntity, StateId>(b, c, TransitionType.INITIAL, null, null, null)));
        assertTrue(transitions.contains(new Transition<TestEntity, StateId>(a, b, TransitionType.EXTERNAL, null, null, null)));
        assertTrue(transitions.contains(new Transition<TestEntity, StateId>(c, d, TransitionType.EXTERNAL, null, null, null)));
        assertTrue(transitions.contains(new Transition<TestEntity, StateId>(d, b, TransitionType.LOCAL, TestEventX.class, testGuard, testAction)));
    }
}
