package com.facebook.react.viewmanagers;

import android.view.View;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.BaseViewManagerDelegate;
import com.facebook.react.uimanager.BaseViewManagerInterface;
import com.facebook.react.viewmanagers.DatePickerManagerInterface;
/* loaded from: classes.dex */
public class DatePickerManagerDelegate<T extends View, U extends BaseViewManagerInterface<T> & DatePickerManagerInterface<T>> extends BaseViewManagerDelegate<T, U> {
    public DatePickerManagerDelegate(BaseViewManagerInterface viewManager) {
        super(viewManager);
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerDelegate, com.facebook.react.uimanager.ViewManagerDelegate
    public void setProperty(T view, String propName, Object value) {
        propName.hashCode();
        int i = 1;
        char c = 65535;
        switch (propName.hashCode()) {
            case -1339516167:
                if (propName.equals("minuteInterval")) {
                    c = 0;
                    break;
                }
                break;
            case -1097462182:
                if (propName.equals("locale")) {
                    c = 1;
                    break;
                }
                break;
            case -292758706:
                if (propName.equals("timeZoneOffsetInMinutes")) {
                    c = 2;
                    break;
                }
                break;
            case 3076014:
                if (propName.equals("date")) {
                    c = 3;
                    break;
                }
                break;
            case 3357091:
                if (propName.equals("mode")) {
                    c = 4;
                    break;
                }
                break;
            case 1007762652:
                if (propName.equals("minimumDate")) {
                    c = 5;
                    break;
                }
                break;
            case 1232894226:
                if (propName.equals("initialDate")) {
                    c = 6;
                    break;
                }
                break;
            case 1685195246:
                if (propName.equals("maximumDate")) {
                    c = 7;
                    break;
                }
                break;
        }
        float f = 0.0f;
        switch (c) {
            case 0:
                DatePickerManagerInterface datePickerManagerInterface = (DatePickerManagerInterface) this.mViewManager;
                if (value != null) {
                    i = ((Double) value).intValue();
                }
                datePickerManagerInterface.setMinuteInterval(view, Integer.valueOf(i));
                return;
            case 1:
                ((DatePickerManagerInterface) this.mViewManager).setLocale(view, value == null ? null : (String) value);
                return;
            case 2:
                DatePickerManagerInterface datePickerManagerInterface2 = (DatePickerManagerInterface) this.mViewManager;
                if (value != null) {
                    f = ((Double) value).floatValue();
                }
                datePickerManagerInterface2.setTimeZoneOffsetInMinutes(view, f);
                return;
            case 3:
                DatePickerManagerInterface datePickerManagerInterface3 = (DatePickerManagerInterface) this.mViewManager;
                if (value != null) {
                    f = ((Double) value).floatValue();
                }
                datePickerManagerInterface3.setDate(view, f);
                return;
            case 4:
                ((DatePickerManagerInterface) this.mViewManager).setMode(view, (String) value);
                return;
            case 5:
                DatePickerManagerInterface datePickerManagerInterface4 = (DatePickerManagerInterface) this.mViewManager;
                if (value != null) {
                    f = ((Double) value).floatValue();
                }
                datePickerManagerInterface4.setMinimumDate(view, f);
                return;
            case 6:
                DatePickerManagerInterface datePickerManagerInterface5 = (DatePickerManagerInterface) this.mViewManager;
                if (value != null) {
                    f = ((Double) value).floatValue();
                }
                datePickerManagerInterface5.setInitialDate(view, f);
                return;
            case 7:
                DatePickerManagerInterface datePickerManagerInterface6 = (DatePickerManagerInterface) this.mViewManager;
                if (value != null) {
                    f = ((Double) value).floatValue();
                }
                datePickerManagerInterface6.setMaximumDate(view, f);
                return;
            default:
                super.setProperty(view, propName, value);
                return;
        }
    }

    public void receiveCommand(DatePickerManagerInterface<T> viewManager, T view, String commandName, ReadableArray args) {
        commandName.hashCode();
        if (!commandName.equals("setNativeDate")) {
            return;
        }
        viewManager.setNativeDate(view, (float) args.getDouble(0));
    }
}
