package com.facebook.react.viewmanagers;

import android.view.View;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.BaseViewManagerDelegate;
import com.facebook.react.uimanager.BaseViewManagerInterface;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.viewmanagers.ModalHostViewManagerInterface;
/* loaded from: classes.dex */
public class ModalHostViewManagerDelegate<T extends View, U extends BaseViewManagerInterface<T> & ModalHostViewManagerInterface<T>> extends BaseViewManagerDelegate<T, U> {
    public ModalHostViewManagerDelegate(BaseViewManagerInterface viewManager) {
        super(viewManager);
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerDelegate, com.facebook.react.uimanager.ViewManagerDelegate
    public void setProperty(T view, String propName, Object value) {
        propName.hashCode();
        boolean z = false;
        boolean z2 = false;
        boolean z3 = false;
        boolean z4 = false;
        boolean z5 = false;
        int i = 0;
        char c = 65535;
        switch (propName.hashCode()) {
            case -1851617609:
                if (propName.equals("presentationStyle")) {
                    c = 0;
                    break;
                }
                break;
            case -1850124175:
                if (propName.equals("supportedOrientations")) {
                    c = 1;
                    break;
                }
                break;
            case -1726194350:
                if (propName.equals("transparent")) {
                    c = 2;
                    break;
                }
                break;
            case -1618432855:
                if (propName.equals("identifier")) {
                    c = 3;
                    break;
                }
                break;
            case -1156137512:
                if (propName.equals("statusBarTranslucent")) {
                    c = 4;
                    break;
                }
                break;
            case -795203165:
                if (propName.equals("animated")) {
                    c = 5;
                    break;
                }
                break;
            case 466743410:
                if (propName.equals(ViewProps.VISIBLE)) {
                    c = 6;
                    break;
                }
                break;
            case 1195991583:
                if (propName.equals("hardwareAccelerated")) {
                    c = 7;
                    break;
                }
                break;
            case 2031205598:
                if (propName.equals("animationType")) {
                    c = '\b';
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                ((ModalHostViewManagerInterface) this.mViewManager).setPresentationStyle(view, (String) value);
                return;
            case 1:
                ((ModalHostViewManagerInterface) this.mViewManager).setSupportedOrientations(view, (ReadableArray) value);
                return;
            case 2:
                ModalHostViewManagerInterface modalHostViewManagerInterface = (ModalHostViewManagerInterface) this.mViewManager;
                if (value != null) {
                    z = ((Boolean) value).booleanValue();
                }
                modalHostViewManagerInterface.setTransparent(view, z);
                return;
            case 3:
                ModalHostViewManagerInterface modalHostViewManagerInterface2 = (ModalHostViewManagerInterface) this.mViewManager;
                if (value != null) {
                    i = ((Double) value).intValue();
                }
                modalHostViewManagerInterface2.setIdentifier(view, i);
                return;
            case 4:
                ModalHostViewManagerInterface modalHostViewManagerInterface3 = (ModalHostViewManagerInterface) this.mViewManager;
                if (value != null) {
                    z5 = ((Boolean) value).booleanValue();
                }
                modalHostViewManagerInterface3.setStatusBarTranslucent(view, z5);
                return;
            case 5:
                ModalHostViewManagerInterface modalHostViewManagerInterface4 = (ModalHostViewManagerInterface) this.mViewManager;
                if (value != null) {
                    z4 = ((Boolean) value).booleanValue();
                }
                modalHostViewManagerInterface4.setAnimated(view, z4);
                return;
            case 6:
                ModalHostViewManagerInterface modalHostViewManagerInterface5 = (ModalHostViewManagerInterface) this.mViewManager;
                if (value != null) {
                    z3 = ((Boolean) value).booleanValue();
                }
                modalHostViewManagerInterface5.setVisible(view, z3);
                return;
            case 7:
                ModalHostViewManagerInterface modalHostViewManagerInterface6 = (ModalHostViewManagerInterface) this.mViewManager;
                if (value != null) {
                    z2 = ((Boolean) value).booleanValue();
                }
                modalHostViewManagerInterface6.setHardwareAccelerated(view, z2);
                return;
            case '\b':
                ((ModalHostViewManagerInterface) this.mViewManager).setAnimationType(view, (String) value);
                return;
            default:
                super.setProperty(view, propName, value);
                return;
        }
    }
}
