package com.facebook.react.uimanager;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.systrace.Systrace;
import java.util.List;
@Deprecated
/* loaded from: classes.dex */
public class UIImplementationProvider {
    public UIImplementation createUIImplementation(ReactApplicationContext reactContext, ViewManagerResolver viewManagerResolver, EventDispatcher eventDispatcher, int minTimeLeftInFrameForNonBatchedOperationMs) {
        Systrace.beginSection(0L, "UIImplementationProvider.createUIImplementation[1]");
        try {
            return new UIImplementation(reactContext, viewManagerResolver, eventDispatcher, minTimeLeftInFrameForNonBatchedOperationMs);
        } finally {
            Systrace.endSection(0L);
        }
    }

    public UIImplementation createUIImplementation(ReactApplicationContext reactContext, List<ViewManager> viewManagerList, EventDispatcher eventDispatcher, int minTimeLeftInFrameForNonBatchedOperationMs) {
        Systrace.beginSection(0L, "UIImplementationProvider.createUIImplementation[2]");
        try {
            return new UIImplementation(reactContext, viewManagerList, eventDispatcher, minTimeLeftInFrameForNonBatchedOperationMs);
        } finally {
            Systrace.endSection(0L);
        }
    }

    public UIImplementation createUIImplementation(ReactApplicationContext reactContext, ViewManagerRegistry viewManagerRegistry, EventDispatcher eventDispatcher, int minTimeLeftInFrameForNonBatchedOperationMs) {
        Systrace.beginSection(0L, "UIImplementationProvider.createUIImplementation[3]");
        try {
            return new UIImplementation(reactContext, viewManagerRegistry, eventDispatcher, minTimeLeftInFrameForNonBatchedOperationMs);
        } finally {
            Systrace.endSection(0L);
        }
    }
}
