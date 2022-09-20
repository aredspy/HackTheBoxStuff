package com.facebook.drawee.controller;

import android.graphics.drawable.Animatable;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public interface ControllerListener<INFO> {
    void onFailure(String id, Throwable throwable);

    void onFinalImageSet(String id, @Nullable INFO imageInfo, @Nullable Animatable animatable);

    void onIntermediateImageFailed(String id, Throwable throwable);

    void onIntermediateImageSet(String id, @Nullable INFO imageInfo);

    void onRelease(String id);

    void onSubmit(String id, Object callerContext);
}
