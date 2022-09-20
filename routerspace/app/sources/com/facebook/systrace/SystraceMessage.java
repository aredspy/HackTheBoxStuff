package com.facebook.systrace;
/* loaded from: classes.dex */
public final class SystraceMessage {
    private static final Builder NOOP_BUILDER = new NoopBuilder();

    /* loaded from: classes.dex */
    public static abstract class Builder {
        public abstract Builder arg(String key, double value);

        public abstract Builder arg(String key, int value);

        public abstract Builder arg(String key, long value);

        public abstract Builder arg(String key, Object value);

        public abstract void flush();
    }

    /* loaded from: classes.dex */
    private interface Flusher {
        void flush(StringBuilder builder);
    }

    public static Builder beginSection(long tag, String sectionName) {
        return NOOP_BUILDER;
    }

    public static Builder endSection(long tag) {
        return NOOP_BUILDER;
    }

    /* loaded from: classes.dex */
    private static class NoopBuilder extends Builder {
        @Override // com.facebook.systrace.SystraceMessage.Builder
        public Builder arg(String key, double value) {
            return this;
        }

        @Override // com.facebook.systrace.SystraceMessage.Builder
        public Builder arg(String key, int value) {
            return this;
        }

        @Override // com.facebook.systrace.SystraceMessage.Builder
        public Builder arg(String key, long value) {
            return this;
        }

        @Override // com.facebook.systrace.SystraceMessage.Builder
        public Builder arg(String key, Object value) {
            return this;
        }

        @Override // com.facebook.systrace.SystraceMessage.Builder
        public void flush() {
        }

        private NoopBuilder() {
        }
    }
}
