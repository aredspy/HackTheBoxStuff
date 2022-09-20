package com.facebook.yoga;
/* loaded from: classes.dex */
public enum YogaOverflow {
    VISIBLE(0),
    HIDDEN(1),
    SCROLL(2);
    
    private final int mIntValue;

    YogaOverflow(int intValue) {
        this.mIntValue = intValue;
    }

    public int intValue() {
        return this.mIntValue;
    }

    public static YogaOverflow fromInt(int value) {
        if (value != 0) {
            if (value == 1) {
                return HIDDEN;
            }
            if (value == 2) {
                return SCROLL;
            }
            throw new IllegalArgumentException("Unknown enum value: " + value);
        }
        return VISIBLE;
    }
}
