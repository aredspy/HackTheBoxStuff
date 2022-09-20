package com.facebook.imagepipeline.nativecode;

import android.graphics.Bitmap;
import com.facebook.common.internal.Preconditions;
/* loaded from: classes.dex */
public class NativeBlurFilter {
    private static native void nativeIterativeBoxBlur(Bitmap bitmap, int iterations, int blurRadius);

    static {
        NativeFiltersLoader.load();
    }

    public static void iterativeBoxBlur(Bitmap bitmap, int iterations, int blurRadius) {
        Preconditions.checkNotNull(bitmap);
        boolean z = true;
        Preconditions.checkArgument(Boolean.valueOf(iterations > 0));
        if (blurRadius <= 0) {
            z = false;
        }
        Preconditions.checkArgument(Boolean.valueOf(z));
        nativeIterativeBoxBlur(bitmap, iterations, blurRadius);
    }
}
