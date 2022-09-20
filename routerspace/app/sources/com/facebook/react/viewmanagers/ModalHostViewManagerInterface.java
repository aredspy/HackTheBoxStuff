package com.facebook.react.viewmanagers;

import android.view.View;
import com.facebook.react.bridge.ReadableArray;
/* loaded from: classes.dex */
public interface ModalHostViewManagerInterface<T extends View> {
    void setAnimated(T view, boolean value);

    void setAnimationType(T view, String value);

    void setHardwareAccelerated(T view, boolean value);

    void setIdentifier(T view, int value);

    void setPresentationStyle(T view, String value);

    void setStatusBarTranslucent(T view, boolean value);

    void setSupportedOrientations(T view, ReadableArray value);

    void setTransparent(T view, boolean value);

    void setVisible(T view, boolean value);
}
