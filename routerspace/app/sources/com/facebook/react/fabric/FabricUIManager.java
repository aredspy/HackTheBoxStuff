package com.facebook.react.fabric;

import android.content.Context;
import android.graphics.Point;
import android.os.SystemClock;
import android.view.View;
import com.facebook.common.logging.FLog;
import com.facebook.debug.holder.PrinterHolder;
import com.facebook.debug.tags.ReactDebugOverlayTags;
import com.facebook.react.bridge.ColorPropConverter;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.NativeArray;
import com.facebook.react.bridge.NativeMap;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactMarker;
import com.facebook.react.bridge.ReactMarkerConstants;
import com.facebook.react.bridge.ReactSoftExceptionLogger;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.UIManager;
import com.facebook.react.bridge.UIManagerListener;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.mapbuffer.ReadableMapBuffer;
import com.facebook.react.config.ReactFeatureFlags;
import com.facebook.react.fabric.events.EventBeatManager;
import com.facebook.react.fabric.events.EventEmitterWrapper;
import com.facebook.react.fabric.events.FabricEventEmitter;
import com.facebook.react.fabric.mounting.LayoutMetricsConversions;
import com.facebook.react.fabric.mounting.MountItemDispatcher;
import com.facebook.react.fabric.mounting.MountingManager;
import com.facebook.react.fabric.mounting.SurfaceMountingManager;
import com.facebook.react.fabric.mounting.mountitems.DispatchIntCommandMountItem;
import com.facebook.react.fabric.mounting.mountitems.DispatchStringCommandMountItem;
import com.facebook.react.fabric.mounting.mountitems.IntBufferBatchMountItem;
import com.facebook.react.fabric.mounting.mountitems.MountItem;
import com.facebook.react.fabric.mounting.mountitems.PreAllocateViewMountItem;
import com.facebook.react.fabric.mounting.mountitems.SendAccessibilityEvent;
import com.facebook.react.modules.core.ReactChoreographer;
import com.facebook.react.modules.i18nmanager.I18nUtil;
import com.facebook.react.uimanager.IllegalViewOperationException;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.ReactRoot;
import com.facebook.react.uimanager.ReactRootViewTagGenerator;
import com.facebook.react.uimanager.RootViewUtil;
import com.facebook.react.uimanager.StateWrapper;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.ViewManagerPropertyUpdater;
import com.facebook.react.uimanager.ViewManagerRegistry;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.uimanager.events.EventDispatcherImpl;
import com.facebook.react.uimanager.events.LockFreeEventDispatcherImpl;
import com.facebook.react.uimanager.events.RCTModernEventEmitter;
import com.facebook.react.views.text.TextLayoutManager;
import com.facebook.react.views.text.TextLayoutManagerMapBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
/* loaded from: classes.dex */
public class FabricUIManager implements UIManager, LifecycleEventListener {
    public static final boolean ENABLE_FABRIC_LOGS;
    public static final boolean IS_DEVELOPMENT_ENVIRONMENT = false;
    public static final String TAG = "FabricUIManager";
    private Binding mBinding;
    private final DispatchUIFrameCallback mDispatchUIFrameCallback;
    private final EventBeatManager mEventBeatManager;
    private final EventDispatcher mEventDispatcher;
    private final MountItemDispatcher mMountItemDispatcher;
    private final MountingManager mMountingManager;
    private final ReactApplicationContext mReactApplicationContext;
    private volatile boolean mShouldDeallocateEventDispatcher;
    private final CopyOnWriteArrayList<UIManagerListener> mListeners = new CopyOnWriteArrayList<>();
    private volatile boolean mDestroyed = false;
    private boolean mDriveCxxAnimations = false;
    private long mDispatchViewUpdatesTime = 0;
    private long mCommitStartTime = 0;
    private long mLayoutTime = 0;
    private long mFinishTransactionTime = 0;
    private long mFinishTransactionCPPTime = 0;
    private int mCurrentSynchronousCommitNumber = 10000;
    private MountingManager.MountItemExecutor mMountItemExecutor = new MountingManager.MountItemExecutor() { // from class: com.facebook.react.fabric.FabricUIManager.1
        @Override // com.facebook.react.fabric.mounting.MountingManager.MountItemExecutor
        public void executeItems(Queue<MountItem> items) {
            FabricUIManager.this.mMountItemDispatcher.dispatchMountItems(items);
        }
    };

