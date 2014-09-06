package com.headstartech.scheelite.samples.calculator;

/**
 * Created by per on 20/02/14.
 */
public class DigitEvent {

    private final Integer digit;

    public DigitEvent(Integer digit) {
        this.digit = digit;
    }

    public Integer getDigit() {
        return digit;
    }
}
