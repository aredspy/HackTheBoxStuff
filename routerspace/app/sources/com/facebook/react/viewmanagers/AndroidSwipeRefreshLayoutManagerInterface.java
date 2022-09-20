package com.facebook.react.viewmanagers;

import android.view.View;
import com.facebook.react.bridge.ReadableArray;
/* loaded from: classes.dex */
public interface AndroidSwipeRefreshLayoutManagerInterface<T extends View> {
    void setColors(T view, ReadableArray value);

    void setEnabled(T view, boolean value);

    void setNativeRefreshing(T view, boolean value);

    void setProgressBackgroundColor(T view, Integer value);

    void setProgressViewOffset(T view, float value);

    void setRefreshing(T view, boolean value);

    void setSize(T view, String value);
}
