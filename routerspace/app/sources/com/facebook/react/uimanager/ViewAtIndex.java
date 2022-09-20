package com.facebook.react.uimanager;

import java.util.Comparator;
/* loaded from: classes.dex */
public class ViewAtIndex {
    public static Comparator<ViewAtIndex> COMPARATOR = new Comparator<ViewAtIndex>() { // from class: com.facebook.react.uimanager.ViewAtIndex.1
        public int compare(ViewAtIndex lhs, ViewAtIndex rhs) {
            return lhs.mIndex - rhs.mIndex;
        }
    };
    public final int mIndex;
    public final int mTag;

    public ViewAtIndex(int tag, int index) {
        this.mTag = tag;
        this.mIndex = index;
    }

    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        ViewAtIndex viewAtIndex = (ViewAtIndex) obj;
        return this.mIndex == viewAtIndex.mIndex && this.mTag == viewAtIndex.mTag;
    }

    public String toString() {
        return "[" + this.mTag + ", " + this.mIndex + "]";
    }
}
