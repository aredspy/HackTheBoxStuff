package com.facebook.imagepipeline.nativecode;

import com.facebook.common.internal.Closeables;
import com.facebook.common.internal.Preconditions;
import com.facebook.imageformat.DefaultImageFormats;
import com.facebook.imageformat.ImageFormat;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.transcoder.DownsampleUtil;
import com.facebook.imagepipeline.transcoder.ImageTranscodeResult;
import com.facebook.imagepipeline.transcoder.ImageTranscoder;
import com.facebook.imagepipeline.transcoder.JpegTranscoderUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class NativeJpegTranscoder implements ImageTranscoder {
    public static final String TAG = "NativeJpegTranscoder";
    private int mMaxBitmapSize;
    private boolean mResizingEnabled;
    private boolean mUseDownsamplingRatio;

    private static native void nativeTranscodeJpeg(InputStream inputStream, OutputStream outputStream, int rotationAngle, int scaleNominator, int quality) throws IOException;

    private static native void nativeTranscodeJpegWithExifOrientation(InputStream inputStream, OutputStream outputStream, int exifOrientation, int scaleNominator, int quality) throws IOException;

    @Override // com.facebook.imagepipeline.transcoder.ImageTranscoder
    public String getIdentifier() {
        return TAG;
    }

    public NativeJpegTranscoder(final boolean resizingEnabled, final int maxBitmapSize, final boolean useDownsamplingRatio, final boolean ensureTranscoderLibraryLoaded) {
        this.mResizingEnabled = resizingEnabled;
        this.mMaxBitmapSize = maxBitmapSize;
        this.mUseDownsamplingRatio = useDownsamplingRatio;
        if (ensureTranscoderLibraryLoaded) {
            NativeJpegTranscoderSoLoader.ensure();
        }
    }

    @Override // com.facebook.imagepipeline.transcoder.ImageTranscoder
    public boolean canResize(EncodedImage encodedImage, @Nullable RotationOptions rotationOptions, @Nullable ResizeOptions resizeOptions) {
        if (rotationOptions == null) {
            rotationOptions = RotationOptions.autoRotate();
        }
        return JpegTranscoderUtils.getSoftwareNumerator(rotationOptions, resizeOptions, encodedImage, this.mResizingEnabled) < 8;
    }

    @Override // com.facebook.imagepipeline.transcoder.ImageTranscoder
    public boolean canTranscode(ImageFormat imageFormat) {
        return imageFormat == DefaultImageFormats.JPEG;
    }

    /* JADX WARN: Finally extract failed */
    @Override // com.facebook.imagepipeline.transcoder.ImageTranscoder
    public ImageTranscodeResult transcode(final EncodedImage encodedImage, final OutputStream outputStream, @Nullable RotationOptions rotationOptions, @Nullable final ResizeOptions resizeOptions, @Nullable ImageFormat outputFormat, @Nullable Integer quality) throws IOException {
        if (quality == null) {
            quality = 85;
        }
        if (rotationOptions == null) {
            rotationOptions = RotationOptions.autoRotate();
        }
        int determineSampleSize = DownsampleUtil.determineSampleSize(rotationOptions, resizeOptions, encodedImage, this.mMaxBitmapSize);
        try {
            int softwareNumerator = JpegTranscoderUtils.getSoftwareNumerator(rotationOptions, resizeOptions, encodedImage, this.mResizingEnabled);
            int calculateDownsampleNumerator = JpegTranscoderUtils.calculateDownsampleNumerator(determineSampleSize);
            if (this.mUseDownsamplingRatio) {
                softwareNumerator = calculateDownsampleNumerator;
            }
            InputStream inputStream = encodedImage.getInputStream();
            if (JpegTranscoderUtils.INVERTED_EXIF_ORIENTATIONS.contains(Integer.valueOf(encodedImage.getExifOrientation()))) {
                transcodeJpegWithExifOrientation((InputStream) Preconditions.checkNotNull(inputStream, "Cannot transcode from null input stream!"), outputStream, JpegTranscoderUtils.getForceRotatedInvertedExifOrientation(rotationOptions, encodedImage), softwareNumerator, quality.intValue());
            } else {
                transcodeJpeg((InputStream) Preconditions.checkNotNull(inputStream, "Cannot transcode from null input stream!"), outputStream, JpegTranscoderUtils.getRotationAngle(rotationOptions, encodedImage), softwareNumerator, quality.intValue());
            }
            Closeables.closeQuietly(inputStream);
            int i = 1;
            if (determineSampleSize != 1) {
                i = 0;
            }
            return new ImageTranscodeResult(i);
        } catch (Throwable th) {
            Closeables.closeQuietly((InputStream) null);
            throw th;
        }
    }

    public static void transcodeJpeg(final InputStream inputStream, final OutputStream outputStream, final int rotationAngle, final int scaleNumerator, final int quality) throws IOException {
        NativeJpegTranscoderSoLoader.ensure();
        boolean z = false;
        Preconditions.checkArgument(Boolean.valueOf(scaleNumerator >= 1));
        Preconditions.checkArgument(Boolean.valueOf(scaleNumerator <= 16));
        Preconditions.checkArgument(Boolean.valueOf(quality >= 0));
        Preconditions.checkArgument(Boolean.valueOf(quality <= 100));
        Preconditions.checkArgument(Boolean.valueOf(JpegTranscoderUtils.isRotationAngleAllowed(rotationAngle)));
        if (scaleNumerator != 8 || rotationAngle != 0) {
            z = true;
        }
        Preconditions.checkArgument(z, "no transformation requested");
        nativeTranscodeJpeg((InputStream) Preconditions.checkNotNull(inputStream), (OutputStream) Preconditions.checkNotNull(outputStream), rotationAngle, scaleNumerator, quality);
    }

    public static void transcodeJpegWithExifOrientation(final InputStream inputStream, final OutputStream outputStream, final int exifOrientation, final int scaleNumerator, final int quality) throws IOException {
        NativeJpegTranscoderSoLoader.ensure();
        boolean z = false;
        Preconditions.checkArgument(Boolean.valueOf(scaleNumerator >= 1));
        Preconditions.checkArgument(Boolean.valueOf(scaleNumerator <= 16));
        Preconditions.checkArgument(Boolean.valueOf(quality >= 0));
        Preconditions.checkArgument(Boolean.valueOf(quality <= 100));
        Preconditions.checkArgument(Boolean.valueOf(JpegTranscoderUtils.isExifOrientationAllowed(exifOrientation)));
        if (scaleNumerator != 8 || exifOrientation != 1) {
            z = true;
        }
        Preconditions.checkArgument(z, "no transformation requested");
        nativeTranscodeJpegWithExifOrientation((InputStream) Preconditions.checkNotNull(inputStream), (OutputStream) Preconditions.checkNotNull(outputStream), exifOrientation, scaleNumerator, quality);
    }
}
