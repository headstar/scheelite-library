package com.headstartech.scheelite.samples.calculator;

import com.headstartech.scheelite.StateAdapter;

/**
 * Created by per on 20/02/14.
 */
public class OffState extends StateAdapter<CalculatorEntity, CalculatorState> {

    @Override
    public CalculatorState getId() {
        return CalculatorState.OFF;
    }
}
