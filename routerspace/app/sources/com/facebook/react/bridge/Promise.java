package com.facebook.react.bridge;
/* loaded from: classes.dex */
public interface Promise {
    @Deprecated
    void reject(String message);

    void reject(String code, WritableMap userInfo);

    void reject(String code, String message);

    void reject(String code, String message, WritableMap userInfo);

    void reject(String code, String message, Throwable throwable);

    void reject(String code, String message, Throwable throwable, WritableMap userInfo);

    void reject(String code, Throwable throwable);

    void reject(String code, Throwable throwable, WritableMap userInfo);

    void reject(Throwable throwable);

    void reject(Throwable throwable, WritableMap userInfo);

    void resolve(Object value);
}
