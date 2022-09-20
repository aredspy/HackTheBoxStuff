package com.facebook.react.shell;

import com.facebook.imagepipeline.core.ImagePipelineConfig;
/* loaded from: classes.dex */
public class MainPackageConfig {
    private ImagePipelineConfig mFrescoConfig;

    private MainPackageConfig(Builder builder) {
        this.mFrescoConfig = builder.mFrescoConfig;
    }

    public ImagePipelineConfig getFrescoConfig() {
        return this.mFrescoConfig;
    }

    /* loaded from: classes.dex */
    public static class Builder {
        private ImagePipelineConfig mFrescoConfig;

        public Builder setFrescoConfig(ImagePipelineConfig frescoConfig) {
            this.mFrescoConfig = frescoConfig;
            return this;
        }

        public MainPackageConfig build() {
            return new MainPackageConfig(this);
        }
    }
}
