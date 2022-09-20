package com.facebook.yoga;
/* loaded from: classes.dex */
public enum YogaFlexDirection {
    COLUMN(0),
    COLUMN_REVERSE(1),
    ROW(2),
    ROW_REVERSE(3);
    
    private final int mIntValue;

    YogaFlexDirection(int intValue) {
        this.mIntValue = intValue;
    }

    public int intValue() {
        return this.mIntValue;
    }

    public static YogaFlexDirection fromInt(int value) {
        if (value != 0) {
            if (value == 1) {
                return COLUMN_REVERSE;
            }
            if (value == 2) {
                return ROW;
            }
            if (value == 3) {
                return ROW_REVERSE;
            }
            throw new IllegalArgumentException("Unknown enum value: " + value);
        }
        return COLUMN;
    }
}
