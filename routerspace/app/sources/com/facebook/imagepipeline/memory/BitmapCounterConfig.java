package com.facebook.imagepipeline.memory;
/* loaded from: classes.dex */
public class BitmapCounterConfig {
    public static final int DEFAULT_MAX_BITMAP_COUNT = 384;
    private int mMaxBitmapCount;

    public BitmapCounterConfig(Builder builder) {
        this.mMaxBitmapCount = DEFAULT_MAX_BITMAP_COUNT;
        this.mMaxBitmapCount = builder.getMaxBitmapCount();
    }

    public int getMaxBitmapCount() {
        return this.mMaxBitmapCount;
    }

    public void setMaxBitmapCount(int maxBitmapCount) {
        this.mMaxBitmapCount = maxBitmapCount;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    /* loaded from: classes.dex */
    public static class Builder {
        private int mMaxBitmapCount;

        private Builder() {
            this.mMaxBitmapCount = BitmapCounterConfig.DEFAULT_MAX_BITMAP_COUNT;
        }

        public Builder setMaxBitmapCount(int maxBitmapCount) {
            this.mMaxBitmapCount = maxBitmapCount;
            return this;
        }

        public int getMaxBitmapCount() {
            return this.mMaxBitmapCount;
        }

        public BitmapCounterConfig build() {
            return new BitmapCounterConfig(this);
        }
    }
}
