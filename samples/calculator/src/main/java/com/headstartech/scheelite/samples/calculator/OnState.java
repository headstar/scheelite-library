package com.headstartech.scheelite.samples.calculator;

import com.headstartech.scheelite.StateAdapter;

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
