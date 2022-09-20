package com.facebook.react.modules.core;

import com.facebook.fbreact.specs.NativeTimingSpec;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.devsupport.interfaces.DevSupportManager;
import com.facebook.react.jstasks.HeadlessJsTaskContext;
import com.facebook.react.jstasks.HeadlessJsTaskEventListener;
import com.facebook.react.module.annotations.ReactModule;
@ReactModule(name = TimingModule.NAME)
/* loaded from: classes.dex */
public final class TimingModule extends NativeTimingSpec implements LifecycleEventListener, HeadlessJsTaskEventListener {
    public static final String NAME = "Timing";
    private final JavaTimerManager mJavaTimerManager;

    @Override // com.facebook.react.bridge.NativeModule
    public String getName() {
        return NAME;
    }

    /* loaded from: classes.dex */
    public class BridgeTimerManager implements JavaScriptTimerManager {
        public BridgeTimerManager() {
            TimingModule.this = this$0;
        }

        @Override // com.facebook.react.modules.core.JavaScriptTimerManager
        public void callTimers(WritableArray timerIDs) {
            ReactApplicationContext reactApplicationContextIfActiveOrWarn = TimingModule.this.getReactApplicationContextIfActiveOrWarn();
            if (reactApplicationContextIfActiveOrWarn != null) {
                ((JSTimers) reactApplicationContextIfActiveOrWarn.getJSModule(JSTimers.class)).callTimers(timerIDs);
            }
        }

        @Override // com.facebook.react.modules.core.JavaScriptTimerManager
        public void callIdleCallbacks(double frameTime) {
            ReactApplicationContext reactApplicationContextIfActiveOrWarn = TimingModule.this.getReactApplicationContextIfActiveOrWarn();
            if (reactApplicationContextIfActiveOrWarn != null) {
                ((JSTimers) reactApplicationContextIfActiveOrWarn.getJSModule(JSTimers.class)).callIdleCallbacks(frameTime);
            }
        }

        @Override // com.facebook.react.modules.core.JavaScriptTimerManager
        public void emitTimeDriftWarning(String warningMessage) {
            ReactApplicationContext reactApplicationContextIfActiveOrWarn = TimingModule.this.getReactApplicationContextIfActiveOrWarn();
            if (reactApplicationContextIfActiveOrWarn != null) {
                ((JSTimers) reactApplicationContextIfActiveOrWarn.getJSModule(JSTimers.class)).emitTimeDriftWarning(warningMessage);
            }
        }
    }

    public TimingModule(ReactApplicationContext reactContext, DevSupportManager devSupportManager) {
        super(reactContext);
        this.mJavaTimerManager = new JavaTimerManager(reactContext, new BridgeTimerManager(), ReactChoreographer.getInstance(), devSupportManager);
    }

    @Override // com.facebook.react.bridge.BaseJavaModule, com.facebook.react.bridge.NativeModule, com.facebook.react.turbomodule.core.interfaces.TurboModule
    public void initialize() {
        getReactApplicationContext().addLifecycleEventListener(this);
        HeadlessJsTaskContext.getInstance(getReactApplicationContext()).addTaskEventListener(this);
    }

    @Override // com.facebook.fbreact.specs.NativeTimingSpec
    public void createTimer(final double callbackIDDouble, final double durationDouble, final double jsSchedulingTime, final boolean repeat) {
        this.mJavaTimerManager.createAndMaybeCallTimer((int) callbackIDDouble, (int) durationDouble, jsSchedulingTime, repeat);
    }

    @Override // com.facebook.fbreact.specs.NativeTimingSpec
    public void deleteTimer(double timerIdDouble) {
        this.mJavaTimerManager.deleteTimer((int) timerIdDouble);
    }

    @Override // com.facebook.fbreact.specs.NativeTimingSpec
    public void setSendIdleEvents(final boolean sendIdleEvents) {
        this.mJavaTimerManager.setSendIdleEvents(sendIdleEvents);
    }

    @Override // com.facebook.react.bridge.LifecycleEventListener
    public void onHostResume() {
        this.mJavaTimerManager.onHostResume();
    }

    @Override // com.facebook.react.bridge.LifecycleEventListener
    public void onHostPause() {
        this.mJavaTimerManager.onHostPause();
    }

    @Override // com.facebook.react.bridge.LifecycleEventListener
    public void onHostDestroy() {
        this.mJavaTimerManager.onHostDestroy();
    }

    @Override // com.facebook.react.jstasks.HeadlessJsTaskEventListener
    public void onHeadlessJsTaskStart(int taskId) {
        this.mJavaTimerManager.onHeadlessJsTaskStart(taskId);
    }

    @Override // com.facebook.react.jstasks.HeadlessJsTaskEventListener
    public void onHeadlessJsTaskFinish(int taskId) {
        this.mJavaTimerManager.onHeadlessJsTaskFinish(taskId);
    }

    @Override // com.facebook.react.bridge.BaseJavaModule, com.facebook.react.bridge.NativeModule, com.facebook.react.turbomodule.core.interfaces.TurboModule
    public void invalidate() {
        ReactApplicationContext reactApplicationContext = getReactApplicationContext();
        HeadlessJsTaskContext.getInstance(reactApplicationContext).removeTaskEventListener(this);
        this.mJavaTimerManager.onInstanceDestroy();
        reactApplicationContext.removeLifecycleEventListener(this);
    }

    public boolean hasActiveTimersInRange(long rangeMs) {
        return this.mJavaTimerManager.hasActiveTimersInRange(rangeMs);
    }
}
