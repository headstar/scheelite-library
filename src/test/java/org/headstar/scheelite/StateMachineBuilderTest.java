package org.headstar.scheelite;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Observable;

public class StateMachineBuilderTest {

    enum STATE {A, B, C, D, E}

    StateMachineBuilder<Entity, Object> builder;

    @BeforeMethod
    public void setup() {
        builder = new StateMachineBuilder<Entity, Object>();
    }

    @Test(expectedExceptions = IllegalStateException.class, expectedExceptionsMessageRegExp = "no start state.*")
    public void testNoStartState() {
        // given

        // when
        builder.build();

        // then ...exception should be thrown
    }

    @Test(expectedExceptions = IllegalStateException.class, expectedExceptionsMessageRegExp = "states unreachable.*")
    public void testUnreachableState1() {
        // given

        // when
        builder.addStartState(new TestState(STATE.A))
                .addState(new TestState(STATE.B))
                .build();

        // then ...exception should be thrown
    }

    @Test(expectedExceptions = IllegalStateException.class, expectedExceptionsMessageRegExp = "states unreachable.*")
    public void testUnreachableState2() {
        // given

        // when
        builder.addStartState(new TestState(STATE.A))
                .addState(new TestState(STATE.B))
                .addState(new TestState(STATE.C))
                .build();

        // then ...exception should be thrown
    }

    @Test(expectedExceptions = IllegalStateException.class, expectedExceptionsMessageRegExp = "transition to state unknown.*")
    public void testTransitionToUnknownState() {
        // given

        // when
        builder.addStartState(new TestState(STATE.A))
                .addTransition(new TestTransition(STATE.A, STATE.B))
                .build();

        // then ...no exception should be thrown
    }

    @Test
    public void testStartOnly() {
        // given

        // when
        builder.addStartState(new TestState(STATE.A))
                .build();

        // then ...no exception should be thrown
    }

    private static class Entity {

    }

    private class TestTransition extends TransitionAdapter<Entity, Object> {

        public TestTransition(Object fromState, Object toState) {
            super(fromState, toState);
        }
    }


    private class TestState extends StateAdapter<Entity, Object> {

        private STATE id;

        TestState(STATE id) {
            this.id = id;
        }

        @Override
        public Object getIdentifier() {
            return id;
        }
    }

}
