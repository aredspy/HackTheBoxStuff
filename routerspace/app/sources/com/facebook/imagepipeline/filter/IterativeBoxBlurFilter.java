package com.facebook.imagepipeline.filter;

import android.graphics.Bitmap;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.logging.FLog;
/* loaded from: classes.dex */
public abstract class IterativeBoxBlurFilter {
    private static final String TAG = "IterativeBoxBlurFilter";

    private static int bound(int x, int l, int h) {
        return x < l ? l : x > h ? h : x;
    }

    public static void boxBlurBitmapInPlace(final Bitmap bitmap, final int iterations, final int radius) {
        Preconditions.checkNotNull(bitmap);
        Preconditions.checkArgument(Boolean.valueOf(bitmap.isMutable()));
        Preconditions.checkArgument(Boolean.valueOf(((float) bitmap.getHeight()) <= 2048.0f));
        Preconditions.checkArgument(Boolean.valueOf(((float) bitmap.getWidth()) <= 2048.0f));
        Preconditions.checkArgument(Boolean.valueOf(radius > 0 && radius <= 25));
        Preconditions.checkArgument(Boolean.valueOf(iterations > 0));
        try {
            fastBoxBlur(bitmap, iterations, radius);
        } catch (OutOfMemoryError e) {
            FLog.e(TAG, String.format(null, "OOM: %d iterations on %dx%d with %d radius", Integer.valueOf(iterations), Integer.valueOf(bitmap.getWidth()), Integer.valueOf(bitmap.getHeight()), Integer.valueOf(radius)));
            throw e;
        }
    }

    private static void fastBoxBlur(final Bitmap bitmap, final int iterations, final int radius) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] iArr = new int[width * height];
        bitmap.getPixels(iArr, 0, width, 0, 0, width, height);
        int i = radius + 1;
        int i2 = i + radius;
        int[] iArr2 = new int[i2 * 256];
        int i3 = 1;
        while (true) {
            if (i3 > 255) {
                break;
            }
            for (int i4 = 0; i4 < i2; i4++) {
                iArr2[i] = i3;
                i++;
            }
            i3++;
        }
        int[] iArr3 = new int[Math.max(width, height)];
        for (int i5 = 0; i5 < iterations; i5++) {
            for (int i6 = 0; i6 < height; i6++) {
                internalHorizontalBlur(iArr, iArr3, width, i6, i2, iArr2);
                System.arraycopy(iArr3, 0, iArr, i6 * width, width);
            }
            int i7 = 0;
            while (i7 < width) {
                int i8 = i7;
                internalVerticalBlur(iArr, iArr3, width, height, i7, i2, iArr2);
                int i9 = i8;
                for (int i10 = 0; i10 < height; i10++) {
                    iArr[i9] = iArr3[i10];
                    i9 += width;
                }
                i7 = i8 + 1;
            }
        }
        bitmap.setPixels(iArr, 0, width, 0, 0, width, height);
    }

    private static void internalHorizontalBlur(int[] pixels, int[] outRow, int w, int row, int diameter, int[] div) {
        int i = w * row;
        int i2 = ((row + 1) * w) - 1;
        int i3 = diameter >> 1;
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        int i7 = 0;
        for (int i8 = -i3; i8 < w + i3; i8++) {
            int i9 = pixels[bound(i + i8, i, i2)];
            i4 += (i9 >> 16) & 255;
            i5 += (i9 >> 8) & 255;
            i6 += i9 & 255;
            i7 += i9 >>> 24;
            if (i8 >= i3) {
                outRow[i8 - i3] = (div[i7] << 24) | (div[i4] << 16) | (div[i5] << 8) | div[i6];
                int i10 = pixels[bound((i8 - (diameter - 1)) + i, i, i2)];
                i4 -= (i10 >> 16) & 255;
                i5 -= (i10 >> 8) & 255;
                i6 -= i10 & 255;
                i7 -= i10 >>> 24;
            }
        }
    }

    private static void internalVerticalBlur(int[] pixels, int[] outCol, int w, int h, int col, int diameter, int[] div) {
        int i = ((h - 1) * w) + col;
        int i2 = (diameter >> 1) * w;
        int i3 = (diameter - 1) * w;
        int i4 = col - i2;
        int i5 = 0;
        int i6 = 0;
        int i7 = 0;
        int i8 = 0;
        int i9 = 0;
        while (i4 <= i + i2) {
            int i10 = pixels[bound(i4, col, i)];
            i5 += (i10 >> 16) & 255;
            i6 += (i10 >> 8) & 255;
            i7 += i10 & 255;
            i8 += i10 >>> 24;
            if (i4 - i2 >= col) {
                outCol[i9] = (div[i8] << 24) | (div[i5] << 16) | (div[i6] << 8) | div[i7];
                i9++;
                int i11 = pixels[bound(i4 - i3, col, i)];
                i5 -= (i11 >> 16) & 255;
                i6 -= (i11 >> 8) & 255;
                i7 -= i11 & 255;
                i8 -= i11 >>> 24;
            }
            i4 += w;
        }
    }
}
