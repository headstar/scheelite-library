package com.headstartech.scheelite.samples.calculator;


/**
 * Created by per on 20/02/14.
 */
public class CalculatorContext {

    private Integer operand1;
    private Integer operand2;
    private Integer result;
    private Operation op;

    CalculatorContext() {
    }

    void clear() {
        operand1 = null;
        operand2 = null;
        op = null;
    }

    public Integer getOperand1() {
        return operand1;
    }

    public void setOperand1(Integer operand1) {
        this.operand1 = operand1;
    }

    public Integer getOperand2() {
        return operand2;
    }

    public void setOperand2(Integer operand2) {
        this.operand2 = operand2;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public Operation getOp() {
        return op;
    }

    public void setOp(Operation op) {
        this.op = op;
    }

    @Override
    public String toString() {
        return "CalculatorEntity{}";
    }
}
