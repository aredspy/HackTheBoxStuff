package com.facebook.react.animated;

import com.facebook.fbreact.specs.NativeAnimatedModuleSpec;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactSoftExceptionLogger;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.UIManager;
import com.facebook.react.bridge.UIManagerListener;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.modules.core.ReactChoreographer;
import com.facebook.react.uimanager.GuardedFrameCallback;
import com.facebook.react.uimanager.NativeViewHierarchyManager;
import com.facebook.react.uimanager.UIBlock;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.common.ViewUtil;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;
@ReactModule(name = NativeAnimatedModule.NAME)
/* loaded from: classes.dex */
public class NativeAnimatedModule extends NativeAnimatedModuleSpec implements LifecycleEventListener, UIManagerListener {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public static final boolean ANIMATED_MODULE_DEBUG = false;
    public static final String NAME = "NativeAnimatedModule";
    private final GuardedFrameCallback mAnimatedFrameCallback;
    private volatile long mCurrentBatchNumber;
    private volatile long mCurrentFrameNumber;
    private final ConcurrentLinkedQueue<UIThreadOperation> mOperations = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<UIThreadOperation> mPreOperations = new ConcurrentLinkedQueue<>();
    private final AtomicReference<NativeAnimatedNodesManager> mNodesManager = new AtomicReference<>();
    private boolean mBatchingControlledByJS = false;
    private boolean mInitializedForFabric = false;
    private boolean mInitializedForNonFabric = false;
    private int mUIManagerType = 1;
    private int mNumFabricAnimations = 0;
    private int mNumNonFabricAnimations = 0;
    private final ReactChoreographer mReactChoreographer = ReactChoreographer.getInstance();

    @Override // com.facebook.fbreact.specs.NativeAnimatedModuleSpec
    public void addListener(String eventName) {
    }

    @Override // com.facebook.react.bridge.NativeModule
    public String getName() {
        return NAME;
    }

    @Override // com.facebook.fbreact.specs.NativeAnimatedModuleSpec
    public void removeListeners(double count) {
    }

    /* loaded from: classes.dex */
    public abstract class UIThreadOperation {
        long mBatchNumber;

        abstract void execute(NativeAnimatedNodesManager animatedNodesManager);

        private UIThreadOperation() {
            NativeAnimatedModule.this = this$0;
            this.mBatchNumber = -1L;
        }

        public void setBatchNumber(long batchNumber) {
            this.mBatchNumber = batchNumber;
        }

        public long getBatchNumber() {
            return this.mBatchNumber;
        }
    }

