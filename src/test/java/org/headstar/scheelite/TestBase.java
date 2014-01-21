package org.headstar.scheelite;

import com.google.common.base.Optional;
import org.testng.annotations.BeforeMethod;

/**
 * Created by Per on 2014-01-16.
 */
public class TestBase {
    enum StateId {A, B, C, D, E}

    protected class TestEntity implements Entity<StateId> {

        private StateId state;

        TestEntity() {
            this(StateId.A);
        }

        TestEntity(StateId state) {
            this.state = state;
        }

        public StateId getState() {
            return state;
        }

        @Override
        public Object getId() {
            return 17L;
        }

        @Override
        public StateId getStateId() {
            return state;
        }

        @Override
        public void setStateId(StateId identifier) {
            this.state = identifier;
        }

    }

    protected StateMachineBuilder<TestEntity, StateId> builder;

    @BeforeMethod
    public void setup() {
        builder = StateMachineBuilder.<TestEntity, StateId>newBuilder();
    }

    protected class TestAction implements Action<TestEntity> {

        @Override
        public String getName() {
            return "testAction";
        }

        @Override
        public void execute(TestEntity entity, Object event) {

        }

    }

    protected class TestGuard implements Guard<TestEntity> {
        private final boolean accept;

        @Override
        public String getName() {
            return "testGuard";
        }

        public TestGuard(boolean accept) {
            this.accept = accept;
        }

        public TestGuard() {
            this(true);
        }

        @Override
        public boolean accept(TestEntity entity, Object event) {
            return accept;
        }

    }

    protected class TestTransition extends TransitionAdapter<TestEntity, StateId> {
        private final Optional<TestAction> action;

        TestTransition(StateId inputStateId, StateId outputStateId) {
            this(inputStateId, outputStateId, Optional.of(new TestAction()), new TestGuard());
        }

        TestTransition(StateId inputStateId, StateId outputStateId, TestGuard guard) {
            this(inputStateId, outputStateId, Optional.of(new TestAction()), guard);
        }

        TestTransition(StateId inputStateId, StateId outputStateId, Optional<TestAction> action, TestGuard guard) {
            super(inputStateId, outputStateId, action.get(), guard);
            this.action = action;
        }


        @Override
        public Optional<TestAction> getAction() {
            return action;
        }
    }

    protected class TestState extends StateAdapter<TestEntity, StateId> {

        private StateId id;

        TestState(StateId id) {
            this.id = id;
        }

        @Override
        public StateId getId() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestState testState = (TestState) o;

            if (id != testState.id) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return id != null ? id.hashCode() : 0;
        }
    }

    protected class TestEventX {

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("TestEventX [");
            sb.append(']');
            return sb.toString();
        }
    }

    protected class TestEventY {

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("TestEventY [");
            sb.append(']');
            return sb.toString();
        }
    }

    protected class TestEventZ {
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("TestEventZ [");
            sb.append(']');
            return sb.toString();
        }

    }

}
