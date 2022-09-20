package com.facebook.yoga;
/* loaded from: classes.dex */
public class YogaConstants {
    public static final float UNDEFINED = Float.NaN;

    public static float getUndefined() {
        return Float.NaN;
    }

    public static boolean isUndefined(float value) {
        return Float.compare(value, Float.NaN) == 0;
    }

    public static boolean isUndefined(YogaValue value) {
        return value.unit == YogaUnit.UNDEFINED;
    }
}
