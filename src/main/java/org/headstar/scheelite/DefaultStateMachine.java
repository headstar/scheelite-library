package org.headstar.scheelite;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class DefaultStateMachine<T extends Entity<U>, U> implements StateMachine<T> {

    private static final Logger logger = LoggerFactory.getLogger(DefaultStateMachine.class);

    private final ImmutableMap<U, State<T, U>> states;  // state id -> state
    private final ImmutableSet<Transition<T, U>> transitions;
    private final ImmutableMultimap<U, Transition<T, U>> transitionsFromState; // state id -> transitions from state
    private final MultipleTransitionsTriggeredResolver<T, U> multipleTransitionsTriggeredResolver;

    protected DefaultStateMachine(StateMachineBuilder<T, U> builder) {
        this.states = createStatesMap(builder.getStates());
        this.transitions = ImmutableSet.copyOf(builder.getTransitions());
        this.transitionsFromState = createTransitionsFromMap(builder.getTransitions());
        this.multipleTransitionsTriggeredResolver = builder.getMultipleTransitionsTriggeredResolver();
    }

    @Override
    public void process(T entity, Object event) {
        checkNotNull(entity);
        checkNotNull(event);

        // get current state
        U stateIdentifier = entity.getState();
        if (stateIdentifier == null) {
            throw new IllegalStateException(String.format("stateIdentifier is null"));
        }

        State<T, U> currentState = states.get(stateIdentifier);
        if (currentState == null) {
            throw new IllegalStateException(String.format("state unknown: stateIdentifier=%s", stateIdentifier));
        }

        // handle event
        logger.debug("handling event: entity={}, event={}, state={}", entity.getId(), event, currentState.getId());
        currentState.onEvent(entity, event);

        // process triggered transition (if any)
        Optional<Transition<T, U>> triggeredTransitionOpt = getTriggeredTransition(stateIdentifier, entity, event);
        if (triggeredTransitionOpt.isPresent()) {
            Transition<T, U> triggeredTransition = triggeredTransitionOpt.get();
            logger.debug("transition triggered: entity={}, transition={}", entity.getId(), triggeredTransition.getName());

            // get next state
            State<T, U> nextState = states.get(triggeredTransition.getToState());
            if (nextState == null) {
                throw new IllegalStateException(String.format("next state unknown: state=%s", triggeredTransition.getToState()));
            }

            // execute action (if any)
            Optional<? extends Action<T>> actionOpt = triggeredTransition.getAction();
            if (actionOpt.isPresent()) {
                Action<T> action = actionOpt.get();
                logger.debug("executing action: entity={}, action={}", entity.getId(), action.getName());
                action.execute(entity, event);
            }

            // exit current state
            logger.debug("exiting state: entity={}, state={}", entity.getId(), currentState.getId());
            currentState.onExit(entity);

            // update entity
            entity.setState(nextState.getId());

            // enter next state
            logger.debug("entering state: entity={}, state={}", entity.getId(), nextState.getId());
            nextState.onEntry(entity);
        }
    }

    protected ImmutableMap<U, State<T, U>> createStatesMap(Set<State<T, U>> states) {
        Map<U, State<T, U>> map = Maps.newHashMap();
        for (State<T, U> state : states) {
            map.put(state.getId(), state);
        }
        return new ImmutableMap.Builder<U, State<T, U>>().putAll(map).build();
    }

    protected ImmutableMultimap<U, Transition<T, U>> createTransitionsFromMap(Set<Transition<T, U>> transitions) {
        Multimap<U, Transition<T, U>> map = ArrayListMultimap.create();
        for (Transition<T, U> transition : transitions) {
            map.put(transition.getFromState(), transition);
        }
        return new ImmutableMultimap.Builder<U, Transition<T, U>>().putAll(map).build();
    }

    protected Optional<Transition<T, U>> getTriggeredTransition(U stateIdentifier, T entity, Object event) {
        Collection<Transition<T, U>> transitionsFromCurrentState = transitionsFromState.get(stateIdentifier);

        Collection<Transition<T, U>> triggeredTransitions = Lists.newArrayList(Iterables.filter(transitionsFromCurrentState,
                new GuardIsAccepting<T, U>(entity, event)));
        if (triggeredTransitions.isEmpty()) {
            return Optional.absent();
        } else if (triggeredTransitions.size() == 1) {
            return Optional.of(triggeredTransitions.iterator().next());
        } else {
            return Optional.of(multipleTransitionsTriggeredResolver
                    .resolve(entity, event, triggeredTransitions));
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
            if(input.getGuard().isPresent()) {
                return input.getGuard().get().accept(entity, event);
            } else {
                return true;
            }
        }
    }
}
