package com.facebook.react.devsupport.interfaces;
/* loaded from: classes.dex */
public enum ErrorType {
    JS("JS"),
    NATIVE("Native");
    
    private final String name;

    ErrorType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
