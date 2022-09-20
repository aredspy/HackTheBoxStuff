package com.facebook.react.fabric;

import com.facebook.jni.HybridData;
import com.facebook.react.bridge.NativeMap;
import com.facebook.react.bridge.ReadableNativeMap;
import com.facebook.react.bridge.RuntimeExecutor;
import com.facebook.react.bridge.queue.MessageQueueThread;
import com.facebook.react.fabric.events.EventBeatManager;
import com.facebook.react.fabric.events.EventEmitterWrapper;
import com.facebook.react.uimanager.PixelUtil;
/* loaded from: classes.dex */
public class Binding {
    private final HybridData mHybridData = initHybrid();

    private static native HybridData initHybrid();

    private native void installFabricUIManager(RuntimeExecutor runtimeExecutor, Object uiManager, EventBeatManager eventBeatManager, MessageQueueThread jsMessageQueueThread, ComponentFactory componentsRegistry, Object reactNativeConfig);

    private native void uninstallFabricUIManager();

    public native void driveCxxAnimations();

    public native ReadableNativeMap getInspectorDataForInstance(EventEmitterWrapper eventEmitterWrapper);

    public native void registerSurface(SurfaceHandlerBinding surfaceHandler);

    public native void renderTemplateToSurface(int surfaceId, String uiTemplate);

    public native void setConstraints(int surfaceId, float minWidth, float maxWidth, float minHeight, float maxHeight, float offsetX, float offsetY, boolean isRTL, boolean doLeftAndRightSwapInRTL);

    public native void setPixelDensity(float pointScaleFactor);

    public native void startSurface(int surfaceId, String moduleName, NativeMap initialProps);

    public native void startSurfaceWithConstraints(int surfaceId, String moduleName, NativeMap initialProps, float minWidth, float maxWidth, float minHeight, float maxHeight, float offsetX, float offsetY, boolean isRTL, boolean doLeftAndRightSwapInRTL);

    public native void stopSurface(int surfaceId);

    public native void unregisterSurface(SurfaceHandlerBinding surfaceHandler);

    static {
        FabricSoLoader.staticInit();
    }

    public void register(RuntimeExecutor runtimeExecutor, FabricUIManager fabricUIManager, EventBeatManager eventBeatManager, MessageQueueThread jsMessageQueueThread, ComponentFactory componentFactory, ReactNativeConfig reactNativeConfig) {
        fabricUIManager.setBinding(this);
        installFabricUIManager(runtimeExecutor, fabricUIManager, eventBeatManager, jsMessageQueueThread, componentFactory, reactNativeConfig);
        setPixelDensity(PixelUtil.getDisplayMetricDensity());
    }

    public void unregister() {
        uninstallFabricUIManager();
    }
}