    @Override // com.facebook.react.bridge.LifecycleEventListener
    public void onHostDestroy() {
    }

    @Override // com.facebook.react.bridge.PerformanceCounter
    public void profileNextBatch() {
    }

    static {
        ENABLE_FABRIC_LOGS = ReactFeatureFlags.enableFabricLogs || PrinterHolder.getPrinter().shouldDisplayLogMessage(ReactDebugOverlayTags.FABRIC_UI_MANAGER);
        FabricSoLoader.staticInit();
    }

    @Deprecated
    public FabricUIManager(ReactApplicationContext reactContext, ViewManagerRegistry viewManagerRegistry, EventDispatcher eventDispatcher, EventBeatManager eventBeatManager) {
        this.mShouldDeallocateEventDispatcher = false;
        this.mDispatchUIFrameCallback = new DispatchUIFrameCallback(reactContext);
        this.mReactApplicationContext = reactContext;
        MountingManager mountingManager = new MountingManager(viewManagerRegistry, this.mMountItemExecutor);
        this.mMountingManager = mountingManager;
        this.mMountItemDispatcher = new MountItemDispatcher(mountingManager, new MountItemDispatchListener());
        this.mEventDispatcher = eventDispatcher;
        this.mShouldDeallocateEventDispatcher = false;
        this.mEventBeatManager = eventBeatManager;
        reactContext.addLifecycleEventListener(this);
    }

    public FabricUIManager(ReactApplicationContext reactContext, ViewManagerRegistry viewManagerRegistry, EventBeatManager eventBeatManager) {
        this.mShouldDeallocateEventDispatcher = false;
        this.mDispatchUIFrameCallback = new DispatchUIFrameCallback(reactContext);
        this.mReactApplicationContext = reactContext;
        MountingManager mountingManager = new MountingManager(viewManagerRegistry, this.mMountItemExecutor);
        this.mMountingManager = mountingManager;
        this.mMountItemDispatcher = new MountItemDispatcher(mountingManager, new MountItemDispatchListener());
        this.mEventDispatcher = ReactFeatureFlags.enableLockFreeEventDispatcher ? new LockFreeEventDispatcherImpl(reactContext) : new EventDispatcherImpl(reactContext);
        this.mShouldDeallocateEventDispatcher = true;
        this.mEventBeatManager = eventBeatManager;
        reactContext.addLifecycleEventListener(this);
    }

    @Override // com.facebook.react.bridge.UIManager
    @Deprecated
    public <T extends View> int addRootView(final T rootView, final WritableMap initialProps, final String initialUITemplate) {
        String str = TAG;
        ReactSoftExceptionLogger.logSoftException(str, new IllegalViewOperationException("Do not call addRootView in Fabric; it is unsupported. Call startSurface instead."));
        int nextRootViewTag = ReactRootViewTagGenerator.getNextRootViewTag();
        ReactRoot reactRoot = (ReactRoot) rootView;
        this.mMountingManager.startSurface(nextRootViewTag, rootView, new ThemedReactContext(this.mReactApplicationContext, rootView.getContext(), reactRoot.getSurfaceID(), nextRootViewTag));
        String jSModuleName = reactRoot.getJSModuleName();
        if (ENABLE_FABRIC_LOGS) {
            FLog.d(str, "Starting surface for module: %s and reactTag: %d", jSModuleName, Integer.valueOf(nextRootViewTag));
        }
        this.mBinding.startSurface(nextRootViewTag, jSModuleName, (NativeMap) initialProps);
        if (initialUITemplate != null) {
            this.mBinding.renderTemplateToSurface(nextRootViewTag, initialUITemplate);
        }
        return nextRootViewTag;
    }

    public ReadableMap getInspectorDataForInstance(final int surfaceId, final View view) {
        UiThreadUtil.assertOnUiThread();
        return this.mBinding.getInspectorDataForInstance(this.mMountingManager.getEventEmitter(surfaceId, view.getId()));
    }