    public NativeAnimatedModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.mAnimatedFrameCallback = new GuardedFrameCallback(reactContext) { // from class: com.facebook.react.animated.NativeAnimatedModule.1
            @Override // com.facebook.react.uimanager.GuardedFrameCallback
            protected void doFrameGuarded(final long frameTimeNanos) {
                try {
                    NativeAnimatedNodesManager nodesManager = NativeAnimatedModule.this.getNodesManager();
                    if (nodesManager != null && nodesManager.hasActiveAnimations()) {
                        nodesManager.runUpdates(frameTimeNanos);
                    }
                    if (nodesManager == null && NativeAnimatedModule.this.mReactChoreographer == null) {
                        return;
                    }
                    ((ReactChoreographer) Assertions.assertNotNull(NativeAnimatedModule.this.mReactChoreographer)).postFrameCallback(ReactChoreographer.CallbackType.NATIVE_ANIMATED_MODULE, NativeAnimatedModule.this.mAnimatedFrameCallback);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Override // com.facebook.react.bridge.BaseJavaModule, com.facebook.react.bridge.NativeModule, com.facebook.react.turbomodule.core.interfaces.TurboModule
    public void initialize() {
        ReactApplicationContext reactApplicationContextIfActiveOrWarn = getReactApplicationContextIfActiveOrWarn();
        if (reactApplicationContextIfActiveOrWarn != null) {
            reactApplicationContextIfActiveOrWarn.addLifecycleEventListener(this);
        }
    }

    @Override // com.facebook.react.bridge.LifecycleEventListener
    public void onHostResume() {
        enqueueFrameCallback();
    }

    private void addOperation(UIThreadOperation operation) {
        operation.setBatchNumber(this.mCurrentBatchNumber);
        this.mOperations.add(operation);
    }

    private void addUnbatchedOperation(UIThreadOperation operation) {
        operation.setBatchNumber(-1L);
        this.mOperations.add(operation);
    }

    private void addPreOperation(UIThreadOperation operation) {
        operation.setBatchNumber(this.mCurrentBatchNumber);
        this.mPreOperations.add(operation);
    }

    @Override // com.facebook.react.bridge.UIManagerListener
    public void didScheduleMountItems(UIManager uiManager) {
        this.mCurrentFrameNumber++;
    }

    @Override // com.facebook.react.bridge.UIManagerListener
    public void didDispatchMountItems(UIManager uiManager) {
        if (this.mUIManagerType != 2) {
            return;
        }
        long j = this.mCurrentBatchNumber - 1;
        if (!this.mBatchingControlledByJS) {
            this.mCurrentFrameNumber++;
            if (this.mCurrentFrameNumber - this.mCurrentBatchNumber > 2) {
                this.mCurrentBatchNumber = this.mCurrentFrameNumber;
                j = this.mCurrentBatchNumber;
            }
        }
        executeAllOperations(this.mPreOperations, j);
        executeAllOperations(this.mOperations, j);
    }

    public void executeAllOperations(Queue<UIThreadOperation> operationQueue, long maxBatchNumber) {
        UIThreadOperation poll;
        NativeAnimatedNodesManager nodesManager = getNodesManager();
        while (true) {
            UIThreadOperation peek = operationQueue.peek();
            if (peek == null || peek.getBatchNumber() > maxBatchNumber || (poll = operationQueue.poll()) == null) {
                return;
            }
            poll.execute(nodesManager);
        }
    }

    @Override // com.facebook.react.bridge.UIManagerListener
    public void willDispatchViewUpdates(final UIManager uiManager) {
        if ((!this.mOperations.isEmpty() || !this.mPreOperations.isEmpty()) && this.mUIManagerType != 2) {
            final long j = this.mCurrentBatchNumber;
            this.mCurrentBatchNumber = 1 + j;
            UIBlock uIBlock = new UIBlock() { // from class: com.facebook.react.animated.NativeAnimatedModule.2
                @Override // com.facebook.react.uimanager.UIBlock
                public void execute(NativeViewHierarchyManager nativeViewHierarchyManager) {
                    NativeAnimatedModule nativeAnimatedModule = NativeAnimatedModule.this;
                    nativeAnimatedModule.executeAllOperations(nativeAnimatedModule.mPreOperations, j);
                }
            };
            UIBlock uIBlock2 = new UIBlock() { // from class: com.facebook.react.animated.NativeAnimatedModule.3
                @Override // com.facebook.react.uimanager.UIBlock
                public void execute(NativeViewHierarchyManager nativeViewHierarchyManager) {
                    NativeAnimatedModule nativeAnimatedModule = NativeAnimatedModule.this;
                    nativeAnimatedModule.executeAllOperations(nativeAnimatedModule.mOperations, j);
                }
            };
            UIManagerModule uIManagerModule = (UIManagerModule) uiManager;
            uIManagerModule.prependUIBlock(uIBlock);
            uIManagerModule.addUIBlock(uIBlock2);
        }
    }

    @Override // com.facebook.react.bridge.LifecycleEventListener
    public void onHostPause() {
        clearFrameCallback();
    }

    @Override // com.facebook.react.bridge.LifecycleEventListener
    public void onHostDestroy() {
        clearFrameCallback();
    }

    public NativeAnimatedNodesManager getNodesManager() {
        ReactApplicationContext reactApplicationContextIfActiveOrWarn;
        if (this.mNodesManager.get() == null && (reactApplicationContextIfActiveOrWarn = getReactApplicationContextIfActiveOrWarn()) != null) {
            this.mNodesManager.compareAndSet(null, new NativeAnimatedNodesManager(reactApplicationContextIfActiveOrWarn));
        }
        return this.mNodesManager.get();
    }

    private void clearFrameCallback() {
        ((ReactChoreographer) Assertions.assertNotNull(this.mReactChoreographer)).removeFrameCallback(ReactChoreographer.CallbackType.NATIVE_ANIMATED_MODULE, this.mAnimatedFrameCallback);
    }

    private void enqueueFrameCallback() {
        ((ReactChoreographer) Assertions.assertNotNull(this.mReactChoreographer)).postFrameCallback(ReactChoreographer.CallbackType.NATIVE_ANIMATED_MODULE, this.mAnimatedFrameCallback);
    }

    public void setNodesManager(NativeAnimatedNodesManager nodesManager) {
        this.mNodesManager.set(nodesManager);
    }

    private void initializeLifecycleEventListenersForViewTag(final int viewTag) {
        ReactApplicationContext reactApplicationContext;
        UIManager uIManager;
        int uIManagerType = ViewUtil.getUIManagerType(viewTag);
        this.mUIManagerType = uIManagerType;
        if (uIManagerType == 2) {
            this.mNumFabricAnimations++;
        } else {
            this.mNumNonFabricAnimations++;
        }
        NativeAnimatedNodesManager nodesManager = getNodesManager();
        if (nodesManager != null) {
            nodesManager.initializeEventListenerForUIManagerType(this.mUIManagerType);
        } else {
            ReactSoftExceptionLogger.logSoftException(NAME, new RuntimeException("initializeLifecycleEventListenersForViewTag could not get NativeAnimatedNodesManager"));
        }
        if (!this.mInitializedForFabric || this.mUIManagerType != 2) {
            if ((this.mInitializedForNonFabric && this.mUIManagerType == 1) || (reactApplicationContext = getReactApplicationContext()) == null || (uIManager = UIManagerHelper.getUIManager(reactApplicationContext, this.mUIManagerType)) == null) {
                return;
            }
            uIManager.addUIManagerEventListener(this);
            if (this.mUIManagerType == 2) {
                this.mInitializedForFabric = true;
            } else {
                this.mInitializedForNonFabric = true;
            }
        }
    }

    private void decrementInFlightAnimationsForViewTag(final int viewTag) {
        if (ViewUtil.getUIManagerType(viewTag) == 2) {
            this.mNumFabricAnimations--;
        } else {
            this.mNumNonFabricAnimations--;
        }
        int i = this.mNumNonFabricAnimations;
        if (i == 0 && this.mNumFabricAnimations > 0 && this.mUIManagerType != 2) {
            this.mUIManagerType = 2;
        } else if (this.mNumFabricAnimations != 0 || i <= 0 || this.mUIManagerType == 1) {
        } else {
            this.mUIManagerType = 1;
        }
    }

    @Override // com.facebook.fbreact.specs.NativeAnimatedModuleSpec
    public void startOperationBatch() {
        this.mBatchingControlledByJS = true;
        this.mCurrentBatchNumber++;
    }

    @Override // com.facebook.fbreact.specs.NativeAnimatedModuleSpec
    public void finishOperationBatch() {
        this.mBatchingControlledByJS = true;
        this.mCurrentBatchNumber++;
    }

    @Override // com.facebook.fbreact.specs.NativeAnimatedModuleSpec
    public void createAnimatedNode(final double tagDouble, final ReadableMap config) {
        final int i = (int) tagDouble;
        addOperation(new UIThreadOperation() { // from class: com.facebook.react.animated.NativeAnimatedModule.4
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super();
                NativeAnimatedModule.this = this;
            }

            @Override // com.facebook.react.animated.NativeAnimatedModule.UIThreadOperation
            public void execute(NativeAnimatedNodesManager animatedNodesManager) {
                animatedNodesManager.createAnimatedNode(i, config);
            }
        });
    }

    @Override // com.facebook.fbreact.specs.NativeAnimatedModuleSpec
    public void startListeningToAnimatedNodeValue(final double tagDouble) {
        final int i = (int) tagDouble;
        final AnimatedNodeValueListener animatedNodeValueListener = new AnimatedNodeValueListener() { // from class: com.facebook.react.animated.NativeAnimatedModule.5
            @Override // com.facebook.react.animated.AnimatedNodeValueListener
            public void onValueUpdate(double value) {
                WritableMap createMap = Arguments.createMap();
                createMap.putInt("tag", i);
                createMap.putDouble("value", value);
                ReactApplicationContext reactApplicationContextIfActiveOrWarn = NativeAnimatedModule.this.getReactApplicationContextIfActiveOrWarn();
                if (reactApplicationContextIfActiveOrWarn != null) {
                    ((DeviceEventManagerModule.RCTDeviceEventEmitter) reactApplicationContextIfActiveOrWarn.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)).emit("onAnimatedValueUpdate", createMap);
                }
            }
        };
        addOperation(new UIThreadOperation() { // from class: com.facebook.react.animated.NativeAnimatedModule.6
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super();
                NativeAnimatedModule.this = this;
            }

            @Override // com.facebook.react.animated.NativeAnimatedModule.UIThreadOperation
            public void execute(NativeAnimatedNodesManager animatedNodesManager) {
                animatedNodesManager.startListeningToAnimatedNodeValue(i, animatedNodeValueListener);
            }
        });
    }

