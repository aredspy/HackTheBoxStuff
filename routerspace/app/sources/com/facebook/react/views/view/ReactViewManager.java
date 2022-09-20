package com.facebook.react.views.view;

import android.graphics.Rect;
import android.view.View;
import androidx.core.view.ViewCompat;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.PointerEvents;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.annotations.ReactPropGroup;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.yoga.YogaConstants;
import java.util.Locale;
import java.util.Map;
@ReactModule(name = "RCTView")
/* loaded from: classes.dex */
public class ReactViewManager extends ReactClippingViewManager<ReactViewGroup> {
    private static final int CMD_HOTSPOT_UPDATE = 1;
    private static final int CMD_SET_PRESSED = 2;
    private static final String HOTSPOT_UPDATE_KEY = "hotspotUpdate";
    public static final String REACT_CLASS = "RCTView";
    private static final int[] SPACING_TYPES = {8, 0, 2, 1, 3, 4, 5};

    @Override // com.facebook.react.uimanager.ViewManager, com.facebook.react.bridge.NativeModule
    public String getName() {
        return "RCTView";
    }

    @ReactProp(name = ViewProps.COLLAPSABLE)
    public void setCollapsable(ReactViewGroup view, boolean collapsable) {
    }

    @ReactProp(name = "accessible")
    public void setAccessible(ReactViewGroup view, boolean accessible) {
        view.setFocusable(accessible);
    }

    @ReactProp(name = "hasTVPreferredFocus")
    public void setTVPreferredFocus(ReactViewGroup view, boolean hasTVPreferredFocus) {
        if (hasTVPreferredFocus) {
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.requestFocus();
        }
    }

    @ReactProp(defaultInt = -1, name = "nextFocusDown")
    public void nextFocusDown(ReactViewGroup view, int viewId) {
        view.setNextFocusDownId(viewId);
    }

    @ReactProp(defaultInt = -1, name = "nextFocusForward")
    public void nextFocusForward(ReactViewGroup view, int viewId) {
        view.setNextFocusForwardId(viewId);
    }

    @ReactProp(defaultInt = -1, name = "nextFocusLeft")
    public void nextFocusLeft(ReactViewGroup view, int viewId) {
        view.setNextFocusLeftId(viewId);
    }

    @ReactProp(defaultInt = -1, name = "nextFocusRight")
    public void nextFocusRight(ReactViewGroup view, int viewId) {
        view.setNextFocusRightId(viewId);
    }

    @ReactProp(defaultInt = -1, name = "nextFocusUp")
    public void nextFocusUp(ReactViewGroup view, int viewId) {
        view.setNextFocusUpId(viewId);
    }

