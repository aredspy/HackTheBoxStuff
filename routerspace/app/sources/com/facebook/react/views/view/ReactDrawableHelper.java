package com.facebook.react.views.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.util.TypedValue;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.SoftAssertions;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.ViewProps;
/* loaded from: classes.dex */
public class ReactDrawableHelper {
    private static final TypedValue sResolveOutValue = new TypedValue();

    public static Drawable createDrawableFromJSDescription(Context context, ReadableMap drawableDescriptionDict) {
        String string = drawableDescriptionDict.getString("type");
        if ("ThemeAttrAndroid".equals(string)) {
            String string2 = drawableDescriptionDict.getString("attribute");
            int attrId = getAttrId(context, string2);
            if (!context.getTheme().resolveAttribute(attrId, sResolveOutValue, true)) {
                throw new JSApplicationIllegalArgumentException("Attribute " + string2 + " with id " + attrId + " couldn't be resolved into a drawable");
            }
            return setRadius(drawableDescriptionDict, getDefaultThemeDrawable(context));
        } else if ("RippleAndroid".equals(string)) {
            return setRadius(drawableDescriptionDict, getRippleDrawable(context, drawableDescriptionDict));
        } else {
            throw new JSApplicationIllegalArgumentException("Invalid type for android drawable: " + string);
        }
    }

    private static int getAttrId(Context context, String attr) {
        SoftAssertions.assertNotNull(attr);
        if ("selectableItemBackground".equals(attr)) {
            return 16843534;
        }
        if (!"selectableItemBackgroundBorderless".equals(attr)) {
            return context.getResources().getIdentifier(attr, "attr", "android");
        }
        return 16843868;
    }

    private static Drawable getDefaultThemeDrawable(Context context) {
        return context.getResources().getDrawable(sResolveOutValue.resourceId, context.getTheme());
    }

    private static RippleDrawable getRippleDrawable(Context context, ReadableMap drawableDescriptionDict) {
        return new RippleDrawable(new ColorStateList(new int[][]{new int[0]}, new int[]{getColor(context, drawableDescriptionDict)}), null, getMask(drawableDescriptionDict));
    }

    private static Drawable setRadius(ReadableMap drawableDescriptionDict, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= 23 && drawableDescriptionDict.hasKey("rippleRadius") && (drawable instanceof RippleDrawable)) {
            ((RippleDrawable) drawable).setRadius((int) PixelUtil.toPixelFromDIP(drawableDescriptionDict.getDouble("rippleRadius")));
        }
        return drawable;
    }

    private static int getColor(Context context, ReadableMap drawableDescriptionDict) {
        if (drawableDescriptionDict.hasKey(ViewProps.COLOR) && !drawableDescriptionDict.isNull(ViewProps.COLOR)) {
            return drawableDescriptionDict.getInt(ViewProps.COLOR);
        }
        Resources.Theme theme = context.getTheme();
        TypedValue typedValue = sResolveOutValue;
        if (theme.resolveAttribute(16843820, typedValue, true)) {
            return context.getResources().getColor(typedValue.resourceId);
        }
        throw new JSApplicationIllegalArgumentException("Attribute colorControlHighlight couldn't be resolved into a drawable");
    }

    private static Drawable getMask(ReadableMap drawableDescriptionDict) {
        if (!drawableDescriptionDict.hasKey("borderless") || drawableDescriptionDict.isNull("borderless") || !drawableDescriptionDict.getBoolean("borderless")) {
            return new ColorDrawable(-1);
        }
        return null;
    }
}
