package com.facebook.imagepipeline.transcoder;
/* loaded from: classes.dex */
public class ImageTranscodeResult {
    private final int mTranscodeStatus;

    public ImageTranscodeResult(int transcodeStatus) {
        this.mTranscodeStatus = transcodeStatus;
    }

    public int getTranscodeStatus() {
        return this.mTranscodeStatus;
    }

    public String toString() {
        return String.format(null, "Status: %d", Integer.valueOf(this.mTranscodeStatus));
    }
}
