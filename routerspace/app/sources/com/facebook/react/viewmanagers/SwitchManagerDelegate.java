package com.facebook.react.viewmanagers;

import android.view.View;
import com.facebook.react.bridge.ColorPropConverter;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.BaseViewManagerDelegate;
import com.facebook.react.uimanager.BaseViewManagerInterface;
import com.facebook.react.viewmanagers.SwitchManagerInterface;
/* loaded from: classes.dex */
public class SwitchManagerDelegate<T extends View, U extends BaseViewManagerInterface<T> & SwitchManagerInterface<T>> extends BaseViewManagerDelegate<T, U> {
    public SwitchManagerDelegate(BaseViewManagerInterface viewManager) {
        super(viewManager);
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerDelegate, com.facebook.react.uimanager.ViewManagerDelegate
    public void setProperty(T view, String propName, Object value) {
        propName.hashCode();
        boolean z = false;
        char c = 65535;
        switch (propName.hashCode()) {
            case -1742453971:
                if (propName.equals("thumbColor")) {
                    c = 0;
                    break;
                }
                break;
            case 111972721:
                if (propName.equals("value")) {
                    c = 1;
                    break;
                }
                break;
            case 270940796:
                if (propName.equals("disabled")) {
                    c = 2;
                    break;
                }
                break;
            case 1084662482:
                if (propName.equals("trackColorForFalse")) {
                    c = 3;
                    break;
                }
                break;
            case 1296813577:
                if (propName.equals("onTintColor")) {
                    c = 4;
                    break;
                }
                break;
            case 1327599912:
                if (propName.equals("tintColor")) {
                    c = 5;
                    break;
                }
                break;
            case 1912319986:
                if (propName.equals("thumbTintColor")) {
                    c = 6;
                    break;
                }
                break;
            case 2113632767:
                if (propName.equals("trackColorForTrue")) {
                    c = 7;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                ((SwitchManagerInterface) this.mViewManager).setThumbColor(view, ColorPropConverter.getColor(value, view.getContext()));
                return;
            case 1:
                SwitchManagerInterface switchManagerInterface = (SwitchManagerInterface) this.mViewManager;
                if (value != null) {
                    z = ((Boolean) value).booleanValue();
                }
                switchManagerInterface.setValue(view, z);
                return;
            case 2:
                SwitchManagerInterface switchManagerInterface2 = (SwitchManagerInterface) this.mViewManager;
                if (value != null) {
                    z = ((Boolean) value).booleanValue();
                }
                switchManagerInterface2.setDisabled(view, z);
                return;
            case 3:
                ((SwitchManagerInterface) this.mViewManager).setTrackColorForFalse(view, ColorPropConverter.getColor(value, view.getContext()));
                return;
            case 4:
                ((SwitchManagerInterface) this.mViewManager).setOnTintColor(view, ColorPropConverter.getColor(value, view.getContext()));
                return;
            case 5:
                ((SwitchManagerInterface) this.mViewManager).setTintColor(view, ColorPropConverter.getColor(value, view.getContext()));
                return;
            case 6:
                ((SwitchManagerInterface) this.mViewManager).setThumbTintColor(view, ColorPropConverter.getColor(value, view.getContext()));
                return;
            case 7:
                ((SwitchManagerInterface) this.mViewManager).setTrackColorForTrue(view, ColorPropConverter.getColor(value, view.getContext()));
                return;
            default:
                super.setProperty(view, propName, value);
                return;
        }
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerDelegate, com.facebook.react.uimanager.ViewManagerDelegate
    public void receiveCommand(T view, String commandName, ReadableArray args) {
        commandName.hashCode();
        if (!commandName.equals("setValue")) {
            return;
        }
        ((SwitchManagerInterface) this.mViewManager).setValue(view, args.getBoolean(0));
    }
}