    @Override // com.facebook.react.bridge.UIManager
    public void preInitializeViewManagers(List<String> viewManagerNames) {
        for (String str : viewManagerNames) {
            this.mMountingManager.initializeViewManager(str);
        }
    }

    @Override // com.facebook.react.bridge.UIManager
    public <T extends View> int startSurface(final T rootView, final String moduleName, final WritableMap initialProps, int widthMeasureSpec, int heightMeasureSpec) {
        int nextRootViewTag = ReactRootViewTagGenerator.getNextRootViewTag();
        Context context = rootView.getContext();
        ThemedReactContext themedReactContext = new ThemedReactContext(this.mReactApplicationContext, context, moduleName, nextRootViewTag);
        if (ENABLE_FABRIC_LOGS) {
            FLog.d(TAG, "Starting surface for module: %s and reactTag: %d", moduleName, Integer.valueOf(nextRootViewTag));
        }
        this.mMountingManager.startSurface(nextRootViewTag, rootView, themedReactContext);
        Point viewportOffset = UiThreadUtil.isOnUiThread() ? RootViewUtil.getViewportOffset(rootView) : new Point(0, 0);
        this.mBinding.startSurfaceWithConstraints(nextRootViewTag, moduleName, (NativeMap) initialProps, LayoutMetricsConversions.getMinSize(widthMeasureSpec), LayoutMetricsConversions.getMaxSize(widthMeasureSpec), LayoutMetricsConversions.getMinSize(heightMeasureSpec), LayoutMetricsConversions.getMaxSize(heightMeasureSpec), viewportOffset.x, viewportOffset.y, I18nUtil.getInstance().isRTL(context), I18nUtil.getInstance().doLeftAndRightSwapInRTL(context));
        return nextRootViewTag;
    }

    public void startSurface(final SurfaceHandler surfaceHandler, final View rootView) {
        int nextRootViewTag = ReactRootViewTagGenerator.getNextRootViewTag();
        if (rootView == null) {
            this.mMountingManager.startSurface(nextRootViewTag);
        } else {
            this.mMountingManager.startSurface(nextRootViewTag, rootView, new ThemedReactContext(this.mReactApplicationContext, rootView.getContext(), surfaceHandler.getModuleName(), nextRootViewTag));
        }
        surfaceHandler.setSurfaceId(nextRootViewTag);
        if (surfaceHandler instanceof SurfaceHandlerBinding) {
            this.mBinding.registerSurface((SurfaceHandlerBinding) surfaceHandler);
        }
        surfaceHandler.setMountable(rootView != null);
        surfaceHandler.start();
    }

    public void attachRootView(final SurfaceHandler surfaceHandler, final View rootView) {
        this.mMountingManager.attachRootView(surfaceHandler.getSurfaceId(), rootView, new ThemedReactContext(this.mReactApplicationContext, rootView.getContext(), surfaceHandler.getModuleName(), surfaceHandler.getSurfaceId()));
        surfaceHandler.setMountable(true);
    }

    public void stopSurface(final SurfaceHandler surfaceHandler) {
        if (!surfaceHandler.isRunning()) {
            ReactSoftExceptionLogger.logSoftException(TAG, new IllegalStateException("Trying to stop surface that hasn't started yet"));
            return;
        }
        this.mMountingManager.stopSurface(surfaceHandler.getSurfaceId());
        surfaceHandler.stop();
        if (!(surfaceHandler instanceof SurfaceHandlerBinding)) {
            return;
        }
        this.mBinding.unregisterSurface((SurfaceHandlerBinding) surfaceHandler);
    }

    public void onRequestEventBeat() {
        this.mEventDispatcher.dispatchAllEvents();
    }

    @Override // com.facebook.react.bridge.UIManager
    public void stopSurface(final int surfaceID) {
        this.mMountingManager.stopSurface(surfaceID);
        this.mBinding.stopSurface(surfaceID);
    }

    @Override // com.facebook.react.bridge.JSIModule
    public void initialize() {
        this.mEventDispatcher.registerEventEmitter(2, (RCTModernEventEmitter) new FabricEventEmitter(this));
        this.mEventDispatcher.addBatchEventDispatchedListener(this.mEventBeatManager);
    }

