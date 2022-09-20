package com.facebook.react.bridge;

import android.content.res.AssetManager;
/* loaded from: classes.dex */
public interface JSBundleLoaderDelegate {
    void loadScriptFromAssets(AssetManager assetManager, String assetURL, boolean loadSynchronously);

    void loadScriptFromFile(String fileName, String sourceURL, boolean loadSynchronously);

    void loadSplitBundleFromFile(String fileName, String sourceURL);

    void setSourceURLs(String deviceURL, String remoteURL);
}
