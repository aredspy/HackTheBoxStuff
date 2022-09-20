package com.facebook.react.uimanager;

import android.util.SparseBooleanArray;
import com.facebook.common.logging.FLog;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
/* loaded from: classes.dex */
public class NativeViewHierarchyOptimizer {
    private static final boolean ENABLED = true;
    private static final String TAG = "NativeViewHierarchyOptimizer";
    private final ShadowNodeRegistry mShadowNodeRegistry;
    private final SparseBooleanArray mTagsWithLayoutVisited = new SparseBooleanArray();
    private final UIViewOperationQueue mUIViewOperationQueue;

    /* loaded from: classes.dex */
    public static class NodeIndexPair {
        public final int index;
        public final ReactShadowNode node;

        NodeIndexPair(ReactShadowNode node, int index) {
            this.node = node;
            this.index = index;
        }
    }

    public static void assertNodeSupportedWithoutOptimizer(ReactShadowNode node) {
        Assertions.assertCondition(node.getNativeKind() != NativeKind.LEAF ? ENABLED : false, "Nodes with NativeKind.LEAF are not supported when the optimizer is disabled");
    }

    public NativeViewHierarchyOptimizer(UIViewOperationQueue uiViewOperationQueue, ShadowNodeRegistry shadowNodeRegistry) {
        this.mUIViewOperationQueue = uiViewOperationQueue;
        this.mShadowNodeRegistry = shadowNodeRegistry;
    }

    public void handleCreateView(ReactShadowNode node, ThemedReactContext themedContext, ReactStylesDiffMap initialProps) {
        node.setIsLayoutOnly((!node.getViewClass().equals("RCTView") || !isLayoutOnlyAndCollapsable(initialProps)) ? false : ENABLED);
        if (node.getNativeKind() != NativeKind.NONE) {
            this.mUIViewOperationQueue.enqueueCreateView(themedContext, node.getReactTag(), node.getViewClass(), initialProps);
        }
    }

    public static void handleRemoveNode(ReactShadowNode node) {
        node.removeAllNativeChildren();
    }

    public void handleUpdateView(ReactShadowNode node, String className, ReactStylesDiffMap props) {
        if ((!node.isLayoutOnly() || isLayoutOnlyAndCollapsable(props)) ? false : ENABLED) {
            transitionLayoutOnlyViewToNativeView(node, props);
        } else if (node.isLayoutOnly()) {
        } else {
            this.mUIViewOperationQueue.enqueueUpdateProperties(node.getReactTag(), className, props);
        }
    }

    public void handleManageChildren(ReactShadowNode nodeToManage, int[] indicesToRemove, int[] tagsToRemove, ViewAtIndex[] viewsToAdd, int[] tagsToDelete) {
        boolean z;
        for (int i : tagsToRemove) {
            int i2 = 0;
            while (true) {
                if (i2 >= tagsToDelete.length) {
                    z = false;
                    break;
                } else if (tagsToDelete[i2] == i) {
                    z = ENABLED;
                    break;
                } else {
                    i2++;
                }
            }
            removeNodeFromParent(this.mShadowNodeRegistry.getNode(i), z);
        }
        for (ViewAtIndex viewAtIndex : viewsToAdd) {
            addNodeToNode(nodeToManage, this.mShadowNodeRegistry.getNode(viewAtIndex.mTag), viewAtIndex.mIndex);
        }
    }

    public void handleSetChildren(ReactShadowNode nodeToManage, ReadableArray childrenTags) {
        for (int i = 0; i < childrenTags.size(); i++) {
            addNodeToNode(nodeToManage, this.mShadowNodeRegistry.getNode(childrenTags.getInt(i)), i);
        }
    }

    public void handleUpdateLayout(ReactShadowNode node) {
        applyLayoutBase(node);
    }

    public void handleForceViewToBeNonLayoutOnly(ReactShadowNode node) {
        if (node.isLayoutOnly()) {
            transitionLayoutOnlyViewToNativeView(node, null);
        }
    }

