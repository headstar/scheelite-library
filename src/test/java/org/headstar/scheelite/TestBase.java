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

        @Override
        public Object getId() {
            return 17L;
        }

        @Override
        public StateId getState() {
            return state;
        }

        @Override
        public void setState(StateId identifier) {
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
        public void execute(TestEntity entity, Optional<?> event) {

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
        public boolean accept(TestEntity entity, Optional<?> event) {
            if(event.isPresent()) {
                return accept;
            }
            return false;
        }

    }

    enum HandleEvent { YES, NO };

    protected class TestState extends StateAdapter<TestEntity, StateId> {

        private final StateId id;
        private final HandleEvent handleEvent;

        TestState(StateId id) {
            this(id, HandleEvent.YES);
        }

        TestState(StateId id, HandleEvent handleEvent) {
            this.id = id;
            this.handleEvent = handleEvent;
        }

        @Override
        public boolean onEvent(TestEntity entity, Object event) {
            return handleEvent.equals(HandleEvent.YES);
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

        @Override
        public String toString() {
            return "TestState{" +
                    "id=" + id +
                    "}";
        }


    }

    protected class TestEventX {

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("TestEventX");
            return sb.toString();
        }
    }
}
