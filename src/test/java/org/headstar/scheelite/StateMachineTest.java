package org.headstar.scheelite;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by Per on 2014-01-16.
 */
public class StateMachineTest extends TestBase {

    @Test
    public void testTwoStates() {
        // given
        Entity e = new Entity();
        StateMachine<Entity, Object> stateMachine = builder
                .addStartState(new TestState(STATE.A))
                .addState(new TestState(STATE.B))
                .addTransition(new TestTransition(STATE.A, STATE.B))
                .build();

        // when
        Object newStateId = stateMachine.process(STATE.A, e, new Object(), new TestEventX());

        // then
        assertEquals(STATE.B, newStateId);
    }

}
