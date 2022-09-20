package com.facebook.imagepipeline.bitmaps;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.DisplayMetrics;
import androidx.core.view.ViewCompat;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.references.CloseableReference;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public abstract class PlatformBitmapFactory {
    public abstract CloseableReference<Bitmap> createBitmapInternal(int width, int height, Bitmap.Config bitmapConfig);

    public CloseableReference<Bitmap> createBitmap(int width, int height, Bitmap.Config bitmapConfig) {
        return createBitmap(width, height, bitmapConfig, (Object) null);
    }

    public CloseableReference<Bitmap> createBitmap(int width, int height) {
        return createBitmap(width, height, Bitmap.Config.ARGB_8888);
    }

    public CloseableReference<Bitmap> createBitmap(int width, int height, Bitmap.Config bitmapConfig, @Nullable Object callerContext) {
        return createBitmapInternal(width, height, bitmapConfig);
    }

    public CloseableReference<Bitmap> createBitmap(int width, int height, @Nullable Object callerContext) {
        return createBitmap(width, height, Bitmap.Config.ARGB_8888, callerContext);
    }

    public CloseableReference<Bitmap> createBitmap(Bitmap source) {
        return createBitmap(source, (Object) null);
    }

    public CloseableReference<Bitmap> createBitmap(Bitmap source, @Nullable Object callerContext) {
        return createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), callerContext);
    }

    public CloseableReference<Bitmap> createBitmap(Bitmap source, int x, int y, int width, int height) {
        return createBitmap(source, x, y, width, height, (Object) null);
    }

    public CloseableReference<Bitmap> createBitmap(Bitmap source, int x, int y, int width, int height, @Nullable Object callerContext) {
        return createBitmap(source, x, y, width, height, (Matrix) null, false, callerContext);
    }

    public CloseableReference<Bitmap> createBitmap(Bitmap source, int x, int y, int width, int height, @Nullable Matrix matrix, boolean filter) {
        return createBitmap(source, x, y, width, height, matrix, filter, (Object) null);
    }

    public CloseableReference<Bitmap> createScaledBitmap(Bitmap source, int destinationWidth, int destinationHeight, boolean filter) {
        return createScaledBitmap(source, destinationWidth, destinationHeight, filter, null);
    }

    public CloseableReference<Bitmap> createScaledBitmap(Bitmap source, int destinationWidth, int destinationHeight, boolean filter, @Nullable Object callerContext) {
        checkWidthHeight(destinationWidth, destinationHeight);
        Matrix matrix = new Matrix();
        int width = source.getWidth();
        int height = source.getHeight();
        matrix.setScale(destinationWidth / width, destinationHeight / height);
        return createBitmap(source, 0, 0, width, height, matrix, filter, callerContext);
    }

    public CloseableReference<Bitmap> createBitmap(Bitmap source, int x, int y, int width, int height, @Nullable Matrix matrix, boolean filter, @Nullable Object callerContext) {
        Canvas canvas;
        CloseableReference<Bitmap> closeableReference;
        Paint paint;
        Preconditions.checkNotNull(source, "Source bitmap cannot be null");
        checkXYSign(x, y);
        checkWidthHeight(width, height);
        checkFinalImageBounds(source, x, y, width, height);
        Rect rect = new Rect(x, y, x + width, y + height);
        RectF rectF = new RectF(0.0f, 0.0f, width, height);
        Bitmap.Config suitableBitmapConfig = getSuitableBitmapConfig(source);
        if (matrix == null || matrix.isIdentity()) {
            closeableReference = createBitmap(width, height, suitableBitmapConfig, source.hasAlpha(), callerContext);
            setPropertyFromSourceBitmap(source, closeableReference.get());
            canvas = new Canvas(closeableReference.get());
            paint = null;
        } else {
            boolean z = !matrix.rectStaysRect();
            RectF rectF2 = new RectF();
            matrix.mapRect(rectF2, rectF);
            int round = Math.round(rectF2.width());
            int round2 = Math.round(rectF2.height());
            if (z) {
                suitableBitmapConfig = Bitmap.Config.ARGB_8888;
            }
            closeableReference = createBitmap(round, round2, suitableBitmapConfig, z || source.hasAlpha(), callerContext);
            setPropertyFromSourceBitmap(source, closeableReference.get());
            canvas = new Canvas(closeableReference.get());
            canvas.translate(-rectF2.left, -rectF2.top);
            canvas.concat(matrix);
            paint = new Paint();
            paint.setFilterBitmap(filter);
            if (z) {
                paint.setAntiAlias(true);
            }
        }
        canvas.drawBitmap(source, rect, rectF, paint);
        canvas.setBitmap(null);
        return closeableReference;
    }

    public CloseableReference<Bitmap> createBitmap(DisplayMetrics display, int width, int height, Bitmap.Config config) {
        return createBitmap(display, width, height, config, (Object) null);
    }

    public CloseableReference<Bitmap> createBitmap(DisplayMetrics display, int width, int height, Bitmap.Config config, @Nullable Object callerContext) {
        return createBitmap(display, width, height, config, true, callerContext);
    }

    private CloseableReference<Bitmap> createBitmap(int width, int height, Bitmap.Config config, boolean hasAlpha) {
        return createBitmap(width, height, config, hasAlpha, (Object) null);
    }

    private CloseableReference<Bitmap> createBitmap(int width, int height, Bitmap.Config config, boolean hasAlpha, @Nullable Object callerContext) {
        return createBitmap((DisplayMetrics) null, width, height, config, hasAlpha, callerContext);
    }

    private CloseableReference<Bitmap> createBitmap(DisplayMetrics display, int width, int height, Bitmap.Config config, boolean hasAlpha) {
        return createBitmap(display, width, height, config, hasAlpha, (Object) null);
    }

    private CloseableReference<Bitmap> createBitmap(@Nullable DisplayMetrics display, int width, int height, Bitmap.Config config, boolean hasAlpha, @Nullable Object callerContext) {
        checkWidthHeight(width, height);
        CloseableReference<Bitmap> createBitmapInternal = createBitmapInternal(width, height, config);
        Bitmap bitmap = createBitmapInternal.get();
        if (display != null) {
            bitmap.setDensity(display.densityDpi);
        }
        if (Build.VERSION.SDK_INT >= 12) {
            bitmap.setHasAlpha(hasAlpha);
        }
        if (config == Bitmap.Config.ARGB_8888 && !hasAlpha) {
            bitmap.eraseColor(ViewCompat.MEASURED_STATE_MASK);
        }
        return createBitmapInternal;
    }

    public CloseableReference<Bitmap> createBitmap(int[] colors, int width, int height, Bitmap.Config config) {
        return createBitmap(colors, width, height, config, (Object) null);
    }

    public CloseableReference<Bitmap> createBitmap(int[] colors, int width, int height, Bitmap.Config config, @Nullable Object callerContext) {
        CloseableReference<Bitmap> createBitmapInternal = createBitmapInternal(width, height, config);
        createBitmapInternal.get().setPixels(colors, 0, width, 0, 0, width, height);
        return createBitmapInternal;
    }

    public CloseableReference<Bitmap> createBitmap(DisplayMetrics display, int[] colors, int width, int height, Bitmap.Config config) {
        return createBitmap(display, colors, width, height, config, (Object) null);
    }

    public CloseableReference<Bitmap> createBitmap(DisplayMetrics display, int[] colors, int width, int height, Bitmap.Config config, @Nullable Object callerContext) {
        return createBitmap(display, colors, 0, width, width, height, config, callerContext);
    }

    public CloseableReference<Bitmap> createBitmap(DisplayMetrics display, int[] colors, int offset, int stride, int width, int height, Bitmap.Config config) {
        return createBitmap(display, colors, offset, stride, width, height, config, (Object) null);
    }

    public CloseableReference<Bitmap> createBitmap(DisplayMetrics display, int[] colors, int offset, int stride, int width, int height, Bitmap.Config config, @Nullable Object callerContext) {
        CloseableReference<Bitmap> createBitmap = createBitmap(display, width, height, config, callerContext);
        createBitmap.get().setPixels(colors, offset, stride, 0, 0, width, height);
        return createBitmap;
    }

    private static Bitmap.Config getSuitableBitmapConfig(Bitmap source) {
        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        Bitmap.Config config2 = source.getConfig();
        if (config2 != null) {
            int i = AnonymousClass1.$SwitchMap$android$graphics$Bitmap$Config[config2.ordinal()];
            if (i == 1) {
                return Bitmap.Config.RGB_565;
            }
            if (i == 2) {
                return Bitmap.Config.ALPHA_8;
            }
            return Bitmap.Config.ARGB_8888;
        }
        return config;
    }

    /* renamed from: com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory$1 */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$android$graphics$Bitmap$Config;

        static {
            int[] iArr = new int[Bitmap.Config.values().length];
            $SwitchMap$android$graphics$Bitmap$Config = iArr;
            try {
                iArr[Bitmap.Config.RGB_565.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$android$graphics$Bitmap$Config[Bitmap.Config.ALPHA_8.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$android$graphics$Bitmap$Config[Bitmap.Config.ARGB_4444.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$android$graphics$Bitmap$Config[Bitmap.Config.ARGB_8888.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    private static void checkWidthHeight(int width, int height) {
        boolean z = true;
        Preconditions.checkArgument(width > 0, "width must be > 0");
        if (height <= 0) {
            z = false;
        }
        Preconditions.checkArgument(z, "height must be > 0");
    }

    private static void checkXYSign(int x, int y) {
        boolean z = true;
        Preconditions.checkArgument(x >= 0, "x must be >= 0");
        if (y < 0) {
            z = false;
        }
        Preconditions.checkArgument(z, "y must be >= 0");
    }

    private static void checkFinalImageBounds(Bitmap source, int x, int y, int width, int height) {
        boolean z = true;
        Preconditions.checkArgument(x + width <= source.getWidth(), "x + width must be <= bitmap.width()");
        if (y + height > source.getHeight()) {
            z = false;
        }
        Preconditions.checkArgument(z, "y + height must be <= bitmap.height()");
    }

    private static void setPropertyFromSourceBitmap(Bitmap source, Bitmap destination) {
        destination.setDensity(source.getDensity());
        if (Build.VERSION.SDK_INT >= 12) {
            destination.setHasAlpha(source.hasAlpha());
        }
        if (Build.VERSION.SDK_INT >= 19) {
            destination.setPremultiplied(source.isPremultiplied());
        }
    }
}
