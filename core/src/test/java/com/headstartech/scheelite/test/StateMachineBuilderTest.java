package com.headstartech.scheelite.test;

import com.headstartech.scheelite.FinalState;
import org.testng.annotations.Test;

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

    @Test
    public void testTransitionToFinalState() {
        // given
        TestState a = new TestState(StateId.A);
        TestState b = new TestState(StateId.B);
        FinalState c = new TestFinalState(StateId.C);

        // when
        builder.withInitialTransition(a)
                .withTransition(a, b)
                .withTransition(b, c)
                .build();

        // when
        builder.build();

        // then ...no exception should be thrown
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testTransitionFromFinalState() {
        // given
        TestState a = new TestState(StateId.A);
        TestState b = new TestState(StateId.B);
        FinalState c = new TestFinalState(StateId.C);

        // when
        builder.withInitialTransition(a)
                .withTransition(a, b)
                .withTransition(b, c)
                .withTransition(c, a)
                .build();

        // when
        builder.build();

        // then ...exception should be thrown
    }

    @SuppressWarnings("unchecked")
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testFinalStateAsSuperState() {
        // given
        FinalState a = new TestFinalState(StateId.A);
        TestState b = new TestState(StateId.B);

        // when
        builder.withCompositeState(a, b);

        // then ...exception should be thrown
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNonCompositeStateWithCompletionTransition() {

        // given
        FinalState a = new TestFinalState(StateId.A);
        TestState b = new TestState(StateId.B);

        // when
        builder.withInitialTransition(a)
                .withCompositeStateCompletedTransition(a, b);

        // then ...exception should be thrown
    }

    @Test
    public void testCompositeStateWithCompletionTransition() {

        // given
        TestState a = new TestState(StateId.A);
        FinalState b = new TestFinalState(StateId.B);
        TestState c = new TestState(StateId.C);

        // when
        builder.withInitialTransition(a)
                .withCompositeState(a, b)
                .withCompositeStateCompletedTransition(b, c)
                .build();

        // then
    }

}
