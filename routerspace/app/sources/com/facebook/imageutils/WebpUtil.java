package com.facebook.imageutils;

import android.util.Pair;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nullable;
import kotlin.UByte;
/* loaded from: classes.dex */
public class WebpUtil {
    private static final String VP8L_HEADER = "VP8L";
    private static final String VP8X_HEADER = "VP8X";
    private static final String VP8_HEADER = "VP8 ";

    private WebpUtil() {
    }

    @Nullable
    public static Pair<Integer, Integer> getSize(InputStream is) {
        byte[] bArr = new byte[4];
        try {
            try {
                try {
                    is.read(bArr);
                } catch (IOException e) {
                    e.printStackTrace();
                    if (is != null) {
                        is.close();
                    }
                }
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            if (!compare(bArr, "RIFF")) {
                return null;
            }
            getInt(is);
            is.read(bArr);
            if (!compare(bArr, "WEBP")) {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
                return null;
            }
            is.read(bArr);
            String header = getHeader(bArr);
            if (VP8_HEADER.equals(header)) {
                Pair<Integer, Integer> vP8Dimension = getVP8Dimension(is);
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e4) {
                        e4.printStackTrace();
                    }
                }
                return vP8Dimension;
            } else if (VP8L_HEADER.equals(header)) {
                Pair<Integer, Integer> vP8LDimension = getVP8LDimension(is);
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e5) {
                        e5.printStackTrace();
                    }
                }
                return vP8LDimension;
            } else if (!VP8X_HEADER.equals(header)) {
                if (is != null) {
                    is.close();
                }
                return null;
            } else {
                Pair<Integer, Integer> vP8XDimension = getVP8XDimension(is);
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e6) {
                        e6.printStackTrace();
                    }
                }
                return vP8XDimension;
            }
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e7) {
                    e7.printStackTrace();
                }
            }
        }
    }

    @Nullable
    private static Pair<Integer, Integer> getVP8Dimension(final InputStream is) throws IOException {
        is.skip(7L);
        short s = getShort(is);
        short s2 = getShort(is);
        short s3 = getShort(is);
        if (s == 157 && s2 == 1 && s3 == 42) {
            return new Pair<>(Integer.valueOf(get2BytesAsInt(is)), Integer.valueOf(get2BytesAsInt(is)));
        }
        return null;
    }

    @Nullable
    private static Pair<Integer, Integer> getVP8LDimension(final InputStream is) throws IOException {
        getInt(is);
        if (getByte(is) != 47) {
            return null;
        }
        int read = ((byte) is.read()) & UByte.MAX_VALUE;
        int read2 = ((byte) is.read()) & UByte.MAX_VALUE;
        return new Pair<>(Integer.valueOf((read | ((read2 & 63) << 8)) + 1), Integer.valueOf(((((((byte) is.read()) & UByte.MAX_VALUE) & 15) << 10) | ((((byte) is.read()) & UByte.MAX_VALUE) << 2) | ((read2 & JfifUtil.MARKER_SOFn) >> 6)) + 1));
    }

    private static Pair<Integer, Integer> getVP8XDimension(final InputStream is) throws IOException {
        is.skip(8L);
        return new Pair<>(Integer.valueOf(read3Bytes(is) + 1), Integer.valueOf(read3Bytes(is) + 1));
    }

    private static boolean compare(byte[] what, String with) {
        if (what.length != with.length()) {
            return false;
        }
        for (int i = 0; i < what.length; i++) {
            if (with.charAt(i) != what[i]) {
                return false;
            }
        }
        return true;
    }

    private static String getHeader(byte[] header) {
        StringBuilder sb = new StringBuilder();
        for (byte b : header) {
            sb.append((char) b);
        }
        return sb.toString();
    }

    private static int getInt(InputStream is) throws IOException {
        return ((((byte) is.read()) << 24) & ViewCompat.MEASURED_STATE_MASK) | ((((byte) is.read()) << 16) & 16711680) | ((((byte) is.read()) << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK) | (((byte) is.read()) & UByte.MAX_VALUE);
    }

    public static int get2BytesAsInt(InputStream is) throws IOException {
        return ((((byte) is.read()) << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK) | (((byte) is.read()) & UByte.MAX_VALUE);
    }

    private static int read3Bytes(InputStream is) throws IOException {
        byte b = getByte(is);
        return ((getByte(is) << 16) & 16711680) | ((getByte(is) << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK) | (b & UByte.MAX_VALUE);
    }

    private static short getShort(InputStream is) throws IOException {
        return (short) (is.read() & 255);
    }

    private static byte getByte(InputStream is) throws IOException {
        return (byte) (is.read() & 255);
    }

    private static boolean isBitOne(byte input, int bitIndex) {
        return ((input >> (bitIndex % 8)) & 1) == 1;
    }
}
