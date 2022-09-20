package com.facebook.imagepipeline.nativecode;

import android.graphics.Bitmap;
import com.facebook.common.internal.Preconditions;
/* loaded from: classes.dex */
public class Bitmaps {
    private static native void nativeCopyBitmap(Bitmap dest, int destStride, Bitmap src, int srcStride, int rows);

    static {
        ImagePipelineNativeLoader.load();
    }

    public static void copyBitmap(Bitmap dest, Bitmap src) {
        boolean z = true;
        Preconditions.checkArgument(Boolean.valueOf(src.getConfig() == dest.getConfig()));
        Preconditions.checkArgument(Boolean.valueOf(dest.isMutable()));
        Preconditions.checkArgument(Boolean.valueOf(dest.getWidth() == src.getWidth()));
        if (dest.getHeight() != src.getHeight()) {
            z = false;
        }
        Preconditions.checkArgument(Boolean.valueOf(z));
        nativeCopyBitmap(dest, dest.getRowBytes(), src, src.getRowBytes(), dest.getHeight());
    }
}
