package com.facebook.react.modules.core;

import android.util.SparseArray;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.common.SystemClock;
import com.facebook.react.devsupport.interfaces.DevSupportManager;
import com.facebook.react.jstasks.HeadlessJsTaskContext;
import com.facebook.react.modules.core.ChoreographerCompat;
import com.facebook.react.modules.core.ReactChoreographer;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicBoolean;
/* loaded from: classes.dex */
public class JavaTimerManager {
    private static final float FRAME_DURATION_MS = 16.666666f;
    private static final float IDLE_CALLBACK_FRAME_DEADLINE_MS = 1.0f;
    private IdleCallbackRunnable mCurrentIdleCallbackRunnable;
    private final DevSupportManager mDevSupportManager;
    private final JavaScriptTimerManager mJavaScriptTimerManager;
    private final ReactApplicationContext mReactApplicationContext;
    private final ReactChoreographer mReactChoreographer;
    private final Object mTimerGuard = new Object();
    private final Object mIdleCallbackGuard = new Object();
    private final AtomicBoolean isPaused = new AtomicBoolean(true);
    private final AtomicBoolean isRunningTasks = new AtomicBoolean(false);
    private final TimerFrameCallback mTimerFrameCallback = new TimerFrameCallback();
    private final IdleFrameCallback mIdleFrameCallback = new IdleFrameCallback();
    private boolean mFrameCallbackPosted = false;
    private boolean mFrameIdleCallbackPosted = false;
    private boolean mSendIdleEvents = false;
    private final PriorityQueue<Timer> mTimers = new PriorityQueue<>(11, new Comparator<Timer>() { // from class: com.facebook.react.modules.core.JavaTimerManager.1
        public int compare(Timer lhs, Timer rhs) {
            int i = ((lhs.mTargetTime - rhs.mTargetTime) > 0L ? 1 : ((lhs.mTargetTime - rhs.mTargetTime) == 0L ? 0 : -1));
            if (i == 0) {
                return 0;
            }
            return i < 0 ? -1 : 1;
        }
    });
    private final SparseArray<Timer> mTimerIdsToTimers = new SparseArray<>();

    /* loaded from: classes.dex */
    public static class Timer {
        private final int mCallbackID;
        private final int mInterval;
        private final boolean mRepeat;
        private long mTargetTime;

        private Timer(int callbackID, long initialTargetTime, int duration, boolean repeat) {
            this.mCallbackID = callbackID;
            this.mTargetTime = initialTargetTime;
            this.mInterval = duration;
            this.mRepeat = repeat;
        }
    }

    /* loaded from: classes.dex */
    public class TimerFrameCallback extends ChoreographerCompat.FrameCallback {
        private WritableArray mTimersToCall;

        private TimerFrameCallback() {
            JavaTimerManager.this = this$0;
            this.mTimersToCall = null;
        }

        @Override // com.facebook.react.modules.core.ChoreographerCompat.FrameCallback
        public void doFrame(long frameTimeNanos) {
            if (!JavaTimerManager.this.isPaused.get() || JavaTimerManager.this.isRunningTasks.get()) {
                long j = frameTimeNanos / 1000000;
                synchronized (JavaTimerManager.this.mTimerGuard) {
                    while (!JavaTimerManager.this.mTimers.isEmpty() && ((Timer) JavaTimerManager.this.mTimers.peek()).mTargetTime < j) {
                        Timer timer = (Timer) JavaTimerManager.this.mTimers.poll();
                        if (this.mTimersToCall == null) {
                            this.mTimersToCall = Arguments.createArray();
                        }
                        this.mTimersToCall.pushInt(timer.mCallbackID);
                        if (timer.mRepeat) {
                            timer.mTargetTime = timer.mInterval + j;
                            JavaTimerManager.this.mTimers.add(timer);
                        } else {
                            JavaTimerManager.this.mTimerIdsToTimers.remove(timer.mCallbackID);
                        }
                    }
                }
                if (this.mTimersToCall != null) {
                    JavaTimerManager.this.mJavaScriptTimerManager.callTimers(this.mTimersToCall);
                    this.mTimersToCall = null;
                }
                JavaTimerManager.this.mReactChoreographer.postFrameCallback(ReactChoreographer.CallbackType.TIMERS_EVENTS, this);
            }
        }
    }

