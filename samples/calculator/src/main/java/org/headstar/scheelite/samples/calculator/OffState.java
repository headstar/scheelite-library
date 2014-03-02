package org.headstar.scheelite.samples.calculator;

import org.headstar.scheelite.StateAdapter;

/**
 * Created by per on 20/02/14.
 */
public class OffState extends StateAdapter<CalculatorEntity, CalculatorState> {

    @Override
    public CalculatorState getId() {
        return CalculatorState.OFF;
    }
}
