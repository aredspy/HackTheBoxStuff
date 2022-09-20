package com.facebook.react.bridge;
/* loaded from: classes.dex */
public interface JavaJSExecutor {

    /* loaded from: classes.dex */
    public interface Factory {
        JavaJSExecutor create() throws Exception;
    }

    void close();

    String executeJSCall(String methodName, String jsonArgsArray) throws ProxyExecutorException;

    void loadBundle(String sourceURL) throws ProxyExecutorException;

    void setGlobalVariable(String propertyName, String jsonEncodedValue);

    /* loaded from: classes.dex */
    public static class ProxyExecutorException extends Exception {
        public ProxyExecutorException(Throwable cause) {
            super(cause);
        }
    }
}
