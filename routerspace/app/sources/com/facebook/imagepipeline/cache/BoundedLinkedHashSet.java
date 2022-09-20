package com.facebook.imagepipeline.cache;

import java.util.LinkedHashSet;
/* loaded from: classes.dex */
public class BoundedLinkedHashSet<E> {
    private LinkedHashSet<E> mLinkedHashSet;
    private int mMaxSize;

    public BoundedLinkedHashSet(final int maxSize) {
        this.mLinkedHashSet = new LinkedHashSet<>(maxSize);
        this.mMaxSize = maxSize;
    }

    public synchronized boolean contains(E o) {
        return this.mLinkedHashSet.contains(o);
    }

    public synchronized boolean add(E key) {
        if (this.mLinkedHashSet.size() == this.mMaxSize) {
            LinkedHashSet<E> linkedHashSet = this.mLinkedHashSet;
            linkedHashSet.remove(linkedHashSet.iterator().next());
        }
        this.mLinkedHashSet.remove(key);
        return this.mLinkedHashSet.add(key);
    }
}
