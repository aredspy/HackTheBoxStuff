package com.facebook.drawee.drawable;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import com.facebook.react.uimanager.ViewProps;
/* loaded from: classes.dex */
public class InstrumentedDrawable extends ForwardingDrawable {
    private boolean mIsChecked = false;
    private final Listener mListener;
    private final String mScaleType;

    /* loaded from: classes.dex */
    public interface Listener {
        void track(int viewWidth, int viewHeight, int imageWidth, int imageHeight, int scaledWidth, int scaledHeight, String scaleType);
    }

    public InstrumentedDrawable(Drawable drawable, Listener listener) {
        super(drawable);
        this.mListener = listener;
        this.mScaleType = getScaleType(drawable);
    }

    private String getScaleType(Drawable drawable) {
        return drawable instanceof ScaleTypeDrawable ? ((ScaleTypeDrawable) drawable).getScaleType().toString() : ViewProps.NONE;
    }

    @Override // com.facebook.drawee.drawable.ForwardingDrawable, android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        if (!this.mIsChecked) {
            this.mIsChecked = true;
            RectF rectF = new RectF();
            getRootBounds(rectF);
            int width = (int) rectF.width();
            int height = (int) rectF.height();
            getTransformedBounds(rectF);
            int width2 = (int) rectF.width();
            int height2 = (int) rectF.height();
            int intrinsicWidth = getIntrinsicWidth();
            int intrinsicHeight = getIntrinsicHeight();
            Listener listener = this.mListener;
            if (listener != null) {
                listener.track(width, height, intrinsicWidth, intrinsicHeight, width2, height2, this.mScaleType);
            }
        }
        super.draw(canvas);
    }
}
