package com.facebook.yoga;
/* loaded from: classes.dex */
public class YogaMeasureOutput {
    public static long make(float width, float height) {
        int floatToRawIntBits = Float.floatToRawIntBits(width);
        return Float.floatToRawIntBits(height) | (floatToRawIntBits << 32);
    }

    public static long make(int width, int height) {
        return make(width, height);
    }

    public static float getWidth(long measureOutput) {
        return Float.intBitsToFloat((int) ((measureOutput >> 32) & (-1)));
    }

    public static float getHeight(long measureOutput) {
        return Float.intBitsToFloat((int) (measureOutput & (-1)));
    }
}
