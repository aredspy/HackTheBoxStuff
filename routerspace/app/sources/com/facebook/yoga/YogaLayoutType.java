package com.facebook.yoga;
/* loaded from: classes.dex */
public enum YogaLayoutType {
    LAYOUT(0),
    MEASURE(1),
    CACHED_LAYOUT(2),
    CACHED_MEASURE(3);
    
    private final int mIntValue;

    YogaLayoutType(int intValue) {
        this.mIntValue = intValue;
    }

    public int intValue() {
        return this.mIntValue;
    }

    public static YogaLayoutType fromInt(int value) {
        if (value != 0) {
            if (value == 1) {
                return MEASURE;
            }
            if (value == 2) {
                return CACHED_LAYOUT;
            }
            if (value == 3) {
                return CACHED_MEASURE;
            }
            throw new IllegalArgumentException("Unknown enum value: " + value);
        }
        return LAYOUT;
    }
}
