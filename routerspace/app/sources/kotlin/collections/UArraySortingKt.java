package kotlin.collections;

import com.facebook.react.uimanager.ViewProps;
import kotlin.Metadata;
import kotlin.UByte;
import kotlin.UByteArray;
import kotlin.UIntArray;
import kotlin.ULongArray;
import kotlin.UShort;
import kotlin.UShortArray;
import kotlin.UnsignedKt;
import kotlin.jvm.internal.Intrinsics;
/* compiled from: UArraySorting.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u00000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0010\u001a*\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003ø\u0001\u0000¢\u0006\u0004\b\u0006\u0010\u0007\u001a*\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\b2\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003ø\u0001\u0000¢\u0006\u0004\b\t\u0010\n\u001a*\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u000b2\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003ø\u0001\u0000¢\u0006\u0004\b\f\u0010\r\u001a*\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u000e2\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003ø\u0001\u0000¢\u0006\u0004\b\u000f\u0010\u0010\u001a*\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003ø\u0001\u0000¢\u0006\u0004\b\u0013\u0010\u0014\u001a*\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0002\u001a\u00020\b2\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003ø\u0001\u0000¢\u0006\u0004\b\u0015\u0010\u0016\u001a*\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0002\u001a\u00020\u000b2\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003ø\u0001\u0000¢\u0006\u0004\b\u0017\u0010\u0018\u001a*\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0002\u001a\u00020\u000e2\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003ø\u0001\u0000¢\u0006\u0004\b\u0019\u0010\u001a\u001a*\u0010\u001b\u001a\u00020\u00122\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u001c\u001a\u00020\u00012\u0006\u0010\u001d\u001a\u00020\u0001H\u0001ø\u0001\u0000¢\u0006\u0004\b\u001e\u0010\u0014\u001a*\u0010\u001b\u001a\u00020\u00122\u0006\u0010\u0002\u001a\u00020\b2\u0006\u0010\u001c\u001a\u00020\u00012\u0006\u0010\u001d\u001a\u00020\u0001H\u0001ø\u0001\u0000¢\u0006\u0004\b\u001f\u0010\u0016\u001a*\u0010\u001b\u001a\u00020\u00122\u0006\u0010\u0002\u001a\u00020\u000b2\u0006\u0010\u001c\u001a\u00020\u00012\u0006\u0010\u001d\u001a\u00020\u0001H\u0001ø\u0001\u0000¢\u0006\u0004\b \u0010\u0018\u001a*\u0010\u001b\u001a\u00020\u00122\u0006\u0010\u0002\u001a\u00020\u000e2\u0006\u0010\u001c\u001a\u00020\u00012\u0006\u0010\u001d\u001a\u00020\u0001H\u0001ø\u0001\u0000¢\u0006\u0004\b!\u0010\u001a\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006\""}, d2 = {"partition", "", "array", "Lkotlin/UByteArray;", ViewProps.LEFT, ViewProps.RIGHT, "partition-4UcCI2c", "([BII)I", "Lkotlin/UIntArray;", "partition-oBK06Vg", "([III)I", "Lkotlin/ULongArray;", "partition--nroSd4", "([JII)I", "Lkotlin/UShortArray;", "partition-Aa5vz7o", "([SII)I", "quickSort", "", "quickSort-4UcCI2c", "([BII)V", "quickSort-oBK06Vg", "([III)V", "quickSort--nroSd4", "([JII)V", "quickSort-Aa5vz7o", "([SII)V", "sortArray", "fromIndex", "toIndex", "sortArray-4UcCI2c", "sortArray-oBK06Vg", "sortArray--nroSd4", "sortArray-Aa5vz7o", "kotlin-stdlib"}, k = 2, mv = {1, 4, 0})
/* loaded from: classes.dex */
public final class UArraySortingKt {
    /* renamed from: partition-4UcCI2c */
    private static final int m420partition4UcCI2c(byte[] bArr, int i, int i2) {
        int i3;
        byte m71getw2LRezQ = UByteArray.m71getw2LRezQ(bArr, (i + i2) / 2);
        while (i <= i2) {
            while (true) {
                int m71getw2LRezQ2 = UByteArray.m71getw2LRezQ(bArr, i) & UByte.MAX_VALUE;
                i3 = m71getw2LRezQ & UByte.MAX_VALUE;
                if (Intrinsics.compare(m71getw2LRezQ2, i3) < 0) {
                    i++;
                }
            }
            while (Intrinsics.compare(UByteArray.m71getw2LRezQ(bArr, i2) & UByte.MAX_VALUE, i3) > 0) {
                i2--;
            }
            if (i <= i2) {
                byte m71getw2LRezQ3 = UByteArray.m71getw2LRezQ(bArr, i);
                UByteArray.m76setVurrAj0(bArr, i, UByteArray.m71getw2LRezQ(bArr, i2));
                UByteArray.m76setVurrAj0(bArr, i2, m71getw2LRezQ3);
                i++;
                i2--;
            }
        }
        return i;
    }

    /* renamed from: quickSort-4UcCI2c */
    private static final void m424quickSort4UcCI2c(byte[] bArr, int i, int i2) {
        int m420partition4UcCI2c = m420partition4UcCI2c(bArr, i, i2);
        int i3 = m420partition4UcCI2c - 1;
        if (i < i3) {
            m424quickSort4UcCI2c(bArr, i, i3);
        }
        if (m420partition4UcCI2c < i2) {
            m424quickSort4UcCI2c(bArr, m420partition4UcCI2c, i2);
        }
    }

