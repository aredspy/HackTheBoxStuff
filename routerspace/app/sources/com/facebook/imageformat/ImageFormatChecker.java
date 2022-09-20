package com.facebook.imageformat;

import com.facebook.common.internal.ByteStreams;
import com.facebook.common.internal.Closeables;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.Throwables;
import com.facebook.imageformat.ImageFormat;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class ImageFormatChecker {
    private static ImageFormatChecker sInstance;
    @Nullable
    private List<ImageFormat.FormatChecker> mCustomImageFormatCheckers;
    private final DefaultImageFormatChecker mDefaultFormatChecker = new DefaultImageFormatChecker();
    private int mMaxHeaderLength;

    private ImageFormatChecker() {
        updateMaxHeaderLength();
    }

    public void setUseNewOrder(boolean useNewOrder) {
        this.mDefaultFormatChecker.setUseNewOrder(useNewOrder);
    }

    public void setCustomImageFormatCheckers(@Nullable List<ImageFormat.FormatChecker> customImageFormatCheckers) {
        this.mCustomImageFormatCheckers = customImageFormatCheckers;
        updateMaxHeaderLength();
    }

    public ImageFormat determineImageFormat(final InputStream is) throws IOException {
        Preconditions.checkNotNull(is);
        int i = this.mMaxHeaderLength;
        byte[] bArr = new byte[i];
        int readHeaderFromStream = readHeaderFromStream(i, is, bArr);
        ImageFormat determineFormat = this.mDefaultFormatChecker.determineFormat(bArr, readHeaderFromStream);
        if (determineFormat == null || determineFormat == ImageFormat.UNKNOWN) {
            List<ImageFormat.FormatChecker> list = this.mCustomImageFormatCheckers;
            if (list != null) {
                for (ImageFormat.FormatChecker formatChecker : list) {
                    ImageFormat determineFormat2 = formatChecker.determineFormat(bArr, readHeaderFromStream);
                    if (determineFormat2 != null && determineFormat2 != ImageFormat.UNKNOWN) {
                        return determineFormat2;
                    }
                }
            }
            return ImageFormat.UNKNOWN;
        }
        return determineFormat;
    }

    private void updateMaxHeaderLength() {
        this.mMaxHeaderLength = this.mDefaultFormatChecker.getHeaderSize();
        List<ImageFormat.FormatChecker> list = this.mCustomImageFormatCheckers;
        if (list != null) {
            for (ImageFormat.FormatChecker formatChecker : list) {
                this.mMaxHeaderLength = Math.max(this.mMaxHeaderLength, formatChecker.getHeaderSize());
            }
        }
    }

    private static int readHeaderFromStream(int maxHeaderLength, final InputStream is, final byte[] imageHeaderBytes) throws IOException {
        Preconditions.checkNotNull(is);
        Preconditions.checkNotNull(imageHeaderBytes);
        Preconditions.checkArgument(Boolean.valueOf(imageHeaderBytes.length >= maxHeaderLength));
        if (is.markSupported()) {
            try {
                is.mark(maxHeaderLength);
                return ByteStreams.read(is, imageHeaderBytes, 0, maxHeaderLength);
            } finally {
                is.reset();
            }
        }
        return ByteStreams.read(is, imageHeaderBytes, 0, maxHeaderLength);
    }

    public static synchronized ImageFormatChecker getInstance() {
        ImageFormatChecker imageFormatChecker;
        synchronized (ImageFormatChecker.class) {
            if (sInstance == null) {
                sInstance = new ImageFormatChecker();
            }
            imageFormatChecker = sInstance;
        }
        return imageFormatChecker;
    }

    public static ImageFormat getImageFormat(final InputStream is) throws IOException {
        return getInstance().determineImageFormat(is);
    }

    public static ImageFormat getImageFormat_WrapIOException(final InputStream is) {
        try {
            return getImageFormat(is);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    public static ImageFormat getImageFormat(String filename) {
        Throwable th;
        FileInputStream fileInputStream;
        FileInputStream fileInputStream2 = null;
        try {
            try {
                fileInputStream = new FileInputStream(filename);
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (IOException unused) {
        }
        try {
            ImageFormat imageFormat = getImageFormat(fileInputStream);
            Closeables.closeQuietly(fileInputStream);
            return imageFormat;
        } catch (IOException unused2) {
            fileInputStream2 = fileInputStream;
            ImageFormat imageFormat2 = ImageFormat.UNKNOWN;
            Closeables.closeQuietly(fileInputStream2);
            return imageFormat2;
        } catch (Throwable th3) {
            th = th3;
            fileInputStream2 = fileInputStream;
            Closeables.closeQuietly(fileInputStream2);
            throw th;
        }
    }
}
