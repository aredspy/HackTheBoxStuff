package com.facebook.soloader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import kotlin.UByte;
import kotlin.UShort;
import okhttp3.internal.ws.WebSocketProtocol;
/* loaded from: classes.dex */
public final class MinElf {
    public static final int DT_NEEDED = 1;
    public static final int DT_NULL = 0;
    public static final int DT_STRTAB = 5;
    public static final int ELF_MAGIC = 1179403647;
    public static final int PN_XNUM = 65535;
    public static final int PT_DYNAMIC = 2;
    public static final int PT_LOAD = 1;
    private static final String TAG = "MinElf";

    /* loaded from: classes.dex */
    public enum ISA {
        NOT_SO("not_so"),
        X86("x86"),
        ARM("armeabi-v7a"),
        X86_64("x86_64"),
        AARCH64("arm64-v8a"),
        OTHERS("others");
        
        private final String value;

        ISA(String str) {
            this.value = str;
        }

        @Override // java.lang.Enum
        public String toString() {
            return this.value;
        }
    }

    /* JADX WARN: Can't wrap try/catch for region: R(3:16|11|12) */
    /* JADX WARN: Code restructure failed: missing block: B:10:0x0018, code lost:
        if (r0 <= 3) goto L16;
     */
    /* JADX WARN: Code restructure failed: missing block: B:11:0x001a, code lost:
        java.lang.Thread.interrupted();
        android.util.Log.e(com.facebook.soloader.MinElf.TAG, "retrying extract_DT_NEEDED due to ClosedByInterruptException", r2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x0028, code lost:
        throw r2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x0029, code lost:
        r1.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x002c, code lost:
        throw r5;
     */
    /* JADX WARN: Code restructure failed: missing block: B:7:0x0012, code lost:
        r5 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x0014, code lost:
        r2 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x0015, code lost:
        r0 = r0 + 1;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.lang.String[] extract_DT_NEEDED(java.io.File r5) throws java.io.IOException {
        /*
            r0 = 0
        L1:
            java.io.FileInputStream r1 = new java.io.FileInputStream
            r1.<init>(r5)
            java.nio.channels.FileChannel r2 = r1.getChannel()     // Catch: java.lang.Throwable -> L12 java.nio.channels.ClosedByInterruptException -> L14
            java.lang.String[] r5 = extract_DT_NEEDED(r2)     // Catch: java.lang.Throwable -> L12 java.nio.channels.ClosedByInterruptException -> L14
            r1.close()
            return r5
        L12:
            r5 = move-exception
            goto L29
        L14:
            r2 = move-exception
            int r0 = r0 + 1
            r3 = 3
            if (r0 > r3) goto L28
            java.lang.Thread.interrupted()     // Catch: java.lang.Throwable -> L12
            java.lang.String r3 = "MinElf"
            java.lang.String r4 = "retrying extract_DT_NEEDED due to ClosedByInterruptException"
            android.util.Log.e(r3, r4, r2)     // Catch: java.lang.Throwable -> L12
            r1.close()
            goto L1
        L28:
            throw r2     // Catch: java.lang.Throwable -> L12
        L29:
            r1.close()
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.facebook.soloader.MinElf.extract_DT_NEEDED(java.io.File):java.lang.String[]");
    }

    public static String[] extract_DT_NEEDED(FileChannel fileChannel) throws IOException {
        long j;
        long j2;
        long j3;
        long j4;
        long j5;
        long j6;
        long j7;
        long j8;
        long j9;
        long j10;
        long j11;
        ByteBuffer allocate = ByteBuffer.allocate(8);
        allocate.order(ByteOrder.LITTLE_ENDIAN);
        if (getu32(fileChannel, allocate, 0L) != 1179403647) {
            throw new ElfError("file is not ELF: 0x" + Long.toHexString(j));
        }
        boolean z = true;
        if (getu8(fileChannel, allocate, 4L) != 1) {
            z = false;
        }
        if (getu8(fileChannel, allocate, 5L) == 2) {
            allocate.order(ByteOrder.BIG_ENDIAN);
        }
        long j12 = z ? getu32(fileChannel, allocate, 28L) : get64(fileChannel, allocate, 32L);
        long j13 = z ? getu16(fileChannel, allocate, 44L) : getu16(fileChannel, allocate, 56L);
        int i = getu16(fileChannel, allocate, z ? 42L : 54L);
        if (j13 == WebSocketProtocol.PAYLOAD_SHORT_MAX) {
            long j14 = z ? getu32(fileChannel, allocate, 32L) : get64(fileChannel, allocate, 40L);
            if (z) {
                j11 = getu32(fileChannel, allocate, j14 + 28);
            } else {
                j11 = getu32(fileChannel, allocate, j14 + 44);
            }
            j13 = j11;
        }
        long j15 = j12;
        long j16 = 0;
        while (true) {
            if (j16 >= j13) {
                j2 = 0;
                break;
            }
            if (z) {
                j10 = getu32(fileChannel, allocate, j15 + 0);
            } else {
                j10 = getu32(fileChannel, allocate, j15 + 0);
            }
            if (j10 != 2) {
                j15 += i;
                j16++;
            } else if (z) {
                j2 = getu32(fileChannel, allocate, j15 + 4);
            } else {
                j2 = get64(fileChannel, allocate, j15 + 8);
            }
        }
        long j17 = 0;
        if (j2 == 0) {
            throw new ElfError("ELF file does not contain dynamic linking information");
        }
        long j18 = j2;
        long j19 = 0;
        int i2 = 0;
        while (true) {
            boolean z2 = z;
            long j20 = z ? getu32(fileChannel, allocate, j18 + j17) : get64(fileChannel, allocate, j18 + j17);
            if (j20 == 1) {
                j3 = j2;
                if (i2 == Integer.MAX_VALUE) {
                    throw new ElfError("malformed DT_NEEDED section");
                }
                i2++;
            } else {
                j3 = j2;
                if (j20 == 5) {
                    j19 = z2 ? getu32(fileChannel, allocate, j18 + 4) : get64(fileChannel, allocate, j18 + 8);
                }
            }
            long j21 = 16;
            j18 += z2 ? 8L : 16L;
            j17 = 0;
            if (j20 != 0) {
                z = z2;
                j2 = j3;
            } else if (j19 == 0) {
                throw new ElfError("Dynamic section string-table not found");
            } else {
                int i3 = 0;
                while (true) {
                    if (i3 >= j13) {
                        j4 = 0;
                        break;
                    }
                    if (z2) {
                        j5 = getu32(fileChannel, allocate, j12 + j17);
                    } else {
                        j5 = getu32(fileChannel, allocate, j12 + j17);
                    }
                    if (j5 == 1) {
                        if (z2) {
                            j7 = getu32(fileChannel, allocate, j12 + 8);
                        } else {
                            j7 = get64(fileChannel, allocate, j12 + j21);
                        }
                        if (z2) {
                            j6 = j13;
                            j8 = getu32(fileChannel, allocate, j12 + 20);
                        } else {
                            j6 = j13;
                            j8 = get64(fileChannel, allocate, j12 + 40);
                        }
                        if (j7 <= j19 && j19 < j8 + j7) {
                            if (z2) {
                                j9 = getu32(fileChannel, allocate, j12 + 4);
                            } else {
                                j9 = get64(fileChannel, allocate, j12 + 8);
                            }
                            j4 = j9 + (j19 - j7);
                        }
                    } else {
                        j6 = j13;
                    }
                    j12 += i;
                    i3++;
                    j13 = j6;
                    j21 = 16;
                    j17 = 0;
                }
                long j22 = 0;
                if (j4 == 0) {
                    throw new ElfError("did not find file offset of DT_STRTAB table");
                }
                String[] strArr = new String[i2];
                int i4 = 0;
                while (true) {
                    long j23 = j3 + j22;
                    long j24 = z2 ? getu32(fileChannel, allocate, j23) : get64(fileChannel, allocate, j23);
                    if (j24 == 1) {
                        strArr[i4] = getSz(fileChannel, allocate, (z2 ? getu32(fileChannel, allocate, j3 + 4) : get64(fileChannel, allocate, j3 + 8)) + j4);
                        if (i4 == Integer.MAX_VALUE) {
                            throw new ElfError("malformed DT_NEEDED section");
                        }
                        i4++;
                    }
                    j3 += z2 ? 8L : 16L;
                    if (j24 == 0) {
                        if (i4 != i2) {
                            throw new ElfError("malformed DT_NEEDED section");
                        }
                        return strArr;
                    }
                    j22 = 0;
                }
            }
        }
    }

    private static String getSz(FileChannel fileChannel, ByteBuffer byteBuffer, long j) throws IOException {
        StringBuilder sb = new StringBuilder();
        while (true) {
            long j2 = 1 + j;
            short u8Var = getu8(fileChannel, byteBuffer, j);
            if (u8Var != 0) {
                sb.append((char) u8Var);
                j = j2;
            } else {
                return sb.toString();
            }
        }
    }

    private static void read(FileChannel fileChannel, ByteBuffer byteBuffer, int i, long j) throws IOException {
        int read;
        byteBuffer.position(0);
        byteBuffer.limit(i);
        while (byteBuffer.remaining() > 0 && (read = fileChannel.read(byteBuffer, j)) != -1) {
            j += read;
        }
        if (byteBuffer.remaining() > 0) {
            throw new ElfError("ELF file truncated");
        }
        byteBuffer.position(0);
    }

    private static long get64(FileChannel fileChannel, ByteBuffer byteBuffer, long j) throws IOException {
        read(fileChannel, byteBuffer, 8, j);
        return byteBuffer.getLong();
    }

    private static long getu32(FileChannel fileChannel, ByteBuffer byteBuffer, long j) throws IOException {
        read(fileChannel, byteBuffer, 4, j);
        return byteBuffer.getInt() & 4294967295L;
    }

    private static int getu16(FileChannel fileChannel, ByteBuffer byteBuffer, long j) throws IOException {
        read(fileChannel, byteBuffer, 2, j);
        return byteBuffer.getShort() & UShort.MAX_VALUE;
    }

    private static short getu8(FileChannel fileChannel, ByteBuffer byteBuffer, long j) throws IOException {
        read(fileChannel, byteBuffer, 1, j);
        return (short) (byteBuffer.get() & UByte.MAX_VALUE);
    }

    /* loaded from: classes.dex */
    public static class ElfError extends RuntimeException {
        ElfError(String str) {
            super(str);
        }
    }
}
