package com.facebook.imagepipeline.producers;

import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.core.ImagePipelineConfigInterface;
import com.facebook.imagepipeline.image.EncodedImageOrigin;
import com.facebook.imagepipeline.request.ImageRequest;
import java.util.Map;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public interface ProducerContext {

    /* loaded from: classes.dex */
    public @interface ExtraKeys {
        public static final String ENCODED_HEIGHT = "encoded_height";
        public static final String ENCODED_SIZE = "encoded_size";
        public static final String ENCODED_WIDTH = "encoded_width";
        public static final String IMAGE_FORMAT = "image_format";
        public static final String MULTIPLEX_BITMAP_COUNT = "multiplex_bmp_cnt";
        public static final String MULTIPLEX_ENCODED_COUNT = "multiplex_enc_cnt";
        public static final String NORMALIZED_URI = "uri_norm";
        public static final String ORIGIN = "origin";
        public static final String ORIGIN_SUBCATEGORY = "origin_sub";
        public static final String SOURCE_URI = "uri_source";
    }

    void addCallbacks(ProducerContextCallbacks callbacks);

    Object getCallerContext();

    EncodedImageOrigin getEncodedImageOrigin();

    @Nullable
    <E> E getExtra(String key);

    @Nullable
    <E> E getExtra(String key, @Nullable E valueIfNotFound);

    Map<String, Object> getExtras();

    String getId();

    ImagePipelineConfigInterface getImagePipelineConfig();

    ImageRequest getImageRequest();

    ImageRequest.RequestLevel getLowestPermittedRequestLevel();

    Priority getPriority();

    ProducerListener2 getProducerListener();

    @Nullable
    String getUiComponentId();

    boolean isIntermediateResultExpected();

    boolean isPrefetch();

    void putExtras(@Nullable Map<String, ?> extras);

    void putOriginExtra(@Nullable String origin);

    void putOriginExtra(@Nullable String origin, @Nullable String subcategory);

    void setEncodedImageOrigin(EncodedImageOrigin encodedImageOrigin);

    <E> void setExtra(String key, @Nullable E value);
}
