package com.facebook.cache.common;

import com.facebook.common.util.SecureHashUtil;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public final class CacheKeyUtil {
    public static List<String> getResourceIds(final CacheKey key) {
        ArrayList arrayList;
        try {
            if (key instanceof MultiCacheKey) {
                List<CacheKey> cacheKeys = ((MultiCacheKey) key).getCacheKeys();
                arrayList = new ArrayList(cacheKeys.size());
                for (int i = 0; i < cacheKeys.size(); i++) {
                    arrayList.add(secureHashKey(cacheKeys.get(i)));
                }
            } else {
                arrayList = new ArrayList(1);
                arrayList.add(key.isResourceIdForDebugging() ? key.getUriString() : secureHashKey(key));
            }
            return arrayList;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getFirstResourceId(final CacheKey key) {
        try {
            if (key instanceof MultiCacheKey) {
                return secureHashKey(((MultiCacheKey) key).getCacheKeys().get(0));
            }
            return secureHashKey(key);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private static String secureHashKey(final CacheKey key) throws UnsupportedEncodingException {
        return SecureHashUtil.makeSHA1HashBase64(key.getUriString().getBytes("UTF-8"));
    }
}
