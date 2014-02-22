package org.headstar.scheelite.samples.calculator;

/**
 * Created by per on 20/02/14.
 */
public class OperationEvent {

    private final Operation op;

    public OperationEvent(Operation op) {
        this.op = op;
    }

    public Operation getOp() {
        return op;
    }
}
