package com.facebook.react.uimanager;

import android.os.SystemClock;
import android.view.View;
import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.GuardedRunnable;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactNoCrashSoftException;
import com.facebook.react.bridge.ReactSoftExceptionLogger;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.RetryableMountingLayerException;
import com.facebook.react.bridge.SoftAssertions;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.modules.core.ReactChoreographer;
import com.facebook.react.uimanager.UIImplementation;
import com.facebook.react.uimanager.debug.NotThreadSafeViewHierarchyUpdateDebugListener;
import com.facebook.systrace.Systrace;
import com.facebook.systrace.SystraceMessage;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
/* loaded from: classes.dex */
public class UIViewOperationQueue {
    public static final int DEFAULT_MIN_TIME_LEFT_IN_FRAME_FOR_NONBATCHED_OPERATION_MS = 8;
    private static final String TAG = "UIViewOperationQueue";
    private long mCreateViewCount;
    private final DispatchUIFrameCallback mDispatchUIFrameCallback;
    private final NativeViewHierarchyManager mNativeViewHierarchyManager;
    private long mNonBatchedExecutionTotalTime;
    private long mProfiledBatchBatchedExecutionTime;
    private long mProfiledBatchCommitEndTime;
    private long mProfiledBatchCommitStartTime;
    private long mProfiledBatchDispatchViewUpdatesTime;
    private long mProfiledBatchLayoutTime;
    private long mProfiledBatchNonBatchedExecutionTime;
    private long mProfiledBatchRunEndTime;
    private long mProfiledBatchRunStartTime;
    private final ReactApplicationContext mReactApplicationContext;
    private long mThreadCpuTime;
    private long mUpdatePropertiesOperationCount;
    private NotThreadSafeViewHierarchyUpdateDebugListener mViewHierarchyUpdateDebugListener;
    private final int[] mMeasureBuffer = new int[4];
    private final Object mDispatchRunnablesLock = new Object();
    private final Object mNonBatchedOperationsLock = new Object();
    private ArrayList<DispatchCommandViewOperation> mViewCommandOperations = new ArrayList<>();
    private ArrayList<UIOperation> mOperations = new ArrayList<>();
    private ArrayList<Runnable> mDispatchUIRunnables = new ArrayList<>();
    private ArrayDeque<UIOperation> mNonBatchedOperations = new ArrayDeque<>();
    private boolean mIsDispatchUIFrameCallbackEnqueued = false;
    private boolean mIsInIllegalUIState = false;
    private boolean mIsProfilingNextBatch = false;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public interface DispatchCommandViewOperation {
        void executeWithExceptions();

        int getRetries();

        void incrementRetries();
    }

