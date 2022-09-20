package com.facebook.react.views.imagehelper;

import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import java.util.List;
/* loaded from: classes.dex */
public class MultiSourceHelper {

    /* loaded from: classes.dex */
    public static class MultiSourceResult {
        private final ImageSource bestResult;
        private final ImageSource bestResultInCache;

        private MultiSourceResult(ImageSource bestResult, ImageSource bestResultInCache) {
            this.bestResult = bestResult;
            this.bestResultInCache = bestResultInCache;
        }

        public ImageSource getBestResult() {
            return this.bestResult;
        }

        public ImageSource getBestResultInCache() {
            return this.bestResultInCache;
        }
    }

    public static MultiSourceResult getBestSourceForSize(int width, int height, List<ImageSource> sources) {
        return getBestSourceForSize(width, height, sources, 1.0d);
    }

    public static MultiSourceResult getBestSourceForSize(int width, int height, List<ImageSource> sources, double multiplier) {
        if (sources.isEmpty()) {
            return new MultiSourceResult(null, null);
        }
        if (sources.size() == 1) {
            return new MultiSourceResult(sources.get(0), null);
        }
        if (width <= 0 || height <= 0) {
            return new MultiSourceResult(null, null);
        }
        ImagePipeline imagePipeline = ImagePipelineFactory.getInstance().getImagePipeline();
        double d = width * height * multiplier;
        double d2 = Double.MAX_VALUE;
        double d3 = Double.MAX_VALUE;
        ImageSource imageSource = null;
        ImageSource imageSource2 = null;
        for (ImageSource imageSource3 : sources) {
            double abs = Math.abs(1.0d - (imageSource3.getSize() / d));
            if (abs < d2) {
                imageSource2 = imageSource3;
                d2 = abs;
            }
            if (abs < d3 && (imagePipeline.isInBitmapMemoryCache(imageSource3.getUri()) || imagePipeline.isInDiskCacheSync(imageSource3.getUri()))) {
                imageSource = imageSource3;
                d3 = abs;
            }
        }
        if (imageSource != null && imageSource2 != null && imageSource.getSource().equals(imageSource2.getSource())) {
            imageSource = null;
        }
        return new MultiSourceResult(imageSource2, imageSource);
    }
}
