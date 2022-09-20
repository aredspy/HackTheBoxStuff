package com.facebook.react.uimanager.events;
/* loaded from: classes.dex */
public interface EventDispatcher {
    void addBatchEventDispatchedListener(BatchEventDispatchedListener listener);

    void addListener(EventDispatcherListener listener);

    void dispatchAllEvents();

    void dispatchEvent(Event event);

    void onCatalystInstanceDestroyed();

    @Deprecated
    void registerEventEmitter(int uiManagerType, RCTEventEmitter eventEmitter);

    void registerEventEmitter(int uiManagerType, RCTModernEventEmitter eventEmitter);

    void removeBatchEventDispatchedListener(BatchEventDispatchedListener listener);

    void removeListener(EventDispatcherListener listener);

    void unregisterEventEmitter(int uiManagerType);
}
