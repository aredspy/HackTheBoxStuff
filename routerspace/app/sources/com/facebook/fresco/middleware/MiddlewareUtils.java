package com.facebook.fresco.middleware;

import android.graphics.PointF;
import android.graphics.Rect;
import android.net.Uri;
import com.facebook.fresco.ui.common.ControllerListener2;
import java.util.Map;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class MiddlewareUtils {
    public static ControllerListener2.Extras obtainExtras(Map<String, Object> componentAttribution, Map<String, Object> shortcutAttribution, @Nullable Map<String, Object> dataSourceExtras, @Nullable Rect viewportDimensions, @Nullable String scaleType, @Nullable PointF focusPoint, @Nullable Map<String, Object> imageExtras, @Nullable Object callerContext, @Nullable Uri mainUri) {
        ControllerListener2.Extras extras = new ControllerListener2.Extras();
        if (viewportDimensions != null) {
            extras.viewportWidth = viewportDimensions.width();
            extras.viewportHeight = viewportDimensions.height();
        }
        extras.scaleType = scaleType;
        if (focusPoint != null) {
            extras.focusX = focusPoint.x;
            extras.focusY = focusPoint.y;
        }
        extras.callerContext = callerContext;
        extras.mainUri = mainUri;
        extras.datasourceExtras = dataSourceExtras;
        extras.imageExtras = imageExtras;
        extras.shortcutExtras = shortcutAttribution;
        extras.componentExtras = componentAttribution;
        return extras;
    }
}
