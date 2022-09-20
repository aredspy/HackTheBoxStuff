package com.facebook.react.uimanager;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.PopupMenu;
import com.facebook.common.logging.FLog;
import com.facebook.react.R;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.RetryableMountingLayerException;
import com.facebook.react.bridge.SoftAssertions;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.touch.JSResponderHandler;
import com.facebook.react.uimanager.layoutanimation.LayoutAnimationController;
import com.facebook.react.uimanager.layoutanimation.LayoutAnimationListener;
import com.facebook.react.views.textinput.ReactEditTextInputConnectionWrapper;
import com.facebook.systrace.Systrace;
import com.facebook.systrace.SystraceMessage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
/* loaded from: classes.dex */
public class NativeViewHierarchyManager {
    private static final String TAG = "NativeViewHierarchyManager";
    private final boolean DEBUG_MODE;
    private final RectF mBoundingBox;
    private final JSResponderHandler mJSResponderHandler;
    private boolean mLayoutAnimationEnabled;
    private final LayoutAnimationController mLayoutAnimator;
    private HashMap<Integer, Set<Integer>> mPendingDeletionsForTag;
    private PopupMenu mPopupMenu;
    private final SparseBooleanArray mRootTags;
    private final RootViewManager mRootViewManager;
    private final SparseArray<ViewManager> mTagsToViewManagers;
    private final SparseArray<View> mTagsToViews;
    private final ViewManagerRegistry mViewManagers;

    public NativeViewHierarchyManager(ViewManagerRegistry viewManagers) {
        this(viewManagers, new RootViewManager());
    }

    public NativeViewHierarchyManager(ViewManagerRegistry viewManagers, RootViewManager manager) {
        this.DEBUG_MODE = false;
        this.mJSResponderHandler = new JSResponderHandler();
        this.mLayoutAnimator = new LayoutAnimationController();
        this.mBoundingBox = new RectF();
        this.mViewManagers = viewManagers;
        this.mTagsToViews = new SparseArray<>();
        this.mTagsToViewManagers = new SparseArray<>();
        this.mRootTags = new SparseBooleanArray();
        this.mRootViewManager = manager;
    }

    public final synchronized View resolveView(int tag) {
        View view;
        view = this.mTagsToViews.get(tag);
        if (view == null) {
            throw new IllegalViewOperationException("Trying to resolve view with tag " + tag + " which doesn't exist");
        }
        return view;
    }

    public final synchronized ViewManager resolveViewManager(int tag) {
        ViewManager viewManager;
        viewManager = this.mTagsToViewManagers.get(tag);
        if (viewManager == null) {
            throw new IllegalViewOperationException("ViewManager for tag " + tag + " could not be found.\n");
        }
        return viewManager;
    }

    public void setLayoutAnimationEnabled(boolean enabled) {
        this.mLayoutAnimationEnabled = enabled;
    }

    public synchronized void updateInstanceHandle(int tag, long instanceHandle) {
        UiThreadUtil.assertOnUiThread();
        try {
            updateInstanceHandle(resolveView(tag), instanceHandle);
        } catch (IllegalViewOperationException e) {
            String str = TAG;
            FLog.e(str, "Unable to update properties for view tag " + tag, e);
        }
    }

    public synchronized void updateProperties(int tag, ReactStylesDiffMap props) {
        UiThreadUtil.assertOnUiThread();
        try {
            ViewManager resolveViewManager = resolveViewManager(tag);
            View resolveView = resolveView(tag);
            if (props != null) {
                resolveViewManager.updateProperties(resolveView, props);
            }
        } catch (IllegalViewOperationException e) {
            String str = TAG;
            FLog.e(str, "Unable to update properties for view tag " + tag, e);
        }
    }

    public synchronized void updateViewExtraData(int tag, Object extraData) {
        UiThreadUtil.assertOnUiThread();
        resolveViewManager(tag).updateExtraData(resolveView(tag), extraData);
    }

