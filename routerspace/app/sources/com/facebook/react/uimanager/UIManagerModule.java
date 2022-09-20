package com.facebook.react.uimanager;

import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.view.View;
import androidx.collection.ArrayMap;
import com.facebook.common.logging.FLog;
import com.facebook.debug.holder.PrinterHolder;
import com.facebook.debug.tags.ReactDebugOverlayTags;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Dynamic;
import com.facebook.react.bridge.GuardedRunnable;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.OnBatchCompleteListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMarker;
import com.facebook.react.bridge.ReactMarkerConstants;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.UIManager;
import com.facebook.react.bridge.UIManagerListener;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.config.ReactFeatureFlags;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.common.ViewUtil;
import com.facebook.react.uimanager.debug.NotThreadSafeViewHierarchyUpdateDebugListener;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.uimanager.events.EventDispatcherImpl;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.facebook.systrace.Systrace;
import com.facebook.systrace.SystraceMessage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
@ReactModule(name = UIManagerModule.NAME)
/* loaded from: classes.dex */
public class UIManagerModule extends ReactContextBaseJavaModule implements OnBatchCompleteListener, LifecycleEventListener, UIManager {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final boolean DEBUG = PrinterHolder.getPrinter().shouldDisplayLogMessage(ReactDebugOverlayTags.UI_MANAGER);
    public static final String NAME = "UIManager";
    public static final String TAG = "UIManagerModule";
    private int mBatchId;
    private final Map<String, Object> mCustomDirectEvents;
    private final EventDispatcher mEventDispatcher;
    private final List<UIManagerModuleListener> mListeners;
    private final MemoryTrimCallback mMemoryTrimCallback;
    private final Map<String, Object> mModuleConstants;
    private int mNumRootViews;
    private final UIImplementation mUIImplementation;
    private final CopyOnWriteArrayList<UIManagerListener> mUIManagerListeners;
    private Map<String, WritableMap> mViewManagerConstantsCache;
    private volatile int mViewManagerConstantsCacheSize;
    private final ViewManagerRegistry mViewManagerRegistry;

    /* loaded from: classes.dex */
    public interface CustomEventNamesResolver {
        String resolveCustomEventName(String eventName);
    }

    @Override // com.facebook.react.bridge.NativeModule
    public String getName() {
        return NAME;
    }

    public UIManagerModule(ReactApplicationContext reactContext, ViewManagerResolver viewManagerResolver, int minTimeLeftInFrameForNonBatchedOperationMs) {
        this(reactContext, viewManagerResolver, new UIImplementationProvider(), minTimeLeftInFrameForNonBatchedOperationMs);
    }

    public UIManagerModule(ReactApplicationContext reactContext, List<ViewManager> viewManagersList, int minTimeLeftInFrameForNonBatchedOperationMs) {
        this(reactContext, viewManagersList, new UIImplementationProvider(), minTimeLeftInFrameForNonBatchedOperationMs);
    }

    @Deprecated
    public UIManagerModule(ReactApplicationContext reactContext, ViewManagerResolver viewManagerResolver, UIImplementationProvider uiImplementationProvider, int minTimeLeftInFrameForNonBatchedOperationMs) {
        super(reactContext);
        this.mMemoryTrimCallback = new MemoryTrimCallback();
        this.mListeners = new ArrayList();
        this.mUIManagerListeners = new CopyOnWriteArrayList<>();
        this.mBatchId = 0;
        this.mNumRootViews = 0;
        DisplayMetricsHolder.initDisplayMetricsIfNotInitialized(reactContext);
        EventDispatcherImpl eventDispatcherImpl = new EventDispatcherImpl(reactContext);
        this.mEventDispatcher = eventDispatcherImpl;
        this.mModuleConstants = createConstants(viewManagerResolver);
        this.mCustomDirectEvents = UIManagerModuleConstants.getDirectEventTypeConstants();
        ViewManagerRegistry viewManagerRegistry = new ViewManagerRegistry(viewManagerResolver);
        this.mViewManagerRegistry = viewManagerRegistry;
        this.mUIImplementation = uiImplementationProvider.createUIImplementation(reactContext, viewManagerRegistry, eventDispatcherImpl, minTimeLeftInFrameForNonBatchedOperationMs);
        reactContext.addLifecycleEventListener(this);
    }

