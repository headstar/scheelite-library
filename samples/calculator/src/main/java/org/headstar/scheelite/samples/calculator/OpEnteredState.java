package org.headstar.scheelite.samples.calculator;

import org.headstar.scheelite.StateAdapter;

/**
 * Created by per on 22/02/14.
 */
public class OpEnteredState extends StateAdapter<CalculatorEntity, CalculatorState> {

    @Override
    public CalculatorState getId() {
        return CalculatorState.OP_ENTERED;
    }

    @Override
    public boolean onEvent(CalculatorEntity entity, Object event) {
        if(event instanceof DigitEvent) {
            DigitEvent ev = (DigitEvent) event;
            entity.setOperand2(ev.getDigit());
            return true;
        }
        return false;
    }

}

