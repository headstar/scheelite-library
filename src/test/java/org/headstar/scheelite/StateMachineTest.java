package org.headstar.scheelite;

import com.google.common.base.Optional;
import org.mockito.InOrder;
import org.testng.annotations.Test;

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
        TestTransition transition = new TestTransition(StateId.A, StateId.B, Optional.of(action), Optional.of(guard));

        TestEventX event = new TestEventX();

        StateMachine<TestEntity> stateMachine = builder
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
        inOrder.verify(e).setStateId(StateId.B);
        inOrder.verify(b).onEntry(e);
    }

    @Test
    public void testGuardDeny() {
        // given
        TestEntity e = new TestEntity(StateId.A);
        StateMachine<TestEntity> stateMachine = builder
                .withStartState(new TestState(StateId.A))
                .withState(new TestState(StateId.B))
                .withTransition(new TestTransition(StateId.A, StateId.B, Optional.<TestAction>absent(), Optional.of(new TestGuard(false))))
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
                .withStartState(new TestState(StateId.A))
                .withState(new TestState(StateId.B))
                .withTransition(new TestTransition(StateId.A, StateId.B, Optional.<TestAction>absent(), Optional.of(new TestGuard(false))))
                .withTransition(new TestTransition(StateId.A, StateId.B, Optional.<TestAction>absent(), Optional.of(new TestGuard(false))))
                .withTransition(new TestTransition(StateId.A, StateId.B, Optional.<TestAction>absent(), Optional.of(new TestGuard(true))))
                .build();

        // when
        stateMachine.process(e, new TestEventX());

        // then
        assertEquals(e.getState(), StateId.B);
    }

    @Test
    public void testSelfTransition() {
        // given
        TestEntity e = spy(new TestEntity(StateId.A));
        TestState a = spy(new TestState(StateId.A));
        TestGuard guard = spy(new TestGuard(true));
        TestAction action = spy(new TestAction());
        TestTransition transition = new TestTransition(StateId.A, StateId.A, Optional.of(action), Optional.of(guard));

        TestEventX event = new TestEventX();

        StateMachine<TestEntity> stateMachine = builder
                .withStartState(a)
                .withTransition(transition)
                .build();

        // when
        stateMachine.process(e, event);

        // then
        InOrder inOrder = inOrder(a, guard, action, e);
        inOrder.verify(a).onEvent(e, event);
        inOrder.verify(guard).accept(e, event);
        inOrder.verify(action).execute(e, event);
        inOrder.verify(a).onExit(e);
        inOrder.verify(e).setStateId(StateId.A);
        inOrder.verify(a).onEntry(e);
    }

    @Test
    public void testInternalTransition() {
        // given
        TestEntity e = spy(new TestEntity(StateId.A));
        TestState a = spy(new TestState(StateId.A));
        TestGuard guard = spy(new TestGuard(false));
        TestAction action = spy(new TestAction());
        TestTransition transition = new TestTransition(StateId.A, StateId.A, Optional.of(action), Optional.of(guard));

        TestEventX event = new TestEventX();

        StateMachine<TestEntity> stateMachine = builder
                .withStartState(a)
                .withTransition(transition)
                .build();

        // when
        stateMachine.process(e, event);

        // then
        InOrder inOrder = inOrder(a, guard, action, e);
        inOrder.verify(a).onEvent(e, event);
        inOrder.verify(guard).accept(e, event);

        verify(action, never()).execute(e, event);
        verify(a, never()).onExit(e);
        verify(e, never()).setStateId(StateId.A);
        verify(a, never()).onEntry(e);
    }
}
