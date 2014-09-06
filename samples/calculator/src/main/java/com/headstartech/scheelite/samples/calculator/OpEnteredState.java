package com.headstartech.scheelite.samples.calculator;

import com.headstartech.scheelite.StateAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by per on 22/02/14.
 */
public class OpEnteredState extends StateAdapter<CalculatorEntity, CalculatorState> {

    private static final Logger logger = LoggerFactory.getLogger(OpEnteredState.class);

    @Override
    public CalculatorState getId() {
        return CalculatorState.OP_ENTERED;
    }

    @Override
    public boolean onEvent(CalculatorEntity entity, Object event) {
        if(event instanceof DigitEvent) {
            DigitEvent ev = (DigitEvent) event;
            entity.setOperand2(ev.getDigit());
            logger.info("Digit entered: digit={}", ev.getDigit());
            return true;
        }
        return false;
    }

}