    @Override // com.facebook.react.bridge.JSIModule
    public void onCatalystInstanceDestroy() {
        String str = TAG;
        FLog.i(str, "FabricUIManager.onCatalystInstanceDestroy");
        if (this.mDestroyed) {
            ReactSoftExceptionLogger.logSoftException(str, new IllegalStateException("Cannot double-destroy FabricUIManager"));
            return;
        }
        this.mDestroyed = true;
        this.mDispatchUIFrameCallback.stop();
        this.mEventDispatcher.removeBatchEventDispatchedListener(this.mEventBeatManager);
        this.mEventDispatcher.unregisterEventEmitter(2);
        this.mReactApplicationContext.removeLifecycleEventListener(this);
        onHostPause();
        this.mDispatchUIFrameCallback.stop();
        this.mBinding.unregister();
        this.mBinding = null;
        ViewManagerPropertyUpdater.clear();
        if (!this.mShouldDeallocateEventDispatcher) {
            return;
        }
        this.mEventDispatcher.onCatalystInstanceDestroyed();
    }

    private NativeArray measureLines(ReadableMap attributedString, ReadableMap paragraphAttributes, float width, float height) {
        return (NativeArray) TextLayoutManager.measureLines(this.mReactApplicationContext, attributedString, paragraphAttributes, PixelUtil.toPixelFromDIP(width));
    }

    private NativeArray measureLinesMapBuffer(ReadableMapBuffer attributedString, ReadableMapBuffer paragraphAttributes, float width, float height) {
        return (NativeArray) TextLayoutManagerMapBuffer.measureLines(this.mReactApplicationContext, attributedString, paragraphAttributes, PixelUtil.toPixelFromDIP(width));
    }

    private long measure(int rootTag, String componentName, ReadableMap localData, ReadableMap props, ReadableMap state, float minWidth, float maxWidth, float minHeight, float maxHeight) {
        return measure(rootTag, componentName, localData, props, state, minWidth, maxWidth, minHeight, maxHeight, null);
    }

    public int getColor(int surfaceId, ReadableMap platformColor) {
        Integer color = ColorPropConverter.getColor(platformColor, this.mMountingManager.getSurfaceManagerEnforced(surfaceId, "getColor").getContext());
        if (color != null) {
            return color.intValue();
        }
        return 0;
    }

    private long measure(int surfaceId, String componentName, ReadableMap localData, ReadableMap props, ReadableMap state, float minWidth, float maxWidth, float minHeight, float maxHeight, float[] attachmentsPositions) {
        ReactContext reactContext;
        if (surfaceId > 0) {
            SurfaceMountingManager surfaceManagerEnforced = this.mMountingManager.getSurfaceManagerEnforced(surfaceId, "measure");
            if (surfaceManagerEnforced.isStopped()) {
                return 0L;
            }
            reactContext = surfaceManagerEnforced.getContext();
        } else {
            reactContext = this.mReactApplicationContext;
        }
        return this.mMountingManager.measure(reactContext, componentName, localData, props, state, LayoutMetricsConversions.getYogaSize(minWidth, maxWidth), LayoutMetricsConversions.getYogaMeasureMode(minWidth, maxWidth), LayoutMetricsConversions.getYogaSize(minHeight, maxHeight), LayoutMetricsConversions.getYogaMeasureMode(minHeight, maxHeight), attachmentsPositions);
    }

    private long measureMapBuffer(int surfaceId, String componentName, ReadableMapBuffer attributedString, ReadableMapBuffer paragraphAttributes, float minWidth, float maxWidth, float minHeight, float maxHeight, float[] attachmentsPositions) {
        ReactContext reactContext;
        if (surfaceId > 0) {
            SurfaceMountingManager surfaceManagerEnforced = this.mMountingManager.getSurfaceManagerEnforced(surfaceId, "measure");
            if (surfaceManagerEnforced.isStopped()) {
                return 0L;
            }
            reactContext = surfaceManagerEnforced.getContext();
        } else {
            reactContext = this.mReactApplicationContext;
        }
        return this.mMountingManager.measureTextMapBuffer(reactContext, componentName, attributedString, paragraphAttributes, LayoutMetricsConversions.getYogaSize(minWidth, maxWidth), LayoutMetricsConversions.getYogaMeasureMode(minWidth, maxWidth), LayoutMetricsConversions.getYogaSize(minHeight, maxHeight), LayoutMetricsConversions.getYogaMeasureMode(minHeight, maxHeight), attachmentsPositions);
    }

