package com.headstartech.scheelite.samples.calculator;

import com.headstartech.scheelite.StateAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by per on 22/02/14.
 */
public class InitState extends StateAdapter<CalculatorContext, CalculatorState> {

    private static final Logger logger = LoggerFactory.getLogger(InitState.class);

    @Override
    public CalculatorState getId() {
        return CalculatorState.INIT_STATE;
    }

    @Override
    public boolean onEvent(CalculatorContext context, Object event) {
        if(event instanceof DigitEvent) {
            DigitEvent ev = (DigitEvent) event;
            context.setOperand1(ev.getDigit());
            logger.info("Digit entered: digit={}", ev.getDigit());
            return true;
        }
        return false;
    }


}
