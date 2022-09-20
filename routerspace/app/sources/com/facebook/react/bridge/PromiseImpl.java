package com.facebook.react.bridge;
/* loaded from: classes.dex */
public class PromiseImpl implements Promise {
    private static final String ERROR_DEFAULT_CODE = "EUNSPECIFIED";
    private static final String ERROR_DEFAULT_MESSAGE = "Error not specified.";
    private static final String ERROR_MAP_KEY_CODE = "code";
    private static final String ERROR_MAP_KEY_MESSAGE = "message";
    private static final String ERROR_MAP_KEY_NATIVE_STACK = "nativeStackAndroid";
    private static final String ERROR_MAP_KEY_USER_INFO = "userInfo";
    private static final int ERROR_STACK_FRAME_LIMIT = 50;
    private static final String STACK_FRAME_KEY_CLASS = "class";
    private static final String STACK_FRAME_KEY_FILE = "file";
    private static final String STACK_FRAME_KEY_LINE_NUMBER = "lineNumber";
    private static final String STACK_FRAME_KEY_METHOD_NAME = "methodName";
    private Callback mReject;
    private Callback mResolve;

    public PromiseImpl(Callback resolve, Callback reject) {
        this.mResolve = resolve;
        this.mReject = reject;
    }

    @Override // com.facebook.react.bridge.Promise
    public void resolve(Object value) {
        Callback callback = this.mResolve;
        if (callback != null) {
            callback.invoke(value);
            this.mResolve = null;
            this.mReject = null;
        }
    }

    @Override // com.facebook.react.bridge.Promise
    public void reject(String code, String message) {
        reject(code, message, null, null);
    }

    @Override // com.facebook.react.bridge.Promise
    public void reject(String code, Throwable throwable) {
        reject(code, null, throwable, null);
    }

    @Override // com.facebook.react.bridge.Promise
    public void reject(String code, String message, Throwable throwable) {
        reject(code, message, throwable, null);
    }

    @Override // com.facebook.react.bridge.Promise
    public void reject(Throwable throwable) {
        reject(null, null, throwable, null);
    }

    @Override // com.facebook.react.bridge.Promise
    public void reject(Throwable throwable, WritableMap userInfo) {
        reject(null, null, throwable, userInfo);
    }

    @Override // com.facebook.react.bridge.Promise
    public void reject(String code, WritableMap userInfo) {
        reject(code, null, null, userInfo);
    }

    @Override // com.facebook.react.bridge.Promise
    public void reject(String code, Throwable throwable, WritableMap userInfo) {
        reject(code, null, throwable, userInfo);
    }

    @Override // com.facebook.react.bridge.Promise
    public void reject(String code, String message, WritableMap userInfo) {
        reject(code, message, null, userInfo);
    }

    @Override // com.facebook.react.bridge.Promise
    public void reject(String code, String message, Throwable throwable, WritableMap userInfo) {
        if (this.mReject == null) {
            this.mResolve = null;
            return;
        }
        WritableNativeMap writableNativeMap = new WritableNativeMap();
        if (code == null) {
            writableNativeMap.putString(ERROR_MAP_KEY_CODE, ERROR_DEFAULT_CODE);
        } else {
            writableNativeMap.putString(ERROR_MAP_KEY_CODE, code);
        }
        if (message != null) {
            writableNativeMap.putString(ERROR_MAP_KEY_MESSAGE, message);
        } else if (throwable != null) {
            writableNativeMap.putString(ERROR_MAP_KEY_MESSAGE, throwable.getMessage());
        } else {
            writableNativeMap.putString(ERROR_MAP_KEY_MESSAGE, ERROR_DEFAULT_MESSAGE);
        }
        if (userInfo != null) {
            writableNativeMap.putMap(ERROR_MAP_KEY_USER_INFO, userInfo);
        } else {
            writableNativeMap.putNull(ERROR_MAP_KEY_USER_INFO);
        }
        if (throwable != null) {
            StackTraceElement[] stackTrace = throwable.getStackTrace();
            WritableNativeArray writableNativeArray = new WritableNativeArray();
            for (int i = 0; i < stackTrace.length && i < 50; i++) {
                StackTraceElement stackTraceElement = stackTrace[i];
                WritableNativeMap writableNativeMap2 = new WritableNativeMap();
                writableNativeMap2.putString(STACK_FRAME_KEY_CLASS, stackTraceElement.getClassName());
                writableNativeMap2.putString("file", stackTraceElement.getFileName());
                writableNativeMap2.putInt("lineNumber", stackTraceElement.getLineNumber());
                writableNativeMap2.putString(STACK_FRAME_KEY_METHOD_NAME, stackTraceElement.getMethodName());
                writableNativeArray.pushMap(writableNativeMap2);
            }
            writableNativeMap.putArray(ERROR_MAP_KEY_NATIVE_STACK, writableNativeArray);
        } else {
            writableNativeMap.putArray(ERROR_MAP_KEY_NATIVE_STACK, new WritableNativeArray());
        }
        this.mReject.invoke(writableNativeMap);
        this.mResolve = null;
        this.mReject = null;
    }

    @Override // com.facebook.react.bridge.Promise
    @Deprecated
    public void reject(String message) {
        reject(null, message, null, null);
    }
}
