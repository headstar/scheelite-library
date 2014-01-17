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

public class DefaultStateMachine<T, U> implements StateMachine<T> {

    private final Map<Object, State<T, U>> states;
    private final Set<Transition<T, U>> transitions;
    private final Multimap<Object, Transition<T, U>> transitionsFromState;
    private final EntityMutator<T, U> entityMutator;

    protected DefaultStateMachine(Set<State<T, U>> states, Set<Transition<T, U>> transitions, EntityMutator<T, U> entityMutator) {
        this.states = Maps.newHashMap();
        for(State<T, U> state : states) {
            this.states.put(state.getIdentifier(), state);
        }
        this.transitions = transitions;
        this.transitionsFromState = ArrayListMultimap.create();
        for(Transition<T, U> transition : transitions) {
            transitionsFromState.put(transition.getFromState(), transition);
        }
        this.entityMutator = entityMutator;
    }

    @Override
    public void process(T entity, Object event) {

        // get current state
        Object stateIdentifier = entityMutator.getStateIdentifier(entity);
        State<T, U> currentState = states.get(stateIdentifier);
        if(currentState == null) {
            throw new IllegalStateException(String.format("unknown current state: stateIdentifier=%s", stateIdentifier));
        }

        // handle event
        currentState.onEvent(entity, event);

        // process triggered transition (if any)
        Optional<Transition<T, U>> activatedTransitionOpt = getActivatedTransition(stateIdentifier, entity, event);
        if(activatedTransitionOpt.isPresent()) {
            Transition<T, U> activatedTransition = activatedTransitionOpt.get();

            // get next state
            State<T, U> nextState = states.get(activatedTransition.getToState());
            if(nextState == null) {
                throw new IllegalStateException(String.format("unknown next state: stateIdentifier=%s", stateIdentifier));
            }

            // execute action (if any)
            Optional<? extends Action<T>> actionOpt = activatedTransition.getAction();
            if(actionOpt.isPresent()) {
                Action<T> action = actionOpt.get();
                action.execute(entity, event);
            }

            // exit current state
            currentState.onExit(entity);

            // update entity
            entityMutator.setStateIdentifier(entity, nextState.getIdentifier());

            // enter next state
            nextState.onEntry(entity);
        }
   }

    protected Optional<Transition<T, U>> getActivatedTransition(Object stateIdentifier, T entity, Object event) {
        Collection<Transition<T, U>> transitionsFromCurrentState = transitionsFromState.get(stateIdentifier);

        Collection<Transition<T, U>> activatedTransitions = Collections2.filter(transitionsFromCurrentState,
                new GuardIsAccepting<T, U>(entity, event));
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
        private final Object event;

        private GuardIsAccepting(T entity, Object event) {
            this.entity = entity;
            this.event = event;
        }

        @Override
        public boolean apply(Transition<T, U> input) {
            return input.getGuard().accept(entity, event);
        }
    }
}
