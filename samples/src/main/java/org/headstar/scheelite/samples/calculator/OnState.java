package org.headstar.scheelite.samples.calculator;

import org.headstar.scheelite.StateAdapter;

/**
 * Created by per on 20/02/14.
 */
public class OnState extends StateAdapter<CalculatorEntity, CalculatorState> {

    @Override
    public CalculatorState getId() {
        return CalculatorState.ON;
    }

    @Override
    public void onEntry(CalculatorEntity entity) {
        entity.clear();
    }

}
