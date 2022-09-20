package com.facebook.common.internal;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
/* loaded from: classes.dex */
public class ImmutableSet<E> extends HashSet<E> {
    private ImmutableSet(Set<E> set) {
        super(set);
    }

    public static <E> ImmutableSet<E> copyOf(Set<E> set) {
        return new ImmutableSet<>(set);
    }

    public static <E> ImmutableSet<E> of(E... elements) {
        HashSet hashSet = new HashSet(elements.length);
        Collections.addAll(hashSet, elements);
        return new ImmutableSet<>(hashSet);
    }
}
