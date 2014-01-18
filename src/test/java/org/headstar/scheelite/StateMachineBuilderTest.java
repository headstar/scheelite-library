package org.headstar.scheelite;

import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
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
    public void testEntityMutatorNull() {
        // given

        // when
        builder.withEntityMutator(null);

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

    @Test(expectedExceptions = IllegalStateException.class)
    public void testStartStateAndOtherStateEquals() {
        // given
        TestState a1 = new TestState(StateId.A);
        TestState a2 = new TestState(StateId.A);
        assertEquals(a1, a2);

        // when
        builder .withEntityMutator(new TestEntity())
                .withState(a2)
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
        builder .withEntityMutator(new TestEntity())
                .withStartState(a1)
                .withState(a2);

        // then ...exception should be thrown
    }


    @Test(expectedExceptions = IllegalStateException.class)
    public void testUnreachableState1() {
        // given

        // when
        builder .withEntityMutator(new TestEntity())
                .withStartState(new TestState(StateId.A))
                .withState(new TestState(StateId.B))
                .build();

        // then ...exception should be thrown
    }

    @Test(expectedExceptions = IllegalStateException.class)
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

    @Test(expectedExceptions = IllegalStateException.class)
    public void testTransitionFromUnknownState() {
        // given

        // when
        builder .withEntityMutator(new TestEntity())
                .withStartState(new TestState(StateId.A))
                .withTransition(new TestTransition(StateId.C, StateId.A))
                .build();

        // then ...no exception should be thrown
    }

    @Test(expectedExceptions = IllegalStateException.class)
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
