package com.facebook.imagepipeline.image;
/* loaded from: classes.dex */
public enum EncodedImageOrigin {
    NOT_SET("not_set"),
    NETWORK("network"),
    DISK("disk"),
    ENCODED_MEM_CACHE("encoded_mem_cache");
    
    private final String mOrigin;

    EncodedImageOrigin(String origin) {
        this.mOrigin = origin;
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.mOrigin;
    }
}
