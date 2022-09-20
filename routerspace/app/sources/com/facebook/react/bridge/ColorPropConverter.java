package com.facebook.react.bridge;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import androidx.core.content.res.ResourcesCompat;
/* loaded from: classes.dex */
public class ColorPropConverter {
    private static final String ATTR = "attr";
    private static final String ATTR_SEGMENT = "attr/";
    private static final String JSON_KEY = "resource_paths";
    private static final String PACKAGE_DELIMITER = ":";
    private static final String PATH_DELIMITER = "/";
    private static final String PREFIX_ATTR = "?";
    private static final String PREFIX_RESOURCE = "@";

    public static Integer getColor(Object value, Context context) {
        if (value == null) {
            return null;
        }
        if (value instanceof Double) {
            return Integer.valueOf(((Double) value).intValue());
        }
        if (context == null) {
            throw new RuntimeException("Context may not be null.");
        }
        if (value instanceof ReadableMap) {
            ReadableArray array = ((ReadableMap) value).getArray(JSON_KEY);
            if (array == null) {
                throw new JSApplicationCausedNativeException("ColorValue: The `resource_paths` must be an array of color resource path strings.");
            }
            for (int i = 0; i < array.size(); i++) {
                String string = array.getString(i);
                if (string != null && !string.isEmpty()) {
                    boolean startsWith = string.startsWith(PREFIX_RESOURCE);
                    boolean startsWith2 = string.startsWith(PREFIX_ATTR);
                    String substring = string.substring(1);
                    try {
                        if (startsWith) {
                            return Integer.valueOf(resolveResource(context, substring));
                        }
                        if (startsWith2) {
                            return Integer.valueOf(resolveThemeAttribute(context, substring));
                        }
                    } catch (Resources.NotFoundException unused) {
                        continue;
                    }
                }
            }
            throw new JSApplicationCausedNativeException("ColorValue: None of the paths in the `resource_paths` array resolved to a color resource.");
        }
        throw new JSApplicationCausedNativeException("ColorValue: the value must be a number or Object.");
    }

    private static int resolveResource(Context context, String resourcePath) {
        String[] split = resourcePath.split(PACKAGE_DELIMITER);
        String packageName = context.getPackageName();
        if (split.length > 1) {
            packageName = split[0];
            resourcePath = split[1];
        }
        String[] split2 = resourcePath.split(PATH_DELIMITER);
        String str = split2[0];
        return ResourcesCompat.getColor(context.getResources(), context.getResources().getIdentifier(split2[1], str, packageName), context.getTheme());
    }

    private static int resolveThemeAttribute(Context context, String resourcePath) {
        String replaceAll = resourcePath.replaceAll(ATTR_SEGMENT, "");
        String[] split = replaceAll.split(PACKAGE_DELIMITER);
        String packageName = context.getPackageName();
        if (split.length > 1) {
            packageName = split[0];
            replaceAll = split[1];
        }
        int identifier = context.getResources().getIdentifier(replaceAll, ATTR, packageName);
        TypedValue typedValue = new TypedValue();
        if (context.getTheme().resolveAttribute(identifier, typedValue, true)) {
            return typedValue.data;
        }
        throw new Resources.NotFoundException();
    }
}
