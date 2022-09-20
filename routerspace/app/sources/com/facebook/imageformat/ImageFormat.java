package com.facebook.imageformat;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class ImageFormat {
    public static final ImageFormat UNKNOWN = new ImageFormat("UNKNOWN", null);
    @Nullable
    private final String mFileExtension;
    private final String mName;

    /* loaded from: classes.dex */
    public interface FormatChecker {
        @Nullable
        ImageFormat determineFormat(@Nonnull byte[] headerBytes, int headerSize);

        int getHeaderSize();
    }

    public ImageFormat(String name, @Nullable String fileExtension) {
        this.mName = name;
        this.mFileExtension = fileExtension;
    }

    @Nullable
    public String getFileExtension() {
        return this.mFileExtension;
    }

    public String toString() {
        return getName();
    }

    public String getName() {
        return this.mName;
    }
}
