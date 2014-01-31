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
        builder.withStartState(null);

        // then ...exception should be thrown
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNullStartStateId() {
        // given

        // when
        builder.withStartState(new TestState(null));

        // then ...exception should be thrown
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testOtherStateNull() {
        // given
        TestState a = new TestState(StateId.A);

        // when
        builder.withStartState(a)
                .withSimpleState(null);

        // then ...exception should be thrown
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNullOtherStateId() {
        // given
        TestState a = new TestState(StateId.A);

        // when
        builder.withStartState(a)
                .withSimpleState(new TestState(null));

        // then ...exception should be thrown
    }

    @Test
    public void testSimpleReachability() {
        // given
        TestState a = new TestState(StateId.A);
        TestState b = new TestState(StateId.B);

        // when
        builder.withStartState(a)
                .withSimpleState(b)
                .withTransition(a, b)
                .build();

        // then ... no exception should be thrown

    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testUnreachableState1() {
        // given
        TestState a = new TestState(StateId.A);
        TestState b = new TestState(StateId.B);

        // when
        builder.withStartState(a)
                .withSimpleState(b)
                .build();

        // then ...exception should be thrown
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testUnreachableState2() {
        // given
        TestState a = new TestState(StateId.A);
        TestState b = new TestState(StateId.B);
        TestState c = new TestState(StateId.C);

        // when
        builder.withStartState(a)
                .withSimpleState(b)
                .withSimpleState(c)  // not reachable
                .withTransition(a, b)
                .build();

        // then ...exception should be thrown
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testTransitionFromUnknownState() {
        // given
        TestState a = new TestState(StateId.A);
        TestState b = new TestState(StateId.B);
        TestState c = new TestState(StateId.C);

        // when
        builder.withStartState(a)
                .withTransition(c, a)
                .build();

        // then ...no exception should be thrown
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testTransitionToUnknownState() {
        // given
        TestState a = new TestState(StateId.A);
        TestState b = new TestState(StateId.B);

        // when
        builder.withStartState(a)
                .withTransition(a, b)
                .build();

        // then ...no exception should be thrown
    }

    @Test
    public void testCompositeStatesWithInitialTransition() {
        // given
        TestState a = new TestState(StateId.A);
        TestState b = new TestState(StateId.B);
        TestState c = new TestState(StateId.C);
        TestState d = new TestState(StateId.D);

        // when
        builder.withStartState(a)
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
        builder.withStartState(new TestState(StateId.A))
                .build();

        // then ...no exception should be thrown
    }




}
