package com.facebook.react.views.view;

import androidx.core.view.ViewCompat;
/* loaded from: classes.dex */
public class ColorUtil {
    public static int getOpacityFromColor(int color) {
        int i = color >>> 24;
        if (i == 255) {
            return -1;
        }
        return i == 0 ? -2 : -3;
    }

    public static int multiplyColorAlpha(int color, int alpha) {
        if (alpha == 255) {
            return color;
        }
        if (alpha == 0) {
            return color & ViewCompat.MEASURED_SIZE_MASK;
        }
        int i = alpha + (alpha >> 7);
        return (color & ViewCompat.MEASURED_SIZE_MASK) | ((((color >>> 24) * i) >> 8) << 24);
    }
}
