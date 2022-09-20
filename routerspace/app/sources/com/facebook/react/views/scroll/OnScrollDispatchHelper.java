package com.facebook.react.views.scroll;

import android.os.SystemClock;
/* loaded from: classes.dex */
public class OnScrollDispatchHelper {
    private static final int MIN_EVENT_SEPARATION_MS = 10;
    private int mPrevX = Integer.MIN_VALUE;
    private int mPrevY = Integer.MIN_VALUE;
    private float mXFlingVelocity = 0.0f;
    private float mYFlingVelocity = 0.0f;
    private long mLastScrollEventTimeMs = -11;

    public boolean onScrollChanged(int x, int y) {
        long uptimeMillis = SystemClock.uptimeMillis();
        long j = this.mLastScrollEventTimeMs;
        boolean z = (uptimeMillis - j <= 10 && this.mPrevX == x && this.mPrevY == y) ? false : true;
        if (uptimeMillis - j != 0) {
            this.mXFlingVelocity = (x - this.mPrevX) / ((float) (uptimeMillis - j));
            this.mYFlingVelocity = (y - this.mPrevY) / ((float) (uptimeMillis - j));
        }
        this.mLastScrollEventTimeMs = uptimeMillis;
        this.mPrevX = x;
        this.mPrevY = y;
        return z;
    }

    public float getXFlingVelocity() {
        return this.mXFlingVelocity;
    }

    public float getYFlingVelocity() {
        return this.mYFlingVelocity;
    }
}