    public boolean getThemeData(int surfaceId, float[] defaultTextInputPadding) {
        float[] defaultTextInputPadding2 = UIManagerHelper.getDefaultTextInputPadding(this.mMountingManager.getSurfaceManagerEnforced(surfaceId, "getThemeData").getContext());
        defaultTextInputPadding[0] = defaultTextInputPadding2[0];
        defaultTextInputPadding[1] = defaultTextInputPadding2[1];
        defaultTextInputPadding[2] = defaultTextInputPadding2[2];
        defaultTextInputPadding[3] = defaultTextInputPadding2[3];
        return true;
    }

    @Override // com.facebook.react.bridge.UIManager
    public void addUIManagerEventListener(UIManagerListener listener) {
        this.mListeners.add(listener);
    }

    @Override // com.facebook.react.bridge.UIManager
    public void removeUIManagerEventListener(UIManagerListener listener) {
        this.mListeners.remove(listener);
    }

    @Override // com.facebook.react.bridge.UIManager
    public void synchronouslyUpdateViewOnUIThread(final int reactTag, final ReadableMap props) {
        UiThreadUtil.assertOnUiThread();
        int i = this.mCurrentSynchronousCommitNumber;
        this.mCurrentSynchronousCommitNumber = i + 1;
        MountItem mountItem = new MountItem() { // from class: com.facebook.react.fabric.FabricUIManager.2
            @Override // com.facebook.react.fabric.mounting.mountitems.MountItem
            public int getSurfaceId() {
                return -1;
            }

            @Override // com.facebook.react.fabric.mounting.mountitems.MountItem
            public void execute(MountingManager mountingManager) {
                try {
                    mountingManager.updateProps(reactTag, props);
                } catch (Exception unused) {
                }
            }
        };
        if (!this.mMountingManager.getViewExists(reactTag)) {
            this.mMountItemDispatcher.addMountItem(mountItem);
            return;
        }
        ReactMarker.logFabricMarker(ReactMarkerConstants.FABRIC_UPDATE_UI_MAIN_THREAD_START, null, i);
        if (ENABLE_FABRIC_LOGS) {
            FLog.d(TAG, "SynchronouslyUpdateViewOnUIThread for tag %d: %s", Integer.valueOf(reactTag), "<hidden>");
        }
        mountItem.execute(this.mMountingManager);
        ReactMarker.logFabricMarker(ReactMarkerConstants.FABRIC_UPDATE_UI_MAIN_THREAD_END, null, i);
    }

    private void preallocateView(int rootTag, int reactTag, final String componentName, ReadableMap props, Object stateWrapper, Object eventEmitterWrapper, boolean isLayoutable) {
        this.mMountItemDispatcher.addPreAllocateMountItem(new PreAllocateViewMountItem(rootTag, reactTag, FabricComponents.getFabricComponentName(componentName), props, (StateWrapper) stateWrapper, (EventEmitterWrapper) eventEmitterWrapper, isLayoutable));
    }

    private MountItem createIntBufferBatchMountItem(int rootTag, int[] intBuffer, Object[] objBuffer, int commitNumber) {
        return new IntBufferBatchMountItem(rootTag, intBuffer, objBuffer, commitNumber);
    }

