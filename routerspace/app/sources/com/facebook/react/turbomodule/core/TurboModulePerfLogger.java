package com.facebook.react.turbomodule.core;

import com.facebook.react.perflogger.NativeModulePerfLogger;
import com.facebook.soloader.SoLoader;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class TurboModulePerfLogger {
    private static boolean sIsSoLibraryLoaded = false;
    @Nullable
    private static NativeModulePerfLogger sNativeModulePerfLogger;

    private static native void jniEnableCppLogging(NativeModulePerfLogger perfLogger);

    public static void moduleDataCreateStart(String moduleName, int id) {
        NativeModulePerfLogger nativeModulePerfLogger = sNativeModulePerfLogger;
        if (nativeModulePerfLogger != null) {
            nativeModulePerfLogger.moduleDataCreateStart(moduleName, id);
        }
    }

    public static void moduleDataCreateEnd(String moduleName, int id) {
        NativeModulePerfLogger nativeModulePerfLogger = sNativeModulePerfLogger;
        if (nativeModulePerfLogger != null) {
            nativeModulePerfLogger.moduleDataCreateEnd(moduleName, id);
        }
    }

    public static void moduleCreateStart(String moduleName, int id) {
        NativeModulePerfLogger nativeModulePerfLogger = sNativeModulePerfLogger;
        if (nativeModulePerfLogger != null) {
            nativeModulePerfLogger.moduleCreateStart(moduleName, id);
        }
    }

    public static void moduleCreateCacheHit(String moduleName, int id) {
        NativeModulePerfLogger nativeModulePerfLogger = sNativeModulePerfLogger;
        if (nativeModulePerfLogger != null) {
            nativeModulePerfLogger.moduleCreateCacheHit(moduleName, id);
        }
    }

    public static void moduleCreateConstructStart(String moduleName, int id) {
        NativeModulePerfLogger nativeModulePerfLogger = sNativeModulePerfLogger;
        if (nativeModulePerfLogger != null) {
            nativeModulePerfLogger.moduleCreateConstructStart(moduleName, id);
        }
    }

    public static void moduleCreateConstructEnd(String moduleName, int id) {
        NativeModulePerfLogger nativeModulePerfLogger = sNativeModulePerfLogger;
        if (nativeModulePerfLogger != null) {
            nativeModulePerfLogger.moduleCreateConstructEnd(moduleName, id);
        }
    }

    public static void moduleCreateSetUpStart(String moduleName, int id) {
        NativeModulePerfLogger nativeModulePerfLogger = sNativeModulePerfLogger;
        if (nativeModulePerfLogger != null) {
            nativeModulePerfLogger.moduleCreateSetUpStart(moduleName, id);
        }
    }

    public static void moduleCreateSetUpEnd(String moduleName, int id) {
        NativeModulePerfLogger nativeModulePerfLogger = sNativeModulePerfLogger;
        if (nativeModulePerfLogger != null) {
            nativeModulePerfLogger.moduleCreateSetUpEnd(moduleName, id);
        }
    }

    public static void moduleCreateEnd(String moduleName, int id) {
        NativeModulePerfLogger nativeModulePerfLogger = sNativeModulePerfLogger;
        if (nativeModulePerfLogger != null) {
            nativeModulePerfLogger.moduleCreateEnd(moduleName, id);
        }
    }

    public static void moduleCreateFail(String moduleName, int id) {
        NativeModulePerfLogger nativeModulePerfLogger = sNativeModulePerfLogger;
        if (nativeModulePerfLogger != null) {
            nativeModulePerfLogger.moduleCreateFail(moduleName, id);
        }
    }

    private static synchronized void maybeLoadSoLibrary() {
        synchronized (TurboModulePerfLogger.class) {
            if (!sIsSoLibraryLoaded) {
                SoLoader.loadLibrary("turbomodulejsijni");
                sIsSoLibraryLoaded = true;
            }
        }
    }

    public static void enableLogging(NativeModulePerfLogger perfLogger) {
        if (perfLogger != null) {
            sNativeModulePerfLogger = perfLogger;
            maybeLoadSoLibrary();
            jniEnableCppLogging(perfLogger);
        }
    }
}
