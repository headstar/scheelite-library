package com.headstartech.scheelite.samples.calculator;

import com.headstartech.scheelite.StateAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by per on 20/02/14.
 */
public class Operand2State extends StateAdapter<CalculatorContext, CalculatorState> {

    private static final Logger logger = LoggerFactory.getLogger(Operand2State.class);

    @Override
    public CalculatorState getId() {
        return CalculatorState.OPERAND2;
    }

    @Override
    public boolean onEvent(CalculatorContext context, Object event) {
        if(event instanceof ResultEvent) {
            Integer op1 = context.getOperand1();
            Integer op2 = context.getOperand2();
            Operation op = context.getOp();
            switch(op) {
                case ADDITION:
                    context.setResult(op1 + op2);
                    break;
                case SUBTRACTION:
                    context.setResult(op1 - op2);
                    break;
            }
            logger.info("Result requested");
            return true;
        }
        return false;
    }
}