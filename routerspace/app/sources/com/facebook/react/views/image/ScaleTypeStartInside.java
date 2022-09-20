package com.facebook.react.views.image;

import android.graphics.Matrix;
import android.graphics.Rect;
import com.facebook.drawee.drawable.ScalingUtils;
/* loaded from: classes.dex */
public class ScaleTypeStartInside extends ScalingUtils.AbstractScaleType {
    public static final ScalingUtils.ScaleType INSTANCE = new ScaleTypeStartInside();

    public String toString() {
        return "start_inside";
    }

    @Override // com.facebook.drawee.drawable.ScalingUtils.AbstractScaleType
    public void getTransformImpl(Matrix outTransform, Rect parentRect, int childWidth, int childHeight, float focusX, float focusY, float scaleX, float scaleY) {
        float min = Math.min(Math.min(scaleX, scaleY), 1.0f);
        outTransform.setScale(min, min);
        outTransform.postTranslate((int) (parentRect.left + 0.5f), (int) (parentRect.top + 0.5f));
    }
}
