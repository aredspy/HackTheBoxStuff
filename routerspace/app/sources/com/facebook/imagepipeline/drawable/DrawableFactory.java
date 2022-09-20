package com.facebook.imagepipeline.drawable;

import android.graphics.drawable.Drawable;
import com.facebook.imagepipeline.image.CloseableImage;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public interface DrawableFactory {
    @Nullable
    Drawable createDrawable(@Nonnull CloseableImage image);

    boolean supportsImageType(@Nonnull CloseableImage image);
}
