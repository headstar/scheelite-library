package org.headstar.scheelite;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class DefaultStateMachine<T> implements StateMachine<T> {

    private final Map<Object, State<T>> states;
    private final Set<Transition<T>> transitions;
    private final Multimap<Object, Transition<T>> transitionsFromState;

    protected DefaultStateMachine(Set<State<T>> states, Set<Transition<T>> transitions) {
        this.states = Maps.newHashMap();
        for(State<T> state : states) {
            this.states.put(state.getIdentifier(), state);
        }
        this.transitions = transitions;
        this.transitionsFromState = ArrayListMultimap.create();
        for(Transition<T> transition : transitions) {
            transitionsFromState.put(transition.getFromState(), transition);
        }
    }

    @Override
    public Object process(Object stateIdentifier, T entity, Object event) {

        // get current state
        State<T> currentState = states.get(stateIdentifier);
        if(currentState == null) {
            throw new IllegalArgumentException(String.format("unknown state: stateIdentifier=%s", stateIdentifier));
        }

        // handle event
        currentState.onEvent(entity, event);

        // process activated transition (if any)
        Optional<Transition<T>> activatedTransitionOpt = getActivatedTransition(stateIdentifier, entity, event);
        if(activatedTransitionOpt.isPresent()) {
            Transition<T> activatedTransition = activatedTransitionOpt.get();

            // execute action (if any)
            Optional<? extends Action<T>> actionOpt = activatedTransition.getAction();
            if(actionOpt.isPresent()) {
                Action<T> action = actionOpt.get();
                action.execute(entity, event);
            }

            // exit current state
            currentState.onExit(entity);

            // enter next state
            State<T> nextState = states.get(activatedTransition.getToState());
            nextState.onEntry(entity);

            // return next state
            return nextState.getIdentifier();
        } else {
            // remain in the same state
            return stateIdentifier;
        }
   }

    protected Optional<Transition<T>> getActivatedTransition(Object stateIdentifier, T entity, Object event) {
        Collection<Transition<T>> transitionsFromCurrentState = transitionsFromState.get(stateIdentifier);

        Collection<Transition<T>> activatedTransitions = Collections2.filter(transitionsFromCurrentState,
                new GuardIsAccepting<T>(entity, event));
        if(activatedTransitions.isEmpty()) {
            return Optional.absent();
        } else if(activatedTransitions.size() == 1) {
            return Optional.of(activatedTransitions.iterator().next());
        } else {
            throw new IllegalStateException("more than 1 transition activated!");
        }
    }

    private static class GuardIsAccepting<T> implements Predicate<Transition<T>> {

        private final T entity;
        private final Object event;

        private GuardIsAccepting(T entity, Object event) {
            this.entity = entity;
            this.event = event;
        }

        @Override
        public boolean apply(Transition<T> input) {
            return input.getGuard().accept(entity, event);
        }
    }
}
