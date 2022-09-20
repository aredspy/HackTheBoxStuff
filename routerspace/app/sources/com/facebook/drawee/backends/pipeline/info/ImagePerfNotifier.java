package com.facebook.drawee.backends.pipeline.info;
/* loaded from: classes.dex */
public interface ImagePerfNotifier {
    void notifyListenersOfVisibilityStateUpdate(ImagePerfState state, int visibilityState);

    void notifyStatusUpdated(ImagePerfState state, int imageLoadStatus);
}
