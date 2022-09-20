package com.facebook.react.viewmanagers;

import android.view.View;
import com.facebook.react.bridge.ReadableMap;
/* loaded from: classes.dex */
public interface ProgressViewManagerInterface<T extends View> {
    void setProgress(T view, float value);

    void setProgressImage(T view, ReadableMap value);

    void setProgressTintColor(T view, Integer value);

    void setProgressViewStyle(T view, String value);

    void setTrackImage(T view, ReadableMap value);

    void setTrackTintColor(T view, Integer value);
}
