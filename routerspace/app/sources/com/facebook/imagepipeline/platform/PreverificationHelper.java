package com.facebook.imagepipeline.platform;

import android.graphics.Bitmap;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
class PreverificationHelper {
    public boolean shouldUseHardwareBitmapConfig(@Nullable Bitmap.Config config) {
        return config == Bitmap.Config.HARDWARE;
    }
}
