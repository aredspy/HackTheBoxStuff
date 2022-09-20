package kotlin.internal;

import com.facebook.react.uimanager.ViewProps;
import kotlin.Metadata;
import kotlin.UInt;
import kotlin.ULong;
import kotlin.UnsignedKt;
/* compiled from: UProgressionUtil.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\u001a*\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u00012\u0006\u0010\u0004\u001a\u00020\u0001H\u0002ø\u0001\u0000¢\u0006\u0004\b\u0005\u0010\u0006\u001a*\u0010\u0000\u001a\u00020\u00072\u0006\u0010\u0002\u001a\u00020\u00072\u0006\u0010\u0003\u001a\u00020\u00072\u0006\u0010\u0004\u001a\u00020\u0007H\u0002ø\u0001\u0000¢\u0006\u0004\b\b\u0010\t\u001a*\u0010\n\u001a\u00020\u00012\u0006\u0010\u000b\u001a\u00020\u00012\u0006\u0010\f\u001a\u00020\u00012\u0006\u0010\r\u001a\u00020\u000eH\u0001ø\u0001\u0000¢\u0006\u0004\b\u000f\u0010\u0006\u001a*\u0010\n\u001a\u00020\u00072\u0006\u0010\u000b\u001a\u00020\u00072\u0006\u0010\f\u001a\u00020\u00072\u0006\u0010\r\u001a\u00020\u0010H\u0001ø\u0001\u0000¢\u0006\u0004\b\u0011\u0010\t\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006\u0012"}, d2 = {"differenceModulo", "Lkotlin/UInt;", "a", "b", "c", "differenceModulo-WZ9TVnA", "(III)I", "Lkotlin/ULong;", "differenceModulo-sambcqE", "(JJJ)J", "getProgressionLastElement", ViewProps.START, ViewProps.END, "step", "", "getProgressionLastElement-Nkh28Cs", "", "getProgressionLastElement-7ftBX0g", "kotlin-stdlib"}, k = 2, mv = {1, 4, 0})
/* loaded from: classes.dex */
public final class UProgressionUtilKt {
    /* renamed from: differenceModulo-WZ9TVnA */
    private static final int m1168differenceModuloWZ9TVnA(int i, int i2, int i3) {
        int m320uintRemainderJ1ME1BU = UnsignedKt.m320uintRemainderJ1ME1BU(i, i3);
        int m320uintRemainderJ1ME1BU2 = UnsignedKt.m320uintRemainderJ1ME1BU(i2, i3);
        int uintCompare = UnsignedKt.uintCompare(m320uintRemainderJ1ME1BU, m320uintRemainderJ1ME1BU2);
        int m90constructorimpl = UInt.m90constructorimpl(m320uintRemainderJ1ME1BU - m320uintRemainderJ1ME1BU2);
        return uintCompare >= 0 ? m90constructorimpl : UInt.m90constructorimpl(m90constructorimpl + i3);
    }

    /* renamed from: differenceModulo-sambcqE */
    private static final long m1169differenceModulosambcqE(long j, long j2, long j3) {
        long m322ulongRemaindereb3DHEI = UnsignedKt.m322ulongRemaindereb3DHEI(j, j3);
        long m322ulongRemaindereb3DHEI2 = UnsignedKt.m322ulongRemaindereb3DHEI(j2, j3);
        int ulongCompare = UnsignedKt.ulongCompare(m322ulongRemaindereb3DHEI, m322ulongRemaindereb3DHEI2);
        long m160constructorimpl = ULong.m160constructorimpl(m322ulongRemaindereb3DHEI - m322ulongRemaindereb3DHEI2);
        return ulongCompare >= 0 ? m160constructorimpl : ULong.m160constructorimpl(m160constructorimpl + j3);
    }

    /* renamed from: getProgressionLastElement-Nkh28Cs */
    public static final int m1171getProgressionLastElementNkh28Cs(int i, int i2, int i3) {
        if (i3 > 0) {
            return UnsignedKt.uintCompare(i, i2) >= 0 ? i2 : UInt.m90constructorimpl(i2 - m1168differenceModuloWZ9TVnA(i2, i, UInt.m90constructorimpl(i3)));
        } else if (i3 >= 0) {
            throw new IllegalArgumentException("Step is zero.");
        } else {
            return UnsignedKt.uintCompare(i, i2) <= 0 ? i2 : UInt.m90constructorimpl(i2 + m1168differenceModuloWZ9TVnA(i, i2, UInt.m90constructorimpl(-i3)));
        }
    }

    /* renamed from: getProgressionLastElement-7ftBX0g */
    public static final long m1170getProgressionLastElement7ftBX0g(long j, long j2, long j3) {
        int i = (j3 > 0L ? 1 : (j3 == 0L ? 0 : -1));
        if (i > 0) {
            return UnsignedKt.ulongCompare(j, j2) >= 0 ? j2 : ULong.m160constructorimpl(j2 - m1169differenceModulosambcqE(j2, j, ULong.m160constructorimpl(j3)));
        } else if (i >= 0) {
            throw new IllegalArgumentException("Step is zero.");
        } else {
            return UnsignedKt.ulongCompare(j, j2) <= 0 ? j2 : ULong.m160constructorimpl(j2 + m1169differenceModulosambcqE(j, j2, ULong.m160constructorimpl(-j3)));
        }
    }
}
