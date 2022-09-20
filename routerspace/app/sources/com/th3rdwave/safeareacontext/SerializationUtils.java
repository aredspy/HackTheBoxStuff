package com.th3rdwave.safeareacontext;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.ViewProps;
import java.util.Map;
/* loaded from: classes.dex */
class SerializationUtils {
    SerializationUtils() {
    }

    public static WritableMap edgeInsetsToJsMap(EdgeInsets insets) {
        WritableMap createMap = Arguments.createMap();
        createMap.putDouble(ViewProps.TOP, PixelUtil.toDIPFromPixel(insets.top));
        createMap.putDouble(ViewProps.RIGHT, PixelUtil.toDIPFromPixel(insets.right));
        createMap.putDouble(ViewProps.BOTTOM, PixelUtil.toDIPFromPixel(insets.bottom));
        createMap.putDouble(ViewProps.LEFT, PixelUtil.toDIPFromPixel(insets.left));
        return createMap;
    }

    public static Map<String, Float> edgeInsetsToJavaMap(EdgeInsets insets) {
        return MapBuilder.of(ViewProps.TOP, Float.valueOf(PixelUtil.toDIPFromPixel(insets.top)), ViewProps.RIGHT, Float.valueOf(PixelUtil.toDIPFromPixel(insets.right)), ViewProps.BOTTOM, Float.valueOf(PixelUtil.toDIPFromPixel(insets.bottom)), ViewProps.LEFT, Float.valueOf(PixelUtil.toDIPFromPixel(insets.left)));
    }

    public static WritableMap rectToJsMap(Rect rect) {
        WritableMap createMap = Arguments.createMap();
        createMap.putDouble("x", PixelUtil.toDIPFromPixel(rect.x));
        createMap.putDouble("y", PixelUtil.toDIPFromPixel(rect.y));
        createMap.putDouble("width", PixelUtil.toDIPFromPixel(rect.width));
        createMap.putDouble("height", PixelUtil.toDIPFromPixel(rect.height));
        return createMap;
    }

    public static Map<String, Float> rectToJavaMap(Rect rect) {
        return MapBuilder.of("x", Float.valueOf(PixelUtil.toDIPFromPixel(rect.x)), "y", Float.valueOf(PixelUtil.toDIPFromPixel(rect.y)), "width", Float.valueOf(PixelUtil.toDIPFromPixel(rect.width)), "height", Float.valueOf(PixelUtil.toDIPFromPixel(rect.height)));
    }
}
