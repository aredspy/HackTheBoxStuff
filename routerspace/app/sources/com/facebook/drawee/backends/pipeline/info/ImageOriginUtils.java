package com.facebook.drawee.backends.pipeline.info;

import androidx.core.os.EnvironmentCompat;
import com.facebook.imagepipeline.producers.BitmapMemoryCacheGetProducer;
import com.facebook.imagepipeline.producers.BitmapMemoryCacheProducer;
import com.facebook.imagepipeline.producers.DataFetchProducer;
import com.facebook.imagepipeline.producers.DiskCacheReadProducer;
import com.facebook.imagepipeline.producers.EncodedMemoryCacheProducer;
import com.facebook.imagepipeline.producers.LocalAssetFetchProducer;
import com.facebook.imagepipeline.producers.LocalContentUriFetchProducer;
import com.facebook.imagepipeline.producers.LocalContentUriThumbnailFetchProducer;
import com.facebook.imagepipeline.producers.LocalFileFetchProducer;
import com.facebook.imagepipeline.producers.LocalResourceFetchProducer;
import com.facebook.imagepipeline.producers.LocalVideoThumbnailProducer;
import com.facebook.imagepipeline.producers.NetworkFetchProducer;
import com.facebook.imagepipeline.producers.PartialDiskCacheProducer;
import com.facebook.imagepipeline.producers.PostprocessedBitmapMemoryCacheProducer;
import com.facebook.imagepipeline.producers.QualifiedResourceFetchProducer;
/* loaded from: classes.dex */
public class ImageOriginUtils {
    public static String toString(int imageOrigin) {
        switch (imageOrigin) {
            case 2:
                return "network";
            case 3:
                return "disk";
            case 4:
                return "memory_encoded";
            case 5:
                return "memory_bitmap";
            case 6:
                return "memory_bitmap_shortcut";
            case 7:
                return "local";
            default:
                return EnvironmentCompat.MEDIA_UNKNOWN;
        }
    }

    public static int mapProducerNameToImageOrigin(final String producerName) {
        producerName.hashCode();
        char c = 65535;
        switch (producerName.hashCode()) {
            case -1917159454:
                if (producerName.equals(QualifiedResourceFetchProducer.PRODUCER_NAME)) {
                    c = 0;
                    break;
                }
                break;
            case -1914072202:
                if (producerName.equals(BitmapMemoryCacheGetProducer.PRODUCER_NAME)) {
                    c = 1;
                    break;
                }
                break;
            case -1683996557:
                if (producerName.equals(LocalResourceFetchProducer.PRODUCER_NAME)) {
                    c = 2;
                    break;
                }
                break;
            case -1579985851:
                if (producerName.equals(LocalFileFetchProducer.PRODUCER_NAME)) {
                    c = 3;
                    break;
                }
                break;
            case -1307634203:
                if (producerName.equals(EncodedMemoryCacheProducer.PRODUCER_NAME)) {
                    c = 4;
                    break;
                }
                break;
            case -1224383234:
                if (producerName.equals(NetworkFetchProducer.PRODUCER_NAME)) {
                    c = 5;
                    break;
                }
                break;
            case 473552259:
                if (producerName.equals(LocalVideoThumbnailProducer.PRODUCER_NAME)) {
                    c = 6;
                    break;
                }
                break;
            case 656304759:
                if (producerName.equals(DiskCacheReadProducer.PRODUCER_NAME)) {
                    c = 7;
                    break;
                }
                break;
            case 957714404:
                if (producerName.equals(BitmapMemoryCacheProducer.PRODUCER_NAME)) {
                    c = '\b';
                    break;
                }
                break;
            case 1019542023:
                if (producerName.equals(LocalAssetFetchProducer.PRODUCER_NAME)) {
                    c = '\t';
                    break;
                }
                break;
            case 1023071510:
                if (producerName.equals(PostprocessedBitmapMemoryCacheProducer.PRODUCER_NAME)) {
                    c = '\n';
                    break;
                }
                break;
            case 1721672898:
                if (producerName.equals(DataFetchProducer.PRODUCER_NAME)) {
                    c = 11;
                    break;
                }
                break;
            case 1793127518:
                if (producerName.equals(LocalContentUriThumbnailFetchProducer.PRODUCER_NAME)) {
                    c = '\f';
                    break;
                }
                break;
            case 2109593398:
                if (producerName.equals(PartialDiskCacheProducer.PRODUCER_NAME)) {
                    c = '\r';
                    break;
                }
                break;
            case 2113652014:
                if (producerName.equals(LocalContentUriFetchProducer.PRODUCER_NAME)) {
                    c = 14;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
            case 2:
            case 3:
            case 6:
            case '\t':
            case 11:
            case '\f':
            case 14:
                return 7;
            case 1:
            case '\b':
            case '\n':
                return 5;
            case 4:
                return 4;
            case 5:
                return 2;
            case 7:
            case '\r':
                return 3;
            default:
                return 1;
        }
    }

    private ImageOriginUtils() {
    }
}