    public void onBatchComplete() {
        this.mTagsWithLayoutVisited.clear();
    }

    private NodeIndexPair walkUpUntilNativeKindIsParent(ReactShadowNode node, int indexInNativeChildren) {
        while (node.getNativeKind() != NativeKind.PARENT) {
            ReactShadowNode parent = node.getParent();
            if (parent == null) {
                return null;
            }
            indexInNativeChildren = indexInNativeChildren + (node.getNativeKind() == NativeKind.LEAF ? 1 : 0) + parent.getNativeOffsetForChild(node);
            node = parent;
        }
        return new NodeIndexPair(node, indexInNativeChildren);
    }

    private void addNodeToNode(ReactShadowNode parent, ReactShadowNode child, int index) {
        int nativeOffsetForChild = parent.getNativeOffsetForChild(parent.getChildAt(index));
        if (parent.getNativeKind() != NativeKind.PARENT) {
            NodeIndexPair walkUpUntilNativeKindIsParent = walkUpUntilNativeKindIsParent(parent, nativeOffsetForChild);
            if (walkUpUntilNativeKindIsParent == null) {
                return;
            }
            ReactShadowNode reactShadowNode = walkUpUntilNativeKindIsParent.node;
            nativeOffsetForChild = walkUpUntilNativeKindIsParent.index;
            parent = reactShadowNode;
        }
        if (child.getNativeKind() != NativeKind.NONE) {
            addNativeChild(parent, child, nativeOffsetForChild);
        } else {
            addNonNativeChild(parent, child, nativeOffsetForChild);
        }
    }

    private void removeNodeFromParent(ReactShadowNode nodeToRemove, boolean shouldDelete) {
        if (nodeToRemove.getNativeKind() != NativeKind.PARENT) {
            for (int childCount = nodeToRemove.getChildCount() - 1; childCount >= 0; childCount--) {
                removeNodeFromParent(nodeToRemove.getChildAt(childCount), shouldDelete);
            }
        }
        ReactShadowNode nativeParent = nodeToRemove.getNativeParent();
        if (nativeParent != null) {
            int indexOfNativeChild = nativeParent.indexOfNativeChild(nodeToRemove);
            nativeParent.removeNativeChildAt(indexOfNativeChild);
            this.mUIViewOperationQueue.enqueueManageChildren(nativeParent.getReactTag(), new int[]{indexOfNativeChild}, null, shouldDelete ? new int[]{nodeToRemove.getReactTag()} : null);
        }
    }

    private void addNonNativeChild(ReactShadowNode nativeParent, ReactShadowNode nonNativeChild, int index) {
        addGrandchildren(nativeParent, nonNativeChild, index);
    }

    private void addNativeChild(ReactShadowNode parent, ReactShadowNode child, int index) {
        parent.addNativeChildAt(child, index);
        this.mUIViewOperationQueue.enqueueManageChildren(parent.getReactTag(), null, new ViewAtIndex[]{new ViewAtIndex(child.getReactTag(), index)}, null);
        if (child.getNativeKind() != NativeKind.PARENT) {
            addGrandchildren(parent, child, index + 1);
        }
    }

    private void addGrandchildren(ReactShadowNode nativeParent, ReactShadowNode child, int index) {
        Assertions.assertCondition(child.getNativeKind() != NativeKind.PARENT ? ENABLED : false);
        for (int i = 0; i < child.getChildCount(); i++) {
            ReactShadowNode childAt = child.getChildAt(i);
            Assertions.assertCondition(childAt.getNativeParent() == null ? ENABLED : false);
            int nativeChildCount = nativeParent.getNativeChildCount();
            if (childAt.getNativeKind() == NativeKind.NONE) {
                addNonNativeChild(nativeParent, childAt, index);
            } else {
                addNativeChild(nativeParent, childAt, index);
            }
            index += nativeParent.getNativeChildCount() - nativeChildCount;
        }
    }

