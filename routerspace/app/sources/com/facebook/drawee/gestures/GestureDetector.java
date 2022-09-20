package com.facebook.drawee.gestures;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class GestureDetector {
    long mActionDownTime;
    float mActionDownX;
    float mActionDownY;
    @Nullable
    ClickListener mClickListener;
    boolean mIsCapturingGesture;
    boolean mIsClickCandidate;
    final float mSingleTapSlopPx;

    /* loaded from: classes.dex */
    public interface ClickListener {
        boolean onClick();
    }

    public GestureDetector(Context context) {
        this.mSingleTapSlopPx = ViewConfiguration.get(context).getScaledTouchSlop();
        init();
    }

    public static GestureDetector newInstance(Context context) {
        return new GestureDetector(context);
    }

    public void init() {
        this.mClickListener = null;
        reset();
    }

    public void reset() {
        this.mIsCapturingGesture = false;
        this.mIsClickCandidate = false;
    }

    public void setClickListener(ClickListener clickListener) {
        this.mClickListener = clickListener;
    }

    public boolean isCapturingGesture() {
        return this.mIsCapturingGesture;
    }

    public boolean onTouchEvent(MotionEvent event) {
        ClickListener clickListener;
        int action = event.getAction();
        if (action == 0) {
            this.mIsCapturingGesture = true;
            this.mIsClickCandidate = true;
            this.mActionDownTime = event.getEventTime();
            this.mActionDownX = event.getX();
            this.mActionDownY = event.getY();
        } else if (action == 1) {
            this.mIsCapturingGesture = false;
            if (Math.abs(event.getX() - this.mActionDownX) > this.mSingleTapSlopPx || Math.abs(event.getY() - this.mActionDownY) > this.mSingleTapSlopPx) {
                this.mIsClickCandidate = false;
            }
            if (this.mIsClickCandidate && event.getEventTime() - this.mActionDownTime <= ViewConfiguration.getLongPressTimeout() && (clickListener = this.mClickListener) != null) {
                clickListener.onClick();
            }
            this.mIsClickCandidate = false;
        } else if (action != 2) {
            if (action == 3) {
                this.mIsCapturingGesture = false;
                this.mIsClickCandidate = false;
            }
        } else if (Math.abs(event.getX() - this.mActionDownX) > this.mSingleTapSlopPx || Math.abs(event.getY() - this.mActionDownY) > this.mSingleTapSlopPx) {
            this.mIsClickCandidate = false;
        }
        return true;
    }
}
