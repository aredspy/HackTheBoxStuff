package com.facebook.react.uimanager.layoutanimation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
/* loaded from: classes.dex */
class OpacityAnimation extends Animation {
    private final float mDeltaOpacity;
    private final float mStartOpacity;
    private final View mView;

    @Override // android.view.animation.Animation
    public boolean willChangeBounds() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class OpacityAnimationListener implements Animation.AnimationListener {
        private boolean mLayerTypeChanged = false;
        private final View mView;

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationRepeat(Animation animation) {
        }

        public OpacityAnimationListener(View view) {
            this.mView = view;
        }

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationStart(Animation animation) {
            if (!this.mView.hasOverlappingRendering() || this.mView.getLayerType() != 0) {
                return;
            }
            this.mLayerTypeChanged = true;
            this.mView.setLayerType(2, null);
        }

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationEnd(Animation animation) {
            if (this.mLayerTypeChanged) {
                this.mView.setLayerType(0, null);
            }
        }
    }

    public OpacityAnimation(View view, float startOpacity, float endOpacity) {
        this.mView = view;
        this.mStartOpacity = startOpacity;
        this.mDeltaOpacity = endOpacity - startOpacity;
        setAnimationListener(new OpacityAnimationListener(view));
    }

    @Override // android.view.animation.Animation
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        this.mView.setAlpha(this.mStartOpacity + (this.mDeltaOpacity * interpolatedTime));
    }
}
