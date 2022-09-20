package com.facebook.imagepipeline.decoder;

import com.facebook.imagepipeline.image.EncodedImage;
/* loaded from: classes.dex */
public class DecodeException extends RuntimeException {
    private final EncodedImage mEncodedImage;

    public DecodeException(String message, EncodedImage encodedImage) {
        super(message);
        this.mEncodedImage = encodedImage;
    }

    public DecodeException(String message, Throwable t, EncodedImage encodedImage) {
        super(message, t);
        this.mEncodedImage = encodedImage;
    }

    public EncodedImage getEncodedImage() {
        return this.mEncodedImage;
    }
}
