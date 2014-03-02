package org.headstar.scheelite.samples.calculator;

import org.headstar.scheelite.StateAdapter;

/**
 * Created by per on 20/02/14.
 */
public class ResultState extends StateAdapter<CalculatorEntity, CalculatorState> {

    @Override
    public CalculatorState getId() {
        return CalculatorState.RESULT;
    }

    @Override
    public void onEntry(CalculatorEntity entity) {
        System.out.println(entity.getResult());
    }

    @Override
    public boolean onEvent(CalculatorEntity entity, Object event) {
        if(event instanceof OperationEvent) {
            OperationEvent ev = (OperationEvent) event;
            entity.setOp(ev.getOp());
            entity.setOperand1(entity.getResult());
            return true;
        } else if(event instanceof DigitEvent) {
            DigitEvent ev = (DigitEvent) event;
            entity.setOperand1(ev.getDigit());
            return true;
        }
        return false;
    }
}