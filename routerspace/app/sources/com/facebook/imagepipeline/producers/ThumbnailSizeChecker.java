package com.facebook.imagepipeline.producers;

import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.EncodedImage;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public final class ThumbnailSizeChecker {
    public static final float ACCEPTABLE_REQUESTED_TO_ACTUAL_SIZE_RATIO = 1.3333334f;
    private static final int ROTATED_90_DEGREES_CLOCKWISE = 90;
    private static final int ROTATED_90_DEGREES_COUNTER_CLOCKWISE = 270;

    public static int getAcceptableSize(int size) {
        return (int) (size * 1.3333334f);
    }

    public static boolean isImageBigEnough(int width, int height, @Nullable ResizeOptions resizeOptions) {
        return resizeOptions == null ? ((float) getAcceptableSize(width)) >= 2048.0f && getAcceptableSize(height) >= 2048 : getAcceptableSize(width) >= resizeOptions.width && getAcceptableSize(height) >= resizeOptions.height;
    }

    public static boolean isImageBigEnough(@Nullable EncodedImage encodedImage, @Nullable ResizeOptions resizeOptions) {
        if (encodedImage == null) {
            return false;
        }
        int rotationAngle = encodedImage.getRotationAngle();
        if (rotationAngle == 90 || rotationAngle == 270) {
            return isImageBigEnough(encodedImage.getHeight(), encodedImage.getWidth(), resizeOptions);
        }
        return isImageBigEnough(encodedImage.getWidth(), encodedImage.getHeight(), resizeOptions);
    }
}
