package org.headstar.scheelite;

import com.google.common.base.Optional;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by Per on 2014-01-16.
 */
public class StateMachineTest extends TestBase {

    @Test
    public void testGuardAccept() {
        // given
        Entity e = new Entity();
        StateMachine<Entity> stateMachine = builder
                .addStartState(new TestState(STATE.A))
                .addState(new TestState(STATE.B))
                .addTransition(new TestTransition(STATE.A, STATE.B, new TestGuard(true)))
                .build();

        // when
        Object newStateId = stateMachine.process(STATE.A, e, new TestEventX());

        // then
        assertEquals(newStateId, STATE.B);
    }

    @Test
    public void testGuardDeny() {
        // given
        Entity e = new Entity();
        StateMachine<Entity> stateMachine = builder
                .addStartState(new TestState(STATE.A))
                .addState(new TestState(STATE.B))
                .addTransition(new TestTransition(STATE.A, STATE.B, new TestGuard(false)))
                .build();

        // when
        Object newStateId = stateMachine.process(STATE.A, e, new TestEventX());

        // then
        assertEquals(newStateId, STATE.A);
    }

}
