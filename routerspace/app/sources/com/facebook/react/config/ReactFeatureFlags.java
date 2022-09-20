package com.facebook.react.config;
/* loaded from: classes.dex */
public class ReactFeatureFlags {
    public static boolean eagerInitializeFabric = false;
    public static boolean enableAggressiveEventEmitterCleanup = false;
    public static boolean enableEagerInitializeMapBufferSoFile = false;
    public static boolean enableExperimentalStaticViewConfigs = false;
    public static boolean enableFabricInLogBox = false;
    public static boolean enableFabricLogs = false;
    public static boolean enableLockFreeEventDispatcher = false;
    public static boolean enableReactContextCleanupFix = false;
    public static boolean enableRuntimeScheduler = false;
    public static volatile boolean enableTurboModulePromiseAsyncDispatch = false;
    private static boolean mapBufferSerializationEnabled = false;
    public static boolean useGlobalCallbackCleanupScopeUsingRetainJSCallback = false;
    public static boolean useTurboModuleManagerCallbackCleanupScope = false;
    public static volatile boolean useTurboModules = false;
    public static volatile boolean warnOnLegacyNativeModuleSystemUse = false;

    public static void setMapBufferSerializationEnabled(boolean enabled) {
        mapBufferSerializationEnabled = enabled;
    }

    public static boolean isMapBufferSerializationEnabled() {
        return mapBufferSerializationEnabled;
    }
}