    /* loaded from: classes.dex */
    public class IdleFrameCallback extends ChoreographerCompat.FrameCallback {
        private IdleFrameCallback() {
            JavaTimerManager.this = this$0;
        }

        @Override // com.facebook.react.modules.core.ChoreographerCompat.FrameCallback
        public void doFrame(long frameTimeNanos) {
            if (!JavaTimerManager.this.isPaused.get() || JavaTimerManager.this.isRunningTasks.get()) {
                if (JavaTimerManager.this.mCurrentIdleCallbackRunnable != null) {
                    JavaTimerManager.this.mCurrentIdleCallbackRunnable.cancel();
                }
                JavaTimerManager javaTimerManager = JavaTimerManager.this;
                javaTimerManager.mCurrentIdleCallbackRunnable = new IdleCallbackRunnable(frameTimeNanos);
                JavaTimerManager.this.mReactApplicationContext.runOnJSQueueThread(JavaTimerManager.this.mCurrentIdleCallbackRunnable);
                JavaTimerManager.this.mReactChoreographer.postFrameCallback(ReactChoreographer.CallbackType.IDLE_EVENT, this);
            }
        }
    }

    /* loaded from: classes.dex */
    private class IdleCallbackRunnable implements Runnable {
        private volatile boolean mCancelled = false;
        private final long mFrameStartTime;

        public IdleCallbackRunnable(long frameStartTime) {
            JavaTimerManager.this = this$0;
            this.mFrameStartTime = frameStartTime;
        }

        @Override // java.lang.Runnable
        public void run() {
            boolean z;
            if (this.mCancelled) {
                return;
            }
            long uptimeMillis = SystemClock.uptimeMillis() - (this.mFrameStartTime / 1000000);
            long currentTimeMillis = SystemClock.currentTimeMillis() - uptimeMillis;
            if (JavaTimerManager.FRAME_DURATION_MS - ((float) uptimeMillis) < 1.0f) {
                return;
            }
            synchronized (JavaTimerManager.this.mIdleCallbackGuard) {
                z = JavaTimerManager.this.mSendIdleEvents;
            }
            if (z) {
                JavaTimerManager.this.mJavaScriptTimerManager.callIdleCallbacks(currentTimeMillis);
            }
            JavaTimerManager.this.mCurrentIdleCallbackRunnable = null;
        }

        public void cancel() {
            this.mCancelled = true;
        }
    }

    public JavaTimerManager(ReactApplicationContext reactContext, JavaScriptTimerManager javaScriptTimerManager, ReactChoreographer reactChoreographer, DevSupportManager devSupportManager) {
        this.mReactApplicationContext = reactContext;
        this.mJavaScriptTimerManager = javaScriptTimerManager;
        this.mReactChoreographer = reactChoreographer;
        this.mDevSupportManager = devSupportManager;
    }

    public void onHostPause() {
        this.isPaused.set(true);
        clearFrameCallback();
        maybeIdleCallback();
    }

    public void onHostDestroy() {
        clearFrameCallback();
        maybeIdleCallback();
    }

    public void onHostResume() {
        this.isPaused.set(false);
        setChoreographerCallback();
        maybeSetChoreographerIdleCallback();
    }

    public void onHeadlessJsTaskStart(int taskId) {
        if (!this.isRunningTasks.getAndSet(true)) {
            setChoreographerCallback();
            maybeSetChoreographerIdleCallback();
        }
    }

    public void onHeadlessJsTaskFinish(int taskId) {
        if (!HeadlessJsTaskContext.getInstance(this.mReactApplicationContext).hasActiveTasks()) {
            this.isRunningTasks.set(false);
            clearFrameCallback();
            maybeIdleCallback();
        }
    }

    public void onInstanceDestroy() {
        clearFrameCallback();
        clearChoreographerIdleCallback();
    }

    private void maybeSetChoreographerIdleCallback() {
        synchronized (this.mIdleCallbackGuard) {
            if (this.mSendIdleEvents) {
                setChoreographerIdleCallback();
            }
        }
    }

    private void maybeIdleCallback() {
        if (!this.isPaused.get() || this.isRunningTasks.get()) {
            return;
        }
        clearFrameCallback();
    }

