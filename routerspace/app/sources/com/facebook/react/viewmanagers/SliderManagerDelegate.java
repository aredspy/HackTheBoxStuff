package com.facebook.react.viewmanagers;

import android.view.View;
import com.facebook.react.bridge.ColorPropConverter;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.BaseViewManagerDelegate;
import com.facebook.react.uimanager.BaseViewManagerInterface;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.viewmanagers.SliderManagerInterface;
/* loaded from: classes.dex */
public class SliderManagerDelegate<T extends View, U extends BaseViewManagerInterface<T> & SliderManagerInterface<T>> extends BaseViewManagerDelegate<T, U> {
    public SliderManagerDelegate(BaseViewManagerInterface viewManager) {
        super(viewManager);
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerDelegate, com.facebook.react.uimanager.ViewManagerDelegate
    public void setProperty(T view, String propName, Object value) {
        propName.hashCode();
        boolean z = true;
        boolean z2 = false;
        char c = 65535;
        switch (propName.hashCode()) {
            case -1900655011:
                if (propName.equals("maximumTrackTintColor")) {
                    c = 0;
                    break;
                }
                break;
            case -1736983259:
                if (propName.equals("thumbImage")) {
                    c = 1;
                    break;
                }
                break;
            case -1609594047:
                if (propName.equals(ViewProps.ENABLED)) {
                    c = 2;
                    break;
                }
                break;
            case -1021497397:
                if (propName.equals("minimumTrackTintColor")) {
                    c = 3;
                    break;
                }
                break;
            case -981448432:
                if (propName.equals("maximumTrackImage")) {
                    c = 4;
                    break;
                }
                break;
            case -877170387:
                if (propName.equals(ViewProps.TEST_ID)) {
                    c = 5;
                    break;
                }
                break;
            case 3540684:
                if (propName.equals("step")) {
                    c = 6;
                    break;
                }
                break;
            case 111972721:
                if (propName.equals("value")) {
                    c = 7;
                    break;
                }
                break;
            case 270940796:
                if (propName.equals("disabled")) {
                    c = '\b';
                    break;
                }
                break;
            case 718061361:
                if (propName.equals("maximumValue")) {
                    c = '\t';
                    break;
                }
                break;
            case 1139400400:
                if (propName.equals("trackImage")) {
                    c = '\n';
                    break;
                }
                break;
            case 1192487427:
                if (propName.equals("minimumValue")) {
                    c = 11;
                    break;
                }
                break;
            case 1333596542:
                if (propName.equals("minimumTrackImage")) {
                    c = '\f';
                    break;
                }
                break;
            case 1912319986:
                if (propName.equals("thumbTintColor")) {
                    c = '\r';
                    break;
                }
                break;
        }
        double d = 0.0d;
        switch (c) {
            case 0:
                ((SliderManagerInterface) this.mViewManager).setMaximumTrackTintColor(view, ColorPropConverter.getColor(value, view.getContext()));
                return;
            case 1:
                ((SliderManagerInterface) this.mViewManager).setThumbImage(view, (ReadableMap) value);
                return;
            case 2:
                SliderManagerInterface sliderManagerInterface = (SliderManagerInterface) this.mViewManager;
                if (value != null) {
                    z = ((Boolean) value).booleanValue();
                }
                sliderManagerInterface.setEnabled(view, z);
                return;
            case 3:
                ((SliderManagerInterface) this.mViewManager).setMinimumTrackTintColor(view, ColorPropConverter.getColor(value, view.getContext()));
                return;
            case 4:
                ((SliderManagerInterface) this.mViewManager).setMaximumTrackImage(view, (ReadableMap) value);
                return;
            case 5:
                ((SliderManagerInterface) this.mViewManager).setTestID(view, value == null ? "" : (String) value);
                return;
            case 6:
                SliderManagerInterface sliderManagerInterface2 = (SliderManagerInterface) this.mViewManager;
                if (value != null) {
                    d = ((Double) value).doubleValue();
                }
                sliderManagerInterface2.setStep(view, d);
                return;
            case 7:
                SliderManagerInterface sliderManagerInterface3 = (SliderManagerInterface) this.mViewManager;
                if (value != null) {
                    d = ((Double) value).doubleValue();
                }
                sliderManagerInterface3.setValue(view, d);
                return;
            case '\b':
                SliderManagerInterface sliderManagerInterface4 = (SliderManagerInterface) this.mViewManager;
                if (value != null) {
                    z2 = ((Boolean) value).booleanValue();
                }
                sliderManagerInterface4.setDisabled(view, z2);
                return;
            case '\t':
                ((SliderManagerInterface) this.mViewManager).setMaximumValue(view, value == null ? 1.0d : ((Double) value).doubleValue());
                return;
            case '\n':
                ((SliderManagerInterface) this.mViewManager).setTrackImage(view, (ReadableMap) value);
                return;
            case 11:
                SliderManagerInterface sliderManagerInterface5 = (SliderManagerInterface) this.mViewManager;
                if (value != null) {
                    d = ((Double) value).doubleValue();
                }
                sliderManagerInterface5.setMinimumValue(view, d);
                return;
            case '\f':
                ((SliderManagerInterface) this.mViewManager).setMinimumTrackImage(view, (ReadableMap) value);
                return;
            case '\r':
                ((SliderManagerInterface) this.mViewManager).setThumbTintColor(view, ColorPropConverter.getColor(value, view.getContext()));
                return;
            default:
                super.setProperty(view, propName, value);
                return;
        }
    }
}
