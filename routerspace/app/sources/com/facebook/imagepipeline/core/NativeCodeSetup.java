package com.facebook.imagepipeline.core;
/* loaded from: classes.dex */
public class NativeCodeSetup {
    private static boolean sUseNativeCode = true;

    private NativeCodeSetup() {
    }

    public static void setUseNativeCode(boolean useNativeCode) {
        sUseNativeCode = useNativeCode;
    }

    public static boolean getUseNativeCode() {
        return sUseNativeCode;
    }
}
