package com.facebook.cache.disk;

import com.facebook.binaryresource.BinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.cache.common.WriterCallback;
import com.facebook.cache.disk.DiskStorage;
import com.facebook.common.disk.DiskTrimmable;
import java.io.IOException;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public interface FileCache extends DiskTrimmable {
    void clearAll();

    long clearOldEntries(long cacheExpirationMs);

    long getCount();

    DiskStorage.DiskDumpInfo getDumpInfo() throws IOException;

    @Nullable
    BinaryResource getResource(CacheKey key);

    long getSize();

    boolean hasKey(CacheKey key);

    boolean hasKeySync(CacheKey key);

    @Nullable
    BinaryResource insert(CacheKey key, WriterCallback writer) throws IOException;

    boolean isEnabled();

    boolean probe(CacheKey key);

    void remove(CacheKey key);
}
