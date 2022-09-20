package com.facebook.datasource;

import com.facebook.common.internal.Preconditions;
import java.util.Map;
/* loaded from: classes.dex */
public class SimpleDataSource<T> extends AbstractDataSource<T> {
    private SimpleDataSource() {
    }

    public static <T> SimpleDataSource<T> create() {
        return new SimpleDataSource<>();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.facebook.datasource.AbstractDataSource
    public boolean setResult(T value, boolean isLast, Map<String, Object> extras) {
        return super.setResult(Preconditions.checkNotNull(value), isLast, extras);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public boolean setResult(T value) {
        return super.setResult(Preconditions.checkNotNull(value), true, null);
    }

    @Override // com.facebook.datasource.AbstractDataSource
    public boolean setFailure(Throwable throwable) {
        return super.setFailure((Throwable) Preconditions.checkNotNull(throwable));
    }

    @Override // com.facebook.datasource.AbstractDataSource
    public boolean setProgress(float progress) {
        return super.setProgress(progress);
    }
}
