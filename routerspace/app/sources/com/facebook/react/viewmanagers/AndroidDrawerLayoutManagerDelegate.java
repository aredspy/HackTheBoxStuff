package com.facebook.react.viewmanagers;

import android.view.View;
import com.facebook.react.bridge.ColorPropConverter;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.BaseViewManagerDelegate;
import com.facebook.react.uimanager.BaseViewManagerInterface;
import com.facebook.react.viewmanagers.AndroidDrawerLayoutManagerInterface;
/* loaded from: classes.dex */
public class AndroidDrawerLayoutManagerDelegate<T extends View, U extends BaseViewManagerInterface<T> & AndroidDrawerLayoutManagerInterface<T>> extends BaseViewManagerDelegate<T, U> {
    public AndroidDrawerLayoutManagerDelegate(BaseViewManagerInterface viewManager) {
        super(viewManager);
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerDelegate, com.facebook.react.uimanager.ViewManagerDelegate
    public void setProperty(T view, String propName, Object value) {
        propName.hashCode();
        char c = 65535;
        switch (propName.hashCode()) {
            case -2082382380:
                if (propName.equals("statusBarBackgroundColor")) {
                    c = 0;
                    break;
                }
                break;
            case -1233873500:
                if (propName.equals("drawerBackgroundColor")) {
                    c = 1;
                    break;
                }
                break;
            case -764307226:
                if (propName.equals("keyboardDismissMode")) {
                    c = 2;
                    break;
                }
                break;
            case 268251989:
                if (propName.equals("drawerWidth")) {
                    c = 3;
                    break;
                }
                break;
            case 695891258:
                if (propName.equals("drawerPosition")) {
                    c = 4;
                    break;
                }
                break;
            case 1857208703:
                if (propName.equals("drawerLockMode")) {
                    c = 5;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                ((AndroidDrawerLayoutManagerInterface) this.mViewManager).setStatusBarBackgroundColor(view, ColorPropConverter.getColor(value, view.getContext()));
                return;
            case 1:
                ((AndroidDrawerLayoutManagerInterface) this.mViewManager).setDrawerBackgroundColor(view, ColorPropConverter.getColor(value, view.getContext()));
                return;
            case 2:
                ((AndroidDrawerLayoutManagerInterface) this.mViewManager).setKeyboardDismissMode(view, (String) value);
                return;
            case 3:
                ((AndroidDrawerLayoutManagerInterface) this.mViewManager).setDrawerWidth(view, value == null ? null : Float.valueOf(((Double) value).floatValue()));
                return;
            case 4:
                ((AndroidDrawerLayoutManagerInterface) this.mViewManager).setDrawerPosition(view, (String) value);
                return;
            case 5:
                ((AndroidDrawerLayoutManagerInterface) this.mViewManager).setDrawerLockMode(view, (String) value);
                return;
            default:
                super.setProperty(view, propName, value);
                return;
        }
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerDelegate, com.facebook.react.uimanager.ViewManagerDelegate
    public void receiveCommand(T view, String commandName, ReadableArray args) {
        commandName.hashCode();
        if (commandName.equals("closeDrawer")) {
            ((AndroidDrawerLayoutManagerInterface) this.mViewManager).closeDrawer(view);
        } else if (!commandName.equals("openDrawer")) {
        } else {
            ((AndroidDrawerLayoutManagerInterface) this.mViewManager).openDrawer(view);
        }
    }
}
