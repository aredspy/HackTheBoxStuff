package com.facebook.react.devsupport.interfaces;
/* loaded from: classes.dex */
public interface DevBundleDownloadListener {
    void onFailure(Exception cause);

    void onProgress(String status, Integer done, Integer total);

    void onSuccess();
}