    /* renamed from: partition-Aa5vz7o */
    private static final int m421partitionAa5vz7o(short[] sArr, int i, int i2) {
        int i3;
        short m307getMh2AYeg = UShortArray.m307getMh2AYeg(sArr, (i + i2) / 2);
        while (i <= i2) {
            while (true) {
                int m307getMh2AYeg2 = UShortArray.m307getMh2AYeg(sArr, i) & UShort.MAX_VALUE;
                i3 = m307getMh2AYeg & UShort.MAX_VALUE;
                if (Intrinsics.compare(m307getMh2AYeg2, i3) < 0) {
                    i++;
                }
            }
            while (Intrinsics.compare(UShortArray.m307getMh2AYeg(sArr, i2) & UShort.MAX_VALUE, i3) > 0) {
                i2--;
            }
            if (i <= i2) {
                short m307getMh2AYeg3 = UShortArray.m307getMh2AYeg(sArr, i);
                UShortArray.m312set01HTLdE(sArr, i, UShortArray.m307getMh2AYeg(sArr, i2));
                UShortArray.m312set01HTLdE(sArr, i2, m307getMh2AYeg3);
                i++;
                i2--;
            }
        }
        return i;
    }

    /* renamed from: quickSort-Aa5vz7o */
    private static final void m425quickSortAa5vz7o(short[] sArr, int i, int i2) {
        int m421partitionAa5vz7o = m421partitionAa5vz7o(sArr, i, i2);
        int i3 = m421partitionAa5vz7o - 1;
        if (i < i3) {
            m425quickSortAa5vz7o(sArr, i, i3);
        }
        if (m421partitionAa5vz7o < i2) {
            m425quickSortAa5vz7o(sArr, m421partitionAa5vz7o, i2);
        }
    }

    /* renamed from: partition-oBK06Vg */
    private static final int m422partitionoBK06Vg(int[] iArr, int i, int i2) {
        int m141getpVg5ArA = UIntArray.m141getpVg5ArA(iArr, (i + i2) / 2);
        while (i <= i2) {
            while (UnsignedKt.uintCompare(UIntArray.m141getpVg5ArA(iArr, i), m141getpVg5ArA) < 0) {
                i++;
            }
            while (UnsignedKt.uintCompare(UIntArray.m141getpVg5ArA(iArr, i2), m141getpVg5ArA) > 0) {
                i2--;
            }
            if (i <= i2) {
                int m141getpVg5ArA2 = UIntArray.m141getpVg5ArA(iArr, i);
                UIntArray.m146setVXSXFK8(iArr, i, UIntArray.m141getpVg5ArA(iArr, i2));
                UIntArray.m146setVXSXFK8(iArr, i2, m141getpVg5ArA2);
                i++;
                i2--;
            }
        }
        return i;
    }

    /* renamed from: quickSort-oBK06Vg */
    private static final void m426quickSortoBK06Vg(int[] iArr, int i, int i2) {
        int m422partitionoBK06Vg = m422partitionoBK06Vg(iArr, i, i2);
        int i3 = m422partitionoBK06Vg - 1;
        if (i < i3) {
            m426quickSortoBK06Vg(iArr, i, i3);
        }
        if (m422partitionoBK06Vg < i2) {
            m426quickSortoBK06Vg(iArr, m422partitionoBK06Vg, i2);
        }
    }

    /* renamed from: partition--nroSd4 */
    private static final int m419partitionnroSd4(long[] jArr, int i, int i2) {
        long m211getsVKNKU = ULongArray.m211getsVKNKU(jArr, (i + i2) / 2);
        while (i <= i2) {
            while (UnsignedKt.ulongCompare(ULongArray.m211getsVKNKU(jArr, i), m211getsVKNKU) < 0) {
                i++;
            }
            while (UnsignedKt.ulongCompare(ULongArray.m211getsVKNKU(jArr, i2), m211getsVKNKU) > 0) {
                i2--;
            }
            if (i <= i2) {
                long m211getsVKNKU2 = ULongArray.m211getsVKNKU(jArr, i);
                ULongArray.m216setk8EXiF4(jArr, i, ULongArray.m211getsVKNKU(jArr, i2));
                ULongArray.m216setk8EXiF4(jArr, i2, m211getsVKNKU2);
                i++;
                i2--;
            }
        }
        return i;
    }

    /* renamed from: quickSort--nroSd4 */
    private static final void m423quickSortnroSd4(long[] jArr, int i, int i2) {
        int m419partitionnroSd4 = m419partitionnroSd4(jArr, i, i2);
        int i3 = m419partitionnroSd4 - 1;
        if (i < i3) {
            m423quickSortnroSd4(jArr, i, i3);
        }
        if (m419partitionnroSd4 < i2) {
            m423quickSortnroSd4(jArr, m419partitionnroSd4, i2);
        }
    }

    /* renamed from: sortArray-4UcCI2c */
    public static final void m428sortArray4UcCI2c(byte[] array, int i, int i2) {
        Intrinsics.checkNotNullParameter(array, "array");
        m424quickSort4UcCI2c(array, i, i2 - 1);
    }

    /* renamed from: sortArray-Aa5vz7o */
    public static final void m429sortArrayAa5vz7o(short[] array, int i, int i2) {
        Intrinsics.checkNotNullParameter(array, "array");
        m425quickSortAa5vz7o(array, i, i2 - 1);
    }

    /* renamed from: sortArray-oBK06Vg */
    public static final void m430sortArrayoBK06Vg(int[] array, int i, int i2) {
        Intrinsics.checkNotNullParameter(array, "array");
        m426quickSortoBK06Vg(array, i, i2 - 1);
    }

    /* renamed from: sortArray--nroSd4 */
    public static final void m427sortArraynroSd4(long[] array, int i, int i2) {
        Intrinsics.checkNotNullParameter(array, "array");
        m423quickSortnroSd4(array, i, i2 - 1);
    }
}
