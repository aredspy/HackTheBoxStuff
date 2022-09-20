package com.facebook.react.uimanager;

import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewParent;
import androidx.core.view.ViewCompat;
import com.facebook.common.logging.FLog;
import com.facebook.react.R;
import com.facebook.react.bridge.Dynamic;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.uimanager.LayoutShadowNode;
import com.facebook.react.uimanager.MatrixMathHelper;
import com.facebook.react.uimanager.ReactAccessibilityDelegate;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.util.ReactFindViewUtil;
import com.google.android.material.color.MaterialColors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes.dex */
public abstract class BaseViewManager<T extends View, C extends LayoutShadowNode> extends ViewManager<T, C> implements BaseViewManagerInterface<T> {
    private static final int PERSPECTIVE_ARRAY_INVERTED_CAMERA_DISTANCE_INDEX = 2;
    private static final String STATE_BUSY = "busy";
    private static final String STATE_CHECKED = "checked";
    private static final String STATE_EXPANDED = "expanded";
    private static final String STATE_MIXED = "mixed";
    public static final Map<String, Integer> sStateDescription;
    private static final float CAMERA_DISTANCE_NORMALIZATION_MULTIPLIER = (float) Math.sqrt(5.0d);
    private static MatrixMathHelper.MatrixDecompositionContext sMatrixDecompositionContext = new MatrixMathHelper.MatrixDecompositionContext();
    private static double[] sTransformDecompositionArray = new double[16];

