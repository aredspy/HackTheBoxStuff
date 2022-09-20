package com.facebook.react.viewmanagers;

import android.view.View;
import androidx.core.app.NotificationCompat;
import com.facebook.react.bridge.ColorPropConverter;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.BaseViewManagerDelegate;
import com.facebook.react.uimanager.BaseViewManagerInterface;
import com.facebook.react.viewmanagers.ProgressViewManagerInterface;
/* loaded from: classes.dex */
public class ProgressViewManagerDelegate<T extends View, U extends BaseViewManagerInterface<T> & ProgressViewManagerInterface<T>> extends BaseViewManagerDelegate<T, U> {
    public ProgressViewManagerDelegate(BaseViewManagerInterface viewManager) {
        super(viewManager);
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerDelegate, com.facebook.react.uimanager.ViewManagerDelegate
    public void setProperty(T view, String propName, Object value) {
        propName.hashCode();
        char c = 65535;
        switch (propName.hashCode()) {
            case -1948954017:
                if (propName.equals("progressViewStyle")) {
                    c = 0;
                    break;
                }
                break;
            case -1001078227:
                if (propName.equals(NotificationCompat.CATEGORY_PROGRESS)) {
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
            case 760630062:
                if (propName.equals("progressImage")) {
                    c = 3;
                    break;
                }
                break;
            case 962728315:
                if (propName.equals("progressTintColor")) {
                    c = 4;
                    break;
                }
                break;
            case 1139400400:
                if (propName.equals("trackImage")) {
                    c = 5;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                ((ProgressViewManagerInterface) this.mViewManager).setProgressViewStyle(view, (String) value);
                return;
            case 1:
                ((ProgressViewManagerInterface) this.mViewManager).setProgress(view, value == null ? 0.0f : ((Double) value).floatValue());
                return;
            case 2:
                ((ProgressViewManagerInterface) this.mViewManager).setTrackTintColor(view, ColorPropConverter.getColor(value, view.getContext()));
                return;
            case 3:
                ((ProgressViewManagerInterface) this.mViewManager).setProgressImage(view, (ReadableMap) value);
                return;
            case 4:
                ((ProgressViewManagerInterface) this.mViewManager).setProgressTintColor(view, ColorPropConverter.getColor(value, view.getContext()));
                return;
            case 5:
                ((ProgressViewManagerInterface) this.mViewManager).setTrackImage(view, (ReadableMap) value);
                return;
            default:
                super.setProperty(view, propName, value);
                return;
        }
    }
}
