package com.facebook.react.viewmanagers;

import android.view.View;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.BaseViewManagerDelegate;
import com.facebook.react.uimanager.BaseViewManagerInterface;
import com.facebook.react.viewmanagers.AndroidViewPagerManagerInterface;
/* loaded from: classes.dex */
public class AndroidViewPagerManagerDelegate<T extends View, U extends BaseViewManagerInterface<T> & AndroidViewPagerManagerInterface<T>> extends BaseViewManagerDelegate<T, U> {
    public AndroidViewPagerManagerDelegate(BaseViewManagerInterface viewManager) {
        super(viewManager);
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerDelegate, com.facebook.react.uimanager.ViewManagerDelegate
    public void setProperty(T view, String propName, Object value) {
        propName.hashCode();
        boolean z = true;
        int i = 0;
        boolean z2 = false;
        int i2 = 0;
        char c = 65535;
        switch (propName.hashCode()) {
            case -1151046732:
                if (propName.equals("scrollEnabled")) {
                    c = 0;
                    break;
                }
                break;
            case -764307226:
                if (propName.equals("keyboardDismissMode")) {
                    c = 1;
                    break;
                }
                break;
            case 1097821469:
                if (propName.equals("pageMargin")) {
                    c = 2;
                    break;
                }
                break;
            case 1233251315:
                if (propName.equals("initialPage")) {
                    c = 3;
                    break;
                }
                break;
            case 1919780198:
                if (propName.equals("peekEnabled")) {
                    c = 4;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                AndroidViewPagerManagerInterface androidViewPagerManagerInterface = (AndroidViewPagerManagerInterface) this.mViewManager;
                if (value != null) {
                    z = ((Boolean) value).booleanValue();
                }
                androidViewPagerManagerInterface.setScrollEnabled(view, z);
                return;
            case 1:
                ((AndroidViewPagerManagerInterface) this.mViewManager).setKeyboardDismissMode(view, (String) value);
                return;
            case 2:
                AndroidViewPagerManagerInterface androidViewPagerManagerInterface2 = (AndroidViewPagerManagerInterface) this.mViewManager;
                if (value != null) {
                    i = ((Double) value).intValue();
                }
                androidViewPagerManagerInterface2.setPageMargin(view, i);
                return;
            case 3:
                AndroidViewPagerManagerInterface androidViewPagerManagerInterface3 = (AndroidViewPagerManagerInterface) this.mViewManager;
                if (value != null) {
                    i2 = ((Double) value).intValue();
                }
                androidViewPagerManagerInterface3.setInitialPage(view, i2);
                return;
            case 4:
                AndroidViewPagerManagerInterface androidViewPagerManagerInterface4 = (AndroidViewPagerManagerInterface) this.mViewManager;
                if (value != null) {
                    z2 = ((Boolean) value).booleanValue();
                }
                androidViewPagerManagerInterface4.setPeekEnabled(view, z2);
                return;
            default:
                super.setProperty(view, propName, value);
                return;
        }
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerDelegate, com.facebook.react.uimanager.ViewManagerDelegate
    public void receiveCommand(T view, String commandName, ReadableArray args) {
        commandName.hashCode();
        if (commandName.equals("setPageWithoutAnimation")) {
            ((AndroidViewPagerManagerInterface) this.mViewManager).setPageWithoutAnimation(view, args.getInt(0));
        } else if (!commandName.equals("setPage")) {
        } else {
            ((AndroidViewPagerManagerInterface) this.mViewManager).setPage(view, args.getInt(0));
        }
    }
}
