package com.facebook.react.viewmanagers;

import android.view.View;
import com.facebook.react.bridge.ColorPropConverter;
import com.facebook.react.uimanager.BaseViewManagerDelegate;
import com.facebook.react.uimanager.BaseViewManagerInterface;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.viewmanagers.ActivityIndicatorViewManagerInterface;
/* loaded from: classes.dex */
public class ActivityIndicatorViewManagerDelegate<T extends View, U extends BaseViewManagerInterface<T> & ActivityIndicatorViewManagerInterface<T>> extends BaseViewManagerDelegate<T, U> {
    public ActivityIndicatorViewManagerDelegate(BaseViewManagerInterface viewManager) {
        super(viewManager);
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerDelegate, com.facebook.react.uimanager.ViewManagerDelegate
    public void setProperty(T view, String propName, Object value) {
        propName.hashCode();
        boolean z = false;
        char c = 65535;
        switch (propName.hashCode()) {
            case 3530753:
                if (propName.equals("size")) {
                    c = 0;
                    break;
                }
                break;
            case 94842723:
                if (propName.equals(ViewProps.COLOR)) {
                    c = 1;
                    break;
                }
                break;
            case 865748226:
                if (propName.equals("hidesWhenStopped")) {
                    c = 2;
                    break;
                }
                break;
            case 1118509918:
                if (propName.equals("animating")) {
                    c = 3;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                ((ActivityIndicatorViewManagerInterface) this.mViewManager).setSize(view, (String) value);
                return;
            case 1:
                ((ActivityIndicatorViewManagerInterface) this.mViewManager).setColor(view, ColorPropConverter.getColor(value, view.getContext()));
                return;
            case 2:
                ActivityIndicatorViewManagerInterface activityIndicatorViewManagerInterface = (ActivityIndicatorViewManagerInterface) this.mViewManager;
                if (value != null) {
                    z = ((Boolean) value).booleanValue();
                }
                activityIndicatorViewManagerInterface.setHidesWhenStopped(view, z);
                return;
            case 3:
                ActivityIndicatorViewManagerInterface activityIndicatorViewManagerInterface2 = (ActivityIndicatorViewManagerInterface) this.mViewManager;
                if (value != null) {
                    z = ((Boolean) value).booleanValue();
                }
                activityIndicatorViewManagerInterface2.setAnimating(view, z);
                return;
            default:
                super.setProperty(view, propName, value);
                return;
        }
    }
}
