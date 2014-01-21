package org.headstar.scheelite;

import com.google.common.base.Optional;
import org.testng.annotations.BeforeMethod;

/**
 * Created by Per on 2014-01-16.
 */
public class TestBase {
    enum StateId {A, B, C, D, E}

    protected class TestEntity implements EntityMutator<TestEntity, StateId> {

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
        public StateId getStateIdentifier(TestEntity entity) {
            return state;
        }

        @Override
        public void setStateIdentifier(TestEntity entity, StateId identifier) {
            this.state = identifier;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("TestEntity [");
            sb.append("state=").append(state);
            sb.append(']');
            return sb.toString();
        }
    }

    protected StateMachineBuilder<TestEntity, StateId> builder;

    @BeforeMethod
    public void setup() {
        builder = StateMachineBuilder.<TestEntity, StateId>newBuilder();
    }

    protected class TestAction implements Action<TestEntity> {

        @Override
        public void execute(TestEntity entity, Object event) {

        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("TestAction [");
            sb.append(']');
            return sb.toString();
        }
    }

    protected class TestGuard implements Guard<TestEntity> {
        private final boolean accept;

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

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("TestGuard [");
            sb.append("accept=").append(accept);
            sb.append(']');
            return sb.toString();
        }
    }

    protected class TestTransition implements Transition<TestEntity, StateId> {
        private final StateId inputStateId;
        private final StateId outputStateId;
        private final Optional<TestAction> action;
        private final Guard<TestEntity> guard;

        TestTransition(StateId inputStateId, StateId outputStateId) {
            this(inputStateId, outputStateId, Optional.of(new TestAction()), new TestGuard());
        }

        TestTransition(StateId inputStateId, StateId outputStateId, TestGuard guard) {
            this(inputStateId, outputStateId, Optional.of(new TestAction()), guard);
        }

        TestTransition(StateId inputStateId, StateId outputStateId, Optional<TestAction> action, TestGuard guard) {
            this.inputStateId = inputStateId;
            this.outputStateId = outputStateId;
            this.action = action;
            this.guard = guard;
        }

        @Override
        public StateId getFromState() {
            return inputStateId;
        }

        @Override
        public StateId getToState() {
            return outputStateId;
        }

        @Override
        public Optional<TestAction> getAction() {
            return action;
        }

        public Guard<TestEntity> getGuard() {
            return guard;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("TestTransition [");
            sb.append("inputStateId=").append(inputStateId);
            sb.append(", outputStateId=").append(outputStateId);
            sb.append(", action=").append(action);
            sb.append(", guard=").append(guard);
            sb.append(']');
            return sb.toString();
        }
    }

    protected class TestState extends StateAdapter<TestEntity, StateId> {

        private StateId id;

        TestState(StateId id) {
            this.id = id;
        }

        @Override
        public StateId getIdentifier() {
            return id;
        }

        @Override
        public String toString() {
            return "TestState{" +
                    "id=" + id +
                    "} ";
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