    private void scheduleMountItem(final MountItem mountItem, int commitNumber, long commitStartTime, long diffStartTime, long diffEndTime, long layoutStartTime, long layoutEndTime, long finishTransactionStartTime, long finishTransactionEndTime) {
        boolean z = mountItem instanceof IntBufferBatchMountItem;
        boolean z2 = (z && ((IntBufferBatchMountItem) mountItem).shouldSchedule()) || (!z && mountItem != null);
        for (Iterator<UIManagerListener> it = this.mListeners.iterator(); it.hasNext(); it = it) {
            it.next().didScheduleMountItems(this);
        }
        if (z) {
            this.mCommitStartTime = commitStartTime;
            this.mLayoutTime = layoutEndTime - layoutStartTime;
            this.mFinishTransactionCPPTime = finishTransactionEndTime - finishTransactionStartTime;
            this.mFinishTransactionTime = SystemClock.uptimeMillis() - finishTransactionStartTime;
            this.mDispatchViewUpdatesTime = SystemClock.uptimeMillis();
        }
        if (z2) {
            this.mMountItemDispatcher.addMountItem(mountItem);
            if (UiThreadUtil.isOnUiThread()) {
                this.mMountItemDispatcher.tryDispatchMountItems();
            }
        }
        if (z) {
            ReactMarker.logFabricMarker(ReactMarkerConstants.FABRIC_COMMIT_START, null, commitNumber, commitStartTime);
            ReactMarker.logFabricMarker(ReactMarkerConstants.FABRIC_FINISH_TRANSACTION_START, null, commitNumber, finishTransactionStartTime);
            ReactMarker.logFabricMarker(ReactMarkerConstants.FABRIC_FINISH_TRANSACTION_END, null, commitNumber, finishTransactionEndTime);
            ReactMarker.logFabricMarker(ReactMarkerConstants.FABRIC_DIFF_START, null, commitNumber, diffStartTime);
            ReactMarker.logFabricMarker(ReactMarkerConstants.FABRIC_DIFF_END, null, commitNumber, diffEndTime);
            ReactMarker.logFabricMarker(ReactMarkerConstants.FABRIC_LAYOUT_START, null, commitNumber, layoutStartTime);
            ReactMarker.logFabricMarker(ReactMarkerConstants.FABRIC_LAYOUT_END, null, commitNumber, layoutEndTime);
            ReactMarker.logFabricMarker(ReactMarkerConstants.FABRIC_COMMIT_END, null, commitNumber);
        }
    }

    public void setBinding(Binding binding) {
        this.mBinding = binding;
    }

    @Override // com.facebook.react.bridge.UIManager
    public void updateRootLayoutSpecs(final int surfaceId, final int widthMeasureSpec, final int heightMeasureSpec, final int offsetX, final int offsetY) {
        boolean z;
        boolean z2;
        if (ENABLE_FABRIC_LOGS) {
            FLog.d(TAG, "Updating Root Layout Specs for [%d]", Integer.valueOf(surfaceId));
        }
        SurfaceMountingManager surfaceManager = this.mMountingManager.getSurfaceManager(surfaceId);
        if (surfaceManager == null) {
            String str = TAG;
            ReactSoftExceptionLogger.logSoftException(str, new IllegalViewOperationException("Cannot updateRootLayoutSpecs on surfaceId that does not exist: " + surfaceId));
            return;
        }
        ThemedReactContext context = surfaceManager.getContext();
        if (context != null) {
            boolean isRTL = I18nUtil.getInstance().isRTL(context);
            z = I18nUtil.getInstance().doLeftAndRightSwapInRTL(context);
            z2 = isRTL;
        } else {
            z2 = false;
            z = false;
        }
        this.mBinding.setConstraints(surfaceId, LayoutMetricsConversions.getMinSize(widthMeasureSpec), LayoutMetricsConversions.getMaxSize(widthMeasureSpec), LayoutMetricsConversions.getMinSize(heightMeasureSpec), LayoutMetricsConversions.getMaxSize(heightMeasureSpec), offsetX, offsetY, z2, z);
    }

    @Override // com.facebook.react.bridge.UIManager
    public View resolveView(int reactTag) {
        UiThreadUtil.assertOnUiThread();
        SurfaceMountingManager surfaceManagerForView = this.mMountingManager.getSurfaceManagerForView(reactTag);
        if (surfaceManagerForView == null) {
            return null;
        }
        return surfaceManagerForView.getView(reactTag);
    }

    @Override // com.facebook.react.bridge.UIManager
    public void receiveEvent(int reactTag, String eventName, WritableMap params) {
        receiveEvent(-1, reactTag, eventName, params);
    }

    @Override // com.facebook.react.bridge.UIManager
    public void receiveEvent(int surfaceId, int reactTag, String eventName, WritableMap params) {
        receiveEvent(surfaceId, reactTag, eventName, false, 0, params);
    }

