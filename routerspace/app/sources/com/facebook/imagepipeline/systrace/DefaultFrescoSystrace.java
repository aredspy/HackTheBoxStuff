package com.facebook.imagepipeline.systrace;

import android.os.Build;
import android.os.Trace;
import com.facebook.imagepipeline.systrace.FrescoSystrace;
/* loaded from: classes.dex */
public class DefaultFrescoSystrace implements FrescoSystrace.Systrace {
    @Override // com.facebook.imagepipeline.systrace.FrescoSystrace.Systrace
    public void beginSection(String name) {
    }

    @Override // com.facebook.imagepipeline.systrace.FrescoSystrace.Systrace
    public void endSection() {
    }

    @Override // com.facebook.imagepipeline.systrace.FrescoSystrace.Systrace
    public boolean isTracing() {
        return false;
    }

    @Override // com.facebook.imagepipeline.systrace.FrescoSystrace.Systrace
    public FrescoSystrace.ArgsBuilder beginSectionWithArgs(String name) {
        return FrescoSystrace.NO_OP_ARGS_BUILDER;
    }

    /* loaded from: classes.dex */
    private static final class DefaultArgsBuilder implements FrescoSystrace.ArgsBuilder {
        private final StringBuilder mStringBuilder;

        public DefaultArgsBuilder(String name) {
            this.mStringBuilder = new StringBuilder(name);
        }

        @Override // com.facebook.imagepipeline.systrace.FrescoSystrace.ArgsBuilder
        public void flush() {
            if (this.mStringBuilder.length() > 127) {
                this.mStringBuilder.setLength(127);
            }
            if (Build.VERSION.SDK_INT >= 18) {
                Trace.beginSection(this.mStringBuilder.toString());
            }
        }

        @Override // com.facebook.imagepipeline.systrace.FrescoSystrace.ArgsBuilder
        public FrescoSystrace.ArgsBuilder arg(String key, Object value) {
            StringBuilder sb = this.mStringBuilder;
            sb.append(';');
            sb.append(key);
            sb.append('=');
            sb.append(value == null ? "null" : value.toString());
            return this;
        }

        @Override // com.facebook.imagepipeline.systrace.FrescoSystrace.ArgsBuilder
        public FrescoSystrace.ArgsBuilder arg(String key, int value) {
            StringBuilder sb = this.mStringBuilder;
            sb.append(';');
            sb.append(key);
            sb.append('=');
            sb.append(Integer.toString(value));
            return this;
        }

        @Override // com.facebook.imagepipeline.systrace.FrescoSystrace.ArgsBuilder
        public FrescoSystrace.ArgsBuilder arg(String key, long value) {
            StringBuilder sb = this.mStringBuilder;
            sb.append(';');
            sb.append(key);
            sb.append('=');
            sb.append(Long.toString(value));
            return this;
        }

        @Override // com.facebook.imagepipeline.systrace.FrescoSystrace.ArgsBuilder
        public FrescoSystrace.ArgsBuilder arg(String key, double value) {
            StringBuilder sb = this.mStringBuilder;
            sb.append(';');
            sb.append(key);
            sb.append('=');
            sb.append(Double.toString(value));
            return this;
        }
    }
}
