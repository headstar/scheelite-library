package com.headstartech.scheelite.diagram.plantuml;

import com.google.common.base.Optional;
import com.headstartech.scheelite.*;
import com.headstartech.scheelite.diagram.annotations.Diagram;
import org.testng.annotations.BeforeMethod;

/**
 * Created by Per on 2014-01-16.
 */
public class TestBase {
    public enum StateId {A, B, C, D, E, F, G}

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

    @Diagram("firstguard")
    public class FirstTestGuard implements Guard<TestEntity> {
        @Override
        public String toString() {
            return getClass().getSimpleName();
        }

        public FirstTestGuard() {
        }

        @Override
        public boolean evaluate(TestEntity context, Optional<?> event) {
            return true;
        }
    }

    @Diagram("secondguard")
    public class SecondTestGuard implements Guard<TestEntity> {
        @Override
        public String toString() {
            return getClass().getSimpleName();
        }

        public SecondTestGuard() {
        }

        @Override
        public boolean evaluate(TestEntity context, Optional<?> event) {
            return true;
        }
    }

    @Diagram("a")
    public static class StateA extends StateAdapter<TestEntity, StateId> {

        private StateId stateId = StateId.A;

        @Override
        public StateId getId() {
            return stateId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            StateA stateA = (StateA) o;

            return stateId == stateA.stateId;

        }

        @Override
        public int hashCode() {
            return stateId.hashCode();
        }
    }

    public static class TestState extends StateAdapter<TestEntity, StateId> {

        private final StateId stateId;

        public TestState(StateId stateId) {
            this.stateId = stateId;
        }

        @Override
        public StateId getId() {
            return stateId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestState testState = (TestState) o;

            return stateId == testState.stateId;

        }

        @Override
        public int hashCode() {
            return stateId.hashCode();
        }
    }

    public static class TestFinalState extends FinalState<TestEntity, StateId> {

        private final StateId stateId;

        public TestFinalState(StateId stateId) {
            this.stateId = stateId;
        }

        public StateId getId() {
            return stateId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestFinalState that = (TestFinalState) o;

            return stateId == that.stateId;

        }

        @Override
        public int hashCode() {
            return stateId != null ? stateId.hashCode() : 0;
        }
    }

    @Diagram("eventX")
    public class TestEventX {
        @Override
        public String toString() {
            return getClass().getSimpleName();
        }
    }

    @Diagram("eventY")
    public class TestEventY {
        @Override
        public String toString() {
            return getClass().getSimpleName();
        }
    }

}