    private void applyLayoutBase(ReactShadowNode node) {
        int reactTag = node.getReactTag();
        if (this.mTagsWithLayoutVisited.get(reactTag)) {
            return;
        }
        this.mTagsWithLayoutVisited.put(reactTag, ENABLED);
        int screenX = node.getScreenX();
        int screenY = node.getScreenY();
        for (ReactShadowNode parent = node.getParent(); parent != null && parent.getNativeKind() != NativeKind.PARENT; parent = parent.getParent()) {
            if (!parent.isVirtual()) {
                screenX += Math.round(parent.getLayoutX());
                screenY += Math.round(parent.getLayoutY());
            }
        }
        applyLayoutRecursive(node, screenX, screenY);
    }

    private void applyLayoutRecursive(ReactShadowNode toUpdate, int x, int y) {
        if (toUpdate.getNativeKind() != NativeKind.NONE && toUpdate.getNativeParent() != null) {
            this.mUIViewOperationQueue.enqueueUpdateLayout(toUpdate.getLayoutParent().getReactTag(), toUpdate.getReactTag(), x, y, toUpdate.getScreenWidth(), toUpdate.getScreenHeight());
            return;
        }
        for (int i = 0; i < toUpdate.getChildCount(); i++) {
            ReactShadowNode childAt = toUpdate.getChildAt(i);
            int reactTag = childAt.getReactTag();
            if (!this.mTagsWithLayoutVisited.get(reactTag)) {
                this.mTagsWithLayoutVisited.put(reactTag, ENABLED);
                applyLayoutRecursive(childAt, childAt.getScreenX() + x, childAt.getScreenY() + y);
            }
        }
    }

    private void transitionLayoutOnlyViewToNativeView(ReactShadowNode node, ReactStylesDiffMap props) {
        ReactShadowNode parent = node.getParent();
        if (parent == null) {
            node.setIsLayoutOnly(false);
            return;
        }
        int indexOf = parent.indexOf(node);
        parent.removeChildAt(indexOf);
        removeNodeFromParent(node, false);
        node.setIsLayoutOnly(false);
        this.mUIViewOperationQueue.enqueueCreateView(node.getThemedContext(), node.getReactTag(), node.getViewClass(), props);
        parent.addChildAt(node, indexOf);
        addNodeToNode(parent, node, indexOf);
        for (int i = 0; i < node.getChildCount(); i++) {
            addNodeToNode(node, node.getChildAt(i), i);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Transitioning LayoutOnlyView - tag: ");
        sb.append(node.getReactTag());
        sb.append(" - rootTag: ");
        sb.append(node.getRootTag());
        sb.append(" - hasProps: ");
        boolean z = ENABLED;
        sb.append(props != null ? ENABLED : false);
        sb.append(" - tagsWithLayout.size: ");
        sb.append(this.mTagsWithLayoutVisited.size());
        FLog.i(TAG, sb.toString());
        if (this.mTagsWithLayoutVisited.size() != 0) {
            z = false;
        }
        Assertions.assertCondition(z);
        applyLayoutBase(node);
        for (int i2 = 0; i2 < node.getChildCount(); i2++) {
            applyLayoutBase(node.getChildAt(i2));
        }
        this.mTagsWithLayoutVisited.clear();
    }

    private static boolean isLayoutOnlyAndCollapsable(ReactStylesDiffMap props) {
        if (props == null) {
            return ENABLED;
        }
        if (props.hasKey(ViewProps.COLLAPSABLE) && !props.getBoolean(ViewProps.COLLAPSABLE, ENABLED)) {
            return false;
        }
        ReadableMapKeySetIterator keySetIterator = props.mBackingMap.keySetIterator();
        while (keySetIterator.hasNextKey()) {
            if (!ViewProps.isLayoutOnly(props.mBackingMap, keySetIterator.nextKey())) {
                return false;
            }
        }
        return ENABLED;
    }

    public void onViewUpdatesCompleted(ReactShadowNode cssNode) {
        this.mTagsWithLayoutVisited.clear();
    }
}
