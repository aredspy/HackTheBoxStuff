package com.facebook.react.uimanager;

import android.util.SparseArray;
import android.util.SparseBooleanArray;
import com.facebook.react.common.SingleThreadAsserter;
/* loaded from: classes.dex */
public class ShadowNodeRegistry {
    private final SparseArray<ReactShadowNode> mTagsToCSSNodes = new SparseArray<>();
    private final SparseBooleanArray mRootTags = new SparseBooleanArray();
    private final SingleThreadAsserter mThreadAsserter = new SingleThreadAsserter();

    public void addRootNode(ReactShadowNode node) {
        this.mThreadAsserter.assertNow();
        int reactTag = node.getReactTag();
        this.mTagsToCSSNodes.put(reactTag, node);
        this.mRootTags.put(reactTag, true);
    }

    public void removeRootNode(int tag) {
        this.mThreadAsserter.assertNow();
        if (tag == -1) {
            return;
        }
        if (!this.mRootTags.get(tag)) {
            throw new IllegalViewOperationException("View with tag " + tag + " is not registered as a root view");
        }
        this.mTagsToCSSNodes.remove(tag);
        this.mRootTags.delete(tag);
    }

    public void addNode(ReactShadowNode node) {
        this.mThreadAsserter.assertNow();
        this.mTagsToCSSNodes.put(node.getReactTag(), node);
    }

    public void removeNode(int tag) {
        this.mThreadAsserter.assertNow();
        if (this.mRootTags.get(tag)) {
            throw new IllegalViewOperationException("Trying to remove root node " + tag + " without using removeRootNode!");
        }
        this.mTagsToCSSNodes.remove(tag);
    }

    public ReactShadowNode getNode(int tag) {
        this.mThreadAsserter.assertNow();
        return this.mTagsToCSSNodes.get(tag);
    }

    public boolean isRootNode(int tag) {
        this.mThreadAsserter.assertNow();
        return this.mRootTags.get(tag);
    }

    public int getRootNodeCount() {
        this.mThreadAsserter.assertNow();
        return this.mRootTags.size();
    }

    public int getRootTag(int index) {
        this.mThreadAsserter.assertNow();
        return this.mRootTags.keyAt(index);
    }
}
