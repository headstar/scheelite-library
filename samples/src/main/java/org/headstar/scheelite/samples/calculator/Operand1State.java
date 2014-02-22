package org.headstar.scheelite.samples.calculator;

import org.headstar.scheelite.StateAdapter;

/**
 * Created by per on 20/02/14.
 */
public class Operand1State extends StateAdapter<CalculatorEntity, CalculatorState> {

    @Override
    public CalculatorState getId() {
        return CalculatorState.OPERAND1;
    }

    @Override
    public boolean onEvent(CalculatorEntity entity, Object event) {
        if(event instanceof OperationEvent) {
            OperationEvent ev = (OperationEvent) event;
            entity.setOp(ev.getOp());
            return true;
        }
        return false;
    }
}
