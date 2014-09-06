package com.headstartech.scheelite.test;

import com.headstartech.scheelite.StateAdapter;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by per on 04/03/14.
 */
public class StateAdapterTest extends TestBase {

    @Test
    public void testOnEvent() {
        // given
        StateAdapter<TestEntity, StateId> adapter = new StateAdapter<TestEntity, StateId>() {
            @Override
            public StateId getId() {
                return StateId.A;
            }
        };

        // when
        boolean res = adapter.onEvent(new TestEntity(), new Object());

        // then
        assertEquals(false, res);
    }
}
