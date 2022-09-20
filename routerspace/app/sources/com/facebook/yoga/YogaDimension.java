package com.facebook.yoga;
/* loaded from: classes.dex */
public enum YogaDimension {
    WIDTH(0),
    HEIGHT(1);
    
    private final int mIntValue;

    YogaDimension(int intValue) {
        this.mIntValue = intValue;
    }

    public int intValue() {
        return this.mIntValue;
    }

    public static YogaDimension fromInt(int value) {
        if (value != 0) {
            if (value == 1) {
                return HEIGHT;
            }
            throw new IllegalArgumentException("Unknown enum value: " + value);
        }
        return WIDTH;
    }
}