    public synchronized void updateLayout(int parentTag, int tag, int x, int y, int width, int height) {
        UiThreadUtil.assertOnUiThread();
        SystraceMessage.beginSection(0L, "NativeViewHierarchyManager_updateLayout").arg("parentTag", parentTag).arg("tag", tag).flush();
        View resolveView = resolveView(tag);
        resolveView.measure(View.MeasureSpec.makeMeasureSpec(width, 1073741824), View.MeasureSpec.makeMeasureSpec(height, 1073741824));
        ViewParent parent = resolveView.getParent();
        if (parent instanceof RootView) {
            parent.requestLayout();
        }
        if (!this.mRootTags.get(parentTag)) {
            ViewManager viewManager = this.mTagsToViewManagers.get(parentTag);
            if (viewManager instanceof IViewManagerWithChildren) {
                IViewManagerWithChildren iViewManagerWithChildren = (IViewManagerWithChildren) viewManager;
                if (iViewManagerWithChildren != null && !iViewManagerWithChildren.needsCustomLayoutForChildren()) {
                    updateLayout(resolveView, x, y, width, height);
                }
            } else {
                throw new IllegalViewOperationException("Trying to use view with tag " + parentTag + " as a parent, but its Manager doesn't implement IViewManagerWithChildren");
            }
        } else {
            updateLayout(resolveView, x, y, width, height);
        }
        Systrace.endSection(0L);
    }

    private void updateInstanceHandle(View viewToUpdate, long instanceHandle) {
        UiThreadUtil.assertOnUiThread();
        viewToUpdate.setTag(R.id.view_tag_instance_handle, Long.valueOf(instanceHandle));
    }

    public long getInstanceHandle(int reactTag) {
        View view = this.mTagsToViews.get(reactTag);
        if (view == null) {
            throw new IllegalViewOperationException("Unable to find view for tag: " + reactTag);
        }
        Long l = (Long) view.getTag(R.id.view_tag_instance_handle);
        if (l == null) {
            throw new IllegalViewOperationException("Unable to find instanceHandle for tag: " + reactTag);
        }
        return l.longValue();
    }

    private void updateLayout(View viewToUpdate, int x, int y, int width, int height) {
        if (this.mLayoutAnimationEnabled && this.mLayoutAnimator.shouldAnimateLayout(viewToUpdate)) {
            this.mLayoutAnimator.applyLayoutUpdate(viewToUpdate, x, y, width, height);
        } else {
            viewToUpdate.layout(x, y, width + x, height + y);
        }
    }

    public synchronized void createView(ThemedReactContext themedContext, int tag, String className, ReactStylesDiffMap initialProps) {
        UiThreadUtil.assertOnUiThread();
        SystraceMessage.beginSection(0L, "NativeViewHierarchyManager_createView").arg("tag", tag).arg("className", className).flush();
        ViewManager viewManager = this.mViewManagers.get(className);
        this.mTagsToViews.put(tag, viewManager.createView(tag, themedContext, initialProps, null, this.mJSResponderHandler));
        this.mTagsToViewManagers.put(tag, viewManager);
        Systrace.endSection(0L);
    }

