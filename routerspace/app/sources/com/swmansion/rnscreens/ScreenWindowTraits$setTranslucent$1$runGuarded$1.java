package com.swmansion.rnscreens;

import android.view.View;
import android.view.WindowInsets;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
/* compiled from: ScreenWindowTraits.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0012\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0000\u001a\n \u0002*\u0004\u0018\u00010\u00010\u00012\u000e\u0010\u0003\u001a\n \u0002*\u0004\u0018\u00010\u00040\u00042\u000e\u0010\u0005\u001a\n \u0002*\u0004\u0018\u00010\u00010\u0001H\n¢\u0006\u0002\b\u0006"}, d2 = {"<anonymous>", "Landroid/view/WindowInsets;", "kotlin.jvm.PlatformType", "v", "Landroid/view/View;", "insets", "onApplyWindowInsets"}, k = 3, mv = {1, 4, 0})
/* loaded from: classes.dex */
final class ScreenWindowTraits$setTranslucent$1$runGuarded$1 implements View.OnApplyWindowInsetsListener {
    public static final ScreenWindowTraits$setTranslucent$1$runGuarded$1 INSTANCE = new ScreenWindowTraits$setTranslucent$1$runGuarded$1();

    ScreenWindowTraits$setTranslucent$1$runGuarded$1() {
    }

    @Override // android.view.View.OnApplyWindowInsetsListener
    public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
        WindowInsets defaultInsets = view.onApplyWindowInsets(windowInsets);
        Intrinsics.checkNotNullExpressionValue(defaultInsets, "defaultInsets");
        return defaultInsets.replaceSystemWindowInsets(defaultInsets.getSystemWindowInsetLeft(), 0, defaultInsets.getSystemWindowInsetRight(), defaultInsets.getSystemWindowInsetBottom());
    }
}