    static {
        HashMap hashMap = new HashMap();
        sStateDescription = hashMap;
        hashMap.put(STATE_BUSY, Integer.valueOf(R.string.state_busy_description));
        hashMap.put(STATE_EXPANDED, Integer.valueOf(R.string.state_expanded_description));
        hashMap.put("collapsed", Integer.valueOf(R.string.state_collapsed_description));
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    @ReactProp(customType = "Color", defaultInt = 0, name = ViewProps.BACKGROUND_COLOR)
    public void setBackgroundColor(T view, int backgroundColor) {
        view.setBackgroundColor(backgroundColor);
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    @ReactProp(name = ViewProps.TRANSFORM)
    public void setTransform(T view, ReadableArray matrix) {
        if (matrix == null) {
            resetTransformProperty(view);
        } else {
            setTransformProperty(view, matrix);
        }
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    @ReactProp(defaultFloat = MaterialColors.ALPHA_FULL, name = ViewProps.OPACITY)
    public void setOpacity(T view, float opacity) {
        view.setAlpha(opacity);
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    @ReactProp(name = ViewProps.ELEVATION)
    public void setElevation(T view, float elevation) {
        ViewCompat.setElevation(view, PixelUtil.toPixelFromDIP(elevation));
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    @ReactProp(customType = "Color", defaultInt = ViewCompat.MEASURED_STATE_MASK, name = ViewProps.SHADOW_COLOR)
    public void setShadowColor(T view, int shadowColor) {
        if (Build.VERSION.SDK_INT >= 28) {
            view.setOutlineAmbientShadowColor(shadowColor);
            view.setOutlineSpotShadowColor(shadowColor);
        }
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    @ReactProp(name = ViewProps.Z_INDEX)
    public void setZIndex(T view, float zIndex) {
        ViewGroupManager.setViewZIndex(view, Math.round(zIndex));
        ViewParent parent = view.getParent();
        if (parent instanceof ReactZIndexedViewGroup) {
            ((ReactZIndexedViewGroup) parent).updateDrawingOrder();
        }
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    @ReactProp(name = ViewProps.RENDER_TO_HARDWARE_TEXTURE)
    public void setRenderToHardwareTexture(T view, boolean useHWTexture) {
        view.setLayerType(useHWTexture ? 2 : 0, null);
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    @ReactProp(name = ViewProps.TEST_ID)
    public void setTestId(T view, String testId) {
        view.setTag(R.id.react_test_id, testId);
        view.setTag(testId);
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    @ReactProp(name = ViewProps.NATIVE_ID)
    public void setNativeId(T view, String nativeId) {
        view.setTag(R.id.view_tag_native_id, nativeId);
        ReactFindViewUtil.notifyViewRendered(view);
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    @ReactProp(name = ViewProps.ACCESSIBILITY_LABEL)
    public void setAccessibilityLabel(T view, String accessibilityLabel) {
        view.setTag(R.id.accessibility_label, accessibilityLabel);
        updateViewContentDescription(view);
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    @ReactProp(name = ViewProps.ACCESSIBILITY_HINT)
    public void setAccessibilityHint(T view, String accessibilityHint) {
        view.setTag(R.id.accessibility_hint, accessibilityHint);
        updateViewContentDescription(view);
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    @ReactProp(name = ViewProps.ACCESSIBILITY_ROLE)
    public void setAccessibilityRole(T view, String accessibilityRole) {
        if (accessibilityRole == null) {
            return;
        }
        view.setTag(R.id.accessibility_role, ReactAccessibilityDelegate.AccessibilityRole.fromValue(accessibilityRole));
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    @ReactProp(name = ViewProps.ACCESSIBILITY_STATE)
    public void setViewState(T view, ReadableMap accessibilityState) {
        if (accessibilityState == null) {
            return;
        }
        if (accessibilityState.hasKey("selected")) {
            boolean isSelected = view.isSelected();
            boolean z = accessibilityState.getBoolean("selected");
            view.setSelected(z);
            if (view.isAccessibilityFocused() && isSelected && !z) {
                view.announceForAccessibility(view.getContext().getString(R.string.state_unselected_description));
            }
        } else {
            view.setSelected(false);
        }
        view.setTag(R.id.accessibility_state, accessibilityState);
        view.setEnabled(true);
        ReadableMapKeySetIterator keySetIterator = accessibilityState.keySetIterator();
        while (keySetIterator.hasNextKey()) {
            String nextKey = keySetIterator.nextKey();
            if (nextKey.equals(STATE_BUSY) || nextKey.equals(STATE_EXPANDED) || (nextKey.equals(STATE_CHECKED) && accessibilityState.getType(STATE_CHECKED) == ReadableType.String)) {
                updateViewContentDescription(view);
                return;
            } else if (view.isAccessibilityFocused()) {
                view.sendAccessibilityEvent(1);
            }
        }
    }

    private void updateViewContentDescription(T view) {
        Dynamic dynamic;
        String str = (String) view.getTag(R.id.accessibility_label);
        ReadableMap readableMap = (ReadableMap) view.getTag(R.id.accessibility_state);
        String str2 = (String) view.getTag(R.id.accessibility_hint);
        ArrayList arrayList = new ArrayList();
        ReadableMap readableMap2 = (ReadableMap) view.getTag(R.id.accessibility_value);
        if (str != null) {
            arrayList.add(str);
        }
        if (readableMap != null) {
            ReadableMapKeySetIterator keySetIterator = readableMap.keySetIterator();
            while (keySetIterator.hasNextKey()) {
                String nextKey = keySetIterator.nextKey();
                Dynamic dynamic2 = readableMap.getDynamic(nextKey);
                if (nextKey.equals(STATE_CHECKED) && dynamic2.getType() == ReadableType.String && dynamic2.asString().equals(STATE_MIXED)) {
                    arrayList.add(view.getContext().getString(R.string.state_mixed_description));
                } else if (nextKey.equals(STATE_BUSY) && dynamic2.getType() == ReadableType.Boolean && dynamic2.asBoolean()) {
                    arrayList.add(view.getContext().getString(R.string.state_busy_description));
                } else if (nextKey.equals(STATE_EXPANDED) && dynamic2.getType() == ReadableType.Boolean) {
                    arrayList.add(view.getContext().getString(dynamic2.asBoolean() ? R.string.state_expanded_description : R.string.state_collapsed_description));
                }
            }
        }
        if (readableMap2 != null && readableMap2.hasKey("text") && (dynamic = readableMap2.getDynamic("text")) != null && dynamic.getType() == ReadableType.String) {
            arrayList.add(dynamic.asString());
        }
        if (str2 != null) {
            arrayList.add(str2);
        }
        if (arrayList.size() > 0) {
            view.setContentDescription(TextUtils.join(", ", arrayList));
        }
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    @ReactProp(name = ViewProps.ACCESSIBILITY_ACTIONS)
    public void setAccessibilityActions(T view, ReadableArray accessibilityActions) {
        if (accessibilityActions == null) {
            return;
        }
        view.setTag(R.id.accessibility_actions, accessibilityActions);
    }

    @ReactProp(name = ViewProps.ACCESSIBILITY_VALUE)
    public void setAccessibilityValue(T view, ReadableMap accessibilityValue) {
        if (accessibilityValue == null) {
            return;
        }
        view.setTag(R.id.accessibility_value, accessibilityValue);
        if (!accessibilityValue.hasKey("text")) {
            return;
        }
        updateViewContentDescription(view);
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    @ReactProp(name = ViewProps.IMPORTANT_FOR_ACCESSIBILITY)
    public void setImportantForAccessibility(T view, String importantForAccessibility) {
        if (importantForAccessibility == null || importantForAccessibility.equals("auto")) {
            ViewCompat.setImportantForAccessibility(view, 0);
        } else if (importantForAccessibility.equals("yes")) {
            ViewCompat.setImportantForAccessibility(view, 1);
        } else if (importantForAccessibility.equals("no")) {
            ViewCompat.setImportantForAccessibility(view, 2);
        } else if (!importantForAccessibility.equals("no-hide-descendants")) {
        } else {
            ViewCompat.setImportantForAccessibility(view, 4);
        }
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    @ReactProp(name = ViewProps.ROTATION)
    @Deprecated
    public void setRotation(T view, float rotation) {
        view.setRotation(rotation);
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    @ReactProp(defaultFloat = MaterialColors.ALPHA_FULL, name = ViewProps.SCALE_X)
    @Deprecated
    public void setScaleX(T view, float scaleX) {
        view.setScaleX(scaleX);
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    @ReactProp(defaultFloat = MaterialColors.ALPHA_FULL, name = ViewProps.SCALE_Y)
    @Deprecated
    public void setScaleY(T view, float scaleY) {
        view.setScaleY(scaleY);
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    @ReactProp(defaultFloat = 0.0f, name = ViewProps.TRANSLATE_X)
    @Deprecated
    public void setTranslateX(T view, float translateX) {
        view.setTranslationX(PixelUtil.toPixelFromDIP(translateX));
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    @ReactProp(defaultFloat = 0.0f, name = ViewProps.TRANSLATE_Y)
    @Deprecated
    public void setTranslateY(T view, float translateY) {
        view.setTranslationY(PixelUtil.toPixelFromDIP(translateY));
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    @ReactProp(name = ViewProps.ACCESSIBILITY_LIVE_REGION)
    public void setAccessibilityLiveRegion(T view, String liveRegion) {
        if (liveRegion == null || liveRegion.equals(ViewProps.NONE)) {
            ViewCompat.setAccessibilityLiveRegion(view, 0);
        } else if (liveRegion.equals("polite")) {
            ViewCompat.setAccessibilityLiveRegion(view, 1);
        } else if (!liveRegion.equals("assertive")) {
        } else {
            ViewCompat.setAccessibilityLiveRegion(view, 2);
        }
    }

    private static void setTransformProperty(View view, ReadableArray transforms) {
        sMatrixDecompositionContext.reset();
        TransformHelper.processTransform(transforms, sTransformDecompositionArray);
        MatrixMathHelper.decomposeMatrix(sTransformDecompositionArray, sMatrixDecompositionContext);
        view.setTranslationX(PixelUtil.toPixelFromDIP(sanitizeFloatPropertyValue((float) sMatrixDecompositionContext.translation[0])));
        view.setTranslationY(PixelUtil.toPixelFromDIP(sanitizeFloatPropertyValue((float) sMatrixDecompositionContext.translation[1])));
        view.setRotation(sanitizeFloatPropertyValue((float) sMatrixDecompositionContext.rotationDegrees[2]));
        view.setRotationX(sanitizeFloatPropertyValue((float) sMatrixDecompositionContext.rotationDegrees[0]));
        view.setRotationY(sanitizeFloatPropertyValue((float) sMatrixDecompositionContext.rotationDegrees[1]));
        view.setScaleX(sanitizeFloatPropertyValue((float) sMatrixDecompositionContext.scale[0]));
        view.setScaleY(sanitizeFloatPropertyValue((float) sMatrixDecompositionContext.scale[1]));
        double[] dArr = sMatrixDecompositionContext.perspective;
        if (dArr.length > 2) {
            float f = (float) dArr[2];
            if (f == 0.0f) {
                f = 7.8125E-4f;
            }
            float f2 = (-1.0f) / f;
            float f3 = DisplayMetricsHolder.getScreenDisplayMetrics().density;
            view.setCameraDistance(sanitizeFloatPropertyValue(f3 * f3 * f2 * CAMERA_DISTANCE_NORMALIZATION_MULTIPLIER));
        }
    }

    private static float sanitizeFloatPropertyValue(float value) {
        if (value < -3.4028235E38f || value > Float.MAX_VALUE) {
            if (value < -3.4028235E38f || value == Float.NEGATIVE_INFINITY) {
                return -3.4028235E38f;
            }
            if (value > Float.MAX_VALUE || value == Float.POSITIVE_INFINITY) {
                return Float.MAX_VALUE;
            }
            if (Float.isNaN(value)) {
                return 0.0f;
            }
            throw new IllegalStateException("Invalid float property value: " + value);
        }
        return value;
    }

    private static void resetTransformProperty(View view) {
        view.setTranslationX(PixelUtil.toPixelFromDIP(0.0f));
        view.setTranslationY(PixelUtil.toPixelFromDIP(0.0f));
        view.setRotation(0.0f);
        view.setRotationX(0.0f);
        view.setRotationY(0.0f);
        view.setScaleX(1.0f);
        view.setScaleY(1.0f);
        view.setCameraDistance(0.0f);
    }

    private void updateViewAccessibility(T view) {
        ReactAccessibilityDelegate.setDelegate(view);
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public void onAfterUpdateTransaction(T view) {
        super.onAfterUpdateTransaction(view);
        updateViewAccessibility(view);
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.builder().put(ReactAccessibilityDelegate.TOP_ACCESSIBILITY_ACTION_EVENT, MapBuilder.of("registrationName", "onAccessibilityAction")).build();
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    public void setBorderRadius(T view, float borderRadius) {
        logUnsupportedPropertyWarning(ViewProps.BORDER_RADIUS);
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    public void setBorderBottomLeftRadius(T view, float borderRadius) {
        logUnsupportedPropertyWarning(ViewProps.BORDER_BOTTOM_LEFT_RADIUS);
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    public void setBorderBottomRightRadius(T view, float borderRadius) {
        logUnsupportedPropertyWarning(ViewProps.BORDER_BOTTOM_RIGHT_RADIUS);
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    public void setBorderTopLeftRadius(T view, float borderRadius) {
        logUnsupportedPropertyWarning(ViewProps.BORDER_TOP_LEFT_RADIUS);
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    public void setBorderTopRightRadius(T view, float borderRadius) {
        logUnsupportedPropertyWarning(ViewProps.BORDER_TOP_RIGHT_RADIUS);
    }

    private void logUnsupportedPropertyWarning(String propName) {
        FLog.w(ReactConstants.TAG, "%s doesn't support property '%s'", getName(), propName);
    }
}
