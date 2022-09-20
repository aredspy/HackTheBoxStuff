package com.facebook.react.animated;

import com.facebook.infer.annotation.Assertions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
abstract class AnimatedNode {
    private static final int DEFAULT_ANIMATED_NODE_CHILD_COUNT = 1;
    public static final int INITIAL_BFS_COLOR = 0;
    List<AnimatedNode> mChildren;
    int mActiveIncomingNodes = 0;
    int mBFSColor = 0;
    int mTag = -1;

    public void onAttachedToNode(AnimatedNode parent) {
    }

    public void onDetachedFromNode(AnimatedNode parent) {
    }

    public abstract String prettyPrint();

    public void update() {
    }

    public final void addChild(AnimatedNode child) {
        if (this.mChildren == null) {
            this.mChildren = new ArrayList(1);
        }
        ((List) Assertions.assertNotNull(this.mChildren)).add(child);
        child.onAttachedToNode(this);
    }

    public final void removeChild(AnimatedNode child) {
        if (this.mChildren == null) {
            return;
        }
        child.onDetachedFromNode(this);
        this.mChildren.remove(child);
    }

    public String prettyPrintWithChildren() {
        String str;
        Iterator<AnimatedNode> it;
        List<AnimatedNode> list = this.mChildren;
        String str2 = "";
        if (list == null || list.size() <= 0) {
            str = str2;
        } else {
            str = str2;
            while (this.mChildren.iterator().hasNext()) {
                str = str + " " + it.next().mTag;
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(prettyPrint());
        if (str.length() > 0) {
            str2 = " children: " + str;
        }
        sb.append(str2);
        return sb.toString();
    }
}
