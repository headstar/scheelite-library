package com.headstartech.scheelite.test;

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

    @SuppressWarnings("unchecked")
    @Test(expectedExceptions = IllegalStateException.class)
    public void testStartStateChild() {
        // given
        TestState a = new TestState(StateId.A);
        TestState b = new TestState(StateId.B);

        // when
        builder.withInitialTransition(b)
                .withCompositeState(a, b)
                .build();

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
    public void testMaxTransitionsInvalid() {
        // given

        // when
        builder.withMaxTransitions(0);

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
    public void testWithTriggerEventClass() {
        // given
        TestState a = new TestState(StateId.A);
        TestState b = new TestState(StateId.B);

        // when
        builder.withInitialTransition(a)
                .withTransition(a, b, TestEventX.class)
                .build();

        // then ... no exception should be thrown

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

    @SuppressWarnings("unchecked")
    @Test
    public void testCompositeStatesNoTransitionToSuperState() {
        // given
        TestState a = new TestState(StateId.A);
        TestState b = new TestState(StateId.B);
        TestState c = new TestState(StateId.C);
        TestState d = new TestState(StateId.D);

        // when
        builder.withInitialTransition(a)
                .withCompositeState(b, c, d)
                .withTransition(a, d)
                .withTransition(d, c)
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

    @SuppressWarnings("unchecked")
    @Test(expectedExceptions = IllegalStateException.class)
    public void testSubStateOfXAlreadySuperStateOfX_1() {
        // given
        TestState a = new TestState(StateId.A);
        TestState b = new TestState(StateId.B);
        TestState c = new TestState(StateId.C);
        TestState d = new TestState(StateId.D);

        // when
        builder.withCompositeState(a, b, c)
               .withCompositeState(c, d, a);

        // then ...exception should be thrown
    }

    @SuppressWarnings("unchecked")
    @Test(expectedExceptions = IllegalStateException.class)
    public void testSubStateOfXAlreadySuperStateOfX_2() {
        // given
        TestState a = new TestState(StateId.A);
        TestState b = new TestState(StateId.B);
        TestState c = new TestState(StateId.C);
        TestState d = new TestState(StateId.D);

        // when
        builder.withCompositeState(a, b, c)
                .withCompositeState(c, a, d);

        // then ...exception should be thrown
    }

    @SuppressWarnings("unchecked")
    @Test(expectedExceptions = IllegalStateException.class)
    public void testSubStateEqualsToSuperState() {
        // given
        TestState a = new TestState(StateId.A);
        TestState b = new TestState(StateId.B);

        // when
        builder.withCompositeState(a, b, a);

        // then ...exception should be thrown
    }

}
