package com.facebook.react.bridge;

import com.facebook.jni.HybridData;
import com.facebook.soloader.SoLoader;
/* loaded from: classes.dex */
public class CxxModuleWrapper extends CxxModuleWrapperBase {
    private static native CxxModuleWrapper makeDsoNative(String soPath, String factory);

    protected CxxModuleWrapper(HybridData hd) {
        super(hd);
    }

    public static CxxModuleWrapper makeDso(String library, String factory) {
        SoLoader.loadLibrary(library);
        return makeDsoNative(SoLoader.unpackLibraryAndDependencies(library).getAbsolutePath(), factory);
    }
}
