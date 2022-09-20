package com.facebook.yoga;
/* loaded from: classes.dex */
public enum YogaPositionType {
    STATIC(0),
    RELATIVE(1),
    ABSOLUTE(2);
    
    private final int mIntValue;

    YogaPositionType(int intValue) {
        this.mIntValue = intValue;
    }

    public int intValue() {
        return this.mIntValue;
    }

    public static YogaPositionType fromInt(int value) {
        if (value != 0) {
            if (value == 1) {
                return RELATIVE;
            }
            if (value == 2) {
                return ABSOLUTE;
            }
            throw new IllegalArgumentException("Unknown enum value: " + value);
        }
        return STATIC;
    }
}