    public void receiveEvent(int surfaceId, int reactTag, String eventName, boolean canCoalesceEvent, int customCoalesceKey, WritableMap params) {
        if (this.mDestroyed) {
            FLog.e(TAG, "Attempted to receiveEvent after destruction");
            return;
        }
        EventEmitterWrapper eventEmitter = this.mMountingManager.getEventEmitter(surfaceId, reactTag);
        if (eventEmitter != null) {
            if (canCoalesceEvent) {
                eventEmitter.invokeUnique(eventName, params, customCoalesceKey);
                return;
            } else {
                eventEmitter.invoke(eventName, params);
                return;
            }
        }
        String str = TAG;
        FLog.d(str, "Unable to invoke event: " + eventName + " for reactTag: " + reactTag);
    }

    @Override // com.facebook.react.bridge.LifecycleEventListener
    public void onHostResume() {
        ReactChoreographer.getInstance().postFrameCallback(ReactChoreographer.CallbackType.DISPATCH_UI, this.mDispatchUIFrameCallback);
    }

    @Override // com.facebook.react.bridge.UIManager
    public EventDispatcher getEventDispatcher() {
        return this.mEventDispatcher;
    }

    @Override // com.facebook.react.bridge.LifecycleEventListener
    public void onHostPause() {
        ReactChoreographer.getInstance().removeFrameCallback(ReactChoreographer.CallbackType.DISPATCH_UI, this.mDispatchUIFrameCallback);
    }

    @Override // com.facebook.react.bridge.UIManager
    @Deprecated
    public void dispatchCommand(final int reactTag, final int commandId, final ReadableArray commandArgs) {
        throw new UnsupportedOperationException("dispatchCommand called without surfaceId - Fabric dispatchCommand must be called through Fabric JSI API");
    }

    @Override // com.facebook.react.bridge.UIManager
    @Deprecated
    public void dispatchCommand(final int reactTag, final String commandId, final ReadableArray commandArgs) {
        throw new UnsupportedOperationException("dispatchCommand called without surfaceId - Fabric dispatchCommand must be called through Fabric JSI API");
    }

    @Deprecated
    public void dispatchCommand(final int surfaceId, final int reactTag, final int commandId, final ReadableArray commandArgs) {
        this.mMountItemDispatcher.dispatchCommandMountItem(new DispatchIntCommandMountItem(surfaceId, reactTag, commandId, commandArgs));
    }

    public void dispatchCommand(final int surfaceId, final int reactTag, final String commandId, final ReadableArray commandArgs) {
        this.mMountItemDispatcher.dispatchCommandMountItem(new DispatchStringCommandMountItem(surfaceId, reactTag, commandId, commandArgs));
    }

    @Override // com.facebook.react.bridge.UIManager
    public void sendAccessibilityEvent(int reactTag, int eventType) {
        this.mMountItemDispatcher.addMountItem(new SendAccessibilityEvent(-1, reactTag, eventType));
    }

    public void sendAccessibilityEventFromJS(int surfaceId, int reactTag, String eventTypeJS) {
        int i;
        if ("focus".equals(eventTypeJS)) {
            i = 8;
        } else if ("windowStateChange".equals(eventTypeJS)) {
            i = 32;
        } else if (!"click".equals(eventTypeJS)) {
            throw new IllegalArgumentException("sendAccessibilityEventFromJS: invalid eventType " + eventTypeJS);
        } else {
            i = 1;
        }
        this.mMountItemDispatcher.addMountItem(new SendAccessibilityEvent(surfaceId, reactTag, i));
    }

    public void setJSResponder(final int surfaceId, final int reactTag, final int initialReactTag, final boolean blockNativeResponder) {
        this.mMountItemDispatcher.addMountItem(new MountItem() { // from class: com.facebook.react.fabric.FabricUIManager.3
            @Override // com.facebook.react.fabric.mounting.mountitems.MountItem
            public void execute(MountingManager mountingManager) {
                SurfaceMountingManager surfaceManager = mountingManager.getSurfaceManager(surfaceId);
                if (surfaceManager != null) {
                    surfaceManager.setJSResponder(reactTag, initialReactTag, blockNativeResponder);
                    return;
                }
                String str = FabricUIManager.TAG;
                FLog.e(str, "setJSResponder skipped, surface no longer available [" + surfaceId + "]");
            }

            @Override // com.facebook.react.fabric.mounting.mountitems.MountItem
            public int getSurfaceId() {
                return surfaceId;
            }
        });
    }

