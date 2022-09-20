package com.facebook.react.common;

import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class JavascriptException extends RuntimeException implements HasJavascriptExceptionMetadata {
    @Nullable
    private String extraDataAsJson;

    public JavascriptException(String jsStackTrace) {
        super(jsStackTrace);
    }

    @Override // com.facebook.react.common.HasJavascriptExceptionMetadata
    @Nullable
    public String getExtraDataAsJson() {
        return this.extraDataAsJson;
    }

    public JavascriptException setExtraDataAsJson(@Nullable String extraDataAsJson) {
        this.extraDataAsJson = extraDataAsJson;
        return this;
    }
}
