package org.headstar.scheelite;

import com.google.common.base.Optional;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class StateMachineBuilderTest extends TestBase {

    @Test(expectedExceptions = IllegalStateException.class)
    public void testNoStartState() {
        // given

        // when
        builder.build();

        // then ...exception should be thrown
    }

    @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "entity mutator cannot be null.*")
    public void testEntityMutatorNull() {
        // given

        // when
        builder.withEntityMutator(null);

        // then ...exception should be thrown
    }

    @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "state cannot be null.*")
    public void testStartStateNull() {
        // given

        // when
        builder.withStartState(null);

        // then ...exception should be thrown
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "state identifier cannot be null.*")
    public void testNullStartStateId() {
        // given

        // when
        builder.withStartState(new TestState(null));

        // then ...exception should be thrown
    }

    @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "state cannot be null.*")
    public void testOtherStateNull() {
        // given

        // when
        builder.withStartState(new TestState(StateId.A))
                .withState(null);

        // then ...exception should be thrown
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "state identifier cannot be null.*")
    public void testNullOtherStateId() {
        // given

        // when
        builder.withStartState(new TestState(StateId.A))
                .withState(new TestState(null));

        // then ...exception should be thrown
    }

    @DataProvider(name = "transitionNulls")
    public Object[][] transitionNulls() {
        return new Object[][]{
                {
                        new TestTransition(null, StateId.B),
                },
                {
                        new TestTransition(StateId.A, null),
                },
                {
                        new TestTransition(StateId.A, StateId.B, null, new TestGuard()),
                },
                {
                        new TestTransition(StateId.A, StateId.B, Optional.of(new TestAction()), null),
                },

        };
    }

    @Test(dataProvider = "transitionNulls", expectedExceptions = IllegalStateException.class,
            expectedExceptionsMessageRegExp = "transition.*")
    public void testTransitionFieldsNull(TestTransition transition) {
        // given

        // when
        builder.withTransition(transition);

        // then ...exception should be thrown
    }


    @Test(expectedExceptions = IllegalStateException.class, expectedExceptionsMessageRegExp = "states unreachable.*")
    public void testUnreachableState1() {
        // given

        // when
        builder .withEntityMutator(new TestEntity())
                .withStartState(new TestState(StateId.A))
                .withState(new TestState(StateId.B))
                .build();

        // then ...exception should be thrown
    }

    @Test(expectedExceptions = IllegalStateException.class, expectedExceptionsMessageRegExp = "states unreachable.*")
    public void testUnreachableState2() {
        // given

        // when
        builder .withEntityMutator(new TestEntity())
                .withStartState(new TestState(StateId.A))
                .withState(new TestState(StateId.B))
                .withState(new TestState(StateId.C))  // not reachable
                .withTransition(new TestTransition(StateId.A, StateId.B))
                .build();

        // then ...exception should be thrown
    }

    @Test(expectedExceptions = IllegalStateException.class, expectedExceptionsMessageRegExp = "transition fromState unknown.*")
    public void testTransitionFromUnknownState() {
        // given

        // when
        builder .withEntityMutator(new TestEntity())
                .withStartState(new TestState(StateId.A))
                .withTransition(new TestTransition(StateId.C, StateId.A))
                .build();

        // then ...no exception should be thrown
    }

    @Test(expectedExceptions = IllegalStateException.class, expectedExceptionsMessageRegExp = "transition toState unknown.*")
    public void testTransitionToUnknownState() {
        // given

        // when
        builder .withEntityMutator(new TestEntity())
                .withStartState(new TestState(StateId.A))
                .withTransition(new TestTransition(StateId.A, StateId.B))
                .build();

        // then ...no exception should be thrown
    }

    @Test
    public void testStartOnly() {
        // given

        // when
        builder .withEntityMutator(new TestEntity())
                .withStartState(new TestState(StateId.A))
                .build();

        // then ...no exception should be thrown
    }

    @Test
    public void testTwoStates() {
        // given

        // when
        builder .withEntityMutator(new TestEntity())
                .withStartState(new TestState(StateId.A))
                .withState(new TestState(StateId.B))
                .withTransition(new TestTransition(StateId.A, StateId.B))
                .withTransition(new TestTransition(StateId.B, StateId.A))
                .build();

        // then ...no exception should be thrown
    }


}
