package com.facebook.react.uimanager;

import android.util.DisplayMetrics;
import android.util.TypedValue;
/* loaded from: classes.dex */
public class PixelUtil {
    public static float toPixelFromDIP(float value) {
        return TypedValue.applyDimension(1, value, DisplayMetricsHolder.getWindowDisplayMetrics());
    }

    public static float toPixelFromDIP(double value) {
        return toPixelFromDIP((float) value);
    }

    public static float toPixelFromSP(float value) {
        return toPixelFromSP(value, Float.NaN);
    }

    public static float toPixelFromSP(float value, float maxFontScale) {
        DisplayMetrics windowDisplayMetrics = DisplayMetricsHolder.getWindowDisplayMetrics();
        float f = windowDisplayMetrics.scaledDensity;
        float f2 = f / windowDisplayMetrics.density;
        if (maxFontScale >= 1.0f && maxFontScale < f2) {
            f = windowDisplayMetrics.density * maxFontScale;
        }
        return value * f;
    }

    public static float toPixelFromSP(double value) {
        return toPixelFromSP((float) value);
    }

    public static float toDIPFromPixel(float value) {
        return value / DisplayMetricsHolder.getWindowDisplayMetrics().density;
    }

    public static float getDisplayMetricDensity() {
        return DisplayMetricsHolder.getWindowDisplayMetrics().density;
    }
}
