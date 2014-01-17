package org.headstar.scheelite;

import com.google.common.base.Optional;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

/**
 * Created by Per on 2014-01-16.
 */
public class StateMachineTest extends TestBase {

    @Test
    public void testSimpleTransition() {
        // given
        TestEntity e = spy(new TestEntity(StateId.A));
        TestState a = spy(new TestState(StateId.A));
        TestState b = spy(new TestState(StateId.B));
        TestGuard guard = spy(new TestGuard(true));
        TestAction action = spy(new TestAction());
        TestTransition transition = new TestTransition(StateId.A, StateId.B, Optional.of(action), guard);

        TestEventX event = new TestEventX();

        StateMachine<TestEntity> stateMachine = builder
                .withEntityMutator(e)
                .withStartState(a)
                .withState(b)
                .withTransition(transition)
                .build();

        // when
        stateMachine.process(e, event);

        // then
        assertEquals(e.getState(), StateId.B);

        InOrder inOrder = inOrder(a, b, guard, action, e);
        inOrder.verify(a).onEvent(e, event);
        inOrder.verify(guard).accept(e, event);
        inOrder.verify(action).execute(e, event);
        inOrder.verify(a).onExit(e);
        inOrder.verify(e).setStateIdentifier(e, StateId.B);
        inOrder.verify(b).onEntry(e);
    }

    @Test
    public void testGuardDeny() {
        // given
        TestEntity e = new TestEntity(StateId.A);
        StateMachine<TestEntity> stateMachine = builder
                .withEntityMutator(e)
                .withStartState(new TestState(StateId.A))
                .withState(new TestState(StateId.B))
                .withTransition(new TestTransition(StateId.A, StateId.B, new TestGuard(false)))
                .build();

        // when
        stateMachine.process(e, new TestEventX());

        // then
        assertEquals(e.getState(), StateId.A);
    }

    @Test
    public void testMultipleTransitionsOneGuardAccept() {
        // given
        TestEntity e = new TestEntity(StateId.A);
        StateMachine<TestEntity> stateMachine = builder
                .withEntityMutator(e)
                .withStartState(new TestState(StateId.A))
                .withState(new TestState(StateId.B))
                .withTransition(new TestTransition(StateId.A, StateId.B, new TestGuard(false)))
                .withTransition(new TestTransition(StateId.A, StateId.B, new TestGuard(false)))
                .withTransition(new TestTransition(StateId.A, StateId.B, new TestGuard(true)))
                .build();

        // when
        stateMachine.process(e, new TestEventX());

        // then
        assertEquals(e.getState(), StateId.B);
    }

    @Test
    public void testSelfTransition() {
        // given
        TestEntity e = new TestEntity(StateId.A);
        StateMachine<TestEntity> stateMachine = builder
                .withEntityMutator(e)
                .withStartState(new TestState(StateId.A))
                .withTransition(new TestTransition(StateId.A, StateId.A, new TestGuard(true)))
                .build();

        // when
        stateMachine.process(e, new TestEventX());

        // then
        assertEquals(e.getState(), StateId.A);
    }


}
