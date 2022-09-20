package com.facebook.react.uimanager;

import android.view.View;
import com.facebook.react.bridge.JSApplicationCausedNativeException;
/* loaded from: classes.dex */
public class IllegalViewOperationException extends JSApplicationCausedNativeException {
    private View mView;

    public IllegalViewOperationException(String msg) {
        super(msg);
    }

    public IllegalViewOperationException(String msg, View view, Throwable cause) {
        super(msg, cause);
        this.mView = view;
    }

    public View getView() {
        return this.mView;
    }
}
