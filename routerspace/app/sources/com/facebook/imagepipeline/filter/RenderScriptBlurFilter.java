package com.facebook.imagepipeline.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import com.facebook.common.internal.Preconditions;
/* loaded from: classes.dex */
public abstract class RenderScriptBlurFilter {
    public static final int BLUR_MAX_RADIUS = 25;

    public static void blurBitmap(final Bitmap dest, final Bitmap src, final Context context, final int radius) {
        Throwable th;
        Preconditions.checkNotNull(dest);
        Preconditions.checkNotNull(src);
        Preconditions.checkNotNull(context);
        Preconditions.checkArgument(Boolean.valueOf(radius > 0 && radius <= 25));
        RenderScript renderScript = null;
        try {
            RenderScript renderScript2 = (RenderScript) Preconditions.checkNotNull(RenderScript.create(context));
            try {
                ScriptIntrinsicBlur create = ScriptIntrinsicBlur.create(renderScript2, Element.U8_4(renderScript2));
                Allocation allocation = (Allocation) Preconditions.checkNotNull(Allocation.createFromBitmap(renderScript2, src));
                Allocation allocation2 = (Allocation) Preconditions.checkNotNull(Allocation.createFromBitmap(renderScript2, dest));
                create.setRadius(radius);
                create.setInput(allocation);
                create.forEach(allocation2);
                allocation2.copyTo(dest);
                create.destroy();
                allocation.destroy();
                allocation2.destroy();
                if (renderScript2 == null) {
                    return;
                }
                renderScript2.destroy();
            } catch (Throwable th2) {
                th = th2;
                renderScript = renderScript2;
                if (renderScript != null) {
                    renderScript.destroy();
                }
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
        }
    }

    public static boolean canUseRenderScript() {
        return Build.VERSION.SDK_INT >= 17;
    }
}
