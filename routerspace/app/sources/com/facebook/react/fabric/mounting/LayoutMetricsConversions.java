package com.facebook.react.fabric.mounting;

import android.view.View;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.yoga.YogaMeasureMode;
/* loaded from: classes.dex */
public class LayoutMetricsConversions {
    public static float getMinSize(int viewMeasureSpec) {
        int mode = View.MeasureSpec.getMode(viewMeasureSpec);
        int size = View.MeasureSpec.getSize(viewMeasureSpec);
        if (mode == 1073741824) {
            return size;
        }
        return 0.0f;
    }

    public static float getMaxSize(int viewMeasureSpec) {
        int mode = View.MeasureSpec.getMode(viewMeasureSpec);
        int size = View.MeasureSpec.getSize(viewMeasureSpec);
        if (mode == 0) {
            return Float.POSITIVE_INFINITY;
        }
        return size;
    }

    public static float getYogaSize(float minSize, float maxSize) {
        if (minSize == maxSize) {
            return PixelUtil.toPixelFromDIP(maxSize);
        }
        if (!Float.isInfinite(maxSize)) {
            return PixelUtil.toPixelFromDIP(maxSize);
        }
        return Float.POSITIVE_INFINITY;
    }

    public static YogaMeasureMode getYogaMeasureMode(float minSize, float maxSize) {
        if (minSize == maxSize) {
            return YogaMeasureMode.EXACTLY;
        }
        if (Float.isInfinite(maxSize)) {
            return YogaMeasureMode.UNDEFINED;
        }
        return YogaMeasureMode.AT_MOST;
    }
}
