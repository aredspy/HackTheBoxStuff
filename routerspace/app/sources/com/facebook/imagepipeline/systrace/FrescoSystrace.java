package com.facebook.imagepipeline.systrace;

import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class FrescoSystrace {
    public static final ArgsBuilder NO_OP_ARGS_BUILDER = new NoOpArgsBuilder();
    @Nullable
    private static volatile Systrace sInstance = null;

    /* loaded from: classes.dex */
    public interface ArgsBuilder {
        ArgsBuilder arg(String key, double value);

        ArgsBuilder arg(String key, int value);

        ArgsBuilder arg(String key, long value);

        ArgsBuilder arg(String key, Object value);

        void flush();
    }

    /* loaded from: classes.dex */
    public interface Systrace {
        void beginSection(String name);

        ArgsBuilder beginSectionWithArgs(String name);

        void endSection();

        boolean isTracing();
    }

    private FrescoSystrace() {
    }

    public static void provide(Systrace instance) {
        sInstance = instance;
    }

    public static void beginSection(String name) {
        getInstance().beginSection(name);
    }

    public static ArgsBuilder beginSectionWithArgs(String name) {
        return getInstance().beginSectionWithArgs(name);
    }

    public static void endSection() {
        getInstance().endSection();
    }

    public static boolean isTracing() {
        return getInstance().isTracing();
    }

    private static Systrace getInstance() {
        if (sInstance == null) {
            synchronized (FrescoSystrace.class) {
                if (sInstance == null) {
                    sInstance = new DefaultFrescoSystrace();
                }
            }
        }
        return sInstance;
    }

    /* loaded from: classes.dex */
    private static final class NoOpArgsBuilder implements ArgsBuilder {
        @Override // com.facebook.imagepipeline.systrace.FrescoSystrace.ArgsBuilder
        public ArgsBuilder arg(String key, double value) {
            return this;
        }

        @Override // com.facebook.imagepipeline.systrace.FrescoSystrace.ArgsBuilder
        public ArgsBuilder arg(String key, int value) {
            return this;
        }

        @Override // com.facebook.imagepipeline.systrace.FrescoSystrace.ArgsBuilder
        public ArgsBuilder arg(String key, long value) {
            return this;
        }

        @Override // com.facebook.imagepipeline.systrace.FrescoSystrace.ArgsBuilder
        public ArgsBuilder arg(String key, Object value) {
            return this;
        }

        @Override // com.facebook.imagepipeline.systrace.FrescoSystrace.ArgsBuilder
        public void flush() {
        }

        private NoOpArgsBuilder() {
        }
    }
}