    public void clearJSResponder() {
        this.mMountItemDispatcher.addMountItem(new MountItem() { // from class: com.facebook.react.fabric.FabricUIManager.4
            @Override // com.facebook.react.fabric.mounting.mountitems.MountItem
            public int getSurfaceId() {
                return -1;
            }

            @Override // com.facebook.react.fabric.mounting.mountitems.MountItem
            public void execute(MountingManager mountingManager) {
                mountingManager.clearJSResponder();
            }
        });
    }

    @Override // com.facebook.react.bridge.UIManager
    @Deprecated
    public String resolveCustomDirectEventName(String eventName) {
        if (eventName == null) {
            return null;
        }
        if (!eventName.startsWith(ViewProps.TOP)) {
            return eventName;
        }
        return ViewProps.ON + eventName.substring(3);
    }

    public void onAnimationStarted() {
        this.mDriveCxxAnimations = true;
    }

    public void onAllAnimationsComplete() {
        this.mDriveCxxAnimations = false;
    }

    @Override // com.facebook.react.bridge.PerformanceCounter
    public Map<String, Long> getPerformanceCounters() {
        HashMap hashMap = new HashMap();
        hashMap.put("CommitStartTime", Long.valueOf(this.mCommitStartTime));
        hashMap.put("LayoutTime", Long.valueOf(this.mLayoutTime));
        hashMap.put("DispatchViewUpdatesTime", Long.valueOf(this.mDispatchViewUpdatesTime));
        hashMap.put("RunStartTime", Long.valueOf(this.mMountItemDispatcher.getRunStartTime()));
        hashMap.put("BatchedExecutionTime", Long.valueOf(this.mMountItemDispatcher.getBatchedExecutionTime()));
        hashMap.put("FinishFabricTransactionTime", Long.valueOf(this.mFinishTransactionTime));
        hashMap.put("FinishFabricTransactionCPPTime", Long.valueOf(this.mFinishTransactionCPPTime));
        return hashMap;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class MountItemDispatchListener implements MountItemDispatcher.ItemDispatchListener {
        private MountItemDispatchListener() {
            FabricUIManager.this = this$0;
        }

        @Override // com.facebook.react.fabric.mounting.MountItemDispatcher.ItemDispatchListener
        public void didDispatchMountItems() {
            Iterator it = FabricUIManager.this.mListeners.iterator();
            while (it.hasNext()) {
                ((UIManagerListener) it.next()).didDispatchMountItems(FabricUIManager.this);
            }
        }
    }

    /* loaded from: classes.dex */
    public class DispatchUIFrameCallback extends GuardedFrameCallback {
        private volatile boolean mIsMountingEnabled;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        private DispatchUIFrameCallback(ReactContext reactContext) {
            super(reactContext);
            FabricUIManager.this = this$0;
            this.mIsMountingEnabled = true;
        }

        void stop() {
            this.mIsMountingEnabled = false;
        }

        @Override // com.facebook.react.fabric.GuardedFrameCallback
        public void doFrameGuarded(long frameTimeNanos) {
            if (this.mIsMountingEnabled && !FabricUIManager.this.mDestroyed) {
                if (FabricUIManager.this.mDriveCxxAnimations && FabricUIManager.this.mBinding != null) {
                    FabricUIManager.this.mBinding.driveCxxAnimations();
                }
                try {
                    try {
                        FabricUIManager.this.mMountItemDispatcher.dispatchPreMountItems(frameTimeNanos);
                        FabricUIManager.this.mMountItemDispatcher.tryDispatchMountItems();
                        return;
                    } catch (Exception e) {
                        FLog.e(FabricUIManager.TAG, "Exception thrown when executing UIFrameGuarded", e);
                        stop();
                        throw e;
                    }
                } finally {
                    ReactChoreographer.getInstance().postFrameCallback(ReactChoreographer.CallbackType.DISPATCH_UI, FabricUIManager.this.mDispatchUIFrameCallback);
                }
            }
            FLog.w(FabricUIManager.TAG, "Not flushing pending UI operations because of previously thrown Exception");
        }
    }
}
