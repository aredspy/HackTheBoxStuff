package com.facebook.react.viewmanagers;

import android.view.View;
import com.facebook.react.bridge.ColorPropConverter;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.BaseViewManagerDelegate;
import com.facebook.react.uimanager.BaseViewManagerInterface;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.viewmanagers.AndroidSwipeRefreshLayoutManagerInterface;
/* loaded from: classes.dex */
public class AndroidSwipeRefreshLayoutManagerDelegate<T extends View, U extends BaseViewManagerInterface<T> & AndroidSwipeRefreshLayoutManagerInterface<T>> extends BaseViewManagerDelegate<T, U> {
    public AndroidSwipeRefreshLayoutManagerDelegate(BaseViewManagerInterface viewManager) {
        super(viewManager);
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerDelegate, com.facebook.react.uimanager.ViewManagerDelegate
    public void setProperty(T view, String propName, Object value) {
        propName.hashCode();
        boolean z = true;
        boolean z2 = false;
        char c = 65535;
        switch (propName.hashCode()) {
            case -1609594047:
                if (propName.equals(ViewProps.ENABLED)) {
                    c = 0;
                    break;
                }
                break;
            case -1354842768:
                if (propName.equals("colors")) {
                    c = 1;
                    break;
                }
                break;
            case -885150488:
                if (propName.equals("progressBackgroundColor")) {
                    c = 2;
                    break;
                }
                break;
            case -416037467:
                if (propName.equals("progressViewOffset")) {
                    c = 3;
                    break;
                }
                break;
            case -321826009:
                if (propName.equals("refreshing")) {
                    c = 4;
                    break;
                }
                break;
            case 3530753:
                if (propName.equals("size")) {
                    c = 5;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                AndroidSwipeRefreshLayoutManagerInterface androidSwipeRefreshLayoutManagerInterface = (AndroidSwipeRefreshLayoutManagerInterface) this.mViewManager;
                if (value != null) {
                    z = ((Boolean) value).booleanValue();
                }
                androidSwipeRefreshLayoutManagerInterface.setEnabled(view, z);
                return;
            case 1:
                ((AndroidSwipeRefreshLayoutManagerInterface) this.mViewManager).setColors(view, (ReadableArray) value);
                return;
            case 2:
                ((AndroidSwipeRefreshLayoutManagerInterface) this.mViewManager).setProgressBackgroundColor(view, ColorPropConverter.getColor(value, view.getContext()));
                return;
            case 3:
                ((AndroidSwipeRefreshLayoutManagerInterface) this.mViewManager).setProgressViewOffset(view, value == null ? 0.0f : ((Double) value).floatValue());
                return;
            case 4:
                AndroidSwipeRefreshLayoutManagerInterface androidSwipeRefreshLayoutManagerInterface2 = (AndroidSwipeRefreshLayoutManagerInterface) this.mViewManager;
                if (value != null) {
                    z2 = ((Boolean) value).booleanValue();
                }
                androidSwipeRefreshLayoutManagerInterface2.setRefreshing(view, z2);
                return;
            case 5:
                ((AndroidSwipeRefreshLayoutManagerInterface) this.mViewManager).setSize(view, (String) value);
                return;
            default:
                super.setProperty(view, propName, value);
                return;
        }
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerDelegate, com.facebook.react.uimanager.ViewManagerDelegate
    public void receiveCommand(T view, String commandName, ReadableArray args) {
        commandName.hashCode();
        if (!commandName.equals("setNativeRefreshing")) {
            return;
        }
        ((AndroidSwipeRefreshLayoutManagerInterface) this.mViewManager).setNativeRefreshing(view, args.getBoolean(0));
    }
}
