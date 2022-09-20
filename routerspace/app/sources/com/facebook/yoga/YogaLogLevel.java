package com.facebook.yoga;
/* loaded from: classes.dex */
public enum YogaLogLevel {
    ERROR(0),
    WARN(1),
    INFO(2),
    DEBUG(3),
    VERBOSE(4),
    FATAL(5);
    
    private final int mIntValue;

    YogaLogLevel(int intValue) {
        this.mIntValue = intValue;
    }

    public int intValue() {
        return this.mIntValue;
    }

    public static YogaLogLevel fromInt(int value) {
        if (value != 0) {
            if (value == 1) {
                return WARN;
            }
            if (value == 2) {
                return INFO;
            }
            if (value == 3) {
                return DEBUG;
            }
            if (value == 4) {
                return VERBOSE;
            }
            if (value == 5) {
                return FATAL;
            }
            throw new IllegalArgumentException("Unknown enum value: " + value);
        }
        return ERROR;
    }
}
