package com.facebook.react.common.mapbuffer;

import com.facebook.react.bridge.ReactMarker;
import com.facebook.react.bridge.ReactMarkerConstants;
import com.facebook.soloader.SoLoader;
import com.facebook.systrace.Systrace;
/* loaded from: classes.dex */
public class ReadableMapBufferSoLoader {
    private static volatile boolean sDidInit = false;

    public static void staticInit() {
        if (sDidInit) {
            return;
        }
        Systrace.beginSection(0L, "ReadableMapBufferSoLoader.staticInit::load:mapbufferjni");
        ReactMarker.logMarker(ReactMarkerConstants.LOAD_REACT_NATIVE_MAPBUFFER_SO_FILE_START);
        SoLoader.loadLibrary("mapbufferjni");
        ReactMarker.logMarker(ReactMarkerConstants.LOAD_REACT_NATIVE_MAPBUFFER_SO_FILE_END);
        Systrace.endSection(0L);
        sDidInit = true;
    }
}
