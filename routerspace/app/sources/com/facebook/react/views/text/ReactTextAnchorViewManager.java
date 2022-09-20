package com.facebook.react.views.text;

import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import androidx.autofill.HintConstants;
import androidx.core.app.NotificationCompat;
import androidx.core.view.ViewCompat;
import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.uimanager.BaseViewManager;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.annotations.ReactPropGroup;
import com.facebook.react.views.text.ReactBaseTextShadowNode;
import com.facebook.yoga.YogaConstants;
/* loaded from: classes.dex */
public abstract class ReactTextAnchorViewManager<T extends View, C extends ReactBaseTextShadowNode> extends BaseViewManager<T, C> {
    private static final int[] SPACING_TYPES = {8, 0, 2, 1, 3};
    private static final String TAG = "ReactTextAnchorViewManager";

    @ReactProp(defaultInt = Integer.MAX_VALUE, name = ViewProps.NUMBER_OF_LINES)
    public void setNumberOfLines(ReactTextView view, int numberOfLines) {
        view.setNumberOfLines(numberOfLines);
    }

    @ReactProp(name = ViewProps.ELLIPSIZE_MODE)
    public void setEllipsizeMode(ReactTextView view, String ellipsizeMode) {
        if (ellipsizeMode == null || ellipsizeMode.equals("tail")) {
            view.setEllipsizeLocation(TextUtils.TruncateAt.END);
        } else if (ellipsizeMode.equals("head")) {
            view.setEllipsizeLocation(TextUtils.TruncateAt.START);
        } else if (ellipsizeMode.equals("middle")) {
            view.setEllipsizeLocation(TextUtils.TruncateAt.MIDDLE);
        } else if (ellipsizeMode.equals("clip")) {
            view.setEllipsizeLocation(null);
        } else {
            throw new JSApplicationIllegalArgumentException("Invalid ellipsizeMode: " + ellipsizeMode);
        }
    }

    @ReactProp(name = ViewProps.ADJUSTS_FONT_SIZE_TO_FIT)
    public void setAdjustFontSizeToFit(ReactTextView view, boolean adjustsFontSizeToFit) {
        view.setAdjustFontSizeToFit(adjustsFontSizeToFit);
    }

    @ReactProp(name = ViewProps.TEXT_ALIGN_VERTICAL)
    public void setTextAlignVertical(ReactTextView view, String textAlignVertical) {
        if (textAlignVertical == null || "auto".equals(textAlignVertical)) {
            view.setGravityVertical(0);
        } else if (ViewProps.TOP.equals(textAlignVertical)) {
            view.setGravityVertical(48);
        } else if (ViewProps.BOTTOM.equals(textAlignVertical)) {
            view.setGravityVertical(80);
        } else if ("center".equals(textAlignVertical)) {
            view.setGravityVertical(16);
        } else {
            throw new JSApplicationIllegalArgumentException("Invalid textAlignVertical: " + textAlignVertical);
        }
    }

    @ReactProp(name = "selectable")
    public void setSelectable(ReactTextView view, boolean isSelectable) {
        view.setTextIsSelectable(isSelectable);
    }

    @ReactProp(customType = "Color", name = "selectionColor")
    public void setSelectionColor(ReactTextView view, Integer color) {
        if (color == null) {
            view.setHighlightColor(DefaultStyleValuesUtil.getDefaultTextColorHighlight(view.getContext()));
        } else {
            view.setHighlightColor(color.intValue());
        }
    }

    @ReactProp(name = "android_hyphenationFrequency")
    public void setAndroidHyphenationFrequency(ReactTextView view, String frequency) {
        if (Build.VERSION.SDK_INT < 23) {
            FLog.w(TAG, "android_hyphenationFrequency only available since android 23");
        } else if (frequency == null || frequency.equals(ViewProps.NONE)) {
            view.setHyphenationFrequency(0);
        } else if (frequency.equals("full")) {
            view.setHyphenationFrequency(2);
        } else if (frequency.equals("balanced")) {
            view.setHyphenationFrequency(2);
        } else if (frequency.equals("high")) {
            view.setHyphenationFrequency(1);
        } else if (frequency.equals("normal")) {
            view.setHyphenationFrequency(1);
        } else {
            throw new JSApplicationIllegalArgumentException("Invalid android_hyphenationFrequency: " + frequency);
        }
    }

