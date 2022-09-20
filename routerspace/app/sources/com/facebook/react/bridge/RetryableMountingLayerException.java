package com.facebook.react.bridge;
/* loaded from: classes.dex */
public class RetryableMountingLayerException extends RuntimeException {
    public RetryableMountingLayerException(String msg, Throwable e) {
        super(msg, e);
    }

    public RetryableMountingLayerException(Throwable e) {
        super(e);
    }

    public RetryableMountingLayerException(String msg) {
        super(msg);
    }
}
