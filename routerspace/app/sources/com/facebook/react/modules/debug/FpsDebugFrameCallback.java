package com.facebook.react.modules.debug;

import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.modules.core.ChoreographerCompat;
import com.facebook.react.uimanager.UIManagerModule;
import java.util.Map;
import java.util.TreeMap;
/* loaded from: classes.dex */
public class FpsDebugFrameCallback extends ChoreographerCompat.FrameCallback {
    private static final double EXPECTED_FRAME_TIME = 16.9d;
    private ChoreographerCompat mChoreographer;
    private final ReactContext mReactContext;
    private TreeMap<Long, FpsInfo> mTimeToFps;
    private final UIManagerModule mUIManagerModule;
    private boolean mShouldStop = false;
    private long mFirstFrameTime = -1;
    private long mLastFrameTime = -1;
    private int mNumFrameCallbacks = 0;
    private int mExpectedNumFramesPrev = 0;
    private int m4PlusFrameStutters = 0;
    private int mNumFrameCallbacksWithBatchDispatches = 0;
    private boolean mIsRecordingFpsInfoAtEachFrame = false;
    private final DidJSUpdateUiDuringFrameDetector mDidJSUpdateUiDuringFrameDetector = new DidJSUpdateUiDuringFrameDetector();

    /* loaded from: classes.dex */
    public static class FpsInfo {
        public final double fps;
        public final double jsFps;
        public final int total4PlusFrameStutters;
        public final int totalExpectedFrames;
        public final int totalFrames;
        public final int totalJsFrames;
        public final int totalTimeMs;

        public FpsInfo(int totalFrames, int totalJsFrames, int totalExpectedFrames, int total4PlusFrameStutters, double fps, double jsFps, int totalTimeMs) {
            this.totalFrames = totalFrames;
            this.totalJsFrames = totalJsFrames;
            this.totalExpectedFrames = totalExpectedFrames;
            this.total4PlusFrameStutters = total4PlusFrameStutters;
            this.fps = fps;
            this.jsFps = jsFps;
            this.totalTimeMs = totalTimeMs;
        }
    }

    public FpsDebugFrameCallback(ReactContext reactContext) {
        this.mReactContext = reactContext;
        this.mUIManagerModule = (UIManagerModule) Assertions.assertNotNull(reactContext.getNativeModule(UIManagerModule.class));
    }

    @Override // com.facebook.react.modules.core.ChoreographerCompat.FrameCallback
    public void doFrame(long l) {
        if (this.mShouldStop) {
            return;
        }
        if (this.mFirstFrameTime == -1) {
            this.mFirstFrameTime = l;
        }
        long j = this.mLastFrameTime;
        this.mLastFrameTime = l;
        if (this.mDidJSUpdateUiDuringFrameDetector.getDidJSHitFrameAndCleanup(j, l)) {
            this.mNumFrameCallbacksWithBatchDispatches++;
        }
        this.mNumFrameCallbacks++;
        int expectedNumFrames = getExpectedNumFrames();
        if ((expectedNumFrames - this.mExpectedNumFramesPrev) - 1 >= 4) {
            this.m4PlusFrameStutters++;
        }
        if (this.mIsRecordingFpsInfoAtEachFrame) {
            Assertions.assertNotNull(this.mTimeToFps);
            this.mTimeToFps.put(Long.valueOf(System.currentTimeMillis()), new FpsInfo(getNumFrames(), getNumJSFrames(), expectedNumFrames, this.m4PlusFrameStutters, getFPS(), getJSFPS(), getTotalTimeMS()));
        }
        this.mExpectedNumFramesPrev = expectedNumFrames;
        ChoreographerCompat choreographerCompat = this.mChoreographer;
        if (choreographerCompat == null) {
            return;
        }
        choreographerCompat.postFrameCallback(this);
    }

    public void start() {
        this.mShouldStop = false;
        this.mReactContext.getCatalystInstance().addBridgeIdleDebugListener(this.mDidJSUpdateUiDuringFrameDetector);
        this.mUIManagerModule.setViewHierarchyUpdateDebugListener(this.mDidJSUpdateUiDuringFrameDetector);
        UiThreadUtil.runOnUiThread(new Runnable() { // from class: com.facebook.react.modules.debug.FpsDebugFrameCallback.1
            @Override // java.lang.Runnable
            public void run() {
                FpsDebugFrameCallback.this.mChoreographer = ChoreographerCompat.getInstance();
                FpsDebugFrameCallback.this.mChoreographer.postFrameCallback(this);
            }
        });
    }

    public void startAndRecordFpsAtEachFrame() {
        this.mTimeToFps = new TreeMap<>();
        this.mIsRecordingFpsInfoAtEachFrame = true;
        start();
    }

    public void stop() {
        this.mShouldStop = true;
        this.mReactContext.getCatalystInstance().removeBridgeIdleDebugListener(this.mDidJSUpdateUiDuringFrameDetector);
        this.mUIManagerModule.setViewHierarchyUpdateDebugListener(null);
    }

    public double getFPS() {
        if (this.mLastFrameTime == this.mFirstFrameTime) {
            return 0.0d;
        }
        return (getNumFrames() * 1.0E9d) / (this.mLastFrameTime - this.mFirstFrameTime);
    }

    public double getJSFPS() {
        if (this.mLastFrameTime == this.mFirstFrameTime) {
            return 0.0d;
        }
        return (getNumJSFrames() * 1.0E9d) / (this.mLastFrameTime - this.mFirstFrameTime);
    }

    public int getNumFrames() {
        return this.mNumFrameCallbacks - 1;
    }

    public int getNumJSFrames() {
        return this.mNumFrameCallbacksWithBatchDispatches - 1;
    }

    public int getExpectedNumFrames() {
        return (int) ((getTotalTimeMS() / EXPECTED_FRAME_TIME) + 1.0d);
    }

    public int get4PlusFrameStutters() {
        return this.m4PlusFrameStutters;
    }

    public int getTotalTimeMS() {
        return ((int) (this.mLastFrameTime - this.mFirstFrameTime)) / 1000000;
    }

    public FpsInfo getFpsInfo(long upToTimeMs) {
        Assertions.assertNotNull(this.mTimeToFps, "FPS was not recorded at each frame!");
        Map.Entry<Long, FpsInfo> floorEntry = this.mTimeToFps.floorEntry(Long.valueOf(upToTimeMs));
        if (floorEntry == null) {
            return null;
        }
        return floorEntry.getValue();
    }

    public void reset() {
        this.mFirstFrameTime = -1L;
        this.mLastFrameTime = -1L;
        this.mNumFrameCallbacks = 0;
        this.m4PlusFrameStutters = 0;
        this.mNumFrameCallbacksWithBatchDispatches = 0;
        this.mIsRecordingFpsInfoAtEachFrame = false;
        this.mTimeToFps = null;
    }
}
