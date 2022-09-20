package com.facebook.cache.disk;

import com.facebook.cache.disk.DiskStorage;
/* loaded from: classes.dex */
public class DefaultEntryEvictionComparatorSupplier implements EntryEvictionComparatorSupplier {
    @Override // com.facebook.cache.disk.EntryEvictionComparatorSupplier
    public EntryEvictionComparator get() {
        return new EntryEvictionComparator() { // from class: com.facebook.cache.disk.DefaultEntryEvictionComparatorSupplier.1
            public int compare(DiskStorage.Entry e1, DiskStorage.Entry e2) {
                long timestamp = e1.getTimestamp();
                long timestamp2 = e2.getTimestamp();
                if (timestamp < timestamp2) {
                    return -1;
                }
                return timestamp2 == timestamp ? 0 : 1;
            }
        };
    }
}
