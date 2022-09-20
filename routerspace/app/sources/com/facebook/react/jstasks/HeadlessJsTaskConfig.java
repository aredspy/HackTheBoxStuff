package com.facebook.react.jstasks;

import com.facebook.react.bridge.WritableMap;
/* loaded from: classes.dex */
public class HeadlessJsTaskConfig {
    private final boolean mAllowedInForeground;
    private final WritableMap mData;
    private final HeadlessJsTaskRetryPolicy mRetryPolicy;
    private final String mTaskKey;
    private final long mTimeout;

    public HeadlessJsTaskConfig(String taskKey, WritableMap data) {
        this(taskKey, data, 0L, false);
    }

    public HeadlessJsTaskConfig(String taskKey, WritableMap data, long timeout) {
        this(taskKey, data, timeout, false);
    }

    public HeadlessJsTaskConfig(String taskKey, WritableMap data, long timeout, boolean allowedInForeground) {
        this(taskKey, data, timeout, allowedInForeground, NoRetryPolicy.INSTANCE);
    }

    public HeadlessJsTaskConfig(String taskKey, WritableMap data, long timeout, boolean allowedInForeground, HeadlessJsTaskRetryPolicy retryPolicy) {
        this.mTaskKey = taskKey;
        this.mData = data;
        this.mTimeout = timeout;
        this.mAllowedInForeground = allowedInForeground;
        this.mRetryPolicy = retryPolicy;
    }

    public HeadlessJsTaskConfig(HeadlessJsTaskConfig source) {
        this.mTaskKey = source.mTaskKey;
        this.mData = source.mData.copy();
        this.mTimeout = source.mTimeout;
        this.mAllowedInForeground = source.mAllowedInForeground;
        HeadlessJsTaskRetryPolicy headlessJsTaskRetryPolicy = source.mRetryPolicy;
        if (headlessJsTaskRetryPolicy != null) {
            this.mRetryPolicy = headlessJsTaskRetryPolicy.copy();
        } else {
            this.mRetryPolicy = null;
        }
    }

    public String getTaskKey() {
        return this.mTaskKey;
    }

    public WritableMap getData() {
        return this.mData;
    }

    public long getTimeout() {
        return this.mTimeout;
    }

    public boolean isAllowedInForeground() {
        return this.mAllowedInForeground;
    }

    public HeadlessJsTaskRetryPolicy getRetryPolicy() {
        return this.mRetryPolicy;
    }
}
