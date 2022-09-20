package com.facebook.yoga;
/* loaded from: classes.dex */
public enum YogaJustify {
    FLEX_START(0),
    CENTER(1),
    FLEX_END(2),
    SPACE_BETWEEN(3),
    SPACE_AROUND(4),
    SPACE_EVENLY(5);
    
    private final int mIntValue;

    YogaJustify(int intValue) {
        this.mIntValue = intValue;
    }

    public int intValue() {
        return this.mIntValue;
    }

    public static YogaJustify fromInt(int value) {
        if (value != 0) {
            if (value == 1) {
                return CENTER;
            }
            if (value == 2) {
                return FLEX_END;
            }
            if (value == 3) {
                return SPACE_BETWEEN;
            }
            if (value == 4) {
                return SPACE_AROUND;
            }
            if (value == 5) {
                return SPACE_EVENLY;
            }
            throw new IllegalArgumentException("Unknown enum value: " + value);
        }
        return FLEX_START;
    }
}
