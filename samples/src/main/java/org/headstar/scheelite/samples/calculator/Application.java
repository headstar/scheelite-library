package org.headstar.scheelite.samples.calculator;

import com.google.common.base.Optional;
import org.headstar.scheelite.*;

import javax.sql.rowset.Predicate;

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
                .withTransition(initState, operand1State, eventInstanceOf(DigitEvent.class))
                .withTransition(operand1State, opEnteredState, eventInstanceOf(OperationEvent.class))
                .withTransition(opEnteredState, operand2State, eventInstanceOf(DigitEvent.class))
                .withTransition(operand2State, resultState, eventInstanceOf(ResultEvent.class))
                .withTransition(resultState, operand1State, eventInstanceOf(DigitEvent.class))
                .withTransition(resultState, opEnteredState, eventInstanceOf(OperationEvent.class))
                .withTransition(onState, offState, eventInstanceOf(OffEvent.class))
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

    private static Guard<CalculatorEntity> eventInstanceOf(Class<?> clazz) {
        return Guards.<CalculatorEntity>eventInstanceOf(clazz);
    }

}
