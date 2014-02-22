package org.headstar.scheelite.samples.calculator;

import com.google.common.base.Optional;
import org.headstar.scheelite.Guard;
import org.headstar.scheelite.StateMachine;
import org.headstar.scheelite.StateMachineBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by per on 20/02/14.
 */
public class Application {

    public static void main(String[] args) {

        OnState onState = new OnState();
        InitState initState = new InitState();
        Operand1State operand1State = new Operand1State();
        Operand2State operand2State = new Operand2State();
        OpEnteredState opEnteredState = new OpEnteredState();
        ResultState resultState = new ResultState();
        OffState offState = new OffState();

        StateMachineBuilder<CalculatorEntity, CalculatorState> fsmBuilder = StateMachineBuilder.<CalculatorEntity, CalculatorState>newBuilder();
        StateMachine<CalculatorEntity> fsm = fsmBuilder
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
        fsm.initialTransition(entity);
        fsm.processEvent(entity, new DigitEvent(7));
        fsm.processEvent(entity, new OperationEvent(Operation.ADDITION));
        fsm.processEvent(entity, new DigitEvent(4));
        fsm.processEvent(entity, new ResultEvent());
        fsm.processEvent(entity, new OperationEvent(Operation.SUBTRACTION));
        fsm.processEvent(entity, new DigitEvent(2));
        fsm.processEvent(entity, new ResultEvent());
        fsm.processEvent(entity, new OffEvent());


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
