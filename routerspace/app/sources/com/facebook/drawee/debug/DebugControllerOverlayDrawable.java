package com.facebook.drawee.debug;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import com.facebook.drawee.debug.listener.ImageLoadingTimeListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.imagepipeline.producers.ProducerContext;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class DebugControllerOverlayDrawable extends Drawable implements ImageLoadingTimeListener {
    private static final float IMAGE_SIZE_THRESHOLD_NOT_OK = 0.5f;
    private static final float IMAGE_SIZE_THRESHOLD_OK = 0.1f;
    private static final int MAX_LINE_WIDTH_EM = 8;
    private static final int MAX_NUMBER_OF_LINES = 9;
    private static final int MAX_TEXT_SIZE_PX = 40;
    private static final int MIN_TEXT_SIZE_PX = 10;
    private static final String NO_CONTROLLER_ID = "none";
    private static final int OUTLINE_COLOR = -26624;
    private static final int OUTLINE_STROKE_WIDTH_PX = 2;
    private static final int TEXT_BACKGROUND_COLOR = 1711276032;
    private static final int TEXT_COLOR = -1;
    static final int TEXT_COLOR_IMAGE_ALMOST_OK = -256;
    static final int TEXT_COLOR_IMAGE_NOT_OK = -65536;
    static final int TEXT_COLOR_IMAGE_OK = -16711936;
    private static final int TEXT_LINE_SPACING_PX = 8;
    private static final int TEXT_PADDING_PX = 10;
    private String mControllerId;
    private int mCurrentTextXPx;
    private int mCurrentTextYPx;
    private long mFinalImageTimeMs;
    private int mFrameCount;
    private int mHeightPx;
    @Nullable
    private String mImageFormat;
    @Nullable
    private String mImageId;
    private int mImageSizeBytes;
    private int mLineIncrementPx;
    private int mLoopCount;
    @Nullable
    private String mOriginText;
    @Nullable
    private ScalingUtils.ScaleType mScaleType;
    private int mStartTextXPx;
    private int mStartTextYPx;
    private int mWidthPx;
    private HashMap<String, String> mAdditionalData = new HashMap<>();
    private int mTextGravity = 80;
    private final Paint mPaint = new Paint(1);
    private final Matrix mMatrix = new Matrix();
    private final Rect mRect = new Rect();
    private final RectF mRectF = new RectF();
    private int mOriginColor = -1;
    private int mOverlayColor = 0;

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter cf) {
    }

    public DebugControllerOverlayDrawable() {
        reset();
    }

    public void reset() {
        this.mWidthPx = -1;
        this.mHeightPx = -1;
        this.mImageSizeBytes = -1;
        this.mAdditionalData = new HashMap<>();
        this.mFrameCount = -1;
        this.mLoopCount = -1;
        this.mImageFormat = null;
        setControllerId(null);
        this.mFinalImageTimeMs = -1L;
        this.mOriginText = null;
        this.mOriginColor = -1;
        invalidateSelf();
    }

    public void setTextGravity(int textGravity) {
        this.mTextGravity = textGravity;
        invalidateSelf();
    }

    public void setControllerId(@Nullable String controllerId) {
        if (controllerId == null) {
            controllerId = "none";
        }
        this.mControllerId = controllerId;
        invalidateSelf();
    }

    public void setImageId(@Nullable String imageId) {
        this.mImageId = imageId;
        invalidateSelf();
    }

    public void setDimensions(int widthPx, int heightPx) {
        this.mWidthPx = widthPx;
        this.mHeightPx = heightPx;
        invalidateSelf();
    }

    public void setAnimationInfo(int frameCount, int loopCount) {
        this.mFrameCount = frameCount;
        this.mLoopCount = loopCount;
        invalidateSelf();
    }

    public void setOrigin(String text, int color) {
        this.mOriginText = text;
        this.mOriginColor = color;
        invalidateSelf();
    }

    public void setImageSize(int imageSizeBytes) {
        this.mImageSizeBytes = imageSizeBytes;
    }

    public void addAdditionalData(String key, String value) {
        this.mAdditionalData.put(key, value);
    }

    public void setImageFormat(@Nullable String imageFormat) {
        this.mImageFormat = imageFormat;
    }

    public void setScaleType(ScalingUtils.ScaleType scaleType) {
        this.mScaleType = scaleType;
    }

    public void setOverlayColor(int overlayColor) {
        this.mOverlayColor = overlayColor;
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        prepareDebugTextParameters(bounds, 9, 8);
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeWidth(2.0f);
        this.mPaint.setColor(OUTLINE_COLOR);
        canvas.drawRect(bounds.left, bounds.top, bounds.right, bounds.bottom, this.mPaint);
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setColor(this.mOverlayColor);
        canvas.drawRect(bounds.left, bounds.top, bounds.right, bounds.bottom, this.mPaint);
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setStrokeWidth(0.0f);
        this.mPaint.setColor(-1);
        this.mCurrentTextXPx = this.mStartTextXPx;
        this.mCurrentTextYPx = this.mStartTextYPx;
        String str = this.mImageId;
        if (str != null) {
            addDebugText(canvas, "IDs", format("%s, %s", this.mControllerId, str));
        } else {
            addDebugText(canvas, "ID", this.mControllerId);
        }
        addDebugText(canvas, "D", format("%dx%d", Integer.valueOf(bounds.width()), Integer.valueOf(bounds.height())));
        addDebugText(canvas, "I", format("%dx%d", Integer.valueOf(this.mWidthPx), Integer.valueOf(this.mHeightPx)), determineSizeHintColor(this.mWidthPx, this.mHeightPx, this.mScaleType));
        addDebugText(canvas, "I", format("%d KiB", Integer.valueOf(this.mImageSizeBytes / 1024)));
        String str2 = this.mImageFormat;
        if (str2 != null) {
            addDebugText(canvas, "i format", str2);
        }
        int i = this.mFrameCount;
        if (i > 0) {
            addDebugText(canvas, "anim", format("f %d, l %d", Integer.valueOf(i), Integer.valueOf(this.mLoopCount)));
        }
        ScalingUtils.ScaleType scaleType = this.mScaleType;
        if (scaleType != null) {
            addDebugText(canvas, "scale", scaleType);
        }
        long j = this.mFinalImageTimeMs;
        if (j >= 0) {
            addDebugText(canvas, "t", format("%d ms", Long.valueOf(j)));
        }
        String str3 = this.mOriginText;
        if (str3 != null) {
            addDebugText(canvas, ProducerContext.ExtraKeys.ORIGIN, str3, this.mOriginColor);
        }
        for (Map.Entry<String, String> entry : this.mAdditionalData.entrySet()) {
            addDebugText(canvas, entry.getKey(), entry.getValue());
        }
    }

    private void prepareDebugTextParameters(Rect bounds, int numberOfLines, int maxLineLengthEm) {
        int min = Math.min(40, Math.max(10, Math.min(bounds.width() / maxLineLengthEm, bounds.height() / numberOfLines)));
        this.mPaint.setTextSize(min);
        int i = min + 8;
        this.mLineIncrementPx = i;
        if (this.mTextGravity == 80) {
            this.mLineIncrementPx = i * (-1);
        }
        this.mStartTextXPx = bounds.left + 10;
        this.mStartTextYPx = this.mTextGravity == 80 ? bounds.bottom - 10 : bounds.top + 10 + 10;
    }

    private static String format(String text, @Nullable Object... args) {
        return args == null ? text : String.format(Locale.US, text, args);
    }

    private void addDebugText(Canvas canvas, String label, Object value) {
        addDebugText(canvas, label, String.valueOf(value), -1);
    }

    private void addDebugText(Canvas canvas, String label, String value) {
        addDebugText(canvas, label, value, -1);
    }

    private void addDebugText(Canvas canvas, String label, String value, int valueColor) {
        String str = label + ": ";
        float measureText = this.mPaint.measureText(str);
        float measureText2 = this.mPaint.measureText(value);
        this.mPaint.setColor(TEXT_BACKGROUND_COLOR);
        int i = this.mCurrentTextXPx;
        int i2 = this.mCurrentTextYPx;
        canvas.drawRect(i - 4, i2 + 8, i + measureText + measureText2 + 4.0f, i2 + this.mLineIncrementPx + 8, this.mPaint);
        this.mPaint.setColor(-1);
        canvas.drawText(str, this.mCurrentTextXPx, this.mCurrentTextYPx, this.mPaint);
        this.mPaint.setColor(valueColor);
        canvas.drawText(value, this.mCurrentTextXPx + measureText, this.mCurrentTextYPx, this.mPaint);
        this.mCurrentTextYPx += this.mLineIncrementPx;
    }

    int determineSizeHintColor(int imageWidth, int imageHeight, @Nullable ScalingUtils.ScaleType scaleType) {
        int width = getBounds().width();
        int height = getBounds().height();
        if (width > 0 && height > 0 && imageWidth > 0 && imageHeight > 0) {
            if (scaleType != null) {
                Rect rect = this.mRect;
                rect.top = 0;
                rect.left = 0;
                this.mRect.right = width;
                this.mRect.bottom = height;
                this.mMatrix.reset();
                scaleType.getTransform(this.mMatrix, this.mRect, imageWidth, imageHeight, 0.0f, 0.0f);
                RectF rectF = this.mRectF;
                rectF.top = 0.0f;
                rectF.left = 0.0f;
                this.mRectF.right = imageWidth;
                this.mRectF.bottom = imageHeight;
                this.mMatrix.mapRect(this.mRectF);
                width = Math.min(width, (int) this.mRectF.width());
                height = Math.min(height, (int) this.mRectF.height());
            }
            float f = width;
            float f2 = f * IMAGE_SIZE_THRESHOLD_OK;
            float f3 = f * IMAGE_SIZE_THRESHOLD_NOT_OK;
            float f4 = height;
            float f5 = IMAGE_SIZE_THRESHOLD_OK * f4;
            float f6 = f4 * IMAGE_SIZE_THRESHOLD_NOT_OK;
            int abs = Math.abs(imageWidth - width);
            int abs2 = Math.abs(imageHeight - height);
            float f7 = abs;
            if (f7 < f2 && abs2 < f5) {
                return TEXT_COLOR_IMAGE_OK;
            }
            if (f7 < f3 && abs2 < f6) {
                return -256;
            }
        }
        return -65536;
    }

    public void setFinalImageTimeMs(long finalImageTimeMs) {
        this.mFinalImageTimeMs = finalImageTimeMs;
    }

    @Override // com.facebook.drawee.debug.listener.ImageLoadingTimeListener
    public void onFinalImageSet(long finalImageTimeMs) {
        this.mFinalImageTimeMs = finalImageTimeMs;
        invalidateSelf();
    }
}
