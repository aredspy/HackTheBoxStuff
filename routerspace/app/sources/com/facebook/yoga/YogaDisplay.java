package com.facebook.yoga;
/* loaded from: classes.dex */
public enum YogaDisplay {
    FLEX(0),
    NONE(1);
    
    private final int mIntValue;

    YogaDisplay(int intValue) {
        this.mIntValue = intValue;
    }

    public int intValue() {
        return this.mIntValue;
    }

    public static YogaDisplay fromInt(int value) {
        if (value != 0) {
            if (value == 1) {
                return NONE;
            }
            throw new IllegalArgumentException("Unknown enum value: " + value);
        }
        return FLEX;
    }
}
