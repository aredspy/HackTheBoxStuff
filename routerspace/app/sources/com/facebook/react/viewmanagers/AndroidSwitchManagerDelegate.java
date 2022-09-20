package com.facebook.react.viewmanagers;

import android.view.View;
import com.facebook.react.bridge.ColorPropConverter;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.BaseViewManagerDelegate;
import com.facebook.react.uimanager.BaseViewManagerInterface;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.viewmanagers.AndroidSwitchManagerInterface;
/* loaded from: classes.dex */
public class AndroidSwitchManagerDelegate<T extends View, U extends BaseViewManagerInterface<T> & AndroidSwitchManagerInterface<T>> extends BaseViewManagerDelegate<T, U> {
    public AndroidSwitchManagerDelegate(BaseViewManagerInterface viewManager) {
        super(viewManager);
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerDelegate, com.facebook.react.uimanager.ViewManagerDelegate
    public void setProperty(T view, String propName, Object value) {
        propName.hashCode();
        boolean z = true;
        boolean z2 = false;
        char c = 65535;
        switch (propName.hashCode()) {
            case -1742453971:
                if (propName.equals("thumbColor")) {
                    c = 0;
                    break;
                }
                break;
            case -1609594047:
                if (propName.equals(ViewProps.ENABLED)) {
                    c = 1;
                    break;
                }
                break;
            case -287374307:
                if (propName.equals("trackTintColor")) {
                    c = 2;
                    break;
                }
                break;
            case 3551:
                if (propName.equals(ViewProps.ON)) {
                    c = 3;
                    break;
                }
                break;
            case 111972721:
                if (propName.equals("value")) {
                    c = 4;
                    break;
                }
                break;
            case 270940796:
                if (propName.equals("disabled")) {
                    c = 5;
                    break;
                }
                break;
            case 1084662482:
                if (propName.equals("trackColorForFalse")) {
                    c = 6;
                    break;
                }
                break;
            case 1912319986:
                if (propName.equals("thumbTintColor")) {
                    c = 7;
                    break;
                }
                break;
            case 2113632767:
                if (propName.equals("trackColorForTrue")) {
                    c = '\b';
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                ((AndroidSwitchManagerInterface) this.mViewManager).setThumbColor(view, ColorPropConverter.getColor(value, view.getContext()));
                return;
            case 1:
                AndroidSwitchManagerInterface androidSwitchManagerInterface = (AndroidSwitchManagerInterface) this.mViewManager;
                if (value != null) {
                    z = ((Boolean) value).booleanValue();
                }
                androidSwitchManagerInterface.setEnabled(view, z);
                return;
            case 2:
                ((AndroidSwitchManagerInterface) this.mViewManager).setTrackTintColor(view, ColorPropConverter.getColor(value, view.getContext()));
                return;
            case 3:
                AndroidSwitchManagerInterface androidSwitchManagerInterface2 = (AndroidSwitchManagerInterface) this.mViewManager;
                if (value != null) {
                    z2 = ((Boolean) value).booleanValue();
                }
                androidSwitchManagerInterface2.setOn(view, z2);
                return;
            case 4:
                AndroidSwitchManagerInterface androidSwitchManagerInterface3 = (AndroidSwitchManagerInterface) this.mViewManager;
                if (value != null) {
                    z2 = ((Boolean) value).booleanValue();
                }
                androidSwitchManagerInterface3.setValue(view, z2);
                return;
            case 5:
                AndroidSwitchManagerInterface androidSwitchManagerInterface4 = (AndroidSwitchManagerInterface) this.mViewManager;
                if (value != null) {
                    z2 = ((Boolean) value).booleanValue();
                }
                androidSwitchManagerInterface4.setDisabled(view, z2);
                return;
            case 6:
                ((AndroidSwitchManagerInterface) this.mViewManager).setTrackColorForFalse(view, ColorPropConverter.getColor(value, view.getContext()));
                return;
            case 7:
                ((AndroidSwitchManagerInterface) this.mViewManager).setThumbTintColor(view, ColorPropConverter.getColor(value, view.getContext()));
                return;
            case '\b':
                ((AndroidSwitchManagerInterface) this.mViewManager).setTrackColorForTrue(view, ColorPropConverter.getColor(value, view.getContext()));
                return;
            default:
                super.setProperty(view, propName, value);
                return;
        }
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerDelegate, com.facebook.react.uimanager.ViewManagerDelegate
    public void receiveCommand(T view, String commandName, ReadableArray args) {
        commandName.hashCode();
        if (!commandName.equals("setNativeValue")) {
            return;
        }
        ((AndroidSwitchManagerInterface) this.mViewManager).setNativeValue(view, args.getBoolean(0));
    }
}
