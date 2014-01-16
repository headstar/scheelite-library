package org.headstar.scheelite;

import com.google.common.base.Optional;
import org.testng.annotations.BeforeMethod;

/**
 * Created by Per on 2014-01-16.
 */
public class TestBase {
    enum STATE {A, B, C, D, E}

    static class Entity {
    }

    protected StateMachineBuilder<Entity, Object> builder;

    @BeforeMethod
    public void setup() {
        builder = new StateMachineBuilder<Entity, Object>();
    }

    protected class TestAction implements Action<Entity, Object> {
        @Override
        public void execute(Entity entity, Object context, Object event) {

        }
    }

    protected class TestGuard implements Guard<Entity, Object> {
        @Override
        public boolean accept(Entity entity, Object context, Object event) {
            return true;
        }
    }

    protected class TestTransition implements Transition<Entity, Object> {
        private final Object inputStateId;
        private final Object outputStateId;
        private final Optional<TestAction> action;
        private final Guard<Entity, Object> guard;

        TestTransition(Object inputStateId, Object outputStateId) {
            this(inputStateId, outputStateId, Optional.of(new TestAction()), new TestGuard());
        }

        TestTransition(Object inputStateId, Object outputStateId, Optional<TestAction> action, TestGuard guard) {
            this.inputStateId = inputStateId;
            this.outputStateId = outputStateId;
            this.action = action;
            this.guard = guard;
        }

        @Override
        public Object getFromState() {
            return inputStateId;
        }

        @Override
        public Object getToState() {
            return outputStateId;
        }

        @Override
        public Optional<? extends Action<Entity, Object>> getAction() {
            return action;
        }

        public Guard<Entity, Object> getGuard() {
            return guard;
        }
    }

    protected class TestState extends StateAdapter<Entity, Object> {

        private STATE id;

        TestState(STATE id) {
            this.id = id;
        }

        @Override
        public Object getIdentifier() {
            return id;
        }

        @Override
        public String toString() {
            return "TestState{" +
                    "id=" + id +
                    "} " + super.toString();
        }
    }

    protected class TestEventX {
    }

    protected class TestEventY {
    }

    protected class TestEventZ {
    }

}
