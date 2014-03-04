package org.headstar.scheelite.samples.calculator;

import org.headstar.scheelite.StateAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by per on 20/02/14.
 */
public class Operand2State extends StateAdapter<CalculatorEntity, CalculatorState> {

    private static final Logger logger = LoggerFactory.getLogger(Operand2State.class);

    @Override
    public CalculatorState getId() {
        return CalculatorState.OPERAND2;
    }

    @Override
    public boolean onEvent(CalculatorEntity entity, Object event) {
        if(event instanceof ResultEvent) {
            Integer op1 = entity.getOperand1();
            Integer op2 = entity.getOperand2();
            Operation op = entity.getOp();
            switch(op) {
                case ADDITION:
                    entity.setResult(op1 + op2);
                    break;
                case SUBTRACTION:
                    entity.setResult(op1 - op2);
                    break;
            }
            logger.info("Result requested");
            return true;
        }
        return false;
    }
}