    /* loaded from: classes.dex */
    public interface UIOperation {
        void execute();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public abstract class ViewOperation implements UIOperation {
        public int mTag;

        public ViewOperation(int tag) {
            UIViewOperationQueue.this = this$0;
            this.mTag = tag;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class RemoveRootViewOperation extends ViewOperation {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public RemoveRootViewOperation(int tag) {
            super(tag);
            UIViewOperationQueue.this = this$0;
        }

        @Override // com.facebook.react.uimanager.UIViewOperationQueue.UIOperation
        public void execute() {
            UIViewOperationQueue.this.mNativeViewHierarchyManager.removeRootView(this.mTag);
        }
    }

    /* loaded from: classes.dex */
    public final class UpdatePropertiesOperation extends ViewOperation {
        private final ReactStylesDiffMap mProps;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        private UpdatePropertiesOperation(int tag, ReactStylesDiffMap props) {
            super(tag);
            UIViewOperationQueue.this = this$0;
            this.mProps = props;
        }

        @Override // com.facebook.react.uimanager.UIViewOperationQueue.UIOperation
        public void execute() {
            UIViewOperationQueue.this.mNativeViewHierarchyManager.updateProperties(this.mTag, this.mProps);
        }
    }

    /* loaded from: classes.dex */
    private final class EmitOnLayoutEventOperation extends ViewOperation {
        private final int mScreenHeight;
        private final int mScreenWidth;
        private final int mScreenX;
        private final int mScreenY;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public EmitOnLayoutEventOperation(int tag, int screenX, int screenY, int screenWidth, int screenHeight) {
            super(tag);
            UIViewOperationQueue.this = this$0;
            this.mScreenX = screenX;
            this.mScreenY = screenY;
            this.mScreenWidth = screenWidth;
            this.mScreenHeight = screenHeight;
        }

        @Override // com.facebook.react.uimanager.UIViewOperationQueue.UIOperation
        public void execute() {
            UIManagerModule uIManagerModule = (UIManagerModule) UIViewOperationQueue.this.mReactApplicationContext.getNativeModule(UIManagerModule.class);
            if (uIManagerModule != null) {
                uIManagerModule.getEventDispatcher().dispatchEvent(OnLayoutEvent.obtain(-1, this.mTag, this.mScreenX, this.mScreenY, this.mScreenWidth, this.mScreenHeight));
            }
        }
    }

    /* loaded from: classes.dex */
    private final class UpdateInstanceHandleOperation extends ViewOperation {
        private final long mInstanceHandle;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        private UpdateInstanceHandleOperation(int tag, long instanceHandle) {
            super(tag);
            UIViewOperationQueue.this = this$0;
            this.mInstanceHandle = instanceHandle;
        }

        @Override // com.facebook.react.uimanager.UIViewOperationQueue.UIOperation
        public void execute() {
            UIViewOperationQueue.this.mNativeViewHierarchyManager.updateInstanceHandle(this.mTag, this.mInstanceHandle);
        }
    }

    /* loaded from: classes.dex */
    public final class UpdateLayoutOperation extends ViewOperation {
        private final int mHeight;
        private final int mParentTag;
        private final int mWidth;
        private final int mX;
        private final int mY;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public UpdateLayoutOperation(int parentTag, int tag, int x, int y, int width, int height) {
            super(tag);
            UIViewOperationQueue.this = this$0;
            this.mParentTag = parentTag;
            this.mX = x;
            this.mY = y;
            this.mWidth = width;
            this.mHeight = height;
            Systrace.startAsyncFlow(0L, "updateLayout", this.mTag);
        }

        @Override // com.facebook.react.uimanager.UIViewOperationQueue.UIOperation
        public void execute() {
            Systrace.endAsyncFlow(0L, "updateLayout", this.mTag);
            UIViewOperationQueue.this.mNativeViewHierarchyManager.updateLayout(this.mParentTag, this.mTag, this.mX, this.mY, this.mWidth, this.mHeight);
        }
    }

    /* loaded from: classes.dex */
    public final class CreateViewOperation extends ViewOperation {
        private final String mClassName;
        private final ReactStylesDiffMap mInitialProps;
        private final ThemedReactContext mThemedContext;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public CreateViewOperation(ThemedReactContext themedContext, int tag, String className, ReactStylesDiffMap initialProps) {
            super(tag);
            UIViewOperationQueue.this = this$0;
            this.mThemedContext = themedContext;
            this.mClassName = className;
            this.mInitialProps = initialProps;
            Systrace.startAsyncFlow(0L, "createView", this.mTag);
        }

        @Override // com.facebook.react.uimanager.UIViewOperationQueue.UIOperation
        public void execute() {
            Systrace.endAsyncFlow(0L, "createView", this.mTag);
            UIViewOperationQueue.this.mNativeViewHierarchyManager.createView(this.mThemedContext, this.mTag, this.mClassName, this.mInitialProps);
        }
    }

    /* loaded from: classes.dex */
    public final class ManageChildrenOperation extends ViewOperation {
        private final int[] mIndicesToRemove;
        private final int[] mTagsToDelete;
        private final ViewAtIndex[] mViewsToAdd;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ManageChildrenOperation(int tag, int[] indicesToRemove, ViewAtIndex[] viewsToAdd, int[] tagsToDelete) {
            super(tag);
            UIViewOperationQueue.this = this$0;
            this.mIndicesToRemove = indicesToRemove;
            this.mViewsToAdd = viewsToAdd;
            this.mTagsToDelete = tagsToDelete;
        }

        @Override // com.facebook.react.uimanager.UIViewOperationQueue.UIOperation
        public void execute() {
            UIViewOperationQueue.this.mNativeViewHierarchyManager.manageChildren(this.mTag, this.mIndicesToRemove, this.mViewsToAdd, this.mTagsToDelete);
        }
    }

    /* loaded from: classes.dex */
    private final class SetChildrenOperation extends ViewOperation {
        private final ReadableArray mChildrenTags;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public SetChildrenOperation(int tag, ReadableArray childrenTags) {
            super(tag);
            UIViewOperationQueue.this = this$0;
            this.mChildrenTags = childrenTags;
        }

        @Override // com.facebook.react.uimanager.UIViewOperationQueue.UIOperation
        public void execute() {
            UIViewOperationQueue.this.mNativeViewHierarchyManager.setChildren(this.mTag, this.mChildrenTags);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class UpdateViewExtraData extends ViewOperation {
        private final Object mExtraData;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public UpdateViewExtraData(int tag, Object extraData) {
            super(tag);
            UIViewOperationQueue.this = this$0;
            this.mExtraData = extraData;
        }

        @Override // com.facebook.react.uimanager.UIViewOperationQueue.UIOperation
        public void execute() {
            UIViewOperationQueue.this.mNativeViewHierarchyManager.updateViewExtraData(this.mTag, this.mExtraData);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class ChangeJSResponderOperation extends ViewOperation {
        private final boolean mBlockNativeResponder;
        private final boolean mClearResponder;
        private final int mInitialTag;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ChangeJSResponderOperation(int tag, int initialTag, boolean clearResponder, boolean blockNativeResponder) {
            super(tag);
            UIViewOperationQueue.this = this$0;
            this.mInitialTag = initialTag;
            this.mClearResponder = clearResponder;
            this.mBlockNativeResponder = blockNativeResponder;
        }

        @Override // com.facebook.react.uimanager.UIViewOperationQueue.UIOperation
        public void execute() {
            if (!this.mClearResponder) {
                UIViewOperationQueue.this.mNativeViewHierarchyManager.setJSResponder(this.mTag, this.mInitialTag, this.mBlockNativeResponder);
            } else {
                UIViewOperationQueue.this.mNativeViewHierarchyManager.clearJSResponder();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @Deprecated
    /* loaded from: classes.dex */
    public final class DispatchCommandOperation extends ViewOperation implements DispatchCommandViewOperation {
        private final ReadableArray mArgs;
        private final int mCommand;
        private int numRetries = 0;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public DispatchCommandOperation(int tag, int command, ReadableArray args) {
            super(tag);
            UIViewOperationQueue.this = this$0;
            this.mCommand = command;
            this.mArgs = args;
        }

        @Override // com.facebook.react.uimanager.UIViewOperationQueue.UIOperation
        public void execute() {
            try {
                UIViewOperationQueue.this.mNativeViewHierarchyManager.dispatchCommand(this.mTag, this.mCommand, this.mArgs);
            } catch (Throwable th) {
                ReactSoftExceptionLogger.logSoftException(UIViewOperationQueue.TAG, new RuntimeException("Error dispatching View Command", th));
            }
        }

        @Override // com.facebook.react.uimanager.UIViewOperationQueue.DispatchCommandViewOperation
        public void executeWithExceptions() {
            UIViewOperationQueue.this.mNativeViewHierarchyManager.dispatchCommand(this.mTag, this.mCommand, this.mArgs);
        }

        @Override // com.facebook.react.uimanager.UIViewOperationQueue.DispatchCommandViewOperation
        public void incrementRetries() {
            this.numRetries++;
        }

        @Override // com.facebook.react.uimanager.UIViewOperationQueue.DispatchCommandViewOperation
        public int getRetries() {
            return this.numRetries;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class DispatchStringCommandOperation extends ViewOperation implements DispatchCommandViewOperation {
        private final ReadableArray mArgs;
        private final String mCommand;
        private int numRetries = 0;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public DispatchStringCommandOperation(int tag, String command, ReadableArray args) {
            super(tag);
            UIViewOperationQueue.this = this$0;
            this.mCommand = command;
            this.mArgs = args;
        }

        @Override // com.facebook.react.uimanager.UIViewOperationQueue.UIOperation
        public void execute() {
            try {
                UIViewOperationQueue.this.mNativeViewHierarchyManager.dispatchCommand(this.mTag, this.mCommand, this.mArgs);
            } catch (Throwable th) {
                ReactSoftExceptionLogger.logSoftException(UIViewOperationQueue.TAG, new RuntimeException("Error dispatching View Command", th));
            }
        }

        @Override // com.facebook.react.uimanager.UIViewOperationQueue.DispatchCommandViewOperation
        public void executeWithExceptions() {
            UIViewOperationQueue.this.mNativeViewHierarchyManager.dispatchCommand(this.mTag, this.mCommand, this.mArgs);
        }

        @Override // com.facebook.react.uimanager.UIViewOperationQueue.DispatchCommandViewOperation
        public void incrementRetries() {
            this.numRetries++;
        }

        @Override // com.facebook.react.uimanager.UIViewOperationQueue.DispatchCommandViewOperation
        public int getRetries() {
            return this.numRetries;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class ShowPopupMenuOperation extends ViewOperation {
        private final Callback mError;
        private final ReadableArray mItems;
        private final Callback mSuccess;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ShowPopupMenuOperation(int tag, ReadableArray items, Callback error, Callback success) {
            super(tag);
            UIViewOperationQueue.this = this$0;
            this.mItems = items;
            this.mError = error;
            this.mSuccess = success;
        }

        @Override // com.facebook.react.uimanager.UIViewOperationQueue.UIOperation
        public void execute() {
            UIViewOperationQueue.this.mNativeViewHierarchyManager.showPopupMenu(this.mTag, this.mItems, this.mSuccess, this.mError);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class DismissPopupMenuOperation implements UIOperation {
        private DismissPopupMenuOperation() {
            UIViewOperationQueue.this = this$0;
        }

        @Override // com.facebook.react.uimanager.UIViewOperationQueue.UIOperation
        public void execute() {
            UIViewOperationQueue.this.mNativeViewHierarchyManager.dismissPopupMenu();
        }
    }

    /* loaded from: classes.dex */
    private static abstract class AnimationOperation implements UIOperation {
        protected final int mAnimationID;

        public AnimationOperation(int animationID) {
            this.mAnimationID = animationID;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SetLayoutAnimationEnabledOperation implements UIOperation {
        private final boolean mEnabled;

        private SetLayoutAnimationEnabledOperation(final boolean enabled) {
            UIViewOperationQueue.this = this$0;
            this.mEnabled = enabled;
        }

        @Override // com.facebook.react.uimanager.UIViewOperationQueue.UIOperation
        public void execute() {
            UIViewOperationQueue.this.mNativeViewHierarchyManager.setLayoutAnimationEnabled(this.mEnabled);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ConfigureLayoutAnimationOperation implements UIOperation {
        private final Callback mAnimationComplete;
        private final ReadableMap mConfig;

        private ConfigureLayoutAnimationOperation(final ReadableMap config, final Callback animationComplete) {
            UIViewOperationQueue.this = this$0;
            this.mConfig = config;
            this.mAnimationComplete = animationComplete;
        }

        @Override // com.facebook.react.uimanager.UIViewOperationQueue.UIOperation
        public void execute() {
            UIViewOperationQueue.this.mNativeViewHierarchyManager.configureLayoutAnimation(this.mConfig, this.mAnimationComplete);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class MeasureOperation implements UIOperation {
        private final Callback mCallback;
        private final int mReactTag;

        private MeasureOperation(final int reactTag, final Callback callback) {
            UIViewOperationQueue.this = this$0;
            this.mReactTag = reactTag;
            this.mCallback = callback;
        }

        @Override // com.facebook.react.uimanager.UIViewOperationQueue.UIOperation
        public void execute() {
            try {
                UIViewOperationQueue.this.mNativeViewHierarchyManager.measure(this.mReactTag, UIViewOperationQueue.this.mMeasureBuffer);
                float dIPFromPixel = PixelUtil.toDIPFromPixel(UIViewOperationQueue.this.mMeasureBuffer[0]);
                float dIPFromPixel2 = PixelUtil.toDIPFromPixel(UIViewOperationQueue.this.mMeasureBuffer[1]);
                this.mCallback.invoke(0, 0, Float.valueOf(PixelUtil.toDIPFromPixel(UIViewOperationQueue.this.mMeasureBuffer[2])), Float.valueOf(PixelUtil.toDIPFromPixel(UIViewOperationQueue.this.mMeasureBuffer[3])), Float.valueOf(dIPFromPixel), Float.valueOf(dIPFromPixel2));
            } catch (NoSuchNativeViewException unused) {
                this.mCallback.invoke(new Object[0]);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class MeasureInWindowOperation implements UIOperation {
        private final Callback mCallback;
        private final int mReactTag;

        private MeasureInWindowOperation(final int reactTag, final Callback callback) {
            UIViewOperationQueue.this = this$0;
            this.mReactTag = reactTag;
            this.mCallback = callback;
        }

        @Override // com.facebook.react.uimanager.UIViewOperationQueue.UIOperation
        public void execute() {
            try {
                UIViewOperationQueue.this.mNativeViewHierarchyManager.measureInWindow(this.mReactTag, UIViewOperationQueue.this.mMeasureBuffer);
                this.mCallback.invoke(Float.valueOf(PixelUtil.toDIPFromPixel(UIViewOperationQueue.this.mMeasureBuffer[0])), Float.valueOf(PixelUtil.toDIPFromPixel(UIViewOperationQueue.this.mMeasureBuffer[1])), Float.valueOf(PixelUtil.toDIPFromPixel(UIViewOperationQueue.this.mMeasureBuffer[2])), Float.valueOf(PixelUtil.toDIPFromPixel(UIViewOperationQueue.this.mMeasureBuffer[3])));
            } catch (NoSuchNativeViewException unused) {
                this.mCallback.invoke(new Object[0]);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class FindTargetForTouchOperation implements UIOperation {
        private final Callback mCallback;
        private final int mReactTag;
        private final float mTargetX;
        private final float mTargetY;

        private FindTargetForTouchOperation(final int reactTag, final float targetX, final float targetY, final Callback callback) {
            UIViewOperationQueue.this = this$0;
            this.mReactTag = reactTag;
            this.mTargetX = targetX;
            this.mTargetY = targetY;
            this.mCallback = callback;
        }

        @Override // com.facebook.react.uimanager.UIViewOperationQueue.UIOperation
        public void execute() {
            try {
                UIViewOperationQueue.this.mNativeViewHierarchyManager.measure(this.mReactTag, UIViewOperationQueue.this.mMeasureBuffer);
                float f = UIViewOperationQueue.this.mMeasureBuffer[0];
                float f2 = UIViewOperationQueue.this.mMeasureBuffer[1];
                int findTargetTagForTouch = UIViewOperationQueue.this.mNativeViewHierarchyManager.findTargetTagForTouch(this.mReactTag, this.mTargetX, this.mTargetY);
                try {
                    UIViewOperationQueue.this.mNativeViewHierarchyManager.measure(findTargetTagForTouch, UIViewOperationQueue.this.mMeasureBuffer);
                    this.mCallback.invoke(Integer.valueOf(findTargetTagForTouch), Float.valueOf(PixelUtil.toDIPFromPixel(UIViewOperationQueue.this.mMeasureBuffer[0] - f)), Float.valueOf(PixelUtil.toDIPFromPixel(UIViewOperationQueue.this.mMeasureBuffer[1] - f2)), Float.valueOf(PixelUtil.toDIPFromPixel(UIViewOperationQueue.this.mMeasureBuffer[2])), Float.valueOf(PixelUtil.toDIPFromPixel(UIViewOperationQueue.this.mMeasureBuffer[3])));
                } catch (IllegalViewOperationException unused) {
                    this.mCallback.invoke(new Object[0]);
                }
            } catch (IllegalViewOperationException unused2) {
                this.mCallback.invoke(new Object[0]);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class LayoutUpdateFinishedOperation implements UIOperation {
        private final UIImplementation.LayoutUpdateListener mListener;
        private final ReactShadowNode mNode;

        private LayoutUpdateFinishedOperation(ReactShadowNode node, UIImplementation.LayoutUpdateListener listener) {
            UIViewOperationQueue.this = this$0;
            this.mNode = node;
            this.mListener = listener;
        }

        @Override // com.facebook.react.uimanager.UIViewOperationQueue.UIOperation
        public void execute() {
            this.mListener.onLayoutUpdated(this.mNode);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class UIBlockOperation implements UIOperation {
        private final UIBlock mBlock;

        public UIBlockOperation(UIBlock block) {
            UIViewOperationQueue.this = this$0;
            this.mBlock = block;
        }

        @Override // com.facebook.react.uimanager.UIViewOperationQueue.UIOperation
        public void execute() {
            this.mBlock.execute(UIViewOperationQueue.this.mNativeViewHierarchyManager);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class SendAccessibilityEvent extends ViewOperation {
        private final int mEventType;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        private SendAccessibilityEvent(int tag, int eventType) {
            super(tag);
            UIViewOperationQueue.this = this$0;
            this.mEventType = eventType;
        }

        @Override // com.facebook.react.uimanager.UIViewOperationQueue.UIOperation
        public void execute() {
            UIViewOperationQueue.this.mNativeViewHierarchyManager.sendAccessibilityEvent(this.mTag, this.mEventType);
        }
    }

    public UIViewOperationQueue(ReactApplicationContext reactContext, NativeViewHierarchyManager nativeViewHierarchyManager, int minTimeLeftInFrameForNonBatchedOperationMs) {
        this.mNativeViewHierarchyManager = nativeViewHierarchyManager;
        this.mDispatchUIFrameCallback = new DispatchUIFrameCallback(reactContext, minTimeLeftInFrameForNonBatchedOperationMs == -1 ? 8 : minTimeLeftInFrameForNonBatchedOperationMs);
        this.mReactApplicationContext = reactContext;
    }

    public NativeViewHierarchyManager getNativeViewHierarchyManager() {
        return this.mNativeViewHierarchyManager;
    }

    public void setViewHierarchyUpdateDebugListener(NotThreadSafeViewHierarchyUpdateDebugListener listener) {
        this.mViewHierarchyUpdateDebugListener = listener;
    }

    public void profileNextBatch() {
        this.mIsProfilingNextBatch = true;
        this.mProfiledBatchCommitStartTime = 0L;
        this.mCreateViewCount = 0L;
        this.mUpdatePropertiesOperationCount = 0L;
    }

    public Map<String, Long> getProfiledBatchPerfCounters() {
        HashMap hashMap = new HashMap();
        hashMap.put("CommitStartTime", Long.valueOf(this.mProfiledBatchCommitStartTime));
        hashMap.put("CommitEndTime", Long.valueOf(this.mProfiledBatchCommitEndTime));
        hashMap.put("LayoutTime", Long.valueOf(this.mProfiledBatchLayoutTime));
        hashMap.put("DispatchViewUpdatesTime", Long.valueOf(this.mProfiledBatchDispatchViewUpdatesTime));
        hashMap.put("RunStartTime", Long.valueOf(this.mProfiledBatchRunStartTime));
        hashMap.put("RunEndTime", Long.valueOf(this.mProfiledBatchRunEndTime));
        hashMap.put("BatchedExecutionTime", Long.valueOf(this.mProfiledBatchBatchedExecutionTime));
        hashMap.put("NonBatchedExecutionTime", Long.valueOf(this.mProfiledBatchNonBatchedExecutionTime));
        hashMap.put("NativeModulesThreadCpuTime", Long.valueOf(this.mThreadCpuTime));
        hashMap.put("CreateViewCount", Long.valueOf(this.mCreateViewCount));
        hashMap.put("UpdatePropsCount", Long.valueOf(this.mUpdatePropertiesOperationCount));
        return hashMap;
    }

    public boolean isEmpty() {
        return this.mOperations.isEmpty() && this.mViewCommandOperations.isEmpty();
    }

    public void addRootView(final int tag, final View rootView) {
        this.mNativeViewHierarchyManager.addRootView(tag, rootView);
    }

    protected void enqueueUIOperation(UIOperation operation) {
        SoftAssertions.assertNotNull(operation);
        this.mOperations.add(operation);
    }

    public void enqueueRemoveRootView(int rootViewTag) {
        this.mOperations.add(new RemoveRootViewOperation(rootViewTag));
    }

    public void enqueueSetJSResponder(int tag, int initialTag, boolean blockNativeResponder) {
        this.mOperations.add(new ChangeJSResponderOperation(tag, initialTag, false, blockNativeResponder));
    }

    public void enqueueClearJSResponder() {
        this.mOperations.add(new ChangeJSResponderOperation(0, 0, true, false));
    }

    @Deprecated
    public void enqueueDispatchCommand(int reactTag, int commandId, ReadableArray commandArgs) {
        this.mViewCommandOperations.add(new DispatchCommandOperation(reactTag, commandId, commandArgs));
    }

    public void enqueueDispatchCommand(int reactTag, String commandId, ReadableArray commandArgs) {
        this.mViewCommandOperations.add(new DispatchStringCommandOperation(reactTag, commandId, commandArgs));
    }

    public void enqueueUpdateExtraData(int reactTag, Object extraData) {
        this.mOperations.add(new UpdateViewExtraData(reactTag, extraData));
    }

    public void enqueueShowPopupMenu(int reactTag, ReadableArray items, Callback error, Callback success) {
        this.mOperations.add(new ShowPopupMenuOperation(reactTag, items, error, success));
    }

    public void enqueueDismissPopupMenu() {
        this.mOperations.add(new DismissPopupMenuOperation());
    }

    public void enqueueCreateView(ThemedReactContext themedContext, int viewReactTag, String viewClassName, ReactStylesDiffMap initialProps) {
        synchronized (this.mNonBatchedOperationsLock) {
            this.mCreateViewCount++;
            this.mNonBatchedOperations.addLast(new CreateViewOperation(themedContext, viewReactTag, viewClassName, initialProps));
        }
    }

    public void enqueueUpdateInstanceHandle(int reactTag, long instanceHandle) {
        this.mOperations.add(new UpdateInstanceHandleOperation(reactTag, instanceHandle));
    }

    public void enqueueUpdateProperties(int reactTag, String className, ReactStylesDiffMap props) {
        this.mUpdatePropertiesOperationCount++;
        this.mOperations.add(new UpdatePropertiesOperation(reactTag, props));
    }

    public void enqueueOnLayoutEvent(int tag, int screenX, int screenY, int screenWidth, int screenHeight) {
        this.mOperations.add(new EmitOnLayoutEventOperation(tag, screenX, screenY, screenWidth, screenHeight));
    }

    public void enqueueUpdateLayout(int parentTag, int reactTag, int x, int y, int width, int height) {
        this.mOperations.add(new UpdateLayoutOperation(parentTag, reactTag, x, y, width, height));
    }

    public void enqueueManageChildren(int reactTag, int[] indicesToRemove, ViewAtIndex[] viewsToAdd, int[] tagsToDelete) {
        this.mOperations.add(new ManageChildrenOperation(reactTag, indicesToRemove, viewsToAdd, tagsToDelete));
    }

    public void enqueueSetChildren(int reactTag, ReadableArray childrenTags) {
        this.mOperations.add(new SetChildrenOperation(reactTag, childrenTags));
    }

    public void enqueueSetLayoutAnimationEnabled(final boolean enabled) {
        this.mOperations.add(new SetLayoutAnimationEnabledOperation(enabled));
    }

    public void enqueueConfigureLayoutAnimation(final ReadableMap config, final Callback onAnimationComplete) {
        this.mOperations.add(new ConfigureLayoutAnimationOperation(config, onAnimationComplete));
    }

    public void enqueueMeasure(final int reactTag, final Callback callback) {
        this.mOperations.add(new MeasureOperation(reactTag, callback));
    }

    public void enqueueMeasureInWindow(final int reactTag, final Callback callback) {
        this.mOperations.add(new MeasureInWindowOperation(reactTag, callback));
    }

    public void enqueueFindTargetForTouch(final int reactTag, final float targetX, final float targetY, final Callback callback) {
        this.mOperations.add(new FindTargetForTouchOperation(reactTag, targetX, targetY, callback));
    }

    public void enqueueSendAccessibilityEvent(int tag, int eventType) {
        this.mOperations.add(new SendAccessibilityEvent(tag, eventType));
    }

    public void enqueueLayoutUpdateFinished(ReactShadowNode node, UIImplementation.LayoutUpdateListener listener) {
        this.mOperations.add(new LayoutUpdateFinishedOperation(node, listener));
    }

    public void enqueueUIBlock(UIBlock block) {
        this.mOperations.add(new UIBlockOperation(block));
    }

    public void prependUIBlock(UIBlock block) {
        this.mOperations.add(0, new UIBlockOperation(block));
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v3 */
    /* JADX WARN: Type inference failed for: r2v8 */
    public void dispatchViewUpdates(final int batchId, final long commitStartTime, final long layoutTime) {
        long j;
        Throwable th;
        final long uptimeMillis;
        final long currentThreadTimeMillis;
        final ArrayList<DispatchCommandViewOperation> arrayList;
        final ArrayList<UIOperation> arrayList2;
        final ArrayDeque<UIOperation> arrayDeque;
        SystraceMessage.beginSection(0L, "UIViewOperationQueue.dispatchViewUpdates").arg("batchId", batchId).flush();
        try {
            uptimeMillis = SystemClock.uptimeMillis();
            currentThreadTimeMillis = SystemClock.currentThreadTimeMillis();
            j = 0;
            ArrayDeque<UIOperation> arrayDeque2 = null;
            if (!this.mViewCommandOperations.isEmpty()) {
                ArrayList<DispatchCommandViewOperation> arrayList3 = this.mViewCommandOperations;
                this.mViewCommandOperations = new ArrayList<>();
                arrayList = arrayList3;
            } else {
                arrayList = null;
            }
            if (!this.mOperations.isEmpty()) {
                ArrayList<UIOperation> arrayList4 = this.mOperations;
                this.mOperations = new ArrayList<>();
                arrayList2 = arrayList4;
            } else {
                arrayList2 = null;
            }
            synchronized (this.mNonBatchedOperationsLock) {
                try {
                    if (!this.mNonBatchedOperations.isEmpty()) {
                        arrayDeque2 = this.mNonBatchedOperations;
                        this.mNonBatchedOperations = new ArrayDeque<>();
                    }
                    arrayDeque = arrayDeque2;
                } catch (Throwable th2) {
                    th = th2;
                }
            }
            NotThreadSafeViewHierarchyUpdateDebugListener notThreadSafeViewHierarchyUpdateDebugListener = this.mViewHierarchyUpdateDebugListener;
            if (notThreadSafeViewHierarchyUpdateDebugListener != null) {
                notThreadSafeViewHierarchyUpdateDebugListener.onViewHierarchyUpdateEnqueued();
            }
        } catch (Throwable th3) {
            th = th3;
            j = 0;
        }
        try {
            Runnable runnable = new Runnable() { // from class: com.facebook.react.uimanager.UIViewOperationQueue.1
                @Override // java.lang.Runnable
                public void run() {
                    SystraceMessage.beginSection(0L, "DispatchUI").arg("BatchId", batchId).flush();
                    try {
                        try {
                            long uptimeMillis2 = SystemClock.uptimeMillis();
                            ArrayList arrayList5 = arrayList;
                            if (arrayList5 != null) {
                                Iterator it = arrayList5.iterator();
                                while (it.hasNext()) {
                                    DispatchCommandViewOperation dispatchCommandViewOperation = (DispatchCommandViewOperation) it.next();
                                    try {
                                        dispatchCommandViewOperation.executeWithExceptions();
                                    } catch (RetryableMountingLayerException e) {
                                        if (dispatchCommandViewOperation.getRetries() == 0) {
                                            dispatchCommandViewOperation.incrementRetries();
                                            UIViewOperationQueue.this.mViewCommandOperations.add(dispatchCommandViewOperation);
                                        } else {
                                            ReactSoftExceptionLogger.logSoftException(UIViewOperationQueue.TAG, new ReactNoCrashSoftException(e));
                                        }
                                    } catch (Throwable th4) {
                                        ReactSoftExceptionLogger.logSoftException(UIViewOperationQueue.TAG, th4);
                                    }
                                }
                            }
                            ArrayDeque arrayDeque3 = arrayDeque;
                            if (arrayDeque3 != null) {
                                Iterator it2 = arrayDeque3.iterator();
                                while (it2.hasNext()) {
                                    ((UIOperation) it2.next()).execute();
                                }
                            }
                            ArrayList arrayList6 = arrayList2;
                            if (arrayList6 != null) {
                                Iterator it3 = arrayList6.iterator();
                                while (it3.hasNext()) {
                                    ((UIOperation) it3.next()).execute();
                                }
                            }
                            if (UIViewOperationQueue.this.mIsProfilingNextBatch && UIViewOperationQueue.this.mProfiledBatchCommitStartTime == 0) {
                                UIViewOperationQueue.this.mProfiledBatchCommitStartTime = commitStartTime;
                                UIViewOperationQueue.this.mProfiledBatchCommitEndTime = SystemClock.uptimeMillis();
                                UIViewOperationQueue.this.mProfiledBatchLayoutTime = layoutTime;
                                UIViewOperationQueue.this.mProfiledBatchDispatchViewUpdatesTime = uptimeMillis;
                                UIViewOperationQueue.this.mProfiledBatchRunStartTime = uptimeMillis2;
                                UIViewOperationQueue uIViewOperationQueue = UIViewOperationQueue.this;
                                uIViewOperationQueue.mProfiledBatchRunEndTime = uIViewOperationQueue.mProfiledBatchCommitEndTime;
                                UIViewOperationQueue.this.mThreadCpuTime = currentThreadTimeMillis;
                                Systrace.beginAsyncSection(0L, "delayBeforeDispatchViewUpdates", 0, UIViewOperationQueue.this.mProfiledBatchCommitStartTime * 1000000);
                                Systrace.endAsyncSection(0L, "delayBeforeDispatchViewUpdates", 0, UIViewOperationQueue.this.mProfiledBatchDispatchViewUpdatesTime * 1000000);
                                Systrace.beginAsyncSection(0L, "delayBeforeBatchRunStart", 0, UIViewOperationQueue.this.mProfiledBatchDispatchViewUpdatesTime * 1000000);
                                Systrace.endAsyncSection(0L, "delayBeforeBatchRunStart", 0, UIViewOperationQueue.this.mProfiledBatchRunStartTime * 1000000);
                            }
                            UIViewOperationQueue.this.mNativeViewHierarchyManager.clearLayoutAnimation();
                            if (UIViewOperationQueue.this.mViewHierarchyUpdateDebugListener != null) {
                                UIViewOperationQueue.this.mViewHierarchyUpdateDebugListener.onViewHierarchyUpdateFinished();
                            }
                        } catch (Exception e2) {
                            UIViewOperationQueue.this.mIsInIllegalUIState = true;
                            throw e2;
                        }
                    } finally {
                        Systrace.endSection(0L);
                    }
                }
            };
            j = 0;
            SystraceMessage.beginSection(0L, "acquiring mDispatchRunnablesLock").arg("batchId", batchId).flush();
            synchronized (this.mDispatchRunnablesLock) {
                Systrace.endSection(0L);
                this.mDispatchUIRunnables.add(runnable);
            }
            if (!this.mIsDispatchUIFrameCallbackEnqueued) {
                UiThreadUtil.runOnUiThread(new GuardedRunnable(this.mReactApplicationContext) { // from class: com.facebook.react.uimanager.UIViewOperationQueue.2
                    @Override // com.facebook.react.bridge.GuardedRunnable
                    public void runGuarded() {
                        UIViewOperationQueue.this.flushPendingBatches();
                    }
                });
            }
            Systrace.endSection(0L);
        } catch (Throwable th4) {
            th = th4;
            j = 0;
            Systrace.endSection(j);
            throw th;
        }
    }

    public void resumeFrameCallback() {
        this.mIsDispatchUIFrameCallbackEnqueued = true;
        ReactChoreographer.getInstance().postFrameCallback(ReactChoreographer.CallbackType.DISPATCH_UI, this.mDispatchUIFrameCallback);
    }

    public void pauseFrameCallback() {
        this.mIsDispatchUIFrameCallbackEnqueued = false;
        ReactChoreographer.getInstance().removeFrameCallback(ReactChoreographer.CallbackType.DISPATCH_UI, this.mDispatchUIFrameCallback);
        flushPendingBatches();
    }

    public void flushPendingBatches() {
        if (this.mIsInIllegalUIState) {
            FLog.w(ReactConstants.TAG, "Not flushing pending UI operations because of previously thrown Exception");
            return;
        }
        synchronized (this.mDispatchRunnablesLock) {
            if (this.mDispatchUIRunnables.isEmpty()) {
                return;
            }
            ArrayList<Runnable> arrayList = this.mDispatchUIRunnables;
            this.mDispatchUIRunnables = new ArrayList<>();
            long uptimeMillis = SystemClock.uptimeMillis();
            Iterator<Runnable> it = arrayList.iterator();
            while (it.hasNext()) {
                it.next().run();
            }
            if (this.mIsProfilingNextBatch) {
                this.mProfiledBatchBatchedExecutionTime = SystemClock.uptimeMillis() - uptimeMillis;
                this.mProfiledBatchNonBatchedExecutionTime = this.mNonBatchedExecutionTotalTime;
                this.mIsProfilingNextBatch = false;
                Systrace.beginAsyncSection(0L, "batchedExecutionTime", 0, 1000000 * uptimeMillis);
                Systrace.endAsyncSection(0L, "batchedExecutionTime", 0);
            }
            this.mNonBatchedExecutionTotalTime = 0L;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class DispatchUIFrameCallback extends GuardedFrameCallback {
        private static final int FRAME_TIME_MS = 16;
        private final int mMinTimeLeftInFrameForNonBatchedOperationMs;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        private DispatchUIFrameCallback(ReactContext reactContext, int minTimeLeftInFrameForNonBatchedOperationMs) {
            super(reactContext);
            UIViewOperationQueue.this = this$0;
            this.mMinTimeLeftInFrameForNonBatchedOperationMs = minTimeLeftInFrameForNonBatchedOperationMs;
        }

        /* JADX WARN: Finally extract failed */
        @Override // com.facebook.react.uimanager.GuardedFrameCallback
        public void doFrameGuarded(long frameTimeNanos) {
            if (UIViewOperationQueue.this.mIsInIllegalUIState) {
                FLog.w(ReactConstants.TAG, "Not flushing pending UI operations because of previously thrown Exception");
                return;
            }
            Systrace.beginSection(0L, "dispatchNonBatchedUIOperations");
            try {
                dispatchPendingNonBatchedOperations(frameTimeNanos);
                Systrace.endSection(0L);
                UIViewOperationQueue.this.flushPendingBatches();
                ReactChoreographer.getInstance().postFrameCallback(ReactChoreographer.CallbackType.DISPATCH_UI, this);
            } catch (Throwable th) {
                Systrace.endSection(0L);
                throw th;
            }
        }

        private void dispatchPendingNonBatchedOperations(long frameTimeNanos) {
            UIOperation uIOperation;
            while (16 - ((System.nanoTime() - frameTimeNanos) / 1000000) >= this.mMinTimeLeftInFrameForNonBatchedOperationMs) {
                synchronized (UIViewOperationQueue.this.mNonBatchedOperationsLock) {
                    if (UIViewOperationQueue.this.mNonBatchedOperations.isEmpty()) {
                        return;
                    }
                    uIOperation = (UIOperation) UIViewOperationQueue.this.mNonBatchedOperations.pollFirst();
                }
                try {
                    long uptimeMillis = SystemClock.uptimeMillis();
                    uIOperation.execute();
                    UIViewOperationQueue.this.mNonBatchedExecutionTotalTime += SystemClock.uptimeMillis() - uptimeMillis;
                } catch (Exception e) {
                    UIViewOperationQueue.this.mIsInIllegalUIState = true;
                    throw e;
                }
            }
        }
    }
}
