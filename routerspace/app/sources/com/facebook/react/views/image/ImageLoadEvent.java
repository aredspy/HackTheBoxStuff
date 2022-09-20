package com.facebook.react.views.image;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;
/* loaded from: classes.dex */
public class ImageLoadEvent extends Event<ImageLoadEvent> {
    public static final int ON_ERROR = 1;
    public static final int ON_LOAD = 2;
    public static final int ON_LOAD_END = 3;
    public static final int ON_LOAD_START = 4;
    public static final int ON_PROGRESS = 5;
    private final String mErrorMessage;
    private final int mEventType;
    private final int mHeight;
    private final int mLoaded;
    private final String mSourceUri;
    private final int mTotal;
    private final int mWidth;

    @Deprecated
    public static final ImageLoadEvent createLoadStartEvent(int viewId) {
        return createLoadStartEvent(-1, viewId);
    }

    @Deprecated
    public static final ImageLoadEvent createProgressEvent(int viewId, String imageUri, int loaded, int total) {
        return createProgressEvent(-1, viewId, imageUri, loaded, total);
    }

    @Deprecated
    public static final ImageLoadEvent createLoadEvent(int viewId, String imageUri, int width, int height) {
        return createLoadEvent(-1, viewId, imageUri, width, height);
    }

    @Deprecated
    public static final ImageLoadEvent createErrorEvent(int viewId, Throwable throwable) {
        return createErrorEvent(-1, viewId, throwable);
    }

    @Deprecated
    public static final ImageLoadEvent createLoadEndEvent(int viewId) {
        return createLoadEndEvent(-1, viewId);
    }

    public static final ImageLoadEvent createLoadStartEvent(int surfaceId, int viewId) {
        return new ImageLoadEvent(surfaceId, viewId, 4);
    }

    public static final ImageLoadEvent createProgressEvent(int surfaceId, int viewId, String imageUri, int loaded, int total) {
        return new ImageLoadEvent(surfaceId, viewId, 5, null, imageUri, 0, 0, loaded, total);
    }

    public static final ImageLoadEvent createLoadEvent(int surfaceId, int viewId, String imageUri, int width, int height) {
        return new ImageLoadEvent(surfaceId, viewId, 2, null, imageUri, width, height, 0, 0);
    }

    public static final ImageLoadEvent createErrorEvent(int surfaceId, int viewId, Throwable throwable) {
        return new ImageLoadEvent(surfaceId, viewId, 1, throwable.getMessage(), null, 0, 0, 0, 0);
    }

    public static final ImageLoadEvent createLoadEndEvent(int surfaceId, int viewId) {
        return new ImageLoadEvent(surfaceId, viewId, 3);
    }

    private ImageLoadEvent(int surfaceId, int viewId, int eventType) {
        this(surfaceId, viewId, eventType, null, null, 0, 0, 0, 0);
    }

    private ImageLoadEvent(int surfaceId, int viewId, int eventType, String errorMessage, String sourceUri, int width, int height, int loaded, int total) {
        super(surfaceId, viewId);
        this.mEventType = eventType;
        this.mErrorMessage = errorMessage;
        this.mSourceUri = sourceUri;
        this.mWidth = width;
        this.mHeight = height;
        this.mLoaded = loaded;
        this.mTotal = total;
    }

    public static String eventNameForType(int eventType) {
        if (eventType != 1) {
            if (eventType == 2) {
                return "topLoad";
            }
            if (eventType == 3) {
                return "topLoadEnd";
            }
            if (eventType == 4) {
                return "topLoadStart";
            }
            if (eventType == 5) {
                return "topProgress";
            }
            throw new IllegalStateException("Invalid image event: " + Integer.toString(eventType));
        }
        return "topError";
    }

    @Override // com.facebook.react.uimanager.events.Event
    public String getEventName() {
        return eventNameForType(this.mEventType);
    }

    @Override // com.facebook.react.uimanager.events.Event
    public short getCoalescingKey() {
        return (short) this.mEventType;
    }

    @Override // com.facebook.react.uimanager.events.Event
    protected WritableMap getEventData() {
        WritableMap createMap = Arguments.createMap();
        int i = this.mEventType;
        if (i == 1) {
            createMap.putString("error", this.mErrorMessage);
        } else if (i == 2) {
            createMap.putMap("source", createEventDataSource());
        } else if (i == 5) {
            createMap.putInt("loaded", this.mLoaded);
            createMap.putInt("total", this.mTotal);
        }
        return createMap;
    }

    private WritableMap createEventDataSource() {
        WritableMap createMap = Arguments.createMap();
        createMap.putString("uri", this.mSourceUri);
        createMap.putDouble("width", this.mWidth);
        createMap.putDouble("height", this.mHeight);
        return createMap;
    }
}
