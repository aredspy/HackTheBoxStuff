package com.facebook.imagepipeline.memory;

import javax.annotation.Nullable;
/* loaded from: classes.dex */
interface PoolBackend<T> {
    @Nullable
    T get(int size);

    int getSize(T item);

    @Nullable
    T pop();

    void put(T item);
}
