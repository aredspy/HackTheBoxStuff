package com.facebook.react.uimanager.layoutanimation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
/* loaded from: classes.dex */
class PositionAndSizeAnimation extends Animation implements LayoutHandlingAnimation {
    private int mDeltaHeight;
    private int mDeltaWidth;
    private float mDeltaX;
    private float mDeltaY;
    private int mStartHeight;
    private int mStartWidth;
    private float mStartX;
    private float mStartY;
    private final View mView;

    @Override // android.view.animation.Animation
    public boolean willChangeBounds() {
        return true;
    }

    public PositionAndSizeAnimation(View view, int x, int y, int width, int height) {
        this.mView = view;
        calculateAnimation(x, y, width, height);
    }

    @Override // android.view.animation.Animation
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float f = this.mStartX + (this.mDeltaX * interpolatedTime);
        float f2 = this.mStartY + (this.mDeltaY * interpolatedTime);
        this.mView.layout(Math.round(f), Math.round(f2), Math.round(f + this.mStartWidth + (this.mDeltaWidth * interpolatedTime)), Math.round(f2 + this.mStartHeight + (this.mDeltaHeight * interpolatedTime)));
    }

    @Override // com.facebook.react.uimanager.layoutanimation.LayoutHandlingAnimation
    public void onLayoutUpdate(int x, int y, int width, int height) {
        calculateAnimation(x, y, width, height);
    }

    private void calculateAnimation(int x, int y, int width, int height) {
        this.mStartX = this.mView.getX() - this.mView.getTranslationX();
        this.mStartY = this.mView.getY() - this.mView.getTranslationY();
        this.mStartWidth = this.mView.getWidth();
        int height2 = this.mView.getHeight();
        this.mStartHeight = height2;
        this.mDeltaX = x - this.mStartX;
        this.mDeltaY = y - this.mStartY;
        this.mDeltaWidth = width - this.mStartWidth;
        this.mDeltaHeight = height - height2;
    }
}
