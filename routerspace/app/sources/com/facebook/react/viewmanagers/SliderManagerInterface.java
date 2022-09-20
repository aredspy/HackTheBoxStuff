package com.facebook.react.viewmanagers;

import android.view.View;
import com.facebook.react.bridge.ReadableMap;
/* loaded from: classes.dex */
public interface SliderManagerInterface<T extends View> {
    void setDisabled(T view, boolean value);

    void setEnabled(T view, boolean value);

    void setMaximumTrackImage(T view, ReadableMap value);

    void setMaximumTrackTintColor(T view, Integer value);

    void setMaximumValue(T view, double value);

    void setMinimumTrackImage(T view, ReadableMap value);

    void setMinimumTrackTintColor(T view, Integer value);

    void setMinimumValue(T view, double value);

    void setStep(T view, double value);

    void setTestID(T view, String value);

    void setThumbImage(T view, ReadableMap value);

    void setThumbTintColor(T view, Integer value);

    void setTrackImage(T view, ReadableMap value);

    void setValue(T view, double value);
}
