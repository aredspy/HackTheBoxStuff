package com.facebook.react.viewmanagers;

import android.view.View;
import com.facebook.react.uimanager.BaseViewManagerDelegate;
import com.facebook.react.uimanager.BaseViewManagerInterface;
import com.facebook.react.viewmanagers.SafeAreaViewManagerInterface;
/* loaded from: classes.dex */
public class SafeAreaViewManagerDelegate<T extends View, U extends BaseViewManagerInterface<T> & SafeAreaViewManagerInterface<T>> extends BaseViewManagerDelegate<T, U> {
    public SafeAreaViewManagerDelegate(BaseViewManagerInterface viewManager) {
        super(viewManager);
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerDelegate, com.facebook.react.uimanager.ViewManagerDelegate
    public void setProperty(T view, String propName, Object value) {
        propName.hashCode();
        if (propName.equals("emulateUnlessSupported")) {
            ((SafeAreaViewManagerInterface) this.mViewManager).setEmulateUnlessSupported(view, value == null ? false : ((Boolean) value).booleanValue());
        } else {
            super.setProperty(view, propName, value);
        }
    }
}
