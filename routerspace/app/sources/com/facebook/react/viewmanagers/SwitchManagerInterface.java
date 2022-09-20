package com.facebook.react.viewmanagers;

import android.view.View;
/* loaded from: classes.dex */
public interface SwitchManagerInterface<T extends View> {
    void setDisabled(T view, boolean value);

    void setOnTintColor(T view, Integer value);

    void setThumbColor(T view, Integer value);

    void setThumbTintColor(T view, Integer value);

    void setTintColor(T view, Integer value);

    void setTrackColorForFalse(T view, Integer value);

    void setTrackColorForTrue(T view, Integer value);

    void setValue(T view, boolean value);
}
