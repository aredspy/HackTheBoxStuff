package com.facebook.react.viewmanagers;

import android.view.View;
import com.facebook.react.uimanager.BaseViewManagerDelegate;
import com.facebook.react.uimanager.BaseViewManagerInterface;
import com.facebook.react.viewmanagers.MaskedViewManagerInterface;
/* loaded from: classes.dex */
public class MaskedViewManagerDelegate<T extends View, U extends BaseViewManagerInterface<T> & MaskedViewManagerInterface<T>> extends BaseViewManagerDelegate<T, U> {
    public MaskedViewManagerDelegate(BaseViewManagerInterface viewManager) {
        super(viewManager);
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerDelegate, com.facebook.react.uimanager.ViewManagerDelegate
    public void setProperty(T view, String propName, Object value) {
        super.setProperty(view, propName, value);
    }
}
