package com.facebook.yoga;
/* loaded from: classes.dex */
public enum YogaDirection {
    INHERIT(0),
    LTR(1),
    RTL(2);
    
    private final int mIntValue;

    YogaDirection(int intValue) {
        this.mIntValue = intValue;
    }

    public int intValue() {
        return this.mIntValue;
    }

    public static YogaDirection fromInt(int value) {
        if (value != 0) {
            if (value == 1) {
                return LTR;
            }
            if (value == 2) {
                return RTL;
            }
            throw new IllegalArgumentException("Unknown enum value: " + value);
        }
        return INHERIT;
    }
}