    private static String constructManageChildrenErrorMessage(ViewGroup viewToManage, ViewGroupManager viewManager, int[] indicesToRemove, ViewAtIndex[] viewsToAdd, int[] tagsToDelete) {
        StringBuilder sb = new StringBuilder();
        if (viewToManage != null) {
            sb.append("View tag:" + viewToManage.getId() + " View Type:" + viewToManage.getClass().toString() + ReactEditTextInputConnectionWrapper.NEWLINE_RAW_VALUE);
            StringBuilder sb2 = new StringBuilder();
            sb2.append("  children(");
            sb2.append(viewManager.getChildCount(viewToManage));
            sb2.append("): [\n");
            sb.append(sb2.toString());
            for (int i = 0; viewManager.getChildAt(viewToManage, i) != null; i += 16) {
                int i2 = 0;
                while (true) {
                    int i3 = i + i2;
                    if (viewManager.getChildAt(viewToManage, i3) != null && i2 < 16) {
                        sb.append(viewManager.getChildAt(viewToManage, i3).getId() + ",");
                        i2++;
                    }
                }
                sb.append(ReactEditTextInputConnectionWrapper.NEWLINE_RAW_VALUE);
            }
            sb.append(" ],\n");
        }
        if (indicesToRemove != null) {
            sb.append("  indicesToRemove(" + indicesToRemove.length + "): [\n");
            for (int i4 = 0; i4 < indicesToRemove.length; i4 += 16) {
                int i5 = 0;
                while (true) {
                    int i6 = i4 + i5;
                    if (i6 < indicesToRemove.length && i5 < 16) {
                        sb.append(indicesToRemove[i6] + ",");
                        i5++;
                    }
                }
                sb.append(ReactEditTextInputConnectionWrapper.NEWLINE_RAW_VALUE);
            }
            sb.append(" ],\n");
        }
        if (viewsToAdd != null) {
            sb.append("  viewsToAdd(" + viewsToAdd.length + "): [\n");
            for (int i7 = 0; i7 < viewsToAdd.length; i7 += 16) {
                int i8 = 0;
                while (true) {
                    int i9 = i7 + i8;
                    if (i9 < viewsToAdd.length && i8 < 16) {
                        sb.append("[" + viewsToAdd[i9].mIndex + "," + viewsToAdd[i9].mTag + "],");
                        i8++;
                    }
                }
                sb.append(ReactEditTextInputConnectionWrapper.NEWLINE_RAW_VALUE);
            }
            sb.append(" ],\n");
        }
        if (tagsToDelete != null) {
            sb.append("  tagsToDelete(" + tagsToDelete.length + "): [\n");
            for (int i10 = 0; i10 < tagsToDelete.length; i10 += 16) {
                int i11 = 0;
                while (true) {
                    int i12 = i10 + i11;
                    if (i12 < tagsToDelete.length && i11 < 16) {
                        sb.append(tagsToDelete[i12] + ",");
                        i11++;
                    }
                }
                sb.append(ReactEditTextInputConnectionWrapper.NEWLINE_RAW_VALUE);
            }
            sb.append(" ]\n");
        }
        return sb.toString();
    }

    private Set<Integer> getPendingDeletionsForTag(int tag) {
        if (this.mPendingDeletionsForTag == null) {
            this.mPendingDeletionsForTag = new HashMap<>();
        }
        if (!this.mPendingDeletionsForTag.containsKey(Integer.valueOf(tag))) {
            this.mPendingDeletionsForTag.put(Integer.valueOf(tag), new HashSet());
        }
        return this.mPendingDeletionsForTag.get(Integer.valueOf(tag));
    }

