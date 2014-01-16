package org.headstar.scheelite;

import com.google.common.base.Optional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class StateMachineBuilderTest extends TestBase {

    @Test(expectedExceptions = IllegalStateException.class, expectedExceptionsMessageRegExp = "no start state.*")
    public void testNoStartState() {
        // given

        // when
        builder.build();

        // then ...exception should be thrown
    }

    @Test(expectedExceptions = IllegalStateException.class, expectedExceptionsMessageRegExp = "state identifier cannot be null.*")
    public void testNullStartStateId() {
        // given

        // when
        builder.addStartState(new TestState(null))
                .build();

        // then ...exception should be thrown
    }

    @Test(expectedExceptions = IllegalStateException.class, expectedExceptionsMessageRegExp = "state identifier cannot be null.*")
    public void testNullOtherStateId() {
        // given

        // when
        builder.addStartState(new TestState(STATE.A))
                .addState(new TestState(null))
                .build();

        // then ...exception should be thrown
    }

    @DataProvider(name = "transitionNulls")
    public Object[][] transitionNulls(){
        return new Object[][]{
                {
                new TestTransition(null, STATE.B),
        },
                {
                        new TestTransition(STATE.A, null),
                },
                {
                        new TestTransition(STATE.A, STATE.B, null, new TestGuard()) ,
                },
                {
                        new TestTransition(STATE.A, STATE.B, Optional.of(new TestAction()), null),
                },

        };
    }

    @Test(dataProvider="transitionNulls", expectedExceptions = IllegalStateException.class,
            expectedExceptionsMessageRegExp = "transition.*")
    public void testTransitionFieldsNull(Transition transition) {
        // given

        // when
        builder.addStartState(new TestState(STATE.A))
                .addState(new TestState(STATE.B))
                .addTransition(transition)
                .build();

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
                .addState(new TestState(STATE.C))  // not reachable
                .addTransition(new TestTransition(STATE.A, STATE.B))
                .build();

        // then ...exception should be thrown
    }

    @Test(expectedExceptions = IllegalStateException.class, expectedExceptionsMessageRegExp = "transition fromState unknown.*")
    public void testTransitionFromUnknownState() {
        // given

        // when
        builder.addStartState(new TestState(STATE.A))
                .addTransition(new TestTransition(STATE.C, STATE.A))
                .build();

        // then ...no exception should be thrown
    }

    @Test(expectedExceptions = IllegalStateException.class, expectedExceptionsMessageRegExp = "transition toState unknown.*")
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

    @Test
    public void testTwoStates() {
        // given

        // when
        builder.addStartState(new TestState(STATE.A))
                .addState(new TestState(STATE.B))
                .addTransition(new TestTransition(STATE.A, STATE.B))
                .addTransition(new TestTransition(STATE.B, STATE.A))
                .build();

        // then ...no exception should be thrown
    }


}
