package com.facebook.react.views.text;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.ViewGroup;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.TintContextWrapper;
import androidx.core.os.EnvironmentCompat;
import androidx.core.view.GravityCompat;
import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.ReactCompoundView;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.views.view.ReactViewBackgroundManager;
/* loaded from: classes.dex */
public class ReactTextView extends AppCompatTextView implements ReactCompoundView {
    private static final ViewGroup.LayoutParams EMPTY_LAYOUT_PARAMS = new ViewGroup.LayoutParams(0, 0);
    private boolean mContainsImages;
    private boolean mNotifyOnInlineViewLayout;
    private Spannable mSpanned;
    private int mTextAlign = 0;
    private int mNumberOfLines = Integer.MAX_VALUE;
    private TextUtils.TruncateAt mEllipsizeLocation = TextUtils.TruncateAt.END;
    private boolean mAdjustsFontSizeToFit = false;
    private int mLinkifyMaskType = 0;
    private ReactViewBackgroundManager mReactBackgroundManager = new ReactViewBackgroundManager(this);
    private int mDefaultGravityHorizontal = getGravity() & GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK;
    private int mDefaultGravityVertical = getGravity() & 112;

    @Override // android.widget.TextView, android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    public ReactTextView(Context context) {
        super(context);
    }

    private static WritableMap inlineViewJson(int visibility, int index, int left, int top, int right, int bottom) {
        WritableMap createMap = Arguments.createMap();
        if (visibility == 8) {
            createMap.putString("visibility", "gone");
            createMap.putInt("index", index);
        } else if (visibility == 0) {
            createMap.putString("visibility", ViewProps.VISIBLE);
            createMap.putInt("index", index);
            createMap.putDouble(ViewProps.LEFT, PixelUtil.toDIPFromPixel(left));
            createMap.putDouble(ViewProps.TOP, PixelUtil.toDIPFromPixel(top));
            createMap.putDouble(ViewProps.RIGHT, PixelUtil.toDIPFromPixel(right));
            createMap.putDouble(ViewProps.BOTTOM, PixelUtil.toDIPFromPixel(bottom));
        } else {
            createMap.putString("visibility", EnvironmentCompat.MEDIA_UNKNOWN);
            createMap.putInt("index", index);
        }
        return createMap;
    }

    private ReactContext getReactContext() {
        Context context = getContext();
        if (context instanceof TintContextWrapper) {
            context = ((TintContextWrapper) context).getBaseContext();
        }
        return (ReactContext) context;
    }

    /* JADX WARN: Code restructure failed: missing block: B:45:0x00df, code lost:
        if (r5 != false) goto L46;
     */
    /* JADX WARN: Removed duplicated region for block: B:48:0x00e4  */
    /* JADX WARN: Removed duplicated region for block: B:49:0x00e9  */
    /* JADX WARN: Removed duplicated region for block: B:55:0x0105  */
    /* JADX WARN: Removed duplicated region for block: B:56:0x0108  */
    /* JADX WARN: Removed duplicated region for block: B:59:0x0116  */
    /* JADX WARN: Removed duplicated region for block: B:76:0x0148 A[SYNTHETIC] */
    @Override // androidx.appcompat.widget.AppCompatTextView, android.widget.TextView, android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void onLayout(boolean r23, int r24, int r25, int r26, int r27) {
        /*
            Method dump skipped, instructions count: 396
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.facebook.react.views.text.ReactTextView.onLayout(boolean, int, int, int, int):void");
    }

    public void setText(ReactTextUpdate update) {
        int i;
        this.mContainsImages = update.containsImages();
        if (getLayoutParams() == null) {
            setLayoutParams(EMPTY_LAYOUT_PARAMS);
        }
        Spannable text = update.getText();
        int i2 = this.mLinkifyMaskType;
        if (i2 > 0) {
            Linkify.addLinks(text, i2);
            setMovementMethod(LinkMovementMethod.getInstance());
        }
        setText(text);
        float paddingLeft = update.getPaddingLeft();
        float paddingTop = update.getPaddingTop();
        float paddingRight = update.getPaddingRight();
        float paddingBottom = update.getPaddingBottom();
        if (paddingLeft != -1.0f && paddingBottom != -1.0f && paddingRight != -1.0f && i != 0) {
            setPadding((int) Math.floor(paddingLeft), (int) Math.floor(paddingTop), (int) Math.floor(paddingRight), (int) Math.floor(paddingBottom));
        }
        int textAlign = update.getTextAlign();
        if (this.mTextAlign != textAlign) {
            this.mTextAlign = textAlign;
        }
        setGravityHorizontal(this.mTextAlign);
        if (Build.VERSION.SDK_INT >= 23 && getBreakStrategy() != update.getTextBreakStrategy()) {
            setBreakStrategy(update.getTextBreakStrategy());
        }
        if (Build.VERSION.SDK_INT >= 26 && getJustificationMode() != update.getJustificationMode()) {
            setJustificationMode(update.getJustificationMode());
        }
        requestLayout();
    }

    @Override // com.facebook.react.uimanager.ReactCompoundView
    public int reactTagForTouch(float touchX, float touchY) {
        int i;
        CharSequence text = getText();
        int id = getId();
        int i2 = (int) touchX;
        int i3 = (int) touchY;
        Layout layout = getLayout();
        if (layout == null) {
            return id;
        }
        int lineForVertical = layout.getLineForVertical(i3);
        int lineLeft = (int) layout.getLineLeft(lineForVertical);
        int lineRight = (int) layout.getLineRight(lineForVertical);
        if ((text instanceof Spanned) && i2 >= lineLeft && i2 <= lineRight) {
            Spanned spanned = (Spanned) text;
            try {
                int offsetForHorizontal = layout.getOffsetForHorizontal(lineForVertical, i2);
                ReactTagSpan[] reactTagSpanArr = (ReactTagSpan[]) spanned.getSpans(offsetForHorizontal, offsetForHorizontal, ReactTagSpan.class);
                if (reactTagSpanArr != null) {
                    int length = text.length();
                    for (int i4 = 0; i4 < reactTagSpanArr.length; i4++) {
                        int spanStart = spanned.getSpanStart(reactTagSpanArr[i4]);
                        int spanEnd = spanned.getSpanEnd(reactTagSpanArr[i4]);
                        if (spanEnd > offsetForHorizontal && (i = spanEnd - spanStart) <= length) {
                            id = reactTagSpanArr[i4].getReactTag();
                            length = i;
                        }
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                FLog.e(ReactConstants.TAG, "Crash in HorizontalMeasurementProvider: " + e.getMessage());
            }
        }
        return id;
    }

    @Override // android.widget.TextView, android.view.View
    protected boolean verifyDrawable(Drawable drawable) {
        if (this.mContainsImages && (getText() instanceof Spanned)) {
            Spanned spanned = (Spanned) getText();
            for (TextInlineImageSpan textInlineImageSpan : (TextInlineImageSpan[]) spanned.getSpans(0, spanned.length(), TextInlineImageSpan.class)) {
                if (textInlineImageSpan.getDrawable() == drawable) {
                    return true;
                }
            }
        }
        return super.verifyDrawable(drawable);
    }

    @Override // android.widget.TextView, android.view.View, android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable drawable) {
        if (this.mContainsImages && (getText() instanceof Spanned)) {
            Spanned spanned = (Spanned) getText();
            for (TextInlineImageSpan textInlineImageSpan : (TextInlineImageSpan[]) spanned.getSpans(0, spanned.length(), TextInlineImageSpan.class)) {
                if (textInlineImageSpan.getDrawable() == drawable) {
                    invalidate();
                }
            }
        }
        super.invalidateDrawable(drawable);
    }

    @Override // android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (!this.mContainsImages || !(getText() instanceof Spanned)) {
            return;
        }
        Spanned spanned = (Spanned) getText();
        for (TextInlineImageSpan textInlineImageSpan : (TextInlineImageSpan[]) spanned.getSpans(0, spanned.length(), TextInlineImageSpan.class)) {
            textInlineImageSpan.onDetachedFromWindow();
        }
    }

    @Override // android.view.View
    public void onStartTemporaryDetach() {
        super.onStartTemporaryDetach();
        if (!this.mContainsImages || !(getText() instanceof Spanned)) {
            return;
        }
        Spanned spanned = (Spanned) getText();
        for (TextInlineImageSpan textInlineImageSpan : (TextInlineImageSpan[]) spanned.getSpans(0, spanned.length(), TextInlineImageSpan.class)) {
            textInlineImageSpan.onStartTemporaryDetach();
        }
    }

    @Override // android.widget.TextView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!this.mContainsImages || !(getText() instanceof Spanned)) {
            return;
        }
        Spanned spanned = (Spanned) getText();
        for (TextInlineImageSpan textInlineImageSpan : (TextInlineImageSpan[]) spanned.getSpans(0, spanned.length(), TextInlineImageSpan.class)) {
            textInlineImageSpan.onAttachedToWindow();
        }
    }

    @Override // android.view.View
    public void onFinishTemporaryDetach() {
        super.onFinishTemporaryDetach();
        if (!this.mContainsImages || !(getText() instanceof Spanned)) {
            return;
        }
        Spanned spanned = (Spanned) getText();
        for (TextInlineImageSpan textInlineImageSpan : (TextInlineImageSpan[]) spanned.getSpans(0, spanned.length(), TextInlineImageSpan.class)) {
            textInlineImageSpan.onFinishTemporaryDetach();
        }
    }

    void setGravityHorizontal(int gravityHorizontal) {
        if (gravityHorizontal == 0) {
            gravityHorizontal = this.mDefaultGravityHorizontal;
        }
        setGravity(gravityHorizontal | (getGravity() & (-8) & (-8388616)));
    }

    public void setGravityVertical(int gravityVertical) {
        if (gravityVertical == 0) {
            gravityVertical = this.mDefaultGravityVertical;
        }
        setGravity(gravityVertical | (getGravity() & (-113)));
    }

    public void setNumberOfLines(int numberOfLines) {
        if (numberOfLines == 0) {
            numberOfLines = Integer.MAX_VALUE;
        }
        this.mNumberOfLines = numberOfLines;
        boolean z = true;
        if (numberOfLines != 1) {
            z = false;
        }
        setSingleLine(z);
        setMaxLines(this.mNumberOfLines);
    }

    public void setAdjustFontSizeToFit(boolean adjustsFontSizeToFit) {
        this.mAdjustsFontSizeToFit = adjustsFontSizeToFit;
    }

    public void setEllipsizeLocation(TextUtils.TruncateAt ellipsizeLocation) {
        this.mEllipsizeLocation = ellipsizeLocation;
    }

    public void setNotifyOnInlineViewLayout(boolean notifyOnInlineViewLayout) {
        this.mNotifyOnInlineViewLayout = notifyOnInlineViewLayout;
    }

    public void updateView() {
        setEllipsize((this.mNumberOfLines == Integer.MAX_VALUE || this.mAdjustsFontSizeToFit) ? null : this.mEllipsizeLocation);
    }

    @Override // android.view.View
    public void setBackgroundColor(int color) {
        this.mReactBackgroundManager.setBackgroundColor(color);
    }

    public void setBorderWidth(int position, float width) {
        this.mReactBackgroundManager.setBorderWidth(position, width);
    }

    public void setBorderColor(int position, float color, float alpha) {
        this.mReactBackgroundManager.setBorderColor(position, color, alpha);
    }

    public void setBorderRadius(float borderRadius) {
        this.mReactBackgroundManager.setBorderRadius(borderRadius);
    }

    public void setBorderRadius(float borderRadius, int position) {
        this.mReactBackgroundManager.setBorderRadius(borderRadius, position);
    }

    public void setBorderStyle(String style) {
        this.mReactBackgroundManager.setBorderStyle(style);
    }

    public void setSpanned(Spannable spanned) {
        this.mSpanned = spanned;
    }

    public Spannable getSpanned() {
        return this.mSpanned;
    }

    public void setLinkifyMask(int mask) {
        this.mLinkifyMaskType = mask;
    }
}
