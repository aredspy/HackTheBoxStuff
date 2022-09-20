package com.facebook.imagepipeline.transcoder;

import com.facebook.common.internal.Preconditions;
import com.facebook.common.logging.FLog;
import com.facebook.imageformat.DefaultImageFormats;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.image.EncodedImage;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class DownsampleUtil {
    public static final int DEFAULT_SAMPLE_SIZE = 1;
    private static final float INTERVAL_ROUNDING = 0.33333334f;

    public static int ratioToSampleSizeJPEG(final float ratio) {
        if (ratio > 0.6666667f) {
            return 1;
        }
        int i = 2;
        while (true) {
            int i2 = i * 2;
            double d = 1.0d / i2;
            if (d + (0.3333333432674408d * d) <= ratio) {
                return i;
            }
            i = i2;
        }
    }

    public static int roundToPowerOfTwo(final int sampleSize) {
        int i = 1;
        while (i < sampleSize) {
            i *= 2;
        }
        return i;
    }

    private DownsampleUtil() {
    }

    public static int determineSampleSize(final RotationOptions rotationOptions, @Nullable final ResizeOptions resizeOptions, final EncodedImage encodedImage, final int maxBitmapSize) {
        int i;
        if (!EncodedImage.isMetaDataAvailable(encodedImage)) {
            return 1;
        }
        float determineDownsampleRatio = determineDownsampleRatio(rotationOptions, resizeOptions, encodedImage);
        if (encodedImage.getImageFormat() == DefaultImageFormats.JPEG) {
            i = ratioToSampleSizeJPEG(determineDownsampleRatio);
        } else {
            i = ratioToSampleSize(determineDownsampleRatio);
        }
        int max = Math.max(encodedImage.getHeight(), encodedImage.getWidth());
        float f = resizeOptions != null ? resizeOptions.maxBitmapSize : maxBitmapSize;
        while (max / i > f) {
            i = encodedImage.getImageFormat() == DefaultImageFormats.JPEG ? i * 2 : i + 1;
        }
        return i;
    }

    public static int determineSampleSizeJPEG(final EncodedImage encodedImage, final int pixelSize, final int maxBitmapSizeInBytes) {
        int sampleSize = encodedImage.getSampleSize();
        while ((((encodedImage.getWidth() * encodedImage.getHeight()) * pixelSize) / sampleSize) / sampleSize > maxBitmapSizeInBytes) {
            sampleSize *= 2;
        }
        return sampleSize;
    }

    public static float determineDownsampleRatio(final RotationOptions rotationOptions, @Nullable final ResizeOptions resizeOptions, final EncodedImage encodedImage) {
        Preconditions.checkArgument(Boolean.valueOf(EncodedImage.isMetaDataAvailable(encodedImage)));
        if (resizeOptions == null || resizeOptions.height <= 0 || resizeOptions.width <= 0 || encodedImage.getWidth() == 0 || encodedImage.getHeight() == 0) {
            return 1.0f;
        }
        int rotationAngle = getRotationAngle(rotationOptions, encodedImage);
        boolean z = rotationAngle == 90 || rotationAngle == 270;
        int height = z ? encodedImage.getHeight() : encodedImage.getWidth();
        int width = z ? encodedImage.getWidth() : encodedImage.getHeight();
        float f = resizeOptions.width / height;
        float f2 = resizeOptions.height / width;
        float max = Math.max(f, f2);
        FLog.v("DownsampleUtil", "Downsample - Specified size: %dx%d, image size: %dx%d ratio: %.1f x %.1f, ratio: %.3f", Integer.valueOf(resizeOptions.width), Integer.valueOf(resizeOptions.height), Integer.valueOf(height), Integer.valueOf(width), Float.valueOf(f), Float.valueOf(f2), Float.valueOf(max));
        return max;
    }

    public static int ratioToSampleSize(final float ratio) {
        if (ratio > 0.6666667f) {
            return 1;
        }
        int i = 2;
        while (true) {
            double d = i;
            if ((1.0d / d) + ((1.0d / (Math.pow(d, 2.0d) - d)) * 0.3333333432674408d) <= ratio) {
                return i - 1;
            }
            i++;
        }
    }

    private static int getRotationAngle(final RotationOptions rotationOptions, final EncodedImage encodedImage) {
        boolean z = false;
        if (!rotationOptions.useImageMetadata()) {
            return 0;
        }
        int rotationAngle = encodedImage.getRotationAngle();
        if (rotationAngle == 0 || rotationAngle == 90 || rotationAngle == 180 || rotationAngle == 270) {
            z = true;
        }
        Preconditions.checkArgument(Boolean.valueOf(z));
        return rotationAngle;
    }
}
