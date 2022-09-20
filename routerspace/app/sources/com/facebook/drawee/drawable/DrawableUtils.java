package com.facebook.drawee.drawable;

import android.graphics.drawable.Drawable;
import androidx.core.view.ViewCompat;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class DrawableUtils {
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

    @Nullable
    public static Drawable cloneDrawable(Drawable drawable) {
        if (drawable instanceof CloneableDrawable) {
            return ((CloneableDrawable) drawable).cloneDrawable();
        }
        Drawable.ConstantState constantState = drawable.getConstantState();
        if (constantState == null) {
            return null;
        }
        return constantState.newDrawable();
    }

    public static void copyProperties(@Nullable Drawable to, @Nullable Drawable from) {
        if (from == null || to == null || to == from) {
            return;
        }
        to.setBounds(from.getBounds());
        to.setChangingConfigurations(from.getChangingConfigurations());
        to.setLevel(from.getLevel());
        to.setVisible(from.isVisible(), false);
        to.setState(from.getState());
    }

    public static void setDrawableProperties(@Nullable Drawable drawable, @Nullable DrawableProperties properties) {
        if (drawable == null || properties == null) {
            return;
        }
        properties.applyTo(drawable);
    }

    public static void setCallbacks(@Nullable Drawable drawable, @Nullable Drawable.Callback callback, @Nullable TransformCallback transformCallback) {
        if (drawable != null) {
            drawable.setCallback(callback);
            if (!(drawable instanceof TransformAwareDrawable)) {
                return;
            }
            ((TransformAwareDrawable) drawable).setTransformCallback(transformCallback);
        }
    }
}
