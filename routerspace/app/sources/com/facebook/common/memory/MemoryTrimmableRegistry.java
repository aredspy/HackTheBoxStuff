package com.facebook.common.memory;
/* loaded from: classes.dex */
public interface MemoryTrimmableRegistry {
    void registerMemoryTrimmable(MemoryTrimmable trimmable);

    void unregisterMemoryTrimmable(MemoryTrimmable trimmable);
}
