package com.facebook.react.bridge;

import com.facebook.common.logging.FLog;
import com.facebook.jni.HybridData;
import com.facebook.react.common.ReactConstants;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
/* loaded from: classes.dex */
public class Inspector {
    private final HybridData mHybridData;

    /* loaded from: classes.dex */
    public interface RemoteConnection {
        void onDisconnect();

        void onMessage(String message);
    }

    private native LocalConnection connectNative(int pageId, RemoteConnection remote);

    private native Page[] getPagesNative();

    private static native Inspector instance();

    static {
        ReactBridge.staticInit();
    }

    public static List<Page> getPages() {
        try {
            return Arrays.asList(instance().getPagesNative());
        } catch (UnsatisfiedLinkError e) {
            FLog.e(ReactConstants.TAG, "Inspector doesn't work in open source yet", e);
            return Collections.emptyList();
        }
    }

    public static LocalConnection connect(int pageId, RemoteConnection remote) {
        try {
            return instance().connectNative(pageId, remote);
        } catch (UnsatisfiedLinkError e) {
            FLog.e(ReactConstants.TAG, "Inspector doesn't work in open source yet", e);
            throw new RuntimeException(e);
        }
    }

    private Inspector(HybridData hybridData) {
        this.mHybridData = hybridData;
    }

    /* loaded from: classes.dex */
    public static class Page {
        private final int mId;
        private final String mTitle;
        private final String mVM;

        public int getId() {
            return this.mId;
        }

        public String getTitle() {
            return this.mTitle;
        }

        public String getVM() {
            return this.mVM;
        }

        public String toString() {
            return "Page{mId=" + this.mId + ", mTitle='" + this.mTitle + "'}";
        }

        private Page(int id, String title, String vm) {
            this.mId = id;
            this.mTitle = title;
            this.mVM = vm;
        }
    }

    /* loaded from: classes.dex */
    public static class LocalConnection {
        private final HybridData mHybridData;

        public native void disconnect();

        public native void sendMessage(String message);

        private LocalConnection(HybridData hybridData) {
            this.mHybridData = hybridData;
        }
    }
}
