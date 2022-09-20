package com.facebook.react.viewmanagers;

import android.view.View;
import androidx.core.app.NotificationCompat;
import com.facebook.react.bridge.ColorPropConverter;
import com.facebook.react.uimanager.BaseViewManagerDelegate;
import com.facebook.react.uimanager.BaseViewManagerInterface;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.viewmanagers.AndroidProgressBarManagerInterface;
/* loaded from: classes.dex */
public class AndroidProgressBarManagerDelegate<T extends View, U extends BaseViewManagerInterface<T> & AndroidProgressBarManagerInterface<T>> extends BaseViewManagerDelegate<T, U> {
    public AndroidProgressBarManagerDelegate(BaseViewManagerInterface viewManager) {
        super(viewManager);
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerDelegate, com.facebook.react.uimanager.ViewManagerDelegate
    public void setProperty(T view, String propName, Object value) {
        propName.hashCode();
        boolean z = true;
        boolean z2 = false;
        char c = 65535;
        switch (propName.hashCode()) {
            case -1001078227:
                if (propName.equals(NotificationCompat.CATEGORY_PROGRESS)) {
                    c = 0;
                    break;
                }
                break;
            case -877170387:
                if (propName.equals(ViewProps.TEST_ID)) {
                    c = 1;
                    break;
                }
                break;
            case -676876213:
                if (propName.equals("typeAttr")) {
                    c = 2;
                    break;
                }
                break;
            case 94842723:
                if (propName.equals(ViewProps.COLOR)) {
                    c = 3;
                    break;
                }
                break;
            case 633138363:
                if (propName.equals("indeterminate")) {
                    c = 4;
                    break;
                }
                break;
            case 1118509918:
                if (propName.equals("animating")) {
                    c = 5;
                    break;
                }
                break;
            case 1804741442:
                if (propName.equals("styleAttr")) {
                    c = 6;
                    break;
                }
                break;
        }
        String str = null;
        switch (c) {
            case 0:
                ((AndroidProgressBarManagerInterface) this.mViewManager).setProgress(view, value == null ? 0.0d : ((Double) value).doubleValue());
                return;
            case 1:
                ((AndroidProgressBarManagerInterface) this.mViewManager).setTestID(view, value == null ? "" : (String) value);
                return;
            case 2:
                AndroidProgressBarManagerInterface androidProgressBarManagerInterface = (AndroidProgressBarManagerInterface) this.mViewManager;
                if (value != null) {
                    str = (String) value;
                }
                androidProgressBarManagerInterface.setTypeAttr(view, str);
                return;
            case 3:
                ((AndroidProgressBarManagerInterface) this.mViewManager).setColor(view, ColorPropConverter.getColor(value, view.getContext()));
                return;
            case 4:
                AndroidProgressBarManagerInterface androidProgressBarManagerInterface2 = (AndroidProgressBarManagerInterface) this.mViewManager;
                if (value != null) {
                    z2 = ((Boolean) value).booleanValue();
                }
                androidProgressBarManagerInterface2.setIndeterminate(view, z2);
                return;
            case 5:
                AndroidProgressBarManagerInterface androidProgressBarManagerInterface3 = (AndroidProgressBarManagerInterface) this.mViewManager;
                if (value != null) {
                    z = ((Boolean) value).booleanValue();
                }
                androidProgressBarManagerInterface3.setAnimating(view, z);
                return;
            case 6:
                AndroidProgressBarManagerInterface androidProgressBarManagerInterface4 = (AndroidProgressBarManagerInterface) this.mViewManager;
                if (value != null) {
                    str = (String) value;
                }
                androidProgressBarManagerInterface4.setStyleAttr(view, str);
                return;
            default:
                super.setProperty(view, propName, value);
                return;
        }
    }
}
