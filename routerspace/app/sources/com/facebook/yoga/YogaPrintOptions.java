package com.facebook.yoga;
/* loaded from: classes.dex */
public enum YogaPrintOptions {
    LAYOUT(1),
    STYLE(2),
    CHILDREN(4);
    
    private final int mIntValue;

    YogaPrintOptions(int intValue) {
        this.mIntValue = intValue;
    }

    public int intValue() {
        return this.mIntValue;
    }

    public static YogaPrintOptions fromInt(int value) {
        if (value != 1) {
            if (value == 2) {
                return STYLE;
            }
            if (value == 4) {
                return CHILDREN;
            }
            throw new IllegalArgumentException("Unknown enum value: " + value);
        }
        return LAYOUT;
    }
}
