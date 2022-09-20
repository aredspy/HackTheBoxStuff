package com.facebook.yoga;
/* loaded from: classes.dex */
public enum YogaMeasureMode {
    UNDEFINED(0),
    EXACTLY(1),
    AT_MOST(2);
    
    private final int mIntValue;

    YogaMeasureMode(int intValue) {
        this.mIntValue = intValue;
    }

    public int intValue() {
        return this.mIntValue;
    }

    public static YogaMeasureMode fromInt(int value) {
        if (value != 0) {
            if (value == 1) {
                return EXACTLY;
            }
            if (value == 2) {
                return AT_MOST;
            }
            throw new IllegalArgumentException("Unknown enum value: " + value);
        }
        return UNDEFINED;
    }
}
