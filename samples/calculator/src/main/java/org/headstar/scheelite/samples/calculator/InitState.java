package org.headstar.scheelite.samples.calculator;

import org.headstar.scheelite.StateAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by per on 22/02/14.
 */
public class InitState extends StateAdapter<CalculatorEntity, CalculatorState> {

    private static final Logger logger = LoggerFactory.getLogger(InitState.class);

    @Override
    public CalculatorState getId() {
        return CalculatorState.INIT_STATE;
    }

    @Override
    public boolean onEvent(CalculatorEntity entity, Object event) {
        if(event instanceof DigitEvent) {
            DigitEvent ev = (DigitEvent) event;
            entity.setOperand1(ev.getDigit());
            logger.info("Digit entered: digit={}", ev.getDigit());
            return true;
        }
        return false;
    }


}
