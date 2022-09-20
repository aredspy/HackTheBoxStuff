package com.facebook.react.modules.core;

import android.os.Handler;
import android.view.Choreographer;
import com.facebook.react.bridge.UiThreadUtil;
/* loaded from: classes.dex */
public class ChoreographerCompat {
    private static final long ONE_FRAME_MILLIS = 17;
    private static ChoreographerCompat sInstance;
    private Choreographer mChoreographer = getChoreographer();
    private Handler mHandler;

    public static ChoreographerCompat getInstance() {
        UiThreadUtil.assertOnUiThread();
        if (sInstance == null) {
            sInstance = new ChoreographerCompat();
        }
        return sInstance;
    }

    private ChoreographerCompat() {
    }

    public void postFrameCallback(FrameCallback callbackWrapper) {
        choreographerPostFrameCallback(callbackWrapper.getFrameCallback());
    }

    public void postFrameCallbackDelayed(FrameCallback callbackWrapper, long delayMillis) {
        choreographerPostFrameCallbackDelayed(callbackWrapper.getFrameCallback(), delayMillis);
    }

    public void removeFrameCallback(FrameCallback callbackWrapper) {
        choreographerRemoveFrameCallback(callbackWrapper.getFrameCallback());
    }

    private Choreographer getChoreographer() {
        return Choreographer.getInstance();
    }

    private void choreographerPostFrameCallback(Choreographer.FrameCallback frameCallback) {
        this.mChoreographer.postFrameCallback(frameCallback);
    }

    private void choreographerPostFrameCallbackDelayed(Choreographer.FrameCallback frameCallback, long delayMillis) {
        this.mChoreographer.postFrameCallbackDelayed(frameCallback, delayMillis);
    }

    private void choreographerRemoveFrameCallback(Choreographer.FrameCallback frameCallback) {
        this.mChoreographer.removeFrameCallback(frameCallback);
    }

    /* loaded from: classes.dex */
    public static abstract class FrameCallback {
        private Choreographer.FrameCallback mFrameCallback;
        private Runnable mRunnable;

        public abstract void doFrame(long frameTimeNanos);

        Choreographer.FrameCallback getFrameCallback() {
            if (this.mFrameCallback == null) {
                this.mFrameCallback = new Choreographer.FrameCallback() { // from class: com.facebook.react.modules.core.ChoreographerCompat.FrameCallback.1
                    @Override // android.view.Choreographer.FrameCallback
                    public void doFrame(long frameTimeNanos) {
                        FrameCallback.this.doFrame(frameTimeNanos);
                    }
                };
            }
            return this.mFrameCallback;
        }

        Runnable getRunnable() {
            if (this.mRunnable == null) {
                this.mRunnable = new Runnable() { // from class: com.facebook.react.modules.core.ChoreographerCompat.FrameCallback.2
                    @Override // java.lang.Runnable
                    public void run() {
                        FrameCallback.this.doFrame(System.nanoTime());
                    }
                };
            }
            return this.mRunnable;
        }
    }
}
