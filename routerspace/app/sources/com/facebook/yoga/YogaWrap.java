package com.facebook.yoga;
/* loaded from: classes.dex */
public enum YogaWrap {
    NO_WRAP(0),
    WRAP(1),
    WRAP_REVERSE(2);
    
    private final int mIntValue;

    YogaWrap(int intValue) {
        this.mIntValue = intValue;
    }

    public int intValue() {
        return this.mIntValue;
    }

    public static YogaWrap fromInt(int value) {
        if (value != 0) {
            if (value == 1) {
                return WRAP;
            }
            if (value == 2) {
                return WRAP_REVERSE;
            }
            throw new IllegalArgumentException("Unknown enum value: " + value);
        }
        return NO_WRAP;
    }
}