    @ReactPropGroup(defaultFloat = Float.NaN, names = {ViewProps.BORDER_RADIUS, ViewProps.BORDER_TOP_LEFT_RADIUS, ViewProps.BORDER_TOP_RIGHT_RADIUS, ViewProps.BORDER_BOTTOM_RIGHT_RADIUS, ViewProps.BORDER_BOTTOM_LEFT_RADIUS})
    public void setBorderRadius(ReactTextView view, int index, float borderRadius) {
        if (!YogaConstants.isUndefined(borderRadius)) {
            borderRadius = PixelUtil.toPixelFromDIP(borderRadius);
        }
        if (index == 0) {
            view.setBorderRadius(borderRadius);
        } else {
            view.setBorderRadius(borderRadius, index - 1);
        }
    }

    @ReactProp(name = "borderStyle")
    public void setBorderStyle(ReactTextView view, String borderStyle) {
        view.setBorderStyle(borderStyle);
    }

    @ReactPropGroup(defaultFloat = Float.NaN, names = {ViewProps.BORDER_WIDTH, ViewProps.BORDER_LEFT_WIDTH, ViewProps.BORDER_RIGHT_WIDTH, ViewProps.BORDER_TOP_WIDTH, ViewProps.BORDER_BOTTOM_WIDTH})
    public void setBorderWidth(ReactTextView view, int index, float width) {
        if (!YogaConstants.isUndefined(width)) {
            width = PixelUtil.toPixelFromDIP(width);
        }
        view.setBorderWidth(SPACING_TYPES[index], width);
    }

    @ReactPropGroup(customType = "Color", names = {ViewProps.BORDER_COLOR, ViewProps.BORDER_LEFT_COLOR, ViewProps.BORDER_RIGHT_COLOR, ViewProps.BORDER_TOP_COLOR, ViewProps.BORDER_BOTTOM_COLOR})
    public void setBorderColor(ReactTextView view, int index, Integer color) {
        float f = Float.NaN;
        float intValue = color == null ? Float.NaN : color.intValue() & ViewCompat.MEASURED_SIZE_MASK;
        if (color != null) {
            f = color.intValue() >>> 24;
        }
        view.setBorderColor(SPACING_TYPES[index], intValue, f);
    }

    @ReactProp(defaultBoolean = true, name = ViewProps.INCLUDE_FONT_PADDING)
    public void setIncludeFontPadding(ReactTextView view, boolean includepad) {
        view.setIncludeFontPadding(includepad);
    }

    @ReactProp(defaultBoolean = false, name = "disabled")
    public void setDisabled(ReactTextView view, boolean disabled) {
        view.setEnabled(!disabled);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @ReactProp(name = "dataDetectorType")
    public void setDataDetectorType(ReactTextView view, String type) {
        char c;
        switch (type.hashCode()) {
            case -1192969641:
                if (type.equals(HintConstants.AUTOFILL_HINT_PHONE_NUMBER)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 96673:
                if (type.equals("all")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 3321850:
                if (type.equals("link")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 3387192:
                if (type.equals(ViewProps.NONE)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 96619420:
                if (type.equals(NotificationCompat.CATEGORY_EMAIL)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        if (c == 0) {
            view.setLinkifyMask(4);
        } else if (c == 1) {
            view.setLinkifyMask(1);
        } else if (c == 2) {
            view.setLinkifyMask(2);
        } else if (c == 3) {
            view.setLinkifyMask(15);
        } else {
            view.setLinkifyMask(0);
        }
    }

    @ReactProp(name = "onInlineViewLayout")
    public void setNotifyOnInlineViewLayout(ReactTextView view, boolean notifyOnInlineViewLayout) {
        view.setNotifyOnInlineViewLayout(notifyOnInlineViewLayout);
    }
}