    @ReactPropGroup(defaultFloat = Float.NaN, names = {ViewProps.BORDER_RADIUS, ViewProps.BORDER_TOP_LEFT_RADIUS, ViewProps.BORDER_TOP_RIGHT_RADIUS, ViewProps.BORDER_BOTTOM_RIGHT_RADIUS, ViewProps.BORDER_BOTTOM_LEFT_RADIUS, ViewProps.BORDER_TOP_START_RADIUS, ViewProps.BORDER_TOP_END_RADIUS, ViewProps.BORDER_BOTTOM_START_RADIUS, ViewProps.BORDER_BOTTOM_END_RADIUS})
    public void setBorderRadius(ReactViewGroup view, int index, float borderRadius) {
        if (!YogaConstants.isUndefined(borderRadius) && borderRadius < 0.0f) {
            borderRadius = Float.NaN;
        }
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
    public void setBorderStyle(ReactViewGroup view, String borderStyle) {
        view.setBorderStyle(borderStyle);
    }

    @ReactProp(name = "hitSlop")
    public void setHitSlop(final ReactViewGroup view, ReadableMap hitSlop) {
        if (hitSlop == null) {
            view.setHitSlopRect(null);
            return;
        }
        int i = 0;
        int pixelFromDIP = hitSlop.hasKey(ViewProps.LEFT) ? (int) PixelUtil.toPixelFromDIP(hitSlop.getDouble(ViewProps.LEFT)) : 0;
        int pixelFromDIP2 = hitSlop.hasKey(ViewProps.TOP) ? (int) PixelUtil.toPixelFromDIP(hitSlop.getDouble(ViewProps.TOP)) : 0;
        int pixelFromDIP3 = hitSlop.hasKey(ViewProps.RIGHT) ? (int) PixelUtil.toPixelFromDIP(hitSlop.getDouble(ViewProps.RIGHT)) : 0;
        if (hitSlop.hasKey(ViewProps.BOTTOM)) {
            i = (int) PixelUtil.toPixelFromDIP(hitSlop.getDouble(ViewProps.BOTTOM));
        }
        view.setHitSlopRect(new Rect(pixelFromDIP, pixelFromDIP2, pixelFromDIP3, i));
    }

    @ReactProp(name = ViewProps.POINTER_EVENTS)
    public void setPointerEvents(ReactViewGroup view, String pointerEventsStr) {
        if (pointerEventsStr == null) {
            view.setPointerEvents(PointerEvents.AUTO);
        } else {
            view.setPointerEvents(PointerEvents.valueOf(pointerEventsStr.toUpperCase(Locale.US).replace("-", "_")));
        }
    }

    @ReactProp(name = "nativeBackgroundAndroid")
    public void setNativeBackground(ReactViewGroup view, ReadableMap bg) {
        view.setTranslucentBackgroundDrawable(bg == null ? null : ReactDrawableHelper.createDrawableFromJSDescription(view.getContext(), bg));
    }

    @ReactProp(name = "nativeForegroundAndroid")
    public void setNativeForeground(ReactViewGroup view, ReadableMap fg) {
        view.setForeground(fg == null ? null : ReactDrawableHelper.createDrawableFromJSDescription(view.getContext(), fg));
    }

    @ReactProp(name = ViewProps.NEEDS_OFFSCREEN_ALPHA_COMPOSITING)
    public void setNeedsOffscreenAlphaCompositing(ReactViewGroup view, boolean needsOffscreenAlphaCompositing) {
        view.setNeedsOffscreenAlphaCompositing(needsOffscreenAlphaCompositing);
    }

    @ReactPropGroup(defaultFloat = Float.NaN, names = {ViewProps.BORDER_WIDTH, ViewProps.BORDER_LEFT_WIDTH, ViewProps.BORDER_RIGHT_WIDTH, ViewProps.BORDER_TOP_WIDTH, ViewProps.BORDER_BOTTOM_WIDTH, ViewProps.BORDER_START_WIDTH, ViewProps.BORDER_END_WIDTH})
    public void setBorderWidth(ReactViewGroup view, int index, float width) {
        if (!YogaConstants.isUndefined(width) && width < 0.0f) {
            width = Float.NaN;
        }
        if (!YogaConstants.isUndefined(width)) {
            width = PixelUtil.toPixelFromDIP(width);
        }
        view.setBorderWidth(SPACING_TYPES[index], width);
    }

    @ReactPropGroup(customType = "Color", names = {ViewProps.BORDER_COLOR, ViewProps.BORDER_LEFT_COLOR, ViewProps.BORDER_RIGHT_COLOR, ViewProps.BORDER_TOP_COLOR, ViewProps.BORDER_BOTTOM_COLOR, ViewProps.BORDER_START_COLOR, ViewProps.BORDER_END_COLOR})
    public void setBorderColor(ReactViewGroup view, int index, Integer color) {
        float f = Float.NaN;
        float intValue = color == null ? Float.NaN : color.intValue() & ViewCompat.MEASURED_SIZE_MASK;
        if (color != null) {
            f = color.intValue() >>> 24;
        }
        view.setBorderColor(SPACING_TYPES[index], intValue, f);
    }

    @ReactProp(name = "focusable")
    public void setFocusable(final ReactViewGroup view, boolean focusable) {
        if (focusable) {
            view.setOnClickListener(new View.OnClickListener() { // from class: com.facebook.react.views.view.ReactViewManager.1
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    EventDispatcher eventDispatcherForReactTag = UIManagerHelper.getEventDispatcherForReactTag((ReactContext) view.getContext(), view.getId());
                    if (eventDispatcherForReactTag == null) {
                        return;
                    }
                    eventDispatcherForReactTag.dispatchEvent(new ViewGroupClickEvent(UIManagerHelper.getSurfaceId(view.getContext()), view.getId()));
                }
            });
            view.setFocusable(true);
            return;
        }
        view.setOnClickListener(null);
        view.setClickable(false);
    }

    @ReactProp(name = ViewProps.OVERFLOW)
    public void setOverflow(ReactViewGroup view, String overflow) {
        view.setOverflow(overflow);
    }

    @ReactProp(name = "backfaceVisibility")
    public void setBackfaceVisibility(ReactViewGroup view, String backfaceVisibility) {
        view.setBackfaceVisibility(backfaceVisibility);
    }

    public void setOpacity(ReactViewGroup view, float opacity) {
        view.setOpacityIfPossible(opacity);
    }

    public void setTransform(ReactViewGroup view, ReadableArray matrix) {
        super.setTransform((ReactViewManager) view, matrix);
        view.setBackfaceVisibilityDependantOpacity();
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public ReactViewGroup createViewInstance(ThemedReactContext context) {
        return new ReactViewGroup(context);
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public Map<String, Integer> getCommandsMap() {
        return MapBuilder.of(HOTSPOT_UPDATE_KEY, 1, "setPressed", 2);
    }

    public void receiveCommand(ReactViewGroup root, int commandId, ReadableArray args) {
        if (commandId == 1) {
            handleHotspotUpdate(root, args);
        } else if (commandId != 2) {
        } else {
            handleSetPressed(root, args);
        }
    }

    public void receiveCommand(ReactViewGroup root, String commandId, ReadableArray args) {
        commandId.hashCode();
        if (commandId.equals("setPressed")) {
            handleSetPressed(root, args);
        } else if (!commandId.equals(HOTSPOT_UPDATE_KEY)) {
        } else {
            handleHotspotUpdate(root, args);
        }
    }

    private void handleSetPressed(ReactViewGroup root, ReadableArray args) {
        if (args == null || args.size() != 1) {
            throw new JSApplicationIllegalArgumentException("Illegal number of arguments for 'setPressed' command");
        }
        root.setPressed(args.getBoolean(0));
    }

    private void handleHotspotUpdate(ReactViewGroup root, ReadableArray args) {
        if (args == null || args.size() != 2) {
            throw new JSApplicationIllegalArgumentException("Illegal number of arguments for 'updateHotspot' command");
        }
        root.drawableHotspotChanged(PixelUtil.toPixelFromDIP(args.getDouble(0)), PixelUtil.toPixelFromDIP(args.getDouble(1)));
    }
}
