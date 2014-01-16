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

    protected StateMachineBuilder<Entity> builder;

    @BeforeMethod
    public void setup() {
        builder = new StateMachineBuilder<Entity>();
    }

    protected class TestAction implements Action<Entity> {

        @Override
        public void execute(Entity entity, Object event) {

        }
    }

    protected class TestGuard implements Guard<Entity> {
        private final boolean accept;

        public TestGuard(boolean accept) {
            this.accept = accept;
        }

        public TestGuard() {
            this(true);
        }

        @Override
        public boolean accept(Entity entity, Object event) {
            return accept;
        }
    }

    protected class TestTransition implements Transition<Entity> {
        private final Object inputStateId;
        private final Object outputStateId;
        private final Optional<TestAction> action;
        private final Guard<Entity> guard;

        TestTransition(Object inputStateId, Object outputStateId) {
            this(inputStateId, outputStateId, Optional.of(new TestAction()), new TestGuard());
        }

        TestTransition(Object inputStateId, Object outputStateId, TestGuard guard) {
            this(inputStateId, outputStateId, Optional.of(new TestAction()), guard);
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
        public Optional<? extends Action<Entity>> getAction() {
            return action;
        }

        public Guard<Entity> getGuard() {
            return guard;
        }
    }

    protected class TestState extends StateAdapter<Entity> {

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
