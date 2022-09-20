package com.facebook.react.devsupport;
/* loaded from: classes.dex */
public class JSException extends Exception {
    private final String mStack;

    public JSException(String message, String stack, Throwable cause) {
        super(message, cause);
        this.mStack = stack;
    }

    public JSException(String message, String stack) {
        super(message);
        this.mStack = stack;
    }

    public String getStack() {
        return this.mStack;
    }
}
