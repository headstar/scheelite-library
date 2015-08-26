package com.headstartech.scheelite.diagram.plantuml;

import com.google.common.base.Optional;
import com.headstartech.scheelite.*;
import com.headstartech.scheelite.diagram.annotations.Diagram;
import org.testng.annotations.BeforeMethod;

/**
 * Created by Per on 2014-01-16.
 */
public class TestBase {
    public enum StateId {A, B, C, D, E, F}

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

    @Diagram("aguard")
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

    public static class StateB extends StateAdapter<TestEntity, StateId> {

        private StateId stateId = StateId.B;

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

    public static class StateC extends StateAdapter<TestEntity, StateId> {

        private StateId stateId = StateId.C;

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

    public static class StateD extends StateAdapter<TestEntity, StateId> {

        private StateId stateId = StateId.D;

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

    public static class TestFinalState extends FinalState<TestEntity, StateId> {

        @Override
        public StateId getId() {
            return StateId.F;
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
