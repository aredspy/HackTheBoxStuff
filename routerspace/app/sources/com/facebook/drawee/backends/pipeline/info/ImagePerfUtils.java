package com.facebook.drawee.backends.pipeline.info;

import androidx.core.os.EnvironmentCompat;
/* loaded from: classes.dex */
public class ImagePerfUtils {
    public static String toString(int imageLoadStatus) {
        return imageLoadStatus != 0 ? imageLoadStatus != 1 ? imageLoadStatus != 2 ? imageLoadStatus != 3 ? imageLoadStatus != 4 ? imageLoadStatus != 5 ? EnvironmentCompat.MEDIA_UNKNOWN : "error" : "canceled" : "success" : "intermediate_available" : "origin_available" : "requested";
    }

    private ImagePerfUtils() {
    }
}
