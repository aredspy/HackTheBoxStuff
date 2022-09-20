package com.facebook.react.viewmanagers;

import android.view.View;
import com.facebook.react.bridge.ColorPropConverter;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.BaseViewManagerDelegate;
import com.facebook.react.uimanager.BaseViewManagerInterface;
import com.facebook.react.viewmanagers.PullToRefreshViewManagerInterface;
/* loaded from: classes.dex */
public class PullToRefreshViewManagerDelegate<T extends View, U extends BaseViewManagerInterface<T> & PullToRefreshViewManagerInterface<T>> extends BaseViewManagerDelegate<T, U> {
    public PullToRefreshViewManagerDelegate(BaseViewManagerInterface viewManager) {
        super(viewManager);
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerDelegate, com.facebook.react.uimanager.ViewManagerDelegate
    public void setProperty(T view, String propName, Object value) {
        propName.hashCode();
        boolean z = false;
        char c = 65535;
        switch (propName.hashCode()) {
            case -1799367701:
                if (propName.equals("titleColor")) {
                    c = 0;
                    break;
                }
                break;
            case -321826009:
                if (propName.equals("refreshing")) {
                    c = 1;
                    break;
                }
                break;
            case 110371416:
                if (propName.equals("title")) {
                    c = 2;
                    break;
                }
                break;
            case 1327599912:
                if (propName.equals("tintColor")) {
                    c = 3;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                ((PullToRefreshViewManagerInterface) this.mViewManager).setTitleColor(view, ColorPropConverter.getColor(value, view.getContext()));
                return;
            case 1:
                PullToRefreshViewManagerInterface pullToRefreshViewManagerInterface = (PullToRefreshViewManagerInterface) this.mViewManager;
                if (value != null) {
                    z = ((Boolean) value).booleanValue();
                }
                pullToRefreshViewManagerInterface.setRefreshing(view, z);
                return;
            case 2:
                ((PullToRefreshViewManagerInterface) this.mViewManager).setTitle(view, value == null ? null : (String) value);
                return;
            case 3:
                ((PullToRefreshViewManagerInterface) this.mViewManager).setTintColor(view, ColorPropConverter.getColor(value, view.getContext()));
                return;
            default:
                super.setProperty(view, propName, value);
                return;
        }
    }

    public void receiveCommand(PullToRefreshViewManagerInterface<T> viewManager, T view, String commandName, ReadableArray args) {
        commandName.hashCode();
        if (!commandName.equals("setNativeRefreshing")) {
            return;
        }
        viewManager.setNativeRefreshing(view, args.getBoolean(0));
    }
}
