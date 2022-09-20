package com.facebook.react.views.text;

import com.facebook.react.uimanager.ReactShadowNodeImpl;
import com.facebook.react.uimanager.annotations.ReactProp;
/* loaded from: classes.dex */
public class ReactRawTextShadowNode extends ReactShadowNodeImpl {
    public static final String PROP_TEXT = "text";
    private String mText = null;

    @Override // com.facebook.react.uimanager.ReactShadowNodeImpl, com.facebook.react.uimanager.ReactShadowNode
    public boolean isVirtual() {
        return true;
    }

    @ReactProp(name = "text")
    public void setText(String text) {
        this.mText = text;
        markUpdated();
    }

    public String getText() {
        return this.mText;
    }

    @Override // com.facebook.react.uimanager.ReactShadowNodeImpl
    public String toString() {
        return getViewClass() + " [text: " + this.mText + "]";
    }
}
