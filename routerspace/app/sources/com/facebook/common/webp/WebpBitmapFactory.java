package com.facebook.common.webp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import java.io.FileDescriptor;
import java.io.InputStream;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public interface WebpBitmapFactory {

    /* loaded from: classes.dex */
    public interface WebpErrorLogger {
        void onWebpErrorLog(String message, @Nullable String extra);
    }

    @Nullable
    Bitmap decodeByteArray(byte[] array, int offset, int length, @Nullable BitmapFactory.Options opts);

    @Nullable
    Bitmap decodeFile(String pathName, @Nullable BitmapFactory.Options opts);

    @Nullable
    Bitmap decodeFileDescriptor(FileDescriptor fd, @Nullable Rect outPadding, @Nullable BitmapFactory.Options opts);

    @Nullable
    Bitmap decodeStream(InputStream inputStream, @Nullable Rect outPadding, @Nullable BitmapFactory.Options opts);

    void setBitmapCreator(final BitmapCreator bitmapCreator);

    void setWebpErrorLogger(WebpErrorLogger logger);
}
