package com.facebook.yoga;
/* loaded from: classes.dex */
public enum YogaNodeType {
    DEFAULT(0),
    TEXT(1);
    
    private final int mIntValue;

    YogaNodeType(int intValue) {
        this.mIntValue = intValue;
    }

    public int intValue() {
        return this.mIntValue;
    }

    public static YogaNodeType fromInt(int value) {
        if (value != 0) {
            if (value == 1) {
                return TEXT;
            }
            throw new IllegalArgumentException("Unknown enum value: " + value);
        }
        return DEFAULT;
    }
}
