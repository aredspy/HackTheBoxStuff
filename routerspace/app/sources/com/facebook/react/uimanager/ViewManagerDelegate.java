package com.facebook.react.uimanager;

import android.view.View;
import com.facebook.react.bridge.ReadableArray;
/* loaded from: classes.dex */
public interface ViewManagerDelegate<T extends View> {
    void receiveCommand(T view, String commandName, ReadableArray args);

    void setProperty(T view, String propName, Object value);
}
