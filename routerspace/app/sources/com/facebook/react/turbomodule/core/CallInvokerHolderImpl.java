package com.facebook.react.turbomodule.core;

import com.facebook.jni.HybridData;
import com.facebook.react.turbomodule.core.interfaces.CallInvokerHolder;
import com.facebook.soloader.SoLoader;
/* loaded from: classes.dex */
public class CallInvokerHolderImpl implements CallInvokerHolder {
    private static volatile boolean sIsSoLibraryLoaded;
    private final HybridData mHybridData;

    private CallInvokerHolderImpl(HybridData hd) {
        maybeLoadSoLibrary();
        this.mHybridData = hd;
    }

    private static synchronized void maybeLoadSoLibrary() {
        synchronized (CallInvokerHolderImpl.class) {
            if (!sIsSoLibraryLoaded) {
                SoLoader.loadLibrary("turbomodulejsijni");
                sIsSoLibraryLoaded = true;
            }
        }
    }
}