    public synchronized void manageChildren(final int tag, int[] indicesToRemove, ViewAtIndex[] viewsToAdd, int[] tagsToDelete) {
        int i;
        int[] iArr = indicesToRemove;
        synchronized (this) {
            UiThreadUtil.assertOnUiThread();
            final Set<Integer> pendingDeletionsForTag = getPendingDeletionsForTag(tag);
            final ViewGroup viewGroup = (ViewGroup) this.mTagsToViews.get(tag);
            final ViewGroupManager viewGroupManager = (ViewGroupManager) resolveViewManager(tag);
            if (viewGroup == null) {
                throw new IllegalViewOperationException("Trying to manageChildren view with tag " + tag + " which doesn't exist\n detail: " + constructManageChildrenErrorMessage(viewGroup, viewGroupManager, iArr, viewsToAdd, tagsToDelete));
            }
            int childCount = viewGroupManager.getChildCount(viewGroup);
            if (iArr != null) {
                int length = iArr.length - 1;
                while (length >= 0) {
                    int i2 = iArr[length];
                    if (i2 < 0) {
                        throw new IllegalViewOperationException("Trying to remove a negative view index:" + i2 + " view tag: " + tag + "\n detail: " + constructManageChildrenErrorMessage(viewGroup, viewGroupManager, iArr, viewsToAdd, tagsToDelete));
                    } else if (viewGroupManager.getChildAt(viewGroup, i2) == null) {
                        if (this.mRootTags.get(tag) && viewGroupManager.getChildCount(viewGroup) == 0) {
                            return;
                        }
                        throw new IllegalViewOperationException("Trying to remove a view index above child count " + i2 + " view tag: " + tag + "\n detail: " + constructManageChildrenErrorMessage(viewGroup, viewGroupManager, iArr, viewsToAdd, tagsToDelete));
                    } else if (i2 >= childCount) {
                        throw new IllegalViewOperationException("Trying to remove an out of order view index:" + i2 + " view tag: " + tag + "\n detail: " + constructManageChildrenErrorMessage(viewGroup, viewGroupManager, iArr, viewsToAdd, tagsToDelete));
                    } else {
                        View childAt = viewGroupManager.getChildAt(viewGroup, i2);
                        if (!this.mLayoutAnimationEnabled || !this.mLayoutAnimator.shouldAnimateLayout(childAt) || !arrayContains(tagsToDelete, childAt.getId())) {
                            viewGroupManager.removeViewAt(viewGroup, i2);
                        }
                        length--;
                        childCount = i2;
                    }
                }
            }
            if (tagsToDelete != null) {
                int i3 = 0;
                while (i3 < tagsToDelete.length) {
                    int i4 = tagsToDelete[i3];
                    final View view = this.mTagsToViews.get(i4);
                    if (view == null) {
                        throw new IllegalViewOperationException("Trying to destroy unknown view tag: " + i4 + "\n detail: " + constructManageChildrenErrorMessage(viewGroup, viewGroupManager, indicesToRemove, viewsToAdd, tagsToDelete));
                    }
                    if (this.mLayoutAnimationEnabled && this.mLayoutAnimator.shouldAnimateLayout(view)) {
                        pendingDeletionsForTag.add(Integer.valueOf(i4));
                        i = i3;
                        this.mLayoutAnimator.deleteView(view, new LayoutAnimationListener() { // from class: com.facebook.react.uimanager.NativeViewHierarchyManager.1
                            @Override // com.facebook.react.uimanager.layoutanimation.LayoutAnimationListener
                            public void onAnimationEnd() {
                                UiThreadUtil.assertOnUiThread();
                                viewGroupManager.removeView(viewGroup, view);
                                NativeViewHierarchyManager.this.dropView(view);
                                pendingDeletionsForTag.remove(Integer.valueOf(view.getId()));
                                if (pendingDeletionsForTag.isEmpty()) {
                                    NativeViewHierarchyManager.this.mPendingDeletionsForTag.remove(Integer.valueOf(tag));
                                }
                            }
                        });
                    } else {
                        i = i3;
                        dropView(view);
                    }
                    i3 = i + 1;
                    iArr = indicesToRemove;
                }
            }
            int[] iArr2 = iArr;
            if (viewsToAdd != null) {
                for (ViewAtIndex viewAtIndex : viewsToAdd) {
                    View view2 = this.mTagsToViews.get(viewAtIndex.mTag);
                    if (view2 == null) {
                        throw new IllegalViewOperationException("Trying to add unknown view tag: " + viewAtIndex.mTag + "\n detail: " + constructManageChildrenErrorMessage(viewGroup, viewGroupManager, iArr2, viewsToAdd, tagsToDelete));
                    }
                    int i5 = viewAtIndex.mIndex;
                    if (!pendingDeletionsForTag.isEmpty()) {
                        i5 = 0;
                        int i6 = 0;
                        while (i5 < viewGroup.getChildCount() && i6 != viewAtIndex.mIndex) {
                            if (!pendingDeletionsForTag.contains(Integer.valueOf(viewGroup.getChildAt(i5).getId()))) {
                                i6++;
                            }
                            i5++;
                        }
                    }
                    viewGroupManager.addView(viewGroup, view2, i5);
                }
            }
            if (pendingDeletionsForTag.isEmpty()) {
                this.mPendingDeletionsForTag.remove(Integer.valueOf(tag));
            }
        }
    }

