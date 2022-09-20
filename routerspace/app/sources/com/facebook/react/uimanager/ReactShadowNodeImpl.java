package com.facebook.react.uimanager;

import com.facebook.infer.annotation.Assertions;
import com.facebook.yoga.YogaAlign;
import com.facebook.yoga.YogaBaselineFunction;
import com.facebook.yoga.YogaConfig;
import com.facebook.yoga.YogaConstants;
import com.facebook.yoga.YogaDirection;
import com.facebook.yoga.YogaDisplay;
import com.facebook.yoga.YogaEdge;
import com.facebook.yoga.YogaFlexDirection;
import com.facebook.yoga.YogaJustify;
import com.facebook.yoga.YogaMeasureFunction;
import com.facebook.yoga.YogaNode;
import com.facebook.yoga.YogaNodeFactory;
import com.facebook.yoga.YogaOverflow;
import com.facebook.yoga.YogaPositionType;
import com.facebook.yoga.YogaValue;
import com.facebook.yoga.YogaWrap;
import java.util.ArrayList;
import java.util.Arrays;
/* loaded from: classes.dex */
public class ReactShadowNodeImpl implements ReactShadowNode<ReactShadowNodeImpl> {
    private static final YogaConfig sYogaConfig = ReactYogaConfigProvider.get();
    private ArrayList<ReactShadowNodeImpl> mChildren;
    private Integer mHeightMeasureSpec;
    private boolean mIsLayoutOnly;
    private ReactShadowNodeImpl mLayoutParent;
    private ArrayList<ReactShadowNodeImpl> mNativeChildren;
    private ReactShadowNodeImpl mNativeParent;
    private final float[] mPadding;
    private ReactShadowNodeImpl mParent;
    private int mReactTag;
    private int mRootTag;
    private int mScreenHeight;
    private int mScreenWidth;
    private int mScreenX;
    private int mScreenY;
    private boolean mShouldNotifyOnLayout;
    private ThemedReactContext mThemedContext;
    private String mViewClassName;
    private Integer mWidthMeasureSpec;
    private YogaNode mYogaNode;
    private boolean mNodeUpdated = true;
    private int mTotalNativeChildren = 0;
    private final boolean[] mPaddingIsPercent = new boolean[9];
    private final Spacing mDefaultPadding = new Spacing(0.0f);

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public boolean hoistNativeChildren() {
        return false;
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public boolean isVirtual() {
        return false;
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public boolean isVirtualAnchor() {
        return false;
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void onAfterUpdateTransaction() {
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void onBeforeLayout(NativeViewHierarchyOptimizer nativeViewHierarchyOptimizer) {
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void onCollectExtraUpdates(UIViewOperationQueue uiViewOperationQueue) {
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setLocalData(Object data) {
    }

    public ReactShadowNodeImpl() {
        float[] fArr = new float[9];
        this.mPadding = fArr;
        if (!isVirtual()) {
            YogaNode acquire = YogaNodePool.get().acquire();
            acquire = acquire == null ? YogaNodeFactory.create(sYogaConfig) : acquire;
            this.mYogaNode = acquire;
            acquire.setData(this);
            Arrays.fill(fArr, Float.NaN);
            return;
        }
        this.mYogaNode = null;
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public boolean isYogaLeafNode() {
        return isMeasureDefined();
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public final String getViewClass() {
        return (String) Assertions.assertNotNull(this.mViewClassName);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public final boolean hasUpdates() {
        return this.mNodeUpdated || hasNewLayout() || isDirty();
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public final void markUpdateSeen() {
        this.mNodeUpdated = false;
        if (hasNewLayout()) {
            markLayoutSeen();
        }
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void markUpdated() {
        if (this.mNodeUpdated) {
            return;
        }
        this.mNodeUpdated = true;
        ReactShadowNodeImpl parent = getParent();
        if (parent == null) {
            return;
        }
        parent.markUpdated();
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public final boolean hasUnseenUpdates() {
        return this.mNodeUpdated;
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void dirty() {
        if (!isVirtual()) {
            this.mYogaNode.dirty();
        } else if (getParent() == null) {
        } else {
            getParent().dirty();
        }
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public final boolean isDirty() {
        YogaNode yogaNode = this.mYogaNode;
        return yogaNode != null && yogaNode.isDirty();
    }

    public void addChildAt(ReactShadowNodeImpl child, int i) {
        if (this.mChildren == null) {
            this.mChildren = new ArrayList<>(4);
        }
        this.mChildren.add(i, child);
        child.mParent = this;
        if (this.mYogaNode != null && !isYogaLeafNode()) {
            YogaNode yogaNode = child.mYogaNode;
            if (yogaNode == null) {
                throw new RuntimeException("Cannot add a child that doesn't have a YogaNode to a parent without a measure function! (Trying to add a '" + child.toString() + "' to a '" + toString() + "')");
            }
            this.mYogaNode.addChildAt(yogaNode, i);
        }
        markUpdated();
        int totalNativeNodeContributionToParent = child.getTotalNativeNodeContributionToParent();
        this.mTotalNativeChildren += totalNativeNodeContributionToParent;
        updateNativeChildrenCountInParent(totalNativeNodeContributionToParent);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public ReactShadowNodeImpl removeChildAt(int i) {
        ArrayList<ReactShadowNodeImpl> arrayList = this.mChildren;
        if (arrayList == null) {
            throw new ArrayIndexOutOfBoundsException("Index " + i + " out of bounds: node has no children");
        }
        ReactShadowNodeImpl remove = arrayList.remove(i);
        remove.mParent = null;
        if (this.mYogaNode != null && !isYogaLeafNode()) {
            this.mYogaNode.removeChildAt(i);
        }
        markUpdated();
        int totalNativeNodeContributionToParent = remove.getTotalNativeNodeContributionToParent();
        this.mTotalNativeChildren -= totalNativeNodeContributionToParent;
        updateNativeChildrenCountInParent(-totalNativeNodeContributionToParent);
        return remove;
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public final int getChildCount() {
        ArrayList<ReactShadowNodeImpl> arrayList = this.mChildren;
        if (arrayList == null) {
            return 0;
        }
        return arrayList.size();
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public final ReactShadowNodeImpl getChildAt(int i) {
        ArrayList<ReactShadowNodeImpl> arrayList = this.mChildren;
        if (arrayList == null) {
            throw new ArrayIndexOutOfBoundsException("Index " + i + " out of bounds: node has no children");
        }
        return arrayList.get(i);
    }

    public final int indexOf(ReactShadowNodeImpl child) {
        ArrayList<ReactShadowNodeImpl> arrayList = this.mChildren;
        if (arrayList == null) {
            return -1;
        }
        return arrayList.indexOf(child);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void removeAndDisposeAllChildren() {
        if (getChildCount() == 0) {
            return;
        }
        int i = 0;
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            if (this.mYogaNode != null && !isYogaLeafNode()) {
                this.mYogaNode.removeChildAt(childCount);
            }
            ReactShadowNodeImpl childAt = getChildAt(childCount);
            childAt.mParent = null;
            i += childAt.getTotalNativeNodeContributionToParent();
            childAt.dispose();
        }
        ((ArrayList) Assertions.assertNotNull(this.mChildren)).clear();
        markUpdated();
        this.mTotalNativeChildren -= i;
        updateNativeChildrenCountInParent(-i);
    }

    private void updateNativeChildrenCountInParent(int delta) {
        if (getNativeKind() != NativeKind.PARENT) {
            for (ReactShadowNodeImpl parent = getParent(); parent != null; parent = parent.getParent()) {
                parent.mTotalNativeChildren += delta;
                if (parent.getNativeKind() == NativeKind.PARENT) {
                    return;
                }
            }
        }
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public final void updateProperties(ReactStylesDiffMap props) {
        ViewManagerPropertyUpdater.updateProps(this, props);
        onAfterUpdateTransaction();
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public boolean dispatchUpdates(float absoluteX, float absoluteY, UIViewOperationQueue uiViewOperationQueue, NativeViewHierarchyOptimizer nativeViewHierarchyOptimizer) {
        if (this.mNodeUpdated) {
            onCollectExtraUpdates(uiViewOperationQueue);
        }
        boolean z = false;
        if (hasNewLayout()) {
            float layoutX = getLayoutX();
            float layoutY = getLayoutY();
            float f = absoluteX + layoutX;
            int round = Math.round(f);
            float f2 = absoluteY + layoutY;
            int round2 = Math.round(f2);
            int round3 = Math.round(f + getLayoutWidth());
            int round4 = Math.round(f2 + getLayoutHeight());
            int round5 = Math.round(layoutX);
            int round6 = Math.round(layoutY);
            int i = round3 - round;
            int i2 = round4 - round2;
            if (round5 != this.mScreenX || round6 != this.mScreenY || i != this.mScreenWidth || i2 != this.mScreenHeight) {
                z = true;
            }
            this.mScreenX = round5;
            this.mScreenY = round6;
            this.mScreenWidth = i;
            this.mScreenHeight = i2;
            if (z) {
                if (nativeViewHierarchyOptimizer != null) {
                    nativeViewHierarchyOptimizer.handleUpdateLayout(this);
                } else {
                    uiViewOperationQueue.enqueueUpdateLayout(getParent().getReactTag(), getReactTag(), getScreenX(), getScreenY(), getScreenWidth(), getScreenHeight());
                }
            }
        }
        return z;
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public final int getReactTag() {
        return this.mReactTag;
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setReactTag(int reactTag) {
        this.mReactTag = reactTag;
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public final int getRootTag() {
        Assertions.assertCondition(this.mRootTag != 0);
        return this.mRootTag;
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public final void setRootTag(int rootTag) {
        this.mRootTag = rootTag;
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public final void setViewClassName(String viewClassName) {
        this.mViewClassName = viewClassName;
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public final ReactShadowNodeImpl getParent() {
        return this.mParent;
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public final ReactShadowNodeImpl getLayoutParent() {
        ReactShadowNodeImpl reactShadowNodeImpl = this.mLayoutParent;
        return reactShadowNodeImpl != null ? reactShadowNodeImpl : getNativeParent();
    }

    public final void setLayoutParent(ReactShadowNodeImpl layoutParent) {
        this.mLayoutParent = layoutParent;
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public final ThemedReactContext getThemedContext() {
        return (ThemedReactContext) Assertions.assertNotNull(this.mThemedContext);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setThemedContext(ThemedReactContext themedContext) {
        this.mThemedContext = themedContext;
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public final boolean shouldNotifyOnLayout() {
        return this.mShouldNotifyOnLayout;
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void calculateLayout() {
        calculateLayout(Float.NaN, Float.NaN);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void calculateLayout(float width, float height) {
        this.mYogaNode.calculateLayout(width, height);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public final boolean hasNewLayout() {
        YogaNode yogaNode = this.mYogaNode;
        return yogaNode != null && yogaNode.hasNewLayout();
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public final void markLayoutSeen() {
        YogaNode yogaNode = this.mYogaNode;
        if (yogaNode != null) {
            yogaNode.markLayoutSeen();
        }
    }

    public final void addNativeChildAt(ReactShadowNodeImpl child, int nativeIndex) {
        boolean z = true;
        Assertions.assertCondition(getNativeKind() == NativeKind.PARENT);
        if (child.getNativeKind() == NativeKind.NONE) {
            z = false;
        }
        Assertions.assertCondition(z);
        if (this.mNativeChildren == null) {
            this.mNativeChildren = new ArrayList<>(4);
        }
        this.mNativeChildren.add(nativeIndex, child);
        child.mNativeParent = this;
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public final ReactShadowNodeImpl removeNativeChildAt(int i) {
        Assertions.assertNotNull(this.mNativeChildren);
        ReactShadowNodeImpl remove = this.mNativeChildren.remove(i);
        remove.mNativeParent = null;
        return remove;
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public final void removeAllNativeChildren() {
        ArrayList<ReactShadowNodeImpl> arrayList = this.mNativeChildren;
        if (arrayList != null) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                this.mNativeChildren.get(size).mNativeParent = null;
            }
            this.mNativeChildren.clear();
        }
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public final int getNativeChildCount() {
        ArrayList<ReactShadowNodeImpl> arrayList = this.mNativeChildren;
        if (arrayList == null) {
            return 0;
        }
        return arrayList.size();
    }

    public final int indexOfNativeChild(ReactShadowNodeImpl nativeChild) {
        Assertions.assertNotNull(this.mNativeChildren);
        return this.mNativeChildren.indexOf(nativeChild);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public final ReactShadowNodeImpl getNativeParent() {
        return this.mNativeParent;
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public final void setIsLayoutOnly(boolean isLayoutOnly) {
        boolean z = true;
        Assertions.assertCondition(getParent() == null, "Must remove from no opt parent first");
        Assertions.assertCondition(this.mNativeParent == null, "Must remove from native parent first");
        if (getNativeChildCount() != 0) {
            z = false;
        }
        Assertions.assertCondition(z, "Must remove all native children first");
        this.mIsLayoutOnly = isLayoutOnly;
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public final boolean isLayoutOnly() {
        return this.mIsLayoutOnly;
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public NativeKind getNativeKind() {
        if (isVirtual() || isLayoutOnly()) {
            return NativeKind.NONE;
        }
        return hoistNativeChildren() ? NativeKind.LEAF : NativeKind.PARENT;
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public final int getTotalNativeChildren() {
        return this.mTotalNativeChildren;
    }

    public boolean isDescendantOf(ReactShadowNodeImpl ancestorNode) {
        for (ReactShadowNodeImpl parent = getParent(); parent != null; parent = parent.getParent()) {
            if (parent == ancestorNode) {
                return true;
            }
        }
        return false;
    }

    private int getTotalNativeNodeContributionToParent() {
        NativeKind nativeKind = getNativeKind();
        if (nativeKind == NativeKind.NONE) {
            return this.mTotalNativeChildren;
        }
        if (nativeKind != NativeKind.LEAF) {
            return 1;
        }
        return 1 + this.mTotalNativeChildren;
    }

    public String toString() {
        return "[" + this.mViewClassName + " " + getReactTag() + "]";
    }

    public final int getNativeOffsetForChild(ReactShadowNodeImpl child) {
        boolean z = false;
        int i = 0;
        int i2 = 0;
        while (true) {
            if (i >= getChildCount()) {
                break;
            }
            ReactShadowNodeImpl childAt = getChildAt(i);
            if (child == childAt) {
                z = true;
                break;
            }
            i2 += childAt.getTotalNativeNodeContributionToParent();
            i++;
        }
        if (z) {
            return i2;
        }
        throw new RuntimeException("Child " + child.getReactTag() + " was not a child of " + this.mReactTag);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public final float getLayoutX() {
        return this.mYogaNode.getLayoutX();
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public final float getLayoutY() {
        return this.mYogaNode.getLayoutY();
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public final float getLayoutWidth() {
        return this.mYogaNode.getLayoutWidth();
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public final float getLayoutHeight() {
        return this.mYogaNode.getLayoutHeight();
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public int getScreenX() {
        return this.mScreenX;
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public int getScreenY() {
        return this.mScreenY;
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public int getScreenWidth() {
        return this.mScreenWidth;
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public int getScreenHeight() {
        return this.mScreenHeight;
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public final YogaDirection getLayoutDirection() {
        return this.mYogaNode.getLayoutDirection();
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setLayoutDirection(YogaDirection direction) {
        this.mYogaNode.setDirection(direction);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public final YogaValue getStyleWidth() {
        return this.mYogaNode.getWidth();
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setStyleWidth(float widthPx) {
        this.mYogaNode.setWidth(widthPx);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setStyleWidthPercent(float percent) {
        this.mYogaNode.setWidthPercent(percent);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setStyleWidthAuto() {
        this.mYogaNode.setWidthAuto();
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setStyleMinWidth(float widthPx) {
        this.mYogaNode.setMinWidth(widthPx);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setStyleMinWidthPercent(float percent) {
        this.mYogaNode.setMinWidthPercent(percent);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setStyleMaxWidth(float widthPx) {
        this.mYogaNode.setMaxWidth(widthPx);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setStyleMaxWidthPercent(float percent) {
        this.mYogaNode.setMaxWidthPercent(percent);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public final YogaValue getStyleHeight() {
        return this.mYogaNode.getHeight();
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setStyleHeight(float heightPx) {
        this.mYogaNode.setHeight(heightPx);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setStyleHeightPercent(float percent) {
        this.mYogaNode.setHeightPercent(percent);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setStyleHeightAuto() {
        this.mYogaNode.setHeightAuto();
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setStyleMinHeight(float widthPx) {
        this.mYogaNode.setMinHeight(widthPx);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setStyleMinHeightPercent(float percent) {
        this.mYogaNode.setMinHeightPercent(percent);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setStyleMaxHeight(float widthPx) {
        this.mYogaNode.setMaxHeight(widthPx);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setStyleMaxHeightPercent(float percent) {
        this.mYogaNode.setMaxHeightPercent(percent);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public float getFlex() {
        return this.mYogaNode.getFlex();
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setFlex(float flex) {
        this.mYogaNode.setFlex(flex);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setFlexGrow(float flexGrow) {
        this.mYogaNode.setFlexGrow(flexGrow);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setFlexShrink(float flexShrink) {
        this.mYogaNode.setFlexShrink(flexShrink);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setFlexBasis(float flexBasis) {
        this.mYogaNode.setFlexBasis(flexBasis);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setFlexBasisAuto() {
        this.mYogaNode.setFlexBasisAuto();
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setFlexBasisPercent(float percent) {
        this.mYogaNode.setFlexBasisPercent(percent);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setStyleAspectRatio(float aspectRatio) {
        this.mYogaNode.setAspectRatio(aspectRatio);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setFlexDirection(YogaFlexDirection flexDirection) {
        this.mYogaNode.setFlexDirection(flexDirection);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setFlexWrap(YogaWrap wrap) {
        this.mYogaNode.setWrap(wrap);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setAlignSelf(YogaAlign alignSelf) {
        this.mYogaNode.setAlignSelf(alignSelf);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setAlignItems(YogaAlign alignItems) {
        this.mYogaNode.setAlignItems(alignItems);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setAlignContent(YogaAlign alignContent) {
        this.mYogaNode.setAlignContent(alignContent);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setJustifyContent(YogaJustify justifyContent) {
        this.mYogaNode.setJustifyContent(justifyContent);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setOverflow(YogaOverflow overflow) {
        this.mYogaNode.setOverflow(overflow);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setDisplay(YogaDisplay display) {
        this.mYogaNode.setDisplay(display);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setMargin(int spacingType, float margin) {
        this.mYogaNode.setMargin(YogaEdge.fromInt(spacingType), margin);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setMarginPercent(int spacingType, float percent) {
        this.mYogaNode.setMarginPercent(YogaEdge.fromInt(spacingType), percent);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setMarginAuto(int spacingType) {
        this.mYogaNode.setMarginAuto(YogaEdge.fromInt(spacingType));
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public final float getPadding(int spacingType) {
        return this.mYogaNode.getLayoutPadding(YogaEdge.fromInt(spacingType));
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public final YogaValue getStylePadding(int spacingType) {
        return this.mYogaNode.getPadding(YogaEdge.fromInt(spacingType));
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setDefaultPadding(int spacingType, float padding) {
        this.mDefaultPadding.set(spacingType, padding);
        updatePadding();
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setPadding(int spacingType, float padding) {
        this.mPadding[spacingType] = padding;
        this.mPaddingIsPercent[spacingType] = false;
        updatePadding();
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setPaddingPercent(int spacingType, float percent) {
        this.mPadding[spacingType] = percent;
        this.mPaddingIsPercent[spacingType] = !YogaConstants.isUndefined(percent);
        updatePadding();
    }

    /* JADX WARN: Removed duplicated region for block: B:35:0x0097  */
    /* JADX WARN: Removed duplicated region for block: B:36:0x00a5  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void updatePadding() {
        /*
            r4 = this;
            r0 = 0
        L1:
            r1 = 8
            if (r0 > r1) goto Lb6
            if (r0 == 0) goto L62
            r2 = 2
            if (r0 == r2) goto L62
            r2 = 4
            if (r0 == r2) goto L62
            r2 = 5
            if (r0 != r2) goto L11
            goto L62
        L11:
            r2 = 1
            if (r0 == r2) goto L33
            r2 = 3
            if (r0 != r2) goto L18
            goto L33
        L18:
            float[] r1 = r4.mPadding
            r1 = r1[r0]
            boolean r1 = com.facebook.yoga.YogaConstants.isUndefined(r1)
            if (r1 == 0) goto L91
            com.facebook.yoga.YogaNode r1 = r4.mYogaNode
            com.facebook.yoga.YogaEdge r2 = com.facebook.yoga.YogaEdge.fromInt(r0)
            com.facebook.react.uimanager.Spacing r3 = r4.mDefaultPadding
            float r3 = r3.getRaw(r0)
            r1.setPadding(r2, r3)
            goto Lb2
        L33:
            float[] r2 = r4.mPadding
            r2 = r2[r0]
            boolean r2 = com.facebook.yoga.YogaConstants.isUndefined(r2)
            if (r2 == 0) goto L91
            float[] r2 = r4.mPadding
            r3 = 7
            r2 = r2[r3]
            boolean r2 = com.facebook.yoga.YogaConstants.isUndefined(r2)
            if (r2 == 0) goto L91
            float[] r2 = r4.mPadding
            r1 = r2[r1]
            boolean r1 = com.facebook.yoga.YogaConstants.isUndefined(r1)
            if (r1 == 0) goto L91
            com.facebook.yoga.YogaNode r1 = r4.mYogaNode
            com.facebook.yoga.YogaEdge r2 = com.facebook.yoga.YogaEdge.fromInt(r0)
            com.facebook.react.uimanager.Spacing r3 = r4.mDefaultPadding
            float r3 = r3.getRaw(r0)
            r1.setPadding(r2, r3)
            goto Lb2
        L62:
            float[] r2 = r4.mPadding
            r2 = r2[r0]
            boolean r2 = com.facebook.yoga.YogaConstants.isUndefined(r2)
            if (r2 == 0) goto L91
            float[] r2 = r4.mPadding
            r3 = 6
            r2 = r2[r3]
            boolean r2 = com.facebook.yoga.YogaConstants.isUndefined(r2)
            if (r2 == 0) goto L91
            float[] r2 = r4.mPadding
            r1 = r2[r1]
            boolean r1 = com.facebook.yoga.YogaConstants.isUndefined(r1)
            if (r1 == 0) goto L91
            com.facebook.yoga.YogaNode r1 = r4.mYogaNode
            com.facebook.yoga.YogaEdge r2 = com.facebook.yoga.YogaEdge.fromInt(r0)
            com.facebook.react.uimanager.Spacing r3 = r4.mDefaultPadding
            float r3 = r3.getRaw(r0)
            r1.setPadding(r2, r3)
            goto Lb2
        L91:
            boolean[] r1 = r4.mPaddingIsPercent
            boolean r1 = r1[r0]
            if (r1 == 0) goto La5
            com.facebook.yoga.YogaNode r1 = r4.mYogaNode
            com.facebook.yoga.YogaEdge r2 = com.facebook.yoga.YogaEdge.fromInt(r0)
            float[] r3 = r4.mPadding
            r3 = r3[r0]
            r1.setPaddingPercent(r2, r3)
            goto Lb2
        La5:
            com.facebook.yoga.YogaNode r1 = r4.mYogaNode
            com.facebook.yoga.YogaEdge r2 = com.facebook.yoga.YogaEdge.fromInt(r0)
            float[] r3 = r4.mPadding
            r3 = r3[r0]
            r1.setPadding(r2, r3)
        Lb2:
            int r0 = r0 + 1
            goto L1
        Lb6:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.facebook.react.uimanager.ReactShadowNodeImpl.updatePadding():void");
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setBorder(int spacingType, float borderWidth) {
        this.mYogaNode.setBorder(YogaEdge.fromInt(spacingType), borderWidth);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setPosition(int spacingType, float position) {
        this.mYogaNode.setPosition(YogaEdge.fromInt(spacingType), position);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setPositionPercent(int spacingType, float percent) {
        this.mYogaNode.setPositionPercent(YogaEdge.fromInt(spacingType), percent);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setPositionType(YogaPositionType positionType) {
        this.mYogaNode.setPositionType(positionType);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setShouldNotifyOnLayout(boolean shouldNotifyOnLayout) {
        this.mShouldNotifyOnLayout = shouldNotifyOnLayout;
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setBaselineFunction(YogaBaselineFunction baselineFunction) {
        this.mYogaNode.setBaselineFunction(baselineFunction);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setMeasureFunction(YogaMeasureFunction measureFunction) {
        this.mYogaNode.setMeasureFunction(measureFunction);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public boolean isMeasureDefined() {
        return this.mYogaNode.isMeasureDefined();
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public String getHierarchyInfo() {
        StringBuilder sb = new StringBuilder();
        getHierarchyInfoWithIndentation(sb, 0);
        return sb.toString();
    }

    private void getHierarchyInfoWithIndentation(StringBuilder result, int level) {
        for (int i = 0; i < level; i++) {
            result.append("  ");
        }
        result.append("<");
        result.append(getClass().getSimpleName());
        result.append(" view='");
        result.append(getViewClass());
        result.append("' tag=");
        result.append(getReactTag());
        if (this.mYogaNode != null) {
            result.append(" layout='x:");
            result.append(getScreenX());
            result.append(" y:");
            result.append(getScreenY());
            result.append(" w:");
            result.append(getLayoutWidth());
            result.append(" h:");
            result.append(getLayoutHeight());
            result.append("'");
        } else {
            result.append("(virtual node)");
        }
        result.append(">\n");
        if (getChildCount() == 0) {
            return;
        }
        for (int i2 = 0; i2 < getChildCount(); i2++) {
            getChildAt(i2).getHierarchyInfoWithIndentation(result, level + 1);
        }
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void dispose() {
        YogaNode yogaNode = this.mYogaNode;
        if (yogaNode != null) {
            yogaNode.reset();
            YogaNodePool.get().release(this.mYogaNode);
        }
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public void setMeasureSpecs(int widthMeasureSpec, int heightMeasureSpec) {
        this.mWidthMeasureSpec = Integer.valueOf(widthMeasureSpec);
        this.mHeightMeasureSpec = Integer.valueOf(heightMeasureSpec);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public Integer getWidthMeasureSpec() {
        return this.mWidthMeasureSpec;
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public Integer getHeightMeasureSpec() {
        return this.mHeightMeasureSpec;
    }

    @Override // com.facebook.react.uimanager.ReactShadowNode
    public Iterable<? extends ReactShadowNode> calculateLayoutOnChildren() {
        if (isVirtualAnchor()) {
            return null;
        }
        return this.mChildren;
    }
}
