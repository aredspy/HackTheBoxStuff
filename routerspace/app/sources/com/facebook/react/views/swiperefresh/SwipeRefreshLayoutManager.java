package com.facebook.react.views.swiperefresh;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.facebook.react.bridge.ColorPropConverter;
import com.facebook.react.bridge.Dynamic;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.ViewManagerDelegate;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.viewmanagers.AndroidSwipeRefreshLayoutManagerDelegate;
import com.facebook.react.viewmanagers.AndroidSwipeRefreshLayoutManagerInterface;
import java.util.Map;
@ReactModule(name = SwipeRefreshLayoutManager.REACT_CLASS)
/* loaded from: classes.dex */
public class SwipeRefreshLayoutManager extends ViewGroupManager<ReactSwipeRefreshLayout> implements AndroidSwipeRefreshLayoutManagerInterface<ReactSwipeRefreshLayout> {
    public static final String REACT_CLASS = "AndroidSwipeRefreshLayout";
    private final ViewManagerDelegate<ReactSwipeRefreshLayout> mDelegate = new AndroidSwipeRefreshLayoutManagerDelegate(this);

    @Override // com.facebook.react.uimanager.ViewManager, com.facebook.react.bridge.NativeModule
    public String getName() {
        return REACT_CLASS;
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public ReactSwipeRefreshLayout createViewInstance(ThemedReactContext reactContext) {
        return new ReactSwipeRefreshLayout(reactContext);
    }

    @ReactProp(defaultBoolean = true, name = ViewProps.ENABLED)
    public void setEnabled(ReactSwipeRefreshLayout view, boolean enabled) {
        view.setEnabled(enabled);
    }

    @ReactProp(customType = "ColorArray", name = "colors")
    public void setColors(ReactSwipeRefreshLayout view, ReadableArray colors) {
        if (colors != null) {
            int[] iArr = new int[colors.size()];
            for (int i = 0; i < colors.size(); i++) {
                if (colors.getType(i) == ReadableType.Map) {
                    iArr[i] = ColorPropConverter.getColor(colors.getMap(i), view.getContext()).intValue();
                } else {
                    iArr[i] = colors.getInt(i);
                }
            }
            view.setColorSchemeColors(iArr);
            return;
        }
        view.setColorSchemeColors(new int[0]);
    }

    @ReactProp(customType = "Color", name = "progressBackgroundColor")
    public void setProgressBackgroundColor(ReactSwipeRefreshLayout view, Integer color) {
        view.setProgressBackgroundColorSchemeColor(color == null ? 0 : color.intValue());
    }

    public void setSize(ReactSwipeRefreshLayout view, int value) {
        view.setSize(value);
    }

    public void setSize(ReactSwipeRefreshLayout view, String size) {
        if (size == null || size.equals("default")) {
            view.setSize(1);
        } else if (size.equals("large")) {
            view.setSize(0);
        } else {
            throw new IllegalArgumentException("Size must be 'default' or 'large', received: " + size);
        }
    }

    @ReactProp(name = "size")
    public void setSize(ReactSwipeRefreshLayout view, Dynamic size) {
        if (size.isNull()) {
            view.setSize(1);
        } else if (size.getType() == ReadableType.Number) {
            view.setSize(size.asInt());
        } else if (size.getType() == ReadableType.String) {
            setSize(view, size.asString());
        } else {
            throw new IllegalArgumentException("Size must be 'default' or 'large'");
        }
    }

    @ReactProp(name = "refreshing")
    public void setRefreshing(ReactSwipeRefreshLayout view, boolean refreshing) {
        view.setRefreshing(refreshing);
    }

    @ReactProp(defaultFloat = 0.0f, name = "progressViewOffset")
    public void setProgressViewOffset(final ReactSwipeRefreshLayout view, final float offset) {
        view.setProgressViewOffset(offset);
    }

    public void setNativeRefreshing(ReactSwipeRefreshLayout view, boolean value) {
        setRefreshing(view, value);
    }

    public void addEventEmitters(final ThemedReactContext reactContext, final ReactSwipeRefreshLayout view) {
        view.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() { // from class: com.facebook.react.views.swiperefresh.SwipeRefreshLayoutManager.1
            @Override // androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
            public void onRefresh() {
                EventDispatcher eventDispatcherForReactTag = UIManagerHelper.getEventDispatcherForReactTag(reactContext, view.getId());
                if (eventDispatcherForReactTag != null) {
                    eventDispatcherForReactTag.dispatchEvent(new RefreshEvent(UIManagerHelper.getSurfaceId(view), view.getId()));
                }
            }
        });
    }

    public void receiveCommand(ReactSwipeRefreshLayout root, String commandId, ReadableArray args) {
        commandId.hashCode();
        if (commandId.equals("setNativeRefreshing") && args != null) {
            setRefreshing(root, args.getBoolean(0));
        }
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public Map<String, Object> getExportedViewConstants() {
        return MapBuilder.of("SIZE", MapBuilder.of("DEFAULT", 1, "LARGE", 0));
    }

    @Override // com.facebook.react.uimanager.BaseViewManager, com.facebook.react.uimanager.ViewManager
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.builder().put("topRefresh", MapBuilder.of("registrationName", "onRefresh")).build();
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public ViewManagerDelegate<ReactSwipeRefreshLayout> getDelegate() {
        return this.mDelegate;
    }
}
