package com.facebook.react.views.drawer;

import android.view.View;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.facebook.react.bridge.Dynamic;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.ViewManagerDelegate;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.viewmanagers.AndroidDrawerLayoutManagerDelegate;
import com.facebook.react.viewmanagers.AndroidDrawerLayoutManagerInterface;
import com.facebook.react.views.drawer.events.DrawerClosedEvent;
import com.facebook.react.views.drawer.events.DrawerOpenedEvent;
import com.facebook.react.views.drawer.events.DrawerSlideEvent;
import com.facebook.react.views.drawer.events.DrawerStateChangedEvent;
import java.util.Map;
@ReactModule(name = ReactDrawerLayoutManager.REACT_CLASS)
/* loaded from: classes.dex */
public class ReactDrawerLayoutManager extends ViewGroupManager<ReactDrawerLayout> implements AndroidDrawerLayoutManagerInterface<ReactDrawerLayout> {
    public static final int CLOSE_DRAWER = 2;
    public static final int OPEN_DRAWER = 1;
    public static final String REACT_CLASS = "AndroidDrawerLayout";
    private final ViewManagerDelegate<ReactDrawerLayout> mDelegate = new AndroidDrawerLayoutManagerDelegate(this);

    @Override // com.facebook.react.uimanager.ViewManager, com.facebook.react.bridge.NativeModule
    public String getName() {
        return REACT_CLASS;
    }

    @Override // com.facebook.react.uimanager.ViewGroupManager, com.facebook.react.uimanager.IViewManagerWithChildren
    public boolean needsCustomLayoutForChildren() {
        return true;
    }

    public void setDrawerBackgroundColor(ReactDrawerLayout view, Integer value) {
    }

    public void setKeyboardDismissMode(ReactDrawerLayout view, String value) {
    }

    public void setStatusBarBackgroundColor(ReactDrawerLayout view, Integer value) {
    }

