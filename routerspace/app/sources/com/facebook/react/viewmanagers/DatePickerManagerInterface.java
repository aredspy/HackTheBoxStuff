package com.facebook.react.viewmanagers;

import android.view.View;
/* loaded from: classes.dex */
public interface DatePickerManagerInterface<T extends View> {
    void setDate(T view, float value);

    void setInitialDate(T view, float value);

    void setLocale(T view, String value);

    void setMaximumDate(T view, float value);

    void setMinimumDate(T view, float value);

    void setMinuteInterval(T view, Integer value);

    void setMode(T view, String value);

    void setNativeDate(T view, float date);

    void setTimeZoneOffsetInMinutes(T view, float value);
}
