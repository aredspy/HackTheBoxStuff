package com.facebook.react.uimanager;

import android.content.Context;
import android.view.View;
import com.facebook.react.bridge.BaseJavaModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.touch.JSResponderHandler;
import com.facebook.react.touch.ReactInterceptingViewGroup;
import com.facebook.react.uimanager.ReactShadowNode;
import com.facebook.yoga.YogaMeasureMode;
import java.util.Map;
/* loaded from: classes.dex */
public abstract class ViewManager<T extends View, C extends ReactShadowNode> extends BaseJavaModule {
    protected void addEventEmitters(ThemedReactContext reactContext, T view) {
    }

    protected abstract T createViewInstance(ThemedReactContext reactContext);

    public Map<String, Integer> getCommandsMap() {
        return null;
    }

    public ViewManagerDelegate<T> getDelegate() {
        return null;
    }

    public Map<String, Object> getExportedCustomBubblingEventTypeConstants() {
        return null;
    }

    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        return null;
    }

    public Map<String, Object> getExportedViewConstants() {
        return null;
    }

    @Override // com.facebook.react.bridge.NativeModule
    public abstract String getName();

    public abstract Class<? extends C> getShadowNodeClass();

    public long measure(Context context, ReadableMap localData, ReadableMap props, ReadableMap state, float width, YogaMeasureMode widthMode, float height, YogaMeasureMode heightMode, float[] attachmentsPositions) {
        return 0L;
    }

    public void onAfterUpdateTransaction(T view) {
    }

    public void onDropViewInstance(T view) {
    }

    @Deprecated
    public void receiveCommand(T root, int commandId, ReadableArray args) {
    }

    public void receiveCommand(T root, String commandId, ReadableArray args) {
    }

    public void setPadding(T view, int left, int top, int right, int bottom) {
    }

    public abstract void updateExtraData(T root, Object extraData);

    public Object updateState(T view, ReactStylesDiffMap props, StateWrapper stateWrapper) {
        return null;
    }

    public void updateProperties(T viewToUpdate, ReactStylesDiffMap props) {
        ViewManagerDelegate<T> delegate = getDelegate();
        if (delegate != null) {
            ViewManagerPropertyUpdater.updateProps(delegate, viewToUpdate, props);
        } else {
            ViewManagerPropertyUpdater.updateProps(this, viewToUpdate, props);
        }
        onAfterUpdateTransaction(viewToUpdate);
    }

    public T createView(int reactTag, ThemedReactContext reactContext, ReactStylesDiffMap props, StateWrapper stateWrapper, JSResponderHandler jsResponderHandler) {
        T createViewInstance = createViewInstance(reactTag, reactContext, props, stateWrapper);
        if (createViewInstance instanceof ReactInterceptingViewGroup) {
            ((ReactInterceptingViewGroup) createViewInstance).setOnInterceptTouchEventListener(jsResponderHandler);
        }
        return createViewInstance;
    }

    public C createShadowNodeInstance() {
        throw new RuntimeException("ViewManager subclasses must implement createShadowNodeInstance()");
    }

    public C createShadowNodeInstance(ReactApplicationContext context) {
        return createShadowNodeInstance();
    }

    protected T createViewInstance(int reactTag, ThemedReactContext reactContext, ReactStylesDiffMap initialProps, StateWrapper stateWrapper) {
        Object updateState;
        T createViewInstance = createViewInstance(reactContext);
        createViewInstance.setId(reactTag);
        addEventEmitters(reactContext, createViewInstance);
        if (initialProps != null) {
            updateProperties(createViewInstance, initialProps);
        }
        if (stateWrapper != null && (updateState = updateState(createViewInstance, initialProps, stateWrapper)) != null) {
            updateExtraData(createViewInstance, updateState);
        }
        return createViewInstance;
    }

    public Map<String, String> getNativeProps() {
        return ViewManagerPropertyUpdater.getNativeProps(getClass(), getShadowNodeClass());
    }
}