    @Override // com.facebook.fbreact.specs.NativeAnimatedModuleSpec
    public void stopListeningToAnimatedNodeValue(final double tagDouble) {
        final int i = (int) tagDouble;
        addOperation(new UIThreadOperation() { // from class: com.facebook.react.animated.NativeAnimatedModule.7
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super();
                NativeAnimatedModule.this = this;
            }

            @Override // com.facebook.react.animated.NativeAnimatedModule.UIThreadOperation
            public void execute(NativeAnimatedNodesManager animatedNodesManager) {
                animatedNodesManager.stopListeningToAnimatedNodeValue(i);
            }
        });
    }

    @Override // com.facebook.fbreact.specs.NativeAnimatedModuleSpec
    public void dropAnimatedNode(final double tagDouble) {
        final int i = (int) tagDouble;
        addOperation(new UIThreadOperation() { // from class: com.facebook.react.animated.NativeAnimatedModule.8
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super();
                NativeAnimatedModule.this = this;
            }

            @Override // com.facebook.react.animated.NativeAnimatedModule.UIThreadOperation
            public void execute(NativeAnimatedNodesManager animatedNodesManager) {
                animatedNodesManager.dropAnimatedNode(i);
            }
        });
    }

    @Override // com.facebook.fbreact.specs.NativeAnimatedModuleSpec
    public void setAnimatedNodeValue(final double tagDouble, final double value) {
        final int i = (int) tagDouble;
        addOperation(new UIThreadOperation() { // from class: com.facebook.react.animated.NativeAnimatedModule.9
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super();
                NativeAnimatedModule.this = this;
            }

            @Override // com.facebook.react.animated.NativeAnimatedModule.UIThreadOperation
            public void execute(NativeAnimatedNodesManager animatedNodesManager) {
                animatedNodesManager.setAnimatedNodeValue(i, value);
            }
        });
    }

    @Override // com.facebook.fbreact.specs.NativeAnimatedModuleSpec
    public void setAnimatedNodeOffset(final double tagDouble, final double value) {
        final int i = (int) tagDouble;
        addOperation(new UIThreadOperation() { // from class: com.facebook.react.animated.NativeAnimatedModule.10
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super();
                NativeAnimatedModule.this = this;
            }

            @Override // com.facebook.react.animated.NativeAnimatedModule.UIThreadOperation
            public void execute(NativeAnimatedNodesManager animatedNodesManager) {
                animatedNodesManager.setAnimatedNodeOffset(i, value);
            }
        });
    }

    @Override // com.facebook.fbreact.specs.NativeAnimatedModuleSpec
    public void flattenAnimatedNodeOffset(final double tagDouble) {
        final int i = (int) tagDouble;
        addOperation(new UIThreadOperation() { // from class: com.facebook.react.animated.NativeAnimatedModule.11
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super();
                NativeAnimatedModule.this = this;
            }

            @Override // com.facebook.react.animated.NativeAnimatedModule.UIThreadOperation
            public void execute(NativeAnimatedNodesManager animatedNodesManager) {
                animatedNodesManager.flattenAnimatedNodeOffset(i);
            }
        });
    }

    @Override // com.facebook.fbreact.specs.NativeAnimatedModuleSpec
    public void extractAnimatedNodeOffset(final double tagDouble) {
        final int i = (int) tagDouble;
        addOperation(new UIThreadOperation() { // from class: com.facebook.react.animated.NativeAnimatedModule.12
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super();
                NativeAnimatedModule.this = this;
            }

            @Override // com.facebook.react.animated.NativeAnimatedModule.UIThreadOperation
            public void execute(NativeAnimatedNodesManager animatedNodesManager) {
                animatedNodesManager.extractAnimatedNodeOffset(i);
            }
        });
    }

    @Override // com.facebook.fbreact.specs.NativeAnimatedModuleSpec
    public void startAnimatingNode(final double animationIdDouble, final double animatedNodeTagDouble, final ReadableMap animationConfig, final Callback endCallback) {
        final int i = (int) animationIdDouble;
        final int i2 = (int) animatedNodeTagDouble;
        addUnbatchedOperation(new UIThreadOperation() { // from class: com.facebook.react.animated.NativeAnimatedModule.13
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super();
                NativeAnimatedModule.this = this;
            }

            @Override // com.facebook.react.animated.NativeAnimatedModule.UIThreadOperation
            public void execute(NativeAnimatedNodesManager animatedNodesManager) {
                animatedNodesManager.startAnimatingNode(i, i2, animationConfig, endCallback);
            }
        });
    }

    @Override // com.facebook.fbreact.specs.NativeAnimatedModuleSpec
    public void stopAnimation(final double animationIdDouble) {
        final int i = (int) animationIdDouble;
        addOperation(new UIThreadOperation() { // from class: com.facebook.react.animated.NativeAnimatedModule.14
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super();
                NativeAnimatedModule.this = this;
            }

            @Override // com.facebook.react.animated.NativeAnimatedModule.UIThreadOperation
            public void execute(NativeAnimatedNodesManager animatedNodesManager) {
                animatedNodesManager.stopAnimation(i);
            }
        });
    }

    @Override // com.facebook.fbreact.specs.NativeAnimatedModuleSpec
    public void connectAnimatedNodes(final double parentNodeTagDouble, final double childNodeTagDouble) {
        final int i = (int) parentNodeTagDouble;
        final int i2 = (int) childNodeTagDouble;
        addOperation(new UIThreadOperation() { // from class: com.facebook.react.animated.NativeAnimatedModule.15
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super();
                NativeAnimatedModule.this = this;
            }

            @Override // com.facebook.react.animated.NativeAnimatedModule.UIThreadOperation
            public void execute(NativeAnimatedNodesManager animatedNodesManager) {
                animatedNodesManager.connectAnimatedNodes(i, i2);
            }
        });
    }

    @Override // com.facebook.fbreact.specs.NativeAnimatedModuleSpec
    public void disconnectAnimatedNodes(final double parentNodeTagDouble, final double childNodeTagDouble) {
        final int i = (int) parentNodeTagDouble;
        final int i2 = (int) childNodeTagDouble;
        addOperation(new UIThreadOperation() { // from class: com.facebook.react.animated.NativeAnimatedModule.16
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super();
                NativeAnimatedModule.this = this;
            }

            @Override // com.facebook.react.animated.NativeAnimatedModule.UIThreadOperation
            public void execute(NativeAnimatedNodesManager animatedNodesManager) {
                animatedNodesManager.disconnectAnimatedNodes(i, i2);
            }
        });
    }

    @Override // com.facebook.fbreact.specs.NativeAnimatedModuleSpec
    public void connectAnimatedNodeToView(final double animatedNodeTagDouble, final double viewTagDouble) {
        final int i = (int) animatedNodeTagDouble;
        final int i2 = (int) viewTagDouble;
        initializeLifecycleEventListenersForViewTag(i2);
        addOperation(new UIThreadOperation() { // from class: com.facebook.react.animated.NativeAnimatedModule.17
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super();
                NativeAnimatedModule.this = this;
            }

            @Override // com.facebook.react.animated.NativeAnimatedModule.UIThreadOperation
            public void execute(NativeAnimatedNodesManager animatedNodesManager) {
                animatedNodesManager.connectAnimatedNodeToView(i, i2);
            }
        });
    }

    @Override // com.facebook.fbreact.specs.NativeAnimatedModuleSpec
    public void disconnectAnimatedNodeFromView(final double animatedNodeTagDouble, final double viewTagDouble) {
        final int i = (int) animatedNodeTagDouble;
        final int i2 = (int) viewTagDouble;
        decrementInFlightAnimationsForViewTag(i2);
        addOperation(new UIThreadOperation() { // from class: com.facebook.react.animated.NativeAnimatedModule.18
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super();
                NativeAnimatedModule.this = this;
            }

            @Override // com.facebook.react.animated.NativeAnimatedModule.UIThreadOperation
            public void execute(NativeAnimatedNodesManager animatedNodesManager) {
                animatedNodesManager.disconnectAnimatedNodeFromView(i, i2);
            }
        });
    }

    @Override // com.facebook.fbreact.specs.NativeAnimatedModuleSpec
    public void restoreDefaultValues(final double animatedNodeTagDouble) {
        final int i = (int) animatedNodeTagDouble;
        addPreOperation(new UIThreadOperation() { // from class: com.facebook.react.animated.NativeAnimatedModule.19
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super();
                NativeAnimatedModule.this = this;
            }

            @Override // com.facebook.react.animated.NativeAnimatedModule.UIThreadOperation
            public void execute(NativeAnimatedNodesManager animatedNodesManager) {
                animatedNodesManager.restoreDefaultValues(i);
            }
        });
    }

    @Override // com.facebook.fbreact.specs.NativeAnimatedModuleSpec
    public void addAnimatedEventToView(final double viewTagDouble, final String eventName, final ReadableMap eventMapping) {
        final int i = (int) viewTagDouble;
        initializeLifecycleEventListenersForViewTag(i);
        addOperation(new UIThreadOperation() { // from class: com.facebook.react.animated.NativeAnimatedModule.20
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super();
                NativeAnimatedModule.this = this;
            }

            @Override // com.facebook.react.animated.NativeAnimatedModule.UIThreadOperation
            public void execute(NativeAnimatedNodesManager animatedNodesManager) {
                animatedNodesManager.addAnimatedEventToView(i, eventName, eventMapping);
            }
        });
    }

    @Override // com.facebook.fbreact.specs.NativeAnimatedModuleSpec
    public void removeAnimatedEventFromView(final double viewTagDouble, final String eventName, final double animatedValueTagDouble) {
        final int i = (int) viewTagDouble;
        final int i2 = (int) animatedValueTagDouble;
        decrementInFlightAnimationsForViewTag(i);
        addOperation(new UIThreadOperation() { // from class: com.facebook.react.animated.NativeAnimatedModule.21
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super();
                NativeAnimatedModule.this = this;
            }

            @Override // com.facebook.react.animated.NativeAnimatedModule.UIThreadOperation
            public void execute(NativeAnimatedNodesManager animatedNodesManager) {
                animatedNodesManager.removeAnimatedEventFromView(i, eventName, i2);
            }
        });
    }

    @Override // com.facebook.fbreact.specs.NativeAnimatedModuleSpec
    public void getValue(final double animatedValueNodeTagDouble, final Callback callback) {
        final int i = (int) animatedValueNodeTagDouble;
        addOperation(new UIThreadOperation() { // from class: com.facebook.react.animated.NativeAnimatedModule.22
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super();
                NativeAnimatedModule.this = this;
            }

            @Override // com.facebook.react.animated.NativeAnimatedModule.UIThreadOperation
            public void execute(NativeAnimatedNodesManager animatedNodesManager) {
                animatedNodesManager.getValue(i, callback);
            }
        });
    }

    @Override // com.facebook.react.bridge.BaseJavaModule, com.facebook.react.bridge.NativeModule, com.facebook.react.turbomodule.core.interfaces.TurboModule
    public void invalidate() {
        ReactApplicationContext reactApplicationContextIfActiveOrWarn = getReactApplicationContextIfActiveOrWarn();
        if (reactApplicationContextIfActiveOrWarn != null) {
            reactApplicationContextIfActiveOrWarn.removeLifecycleEventListener(this);
        }
    }
}
