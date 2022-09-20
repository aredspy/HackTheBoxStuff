package com.facebook.imageformat;

import com.facebook.common.internal.Preconditions;
import java.io.UnsupportedEncodingException;
/* loaded from: classes.dex */
public class ImageFormatCheckerUtils {
    public static byte[] asciiBytes(String value) {
        Preconditions.checkNotNull(value);
        try {
            return value.getBytes("ASCII");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("ASCII not found!", e);
        }
    }

    public static boolean startsWithPattern(final byte[] byteArray, final byte[] pattern) {
        return hasPatternAt(byteArray, pattern, 0);
    }

    public static boolean hasPatternAt(final byte[] byteArray, final byte[] pattern, int offset) {
        Preconditions.checkNotNull(byteArray);
        Preconditions.checkNotNull(pattern);
        if (pattern.length + offset > byteArray.length) {
            return false;
        }
        for (int i = 0; i < pattern.length; i++) {
            if (byteArray[offset + i] != pattern[i]) {
                return false;
            }
        }
        return true;
    }

    public static int indexOfPattern(final byte[] byteArray, final int byteArrayLen, final byte[] pattern, final int patternLen) {
        Preconditions.checkNotNull(byteArray);
        Preconditions.checkNotNull(pattern);
        if (patternLen > byteArrayLen) {
            return -1;
        }
        int i = 0;
        byte b = pattern[0];
        int i2 = byteArrayLen - patternLen;
        while (i <= i2) {
            if (byteArray[i] != b) {
                do {
                    i++;
                    if (i > i2) {
                        break;
                    }
                } while (byteArray[i] != b);
            }
            if (i <= i2) {
                int i3 = i + 1;
                int i4 = (i3 + patternLen) - 1;
                for (int i5 = 1; i3 < i4 && byteArray[i3] == pattern[i5]; i5++) {
                    i3++;
                }
                if (i3 == i4) {
                    return i;
                }
            }
            i++;
        }
        return -1;
    }

    private ImageFormatCheckerUtils() {
    }
}
