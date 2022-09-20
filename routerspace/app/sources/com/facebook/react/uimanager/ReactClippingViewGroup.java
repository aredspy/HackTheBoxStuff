package com.facebook.react.uimanager;

import android.graphics.Rect;
/* loaded from: classes.dex */
public interface ReactClippingViewGroup {
    void getClippingRect(Rect outClippingRect);

    boolean getRemoveClippedSubviews();

    void setRemoveClippedSubviews(boolean removeClippedSubviews);

    void updateClippingRect();
}
