package com.facebook.react.uimanager;

import android.os.SystemClock;
import android.view.View;
import com.facebook.common.logging.FLog;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.modules.i18nmanager.I18nUtil;
import com.facebook.react.uimanager.debug.NotThreadSafeViewHierarchyUpdateDebugListener;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.systrace.Systrace;
import com.facebook.systrace.SystraceMessage;
import com.facebook.yoga.YogaDirection;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public class UIImplementation {
    protected final EventDispatcher mEventDispatcher;
    private long mLastCalculateLayoutTime;
    protected LayoutUpdateListener mLayoutUpdateListener;
    private final int[] mMeasureBuffer;
    private final NativeViewHierarchyOptimizer mNativeViewHierarchyOptimizer;
    private final UIViewOperationQueue mOperationsQueue;
    protected final ReactApplicationContext mReactContext;
    protected final ShadowNodeRegistry mShadowNodeRegistry;
    private final ViewManagerRegistry mViewManagers;
    private volatile boolean mViewOperationsEnabled;
    protected Object uiImplementationThreadLock;

    /* loaded from: classes.dex */
    public interface LayoutUpdateListener {
        void onLayoutUpdated(ReactShadowNode root);
    }

    public void onHostDestroy() {
    }

    public UIImplementation(ReactApplicationContext reactContext, ViewManagerResolver viewManagerResolver, EventDispatcher eventDispatcher, int minTimeLeftInFrameForNonBatchedOperationMs) {
        this(reactContext, new ViewManagerRegistry(viewManagerResolver), eventDispatcher, minTimeLeftInFrameForNonBatchedOperationMs);
    }

    public UIImplementation(ReactApplicationContext reactContext, List<ViewManager> viewManagers, EventDispatcher eventDispatcher, int minTimeLeftInFrameForNonBatchedOperationMs) {
        this(reactContext, new ViewManagerRegistry(viewManagers), eventDispatcher, minTimeLeftInFrameForNonBatchedOperationMs);
    }

    public UIImplementation(ReactApplicationContext reactContext, ViewManagerRegistry viewManagers, EventDispatcher eventDispatcher, int minTimeLeftInFrameForNonBatchedOperationMs) {
        this(reactContext, viewManagers, new UIViewOperationQueue(reactContext, new NativeViewHierarchyManager(viewManagers), minTimeLeftInFrameForNonBatchedOperationMs), eventDispatcher);
    }

    protected UIImplementation(ReactApplicationContext reactContext, ViewManagerRegistry viewManagers, UIViewOperationQueue operationsQueue, EventDispatcher eventDispatcher) {
        this.uiImplementationThreadLock = new Object();
        ShadowNodeRegistry shadowNodeRegistry = new ShadowNodeRegistry();
        this.mShadowNodeRegistry = shadowNodeRegistry;
        this.mMeasureBuffer = new int[4];
        this.mLastCalculateLayoutTime = 0L;
        this.mViewOperationsEnabled = true;
        this.mReactContext = reactContext;
        this.mViewManagers = viewManagers;
        this.mOperationsQueue = operationsQueue;
        this.mNativeViewHierarchyOptimizer = new NativeViewHierarchyOptimizer(operationsQueue, shadowNodeRegistry);
        this.mEventDispatcher = eventDispatcher;
    }

    protected ReactShadowNode createRootShadowNode() {
        ReactShadowNodeImpl reactShadowNodeImpl = new ReactShadowNodeImpl();
        if (I18nUtil.getInstance().isRTL(this.mReactContext)) {
            reactShadowNodeImpl.setLayoutDirection(YogaDirection.RTL);
        }
        reactShadowNodeImpl.setViewClassName("Root");
        return reactShadowNodeImpl;
    }

    protected ReactShadowNode createShadowNode(String className) {
        return this.mViewManagers.get(className).createShadowNodeInstance(this.mReactContext);
    }

    public final ReactShadowNode resolveShadowNode(int reactTag) {
        return this.mShadowNodeRegistry.getNode(reactTag);
    }

    public final ViewManager resolveViewManager(String className) {
        return this.mViewManagers.getViewManagerIfExists(className);
    }

    public UIViewOperationQueue getUIViewOperationQueue() {
        return this.mOperationsQueue;
    }

    public void updateRootView(int tag, int widthMeasureSpec, int heightMeasureSpec) {
        ReactShadowNode node = this.mShadowNodeRegistry.getNode(tag);
        if (node == null) {
            FLog.w(ReactConstants.TAG, "Tried to update non-existent root tag: " + tag);
            return;
        }
        updateRootView(node, widthMeasureSpec, heightMeasureSpec);
    }

    public void updateRootView(ReactShadowNode rootCSSNode, int widthMeasureSpec, int heightMeasureSpec) {
        rootCSSNode.setMeasureSpecs(widthMeasureSpec, heightMeasureSpec);
    }

    public <T extends View> void registerRootView(T rootView, int tag, ThemedReactContext context) {
        synchronized (this.uiImplementationThreadLock) {
            final ReactShadowNode createRootShadowNode = createRootShadowNode();
            createRootShadowNode.setReactTag(tag);
            createRootShadowNode.setThemedContext(context);
            context.runOnNativeModulesQueueThread(new Runnable() { // from class: com.facebook.react.uimanager.UIImplementation.1
                @Override // java.lang.Runnable
                public void run() {
                    UIImplementation.this.mShadowNodeRegistry.addRootNode(createRootShadowNode);
                }
            });
            this.mOperationsQueue.addRootView(tag, rootView);
        }
    }

    public void removeRootView(int rootViewTag) {
        removeRootShadowNode(rootViewTag);
        this.mOperationsQueue.enqueueRemoveRootView(rootViewTag);
    }

    public void removeRootShadowNode(int rootViewTag) {
        synchronized (this.uiImplementationThreadLock) {
            this.mShadowNodeRegistry.removeRootNode(rootViewTag);
        }
    }

    public void updateNodeSize(int nodeViewTag, int newWidth, int newHeight) {
        ReactShadowNode node = this.mShadowNodeRegistry.getNode(nodeViewTag);
        if (node == null) {
            FLog.w(ReactConstants.TAG, "Tried to update size of non-existent tag: " + nodeViewTag);
            return;
        }
        node.setStyleWidth(newWidth);
        node.setStyleHeight(newHeight);
        dispatchViewUpdatesIfNeeded();
    }

    public void setViewLocalData(int tag, Object data) {
        ReactShadowNode node = this.mShadowNodeRegistry.getNode(tag);
        if (node == null) {
            FLog.w(ReactConstants.TAG, "Attempt to set local data for view with unknown tag: " + tag);
            return;
        }
        node.setLocalData(data);
        dispatchViewUpdatesIfNeeded();
    }

    public void profileNextBatch() {
        this.mOperationsQueue.profileNextBatch();
    }

    public Map<String, Long> getProfiledBatchPerfCounters() {
        return this.mOperationsQueue.getProfiledBatchPerfCounters();
    }

    public void createView(int tag, String className, int rootViewTag, ReadableMap props) {
        if (!this.mViewOperationsEnabled) {
            return;
        }
        synchronized (this.uiImplementationThreadLock) {
            ReactShadowNode createShadowNode = createShadowNode(className);
            ReactShadowNode node = this.mShadowNodeRegistry.getNode(rootViewTag);
            Assertions.assertNotNull(node, "Root node with tag " + rootViewTag + " doesn't exist");
            createShadowNode.setReactTag(tag);
            createShadowNode.setViewClassName(className);
            createShadowNode.setRootTag(node.getReactTag());
            createShadowNode.setThemedContext(node.getThemedContext());
            this.mShadowNodeRegistry.addNode(createShadowNode);
            ReactStylesDiffMap reactStylesDiffMap = null;
            if (props != null) {
                reactStylesDiffMap = new ReactStylesDiffMap(props);
                createShadowNode.updateProperties(reactStylesDiffMap);
            }
            handleCreateView(createShadowNode, rootViewTag, reactStylesDiffMap);
        }
    }

    protected void handleCreateView(ReactShadowNode cssNode, int rootViewTag, ReactStylesDiffMap styles) {
        if (!cssNode.isVirtual()) {
            this.mNativeViewHierarchyOptimizer.handleCreateView(cssNode, cssNode.getThemedContext(), styles);
        }
    }

    public void updateView(int tag, String className, ReadableMap props) {
        if (!this.mViewOperationsEnabled) {
            return;
        }
        if (this.mViewManagers.get(className) == null) {
            throw new IllegalViewOperationException("Got unknown view type: " + className);
        }
        ReactShadowNode node = this.mShadowNodeRegistry.getNode(tag);
        if (node == null) {
            throw new IllegalViewOperationException("Trying to update non-existent view with tag " + tag);
        } else if (props == null) {
        } else {
            ReactStylesDiffMap reactStylesDiffMap = new ReactStylesDiffMap(props);
            node.updateProperties(reactStylesDiffMap);
            handleUpdateView(node, className, reactStylesDiffMap);
        }
    }

    public void synchronouslyUpdateViewOnUIThread(int tag, ReactStylesDiffMap props) {
        UiThreadUtil.assertOnUiThread();
        this.mOperationsQueue.getNativeViewHierarchyManager().updateProperties(tag, props);
    }

    protected void handleUpdateView(ReactShadowNode cssNode, String className, ReactStylesDiffMap styles) {
        if (!cssNode.isVirtual()) {
            this.mNativeViewHierarchyOptimizer.handleUpdateView(cssNode, className, styles);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:25:0x0049, code lost:
        if (r25 == null) goto L29;
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x004f, code lost:
        if (r11 != r25.size()) goto L29;
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x0059, code lost:
        throw new com.facebook.react.uimanager.IllegalViewOperationException("Size of addChildTags != size of addAtIndices!");
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void manageChildren(int r21, com.facebook.react.bridge.ReadableArray r22, com.facebook.react.bridge.ReadableArray r23, com.facebook.react.bridge.ReadableArray r24, com.facebook.react.bridge.ReadableArray r25, com.facebook.react.bridge.ReadableArray r26) {
        /*
            Method dump skipped, instructions count: 387
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.facebook.react.uimanager.UIImplementation.manageChildren(int, com.facebook.react.bridge.ReadableArray, com.facebook.react.bridge.ReadableArray, com.facebook.react.bridge.ReadableArray, com.facebook.react.bridge.ReadableArray, com.facebook.react.bridge.ReadableArray):void");
    }

    public void setChildren(int viewTag, ReadableArray childrenTags) {
        if (!this.mViewOperationsEnabled) {
            return;
        }
        synchronized (this.uiImplementationThreadLock) {
            ReactShadowNode node = this.mShadowNodeRegistry.getNode(viewTag);
            for (int i = 0; i < childrenTags.size(); i++) {
                ReactShadowNode node2 = this.mShadowNodeRegistry.getNode(childrenTags.getInt(i));
                if (node2 == null) {
                    throw new IllegalViewOperationException("Trying to add unknown view tag: " + childrenTags.getInt(i));
                }
                node.addChildAt(node2, i);
            }
            this.mNativeViewHierarchyOptimizer.handleSetChildren(node, childrenTags);
        }
    }

    public void replaceExistingNonRootView(int oldTag, int newTag) {
        if (this.mShadowNodeRegistry.isRootNode(oldTag) || this.mShadowNodeRegistry.isRootNode(newTag)) {
            throw new IllegalViewOperationException("Trying to add or replace a root tag!");
        }
        ReactShadowNode node = this.mShadowNodeRegistry.getNode(oldTag);
        if (node == null) {
            throw new IllegalViewOperationException("Trying to replace unknown view tag: " + oldTag);
        }
        ReactShadowNode parent = node.getParent();
        if (parent == null) {
            throw new IllegalViewOperationException("Node is not attached to a parent: " + oldTag);
        }
        int indexOf = parent.indexOf(node);
        if (indexOf < 0) {
            throw new IllegalStateException("Didn't find child tag in parent");
        }
        WritableArray createArray = Arguments.createArray();
        createArray.pushInt(newTag);
        WritableArray createArray2 = Arguments.createArray();
        createArray2.pushInt(indexOf);
        WritableArray createArray3 = Arguments.createArray();
        createArray3.pushInt(indexOf);
        manageChildren(parent.getReactTag(), null, null, createArray, createArray2, createArray3);
    }

    public void removeSubviewsFromContainerWithID(int containerTag) {
        ReactShadowNode node = this.mShadowNodeRegistry.getNode(containerTag);
        if (node == null) {
            throw new IllegalViewOperationException("Trying to remove subviews of an unknown view tag: " + containerTag);
        }
        WritableArray createArray = Arguments.createArray();
        for (int i = 0; i < node.getChildCount(); i++) {
            createArray.pushInt(i);
        }
        manageChildren(containerTag, null, null, null, null, createArray);
    }

    public void findSubviewIn(int reactTag, float targetX, float targetY, Callback callback) {
        this.mOperationsQueue.enqueueFindTargetForTouch(reactTag, targetX, targetY, callback);
    }

    @Deprecated
    public void viewIsDescendantOf(final int reactTag, final int ancestorReactTag, final Callback callback) {
        ReactShadowNode node = this.mShadowNodeRegistry.getNode(reactTag);
        ReactShadowNode node2 = this.mShadowNodeRegistry.getNode(ancestorReactTag);
        if (node == null || node2 == null) {
            callback.invoke(false);
        } else {
            callback.invoke(Boolean.valueOf(node.isDescendantOf(node2)));
        }
    }

    public void measure(int reactTag, Callback callback) {
        if (!this.mViewOperationsEnabled) {
            return;
        }
        this.mOperationsQueue.enqueueMeasure(reactTag, callback);
    }

    public void measureInWindow(int reactTag, Callback callback) {
        if (!this.mViewOperationsEnabled) {
            return;
        }
        this.mOperationsQueue.enqueueMeasureInWindow(reactTag, callback);
    }

    public void measureLayout(int tag, int ancestorTag, Callback errorCallback, Callback successCallback) {
        if (!this.mViewOperationsEnabled) {
            return;
        }
        try {
            measureLayout(tag, ancestorTag, this.mMeasureBuffer);
            successCallback.invoke(Float.valueOf(PixelUtil.toDIPFromPixel(this.mMeasureBuffer[0])), Float.valueOf(PixelUtil.toDIPFromPixel(this.mMeasureBuffer[1])), Float.valueOf(PixelUtil.toDIPFromPixel(this.mMeasureBuffer[2])), Float.valueOf(PixelUtil.toDIPFromPixel(this.mMeasureBuffer[3])));
        } catch (IllegalViewOperationException e) {
            errorCallback.invoke(e.getMessage());
        }
    }

    public void measureLayoutRelativeToParent(int tag, Callback errorCallback, Callback successCallback) {
        if (!this.mViewOperationsEnabled) {
            return;
        }
        try {
            measureLayoutRelativeToParent(tag, this.mMeasureBuffer);
            successCallback.invoke(Float.valueOf(PixelUtil.toDIPFromPixel(this.mMeasureBuffer[0])), Float.valueOf(PixelUtil.toDIPFromPixel(this.mMeasureBuffer[1])), Float.valueOf(PixelUtil.toDIPFromPixel(this.mMeasureBuffer[2])), Float.valueOf(PixelUtil.toDIPFromPixel(this.mMeasureBuffer[3])));
        } catch (IllegalViewOperationException e) {
            errorCallback.invoke(e.getMessage());
        }
    }

    public void dispatchViewUpdates(int batchId) {
        SystraceMessage.beginSection(0L, "UIImplementation.dispatchViewUpdates").arg("batchId", batchId).flush();
        long uptimeMillis = SystemClock.uptimeMillis();
        try {
            updateViewHierarchy();
            this.mNativeViewHierarchyOptimizer.onBatchComplete();
            this.mOperationsQueue.dispatchViewUpdates(batchId, uptimeMillis, this.mLastCalculateLayoutTime);
        } finally {
            Systrace.endSection(0L);
        }
    }

    private void dispatchViewUpdatesIfNeeded() {
        if (this.mOperationsQueue.isEmpty()) {
            dispatchViewUpdates(-1);
        }
    }

    protected void updateViewHierarchy() {
        Systrace.beginSection(0L, "UIImplementation.updateViewHierarchy");
        for (int i = 0; i < this.mShadowNodeRegistry.getRootNodeCount(); i++) {
            try {
                ReactShadowNode node = this.mShadowNodeRegistry.getNode(this.mShadowNodeRegistry.getRootTag(i));
                if (node.getWidthMeasureSpec() != null && node.getHeightMeasureSpec() != null) {
                    SystraceMessage.beginSection(0L, "UIImplementation.notifyOnBeforeLayoutRecursive").arg("rootTag", node.getReactTag()).flush();
                    notifyOnBeforeLayoutRecursive(node);
                    Systrace.endSection(0L);
                    calculateRootLayout(node);
                    SystraceMessage.beginSection(0L, "UIImplementation.applyUpdatesRecursive").arg("rootTag", node.getReactTag()).flush();
                    applyUpdatesRecursive(node, 0.0f, 0.0f);
                    Systrace.endSection(0L);
                    LayoutUpdateListener layoutUpdateListener = this.mLayoutUpdateListener;
                    if (layoutUpdateListener != null) {
                        this.mOperationsQueue.enqueueLayoutUpdateFinished(node, layoutUpdateListener);
                    }
                }
            } finally {
                Systrace.endSection(0L);
            }
        }
    }

    public void setLayoutAnimationEnabledExperimental(boolean enabled) {
        this.mOperationsQueue.enqueueSetLayoutAnimationEnabled(enabled);
    }

    public void configureNextLayoutAnimation(ReadableMap config, Callback success) {
        this.mOperationsQueue.enqueueConfigureLayoutAnimation(config, success);
    }

    public void setJSResponder(int reactTag, boolean blockNativeResponder) {
        ReactShadowNode node = this.mShadowNodeRegistry.getNode(reactTag);
        if (node == null) {
            return;
        }
        while (node.getNativeKind() == NativeKind.NONE) {
            node = node.getParent();
        }
        this.mOperationsQueue.enqueueSetJSResponder(node.getReactTag(), reactTag, blockNativeResponder);
    }

    public void clearJSResponder() {
        this.mOperationsQueue.enqueueClearJSResponder();
    }

    @Deprecated
    public void dispatchViewManagerCommand(int reactTag, int commandId, ReadableArray commandArgs) {
        assertViewExists(reactTag, "dispatchViewManagerCommand: " + commandId);
        this.mOperationsQueue.enqueueDispatchCommand(reactTag, commandId, commandArgs);
    }

    public void dispatchViewManagerCommand(int reactTag, String commandId, ReadableArray commandArgs) {
        assertViewExists(reactTag, "dispatchViewManagerCommand: " + commandId);
        this.mOperationsQueue.enqueueDispatchCommand(reactTag, commandId, commandArgs);
    }

    public void showPopupMenu(int reactTag, ReadableArray items, Callback error, Callback success) {
        assertViewExists(reactTag, "showPopupMenu");
        this.mOperationsQueue.enqueueShowPopupMenu(reactTag, items, error, success);
    }

    public void dismissPopupMenu() {
        this.mOperationsQueue.enqueueDismissPopupMenu();
    }

    public void sendAccessibilityEvent(int tag, int eventType) {
        this.mOperationsQueue.enqueueSendAccessibilityEvent(tag, eventType);
    }

    public void onHostResume() {
        this.mOperationsQueue.resumeFrameCallback();
    }

    public void onHostPause() {
        this.mOperationsQueue.pauseFrameCallback();
    }

    public void onCatalystInstanceDestroyed() {
        this.mViewOperationsEnabled = false;
    }

    public void setViewHierarchyUpdateDebugListener(NotThreadSafeViewHierarchyUpdateDebugListener listener) {
        this.mOperationsQueue.setViewHierarchyUpdateDebugListener(listener);
    }

    protected final void removeShadowNode(ReactShadowNode nodeToRemove) {
        removeShadowNodeRecursive(nodeToRemove);
        nodeToRemove.dispose();
    }

    private void removeShadowNodeRecursive(ReactShadowNode nodeToRemove) {
        NativeViewHierarchyOptimizer.handleRemoveNode(nodeToRemove);
        this.mShadowNodeRegistry.removeNode(nodeToRemove.getReactTag());
        for (int childCount = nodeToRemove.getChildCount() - 1; childCount >= 0; childCount--) {
            removeShadowNodeRecursive(nodeToRemove.getChildAt(childCount));
        }
        nodeToRemove.removeAndDisposeAllChildren();
    }

    private void measureLayout(int tag, int ancestorTag, int[] outputBuffer) {
        ReactShadowNode node = this.mShadowNodeRegistry.getNode(tag);
        ReactShadowNode node2 = this.mShadowNodeRegistry.getNode(ancestorTag);
        if (node == null || node2 == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Tag ");
            if (node != null) {
                tag = ancestorTag;
            }
            sb.append(tag);
            sb.append(" does not exist");
            throw new IllegalViewOperationException(sb.toString());
        }
        if (node != node2) {
            for (ReactShadowNode parent = node.getParent(); parent != node2; parent = parent.getParent()) {
                if (parent == null) {
                    throw new IllegalViewOperationException("Tag " + ancestorTag + " is not an ancestor of tag " + tag);
                }
            }
        }
        measureLayoutRelativeToVerifiedAncestor(node, node2, outputBuffer);
    }

    private void measureLayoutRelativeToParent(int tag, int[] outputBuffer) {
        ReactShadowNode node = this.mShadowNodeRegistry.getNode(tag);
        if (node == null) {
            throw new IllegalViewOperationException("No native view for tag " + tag + " exists!");
        }
        ReactShadowNode parent = node.getParent();
        if (parent == null) {
            throw new IllegalViewOperationException("View with tag " + tag + " doesn't have a parent!");
        }
        measureLayoutRelativeToVerifiedAncestor(node, parent, outputBuffer);
    }

    private void measureLayoutRelativeToVerifiedAncestor(ReactShadowNode node, ReactShadowNode ancestor, int[] outputBuffer) {
        int i;
        int i2;
        if (node == ancestor || node.isVirtual()) {
            i2 = 0;
            i = 0;
        } else {
            i2 = Math.round(node.getLayoutX());
            i = Math.round(node.getLayoutY());
            for (ReactShadowNode parent = node.getParent(); parent != ancestor; parent = parent.getParent()) {
                Assertions.assertNotNull(parent);
                assertNodeDoesNotNeedCustomLayoutForChildren(parent);
                i2 += Math.round(parent.getLayoutX());
                i += Math.round(parent.getLayoutY());
            }
            assertNodeDoesNotNeedCustomLayoutForChildren(ancestor);
        }
        outputBuffer[0] = i2;
        outputBuffer[1] = i;
        outputBuffer[2] = node.getScreenWidth();
        outputBuffer[3] = node.getScreenHeight();
    }

    private void assertViewExists(int reactTag, String operationNameForExceptionMessage) {
        if (this.mShadowNodeRegistry.getNode(reactTag) != null) {
            return;
        }
        throw new IllegalViewOperationException("Unable to execute operation " + operationNameForExceptionMessage + " on view with tag: " + reactTag + ", since the view does not exists");
    }

    private void assertNodeDoesNotNeedCustomLayoutForChildren(ReactShadowNode node) {
        ViewManager viewManager = (ViewManager) Assertions.assertNotNull(this.mViewManagers.get(node.getViewClass()));
        if (viewManager instanceof IViewManagerWithChildren) {
            IViewManagerWithChildren iViewManagerWithChildren = (IViewManagerWithChildren) viewManager;
            if (iViewManagerWithChildren == null || !iViewManagerWithChildren.needsCustomLayoutForChildren()) {
                return;
            }
            throw new IllegalViewOperationException("Trying to measure a view using measureLayout/measureLayoutRelativeToParent relative to an ancestor that requires custom layout for it's children (" + node.getViewClass() + "). Use measure instead.");
        }
        throw new IllegalViewOperationException("Trying to use view " + node.getViewClass() + " as a parent, but its Manager doesn't extends ViewGroupManager");
    }

    private void notifyOnBeforeLayoutRecursive(ReactShadowNode cssNode) {
        if (!cssNode.hasUpdates()) {
            return;
        }
        for (int i = 0; i < cssNode.getChildCount(); i++) {
            notifyOnBeforeLayoutRecursive(cssNode.getChildAt(i));
        }
        cssNode.onBeforeLayout(this.mNativeViewHierarchyOptimizer);
    }

    protected void calculateRootLayout(ReactShadowNode cssRoot) {
        SystraceMessage.beginSection(0L, "cssRoot.calculateLayout").arg("rootTag", cssRoot.getReactTag()).flush();
        long uptimeMillis = SystemClock.uptimeMillis();
        try {
            int intValue = cssRoot.getWidthMeasureSpec().intValue();
            int intValue2 = cssRoot.getHeightMeasureSpec().intValue();
            float f = Float.NaN;
            float size = View.MeasureSpec.getMode(intValue) == 0 ? Float.NaN : View.MeasureSpec.getSize(intValue);
            if (View.MeasureSpec.getMode(intValue2) != 0) {
                f = View.MeasureSpec.getSize(intValue2);
            }
            cssRoot.calculateLayout(size, f);
        } finally {
            Systrace.endSection(0L);
            this.mLastCalculateLayoutTime = SystemClock.uptimeMillis() - uptimeMillis;
        }
    }

    protected void applyUpdatesRecursive(ReactShadowNode cssNode, float absoluteX, float absoluteY) {
        if (!cssNode.hasUpdates()) {
            return;
        }
        Iterable<? extends ReactShadowNode> calculateLayoutOnChildren = cssNode.calculateLayoutOnChildren();
        if (calculateLayoutOnChildren != null) {
            for (ReactShadowNode reactShadowNode : calculateLayoutOnChildren) {
                applyUpdatesRecursive(reactShadowNode, cssNode.getLayoutX() + absoluteX, cssNode.getLayoutY() + absoluteY);
            }
        }
        int reactTag = cssNode.getReactTag();
        if (!this.mShadowNodeRegistry.isRootNode(reactTag) && cssNode.dispatchUpdates(absoluteX, absoluteY, this.mOperationsQueue, this.mNativeViewHierarchyOptimizer) && cssNode.shouldNotifyOnLayout()) {
            this.mEventDispatcher.dispatchEvent(OnLayoutEvent.obtain(-1, reactTag, cssNode.getScreenX(), cssNode.getScreenY(), cssNode.getScreenWidth(), cssNode.getScreenHeight()));
        }
        cssNode.markUpdateSeen();
        this.mNativeViewHierarchyOptimizer.onViewUpdatesCompleted(cssNode);
    }

    public void addUIBlock(UIBlock block) {
        this.mOperationsQueue.enqueueUIBlock(block);
    }

    public void prependUIBlock(UIBlock block) {
        this.mOperationsQueue.prependUIBlock(block);
    }

    public int resolveRootTagFromReactTag(int reactTag) {
        if (this.mShadowNodeRegistry.isRootNode(reactTag)) {
            return reactTag;
        }
        ReactShadowNode resolveShadowNode = resolveShadowNode(reactTag);
        if (resolveShadowNode != null) {
            return resolveShadowNode.getRootTag();
        }
        FLog.w(ReactConstants.TAG, "Warning : attempted to resolve a non-existent react shadow node. reactTag=" + reactTag);
        return 0;
    }

    public void setLayoutUpdateListener(LayoutUpdateListener listener) {
        this.mLayoutUpdateListener = listener;
    }

    public void removeLayoutUpdateListener() {
        this.mLayoutUpdateListener = null;
    }
}