    public void addEventEmitters(ThemedReactContext reactContext, ReactDrawerLayout view) {
        EventDispatcher eventDispatcherForReactTag = UIManagerHelper.getEventDispatcherForReactTag(reactContext, view.getId());
        if (eventDispatcherForReactTag == null) {
            return;
        }
        view.addDrawerListener(new DrawerEventEmitter(view, eventDispatcherForReactTag));
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public ReactDrawerLayout createViewInstance(ThemedReactContext context) {
        return new ReactDrawerLayout(context);
    }

    public void setDrawerPosition(ReactDrawerLayout view, String value) {
        if (value == null) {
            view.setDrawerPosition(GravityCompat.START);
        } else {
            setDrawerPositionInternal(view, value);
        }
    }

    @ReactProp(name = "drawerPosition")
    public void setDrawerPosition(ReactDrawerLayout view, Dynamic drawerPosition) {
        if (drawerPosition.isNull()) {
            view.setDrawerPosition(GravityCompat.START);
        } else if (drawerPosition.getType() == ReadableType.Number) {
            int asInt = drawerPosition.asInt();
            if (8388611 == asInt || 8388613 == asInt) {
                view.setDrawerPosition(asInt);
                return;
            }
            throw new JSApplicationIllegalArgumentException("Unknown drawerPosition " + asInt);
        } else if (drawerPosition.getType() == ReadableType.String) {
            setDrawerPositionInternal(view, drawerPosition.asString());
        } else {
            throw new JSApplicationIllegalArgumentException("drawerPosition must be a string or int");
        }
    }

    private void setDrawerPositionInternal(ReactDrawerLayout view, String drawerPosition) {
        if (drawerPosition.equals(ViewProps.LEFT)) {
            view.setDrawerPosition(GravityCompat.START);
        } else if (drawerPosition.equals(ViewProps.RIGHT)) {
            view.setDrawerPosition(GravityCompat.END);
        } else {
            throw new JSApplicationIllegalArgumentException("drawerPosition must be 'left' or 'right', received" + drawerPosition);
        }
    }

    @ReactProp(defaultFloat = Float.NaN, name = "drawerWidth")
    public void setDrawerWidth(ReactDrawerLayout view, float width) {
        view.setDrawerWidth(Float.isNaN(width) ? -1 : Math.round(PixelUtil.toPixelFromDIP(width)));
    }

    public void setDrawerWidth(ReactDrawerLayout view, Float width) {
        view.setDrawerWidth(width == null ? -1 : Math.round(PixelUtil.toPixelFromDIP(width.floatValue())));
    }

    @ReactProp(name = "drawerLockMode")
    public void setDrawerLockMode(ReactDrawerLayout view, String drawerLockMode) {
        if (drawerLockMode == null || "unlocked".equals(drawerLockMode)) {
            view.setDrawerLockMode(0);
        } else if ("locked-closed".equals(drawerLockMode)) {
            view.setDrawerLockMode(1);
        } else if ("locked-open".equals(drawerLockMode)) {
            view.setDrawerLockMode(2);
        } else {
            throw new JSApplicationIllegalArgumentException("Unknown drawerLockMode " + drawerLockMode);
        }
    }

    public void openDrawer(ReactDrawerLayout view) {
        view.openDrawer();
    }

    public void closeDrawer(ReactDrawerLayout view) {
        view.closeDrawer();
    }

    public void setElevation(ReactDrawerLayout view, float elevation) {
        view.setDrawerElevation(PixelUtil.toPixelFromDIP(elevation));
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public Map<String, Integer> getCommandsMap() {
        return MapBuilder.of("openDrawer", 1, "closeDrawer", 2);
    }

    public void receiveCommand(ReactDrawerLayout root, int commandId, ReadableArray args) {
        if (commandId == 1) {
            root.openDrawer();
        } else if (commandId != 2) {
        } else {
            root.closeDrawer();
        }
    }

    public void receiveCommand(ReactDrawerLayout root, String commandId, ReadableArray args) {
        commandId.hashCode();
        if (commandId.equals("closeDrawer")) {
            root.closeDrawer();
        } else if (!commandId.equals("openDrawer")) {
        } else {
            root.openDrawer();
        }
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public Map getExportedViewConstants() {
        return MapBuilder.of("DrawerPosition", MapBuilder.of("Left", Integer.valueOf((int) GravityCompat.START), "Right", Integer.valueOf((int) GravityCompat.END)));
    }

    @Override // com.facebook.react.uimanager.BaseViewManager, com.facebook.react.uimanager.ViewManager
    public Map getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.of(DrawerSlideEvent.EVENT_NAME, MapBuilder.of("registrationName", "onDrawerSlide"), DrawerOpenedEvent.EVENT_NAME, MapBuilder.of("registrationName", "onDrawerOpen"), DrawerClosedEvent.EVENT_NAME, MapBuilder.of("registrationName", "onDrawerClose"), DrawerStateChangedEvent.EVENT_NAME, MapBuilder.of("registrationName", "onDrawerStateChanged"));
    }

    public void addView(ReactDrawerLayout parent, View child, int index) {
        if (getChildCount(parent) < 2) {
            if (index != 0 && index != 1) {
                throw new JSApplicationIllegalArgumentException("The only valid indices for drawer's child are 0 or 1. Got " + index + " instead.");
            }
            parent.addView(child, index);
            parent.setDrawerProperties();
            return;
        }
        throw new JSApplicationIllegalArgumentException("The Drawer cannot have more than two children");
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public ViewManagerDelegate<ReactDrawerLayout> getDelegate() {
        return this.mDelegate;
    }

    /* loaded from: classes.dex */
    public static class DrawerEventEmitter implements DrawerLayout.DrawerListener {
        private final DrawerLayout mDrawerLayout;
        private final EventDispatcher mEventDispatcher;

        public DrawerEventEmitter(DrawerLayout drawerLayout, EventDispatcher eventDispatcher) {
            this.mDrawerLayout = drawerLayout;
            this.mEventDispatcher = eventDispatcher;
        }

        @Override // androidx.drawerlayout.widget.DrawerLayout.DrawerListener
        public void onDrawerSlide(View view, float v) {
            this.mEventDispatcher.dispatchEvent(new DrawerSlideEvent(UIManagerHelper.getSurfaceId(this.mDrawerLayout), this.mDrawerLayout.getId(), v));
        }

        @Override // androidx.drawerlayout.widget.DrawerLayout.DrawerListener
        public void onDrawerOpened(View view) {
            this.mEventDispatcher.dispatchEvent(new DrawerOpenedEvent(UIManagerHelper.getSurfaceId(this.mDrawerLayout), this.mDrawerLayout.getId()));
        }

        @Override // androidx.drawerlayout.widget.DrawerLayout.DrawerListener
        public void onDrawerClosed(View view) {
            this.mEventDispatcher.dispatchEvent(new DrawerClosedEvent(UIManagerHelper.getSurfaceId(this.mDrawerLayout), this.mDrawerLayout.getId()));
        }

        @Override // androidx.drawerlayout.widget.DrawerLayout.DrawerListener
        public void onDrawerStateChanged(int i) {
            this.mEventDispatcher.dispatchEvent(new DrawerStateChangedEvent(UIManagerHelper.getSurfaceId(this.mDrawerLayout), this.mDrawerLayout.getId(), i));
        }
    }
}
