package com.facebook.react.views.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import androidx.core.view.ViewCompat;
import com.facebook.react.modules.i18nmanager.I18nUtil;
import com.facebook.react.uimanager.FloatUtil;
import com.facebook.react.uimanager.Spacing;
import com.facebook.yoga.YogaConstants;
import java.util.Arrays;
import java.util.Locale;
/* loaded from: classes.dex */
public class ReactViewBackgroundDrawable extends Drawable {
    private static final int ALL_BITS_SET = -1;
    private static final int ALL_BITS_UNSET = 0;
    private static final int DEFAULT_BORDER_ALPHA = 255;
    private static final int DEFAULT_BORDER_COLOR = -16777216;
    private static final int DEFAULT_BORDER_RGB = 0;
    private Spacing mBorderAlpha;
    private float[] mBorderCornerRadii;
    private Spacing mBorderRGB;
    private BorderStyle mBorderStyle;
    private Spacing mBorderWidth;
    private Path mCenterDrawPath;
    private final Context mContext;
    private PointF mInnerBottomLeftCorner;
    private PointF mInnerBottomRightCorner;
    private Path mInnerClipPathForBorderRadius;
    private RectF mInnerClipTempRectForBorderRadius;
    private PointF mInnerTopLeftCorner;
    private PointF mInnerTopRightCorner;
    private int mLayoutDirection;
    private Path mOuterClipPathForBorderRadius;
    private RectF mOuterClipTempRectForBorderRadius;
    private Path mPathForBorder;
    private Path mPathForBorderRadiusOutline;
    private RectF mTempRectForBorderRadiusOutline;
    private RectF mTempRectForCenterDrawPath;
    private Path mPathForSingleBorder = new Path();
    private boolean mNeedUpdatePathForBorderRadius = false;
    private float mBorderRadius = Float.NaN;
    private final Paint mPaint = new Paint(1);
    private int mColor = 0;
    private int mAlpha = 255;

    /* loaded from: classes.dex */
    public enum BorderRadiusLocation {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_RIGHT,
        BOTTOM_LEFT,
        TOP_START,
        TOP_END,
        BOTTOM_START,
        BOTTOM_END
    }

    private static int colorFromAlphaAndRGBComponents(float alpha, float rgb) {
        return ((((int) alpha) << 24) & (-16777216)) | (((int) rgb) & ViewCompat.MEASURED_SIZE_MASK);
    }

    private static int fastBorderCompatibleColorOrZero(int borderLeft, int borderTop, int borderRight, int borderBottom, int colorLeft, int colorTop, int colorRight, int colorBottom) {
        int i = -1;
        int i2 = (borderLeft > 0 ? colorLeft : -1) & (borderTop > 0 ? colorTop : -1) & (borderRight > 0 ? colorRight : -1);
        if (borderBottom > 0) {
            i = colorBottom;
        }
        int i3 = i & i2;
        if (borderLeft <= 0) {
            colorLeft = 0;
        }
        if (borderTop <= 0) {
            colorTop = 0;
        }
        int i4 = colorLeft | colorTop;
        if (borderRight <= 0) {
            colorRight = 0;
        }
        int i5 = i4 | colorRight;
        if (borderBottom <= 0) {
            colorBottom = 0;
        }
        if (i3 == (i5 | colorBottom)) {
            return i3;
        }
        return 0;
    }

    public boolean onResolvedLayoutDirectionChanged(int layoutDirection) {
        return false;
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter cf) {
    }

    /* renamed from: com.facebook.react.views.view.ReactViewBackgroundDrawable$1 */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$facebook$react$views$view$ReactViewBackgroundDrawable$BorderStyle;

