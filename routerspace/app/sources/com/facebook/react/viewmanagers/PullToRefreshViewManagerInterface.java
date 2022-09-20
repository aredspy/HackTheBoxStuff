package com.facebook.react.viewmanagers;

import android.view.View;
/* loaded from: classes.dex */
public interface PullToRefreshViewManagerInterface<T extends View> {
    void setNativeRefreshing(T view, boolean refreshing);

    void setRefreshing(T view, boolean value);

    void setTintColor(T view, Integer value);

    void setTitle(T view, String value);

    void setTitleColor(T view, Integer value);
}
