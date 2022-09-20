package com.facebook.react.viewmanagers;

import android.view.View;
import androidx.autofill.HintConstants;
import com.facebook.react.uimanager.BaseViewManagerDelegate;
import com.facebook.react.uimanager.BaseViewManagerInterface;
import com.facebook.react.viewmanagers.UnimplementedNativeViewManagerInterface;
/* loaded from: classes.dex */
public class UnimplementedNativeViewManagerDelegate<T extends View, U extends BaseViewManagerInterface<T> & UnimplementedNativeViewManagerInterface<T>> extends BaseViewManagerDelegate<T, U> {
    public UnimplementedNativeViewManagerDelegate(BaseViewManagerInterface viewManager) {
        super(viewManager);
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerDelegate, com.facebook.react.uimanager.ViewManagerDelegate
    public void setProperty(T view, String propName, Object value) {
        propName.hashCode();
        if (propName.equals(HintConstants.AUTOFILL_HINT_NAME)) {
            ((UnimplementedNativeViewManagerInterface) this.mViewManager).setName(view, value == null ? "" : (String) value);
        } else {
            super.setProperty(view, propName, value);
        }
    }
}
