package com.facebook.common.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* loaded from: classes.dex */
public class ImmutableList<E> extends ArrayList<E> {
    private ImmutableList(final int capacity) {
        super(capacity);
    }

    private ImmutableList(List<E> list) {
        super(list);
    }

    public static <E> ImmutableList<E> copyOf(List<E> list) {
        return new ImmutableList<>(list);
    }

    public static <E> ImmutableList<E> of(E... elements) {
        ImmutableList<E> immutableList = new ImmutableList<>(elements.length);
        Collections.addAll(immutableList, elements);
        return immutableList;
    }
}
