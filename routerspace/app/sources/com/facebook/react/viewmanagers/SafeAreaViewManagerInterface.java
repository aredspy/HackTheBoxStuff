package com.facebook.react.viewmanagers;

import android.view.View;
/* loaded from: classes.dex */
public interface SafeAreaViewManagerInterface<T extends View> {
    void setEmulateUnlessSupported(T view, boolean value);
}
