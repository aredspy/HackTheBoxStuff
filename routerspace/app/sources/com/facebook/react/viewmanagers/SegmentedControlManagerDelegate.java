package com.facebook.react.viewmanagers;

import android.view.View;
import com.facebook.react.bridge.ColorPropConverter;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.BaseViewManagerDelegate;
import com.facebook.react.uimanager.BaseViewManagerInterface;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.viewmanagers.SegmentedControlManagerInterface;
/* loaded from: classes.dex */
public class SegmentedControlManagerDelegate<T extends View, U extends BaseViewManagerInterface<T> & SegmentedControlManagerInterface<T>> extends BaseViewManagerDelegate<T, U> {
    public SegmentedControlManagerDelegate(BaseViewManagerInterface viewManager) {
        super(viewManager);
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerDelegate, com.facebook.react.uimanager.ViewManagerDelegate
    public void setProperty(T view, String propName, Object value) {
        propName.hashCode();
        boolean z = true;
        int i = 0;
        boolean z2 = false;
        char c = 65535;
        switch (propName.hashCode()) {
            case -1609594047:
                if (propName.equals(ViewProps.ENABLED)) {
                    c = 0;
                    break;
                }
                break;
            case -1063571914:
                if (propName.equals("textColor")) {
                    c = 1;
                    break;
                }
                break;
            case -823812830:
                if (propName.equals("values")) {
                    c = 2;
                    break;
                }
                break;
            case 1287124693:
                if (propName.equals(ViewProps.BACKGROUND_COLOR)) {
                    c = 3;
                    break;
                }
                break;
            case 1327599912:
                if (propName.equals("tintColor")) {
                    c = 4;
                    break;
                }
                break;
            case 1436069623:
                if (propName.equals("selectedIndex")) {
                    c = 5;
                    break;
                }
                break;
            case 1684715624:
                if (propName.equals("momentary")) {
                    c = 6;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                SegmentedControlManagerInterface segmentedControlManagerInterface = (SegmentedControlManagerInterface) this.mViewManager;
                if (value != null) {
                    z = ((Boolean) value).booleanValue();
                }
                segmentedControlManagerInterface.setEnabled(view, z);
                return;
            case 1:
                ((SegmentedControlManagerInterface) this.mViewManager).setTextColor(view, ColorPropConverter.getColor(value, view.getContext()));
                return;
            case 2:
                ((SegmentedControlManagerInterface) this.mViewManager).setValues(view, (ReadableArray) value);
                return;
            case 3:
                ((SegmentedControlManagerInterface) this.mViewManager).setBackgroundColor(view, ColorPropConverter.getColor(value, view.getContext()));
                return;
            case 4:
                ((SegmentedControlManagerInterface) this.mViewManager).setTintColor(view, ColorPropConverter.getColor(value, view.getContext()));
                return;
            case 5:
                SegmentedControlManagerInterface segmentedControlManagerInterface2 = (SegmentedControlManagerInterface) this.mViewManager;
                if (value != null) {
                    i = ((Double) value).intValue();
                }
                segmentedControlManagerInterface2.setSelectedIndex(view, i);
                return;
            case 6:
                SegmentedControlManagerInterface segmentedControlManagerInterface3 = (SegmentedControlManagerInterface) this.mViewManager;
                if (value != null) {
                    z2 = ((Boolean) value).booleanValue();
                }
                segmentedControlManagerInterface3.setMomentary(view, z2);
                return;
            default:
                super.setProperty(view, propName, value);
                return;
        }
    }
}