    @Deprecated
    public UIManagerModule(ReactApplicationContext reactContext, List<ViewManager> viewManagersList, UIImplementationProvider uiImplementationProvider, int minTimeLeftInFrameForNonBatchedOperationMs) {
        super(reactContext);
        this.mMemoryTrimCallback = new MemoryTrimCallback();
        this.mListeners = new ArrayList();
        this.mUIManagerListeners = new CopyOnWriteArrayList<>();
        this.mBatchId = 0;
        this.mNumRootViews = 0;
        DisplayMetricsHolder.initDisplayMetricsIfNotInitialized(reactContext);
        EventDispatcherImpl eventDispatcherImpl = new EventDispatcherImpl(reactContext);
        this.mEventDispatcher = eventDispatcherImpl;
        HashMap newHashMap = MapBuilder.newHashMap();
        this.mCustomDirectEvents = newHashMap;
        this.mModuleConstants = createConstants(viewManagersList, null, newHashMap);
        ViewManagerRegistry viewManagerRegistry = new ViewManagerRegistry(viewManagersList);
        this.mViewManagerRegistry = viewManagerRegistry;
        this.mUIImplementation = uiImplementationProvider.createUIImplementation(reactContext, viewManagerRegistry, eventDispatcherImpl, minTimeLeftInFrameForNonBatchedOperationMs);
        reactContext.addLifecycleEventListener(this);
    }

    @Deprecated
    public UIImplementation getUIImplementation() {
        return this.mUIImplementation;
    }

    @Override // com.facebook.react.bridge.BaseJavaModule
    public Map<String, Object> getConstants() {
        return this.mModuleConstants;
    }

    @Override // com.facebook.react.bridge.BaseJavaModule, com.facebook.react.bridge.NativeModule, com.facebook.react.turbomodule.core.interfaces.TurboModule
    public void initialize() {
        getReactApplicationContext().registerComponentCallbacks(this.mMemoryTrimCallback);
        this.mEventDispatcher.registerEventEmitter(1, (RCTEventEmitter) getReactApplicationContext().getJSModule(RCTEventEmitter.class));
    }

    @Override // com.facebook.react.bridge.LifecycleEventListener
    public void onHostResume() {
        this.mUIImplementation.onHostResume();
    }

    @Override // com.facebook.react.bridge.LifecycleEventListener
    public void onHostPause() {
        this.mUIImplementation.onHostPause();
    }

    @Override // com.facebook.react.bridge.LifecycleEventListener
    public void onHostDestroy() {
        this.mUIImplementation.onHostDestroy();
    }

    @Override // com.facebook.react.bridge.BaseJavaModule, com.facebook.react.bridge.NativeModule
    public void onCatalystInstanceDestroy() {
        super.onCatalystInstanceDestroy();
        this.mEventDispatcher.onCatalystInstanceDestroyed();
        this.mUIImplementation.onCatalystInstanceDestroyed();
        ReactApplicationContext reactApplicationContext = getReactApplicationContext();
        if (ReactFeatureFlags.enableReactContextCleanupFix) {
            reactApplicationContext.removeLifecycleEventListener(this);
        }
        reactApplicationContext.unregisterComponentCallbacks(this.mMemoryTrimCallback);
        YogaNodePool.get().clear();
        ViewManagerPropertyUpdater.clear();
    }

    @Deprecated
    public ViewManagerRegistry getViewManagerRegistry_DO_NOT_USE() {
        return this.mViewManagerRegistry;
    }

    private static Map<String, Object> createConstants(ViewManagerResolver viewManagerResolver) {
        ReactMarker.logMarker(ReactMarkerConstants.CREATE_UI_MANAGER_MODULE_CONSTANTS_START);
        SystraceMessage.beginSection(0L, "CreateUIManagerConstants").arg("Lazy", (Object) true).flush();
        try {
            return UIManagerModuleConstantsHelper.createConstants(viewManagerResolver);
        } finally {
            Systrace.endSection(0L);
            ReactMarker.logMarker(ReactMarkerConstants.CREATE_UI_MANAGER_MODULE_CONSTANTS_END);
        }
    }

    private static Map<String, Object> createConstants(List<ViewManager> viewManagers, Map<String, Object> customBubblingEvents, Map<String, Object> customDirectEvents) {
        ReactMarker.logMarker(ReactMarkerConstants.CREATE_UI_MANAGER_MODULE_CONSTANTS_START);
        SystraceMessage.beginSection(0L, "CreateUIManagerConstants").arg("Lazy", (Object) false).flush();
        try {
            return UIManagerModuleConstantsHelper.createConstants(viewManagers, customBubblingEvents, customDirectEvents);
        } finally {
            Systrace.endSection(0L);
            ReactMarker.logMarker(ReactMarkerConstants.CREATE_UI_MANAGER_MODULE_CONSTANTS_END);
        }
    }

