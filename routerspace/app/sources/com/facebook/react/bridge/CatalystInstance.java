package com.facebook.react.bridge;

import com.facebook.react.bridge.queue.ReactQueueConfiguration;
import com.facebook.react.turbomodule.core.interfaces.CallInvokerHolder;
import java.util.Collection;
import java.util.List;
/* loaded from: classes.dex */
public interface CatalystInstance extends MemoryPressureListener, JSInstance, JSBundleLoaderDelegate {
    void addBridgeIdleDebugListener(NotThreadSafeBridgeIdleDebugListener listener);

    void addJSIModules(List<JSIModuleSpec> jsiModules);

    void callFunction(String module, String method, NativeArray arguments);

    void destroy();

    void extendNativeModules(NativeModuleRegistry modules);

    CallInvokerHolder getJSCallInvokerHolder();

    JSIModule getJSIModule(JSIModuleType moduleType);

    <T extends JavaScriptModule> T getJSModule(Class<T> jsInterface);

    @Deprecated
    JavaScriptContextHolder getJavaScriptContextHolder();

    CallInvokerHolder getNativeCallInvokerHolder();

    <T extends NativeModule> T getNativeModule(Class<T> nativeModuleInterface);

    NativeModule getNativeModule(String moduleName);

    Collection<NativeModule> getNativeModules();

    ReactQueueConfiguration getReactQueueConfiguration();

    RuntimeExecutor getRuntimeExecutor();

    String getSourceURL();

    <T extends NativeModule> boolean hasNativeModule(Class<T> nativeModuleInterface);

    boolean hasRunJSBundle();

    void initialize();

    @Override // com.facebook.react.bridge.JSInstance
    void invokeCallback(int callbackID, NativeArrayInterface arguments);

    boolean isDestroyed();

    void registerSegment(int segmentId, String path);

    void removeBridgeIdleDebugListener(NotThreadSafeBridgeIdleDebugListener listener);

    void runJSBundle();

    void setGlobalVariable(String propName, String jsonValue);

    void setTurboModuleManager(JSIModule getter);
}