        static {
            int[] iArr = new int[BorderStyle.values().length];
            $SwitchMap$com$facebook$react$views$view$ReactViewBackgroundDrawable$BorderStyle = iArr;
            try {
                iArr[BorderStyle.SOLID.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$facebook$react$views$view$ReactViewBackgroundDrawable$BorderStyle[BorderStyle.DASHED.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$facebook$react$views$view$ReactViewBackgroundDrawable$BorderStyle[BorderStyle.DOTTED.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    /* loaded from: classes.dex */
    public enum BorderStyle {
        SOLID,
        DASHED,
        DOTTED;

        public static PathEffect getPathEffect(BorderStyle style, float borderWidth) {
            int i = AnonymousClass1.$SwitchMap$com$facebook$react$views$view$ReactViewBackgroundDrawable$BorderStyle[style.ordinal()];
            if (i == 2) {
                float f = borderWidth * 3.0f;
                return new DashPathEffect(new float[]{f, f, f, f}, 0.0f);
            } else if (i == 3) {
                return new DashPathEffect(new float[]{borderWidth, borderWidth, borderWidth, borderWidth}, 0.0f);
            } else {
                return null;
            }
        }
    }

    public ReactViewBackgroundDrawable(Context context) {
        this.mContext = context;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        updatePathEffect();
        if (!hasRoundedBorders()) {
            drawRectangularBackgroundWithBorders(canvas);
        } else {
            drawRoundedBackgroundWithBorders(canvas);
        }
    }

    public boolean hasRoundedBorders() {
        if (YogaConstants.isUndefined(this.mBorderRadius) || this.mBorderRadius <= 0.0f) {
            float[] fArr = this.mBorderCornerRadii;
            if (fArr != null) {
                for (float f : fArr) {
                    if (!YogaConstants.isUndefined(f) && f > 0.0f) {
                        return true;
                    }
                }
            }
            return false;
        }
        return true;
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        this.mNeedUpdatePathForBorderRadius = true;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        if (alpha != this.mAlpha) {
            this.mAlpha = alpha;
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        return this.mAlpha;
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return ColorUtil.getOpacityFromColor(ColorUtil.multiplyColorAlpha(this.mColor, this.mAlpha));
    }

    @Override // android.graphics.drawable.Drawable
    public void getOutline(Outline outline) {
        if ((!YogaConstants.isUndefined(this.mBorderRadius) && this.mBorderRadius > 0.0f) || this.mBorderCornerRadii != null) {
            updatePath();
            outline.setConvexPath(this.mPathForBorderRadiusOutline);
            return;
        }
        outline.setRect(getBounds());
    }

    public void setBorderWidth(int position, float width) {
        if (this.mBorderWidth == null) {
            this.mBorderWidth = new Spacing();
        }
        if (!FloatUtil.floatsEqual(this.mBorderWidth.getRaw(position), width)) {
            this.mBorderWidth.set(position, width);
            if (position == 0 || position == 1 || position == 2 || position == 3 || position == 4 || position == 5 || position == 8) {
                this.mNeedUpdatePathForBorderRadius = true;
            }
            invalidateSelf();
        }
    }

    public void setBorderColor(int position, float rgb, float alpha) {
        setBorderRGB(position, rgb);
        setBorderAlpha(position, alpha);
    }

    private void setBorderRGB(int position, float rgb) {
        if (this.mBorderRGB == null) {
            this.mBorderRGB = new Spacing(0.0f);
        }
        if (!FloatUtil.floatsEqual(this.mBorderRGB.getRaw(position), rgb)) {
            this.mBorderRGB.set(position, rgb);
            invalidateSelf();
        }
    }

    private void setBorderAlpha(int position, float alpha) {
        if (this.mBorderAlpha == null) {
            this.mBorderAlpha = new Spacing(255.0f);
        }
        if (!FloatUtil.floatsEqual(this.mBorderAlpha.getRaw(position), alpha)) {
            this.mBorderAlpha.set(position, alpha);
            invalidateSelf();
        }
    }

    public void setBorderStyle(String style) {
        BorderStyle valueOf = style == null ? null : BorderStyle.valueOf(style.toUpperCase(Locale.US));
        if (this.mBorderStyle != valueOf) {
            this.mBorderStyle = valueOf;
            this.mNeedUpdatePathForBorderRadius = true;
            invalidateSelf();
        }
    }

    public void setRadius(float radius) {
        if (!FloatUtil.floatsEqual(this.mBorderRadius, radius)) {
            this.mBorderRadius = radius;
            this.mNeedUpdatePathForBorderRadius = true;
            invalidateSelf();
        }
    }

    public void setRadius(float radius, int position) {
        if (this.mBorderCornerRadii == null) {
            float[] fArr = new float[8];
            this.mBorderCornerRadii = fArr;
            Arrays.fill(fArr, Float.NaN);
        }
        if (!FloatUtil.floatsEqual(this.mBorderCornerRadii[position], radius)) {
            this.mBorderCornerRadii[position] = radius;
            this.mNeedUpdatePathForBorderRadius = true;
            invalidateSelf();
        }
    }

    public float getFullBorderRadius() {
        if (YogaConstants.isUndefined(this.mBorderRadius)) {
            return 0.0f;
        }
        return this.mBorderRadius;
    }

    public float getBorderRadius(final BorderRadiusLocation location) {
        return getBorderRadiusOrDefaultTo(Float.NaN, location);
    }

    public float getBorderRadiusOrDefaultTo(final float defaultValue, final BorderRadiusLocation location) {
        float[] fArr = this.mBorderCornerRadii;
        if (fArr == null) {
            return defaultValue;
        }
        float f = fArr[location.ordinal()];
        return YogaConstants.isUndefined(f) ? defaultValue : f;
    }

    public void setColor(int color) {
        this.mColor = color;
        invalidateSelf();
    }

    public int getResolvedLayoutDirection() {
        return this.mLayoutDirection;
    }

    public boolean setResolvedLayoutDirection(int layoutDirection) {
        if (this.mLayoutDirection != layoutDirection) {
            this.mLayoutDirection = layoutDirection;
            return onResolvedLayoutDirectionChanged(layoutDirection);
        }
        return false;
    }

    public int getColor() {
        return this.mColor;
    }

    private void drawRoundedBackgroundWithBorders(Canvas canvas) {
        int i;
        int i2;
        float f;
        float f2;
        float f3;
        float f4;
        updatePath();
        canvas.save();
        int multiplyColorAlpha = ColorUtil.multiplyColorAlpha(this.mColor, this.mAlpha);
        if (Color.alpha(multiplyColorAlpha) != 0) {
            this.mPaint.setColor(multiplyColorAlpha);
            this.mPaint.setStyle(Paint.Style.FILL);
            canvas.drawPath(this.mInnerClipPathForBorderRadius, this.mPaint);
        }
        RectF directionAwareBorderInsets = getDirectionAwareBorderInsets();
        boolean z = false;
        int borderColor = getBorderColor(0);
        int borderColor2 = getBorderColor(1);
        int borderColor3 = getBorderColor(2);
        int borderColor4 = getBorderColor(3);
        if (directionAwareBorderInsets.top > 0.0f || directionAwareBorderInsets.bottom > 0.0f || directionAwareBorderInsets.left > 0.0f || directionAwareBorderInsets.right > 0.0f) {
            float fullBorderWidth = getFullBorderWidth();
            int borderColor5 = getBorderColor(8);
            if (directionAwareBorderInsets.top != fullBorderWidth || directionAwareBorderInsets.bottom != fullBorderWidth || directionAwareBorderInsets.left != fullBorderWidth || directionAwareBorderInsets.right != fullBorderWidth || borderColor != borderColor5 || borderColor2 != borderColor5 || borderColor3 != borderColor5 || borderColor4 != borderColor5) {
                this.mPaint.setStyle(Paint.Style.FILL);
                canvas.clipPath(this.mOuterClipPathForBorderRadius, Region.Op.INTERSECT);
                canvas.clipPath(this.mInnerClipPathForBorderRadius, Region.Op.DIFFERENCE);
                if (getResolvedLayoutDirection() == 1) {
                    z = true;
                }
                int borderColor6 = getBorderColor(4);
                int borderColor7 = getBorderColor(5);
                if (I18nUtil.getInstance().doLeftAndRightSwapInRTL(this.mContext)) {
                    if (isBorderColorDefined(4)) {
                        borderColor = borderColor6;
                    }
                    if (isBorderColorDefined(5)) {
                        borderColor3 = borderColor7;
                    }
                    i2 = z ? borderColor3 : borderColor;
                    if (!z) {
                        borderColor = borderColor3;
                    }
                    i = borderColor;
                } else {
                    int i3 = z ? borderColor7 : borderColor6;
                    if (!z) {
                        borderColor6 = borderColor7;
                    }
                    boolean isBorderColorDefined = isBorderColorDefined(4);
                    boolean isBorderColorDefined2 = isBorderColorDefined(5);
                    boolean z2 = z ? isBorderColorDefined2 : isBorderColorDefined;
                    if (!z) {
                        isBorderColorDefined = isBorderColorDefined2;
                    }
                    if (z2) {
                        borderColor = i3;
                    }
                    if (isBorderColorDefined) {
                        i2 = borderColor;
                        i = borderColor6;
                    } else {
                        i2 = borderColor;
                        i = borderColor3;
                    }
                }
                float f5 = this.mOuterClipTempRectForBorderRadius.left;
                float f6 = this.mOuterClipTempRectForBorderRadius.right;
                float f7 = this.mOuterClipTempRectForBorderRadius.top;
                float f8 = this.mOuterClipTempRectForBorderRadius.bottom;
                if (directionAwareBorderInsets.left > 0.0f) {
                    f2 = f8;
                    f3 = f7;
                    f4 = f6;
                    f = f5;
                    drawQuadrilateral(canvas, i2, f5, f7, this.mInnerTopLeftCorner.x, this.mInnerTopLeftCorner.y, this.mInnerBottomLeftCorner.x, this.mInnerBottomLeftCorner.y, f5, f2);
                } else {
                    f2 = f8;
                    f3 = f7;
                    f4 = f6;
                    f = f5;
                }
                if (directionAwareBorderInsets.top > 0.0f) {
                    drawQuadrilateral(canvas, borderColor2, f, f3, this.mInnerTopLeftCorner.x, this.mInnerTopLeftCorner.y, this.mInnerTopRightCorner.x, this.mInnerTopRightCorner.y, f4, f3);
                }
                if (directionAwareBorderInsets.right > 0.0f) {
                    drawQuadrilateral(canvas, i, f4, f3, this.mInnerTopRightCorner.x, this.mInnerTopRightCorner.y, this.mInnerBottomRightCorner.x, this.mInnerBottomRightCorner.y, f4, f2);
                }
                if (directionAwareBorderInsets.bottom > 0.0f) {
                    drawQuadrilateral(canvas, borderColor4, f, f2, this.mInnerBottomLeftCorner.x, this.mInnerBottomLeftCorner.y, this.mInnerBottomRightCorner.x, this.mInnerBottomRightCorner.y, f4, f2);
                }
            } else if (fullBorderWidth > 0.0f) {
                this.mPaint.setColor(ColorUtil.multiplyColorAlpha(borderColor5, this.mAlpha));
                this.mPaint.setStyle(Paint.Style.STROKE);
                this.mPaint.setStrokeWidth(fullBorderWidth);
                canvas.drawPath(this.mCenterDrawPath, this.mPaint);
            }
        }
        canvas.restore();
    }

    private void updatePath() {
        float f;
        float f2;
        float max;
        float max2;
        float max3;
        float max4;
        float max5;
        float max6;
        float max7;
        float max8;
        if (!this.mNeedUpdatePathForBorderRadius) {
            return;
        }
        this.mNeedUpdatePathForBorderRadius = false;
        if (this.mInnerClipPathForBorderRadius == null) {
            this.mInnerClipPathForBorderRadius = new Path();
        }
        if (this.mOuterClipPathForBorderRadius == null) {
            this.mOuterClipPathForBorderRadius = new Path();
        }
        if (this.mPathForBorderRadiusOutline == null) {
            this.mPathForBorderRadiusOutline = new Path();
        }
        if (this.mCenterDrawPath == null) {
            this.mCenterDrawPath = new Path();
        }
        if (this.mInnerClipTempRectForBorderRadius == null) {
            this.mInnerClipTempRectForBorderRadius = new RectF();
        }
        if (this.mOuterClipTempRectForBorderRadius == null) {
            this.mOuterClipTempRectForBorderRadius = new RectF();
        }
        if (this.mTempRectForBorderRadiusOutline == null) {
            this.mTempRectForBorderRadiusOutline = new RectF();
        }
        if (this.mTempRectForCenterDrawPath == null) {
            this.mTempRectForCenterDrawPath = new RectF();
        }
        this.mInnerClipPathForBorderRadius.reset();
        this.mOuterClipPathForBorderRadius.reset();
        this.mPathForBorderRadiusOutline.reset();
        this.mCenterDrawPath.reset();
        this.mInnerClipTempRectForBorderRadius.set(getBounds());
        this.mOuterClipTempRectForBorderRadius.set(getBounds());
        this.mTempRectForBorderRadiusOutline.set(getBounds());
        this.mTempRectForCenterDrawPath.set(getBounds());
        RectF directionAwareBorderInsets = getDirectionAwareBorderInsets();
        this.mInnerClipTempRectForBorderRadius.top += directionAwareBorderInsets.top;
        this.mInnerClipTempRectForBorderRadius.bottom -= directionAwareBorderInsets.bottom;
        this.mInnerClipTempRectForBorderRadius.left += directionAwareBorderInsets.left;
        this.mInnerClipTempRectForBorderRadius.right -= directionAwareBorderInsets.right;
        this.mTempRectForCenterDrawPath.top += directionAwareBorderInsets.top * 0.5f;
        this.mTempRectForCenterDrawPath.bottom -= directionAwareBorderInsets.bottom * 0.5f;
        this.mTempRectForCenterDrawPath.left += directionAwareBorderInsets.left * 0.5f;
        this.mTempRectForCenterDrawPath.right -= directionAwareBorderInsets.right * 0.5f;
        float fullBorderRadius = getFullBorderRadius();
        float borderRadiusOrDefaultTo = getBorderRadiusOrDefaultTo(fullBorderRadius, BorderRadiusLocation.TOP_LEFT);
        float borderRadiusOrDefaultTo2 = getBorderRadiusOrDefaultTo(fullBorderRadius, BorderRadiusLocation.TOP_RIGHT);
        float borderRadiusOrDefaultTo3 = getBorderRadiusOrDefaultTo(fullBorderRadius, BorderRadiusLocation.BOTTOM_LEFT);
        float borderRadiusOrDefaultTo4 = getBorderRadiusOrDefaultTo(fullBorderRadius, BorderRadiusLocation.BOTTOM_RIGHT);
        boolean z = getResolvedLayoutDirection() == 1;
        float borderRadius = getBorderRadius(BorderRadiusLocation.TOP_START);
        float borderRadius2 = getBorderRadius(BorderRadiusLocation.TOP_END);
        float borderRadius3 = getBorderRadius(BorderRadiusLocation.BOTTOM_START);
        float borderRadius4 = getBorderRadius(BorderRadiusLocation.BOTTOM_END);
        if (I18nUtil.getInstance().doLeftAndRightSwapInRTL(this.mContext)) {
            if (!YogaConstants.isUndefined(borderRadius)) {
                borderRadiusOrDefaultTo = borderRadius;
            }
            if (!YogaConstants.isUndefined(borderRadius2)) {
                borderRadiusOrDefaultTo2 = borderRadius2;
            }
            if (!YogaConstants.isUndefined(borderRadius3)) {
                borderRadiusOrDefaultTo3 = borderRadius3;
            }
            if (!YogaConstants.isUndefined(borderRadius4)) {
                borderRadiusOrDefaultTo4 = borderRadius4;
            }
            f = z ? borderRadiusOrDefaultTo2 : borderRadiusOrDefaultTo;
            if (!z) {
                borderRadiusOrDefaultTo = borderRadiusOrDefaultTo2;
            }
            f2 = z ? borderRadiusOrDefaultTo4 : borderRadiusOrDefaultTo3;
            if (z) {
                borderRadiusOrDefaultTo4 = borderRadiusOrDefaultTo3;
            }
        } else {
            float f3 = z ? borderRadius2 : borderRadius;
            if (!z) {
                borderRadius = borderRadius2;
            }
            float f4 = z ? borderRadius4 : borderRadius3;
            if (!z) {
                borderRadius3 = borderRadius4;
            }
            if (!YogaConstants.isUndefined(f3)) {
                borderRadiusOrDefaultTo = f3;
            }
            if (!YogaConstants.isUndefined(borderRadius)) {
                borderRadiusOrDefaultTo2 = borderRadius;
            }
            if (!YogaConstants.isUndefined(f4)) {
                borderRadiusOrDefaultTo3 = f4;
            }
            f = borderRadiusOrDefaultTo;
            borderRadiusOrDefaultTo = borderRadiusOrDefaultTo2;
            f2 = borderRadiusOrDefaultTo3;
            if (!YogaConstants.isUndefined(borderRadius3)) {
                borderRadiusOrDefaultTo4 = borderRadius3;
            }
        }
        float f5 = f2;
        this.mInnerClipPathForBorderRadius.addRoundRect(this.mInnerClipTempRectForBorderRadius, new float[]{Math.max(f - directionAwareBorderInsets.left, 0.0f), Math.max(f - directionAwareBorderInsets.top, 0.0f), Math.max(borderRadiusOrDefaultTo - directionAwareBorderInsets.right, 0.0f), Math.max(borderRadiusOrDefaultTo - directionAwareBorderInsets.top, 0.0f), Math.max(borderRadiusOrDefaultTo4 - directionAwareBorderInsets.right, 0.0f), Math.max(borderRadiusOrDefaultTo4 - directionAwareBorderInsets.bottom, 0.0f), Math.max(f2 - directionAwareBorderInsets.left, 0.0f), Math.max(f2 - directionAwareBorderInsets.bottom, 0.0f)}, Path.Direction.CW);
        this.mOuterClipPathForBorderRadius.addRoundRect(this.mOuterClipTempRectForBorderRadius, new float[]{f, f, borderRadiusOrDefaultTo, borderRadiusOrDefaultTo, borderRadiusOrDefaultTo4, borderRadiusOrDefaultTo4, f5, f5}, Path.Direction.CW);
        Spacing spacing = this.mBorderWidth;
        float f6 = spacing != null ? spacing.get(8) / 2.0f : 0.0f;
        float f7 = f + f6;
        float f8 = borderRadiusOrDefaultTo + f6;
        float f9 = borderRadiusOrDefaultTo4 + f6;
        float f10 = f5 + f6;
        this.mPathForBorderRadiusOutline.addRoundRect(this.mTempRectForBorderRadiusOutline, new float[]{f7, f7, f8, f8, f9, f9, f10, f10}, Path.Direction.CW);
        Path path = this.mCenterDrawPath;
        RectF rectF = this.mTempRectForCenterDrawPath;
        float[] fArr = new float[8];
        fArr[0] = Math.max(f - (directionAwareBorderInsets.left * 0.5f), directionAwareBorderInsets.left > 0.0f ? f / directionAwareBorderInsets.left : 0.0f);
        fArr[1] = Math.max(f - (directionAwareBorderInsets.top * 0.5f), directionAwareBorderInsets.top > 0.0f ? f / directionAwareBorderInsets.top : 0.0f);
        fArr[2] = Math.max(borderRadiusOrDefaultTo - (directionAwareBorderInsets.right * 0.5f), directionAwareBorderInsets.right > 0.0f ? borderRadiusOrDefaultTo / directionAwareBorderInsets.right : 0.0f);
        fArr[3] = Math.max(borderRadiusOrDefaultTo - (directionAwareBorderInsets.top * 0.5f), directionAwareBorderInsets.top > 0.0f ? borderRadiusOrDefaultTo / directionAwareBorderInsets.top : 0.0f);
        fArr[4] = Math.max(borderRadiusOrDefaultTo4 - (directionAwareBorderInsets.right * 0.5f), directionAwareBorderInsets.right > 0.0f ? borderRadiusOrDefaultTo4 / directionAwareBorderInsets.right : 0.0f);
        fArr[5] = Math.max(borderRadiusOrDefaultTo4 - (directionAwareBorderInsets.bottom * 0.5f), directionAwareBorderInsets.bottom > 0.0f ? borderRadiusOrDefaultTo4 / directionAwareBorderInsets.bottom : 0.0f);
        fArr[6] = Math.max(f5 - (directionAwareBorderInsets.left * 0.5f), directionAwareBorderInsets.left > 0.0f ? f5 / directionAwareBorderInsets.left : 0.0f);
        fArr[7] = Math.max(f5 - (directionAwareBorderInsets.bottom * 0.5f), directionAwareBorderInsets.bottom > 0.0f ? f5 / directionAwareBorderInsets.bottom : 0.0f);
        path.addRoundRect(rectF, fArr, Path.Direction.CW);
        if (this.mInnerTopLeftCorner == null) {
            this.mInnerTopLeftCorner = new PointF();
        }
        this.mInnerTopLeftCorner.x = this.mInnerClipTempRectForBorderRadius.left;
        this.mInnerTopLeftCorner.y = this.mInnerClipTempRectForBorderRadius.top;
        getEllipseIntersectionWithLine(this.mInnerClipTempRectForBorderRadius.left, this.mInnerClipTempRectForBorderRadius.top, this.mInnerClipTempRectForBorderRadius.left + (max * 2.0f), this.mInnerClipTempRectForBorderRadius.top + (max2 * 2.0f), this.mOuterClipTempRectForBorderRadius.left, this.mOuterClipTempRectForBorderRadius.top, this.mInnerClipTempRectForBorderRadius.left, this.mInnerClipTempRectForBorderRadius.top, this.mInnerTopLeftCorner);
        if (this.mInnerBottomLeftCorner == null) {
            this.mInnerBottomLeftCorner = new PointF();
        }
        this.mInnerBottomLeftCorner.x = this.mInnerClipTempRectForBorderRadius.left;
        this.mInnerBottomLeftCorner.y = this.mInnerClipTempRectForBorderRadius.bottom;
        getEllipseIntersectionWithLine(this.mInnerClipTempRectForBorderRadius.left, this.mInnerClipTempRectForBorderRadius.bottom - (max8 * 2.0f), this.mInnerClipTempRectForBorderRadius.left + (max7 * 2.0f), this.mInnerClipTempRectForBorderRadius.bottom, this.mOuterClipTempRectForBorderRadius.left, this.mOuterClipTempRectForBorderRadius.bottom, this.mInnerClipTempRectForBorderRadius.left, this.mInnerClipTempRectForBorderRadius.bottom, this.mInnerBottomLeftCorner);
        if (this.mInnerTopRightCorner == null) {
            this.mInnerTopRightCorner = new PointF();
        }
        this.mInnerTopRightCorner.x = this.mInnerClipTempRectForBorderRadius.right;
        this.mInnerTopRightCorner.y = this.mInnerClipTempRectForBorderRadius.top;
        getEllipseIntersectionWithLine(this.mInnerClipTempRectForBorderRadius.right - (max3 * 2.0f), this.mInnerClipTempRectForBorderRadius.top, this.mInnerClipTempRectForBorderRadius.right, this.mInnerClipTempRectForBorderRadius.top + (max4 * 2.0f), this.mOuterClipTempRectForBorderRadius.right, this.mOuterClipTempRectForBorderRadius.top, this.mInnerClipTempRectForBorderRadius.right, this.mInnerClipTempRectForBorderRadius.top, this.mInnerTopRightCorner);
        if (this.mInnerBottomRightCorner == null) {
            this.mInnerBottomRightCorner = new PointF();
        }
        this.mInnerBottomRightCorner.x = this.mInnerClipTempRectForBorderRadius.right;
        this.mInnerBottomRightCorner.y = this.mInnerClipTempRectForBorderRadius.bottom;
        getEllipseIntersectionWithLine(this.mInnerClipTempRectForBorderRadius.right - (max5 * 2.0f), this.mInnerClipTempRectForBorderRadius.bottom - (max6 * 2.0f), this.mInnerClipTempRectForBorderRadius.right, this.mInnerClipTempRectForBorderRadius.bottom, this.mOuterClipTempRectForBorderRadius.right, this.mOuterClipTempRectForBorderRadius.bottom, this.mInnerClipTempRectForBorderRadius.right, this.mInnerClipTempRectForBorderRadius.bottom, this.mInnerBottomRightCorner);
    }

    private static void getEllipseIntersectionWithLine(double ellipseBoundsLeft, double ellipseBoundsTop, double ellipseBoundsRight, double ellipseBoundsBottom, double lineStartX, double lineStartY, double lineEndX, double lineEndY, PointF result) {
        double d = (ellipseBoundsLeft + ellipseBoundsRight) / 2.0d;
        double d2 = (ellipseBoundsTop + ellipseBoundsBottom) / 2.0d;
        double d3 = lineStartX - d;
        double d4 = lineStartY - d2;
        double abs = Math.abs(ellipseBoundsRight - ellipseBoundsLeft) / 2.0d;
        double abs2 = Math.abs(ellipseBoundsBottom - ellipseBoundsTop) / 2.0d;
        double d5 = ((lineEndY - d2) - d4) / ((lineEndX - d) - d3);
        double d6 = d4 - (d3 * d5);
        double d7 = abs2 * abs2;
        double d8 = abs * abs;
        double d9 = d7 + (d8 * d5 * d5);
        double d10 = abs * 2.0d * abs * d6 * d5;
        double d11 = (-(d8 * ((d6 * d6) - d7))) / d9;
        double d12 = d9 * 2.0d;
        double sqrt = ((-d10) / d12) - Math.sqrt(d11 + Math.pow(d10 / d12, 2.0d));
        double d13 = sqrt + d;
        double d14 = (d5 * sqrt) + d6 + d2;
        if (Double.isNaN(d13) || Double.isNaN(d14)) {
            return;
        }
        result.x = (float) d13;
        result.y = (float) d14;
    }

    public float getBorderWidthOrDefaultTo(final float defaultValue, final int spacingType) {
        Spacing spacing = this.mBorderWidth;
        if (spacing == null) {
            return defaultValue;
        }
        float raw = spacing.getRaw(spacingType);
        return YogaConstants.isUndefined(raw) ? defaultValue : raw;
    }

    private void updatePathEffect() {
        BorderStyle borderStyle = this.mBorderStyle;
        this.mPaint.setPathEffect(borderStyle != null ? BorderStyle.getPathEffect(borderStyle, getFullBorderWidth()) : null);
    }

    private void updatePathEffect(int borderWidth) {
        BorderStyle borderStyle = this.mBorderStyle;
        this.mPaint.setPathEffect(borderStyle != null ? BorderStyle.getPathEffect(borderStyle, borderWidth) : null);
    }

    public float getFullBorderWidth() {
        Spacing spacing = this.mBorderWidth;
        if (spacing == null || YogaConstants.isUndefined(spacing.getRaw(8))) {
            return 0.0f;
        }
        return this.mBorderWidth.getRaw(8);
    }

    private void drawRectangularBackgroundWithBorders(Canvas canvas) {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        this.mPaint.setStyle(Paint.Style.FILL);
        int multiplyColorAlpha = ColorUtil.multiplyColorAlpha(this.mColor, this.mAlpha);
        if (Color.alpha(multiplyColorAlpha) != 0) {
            this.mPaint.setColor(multiplyColorAlpha);
            canvas.drawRect(getBounds(), this.mPaint);
        }
        RectF directionAwareBorderInsets = getDirectionAwareBorderInsets();
        int round = Math.round(directionAwareBorderInsets.left);
        int round2 = Math.round(directionAwareBorderInsets.top);
        int round3 = Math.round(directionAwareBorderInsets.right);
        int round4 = Math.round(directionAwareBorderInsets.bottom);
        if (round > 0 || round3 > 0 || round2 > 0 || round4 > 0) {
            Rect bounds = getBounds();
            int borderColor = getBorderColor(0);
            int borderColor2 = getBorderColor(1);
            int borderColor3 = getBorderColor(2);
            int borderColor4 = getBorderColor(3);
            boolean z = getResolvedLayoutDirection() == 1;
            int borderColor5 = getBorderColor(4);
            int borderColor6 = getBorderColor(5);
            if (I18nUtil.getInstance().doLeftAndRightSwapInRTL(this.mContext)) {
                if (isBorderColorDefined(4)) {
                    borderColor = borderColor5;
                }
                if (isBorderColorDefined(5)) {
                    borderColor3 = borderColor6;
                }
                int i8 = z ? borderColor3 : borderColor;
                if (!z) {
                    borderColor = borderColor3;
                }
                i = borderColor;
                i2 = i8;
            } else {
                int i9 = z ? borderColor6 : borderColor5;
                if (!z) {
                    borderColor5 = borderColor6;
                }
                boolean isBorderColorDefined = isBorderColorDefined(4);
                boolean isBorderColorDefined2 = isBorderColorDefined(5);
                boolean z2 = z ? isBorderColorDefined2 : isBorderColorDefined;
                if (!z) {
                    isBorderColorDefined = isBorderColorDefined2;
                }
                if (z2) {
                    borderColor = i9;
                }
                i2 = borderColor;
                i = isBorderColorDefined ? borderColor5 : borderColor3;
            }
            int i10 = bounds.left;
            int i11 = bounds.top;
            int fastBorderCompatibleColorOrZero = fastBorderCompatibleColorOrZero(round, round2, round3, round4, i2, borderColor2, i, borderColor4);
            if (fastBorderCompatibleColorOrZero != 0) {
                if (Color.alpha(fastBorderCompatibleColorOrZero) == 0) {
                    return;
                }
                int i12 = bounds.right;
                int i13 = bounds.bottom;
                this.mPaint.setColor(fastBorderCompatibleColorOrZero);
                this.mPaint.setStyle(Paint.Style.STROKE);
                if (round > 0) {
                    this.mPathForSingleBorder.reset();
                    int round5 = Math.round(directionAwareBorderInsets.left);
                    updatePathEffect(round5);
                    this.mPaint.setStrokeWidth(round5);
                    float f = i10 + (round5 / 2);
                    this.mPathForSingleBorder.moveTo(f, i11);
                    this.mPathForSingleBorder.lineTo(f, i13);
                    canvas.drawPath(this.mPathForSingleBorder, this.mPaint);
                }
                if (round2 > 0) {
                    this.mPathForSingleBorder.reset();
                    int round6 = Math.round(directionAwareBorderInsets.top);
                    updatePathEffect(round6);
                    this.mPaint.setStrokeWidth(round6);
                    float f2 = i11 + (round6 / 2);
                    this.mPathForSingleBorder.moveTo(i10, f2);
                    this.mPathForSingleBorder.lineTo(i12, f2);
                    canvas.drawPath(this.mPathForSingleBorder, this.mPaint);
                }
                if (round3 > 0) {
                    this.mPathForSingleBorder.reset();
                    int round7 = Math.round(directionAwareBorderInsets.right);
                    updatePathEffect(round7);
                    this.mPaint.setStrokeWidth(round7);
                    float f3 = i12 - (round7 / 2);
                    this.mPathForSingleBorder.moveTo(f3, i11);
                    this.mPathForSingleBorder.lineTo(f3, i13);
                    canvas.drawPath(this.mPathForSingleBorder, this.mPaint);
                }
                if (round4 <= 0) {
                    return;
                }
                this.mPathForSingleBorder.reset();
                int round8 = Math.round(directionAwareBorderInsets.bottom);
                updatePathEffect(round8);
                this.mPaint.setStrokeWidth(round8);
                float f4 = i13 - (round8 / 2);
                this.mPathForSingleBorder.moveTo(i10, f4);
                this.mPathForSingleBorder.lineTo(i12, f4);
                canvas.drawPath(this.mPathForSingleBorder, this.mPaint);
                return;
            }
            this.mPaint.setAntiAlias(false);
            int width = bounds.width();
            int height = bounds.height();
            if (round > 0) {
                float f5 = i10;
                float f6 = i10 + round;
                i3 = i11;
                drawQuadrilateral(canvas, i2, f5, i11, f6, i11 + round2, f6, i7 - round4, f5, i11 + height);
            } else {
                i3 = i11;
            }
            if (round2 > 0) {
                float f7 = i3;
                float f8 = i3 + round2;
                drawQuadrilateral(canvas, borderColor2, i10, f7, i10 + round, f8, i6 - round3, f8, i10 + width, f7);
            }
            if (round3 > 0) {
                int i14 = i10 + width;
                float f9 = i14;
                float f10 = i14 - round3;
                drawQuadrilateral(canvas, i, f9, i3, f9, i3 + height, f10, i5 - round4, f10, i3 + round2);
            }
            if (round4 > 0) {
                int i15 = i3 + height;
                float f11 = i15;
                float f12 = i15 - round4;
                drawQuadrilateral(canvas, borderColor4, i10, f11, i10 + width, f11, i4 - round3, f12, i10 + round, f12);
            }
            this.mPaint.setAntiAlias(true);
        }
    }

    private void drawQuadrilateral(Canvas canvas, int fillColor, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        if (fillColor == 0) {
            return;
        }
        if (this.mPathForBorder == null) {
            this.mPathForBorder = new Path();
        }
        this.mPaint.setColor(fillColor);
        this.mPathForBorder.reset();
        this.mPathForBorder.moveTo(x1, y1);
        this.mPathForBorder.lineTo(x2, y2);
        this.mPathForBorder.lineTo(x3, y3);
        this.mPathForBorder.lineTo(x4, y4);
        this.mPathForBorder.lineTo(x1, y1);
        canvas.drawPath(this.mPathForBorder, this.mPaint);
    }

    private int getBorderWidth(int position) {
        Spacing spacing = this.mBorderWidth;
        if (spacing == null) {
            return 0;
        }
        float f = spacing.get(position);
        if (!YogaConstants.isUndefined(f)) {
            return Math.round(f);
        }
        return -1;
    }

    private boolean isBorderColorDefined(int position) {
        Spacing spacing = this.mBorderRGB;
        float f = Float.NaN;
        float f2 = spacing != null ? spacing.get(position) : Float.NaN;
        Spacing spacing2 = this.mBorderAlpha;
        if (spacing2 != null) {
            f = spacing2.get(position);
        }
        return !YogaConstants.isUndefined(f2) && !YogaConstants.isUndefined(f);
    }

    private int getBorderColor(int position) {
        Spacing spacing = this.mBorderRGB;
        float f = spacing != null ? spacing.get(position) : 0.0f;
        Spacing spacing2 = this.mBorderAlpha;
        return colorFromAlphaAndRGBComponents(spacing2 != null ? spacing2.get(position) : 255.0f, f);
    }

    public RectF getDirectionAwareBorderInsets() {
        float borderWidthOrDefaultTo = getBorderWidthOrDefaultTo(0.0f, 8);
        boolean z = true;
        float borderWidthOrDefaultTo2 = getBorderWidthOrDefaultTo(borderWidthOrDefaultTo, 1);
        float borderWidthOrDefaultTo3 = getBorderWidthOrDefaultTo(borderWidthOrDefaultTo, 3);
        float borderWidthOrDefaultTo4 = getBorderWidthOrDefaultTo(borderWidthOrDefaultTo, 0);
        float borderWidthOrDefaultTo5 = getBorderWidthOrDefaultTo(borderWidthOrDefaultTo, 2);
        if (this.mBorderWidth != null) {
            if (getResolvedLayoutDirection() != 1) {
                z = false;
            }
            float raw = this.mBorderWidth.getRaw(4);
            float raw2 = this.mBorderWidth.getRaw(5);
            if (I18nUtil.getInstance().doLeftAndRightSwapInRTL(this.mContext)) {
                if (!YogaConstants.isUndefined(raw)) {
                    borderWidthOrDefaultTo4 = raw;
                }
                if (!YogaConstants.isUndefined(raw2)) {
                    borderWidthOrDefaultTo5 = raw2;
                }
                float f = z ? borderWidthOrDefaultTo5 : borderWidthOrDefaultTo4;
                if (z) {
                    borderWidthOrDefaultTo5 = borderWidthOrDefaultTo4;
                }
                borderWidthOrDefaultTo4 = f;
            } else {
                float f2 = z ? raw2 : raw;
                if (!z) {
                    raw = raw2;
                }
                if (!YogaConstants.isUndefined(f2)) {
                    borderWidthOrDefaultTo4 = f2;
                }
                if (!YogaConstants.isUndefined(raw)) {
                    borderWidthOrDefaultTo5 = raw;
                }
            }
        }
        return new RectF(borderWidthOrDefaultTo4, borderWidthOrDefaultTo2, borderWidthOrDefaultTo5, borderWidthOrDefaultTo3);
    }
}