    @Override // com.facebook.react.bridge.UIManager
    @Deprecated
    public void preInitializeViewManagers(List<String> viewManagerNames) {
        if (ReactFeatureFlags.enableExperimentalStaticViewConfigs) {
            for (String str : viewManagerNames) {
                this.mUIImplementation.resolveViewManager(str);
            }
            return;
        }
        ArrayMap arrayMap = new ArrayMap();
        for (String str2 : viewManagerNames) {
            WritableMap computeConstantsForViewManager = computeConstantsForViewManager(str2);
            if (computeConstantsForViewManager != null) {
                arrayMap.put(str2, computeConstantsForViewManager);
            }
        }
        this.mViewManagerConstantsCacheSize = viewManagerNames.size();
        this.mViewManagerConstantsCache = Collections.unmodifiableMap(arrayMap);
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public WritableMap getConstantsForViewManager(String viewManagerName) {
        Map<String, WritableMap> map = this.mViewManagerConstantsCache;
        if (map != null && map.containsKey(viewManagerName)) {
            WritableMap writableMap = this.mViewManagerConstantsCache.get(viewManagerName);
            int i = this.mViewManagerConstantsCacheSize - 1;
            this.mViewManagerConstantsCacheSize = i;
            if (i <= 0) {
                this.mViewManagerConstantsCache = null;
            }
            return writableMap;
        }
        return computeConstantsForViewManager(viewManagerName);
    }

    private WritableMap computeConstantsForViewManager(String viewManagerName) {
        ViewManager resolveViewManager = viewManagerName != null ? this.mUIImplementation.resolveViewManager(viewManagerName) : null;
        if (resolveViewManager == null) {
            return null;
        }
        SystraceMessage.beginSection(0L, "UIManagerModule.getConstantsForViewManager").arg("ViewManager", resolveViewManager.getName()).arg("Lazy", (Object) true).flush();
        try {
            Map<String, Object> createConstantsForViewManager = UIManagerModuleConstantsHelper.createConstantsForViewManager(resolveViewManager, null, null, null, this.mCustomDirectEvents);
            if (createConstantsForViewManager == null) {
                return null;
            }
            return Arguments.makeNativeMap(createConstantsForViewManager);
        } finally {
            SystraceMessage.endSection(0L).flush();
        }
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public WritableMap getDefaultEventTypes() {
        return Arguments.makeNativeMap(UIManagerModuleConstantsHelper.getDefaultExportableEventTypes());
    }

    @Deprecated
    public CustomEventNamesResolver getDirectEventNamesResolver() {
        return new CustomEventNamesResolver() { // from class: com.facebook.react.uimanager.UIManagerModule.1
            @Override // com.facebook.react.uimanager.UIManagerModule.CustomEventNamesResolver
            public String resolveCustomEventName(String eventName) {
                return UIManagerModule.this.resolveCustomDirectEventName(eventName);
            }
        };
    }

    @Override // com.facebook.react.bridge.UIManager
    @Deprecated
    public String resolveCustomDirectEventName(String eventName) {
        Map map;
        return (eventName == null || (map = (Map) this.mCustomDirectEvents.get(eventName)) == null) ? eventName : (String) map.get("registrationName");
    }

    @Override // com.facebook.react.bridge.PerformanceCounter
    public void profileNextBatch() {
        this.mUIImplementation.profileNextBatch();
    }

    @Override // com.facebook.react.bridge.PerformanceCounter
    public Map<String, Long> getPerformanceCounters() {
        return this.mUIImplementation.getProfiledBatchPerfCounters();
    }

    public <T extends View> int addRootView(final T rootView) {
        return addRootView(rootView, null, null);
    }

    @Override // com.facebook.react.bridge.UIManager
    public void synchronouslyUpdateViewOnUIThread(int tag, ReadableMap props) {
        this.mUIImplementation.synchronouslyUpdateViewOnUIThread(tag, new ReactStylesDiffMap(props));
    }

    @Override // com.facebook.react.bridge.UIManager
    public <T extends View> int addRootView(final T rootView, WritableMap initialProps, String initialUITemplate) {
        Systrace.beginSection(0L, "UIManagerModule.addRootView");
        int nextRootViewTag = ReactRootViewTagGenerator.getNextRootViewTag();
        this.mUIImplementation.registerRootView(rootView, nextRootViewTag, new ThemedReactContext(getReactApplicationContext(), rootView.getContext(), ((ReactRoot) rootView).getSurfaceID(), -1));
        this.mNumRootViews++;
        Systrace.endSection(0L);
        return nextRootViewTag;
    }

    @Override // com.facebook.react.bridge.UIManager
    public <T extends View> int startSurface(final T rootView, final String moduleName, final WritableMap initialProps, int widthMeasureSpec, int heightMeasureSpec) {
        throw new UnsupportedOperationException();
    }

    @Override // com.facebook.react.bridge.UIManager
    public void stopSurface(final int surfaceId) {
        throw new UnsupportedOperationException();
    }

    @ReactMethod
    public void removeRootView(int rootViewTag) {
        this.mUIImplementation.removeRootView(rootViewTag);
        this.mNumRootViews--;
    }

    public void updateNodeSize(int nodeViewTag, int newWidth, int newHeight) {
        getReactApplicationContext().assertOnNativeModulesQueueThread();
        this.mUIImplementation.updateNodeSize(nodeViewTag, newWidth, newHeight);
    }

    public void setViewLocalData(final int tag, final Object data) {
        ReactApplicationContext reactApplicationContext = getReactApplicationContext();
        reactApplicationContext.assertOnUiQueueThread();
        reactApplicationContext.runOnNativeModulesQueueThread(new GuardedRunnable(reactApplicationContext) { // from class: com.facebook.react.uimanager.UIManagerModule.2
            @Override // com.facebook.react.bridge.GuardedRunnable
            public void runGuarded() {
                UIManagerModule.this.mUIImplementation.setViewLocalData(tag, data);
            }
        });
    }

    @ReactMethod
    public void createView(int tag, String className, int rootViewTag, ReadableMap props) {
        if (DEBUG) {
            String str = "(UIManager.createView) tag: " + tag + ", class: " + className + ", props: " + props;
            FLog.d(ReactConstants.TAG, str);
            PrinterHolder.getPrinter().logMessage(ReactDebugOverlayTags.UI_MANAGER, str);
        }
        this.mUIImplementation.createView(tag, className, rootViewTag, props);
    }

    @ReactMethod
    public void updateView(final int tag, final String className, final ReadableMap props) {
        if (DEBUG) {
            String str = "(UIManager.updateView) tag: " + tag + ", class: " + className + ", props: " + props;
            FLog.d(ReactConstants.TAG, str);
            PrinterHolder.getPrinter().logMessage(ReactDebugOverlayTags.UI_MANAGER, str);
        }
        this.mUIImplementation.updateView(tag, className, props);
    }

    @ReactMethod
    public void manageChildren(int viewTag, ReadableArray moveFrom, ReadableArray moveTo, ReadableArray addChildTags, ReadableArray addAtIndices, ReadableArray removeFrom) {
        if (DEBUG) {
            String str = "(UIManager.manageChildren) tag: " + viewTag + ", moveFrom: " + moveFrom + ", moveTo: " + moveTo + ", addTags: " + addChildTags + ", atIndices: " + addAtIndices + ", removeFrom: " + removeFrom;
            FLog.d(ReactConstants.TAG, str);
            PrinterHolder.getPrinter().logMessage(ReactDebugOverlayTags.UI_MANAGER, str);
        }
        this.mUIImplementation.manageChildren(viewTag, moveFrom, moveTo, addChildTags, addAtIndices, removeFrom);
    }

    @ReactMethod
    public void setChildren(int viewTag, ReadableArray childrenTags) {
        if (DEBUG) {
            String str = "(UIManager.setChildren) tag: " + viewTag + ", children: " + childrenTags;
            FLog.d(ReactConstants.TAG, str);
            PrinterHolder.getPrinter().logMessage(ReactDebugOverlayTags.UI_MANAGER, str);
        }
        this.mUIImplementation.setChildren(viewTag, childrenTags);
    }

    @ReactMethod
    @Deprecated
    public void replaceExistingNonRootView(int oldTag, int newTag) {
        this.mUIImplementation.replaceExistingNonRootView(oldTag, newTag);
    }

    @ReactMethod
    @Deprecated
    public void removeSubviewsFromContainerWithID(int containerTag) {
        this.mUIImplementation.removeSubviewsFromContainerWithID(containerTag);
    }

    @ReactMethod
    public void measure(int reactTag, Callback callback) {
        this.mUIImplementation.measure(reactTag, callback);
    }

    @ReactMethod
    public void measureInWindow(int reactTag, Callback callback) {
        this.mUIImplementation.measureInWindow(reactTag, callback);
    }

    @ReactMethod
    public void measureLayout(int tag, int ancestorTag, Callback errorCallback, Callback successCallback) {
        this.mUIImplementation.measureLayout(tag, ancestorTag, errorCallback, successCallback);
    }

    @ReactMethod
    @Deprecated
    public void measureLayoutRelativeToParent(int tag, Callback errorCallback, Callback successCallback) {
        this.mUIImplementation.measureLayoutRelativeToParent(tag, errorCallback, successCallback);
    }

    @ReactMethod
    public void findSubviewIn(final int reactTag, final ReadableArray point, final Callback callback) {
        this.mUIImplementation.findSubviewIn(reactTag, Math.round(PixelUtil.toPixelFromDIP(point.getDouble(0))), Math.round(PixelUtil.toPixelFromDIP(point.getDouble(1))), callback);
    }

    @ReactMethod
    @Deprecated
    public void viewIsDescendantOf(final int reactTag, final int ancestorReactTag, final Callback callback) {
        this.mUIImplementation.viewIsDescendantOf(reactTag, ancestorReactTag, callback);
    }

    @ReactMethod
    public void setJSResponder(int reactTag, boolean blockNativeResponder) {
        this.mUIImplementation.setJSResponder(reactTag, blockNativeResponder);
    }

    @ReactMethod
    public void clearJSResponder() {
        this.mUIImplementation.clearJSResponder();
    }

    @ReactMethod
    public void dispatchViewManagerCommand(int reactTag, Dynamic commandId, ReadableArray commandArgs) {
        UIManager uIManager = UIManagerHelper.getUIManager(getReactApplicationContext(), ViewUtil.getUIManagerType(reactTag));
        if (uIManager == null) {
            return;
        }
        if (commandId.getType() == ReadableType.Number) {
            uIManager.dispatchCommand(reactTag, commandId.asInt(), commandArgs);
        } else if (commandId.getType() != ReadableType.String) {
        } else {
            uIManager.dispatchCommand(reactTag, commandId.asString(), commandArgs);
        }
    }

    @Override // com.facebook.react.bridge.UIManager
    @Deprecated
    public void dispatchCommand(int reactTag, int commandId, ReadableArray commandArgs) {
        this.mUIImplementation.dispatchViewManagerCommand(reactTag, commandId, commandArgs);
    }

    @Override // com.facebook.react.bridge.UIManager
    public void dispatchCommand(int reactTag, String commandId, ReadableArray commandArgs) {
        this.mUIImplementation.dispatchViewManagerCommand(reactTag, commandId, commandArgs);
    }

    @ReactMethod
    public void showPopupMenu(int reactTag, ReadableArray items, Callback error, Callback success) {
        this.mUIImplementation.showPopupMenu(reactTag, items, error, success);
    }

    @ReactMethod
    public void dismissPopupMenu() {
        this.mUIImplementation.dismissPopupMenu();
    }

    @ReactMethod
    public void setLayoutAnimationEnabledExperimental(boolean enabled) {
        this.mUIImplementation.setLayoutAnimationEnabledExperimental(enabled);
    }

    @ReactMethod
    public void configureNextLayoutAnimation(ReadableMap config, Callback success, Callback error) {
        this.mUIImplementation.configureNextLayoutAnimation(config, success);
    }

    @Override // com.facebook.react.bridge.OnBatchCompleteListener
    public void onBatchComplete() {
        int i = this.mBatchId;
        this.mBatchId = i + 1;
        SystraceMessage.beginSection(0L, "onBatchCompleteUI").arg("BatchId", i).flush();
        for (UIManagerModuleListener uIManagerModuleListener : this.mListeners) {
            uIManagerModuleListener.willDispatchViewUpdates(this);
        }
        Iterator<UIManagerListener> it = this.mUIManagerListeners.iterator();
        while (it.hasNext()) {
            it.next().willDispatchViewUpdates(this);
        }
        try {
            if (this.mNumRootViews > 0) {
                this.mUIImplementation.dispatchViewUpdates(i);
            }
        } finally {
            Systrace.endSection(0L);
        }
    }

    public void setViewHierarchyUpdateDebugListener(NotThreadSafeViewHierarchyUpdateDebugListener listener) {
        this.mUIImplementation.setViewHierarchyUpdateDebugListener(listener);
    }

    @Override // com.facebook.react.bridge.UIManager
    public EventDispatcher getEventDispatcher() {
        return this.mEventDispatcher;
    }

    @Override // com.facebook.react.bridge.UIManager
    @ReactMethod
    public void sendAccessibilityEvent(int tag, int eventType) {
        int uIManagerType = ViewUtil.getUIManagerType(tag);
        if (uIManagerType == 2) {
            UIManager uIManager = UIManagerHelper.getUIManager(getReactApplicationContext(), uIManagerType);
            if (uIManager == null) {
                return;
            }
            uIManager.sendAccessibilityEvent(tag, eventType);
            return;
        }
        this.mUIImplementation.sendAccessibilityEvent(tag, eventType);
    }

    public void addUIBlock(UIBlock block) {
        this.mUIImplementation.addUIBlock(block);
    }

    public void prependUIBlock(UIBlock block) {
        this.mUIImplementation.prependUIBlock(block);
    }

    @Deprecated
    public void addUIManagerListener(UIManagerModuleListener listener) {
        this.mListeners.add(listener);
    }

    @Deprecated
    public void removeUIManagerListener(UIManagerModuleListener listener) {
        this.mListeners.remove(listener);
    }

    @Override // com.facebook.react.bridge.UIManager
    public void addUIManagerEventListener(UIManagerListener listener) {
        this.mUIManagerListeners.add(listener);
    }

    @Override // com.facebook.react.bridge.UIManager
    public void removeUIManagerEventListener(UIManagerListener listener) {
        this.mUIManagerListeners.remove(listener);
    }

    @Deprecated
    public int resolveRootTagFromReactTag(int reactTag) {
        return ViewUtil.isRootTag(reactTag) ? reactTag : this.mUIImplementation.resolveRootTagFromReactTag(reactTag);
    }

    public void invalidateNodeLayout(int tag) {
        ReactShadowNode resolveShadowNode = this.mUIImplementation.resolveShadowNode(tag);
        if (resolveShadowNode == null) {
            FLog.w(ReactConstants.TAG, "Warning : attempted to dirty a non-existent react shadow node. reactTag=" + tag);
            return;
        }
        resolveShadowNode.dirty();
        this.mUIImplementation.dispatchViewUpdates(-1);
    }

    @Override // com.facebook.react.bridge.UIManager
    public void updateRootLayoutSpecs(final int rootViewTag, final int widthMeasureSpec, final int heightMeasureSpec, int offsetX, int offsetY) {
        ReactApplicationContext reactApplicationContext = getReactApplicationContext();
        reactApplicationContext.runOnNativeModulesQueueThread(new GuardedRunnable(reactApplicationContext) { // from class: com.facebook.react.uimanager.UIManagerModule.3
            @Override // com.facebook.react.bridge.GuardedRunnable
            public void runGuarded() {
                UIManagerModule.this.mUIImplementation.updateRootView(rootViewTag, widthMeasureSpec, heightMeasureSpec);
                UIManagerModule.this.mUIImplementation.dispatchViewUpdates(-1);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class MemoryTrimCallback implements ComponentCallbacks2 {
        @Override // android.content.ComponentCallbacks
        public void onConfigurationChanged(Configuration newConfig) {
        }

        @Override // android.content.ComponentCallbacks
        public void onLowMemory() {
        }

        private MemoryTrimCallback() {
            UIManagerModule.this = this$0;
        }

        @Override // android.content.ComponentCallbacks2
        public void onTrimMemory(int level) {
            if (level >= 60) {
                YogaNodePool.get().clear();
            }
        }
    }

    @Override // com.facebook.react.bridge.UIManager
    public View resolveView(int tag) {
        UiThreadUtil.assertOnUiThread();
        return this.mUIImplementation.getUIViewOperationQueue().getNativeViewHierarchyManager().resolveView(tag);
    }

    @Override // com.facebook.react.bridge.UIManager
    public void receiveEvent(int reactTag, String eventName, WritableMap event) {
        receiveEvent(-1, reactTag, eventName, event);
    }

    @Override // com.facebook.react.bridge.UIManager
    public void receiveEvent(int surfaceId, int reactTag, String eventName, WritableMap event) {
        ((RCTEventEmitter) getReactApplicationContext().getJSModule(RCTEventEmitter.class)).receiveEvent(reactTag, eventName, event);
    }
}
