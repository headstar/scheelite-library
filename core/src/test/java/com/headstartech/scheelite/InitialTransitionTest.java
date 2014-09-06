package com.headstartech.scheelite;

import com.google.common.base.Optional;
import com.headstartech.scheelite.test.TestBase;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by per on 04/03/14.
 */
public class InitialTransitionTest extends TestBase {

    @Test
    public void testEquals() {
        // given
        InitialTransition<TestEntity, StateId> it1 = new InitialTransition<TestEntity, StateId>(Optional.<State<TestEntity, StateId>>of(new TestState(StateId.A)),
                new TestState(StateId.B), Optional.<InitialAction<TestEntity>>absent());
        InitialTransition<TestEntity, StateId> it2 = new InitialTransition<TestEntity, StateId>(Optional.<State<TestEntity, StateId>>of(new TestState(StateId.A)),
                new TestState(StateId.B), Optional.<InitialAction<TestEntity>>absent());

        // when
        boolean equalsRes = it1.equals(it2);
        boolean hashCodeRes = it1.hashCode() == it2.hashCode();

        // then
        assertEquals(true, equalsRes);
        assertEquals(true, hashCodeRes);
    }

    @Test
    public void testNotEquals() {
        // given
        InitialTransition<TestEntity, StateId> it1 = new InitialTransition<TestEntity, StateId>(Optional.<State<TestEntity, StateId>>of(new TestState(StateId.A)),
                new TestState(StateId.B), Optional.<InitialAction<TestEntity>>absent());
        InitialTransition<TestEntity, StateId> it2 = new InitialTransition<TestEntity, StateId>(Optional.<State<TestEntity, StateId>>of(new TestState(StateId.C)),
                new TestState(StateId.B), Optional.<InitialAction<TestEntity>>absent());

        // when
        boolean equalsRes = it1.equals(it2);

        // then
        assertEquals(false, equalsRes);
    }

}
