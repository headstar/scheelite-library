package com.headstartech.scheelite.samples.calculator;

import com.headstartech.scheelite.StateAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by per on 20/02/14.
 */
public class ResultState extends StateAdapter<CalculatorContext, CalculatorState> {

    private static final Logger logger = LoggerFactory.getLogger(ResultState.class);

    @Override
    public CalculatorState getId() {
        return CalculatorState.RESULT;
    }

    @Override
    public void onEntry(CalculatorContext context) {
        logger.info("Result: operation={}", context.getResult());
    }

    @Override
    public boolean onEvent(CalculatorContext context, Object event) {
        if(event instanceof OperationEvent) {
            OperationEvent ev = (OperationEvent) event;
            context.setOp(ev.getOp());
            context.setOperand1(context.getResult());
            logger.info("Operation entered: operation={}", ev.getOp().name());
            return true;
        } else if(event instanceof DigitEvent) {
            DigitEvent ev = (DigitEvent) event;
            context.setOperand1(ev.getDigit());
            logger.info("Digit entered: digit={}", ev.getDigit());
            return true;
        }
        return false;
    }
}