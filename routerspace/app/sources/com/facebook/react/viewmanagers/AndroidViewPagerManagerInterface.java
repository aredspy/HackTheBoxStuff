package com.facebook.react.viewmanagers;

import android.view.View;
/* loaded from: classes.dex */
public interface AndroidViewPagerManagerInterface<T extends View> {
    void setInitialPage(T view, int value);

    void setKeyboardDismissMode(T view, String value);

    void setPage(T view, int page);

    void setPageMargin(T view, int value);

    void setPageWithoutAnimation(T view, int page);

    void setPeekEnabled(T view, boolean value);

    void setScrollEnabled(T view, boolean value);
}
