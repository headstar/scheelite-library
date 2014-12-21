package com.headstartech.scheelite;

import com.headstartech.scheelite.test.TestBase;
import org.testng.annotations.Test;

/**
 * Created by per on 12/21/14.
 */
public class TransitionTest extends TestBase {

    @Test(expectedExceptions = {IllegalArgumentException.class})
    public void testInitialWithTriggerEventClass() {
        // given
        TestState stateA = new TestState(StateId.A);
        TestState stateB = new TestState(StateId.A);

        // when
        new Transition<TestEntity, StateId>(stateA, stateB, TransitionType.INITIAL, TestEventX.class, null, null);

        // then... exception should be thrown
    }

    @Test(expectedExceptions = {IllegalArgumentException.class})
    public void testInitialWithGuard() {
        // given
        TestState stateA = new TestState(StateId.A);
        TestState stateB = new TestState(StateId.A);

        // when
        new Transition<TestEntity, StateId>(stateA, stateB, TransitionType.INITIAL, null, new TestGuard(), null);

        // then... exception should be thrown
    }
}
