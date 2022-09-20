package com.facebook.react.viewmanagers;

import android.view.View;
import com.facebook.react.bridge.ReadableArray;
/* loaded from: classes.dex */
public interface SegmentedControlManagerInterface<T extends View> {
    void setBackgroundColor(T view, Integer value);

    void setEnabled(T view, boolean value);

    void setMomentary(T view, boolean value);

    void setSelectedIndex(T view, int value);

    void setTextColor(T view, Integer value);

    void setTintColor(T view, Integer value);

    void setValues(T view, ReadableArray value);
}
