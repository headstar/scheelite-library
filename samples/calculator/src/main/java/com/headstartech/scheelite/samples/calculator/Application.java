package com.headstartech.scheelite.samples.calculator;

import com.headstartech.scheelite.StateMachine;
import com.headstartech.scheelite.StateMachineBuilder;

/**
 * Created by per on 20/02/14.
 */
public class Application {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {

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
                .withTransition(initState, operand1State, DigitEvent.class)
                .withTransition(operand1State, opEnteredState, OperationEvent.class)
                .withTransition(opEnteredState, operand2State, DigitEvent.class)
                .withTransition(operand2State, resultState, ResultEvent.class)
                .withTransition(resultState, operand1State, DigitEvent.class)
                .withTransition(resultState, opEnteredState, OperationEvent.class)
                .withTransition(onState, offState, OffEvent.class)
                .build();


        CalculatorEntity entity = new CalculatorEntity();
        CalculatorState state = fsm.start(entity);
        state = fsm.processEvent(entity, state, new DigitEvent(7));
        state = fsm.processEvent(entity, state, new OperationEvent(Operation.ADDITION));
        state = fsm.processEvent(entity, state, new DigitEvent(4));
        state = fsm.processEvent(entity, state, new ResultEvent());
        state = fsm.processEvent(entity, state, new OperationEvent(Operation.SUBTRACTION));
        state = fsm.processEvent(entity, state, new DigitEvent(2));
        state = fsm.processEvent(entity, state, new ResultEvent());
        fsm.processEvent(entity, state, new OffEvent());
    }
}
