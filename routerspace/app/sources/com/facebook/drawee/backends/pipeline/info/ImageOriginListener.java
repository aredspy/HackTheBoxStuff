package com.facebook.drawee.backends.pipeline.info;

import javax.annotation.Nullable;
/* loaded from: classes.dex */
public interface ImageOriginListener {
    void onImageLoaded(String controllerId, int imageOrigin, boolean successful, @Nullable String ultimateProducerName);
}
