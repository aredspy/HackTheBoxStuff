package com.facebook.react.modules.network;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import java.net.SocketTimeoutException;
/* loaded from: classes.dex */
public class ResponseUtil {
    public static void onDataSend(DeviceEventManagerModule.RCTDeviceEventEmitter eventEmitter, int requestId, long progress, long total) {
        WritableArray createArray = Arguments.createArray();
        createArray.pushInt(requestId);
        createArray.pushInt((int) progress);
        createArray.pushInt((int) total);
        if (eventEmitter != null) {
            eventEmitter.emit("didSendNetworkData", createArray);
        }
    }

    public static void onIncrementalDataReceived(DeviceEventManagerModule.RCTDeviceEventEmitter eventEmitter, int requestId, String data, long progress, long total) {
        WritableArray createArray = Arguments.createArray();
        createArray.pushInt(requestId);
        createArray.pushString(data);
        createArray.pushInt((int) progress);
        createArray.pushInt((int) total);
        if (eventEmitter != null) {
            eventEmitter.emit("didReceiveNetworkIncrementalData", createArray);
        }
    }

    public static void onDataReceivedProgress(DeviceEventManagerModule.RCTDeviceEventEmitter eventEmitter, int requestId, long progress, long total) {
        WritableArray createArray = Arguments.createArray();
        createArray.pushInt(requestId);
        createArray.pushInt((int) progress);
        createArray.pushInt((int) total);
        if (eventEmitter != null) {
            eventEmitter.emit("didReceiveNetworkDataProgress", createArray);
        }
    }

    public static void onDataReceived(DeviceEventManagerModule.RCTDeviceEventEmitter eventEmitter, int requestId, String data) {
        WritableArray createArray = Arguments.createArray();
        createArray.pushInt(requestId);
        createArray.pushString(data);
        if (eventEmitter != null) {
            eventEmitter.emit("didReceiveNetworkData", createArray);
        }
    }

    public static void onDataReceived(DeviceEventManagerModule.RCTDeviceEventEmitter eventEmitter, int requestId, WritableMap data) {
        WritableArray createArray = Arguments.createArray();
        createArray.pushInt(requestId);
        createArray.pushMap(data);
        if (eventEmitter != null) {
            eventEmitter.emit("didReceiveNetworkData", createArray);
        }
    }

    public static void onRequestError(DeviceEventManagerModule.RCTDeviceEventEmitter eventEmitter, int requestId, String error, Throwable e) {
        WritableArray createArray = Arguments.createArray();
        createArray.pushInt(requestId);
        createArray.pushString(error);
        if (e != null && e.getClass() == SocketTimeoutException.class) {
            createArray.pushBoolean(true);
        }
        if (eventEmitter != null) {
            eventEmitter.emit("didCompleteNetworkResponse", createArray);
        }
    }

    public static void onRequestSuccess(DeviceEventManagerModule.RCTDeviceEventEmitter eventEmitter, int requestId) {
        WritableArray createArray = Arguments.createArray();
        createArray.pushInt(requestId);
        createArray.pushNull();
        if (eventEmitter != null) {
            eventEmitter.emit("didCompleteNetworkResponse", createArray);
        }
    }

    public static void onResponseReceived(DeviceEventManagerModule.RCTDeviceEventEmitter eventEmitter, int requestId, int statusCode, WritableMap headers, String url) {
        WritableArray createArray = Arguments.createArray();
        createArray.pushInt(requestId);
        createArray.pushInt(statusCode);
        createArray.pushMap(headers);
        createArray.pushString(url);
        if (eventEmitter != null) {
            eventEmitter.emit("didReceiveNetworkResponse", createArray);
        }
    }
}
