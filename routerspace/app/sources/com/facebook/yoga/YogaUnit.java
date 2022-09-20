package com.facebook.yoga;
/* loaded from: classes.dex */
public enum YogaUnit {
    UNDEFINED(0),
    POINT(1),
    PERCENT(2),
    AUTO(3);
    
    private final int mIntValue;

    YogaUnit(int intValue) {
        this.mIntValue = intValue;
    }

    public int intValue() {
        return this.mIntValue;
    }

    public static YogaUnit fromInt(int value) {
        if (value != 0) {
            if (value == 1) {
                return POINT;
            }
            if (value == 2) {
                return PERCENT;
            }
            if (value == 3) {
                return AUTO;
            }
            throw new IllegalArgumentException("Unknown enum value: " + value);
        }
        return UNDEFINED;
    }
}
