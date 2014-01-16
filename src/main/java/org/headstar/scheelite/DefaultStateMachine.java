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

public class DefaultStateMachine<T, U> implements StateMachine<T, U> {

    private final Map<Object, State<T, U>> states;
    private final Set<Transition<T, U>> transitions;
    private final Multimap<Object, Transition<T, U>> transitionsFromState;

    protected DefaultStateMachine(Set<State<T, U>> states, Set<Transition<T, U>> transitions) {
        this.states = Maps.newHashMap();
        for(State<T, U> state : states) {
            this.states.put(state.getIdentifier(), state);
        }
        this.transitions = transitions;
        this.transitionsFromState = ArrayListMultimap.create();
        for(Transition<T, U> transition : transitions) {
            transitionsFromState.put(transition.getFromState(), transition);
        }
    }

    @Override
    public Object process(Object stateIdentifier, T entity, U context, Object event) {

        // get current state
        State<T, U> currentState = states.get(stateIdentifier);
        if(currentState == null) {
            throw new IllegalArgumentException(String.format("unknown state: stateIdentifier=%s", stateIdentifier));
        }

        // handle event
        currentState.onEvent(entity, context, event);

        // process activated transition (if any)
        Optional<Transition<T, U>> activatedTransitionOpt = getActivatedTransition(stateIdentifier,entity, context, event);
        if(activatedTransitionOpt.isPresent()) {
            Transition<T, U> activatedTransition = activatedTransitionOpt.get();

            // execute action (if any)
            Optional<Action<T, U>> actionOpt = activatedTransition.getAction();
            if(actionOpt.isPresent()) {
                Action<T, U> action = actionOpt.get();
                action.execute(entity, context, event);
            }

            // exit current state
            currentState.onExit(entity, context);

            // enter next state
            State<T, U> nextState = states.get(activatedTransition.getToState());
            nextState.onEntry(entity, context);

            // return next state
            return nextState.getIdentifier();
        } else {
            // remain in the same state
            return stateIdentifier;
        }
   }

    protected Optional<Transition<T, U>> getActivatedTransition(Object stateIdentifier, T entity, U context, Object event) {
        Collection<Transition<T, U>> transitionsFromCurrentState = transitionsFromState.get(stateIdentifier);

        Collection<Transition<T, U>> activatedTransitions = Collections2.filter(transitionsFromCurrentState, new GuardIsAccepting<T, U>(entity, context, event));
        if(activatedTransitions.isEmpty()) {
            return Optional.absent();
        } else if(activatedTransitions.size() == 1) {
            return Optional.of(activatedTransitions.iterator().next());
        } else {
            throw new IllegalStateException("more than 1 transition activated!");
        }
    }

    private static class GuardIsAccepting<T, U> implements Predicate<Transition<T, U>> {

        private final T entity;
        private final U context;
        private final Object event;

        private GuardIsAccepting(T entity, U context, Object event) {
            this.entity = entity;
            this.context = context;
            this.event = event;
        }

        @Override
        public boolean apply(Transition<T, U> input) {
            return input.getGuard().accept(entity, context, event);
        }
    }
}
