package com.facebook.imagepipeline.filter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import androidx.core.view.ViewCompat;
import com.facebook.common.internal.Preconditions;
/* loaded from: classes.dex */
public final class XferRoundFilter {
    private XferRoundFilter() {
    }

    public static void xferRoundBitmap(Bitmap output, Bitmap source, boolean enableAntiAliasing) {
        Paint paint;
        Paint paint2;
        Preconditions.checkNotNull(source);
        Preconditions.checkNotNull(output);
        output.setHasAlpha(true);
        if (enableAntiAliasing) {
            paint = new Paint(1);
            paint2 = new Paint(1);
        } else {
            paint = new Paint();
            paint2 = new Paint();
        }
        paint.setColor(ViewCompat.MEASURED_STATE_MASK);
        paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        Canvas canvas = new Canvas(output);
        float width = source.getWidth() / 2.0f;
        float height = source.getHeight() / 2.0f;
        canvas.drawCircle(width, height, Math.min(width, height), paint);
        canvas.drawBitmap(source, 0.0f, 0.0f, paint2);
    }

    public static boolean canUseXferRoundFilter() {
        return Build.VERSION.SDK_INT >= 12;
    }
}