    private boolean arrayContains(int[] array, int ele) {
        if (array == null) {
            return false;
        }
        for (int i : array) {
            if (i == ele) {
                return true;
            }
        }
        return false;
    }

    private static String constructSetChildrenErrorMessage(ViewGroup viewToManage, ViewGroupManager viewManager, ReadableArray childrenTags) {
        ViewAtIndex[] viewAtIndexArr = new ViewAtIndex[childrenTags.size()];
        for (int i = 0; i < childrenTags.size(); i++) {
            viewAtIndexArr[i] = new ViewAtIndex(childrenTags.getInt(i), i);
        }
        return constructManageChildrenErrorMessage(viewToManage, viewManager, null, viewAtIndexArr, null);
    }

    public synchronized void setChildren(int tag, ReadableArray childrenTags) {
        UiThreadUtil.assertOnUiThread();
        ViewGroup viewGroup = (ViewGroup) this.mTagsToViews.get(tag);
        ViewGroupManager viewGroupManager = (ViewGroupManager) resolveViewManager(tag);
        for (int i = 0; i < childrenTags.size(); i++) {
            View view = this.mTagsToViews.get(childrenTags.getInt(i));
            if (view == null) {
                throw new IllegalViewOperationException("Trying to add unknown view tag: " + childrenTags.getInt(i) + "\n detail: " + constructSetChildrenErrorMessage(viewGroup, viewGroupManager, childrenTags));
            }
            viewGroupManager.addView(viewGroup, view, i);
        }
    }

    public synchronized void addRootView(int tag, View view) {
        addRootViewGroup(tag, view);
    }

    protected final synchronized void addRootViewGroup(int tag, View view) {
        if (view.getId() != -1) {
            String str = TAG;
            FLog.e(str, "Trying to add a root view with an explicit id (" + view.getId() + ") already set. React Native uses the id field to track react tags and will overwrite this field. If that is fine, explicitly overwrite the id field to View.NO_ID before calling addRootView.");
        }
        this.mTagsToViews.put(tag, view);
        this.mTagsToViewManagers.put(tag, this.mRootViewManager);
        this.mRootTags.put(tag, true);
        view.setId(tag);
    }

    protected synchronized void dropView(View view) {
        UiThreadUtil.assertOnUiThread();
        if (view == null) {
            return;
        }
        if (this.mTagsToViewManagers.get(view.getId()) == null) {
            return;
        }
        if (!this.mRootTags.get(view.getId())) {
            resolveViewManager(view.getId()).onDropViewInstance(view);
        }
        ViewManager viewManager = this.mTagsToViewManagers.get(view.getId());
        if ((view instanceof ViewGroup) && (viewManager instanceof ViewGroupManager)) {
            ViewGroup viewGroup = (ViewGroup) view;
            ViewGroupManager viewGroupManager = (ViewGroupManager) viewManager;
            for (int childCount = viewGroupManager.getChildCount(viewGroup) - 1; childCount >= 0; childCount--) {
                View childAt = viewGroupManager.getChildAt(viewGroup, childCount);
                if (childAt == null) {
                    FLog.e(TAG, "Unable to drop null child view");
                } else if (this.mTagsToViews.get(childAt.getId()) != null) {
                    dropView(childAt);
                }
            }
            viewGroupManager.removeAllViews(viewGroup);
        }
        this.mTagsToViews.remove(view.getId());
        this.mTagsToViewManagers.remove(view.getId());
    }

    public synchronized void removeRootView(int rootViewTag) {
        UiThreadUtil.assertOnUiThread();
        if (!this.mRootTags.get(rootViewTag)) {
            SoftAssertions.assertUnreachable("View with tag " + rootViewTag + " is not registered as a root view");
        }
        dropView(this.mTagsToViews.get(rootViewTag));
        this.mRootTags.delete(rootViewTag);
    }

