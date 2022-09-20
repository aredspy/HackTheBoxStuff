package com.facebook.react.uimanager;
/* loaded from: classes.dex */
public class FloatUtil {
    private static final float EPSILON = 1.0E-5f;

    public static boolean floatsEqual(float f1, float f2) {
        return (Float.isNaN(f1) || Float.isNaN(f2)) ? Float.isNaN(f1) && Float.isNaN(f2) : Math.abs(f2 - f1) < EPSILON;
    }
}
