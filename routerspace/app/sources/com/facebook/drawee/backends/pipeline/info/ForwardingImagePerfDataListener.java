package com.facebook.drawee.backends.pipeline.info;

import java.util.Collection;
/* loaded from: classes.dex */
public class ForwardingImagePerfDataListener implements ImagePerfDataListener {
    private final Collection<ImagePerfDataListener> mListeners;

    public ForwardingImagePerfDataListener(Collection<ImagePerfDataListener> listeners) {
        this.mListeners = listeners;
    }

    @Override // com.facebook.drawee.backends.pipeline.info.ImagePerfDataListener
    public void onImageLoadStatusUpdated(ImagePerfData imagePerfData, int imageLoadStatus) {
        for (ImagePerfDataListener imagePerfDataListener : this.mListeners) {
            imagePerfDataListener.onImageLoadStatusUpdated(imagePerfData, imageLoadStatus);
        }
    }

    @Override // com.facebook.drawee.backends.pipeline.info.ImagePerfDataListener
    public void onImageVisibilityUpdated(ImagePerfData imagePerfData, int visibilityState) {
        for (ImagePerfDataListener imagePerfDataListener : this.mListeners) {
            imagePerfDataListener.onImageVisibilityUpdated(imagePerfData, visibilityState);
        }
    }
}
