package org.headstar.scheelite;

import com.google.common.base.Optional;
import org.mockito.InOrder;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * Created by Per on 2014-01-16.
 */
public class StateMachineTest extends TestBase {

    @Test
    public void testThrowExceptionResolver() {
        // given
        TestEntity e = spy(new TestEntity(StateId.A));
        TestState a = spy(new TestState(StateId.A));
        TestState b = spy(new TestState(StateId.B));
        TestEventX event = new TestEventX();
        MultipleTransitionsTriggeredResolver<TestEntity, StateId> resolver = spy(new ThrowExceptionResolver<TestEntity, StateId>());

        StateMachine<TestEntity> stateMachine = builder
                .withStartState(a)
                .withSimpleState(b)
                .withMultipleTransitionsTriggerPolicy(resolver)
                .withTransition(a, b, new TestGuard(true))
                .withTransition(a, b, new TestGuard(true))
                .build();

        // when
        try {
            stateMachine.process(e, event);
            fail("should have thrown");
        } catch(IllegalStateException ex) {
            // expected
            assertThat(ex.getMessage(), containsString("multiple transitions triggered"));
        }

        // then ... exception should be thrown
        verify(a).onEvent(e, event);
    }


    @Test
    public void testSimpleTransition() {
        // given
        TestEntity e = spy(new TestEntity(StateId.A));
        TestState a = spy(new TestState(StateId.A));
        TestState b = spy(new TestState(StateId.B));
        TestGuard guard = spy(new TestGuard(true));
        TestAction action = spy(new TestAction());

        TestEventX event = new TestEventX();

        StateMachine<TestEntity> stateMachine = builder
                .withStartState(a)
                .withSimpleState(b)
                .withTransition(a, b, action, guard)
                .build();

        // when
        stateMachine.process(e, event);

        // then
        assertEquals(e.getState(), StateId.B);

        InOrder inOrder = inOrder(a, b, guard, action, e);
        inOrder.verify(a).onEvent(e, event);
        inOrder.verify(guard).accept(e, Optional.of(event));
        inOrder.verify(a).onExit(e);
        inOrder.verify(action).execute(e, Optional.of(event));
        inOrder.verify(b).onEntry(e);
        inOrder.verify(e).setState(StateId.B);
    }

    @Test
    public void testGuardDeny() {
        // given
        TestEntity e = new TestEntity(StateId.A);
        TestState a = new TestState(StateId.A);
        TestState b = new TestState(StateId.B);
        StateMachine<TestEntity> stateMachine = builder
                .withStartState(a)
                .withSimpleState(b)
                .withTransition(a, b, new TestGuard(false))
                .build();

        // when
        stateMachine.process(e, new TestEventX());

        // then
        assertEquals(e.getState(), StateId.A);
    }

    @Test
    public void testMultipleTransitionsOneGuardAccept() {
        // given
        TestState a = new TestState(StateId.A);
        TestState b = new TestState(StateId.B);

        TestEntity e = new TestEntity(StateId.A);
        StateMachine<TestEntity> stateMachine = builder
                .withStartState(a)
                .withSimpleState(b)
                .withTransition(a, b, new TestGuard(false))
                .withTransition(a, b, new TestGuard(false))
                .withTransition(a, b, new TestGuard(true))
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
        TestEventX event = new TestEventX();

        StateMachine<TestEntity> stateMachine = builder
                .withStartState(a)
                .withTransition(a, a, action, guard)
                .build();

        // when
        stateMachine.process(e, event);

        // then
        InOrder inOrder = inOrder(a, guard, action, e);
        inOrder.verify(a).onEvent(e, event);
        inOrder.verify(guard).accept(e, Optional.of(event));
        inOrder.verify(a).onExit(e);
        inOrder.verify(action).execute(e, Optional.of(event));
        inOrder.verify(a).onEntry(e);
        inOrder.verify(e).setState(StateId.A);
    }

    @Test
    public void testTriggerlessTransition() {
        // given
        TestEntity e = spy(new TestEntity(StateId.A));
        TestState a = spy(new TestState(StateId.A));
        TestState b = spy(new TestState(StateId.B));
        TestState c = spy(new TestState(StateId.C));
        TestState d = spy(new TestState(StateId.D));
        TestGuard guard = spy(new TestGuard(true));
        TestAction action = spy(new TestAction());
        TestEventX event = new TestEventX();

        StateMachine<TestEntity> stateMachine = builder
                .withStartState(a)
                .withSimpleState(b)
                .withSimpleState(c)
                .withSimpleState(d)
                .withTransition(a, b, action, guard)
                .withTransition(b, c, new TestGuard(false))
                .withTransition(b, d)
                .build();

        // when
        stateMachine.process(e, event);

        // then
        InOrder inOrder = inOrder(a, b, c, d, guard, action, e);
        inOrder.verify(a).onEvent(e, event);
        inOrder.verify(guard).accept(e, Optional.of(event));
        inOrder.verify(a).onExit(e);
        inOrder.verify(action).execute(e, Optional.of(event));
        inOrder.verify(b).onEntry(e);
        inOrder.verify(e).setState(StateId.B);

        inOrder.verify(b).onExit(e);
        inOrder.verify(d).onEntry(e);
    }


    @Test
    public void testInternalTransition() {
        // given
        TestEntity e = spy(new TestEntity(StateId.A));
        TestState a = spy(new TestState(StateId.A));

        TestEventX event = new TestEventX();

        StateMachine<TestEntity> stateMachine = builder
                .withStartState(a)
                .build();

        // when
        stateMachine.process(e, event);

        // then
        InOrder inOrder = inOrder(a, e);
        inOrder.verify(a).onEvent(e, event);

        verify(a, never()).onExit(e);
        verify(e, never()).setState(StateId.A);
        verify(a, never()).onEntry(e);
    }

    @Test
    public void testEventHandledBySuperState() {
        // given
        TestEntity e = spy(new TestEntity(StateId.B));
        TestState a = spy(new TestState(StateId.A));
        TestState b = spy(new TestState(StateId.B, HandleEvent.NO));

        TestEventX event = new TestEventX();

        StateMachine<TestEntity> stateMachine = builder
                .withStartState(a)
                .withCompositeState(a, b)
                .build();

        // when
        stateMachine.process(e, event);

        // then
        InOrder inOrder = inOrder(a, b);
        inOrder.verify(b).onEvent(e, event);
        inOrder.verify(a).onEvent(e, event);
    }
}
