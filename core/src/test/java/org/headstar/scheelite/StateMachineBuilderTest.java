package org.headstar.scheelite;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class StateMachineBuilderTest extends TestBase {

    @Test(expectedExceptions = IllegalStateException.class)
    public void testNoStartState() {
        // given

        // when
        builder.build();

        // then ...exception should be thrown
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testStartStateNull() {
        // given

        // when
        builder.withInitialTransition(null);

        // then ...exception should be thrown
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNullStartStateId() {
        // given

        // when
        builder.withInitialTransition(new TestState(null));

        // then ...exception should be thrown
    }

    @Test
    public void testSimpleReachability() {
        // given
        TestState a = new TestState(StateId.A);
        TestState b = new TestState(StateId.B);

        // when
        builder.withInitialTransition(a)
                .withTransition(a, b)
                .build();

        // then ... no exception should be thrown

    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testUnreachableState1() {
        // given
        TestState a = new TestState(StateId.A);
        TestState b = new TestState(StateId.B);
        TestState c = new TestState(StateId.C);

        // when
        builder.withInitialTransition(a)
                .withTransition(b, c)
                .build();

        // then ...exception should be thrown
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testUnreachableState2() {
        // given
        TestState a = new TestState(StateId.A);
        TestState b = new TestState(StateId.B);
        TestState c = new TestState(StateId.C);
        TestState d = new TestState(StateId.D);

        // when
        builder.withInitialTransition(a)
                .withTransition(a, b)
                .withTransition(c, d)
                .build();

        // then ...exception should be thrown
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCompositeStatesWithInitialTransition() {
        // given
        TestState a = new TestState(StateId.A);
        TestState b = new TestState(StateId.B);
        TestState c = new TestState(StateId.C);
        TestState d = new TestState(StateId.D);

        // when
        builder.withInitialTransition(a)
                .withCompositeState(b, c, d)
                .withTransition(a, b)
                .withTransition(c, d)
                .build();

        // then ... no exception should be thrown
    }


    @Test
    public void testStartOnly() {
        // given

        // when
        builder.withInitialTransition(new TestState(StateId.A))
                .build();

        // then ...no exception should be thrown
    }
}
