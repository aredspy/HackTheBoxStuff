package com.facebook.react.fabric.mounting;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import com.facebook.common.logging.FLog;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.ReactNoCrashSoftException;
import com.facebook.react.bridge.ReactSoftExceptionLogger;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.RetryableMountingLayerException;
import com.facebook.react.bridge.SoftAssertions;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.config.ReactFeatureFlags;
import com.facebook.react.fabric.events.EventEmitterWrapper;
import com.facebook.react.fabric.mounting.MountingManager;
import com.facebook.react.fabric.mounting.mountitems.MountItem;
import com.facebook.react.touch.JSResponderHandler;
import com.facebook.react.uimanager.IllegalViewOperationException;
import com.facebook.react.uimanager.ReactRoot;
import com.facebook.react.uimanager.ReactStylesDiffMap;
import com.facebook.react.uimanager.RootView;
import com.facebook.react.uimanager.RootViewManager;
import com.facebook.react.uimanager.StateWrapper;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.react.uimanager.ViewManagerRegistry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class SurfaceMountingManager {
    private static final boolean SHOW_CHANGED_VIEW_HIERARCHIES = false;
    public static final String TAG = "SurfaceMountingManager";
    private JSResponderHandler mJSResponderHandler;
    private MountingManager.MountItemExecutor mMountItemExecutor;
    private RootViewManager mRootViewManager;
    private final int mSurfaceId;
    private Set<Integer> mTagSetForStoppedSurface;
    @Nullable
    private ThemedReactContext mThemedReactContext;
    private ViewManagerRegistry mViewManagerRegistry;
    private volatile boolean mIsStopped = false;
    private volatile boolean mRootViewAttached = false;
    private ConcurrentHashMap<Integer, ViewState> mTagToViewState = new ConcurrentHashMap<>();
    private ConcurrentLinkedQueue<MountItem> mOnViewAttachItems = new ConcurrentLinkedQueue<>();

    public SurfaceMountingManager(int surfaceId, JSResponderHandler jsResponderHandler, ViewManagerRegistry viewManagerRegistry, RootViewManager rootViewManager, MountingManager.MountItemExecutor mountItemExecutor) {
        this.mSurfaceId = surfaceId;
        this.mJSResponderHandler = jsResponderHandler;
        this.mViewManagerRegistry = viewManagerRegistry;
        this.mRootViewManager = rootViewManager;
        this.mMountItemExecutor = mountItemExecutor;
    }

    public boolean isStopped() {
        return this.mIsStopped;
    }

    public void attachRootView(View rootView, ThemedReactContext themedReactContext) {
        this.mThemedReactContext = themedReactContext;
        addRootView(rootView);
    }

    public int getSurfaceId() {
        return this.mSurfaceId;
    }

    public boolean isRootViewAttached() {
        return this.mRootViewAttached;
    }

    @Nullable
    public ThemedReactContext getContext() {
        return this.mThemedReactContext;
    }

    private static void logViewHierarchy(ViewGroup parent, boolean recurse) {
        int id = parent.getId();
        String str = TAG;
        FLog.e(str, "  <ViewGroup tag=" + id + " class=" + parent.getClass().toString() + ">");
        for (int i = 0; i < parent.getChildCount(); i++) {
            String str2 = TAG;
            FLog.e(str2, "     <View idx=" + i + " tag=" + parent.getChildAt(i).getId() + " class=" + parent.getChildAt(i).getClass().toString() + ">");
        }
        String str3 = TAG;
        FLog.e(str3, "  </ViewGroup tag=" + id + ">");
        if (recurse) {
            FLog.e(str3, "Displaying Ancestors:");
            for (ViewParent parent2 = parent.getParent(); parent2 != null; parent2 = parent2.getParent()) {
                ViewGroup viewGroup = parent2 instanceof ViewGroup ? (ViewGroup) parent2 : null;
                int id2 = viewGroup == null ? -1 : viewGroup.getId();
                String str4 = TAG;
                FLog.e(str4, "<ViewParent tag=" + id2 + " class=" + parent2.getClass().toString() + ">");
            }
        }
    }

    public boolean getViewExists(int tag) {
        Set<Integer> set = this.mTagSetForStoppedSurface;
        if (set == null || !set.contains(Integer.valueOf(tag))) {
            ConcurrentHashMap<Integer, ViewState> concurrentHashMap = this.mTagToViewState;
            if (concurrentHashMap != null) {
                return concurrentHashMap.containsKey(Integer.valueOf(tag));
            }
            return false;
        }
        return true;
    }

    public void executeOnViewAttach(MountItem item) {
        this.mOnViewAttachItems.add(item);
    }

    private void addRootView(final View rootView) {
        if (isStopped()) {
            return;
        }
        this.mTagToViewState.put(Integer.valueOf(this.mSurfaceId), new ViewState(this.mSurfaceId, rootView, this.mRootViewManager, true));
        Runnable runnable = new Runnable() { // from class: com.facebook.react.fabric.mounting.SurfaceMountingManager.1
            @Override // java.lang.Runnable
            public void run() {
                if (SurfaceMountingManager.this.isStopped()) {
                    return;
                }
                if (rootView.getId() == SurfaceMountingManager.this.mSurfaceId) {
                    String str = SurfaceMountingManager.TAG;
                    ReactSoftExceptionLogger.logSoftException(str, new IllegalViewOperationException("Race condition in addRootView detected. Trying to set an id of [" + SurfaceMountingManager.this.mSurfaceId + "] on the RootView, but that id has already been set. "));
                } else if (rootView.getId() != -1) {
                    FLog.e(SurfaceMountingManager.TAG, "Trying to add RootTag to RootView that already has a tag: existing tag: [%d] new tag: [%d]", Integer.valueOf(rootView.getId()), Integer.valueOf(SurfaceMountingManager.this.mSurfaceId));
                    throw new IllegalViewOperationException("Trying to add a root view with an explicit id already set. React Native uses the id field to track react tags and will overwrite this field. If that is fine, explicitly overwrite the id field to View.NO_ID before calling addRootView.");
                }
                rootView.setId(SurfaceMountingManager.this.mSurfaceId);
                View view = rootView;
                if (view instanceof ReactRoot) {
                    ((ReactRoot) view).setRootViewTag(SurfaceMountingManager.this.mSurfaceId);
                }
                SurfaceMountingManager.this.mRootViewAttached = true;
                SurfaceMountingManager.this.executeViewAttachMountItems();
            }
        };
        if (UiThreadUtil.isOnUiThread()) {
            runnable.run();
        } else {
            UiThreadUtil.runOnUiThread(runnable);
        }
    }

    public void executeViewAttachMountItems() {
        this.mMountItemExecutor.executeItems(this.mOnViewAttachItems);
    }

    public void stopSurface() {
        if (isStopped()) {
            return;
        }
        this.mIsStopped = true;
        for (ViewState viewState : this.mTagToViewState.values()) {
            if (viewState.mStateWrapper != null) {
                viewState.mStateWrapper.destroyState();
                viewState.mStateWrapper = null;
            }
            if (ReactFeatureFlags.enableAggressiveEventEmitterCleanup && viewState.mEventEmitter != null) {
                viewState.mEventEmitter.destroy();
                viewState.mEventEmitter = null;
            }
        }
        Runnable runnable = new Runnable() { // from class: com.facebook.react.fabric.mounting.SurfaceMountingManager.2
            @Override // java.lang.Runnable
            public void run() {
                for (ViewState viewState2 : SurfaceMountingManager.this.mTagToViewState.values()) {
                    SurfaceMountingManager.this.onViewStateDeleted(viewState2);
                }
                SurfaceMountingManager surfaceMountingManager = SurfaceMountingManager.this;
                surfaceMountingManager.mTagSetForStoppedSurface = surfaceMountingManager.mTagToViewState.keySet();
                SurfaceMountingManager.this.mTagToViewState = null;
                SurfaceMountingManager.this.mJSResponderHandler = null;
                SurfaceMountingManager.this.mRootViewManager = null;
                SurfaceMountingManager.this.mMountItemExecutor = null;
                SurfaceMountingManager.this.mOnViewAttachItems.clear();
            }
        };
        if (UiThreadUtil.isOnUiThread()) {
            runnable.run();
        } else {
            UiThreadUtil.runOnUiThread(runnable);
        }
    }

    public void addViewAt(final int parentTag, final int tag, final int index) {
        UiThreadUtil.assertOnUiThread();
        if (isStopped()) {
            return;
        }
        ViewState viewState = getViewState(parentTag);
        if (!(viewState.mView instanceof ViewGroup)) {
            String str = "Unable to add a view into a view that is not a ViewGroup. ParentTag: " + parentTag + " - Tag: " + tag + " - Index: " + index;
            FLog.e(TAG, str);
            throw new IllegalStateException(str);
        }
        ViewGroup viewGroup = (ViewGroup) viewState.mView;
        ViewState viewState2 = getViewState(tag);
        View view = viewState2.mView;
        if (view == null) {
            throw new IllegalStateException("Unable to find view for viewState " + viewState2 + " and tag " + tag);
        }
        ViewParent parent = view.getParent();
        if (parent != null) {
            int id = parent instanceof ViewGroup ? ((ViewGroup) parent).getId() : -1;
            ReactSoftExceptionLogger.logSoftException(TAG, new IllegalStateException("addViewAt: cannot insert view [" + tag + "] into parent [" + parentTag + "]: View already has a parent: [" + id + "] " + parent.getClass().getSimpleName()));
        }
        try {
            getViewGroupManager(viewState).addView(viewGroup, view, index);
        } catch (IllegalStateException e) {
            throw new IllegalStateException("addViewAt: failed to insert view [" + tag + "] into parent [" + parentTag + "] at index " + index, e);
        }
    }

    public void removeViewAt(final int tag, final int parentTag, int index) {
        if (isStopped()) {
            return;
        }
        UiThreadUtil.assertOnUiThread();
        ViewState nullableViewState = getNullableViewState(parentTag);
        if (nullableViewState == null) {
            ReactSoftExceptionLogger.logSoftException(MountingManager.TAG, new IllegalStateException("Unable to find viewState for tag: [" + parentTag + "] for removeViewAt"));
        } else if (!(nullableViewState.mView instanceof ViewGroup)) {
            String str = "Unable to remove a view from a view that is not a ViewGroup. ParentTag: " + parentTag + " - Tag: " + tag + " - Index: " + index;
            FLog.e(TAG, str);
            throw new IllegalStateException(str);
        } else {
            ViewGroup viewGroup = (ViewGroup) nullableViewState.mView;
            if (viewGroup == null) {
                throw new IllegalStateException("Unable to find view for tag [" + parentTag + "]");
            }
            ViewGroupManager<ViewGroup> viewGroupManager = getViewGroupManager(nullableViewState);
            View childAt = viewGroupManager.getChildAt(viewGroup, index);
            int id = childAt != null ? childAt.getId() : -1;
            if (id != tag) {
                int childCount = viewGroup.getChildCount();
                int i = 0;
                while (true) {
                    if (i >= childCount) {
                        i = -1;
                        break;
                    } else if (viewGroup.getChildAt(i).getId() == tag) {
                        break;
                    } else {
                        i++;
                    }
                }
                if (i == -1) {
                    FLog.e(TAG, "removeViewAt: [" + tag + "] -> [" + parentTag + "] @" + index + ": view already removed from parent! Children in parent: " + childCount);
                    return;
                }
                logViewHierarchy(viewGroup, true);
                ReactSoftExceptionLogger.logSoftException(TAG, new IllegalStateException("Tried to remove view [" + tag + "] of parent [" + parentTag + "] at index " + index + ", but got view tag " + id + " - actual index of view: " + i));
                index = i;
            }
            try {
                viewGroupManager.removeViewAt(viewGroup, index);
            } catch (RuntimeException e) {
                int childCount2 = viewGroupManager.getChildCount(viewGroup);
                logViewHierarchy(viewGroup, true);
                throw new IllegalStateException("Cannot remove child at index " + index + " from parent ViewGroup [" + viewGroup.getId() + "], only " + childCount2 + " children in parent. Warning: childCount may be incorrect!", e);
            }
        }
    }

    public void createView(String componentName, int reactTag, @Nullable ReadableMap props, @Nullable StateWrapper stateWrapper, @Nullable EventEmitterWrapper eventEmitterWrapper, boolean isLayoutable) {
        if (isStopped()) {
            return;
        }
        if (getNullableViewState(reactTag) != null) {
            String str = TAG;
            ReactSoftExceptionLogger.logSoftException(str, new ReactNoCrashSoftException("Cannot CREATE view with tag [" + reactTag + "], already exists."));
            return;
        }
        createViewUnsafe(componentName, reactTag, props, stateWrapper, eventEmitterWrapper, isLayoutable);
    }

    public void createViewUnsafe(String componentName, int reactTag, @Nullable ReadableMap props, @Nullable StateWrapper stateWrapper, @Nullable EventEmitterWrapper eventEmitterWrapper, boolean isLayoutable) {
        View view;
        ViewManager viewManager;
        ReactStylesDiffMap reactStylesDiffMap = props != null ? new ReactStylesDiffMap(props) : null;
        if (isLayoutable) {
            viewManager = this.mViewManagerRegistry.get(componentName);
            view = viewManager.createView(reactTag, this.mThemedReactContext, reactStylesDiffMap, stateWrapper, this.mJSResponderHandler);
        } else {
            viewManager = null;
            view = null;
        }
        ViewState viewState = new ViewState(reactTag, view, viewManager);
        viewState.mCurrentProps = reactStylesDiffMap;
        viewState.mStateWrapper = stateWrapper;
        viewState.mEventEmitter = eventEmitterWrapper;
        this.mTagToViewState.put(Integer.valueOf(reactTag), viewState);
    }

    public void updateProps(int reactTag, ReadableMap props) {
        if (isStopped()) {
            return;
        }
        ViewState viewState = getViewState(reactTag);
        viewState.mCurrentProps = new ReactStylesDiffMap(props);
        View view = viewState.mView;
        if (view == null) {
            throw new IllegalStateException("Unable to find view for tag [" + reactTag + "]");
        }
        ((ViewManager) Assertions.assertNotNull(viewState.mViewManager)).updateProperties(view, viewState.mCurrentProps);
    }

    @Deprecated
    public void receiveCommand(int reactTag, int commandId, @Nullable ReadableArray commandArgs) {
        if (isStopped()) {
            return;
        }
        ViewState nullableViewState = getNullableViewState(reactTag);
        if (nullableViewState == null) {
            throw new RetryableMountingLayerException("Unable to find viewState for tag: [" + reactTag + "] for commandId: " + commandId);
        } else if (nullableViewState.mViewManager == null) {
            throw new RetryableMountingLayerException("Unable to find viewManager for tag " + reactTag);
        } else if (nullableViewState.mView == null) {
            throw new RetryableMountingLayerException("Unable to find viewState view for tag " + reactTag);
        } else {
            nullableViewState.mViewManager.receiveCommand((ViewManager) nullableViewState.mView, commandId, commandArgs);
        }
    }

    public void receiveCommand(int reactTag, String commandId, @Nullable ReadableArray commandArgs) {
        if (isStopped()) {
            return;
        }
        ViewState nullableViewState = getNullableViewState(reactTag);
        if (nullableViewState == null) {
            throw new RetryableMountingLayerException("Unable to find viewState for tag: " + reactTag + " for commandId: " + commandId);
        } else if (nullableViewState.mViewManager == null) {
            throw new RetryableMountingLayerException("Unable to find viewState manager for tag " + reactTag);
        } else if (nullableViewState.mView == null) {
            throw new RetryableMountingLayerException("Unable to find viewState view for tag " + reactTag);
        } else {
            nullableViewState.mViewManager.receiveCommand((ViewManager) nullableViewState.mView, commandId, commandArgs);
        }
    }

    public void sendAccessibilityEvent(int reactTag, int eventType) {
        if (isStopped()) {
            return;
        }
        ViewState viewState = getViewState(reactTag);
        if (viewState.mViewManager == null) {
            throw new RetryableMountingLayerException("Unable to find viewState manager for tag " + reactTag);
        } else if (viewState.mView == null) {
            throw new RetryableMountingLayerException("Unable to find viewState view for tag " + reactTag);
        } else {
            viewState.mView.sendAccessibilityEvent(eventType);
        }
    }

    public void updateLayout(int reactTag, int x, int y, int width, int height, int displayType) {
        if (isStopped()) {
            return;
        }
        ViewState viewState = getViewState(reactTag);
        if (viewState.mIsRoot) {
            return;
        }
        View view = viewState.mView;
        if (view == null) {
            throw new IllegalStateException("Unable to find View for tag: " + reactTag);
        }
        view.measure(View.MeasureSpec.makeMeasureSpec(width, 1073741824), View.MeasureSpec.makeMeasureSpec(height, 1073741824));
        ViewParent parent = view.getParent();
        if (parent instanceof RootView) {
            parent.requestLayout();
        }
        view.layout(x, y, width + x, height + y);
        int i = displayType == 0 ? 4 : 0;
        if (view.getVisibility() == i) {
            return;
        }
        view.setVisibility(i);
    }

    public void updatePadding(int reactTag, int left, int top, int right, int bottom) {
        UiThreadUtil.assertOnUiThread();
        if (isStopped()) {
            return;
        }
        ViewState viewState = getViewState(reactTag);
        if (viewState.mIsRoot) {
            return;
        }
        View view = viewState.mView;
        if (view == null) {
            throw new IllegalStateException("Unable to find View for tag: " + reactTag);
        }
        ViewManager viewManager = viewState.mViewManager;
        if (viewManager == null) {
            throw new IllegalStateException("Unable to find ViewManager for view: " + viewState);
        }
        viewManager.setPadding(view, left, top, right, bottom);
    }

    public void updateState(final int reactTag, @Nullable StateWrapper stateWrapper) {
        UiThreadUtil.assertOnUiThread();
        if (isStopped()) {
            return;
        }
        ViewState viewState = getViewState(reactTag);
        StateWrapper stateWrapper2 = viewState.mStateWrapper;
        viewState.mStateWrapper = stateWrapper;
        ViewManager viewManager = viewState.mViewManager;
        if (viewManager == null) {
            throw new IllegalStateException("Unable to find ViewManager for tag: " + reactTag);
        }
        Object updateState = viewManager.updateState(viewState.mView, viewState.mCurrentProps, stateWrapper);
        if (updateState != null) {
            viewManager.updateExtraData(viewState.mView, updateState);
        }
        if (stateWrapper2 == null) {
            return;
        }
        stateWrapper2.destroyState();
    }

    public void updateEventEmitter(int reactTag, EventEmitterWrapper eventEmitter) {
        UiThreadUtil.assertOnUiThread();
        if (isStopped()) {
            return;
        }
        ViewState viewState = this.mTagToViewState.get(Integer.valueOf(reactTag));
        if (viewState == null) {
            viewState = new ViewState(reactTag, (View) null, (ViewManager) null);
            this.mTagToViewState.put(Integer.valueOf(reactTag), viewState);
        }
        EventEmitterWrapper eventEmitterWrapper = viewState.mEventEmitter;
        viewState.mEventEmitter = eventEmitter;
        if (eventEmitterWrapper == eventEmitter || eventEmitterWrapper == null) {
            return;
        }
        eventEmitterWrapper.destroy();
    }

    public synchronized void setJSResponder(int reactTag, int initialReactTag, boolean blockNativeResponder) {
        UiThreadUtil.assertOnUiThread();
        if (isStopped()) {
            return;
        }
        if (!blockNativeResponder) {
            this.mJSResponderHandler.setJSResponder(initialReactTag, null);
            return;
        }
        ViewState viewState = getViewState(reactTag);
        View view = viewState.mView;
        if (initialReactTag != reactTag && (view instanceof ViewParent)) {
            this.mJSResponderHandler.setJSResponder(initialReactTag, (ViewParent) view);
        } else if (view == null) {
            SoftAssertions.assertUnreachable("Cannot find view for tag [" + reactTag + "].");
        } else {
            if (viewState.mIsRoot) {
                SoftAssertions.assertUnreachable("Cannot block native responder on [" + reactTag + "] that is a root view");
            }
            this.mJSResponderHandler.setJSResponder(initialReactTag, view.getParent());
        }
    }

    public void onViewStateDeleted(ViewState viewState) {
        if (viewState.mStateWrapper != null) {
            viewState.mStateWrapper.destroyState();
            viewState.mStateWrapper = null;
        }
        if (viewState.mEventEmitter != null) {
            viewState.mEventEmitter.destroy();
            viewState.mEventEmitter = null;
        }
        ViewManager viewManager = viewState.mViewManager;
        if (viewState.mIsRoot || viewManager == null) {
            return;
        }
        viewManager.onDropViewInstance(viewState.mView);
    }

    public void deleteView(int reactTag) {
        UiThreadUtil.assertOnUiThread();
        if (isStopped()) {
            return;
        }
        ViewState nullableViewState = getNullableViewState(reactTag);
        if (nullableViewState == null) {
            String str = MountingManager.TAG;
            ReactSoftExceptionLogger.logSoftException(str, new IllegalStateException("Unable to find viewState for tag: " + reactTag + " for deleteView"));
            return;
        }
        this.mTagToViewState.remove(Integer.valueOf(reactTag));
        onViewStateDeleted(nullableViewState);
    }

    public void preallocateView(String componentName, int reactTag, @Nullable ReadableMap props, @Nullable StateWrapper stateWrapper, @Nullable EventEmitterWrapper eventEmitterWrapper, boolean isLayoutable) {
        UiThreadUtil.assertOnUiThread();
        if (isStopped()) {
            return;
        }
        if (getNullableViewState(reactTag) != null) {
            String str = TAG;
            ReactSoftExceptionLogger.logSoftException(str, new IllegalStateException("Cannot Preallocate view with tag [" + reactTag + "], already exists."));
            return;
        }
        createViewUnsafe(componentName, reactTag, props, stateWrapper, eventEmitterWrapper, isLayoutable);
    }

    @Nullable
    public EventEmitterWrapper getEventEmitter(int reactTag) {
        ViewState nullableViewState = getNullableViewState(reactTag);
        if (nullableViewState == null) {
            return null;
        }
        return nullableViewState.mEventEmitter;
    }

    public View getView(int reactTag) {
        ViewState nullableViewState = getNullableViewState(reactTag);
        View view = nullableViewState == null ? null : nullableViewState.mView;
        if (view != null) {
            return view;
        }
        throw new IllegalViewOperationException("Trying to resolve view with tag " + reactTag + " which doesn't exist");
    }

    private ViewState getViewState(int tag) {
        ViewState viewState = this.mTagToViewState.get(Integer.valueOf(tag));
        if (viewState != null) {
            return viewState;
        }
        throw new RetryableMountingLayerException("Unable to find viewState for tag " + tag);
    }

    @Nullable
    private ViewState getNullableViewState(int tag) {
        ConcurrentHashMap<Integer, ViewState> concurrentHashMap = this.mTagToViewState;
        if (concurrentHashMap == null) {
            return null;
        }
        return concurrentHashMap.get(Integer.valueOf(tag));
    }

    private static ViewGroupManager<ViewGroup> getViewGroupManager(ViewState viewState) {
        if (viewState.mViewManager == null) {
            throw new IllegalStateException("Unable to find ViewManager for view: " + viewState);
        }
        return (ViewGroupManager) viewState.mViewManager;
    }

    /* loaded from: classes.dex */
    public static class ViewState {
        @Nullable
        public ReadableMap mCurrentLocalData;
        @Nullable
        public ReactStylesDiffMap mCurrentProps;
        @Nullable
        public EventEmitterWrapper mEventEmitter;
        final boolean mIsRoot;
        final int mReactTag;
        @Nullable
        public StateWrapper mStateWrapper;
        @Nullable
        final View mView;
        @Nullable
        final ViewManager mViewManager;

        private ViewState(int reactTag, @Nullable View view, @Nullable ViewManager viewManager) {
            this(reactTag, view, viewManager, false);
        }

        private ViewState(int reactTag, @Nullable View view, ViewManager viewManager, boolean isRoot) {
            this.mCurrentProps = null;
            this.mCurrentLocalData = null;
            this.mStateWrapper = null;
            this.mEventEmitter = null;
            this.mReactTag = reactTag;
            this.mView = view;
            this.mIsRoot = isRoot;
            this.mViewManager = viewManager;
        }

        public String toString() {
            boolean z = this.mViewManager == null;
            return "ViewState [" + this.mReactTag + "] - isRoot: " + this.mIsRoot + " - props: " + this.mCurrentProps + " - localData: " + this.mCurrentLocalData + " - viewManager: " + this.mViewManager + " - isLayoutOnly: " + z;
        }
    }
}
