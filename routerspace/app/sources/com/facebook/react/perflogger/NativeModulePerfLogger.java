package com.facebook.react.perflogger;

import com.facebook.jni.HybridData;
import com.facebook.soloader.SoLoader;
/* loaded from: classes.dex */
public abstract class NativeModulePerfLogger {
    private static volatile boolean sIsSoLibraryLoaded;
    private final HybridData mHybridData = initHybrid();

    protected abstract HybridData initHybrid();

    public abstract void moduleCreateCacheHit(String moduleName, int id);

    public abstract void moduleCreateConstructEnd(String moduleName, int id);

    public abstract void moduleCreateConstructStart(String moduleName, int id);

    public abstract void moduleCreateEnd(String moduleName, int id);

    public abstract void moduleCreateFail(String moduleName, int id);

    public abstract void moduleCreateSetUpEnd(String moduleName, int id);

    public abstract void moduleCreateSetUpStart(String moduleName, int id);

    public abstract void moduleCreateStart(String moduleName, int id);

    public abstract void moduleDataCreateEnd(String moduleName, int id);

    public abstract void moduleDataCreateStart(String moduleName, int id);

    protected NativeModulePerfLogger() {
        maybeLoadOtherSoLibraries();
        maybeLoadSoLibrary();
    }

    private static synchronized void maybeLoadSoLibrary() {
        synchronized (NativeModulePerfLogger.class) {
            if (!sIsSoLibraryLoaded) {
                SoLoader.loadLibrary("reactperfloggerjni");
                sIsSoLibraryLoaded = true;
            }
        }
    }

    protected synchronized void maybeLoadOtherSoLibraries() {
    }
}
