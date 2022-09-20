package com.facebook.react.viewmanagers;

import android.view.View;
/* loaded from: classes.dex */
public interface AndroidProgressBarManagerInterface<T extends View> {
    void setAnimating(T view, boolean value);

    void setColor(T view, Integer value);

    void setIndeterminate(T view, boolean value);

    void setProgress(T view, double value);

    void setStyleAttr(T view, String value);

    void setTestID(T view, String value);

    void setTypeAttr(T view, String value);
}