    public synchronized void measure(int tag, int[] outputBuffer) {
        UiThreadUtil.assertOnUiThread();
        View view = this.mTagsToViews.get(tag);
        if (view == null) {
            throw new NoSuchNativeViewException("No native view for " + tag + " currently exists");
        }
        View view2 = (View) RootViewUtil.getRootView(view);
        if (view2 == null) {
            throw new NoSuchNativeViewException("Native view " + tag + " is no longer on screen");
        }
        computeBoundingBox(view2, outputBuffer);
        int i = outputBuffer[0];
        int i2 = outputBuffer[1];
        computeBoundingBox(view, outputBuffer);
        outputBuffer[0] = outputBuffer[0] - i;
        outputBuffer[1] = outputBuffer[1] - i2;
    }

    private void computeBoundingBox(View view, int[] outputBuffer) {
        this.mBoundingBox.set(0.0f, 0.0f, view.getWidth(), view.getHeight());
        mapRectFromViewToWindowCoords(view, this.mBoundingBox);
        outputBuffer[0] = Math.round(this.mBoundingBox.left);
        outputBuffer[1] = Math.round(this.mBoundingBox.top);
        outputBuffer[2] = Math.round(this.mBoundingBox.right - this.mBoundingBox.left);
        outputBuffer[3] = Math.round(this.mBoundingBox.bottom - this.mBoundingBox.top);
    }

    private void mapRectFromViewToWindowCoords(View view, RectF rect) {
        Matrix matrix = view.getMatrix();
        if (!matrix.isIdentity()) {
            matrix.mapRect(rect);
        }
        rect.offset(view.getLeft(), view.getTop());
        ViewParent parent = view.getParent();
        while (parent instanceof View) {
            View view2 = (View) parent;
            rect.offset(-view2.getScrollX(), -view2.getScrollY());
            Matrix matrix2 = view2.getMatrix();
            if (!matrix2.isIdentity()) {
                matrix2.mapRect(rect);
            }
            rect.offset(view2.getLeft(), view2.getTop());
            parent = view2.getParent();
        }
    }

    public synchronized void measureInWindow(int tag, int[] outputBuffer) {
        UiThreadUtil.assertOnUiThread();
        View view = this.mTagsToViews.get(tag);
        if (view == null) {
            throw new NoSuchNativeViewException("No native view for " + tag + " currently exists");
        }
        view.getLocationOnScreen(outputBuffer);
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        outputBuffer[0] = outputBuffer[0] - rect.left;
        outputBuffer[1] = outputBuffer[1] - rect.top;
        outputBuffer[2] = view.getWidth();
        outputBuffer[3] = view.getHeight();
    }

    public synchronized int findTargetTagForTouch(int reactTag, float touchX, float touchY) {
        View view;
        UiThreadUtil.assertOnUiThread();
        view = this.mTagsToViews.get(reactTag);
        if (view == null) {
            throw new JSApplicationIllegalArgumentException("Could not find view with tag " + reactTag);
        }
        return TouchTargetHelper.findTargetTagForTouch(touchX, touchY, (ViewGroup) view);
    }

    public synchronized void setJSResponder(int reactTag, int initialReactTag, boolean blockNativeResponder) {
        if (!blockNativeResponder) {
            this.mJSResponderHandler.setJSResponder(initialReactTag, null);
            return;
        }
        View view = this.mTagsToViews.get(reactTag);
        if (initialReactTag != reactTag && (view instanceof ViewParent)) {
            this.mJSResponderHandler.setJSResponder(initialReactTag, (ViewParent) view);
            return;
        }
        if (this.mRootTags.get(reactTag)) {
            SoftAssertions.assertUnreachable("Cannot block native responder on " + reactTag + " that is a root view");
        }
        this.mJSResponderHandler.setJSResponder(initialReactTag, view.getParent());
    }

    public void clearJSResponder() {
        this.mJSResponderHandler.clearJSResponder();
    }

    public void configureLayoutAnimation(final ReadableMap config, final Callback onAnimationComplete) {
        this.mLayoutAnimator.initializeFromConfig(config, onAnimationComplete);
    }

