package com.facebook.react.bridge;

import android.content.Context;
import com.facebook.react.common.DebugServerException;
/* loaded from: classes.dex */
public abstract class JSBundleLoader {
    public abstract String loadScript(JSBundleLoaderDelegate delegate);

    public static JSBundleLoader createAssetLoader(final Context context, final String assetUrl, final boolean loadSynchronously) {
        return new JSBundleLoader() { // from class: com.facebook.react.bridge.JSBundleLoader.1
            @Override // com.facebook.react.bridge.JSBundleLoader
            public String loadScript(JSBundleLoaderDelegate delegate) {
                delegate.loadScriptFromAssets(context.getAssets(), assetUrl, loadSynchronously);
                return assetUrl;
            }
        };
    }

    public static JSBundleLoader createFileLoader(final String fileName) {
        return createFileLoader(fileName, fileName, false);
    }

    public static JSBundleLoader createFileLoader(final String fileName, final String assetUrl, final boolean loadSynchronously) {
        return new JSBundleLoader() { // from class: com.facebook.react.bridge.JSBundleLoader.2
            @Override // com.facebook.react.bridge.JSBundleLoader
            public String loadScript(JSBundleLoaderDelegate delegate) {
                delegate.loadScriptFromFile(fileName, assetUrl, loadSynchronously);
                return fileName;
            }
        };
    }

    public static JSBundleLoader createCachedBundleFromNetworkLoader(final String sourceURL, final String cachedFileLocation) {
        return new JSBundleLoader() { // from class: com.facebook.react.bridge.JSBundleLoader.3
            @Override // com.facebook.react.bridge.JSBundleLoader
            public String loadScript(JSBundleLoaderDelegate delegate) {
                try {
                    delegate.loadScriptFromFile(cachedFileLocation, sourceURL, false);
                    return sourceURL;
                } catch (Exception e) {
                    throw DebugServerException.makeGeneric(sourceURL, e.getMessage(), e);
                }
            }
        };
    }

    public static JSBundleLoader createCachedSplitBundleFromNetworkLoader(final String sourceURL, final String cachedFileLocation) {
        return new JSBundleLoader() { // from class: com.facebook.react.bridge.JSBundleLoader.4
            @Override // com.facebook.react.bridge.JSBundleLoader
            public String loadScript(JSBundleLoaderDelegate delegate) {
                try {
                    delegate.loadSplitBundleFromFile(cachedFileLocation, sourceURL);
                    return sourceURL;
                } catch (Exception e) {
                    throw DebugServerException.makeGeneric(sourceURL, e.getMessage(), e);
                }
            }
        };
    }

    public static JSBundleLoader createRemoteDebuggerBundleLoader(final String proxySourceURL, final String realSourceURL) {
        return new JSBundleLoader() { // from class: com.facebook.react.bridge.JSBundleLoader.5
            @Override // com.facebook.react.bridge.JSBundleLoader
            public String loadScript(JSBundleLoaderDelegate delegate) {
                delegate.setSourceURLs(realSourceURL, proxySourceURL);
                return realSourceURL;
            }
        };
    }
}
