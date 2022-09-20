package com.facebook.react.viewmanagers;

import android.view.View;
/* loaded from: classes.dex */
public interface AndroidDrawerLayoutManagerInterface<T extends View> {
    void closeDrawer(T view);

    void openDrawer(T view);

    void setDrawerBackgroundColor(T view, Integer value);

    void setDrawerLockMode(T view, String value);

    void setDrawerPosition(T view, String value);

    void setDrawerWidth(T view, Float value);

    void setKeyboardDismissMode(T view, String value);

    void setStatusBarBackgroundColor(T view, Integer value);
}
