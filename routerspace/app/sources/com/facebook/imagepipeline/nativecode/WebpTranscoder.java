package com.facebook.imagepipeline.nativecode;

import com.facebook.imageformat.ImageFormat;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
/* loaded from: classes.dex */
public interface WebpTranscoder {
    boolean isWebpNativelySupported(ImageFormat webpFormat);

    void transcodeWebpToJpeg(InputStream inputStream, OutputStream outputStream, int quality) throws IOException;

    void transcodeWebpToPng(InputStream inputStream, OutputStream outputStream) throws IOException;
}
