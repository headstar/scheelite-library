package com.headstartech.scheelite.diagram;

import com.headstartech.scheelite.State;
import com.headstartech.scheelite.StateMachineConfiguration;
import com.headstartech.scheelite.Transition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * Created by per on 8/12/15.
 */
public class StateTreeTraverser<T, U> {

    private StateMachineConfiguration<T, U> configuration;

    public StateTreeTraverser(StateMachineConfiguration<T, U> configuration) {
        this.configuration = configuration;
    }

    public void accept(StateTreeVisitor<T, U> visitor) {
        visit(visitor, configuration.getRootState());
    }

    private void visit(StateTreeVisitor<T, U> visitor, State<T, U> state) {
        visitor.visitStateStart(state);
        Collection<Transition<T, U>> transitions = getTransitionsFrom(configuration.getTransitions(), state);
        for (Transition<T, U> t : transitions) {
            visitor.visitTransition(t);
        }
        Iterable<State<T, U>> children = getChildren(state);
        for (State<T, U> child : children) {
            visit(visitor, child);
        }

        visitor.visitStateEnd(state);
    }

    private Iterable<State<T, U>> getChildren(State<T, U> root) {
        Collection<State<T, U>> children = new ArrayList<State<T, U>>();
        for (State<T, U> s : configuration.getStates()) {
            if (configuration.getSuperState(s).equals(root)) {
                children.add(s);
            }
        }
        return children;
    }

    private <T, U> Collection<Transition<T, U>> getTransitionsFrom(Set<Transition<T, U>> transitions, State<T, U> state) {
        Collection<Transition<T, U>> targets = new ArrayList<Transition<T, U>>();
        for (Transition<T, U> t : transitions) {
            if (t.getMainSourceState().equals(state)) {
                targets.add(t);
            }
        }
        return targets;
    }


}
