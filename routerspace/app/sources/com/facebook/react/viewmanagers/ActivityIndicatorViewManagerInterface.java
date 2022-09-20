package com.facebook.react.viewmanagers;

import android.view.View;
/* loaded from: classes.dex */
public interface ActivityIndicatorViewManagerInterface<T extends View> {
    void setAnimating(T view, boolean value);

    void setColor(T view, Integer value);

    void setHidesWhenStopped(T view, boolean value);

    void setSize(T view, String value);
}
