package com.facebook.imagepipeline.memory;

import android.graphics.Bitmap;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.memory.MemoryTrimType;
/* loaded from: classes.dex */
public class DummyBitmapPool implements BitmapPool {
    @Override // com.facebook.common.memory.MemoryTrimmable
    public void trim(MemoryTrimType trimType) {
    }

    @Override // com.facebook.common.memory.Pool
    public Bitmap get(int size) {
        return Bitmap.createBitmap(1, (int) Math.ceil(size / 2.0d), Bitmap.Config.RGB_565);
    }

    public void release(Bitmap value) {
        Preconditions.checkNotNull(value);
        value.recycle();
    }
}