    public void clearLayoutAnimation() {
        this.mLayoutAnimator.reset();
    }

    @Deprecated
    public synchronized void dispatchCommand(int reactTag, int commandId, ReadableArray args) {
        UiThreadUtil.assertOnUiThread();
        View view = this.mTagsToViews.get(reactTag);
        if (view == null) {
            throw new RetryableMountingLayerException("Trying to send command to a non-existing view with tag [" + reactTag + "] and command " + commandId);
        }
        resolveViewManager(reactTag).receiveCommand((ViewManager) view, commandId, args);
    }

    public synchronized void dispatchCommand(int reactTag, String commandId, ReadableArray args) {
        UiThreadUtil.assertOnUiThread();
        View view = this.mTagsToViews.get(reactTag);
        if (view == null) {
            throw new RetryableMountingLayerException("Trying to send command to a non-existing view with tag [" + reactTag + "] and command " + commandId);
        }
        ViewManager resolveViewManager = resolveViewManager(reactTag);
        ViewManagerDelegate delegate = resolveViewManager.getDelegate();
        if (delegate != null) {
            delegate.receiveCommand(view, commandId, args);
        } else {
            resolveViewManager.receiveCommand((ViewManager) view, commandId, args);
        }
    }

    public synchronized void showPopupMenu(int reactTag, ReadableArray items, Callback success, Callback error) {
        UiThreadUtil.assertOnUiThread();
        View view = this.mTagsToViews.get(reactTag);
        if (view == null) {
            error.invoke("Can't display popup. Could not find view with tag " + reactTag);
            return;
        }
        PopupMenu popupMenu = new PopupMenu(getReactContextForView(reactTag), view);
        this.mPopupMenu = popupMenu;
        Menu menu = popupMenu.getMenu();
        for (int i = 0; i < items.size(); i++) {
            menu.add(0, 0, i, items.getString(i));
        }
        PopupMenuCallbackHandler popupMenuCallbackHandler = new PopupMenuCallbackHandler(success);
        this.mPopupMenu.setOnMenuItemClickListener(popupMenuCallbackHandler);
        this.mPopupMenu.setOnDismissListener(popupMenuCallbackHandler);
        this.mPopupMenu.show();
    }

    public void dismissPopupMenu() {
        PopupMenu popupMenu = this.mPopupMenu;
        if (popupMenu != null) {
            popupMenu.dismiss();
        }
    }

    /* loaded from: classes.dex */
    public static class PopupMenuCallbackHandler implements PopupMenu.OnMenuItemClickListener, PopupMenu.OnDismissListener {
        boolean mConsumed;
        final Callback mSuccess;

        private PopupMenuCallbackHandler(Callback success) {
            this.mConsumed = false;
            this.mSuccess = success;
        }

        @Override // android.widget.PopupMenu.OnDismissListener
        public void onDismiss(PopupMenu menu) {
            if (!this.mConsumed) {
                this.mSuccess.invoke(UIManagerModuleConstants.ACTION_DISMISSED);
                this.mConsumed = true;
            }
        }

        @Override // android.widget.PopupMenu.OnMenuItemClickListener
        public boolean onMenuItemClick(MenuItem item) {
            if (!this.mConsumed) {
                this.mSuccess.invoke(UIManagerModuleConstants.ACTION_ITEM_SELECTED, Integer.valueOf(item.getOrder()));
                this.mConsumed = true;
                return true;
            }
            return false;
        }
    }

    private ThemedReactContext getReactContextForView(int reactTag) {
        View view = this.mTagsToViews.get(reactTag);
        if (view == null) {
            throw new JSApplicationIllegalArgumentException("Could not find view with tag " + reactTag);
        }
        return (ThemedReactContext) view.getContext();
    }

    public void sendAccessibilityEvent(int tag, int eventType) {
        View view = this.mTagsToViews.get(tag);
        if (view == null) {
            throw new JSApplicationIllegalArgumentException("Could not find view with tag " + tag);
        }
        view.sendAccessibilityEvent(eventType);
    }
}
