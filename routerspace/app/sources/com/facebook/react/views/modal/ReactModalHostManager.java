package com.facebook.react.views.modal;

import android.content.DialogInterface;
import android.graphics.Point;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.LayoutShadowNode;
import com.facebook.react.uimanager.ReactStylesDiffMap;
import com.facebook.react.uimanager.StateWrapper;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.ViewManagerDelegate;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.viewmanagers.ModalHostViewManagerDelegate;
import com.facebook.react.viewmanagers.ModalHostViewManagerInterface;
import com.facebook.react.views.modal.ReactModalHostView;
import java.util.Map;
@ReactModule(name = ReactModalHostManager.REACT_CLASS)
/* loaded from: classes.dex */
public class ReactModalHostManager extends ViewGroupManager<ReactModalHostView> implements ModalHostViewManagerInterface<ReactModalHostView> {
    public static final String REACT_CLASS = "RCTModalHostView";
    private final ViewManagerDelegate<ReactModalHostView> mDelegate = new ModalHostViewManagerDelegate(this);

    @Override // com.facebook.react.uimanager.ViewManager, com.facebook.react.bridge.NativeModule
    public String getName() {
        return REACT_CLASS;
    }

    public void setAnimated(ReactModalHostView view, boolean value) {
    }

    public void setIdentifier(ReactModalHostView view, int value) {
    }

    public void setPresentationStyle(ReactModalHostView view, String value) {
    }

    public void setSupportedOrientations(ReactModalHostView view, ReadableArray value) {
    }

    @ReactProp(name = ViewProps.VISIBLE)
    public void setVisible(ReactModalHostView view, boolean visible) {
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public ReactModalHostView createViewInstance(ThemedReactContext reactContext) {
        return new ReactModalHostView(reactContext);
    }

    @Override // com.facebook.react.uimanager.ViewGroupManager, com.facebook.react.uimanager.ViewManager
    public LayoutShadowNode createShadowNodeInstance() {
        return new ModalHostShadowNode();
    }

    @Override // com.facebook.react.uimanager.ViewGroupManager, com.facebook.react.uimanager.ViewManager
    public Class<? extends LayoutShadowNode> getShadowNodeClass() {
        return ModalHostShadowNode.class;
    }

    public void onDropViewInstance(ReactModalHostView view) {
        super.onDropViewInstance((ReactModalHostManager) view);
        view.onDropInstance();
    }

    @ReactProp(name = "animationType")
    public void setAnimationType(ReactModalHostView view, String animationType) {
        if (animationType != null) {
            view.setAnimationType(animationType);
        }
    }

    @ReactProp(name = "transparent")
    public void setTransparent(ReactModalHostView view, boolean transparent) {
        view.setTransparent(transparent);
    }

    @ReactProp(name = "statusBarTranslucent")
    public void setStatusBarTranslucent(ReactModalHostView view, boolean statusBarTranslucent) {
        view.setStatusBarTranslucent(statusBarTranslucent);
    }

    @ReactProp(name = "hardwareAccelerated")
    public void setHardwareAccelerated(ReactModalHostView view, boolean hardwareAccelerated) {
        view.setHardwareAccelerated(hardwareAccelerated);
    }

    public void addEventEmitters(final ThemedReactContext reactContext, final ReactModalHostView view) {
        final EventDispatcher eventDispatcherForReactTag = UIManagerHelper.getEventDispatcherForReactTag(reactContext, view.getId());
        if (eventDispatcherForReactTag != null) {
            view.setOnRequestCloseListener(new ReactModalHostView.OnRequestCloseListener() { // from class: com.facebook.react.views.modal.ReactModalHostManager.1
                @Override // com.facebook.react.views.modal.ReactModalHostView.OnRequestCloseListener
                public void onRequestClose(DialogInterface dialog) {
                    eventDispatcherForReactTag.dispatchEvent(new RequestCloseEvent(UIManagerHelper.getSurfaceId(reactContext), view.getId()));
                }
            });
            view.setOnShowListener(new DialogInterface.OnShowListener() { // from class: com.facebook.react.views.modal.ReactModalHostManager.2
                @Override // android.content.DialogInterface.OnShowListener
                public void onShow(DialogInterface dialog) {
                    eventDispatcherForReactTag.dispatchEvent(new ShowEvent(UIManagerHelper.getSurfaceId(reactContext), view.getId()));
                }
            });
            view.setEventDispatcher(eventDispatcherForReactTag);
        }
    }

    @Override // com.facebook.react.uimanager.BaseViewManager, com.facebook.react.uimanager.ViewManager
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.builder().put(RequestCloseEvent.EVENT_NAME, MapBuilder.of("registrationName", "onRequestClose")).put(ShowEvent.EVENT_NAME, MapBuilder.of("registrationName", "onShow")).build();
    }

    public void onAfterUpdateTransaction(ReactModalHostView view) {
        super.onAfterUpdateTransaction((ReactModalHostManager) view);
        view.showOrUpdate();
    }

    public Object updateState(ReactModalHostView view, ReactStylesDiffMap props, StateWrapper stateWrapper) {
        view.getFabricViewStateManager().setStateWrapper(stateWrapper);
        Point modalHostSize = ModalHostHelper.getModalHostSize(view.getContext());
        view.updateState(modalHostSize.x, modalHostSize.y);
        return null;
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public ViewManagerDelegate<ReactModalHostView> getDelegate() {
        return this.mDelegate;
    }
}