    private void setChoreographerCallback() {
        if (!this.mFrameCallbackPosted) {
            this.mReactChoreographer.postFrameCallback(ReactChoreographer.CallbackType.TIMERS_EVENTS, this.mTimerFrameCallback);
            this.mFrameCallbackPosted = true;
        }
    }

    private void clearFrameCallback() {
        HeadlessJsTaskContext headlessJsTaskContext = HeadlessJsTaskContext.getInstance(this.mReactApplicationContext);
        if (!this.mFrameCallbackPosted || !this.isPaused.get() || headlessJsTaskContext.hasActiveTasks()) {
            return;
        }
        this.mReactChoreographer.removeFrameCallback(ReactChoreographer.CallbackType.TIMERS_EVENTS, this.mTimerFrameCallback);
        this.mFrameCallbackPosted = false;
    }

    public void setChoreographerIdleCallback() {
        if (!this.mFrameIdleCallbackPosted) {
            this.mReactChoreographer.postFrameCallback(ReactChoreographer.CallbackType.IDLE_EVENT, this.mIdleFrameCallback);
            this.mFrameIdleCallbackPosted = true;
        }
    }

    public void clearChoreographerIdleCallback() {
        if (this.mFrameIdleCallbackPosted) {
            this.mReactChoreographer.removeFrameCallback(ReactChoreographer.CallbackType.IDLE_EVENT, this.mIdleFrameCallback);
            this.mFrameIdleCallbackPosted = false;
        }
    }

    public void createTimer(final int callbackID, final long delay, final boolean repeat) {
        Timer timer = new Timer(callbackID, (SystemClock.nanoTime() / 1000000) + delay, (int) delay, repeat);
        synchronized (this.mTimerGuard) {
            this.mTimers.add(timer);
            this.mTimerIdsToTimers.put(callbackID, timer);
        }
    }

    public void createAndMaybeCallTimer(final int callbackID, final int duration, final double jsSchedulingTime, final boolean repeat) {
        long currentTimeMillis = SystemClock.currentTimeMillis();
        long j = (long) jsSchedulingTime;
        if (this.mDevSupportManager.getDevSupportEnabled() && Math.abs(j - currentTimeMillis) > 60000) {
            this.mJavaScriptTimerManager.emitTimeDriftWarning("Debugger and device times have drifted by more than 60s. Please correct this by running adb shell \"date `date +%m%d%H%M%Y.%S`\" on your debugger machine.");
        }
        long max = Math.max(0L, (j - currentTimeMillis) + duration);
        if (duration == 0 && !repeat) {
            WritableArray createArray = Arguments.createArray();
            createArray.pushInt(callbackID);
            this.mJavaScriptTimerManager.callTimers(createArray);
            return;
        }
        createTimer(callbackID, max, repeat);
    }

    public void deleteTimer(int timerId) {
        synchronized (this.mTimerGuard) {
            Timer timer = this.mTimerIdsToTimers.get(timerId);
            if (timer == null) {
                return;
            }
            this.mTimerIdsToTimers.remove(timerId);
            this.mTimers.remove(timer);
        }
    }

    public void setSendIdleEvents(final boolean sendIdleEvents) {
        synchronized (this.mIdleCallbackGuard) {
            this.mSendIdleEvents = sendIdleEvents;
        }
        UiThreadUtil.runOnUiThread(new Runnable() { // from class: com.facebook.react.modules.core.JavaTimerManager.2
            @Override // java.lang.Runnable
            public void run() {
                synchronized (JavaTimerManager.this.mIdleCallbackGuard) {
                    if (sendIdleEvents) {
                        JavaTimerManager.this.setChoreographerIdleCallback();
                    } else {
                        JavaTimerManager.this.clearChoreographerIdleCallback();
                    }
                }
            }
        });
    }

    public boolean hasActiveTimersInRange(long rangeMs) {
        synchronized (this.mTimerGuard) {
            Timer peek = this.mTimers.peek();
            if (peek == null) {
                return false;
            }
            if (isTimerInRange(peek, rangeMs)) {
                return true;
            }
            Iterator<Timer> it = this.mTimers.iterator();
            while (it.hasNext()) {
                if (isTimerInRange(it.next(), rangeMs)) {
                    return true;
                }
            }
            return false;
        }
    }

    private static boolean isTimerInRange(Timer timer, long rangeMs) {
        return !timer.mRepeat && ((long) timer.mInterval) < rangeMs;
    }
}
