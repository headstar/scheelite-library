package org.headstar.scheelite.samples.calculator;

import com.google.common.base.Optional;
import org.headstar.scheelite.Guard;
import org.headstar.scheelite.StateMachine;
import org.headstar.scheelite.StateMachineBuilder;

/**
 * Created by per on 20/02/14.
 */
public class Application {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {

        OnState onState = new OnState();
        InitState initState = new InitState();
        Operand1State operand1State = new Operand1State();
        Operand2State operand2State = new Operand2State();
        OpEnteredState opEnteredState = new OpEnteredState();
        ResultState resultState = new ResultState();
        OffState offState = new OffState();

        StateMachineBuilder<CalculatorEntity, CalculatorState> fsmBuilder = StateMachineBuilder.<CalculatorEntity, CalculatorState>newBuilder();
        StateMachine<CalculatorEntity, CalculatorState> fsm = fsmBuilder
                .withInitialTransition(onState)
                .withCompositeState(onState, initState, operand1State, operand2State, opEnteredState, resultState)
                .withTransition(initState, operand1State, new AcceptingInstanceOf(DigitEvent.class))
                .withTransition(operand1State, opEnteredState, new AcceptingInstanceOf(OperationEvent.class))
                .withTransition(opEnteredState, operand2State, new AcceptingInstanceOf(DigitEvent.class))
                .withTransition(operand2State, resultState, new AcceptingInstanceOf(ResultEvent.class))
                .withTransition(resultState, operand1State, new AcceptingInstanceOf(DigitEvent.class))
                .withTransition(resultState, opEnteredState, new AcceptingInstanceOf(OperationEvent.class))
                .withTransition(onState, offState, new AcceptingInstanceOf(OffEvent.class))
                .build();


        CalculatorEntity entity = new CalculatorEntity();
        CalculatorState state = fsm.processInitialTransition(entity);
        state = fsm.processEvent(entity, state, new DigitEvent(7));
        state = fsm.processEvent(entity, state, new OperationEvent(Operation.ADDITION));
        state = fsm.processEvent(entity, state, new DigitEvent(4));
        state = fsm.processEvent(entity, state, new ResultEvent());
        state = fsm.processEvent(entity, state, new OperationEvent(Operation.SUBTRACTION));
        state = fsm.processEvent(entity, state, new DigitEvent(2));
        state = fsm.processEvent(entity, state, new ResultEvent());
        fsm.processEvent(entity, state, new OffEvent());


    }

    private static class AcceptingInstanceOf implements Guard<CalculatorEntity> {

        private final Class<?> clazz;

        AcceptingInstanceOf(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public String getName() {
            return this.getClass().getSimpleName();
        }

        @Override
        public boolean accept(CalculatorEntity entity, Optional<?> event) {
           return event.isPresent() && clazz.isInstance(event.get());
        }
    }
}
