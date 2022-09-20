package com.facebook.react.modules.network;

import com.facebook.common.logging.FLog;
import com.facebook.react.common.ReactConstants;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
/* loaded from: classes.dex */
public class ProgressiveStringDecoder {
    private static final String EMPTY_STRING = "";
    private final CharsetDecoder mDecoder;
    private byte[] remainder = null;

    public ProgressiveStringDecoder(Charset charset) {
        this.mDecoder = charset.newDecoder();
    }

    public String decodeNext(byte[] data, int length) {
        byte[] bArr = this.remainder;
        if (bArr != null) {
            byte[] bArr2 = new byte[bArr.length + length];
            System.arraycopy(bArr, 0, bArr2, 0, bArr.length);
            System.arraycopy(data, 0, bArr2, this.remainder.length, length);
            length += this.remainder.length;
            data = bArr2;
        }
        ByteBuffer wrap = ByteBuffer.wrap(data, 0, length);
        boolean z = true;
        CharBuffer charBuffer = null;
        boolean z2 = false;
        int i = 0;
        while (!z2 && i < 4) {
            try {
                charBuffer = this.mDecoder.decode(wrap);
                z2 = true;
            } catch (CharacterCodingException unused) {
                i++;
                wrap = ByteBuffer.wrap(data, 0, length - i);
            }
        }
        if (!z2 || i <= 0) {
            z = false;
        }
        if (z) {
            byte[] bArr3 = new byte[i];
            this.remainder = bArr3;
            System.arraycopy(data, length - i, bArr3, 0, i);
        } else {
            this.remainder = null;
        }
        if (!z2) {
            FLog.w(ReactConstants.TAG, "failed to decode string from byte array");
            return "";
        }
        return new String(charBuffer.array(), 0, charBuffer.length());
    }
}
