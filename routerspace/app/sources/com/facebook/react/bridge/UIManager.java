package com.facebook.react.bridge;

import android.view.View;
import java.util.List;
/* loaded from: classes.dex */
public interface UIManager extends JSIModule, PerformanceCounter {
    @Deprecated
    <T extends View> int addRootView(final T rootView, WritableMap initialProps, String initialUITemplate);

    void addUIManagerEventListener(UIManagerListener listener);

    void dispatchCommand(int reactTag, int commandId, ReadableArray commandArgs);

    void dispatchCommand(int reactTag, String commandId, ReadableArray commandArgs);

    <T> T getEventDispatcher();

    @Deprecated
    void preInitializeViewManagers(List<String> viewManagerNames);

    void receiveEvent(int surfaceId, int reactTag, String eventName, WritableMap event);

    @Deprecated
    void receiveEvent(int reactTag, String eventName, WritableMap event);

    void removeUIManagerEventListener(UIManagerListener listener);

    @Deprecated
    String resolveCustomDirectEventName(String eventName);

    View resolveView(int reactTag);

    void sendAccessibilityEvent(int reactTag, int eventType);

    <T extends View> int startSurface(final T rootView, final String moduleName, final WritableMap initialProps, int widthMeasureSpec, int heightMeasureSpec);

    void stopSurface(final int surfaceId);

    void synchronouslyUpdateViewOnUIThread(int reactTag, ReadableMap props);

    void updateRootLayoutSpecs(int rootTag, int widthMeasureSpec, int heightMeasureSpec, int offsetX, int offsetY);
}
