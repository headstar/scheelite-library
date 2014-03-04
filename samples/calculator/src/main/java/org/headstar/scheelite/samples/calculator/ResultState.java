package org.headstar.scheelite.samples.calculator;

import org.headstar.scheelite.StateAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by per on 20/02/14.
 */
public class ResultState extends StateAdapter<CalculatorEntity, CalculatorState> {

    private static final Logger logger = LoggerFactory.getLogger(ResultState.class);

    @Override
    public CalculatorState getId() {
        return CalculatorState.RESULT;
    }

    @Override
    public void onEntry(CalculatorEntity entity) {
        logger.info("Result: operation={}", entity.getResult());
    }

    @Override
    public boolean onEvent(CalculatorEntity entity, Object event) {
        if(event instanceof OperationEvent) {
            OperationEvent ev = (OperationEvent) event;
            entity.setOp(ev.getOp());
            entity.setOperand1(entity.getResult());
            logger.info("Operation entered: operation={}", ev.getOp().name());
            return true;
        } else if(event instanceof DigitEvent) {
            DigitEvent ev = (DigitEvent) event;
            entity.setOperand1(ev.getDigit());
            logger.info("Digit entered: digit={}", ev.getDigit());
            return true;
        }
        return false;
    }
}