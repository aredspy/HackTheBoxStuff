package kotlin.text;

import java.util.Iterator;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;
/* compiled from: _Strings.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\f\n\u0000\n\u0002\u0010(\n\u0002\u0010\f\n\u0000\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001H\n¢\u0006\u0002\b\u0003"}, d2 = {"<anonymous>", "", "", "invoke"}, k = 3, mv = {1, 4, 0})
/* loaded from: classes.dex */
final class StringsKt___StringsKt$withIndex$1 extends Lambda implements Function0<Iterator<? extends Character>> {
    final /* synthetic */ CharSequence $this_withIndex;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public StringsKt___StringsKt$withIndex$1(CharSequence charSequence) {
        super(0);
        this.$this_withIndex = charSequence;
    }

    @Override // kotlin.jvm.functions.Function0
    public final Iterator<? extends Character> invoke() {
        return StringsKt.iterator(this.$this_withIndex);
    }
}
