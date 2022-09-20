package com.th3rdwave.safeareacontext;

import android.content.Context;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.views.view.ReactViewGroup;
/* loaded from: classes.dex */
public class SafeAreaProvider extends ReactViewGroup implements ViewTreeObserver.OnPreDrawListener {
    private OnInsetsChangeListener mInsetsChangeListener;
    private Rect mLastFrame;
    private EdgeInsets mLastInsets;

    /* loaded from: classes.dex */
    public interface OnInsetsChangeListener {
        void onInsetsChange(SafeAreaProvider view, EdgeInsets insets, Rect frame);
    }

    public SafeAreaProvider(Context context) {
        super(context);
    }

    private void maybeUpdateInsets() {
        EdgeInsets safeAreaInsets = SafeAreaUtils.getSafeAreaInsets(this);
        Rect frame = SafeAreaUtils.getFrame((ViewGroup) getRootView(), this);
        if (safeAreaInsets == null || frame == null) {
            return;
        }
        EdgeInsets edgeInsets = this.mLastInsets;
        if (edgeInsets != null && this.mLastFrame != null && edgeInsets.equalsToEdgeInsets(safeAreaInsets) && this.mLastFrame.equalsToRect(frame)) {
            return;
        }
        ((OnInsetsChangeListener) Assertions.assertNotNull(this.mInsetsChangeListener)).onInsetsChange(this, safeAreaInsets, frame);
        this.mLastInsets = safeAreaInsets;
        this.mLastFrame = frame;
    }

    @Override // com.facebook.react.views.view.ReactViewGroup, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnPreDrawListener(this);
        maybeUpdateInsets();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnPreDrawListener(this);
    }

    @Override // android.view.ViewTreeObserver.OnPreDrawListener
    public boolean onPreDraw() {
        maybeUpdateInsets();
        return true;
    }

    public void setOnInsetsChangeListener(OnInsetsChangeListener listener) {
        this.mInsetsChangeListener = listener;
    }
}
