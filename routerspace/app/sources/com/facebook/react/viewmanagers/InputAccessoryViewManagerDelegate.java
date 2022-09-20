package com.facebook.react.viewmanagers;

import android.view.View;
import com.facebook.react.bridge.ColorPropConverter;
import com.facebook.react.uimanager.BaseViewManagerDelegate;
import com.facebook.react.uimanager.BaseViewManagerInterface;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.viewmanagers.InputAccessoryViewManagerInterface;
/* loaded from: classes.dex */
public class InputAccessoryViewManagerDelegate<T extends View, U extends BaseViewManagerInterface<T> & InputAccessoryViewManagerInterface<T>> extends BaseViewManagerDelegate<T, U> {
    public InputAccessoryViewManagerDelegate(BaseViewManagerInterface viewManager) {
        super(viewManager);
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerDelegate, com.facebook.react.uimanager.ViewManagerDelegate
    public void setProperty(T view, String propName, Object value) {
        propName.hashCode();
        if (propName.equals(ViewProps.BACKGROUND_COLOR)) {
            ((InputAccessoryViewManagerInterface) this.mViewManager).setBackgroundColor(view, ColorPropConverter.getColor(value, view.getContext()));
        } else {
            super.setProperty(view, propName, value);
        }
    }
}
