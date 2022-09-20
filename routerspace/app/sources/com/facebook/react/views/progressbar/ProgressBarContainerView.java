package com.facebook.react.views.progressbar;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class ProgressBarContainerView extends FrameLayout {
    private static final int MAX_PROGRESS = 1000;
    private Integer mColor;
    private double mProgress;
    private ProgressBar mProgressBar;
    private boolean mIndeterminate = true;
    private boolean mAnimating = true;

    public ProgressBarContainerView(Context context) {
        super(context);
    }

    public void setStyle(String styleName) {
        ProgressBar createProgressBar = ReactProgressBarViewManager.createProgressBar(getContext(), ReactProgressBarViewManager.getStyleFromString(styleName));
        this.mProgressBar = createProgressBar;
        createProgressBar.setMax(1000);
        removeAllViews();
        addView(this.mProgressBar, new ViewGroup.LayoutParams(-1, -1));
    }

    public void setColor(Integer color) {
        this.mColor = color;
    }

    public void setIndeterminate(boolean indeterminate) {
        this.mIndeterminate = indeterminate;
    }

    public void setProgress(double progress) {
        this.mProgress = progress;
    }

    public void setAnimating(boolean animating) {
        this.mAnimating = animating;
    }

    public void apply() {
        ProgressBar progressBar = this.mProgressBar;
        if (progressBar == null) {
            throw new JSApplicationIllegalArgumentException("setStyle() not called");
        }
        progressBar.setIndeterminate(this.mIndeterminate);
        setColor(this.mProgressBar);
        this.mProgressBar.setProgress((int) (this.mProgress * 1000.0d));
        if (this.mAnimating) {
            this.mProgressBar.setVisibility(0);
        } else {
            this.mProgressBar.setVisibility(4);
        }
    }

    private void setColor(ProgressBar progressBar) {
        Drawable drawable;
        if (progressBar.isIndeterminate()) {
            drawable = progressBar.getIndeterminateDrawable();
        } else {
            drawable = progressBar.getProgressDrawable();
        }
        if (drawable == null) {
            return;
        }
        Integer num = this.mColor;
        if (num != null) {
            drawable.setColorFilter(num.intValue(), PorterDuff.Mode.SRC_IN);
        } else {
            drawable.clearColorFilter();
        }
    }
}
