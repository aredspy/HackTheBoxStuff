package com.facebook.imagepipeline.transcoder;

import com.facebook.imageformat.ImageFormat;
/* loaded from: classes.dex */
public class SimpleImageTranscoderFactory implements ImageTranscoderFactory {
    private final int mMaxBitmapSize;

    public SimpleImageTranscoderFactory(final int maxBitmapSize) {
        this.mMaxBitmapSize = maxBitmapSize;
    }

    @Override // com.facebook.imagepipeline.transcoder.ImageTranscoderFactory
    public ImageTranscoder createImageTranscoder(ImageFormat imageFormat, boolean isResizingEnabled) {
        return new SimpleImageTranscoder(isResizingEnabled, this.mMaxBitmapSize);
    }
}
