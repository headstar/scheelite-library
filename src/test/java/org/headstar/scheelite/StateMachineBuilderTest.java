package org.headstar.scheelite;

import com.google.common.base.Optional;
import org.testng.annotations.DataProvider;
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

        // when
        builder.withStartState(new TestState(StateId.A))
                .withState(null);

        // then ...exception should be thrown
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNullOtherStateId() {
        // given

        // when
        builder.withStartState(new TestState(StateId.A))
                .withState(new TestState(null));

        // then ...exception should be thrown
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testStartStateAndOtherStateEquals() {
        // given
        TestState a1 = new TestState(StateId.A);
        TestState a2 = new TestState(StateId.A);
        assertEquals(a1, a2);

        // when
        builder .withState(a2)
                .withStartState(a1);

        // then ...exception should be thrown
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testOtherStateAndStartStateAndEquals() {
        // given
        TestState a1 = new TestState(StateId.A);
        TestState a2 = new TestState(StateId.A);
        assertEquals(a1, a2);

        // when
        builder.withStartState(a1)
                .withState(a2);

        // then ...exception should be thrown
    }


    @Test(expectedExceptions = IllegalStateException.class)
    public void testUnreachableState1() {
        // given

        // when
        builder.withStartState(new TestState(StateId.A))
                .withState(new TestState(StateId.B))
                .build();

        // then ...exception should be thrown
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testUnreachableState2() {
        // given

        // when
        builder.withStartState(new TestState(StateId.A))
                .withState(new TestState(StateId.B))
                .withState(new TestState(StateId.C))  // not reachable
                .withTransition(new TestTransition(StateId.A, StateId.B))
                .build();

        // then ...exception should be thrown
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testTransitionFromUnknownState() {
        // given

        // when
        builder.withStartState(new TestState(StateId.A))
                .withTransition(new TestTransition(StateId.C, StateId.A))
                .build();

        // then ...no exception should be thrown
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testTransitionToUnknownState() {
        // given

        // when
        builder.withStartState(new TestState(StateId.A))
                .withTransition(new TestTransition(StateId.A, StateId.B))
                .build();

        // then ...no exception should be thrown
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testCompositeStatesNoInitialTransition() {
        // given

        // when
        builder.withStartState(new TestState(StateId.A))
                .withState(new TestState(StateId.B, StateId.A))
                .build();

        // then ...exception should be thrown
    }

    @Test
    public void testCompositeStatesWithInitialTransition() {
        // given

        // when
        builder.withStartState(new TestState(StateId.A))
                .withState(new TestState(StateId.B, StateId.A))
                .withInitialTransition(new TestInitialTransition(StateId.A, StateId.B))
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

    @Test
    public void testTwoSimpleStates() {
        // given

        // when
        builder.withStartState(new TestState(StateId.A))
                .withState(new TestState(StateId.B))
                .withTransition(new TestTransition(StateId.A, StateId.B))
                .withTransition(new TestTransition(StateId.B, StateId.A))
                .build();

        // then ...no exception should be thrown

    }




}
