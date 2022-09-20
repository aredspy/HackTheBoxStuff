package com.facebook.react.bridge;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
/* loaded from: classes.dex */
public class ReactMarker {
    private static final List<MarkerListener> sListeners = new CopyOnWriteArrayList();
    private static final List<FabricMarkerListener> sFabricMarkerListeners = new CopyOnWriteArrayList();

    /* loaded from: classes.dex */
    public interface FabricMarkerListener {
        void logFabricMarker(ReactMarkerConstants name, String tag, int instanceKey, long timestamp);
    }

    /* loaded from: classes.dex */
    public interface MarkerListener {
        void logMarker(ReactMarkerConstants name, String tag, int instanceKey);
    }

    public static void addListener(MarkerListener listener) {
        List<MarkerListener> list = sListeners;
        if (!list.contains(listener)) {
            list.add(listener);
        }
    }

    public static void removeListener(MarkerListener listener) {
        sListeners.remove(listener);
    }

    public static void clearMarkerListeners() {
        sListeners.clear();
    }

    public static void addFabricListener(FabricMarkerListener listener) {
        List<FabricMarkerListener> list = sFabricMarkerListeners;
        if (!list.contains(listener)) {
            list.add(listener);
        }
    }

    public static void removeFabricListener(FabricMarkerListener listener) {
        sFabricMarkerListeners.remove(listener);
    }

    public static void clearFabricMarkerListeners() {
        sFabricMarkerListeners.clear();
    }

    public static void logFabricMarker(ReactMarkerConstants name, String tag, int instanceKey, long timestamp) {
        for (FabricMarkerListener fabricMarkerListener : sFabricMarkerListeners) {
            fabricMarkerListener.logFabricMarker(name, tag, instanceKey, timestamp);
        }
    }

    public static void logFabricMarker(ReactMarkerConstants name, String tag, int instanceKey) {
        logFabricMarker(name, tag, instanceKey, -1L);
    }

    public static void logMarker(String name) {
        logMarker(name, (String) null);
    }

    public static void logMarker(String name, int instanceKey) {
        logMarker(name, (String) null, instanceKey);
    }

    public static void logMarker(String name, String tag) {
        logMarker(name, tag, 0);
    }

    public static void logMarker(String name, String tag, int instanceKey) {
        logMarker(ReactMarkerConstants.valueOf(name), tag, instanceKey);
    }

    public static void logMarker(ReactMarkerConstants name) {
        logMarker(name, (String) null, 0);
    }

    public static void logMarker(ReactMarkerConstants name, int instanceKey) {
        logMarker(name, (String) null, instanceKey);
    }

    public static void logMarker(ReactMarkerConstants name, String tag) {
        logMarker(name, tag, 0);
    }

    public static void logMarker(ReactMarkerConstants name, String tag, int instanceKey) {
        logFabricMarker(name, tag, instanceKey);
        for (MarkerListener markerListener : sListeners) {
            markerListener.logMarker(name, tag, instanceKey);
        }
    }
}
