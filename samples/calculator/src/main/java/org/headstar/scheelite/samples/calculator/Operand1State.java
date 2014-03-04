package org.headstar.scheelite.samples.calculator;

import org.headstar.scheelite.StateAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by per on 20/02/14.
 */
public class Operand1State extends StateAdapter<CalculatorEntity, CalculatorState> {

    private static final Logger logger = LoggerFactory.getLogger(Operand1State.class);

    @Override
    public CalculatorState getId() {
        return CalculatorState.OPERAND1;
    }

    @Override
    public boolean onEvent(CalculatorEntity entity, Object event) {
        if(event instanceof OperationEvent) {
            OperationEvent ev = (OperationEvent) event;
            entity.setOp(ev.getOp());
            logger.info("Operation entered: operation={}", ev.getOp().name());
            return true;
        }
        return false;
    }
}
