package com.facebook.react.modules.fresco;

import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.react.bridge.ReadableMap;
/* loaded from: classes.dex */
public class ReactNetworkImageRequest extends ImageRequest {
    private final ReadableMap mHeaders;

    public static ReactNetworkImageRequest fromBuilderWithHeaders(ImageRequestBuilder builder, ReadableMap headers) {
        return new ReactNetworkImageRequest(builder, headers);
    }

    protected ReactNetworkImageRequest(ImageRequestBuilder builder, ReadableMap headers) {
        super(builder);
        this.mHeaders = headers;
    }

    public ReadableMap getHeaders() {
        return this.mHeaders;
    }
}
