package org.headstar.scheelite;

import com.google.common.base.Optional;
import org.mockito.InOrder;
import org.mockito.Mockito;
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
                .withInitialTransition(a)
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

    @Test(expectedExceptions = InvalidStateIdException.class)
    public void testNullStateId() {
        // given
        TestEntity e = new TestEntity(null);
        TestState a = new TestState(StateId.A);
        TestEventX event = new TestEventX();

        StateMachine<TestEntity> stateMachine = builder
                .withInitialTransition(a)
                .build();

        // when
        stateMachine.process(e, event);

        // then ... exception should be thrown
    }


    @Test(expectedExceptions = InvalidStateIdException.class)
    public void testNoStateForStateId() {
        // given
        TestEntity e = new TestEntity(StateId.B);
        TestState a = new TestState(StateId.A);
        TestEventX event = new TestEventX();

        StateMachine<TestEntity> stateMachine = builder
                .withInitialTransition(a)
                .build();

        // when
        stateMachine.process(e, event);

        // then ... exception should be thrown
    }

    @Test
    public void testInitialTransition() {
        // given
        TestEntity e = spy(new TestEntity(StateId.B));
        TestState a = spy(new TestState(StateId.A));
        TestState b = spy(new TestState(StateId.B));
        TestState c = spy(new TestState(StateId.C));
        TestDefaultAction defaultAction1 = spy(new TestDefaultAction());
        TestDefaultAction defaultAction2 = spy(new TestDefaultAction());
        TestDefaultAction defaultAction3 = spy(new TestDefaultAction());

        StateMachine<TestEntity> stateMachine = builder
                .withInitialTransition(a, defaultAction1)
                .withCompositeState(a, defaultAction2, b)
                .withCompositeState(b, defaultAction3, c)
                .build();

        // when
        stateMachine.initialTransition(e);

        // then
        assertEquals(e.getStateId(), StateId.C);

        InOrder inOrder = inOrder(a, b, c, defaultAction1, defaultAction2, defaultAction3, e);
        inOrder.verify(defaultAction1).execute(e);
        inOrder.verify(a).onEntry(e);
        inOrder.verify(defaultAction2).execute(e);
        inOrder.verify(b).onEntry(e);
        inOrder.verify(defaultAction3).execute(e);
        inOrder.verify(c).onEntry(e);

        assertEquals(e.getStateId(), StateId.C);

        verifyStateInteraction(a, TestEntity.class, onEntry(1), onExit(0), onEvent(0));
        verifyStateInteraction(b, TestEntity.class, onEntry(1), onExit(0), onEvent(0));
        verifyStateInteraction(c, TestEntity.class, onEntry(1), onExit(0), onEvent(0));
        verify(defaultAction1).execute(e);
        verify(defaultAction2).execute(e);
        verify(defaultAction3).execute(e);
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
                .withInitialTransition(a)
                .withTransition(a, b, guard, action)
                .build();

        // when
        stateMachine.process(e, event);

        // then
        assertEquals(e.getStateId(), StateId.B);

        InOrder inOrder = inOrder(a, b, guard, action, e);
        inOrder.verify(a).onEvent(e, event);
        inOrder.verify(guard).accept(e, Optional.of(event));
        inOrder.verify(a).onExit(e);
        inOrder.verify(action).execute(e, Optional.of(event));
        inOrder.verify(b).onEntry(e);

        assertEquals(e.getStateId(), StateId.B);

        verifyStateInteraction(a, TestEntity.class, onEntry(0), onExit(1), onEvent(1));
        verifyStateInteraction(b, TestEntity.class, onEntry(1), onExit(0), onEvent(0));
    }

    @Test
    public void testGuardDeny() {
        // given
        TestEntity e = new TestEntity(StateId.A);
        TestState a = new TestState(StateId.A);
        TestState b = new TestState(StateId.B);
        StateMachine<TestEntity> stateMachine = builder
                .withInitialTransition(a)
                .withTransition(a, b, new TestGuard(false))
                .build();

        // when
        stateMachine.process(e, new TestEventX());

        // then
        assertEquals(e.getStateId(), StateId.A);
    }

    @Test
    public void testMultipleTransitionsOneGuardAccept() {
        // given
        TestState a = new TestState(StateId.A);
        TestState b = new TestState(StateId.B);

        TestEntity e = new TestEntity(StateId.A);
        StateMachine<TestEntity> stateMachine = builder
                .withInitialTransition(a)
                .withTransition(a, b, new TestGuard(false))
                .withTransition(a, b, new TestGuard(false))
                .withTransition(a, b, new TestGuard(true))
                .build();

        // when
        stateMachine.process(e, new TestEventX());

        // then
        assertEquals(e.getStateId(), StateId.B);
    }

    @Test
    public void testExternalSelfTransition() {
        // given
        TestEntity e = spy(new TestEntity(StateId.A));
        TestState a = spy(new TestState(StateId.A));
        TestGuard guard = spy(new TestGuard(true));
        TestAction action = spy(new TestAction());
        TestEventX event = new TestEventX();

        StateMachine<TestEntity> stateMachine = builder
                .withInitialTransition(a)
                .withTransition(a, a, guard, action)
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

        assertEquals(e.getStateId(), StateId.A);

        verifyStateInteraction(a, TestEntity.class, onEntry(1), onExit(1), onEvent(1));
    }

    @Test
    public void testLocalSelfTransition() {
        // given
        TestEntity e = spy(new TestEntity(StateId.A));
        TestState a = spy(new TestState(StateId.A));
        TestGuard guard = spy(new TestGuard(true));
        TestAction action = spy(new TestAction());
        TestEventX event = new TestEventX();

        StateMachine<TestEntity> stateMachine = builder
                .withInitialTransition(a)
                .withLocalTransition(a, a, guard, action)
                .build();

        // when
        stateMachine.process(e, event);

        // then
        InOrder inOrder = inOrder(a, guard, action, e);
        inOrder.verify(a).onEvent(e, event);
        inOrder.verify(guard).accept(e, Optional.of(event));
        inOrder.verify(action).execute(e, Optional.of(event));

        assertEquals(e.getStateId(), StateId.A);

        verifyStateInteraction(a, TestEntity.class, onEntry(0), onExit(0), onEvent(1));
    }

    @Test
    public void testInternalTransition() {
        // given
        TestEntity e = spy(new TestEntity(StateId.A));
        TestState a = spy(new TestState(StateId.A));

        TestEventX event = new TestEventX();

        StateMachine<TestEntity> stateMachine = builder
                .withInitialTransition(a)
                .build();

        // when
        stateMachine.process(e, event);

        // then
        assertEquals(e.getStateId(), StateId.A);

        verifyStateInteraction(a, TestEntity.class, onEntry(0), onExit(0), onEvent(1));
    }

    @Test
    public void testExternalTransitionToSuperState() {
        // given
        TestEntity e = spy(new TestEntity(StateId.C));
        TestState a = spy(new TestState(StateId.A));
        TestState b = spy(new TestState(StateId.B));
        TestState c = spy(new TestState(StateId.C));
        TestAction action = spy(new TestAction());
        TestEventX event = new TestEventX();

        StateMachine<TestEntity> stateMachine = builder
                .withInitialTransition(a)
                .withCompositeState(a, b, c)
                .withTransition(b, c, new AlwaysDenyTestGuard())
                .withTransition(c, a, action)
                .build();

        // when
        stateMachine.process(e, event);

        // then
        InOrder inOrder = inOrder(a, b, c, action, e);
        inOrder.verify(c).onEvent(e, event);
        inOrder.verify(c).onExit(e);
        inOrder.verify(a).onExit(e);
        inOrder.verify(action).execute(e, Optional.of(event));
        inOrder.verify(a).onEntry(e);
        inOrder.verify(b).onEntry(e);

        verifyStateInteraction(a, TestEntity.class, onEntry(1), onExit(1), onEvent(0));
        verifyStateInteraction(b, TestEntity.class, onEntry(1), onExit(0), onEvent(0));
        verifyStateInteraction(c, TestEntity.class, onEntry(0), onExit(1), onEvent(1));
    }

    @Test
    public void testLocalTransitionToSuperState() {
        // given
        TestEntity e = spy(new TestEntity(StateId.C));
        TestState a = spy(new TestState(StateId.A));
        TestState b = spy(new TestState(StateId.B));
        TestState c = spy(new TestState(StateId.C));
        TestAction action = spy(new TestAction());
        TestEventX event = new TestEventX();

        StateMachine<TestEntity> stateMachine = builder
                .withInitialTransition(a)
                .withCompositeState(a, b, c)
                .withTransition(b, c, new AlwaysDenyTestGuard())
                .withLocalTransition(c, a, new AlwaysAcceptTestGuard(), action)
                .build();

        // when
        stateMachine.process(e, event);

        // then
        InOrder inOrder = inOrder(a, b, c, action, e);

        inOrder.verify(c).onEvent(e, event);
        inOrder.verify(c).onExit(e);
        inOrder.verify(action).execute(e, Optional.of(event));
        inOrder.verify(b).onEntry(e);

        assertEquals(e.getStateId(), StateId.B);

        verifyStateInteraction(a, TestEntity.class, onEntry(0), onExit(0), onEvent(0));
        verifyStateInteraction(b, TestEntity.class, onEntry(1), onExit(0), onEvent(0));
        verifyStateInteraction(c, TestEntity.class, onEntry(0), onExit(1), onEvent(1));
    }

    @Test
    public void testExternalTransitionToSubState() {
        // given
        TestEntity e = spy(new TestEntity(StateId.B));
        TestState a = spy(new TestState(StateId.A));
        TestState b = spy(new TestState(StateId.B, HandleEvent.NO));
        TestState c = spy(new TestState(StateId.C));
        TestAction action = spy(new TestAction());
        TestEventX event = new TestEventX();

        StateMachine<TestEntity> stateMachine = builder
                .withInitialTransition(a)
                .withCompositeState(a, b, c)
                .withTransition(a, c, new AlwaysAcceptTestGuard(), action)
                .build();

        // when
        stateMachine.process(e, event);

        // then
        InOrder inOrder = inOrder(a, b, c, action, e);
        inOrder.verify(b).onEvent(e, event);
        inOrder.verify(a).onEvent(e, event);
        inOrder.verify(b).onExit(e);
        inOrder.verify(a).onExit(e);
        inOrder.verify(action).execute(e, Optional.of(event));
        inOrder.verify(a).onEntry(e);
        inOrder.verify(c).onEntry(e);

        assertEquals(e.getStateId(), StateId.C);

        verifyStateInteraction(a, TestEntity.class, onEntry(1), onExit(1), onEvent(1));
        verifyStateInteraction(b, TestEntity.class, onEntry(0), onExit(1), onEvent(1));
        verifyStateInteraction(c, TestEntity.class, onEntry(1), onExit(0), onEvent(0));
    }

    @Test
    public void testLocalTransitionToSubState() {
        // given
        TestEntity e = spy(new TestEntity(StateId.B));
        TestState a = spy(new TestState(StateId.A));
        TestState b = spy(new TestState(StateId.B, HandleEvent.NO));
        TestState c = spy(new TestState(StateId.C));
        TestAction action = spy(new TestAction());
        TestEventX event = new TestEventX();

        StateMachine<TestEntity> stateMachine = builder
                .withInitialTransition(a)
                .withCompositeState(a, b, c)
                .withLocalTransition(a, c, new AlwaysAcceptTestGuard(), action)
                .build();

        // when
        stateMachine.process(e, event);

        // then
        InOrder inOrder = inOrder(a, b, c, action, e);
        inOrder.verify(b).onEvent(e, event);
        inOrder.verify(a).onEvent(e, event);
        inOrder.verify(b).onExit(e);
        inOrder.verify(action).execute(e, Optional.of(event));
        inOrder.verify(c).onEntry(e);

        assertEquals(e.getStateId(), StateId.C);

        verifyStateInteraction(a, TestEntity.class, onEntry(0), onExit(0), onEvent(1));
        verifyStateInteraction(b, TestEntity.class, onEntry(0), onExit(1), onEvent(1));
        verifyStateInteraction(c, TestEntity.class, onEntry(1), onExit(0), onEvent(0));
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
                .withInitialTransition(a)
                .withTransition(a, b, guard, action)
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

        inOrder.verify(b).onExit(e);
        inOrder.verify(d).onEntry(e);

        assertEquals(e.getStateId(), StateId.D);

        verifyStateInteraction(a, TestEntity.class, onEntry(0), onExit(1), onEvent(1));
        verifyStateInteraction(b, TestEntity.class, onEntry(1), onExit(1), onEvent(0));
        verifyStateInteraction(c, TestEntity.class, onEntry(0), onExit(0), onEvent(0));
        verifyStateInteraction(d, TestEntity.class, onEntry(1), onExit(0), onEvent(0));
    }

    @Test(expectedExceptions = MaxTransitionsException.class)
    public void testTransitionLoopDetection() {
        // given
        TestEntity e = spy(new TestEntity(StateId.A));
        TestState a = spy(new TestState(StateId.A));
        TestState b = spy(new TestState(StateId.B));
        TestEventX event = new TestEventX();

        StateMachine<TestEntity> stateMachine = builder
                .withInitialTransition(a)
                .withMaxTransitions(2)
                .withTransition(a, b)
                .withTransition(b, a)
                .build();

        // when
        try {
            stateMachine.process(e, event);

            // then
            verifyStateInteraction(a, TestEntity.class, onEntry(1), onExit(1), onEvent(1));
            verifyStateInteraction(b, TestEntity.class, onEntry(1), onExit(0), onEvent(0));
        } catch(MaxTransitionsException ex) {
            throw ex;
        }

    }

    @Test(expectedExceptions = MaxTransitionsException.class)
    public void testTransitionLoopNoStackOverflow() {
        // given
        TestEntity e = spy(new TestEntity(StateId.A));
        TestState a = spy(new TestState(StateId.A));
        TestState b = spy(new TestState(StateId.B));
        TestEventX event = new TestEventX();

        StateMachine<TestEntity> stateMachine = builder
                .withInitialTransition(a)
                .withMaxTransitions(1000)
                .withTransition(a, b)
                .withTransition(b, a)
                .build();

        // when
            stateMachine.process(e, event);

        // then... exception should be thrown
    }

    @Test
    public void testEventHandledBySuperState() {
        // given
        TestEntity e = spy(new TestEntity(StateId.B));
        TestState a = spy(new TestState(StateId.A));
        TestState b = spy(new TestState(StateId.B, HandleEvent.NO));

        TestEventX event = new TestEventX();

        StateMachine<TestEntity> stateMachine = builder
                .withInitialTransition(a)
                .withCompositeState(a, b)
                .build();

        // when
        stateMachine.process(e, event);

        // then
        InOrder inOrder = inOrder(a, b);
        inOrder.verify(b).onEvent(e, event);
        inOrder.verify(a).onEvent(e, event);
    }

    private <T,U> void verifyStateInteraction(State<T, U> state, Class<T> clazz, OnEntry onEntry, OnExit onExit, OnEvent onEvent) {
        verify(state, times(onEntry.times)).onEntry(Mockito.<T>any(clazz));
        verify(state, times(onExit.times)).onExit(Mockito.<T>any(clazz));
        verify(state, times(onEvent.times)).onEvent(Mockito.<T>any(clazz), Mockito.anyObject());
    }

    static class StateInteraction {

        final int times;

        StateInteraction(int times) {
            this.times = times;
        }
    }

    static OnEntry onEntry() {
        return new OnEntry(1);
    }

    static OnExit onExit() {
        return new OnExit(1);
    }

    static OnEvent onEvent() {
        return new OnEvent(1);
    }


    static OnEntry onEntry(int times) {
        return new OnEntry(times);
    }

    static OnExit onExit(int times) {
        return new OnExit(times);
    }

    static OnEvent onEvent(int times) {
        return new OnEvent(times);
    }


    static class OnEntry extends StateInteraction {
        OnEntry(int times) {
            super(times);
        }
    }

    static class OnExit extends StateInteraction {
        OnExit(int times) {
            super(times);
        }
    }

    static class OnEvent extends StateInteraction {
        OnEvent(int times) {
            super(times);
        }
    }

}
