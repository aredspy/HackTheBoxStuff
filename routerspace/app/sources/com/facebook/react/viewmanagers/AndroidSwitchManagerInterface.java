package com.facebook.react.viewmanagers;

import android.view.View;
/* loaded from: classes.dex */
public interface AndroidSwitchManagerInterface<T extends View> {
    void setDisabled(T view, boolean value);

    void setEnabled(T view, boolean value);

    void setNativeValue(T view, boolean value);

    void setOn(T view, boolean value);

    void setThumbColor(T view, Integer value);

    void setThumbTintColor(T view, Integer value);

    void setTrackColorForFalse(T view, Integer value);

    void setTrackColorForTrue(T view, Integer value);

    void setTrackTintColor(T view, Integer value);

    void setValue(T view, boolean value);
}
