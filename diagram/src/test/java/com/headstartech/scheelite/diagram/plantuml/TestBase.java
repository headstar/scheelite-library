package com.headstartech.scheelite.diagram.plantuml;

import com.google.common.base.Optional;
import com.headstartech.scheelite.*;
import org.testng.annotations.BeforeMethod;

/**
 * Created by Per on 2014-01-16.
 */
public class TestBase {
    public enum StateId {A, B, C, D, E}

    public class TestEntity {

        private StateId state;

        TestEntity() {
            this(StateId.A);
        }

        TestEntity(StateId state) {
            this.state = state;
        }

        public StateId getStateId() {
            return state;
        }

        @Override
        public String toString() {
            return "TestEntity{" +
                    "state=" + state +
                    '}';
        }
    }

    public StateMachineBuilder<TestEntity, StateId> builder;

    @BeforeMethod
    public void setup() {
        builder = StateMachineBuilder.<TestEntity, StateId>newBuilder();
    }

    public class TestAction implements Action<TestEntity> {

        @Override
        public void execute(TestEntity context, Optional<?> event) {

        }

    }

    public class AlwaysAcceptTestGuard extends TestGuard {

        public AlwaysAcceptTestGuard() {
            super(true);
        }

    }

    public class AlwaysDenyTestGuard extends TestGuard {

        public AlwaysDenyTestGuard() {
            super(false);
        }

    }

    public class TestGuard implements Guard<TestEntity> {
        private final boolean accept;

        @Override
        public String toString() {
            return getClass().getSimpleName();
        }

        public TestGuard(boolean accept) {
            this.accept = accept;
        }

        public TestGuard() {
            this(true);
        }


        @Override
        public boolean evaluate(TestEntity context, Optional<?> event) {
            return accept;
        }
    }

    enum HandleEvent { YES, NO };

    public static class TestState extends StateAdapter<TestEntity, StateId> {

        private final StateId id;
        private final HandleEvent handleEvent;

        public TestState(StateId id) {
            this(id, HandleEvent.YES);
        }

        TestState(StateId id, HandleEvent handleEvent) {
            this.id = id;
            this.handleEvent = handleEvent;
        }

        @Override
        public boolean onEvent(TestEntity context, Object event) {
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

    public static class TestFinalState extends FinalState<TestEntity, StateId> {

        private final StateId id;

        public TestFinalState(StateId id) {
            this.id = id;
        }

        @Override
        public StateId getId() {
            return id;
        }
    }

    public class TestEventX {
        @Override
        public String toString() {
            return getClass().getSimpleName();
        }
    }

    public class TestEventY {
        @Override
        public String toString() {
            return getClass().getSimpleName();
        }
    }

}
