package com.facebook.react.views.scroll;

import android.view.MotionEvent;
import android.view.VelocityTracker;
/* loaded from: classes.dex */
public class VelocityHelper {
    private VelocityTracker mVelocityTracker;
    private float mXVelocity;
    private float mYVelocity;

    public void calculateVelocity(MotionEvent ev) {
        int action = ev.getAction() & 255;
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(ev);
        if (action == 1 || action == 3) {
            this.mVelocityTracker.computeCurrentVelocity(1);
            this.mXVelocity = this.mVelocityTracker.getXVelocity();
            this.mYVelocity = this.mVelocityTracker.getYVelocity();
            VelocityTracker velocityTracker = this.mVelocityTracker;
            if (velocityTracker == null) {
                return;
            }
            velocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    public float getXVelocity() {
        return this.mXVelocity;
    }

    public float getYVelocity() {
        return this.mYVelocity;
    }
}
