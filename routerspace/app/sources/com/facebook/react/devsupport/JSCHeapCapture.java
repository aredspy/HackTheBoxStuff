package com.facebook.react.devsupport;

import com.facebook.fbreact.specs.NativeJSCHeapCaptureSpec;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.module.annotations.ReactModule;
import java.io.File;
@ReactModule(name = JSCHeapCapture.TAG, needsEagerInit = true)
/* loaded from: classes.dex */
public class JSCHeapCapture extends NativeJSCHeapCaptureSpec {
    public static final String TAG = "JSCHeapCapture";
    private CaptureCallback mCaptureInProgress = null;

    /* loaded from: classes.dex */
    public interface CaptureCallback {
        void onFailure(CaptureException error);

        void onSuccess(File capture);
    }

    /* loaded from: classes.dex */
    public interface HeapCapture extends JavaScriptModule {
        void captureHeap(String path);
    }

    @Override // com.facebook.react.bridge.NativeModule
    public String getName() {
        return TAG;
    }

    /* loaded from: classes.dex */
    public static class CaptureException extends Exception {
        public CaptureException(String message) {
            super(message);
        }

        CaptureException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public JSCHeapCapture(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    public synchronized void captureHeap(String path, final CaptureCallback callback) {
        if (this.mCaptureInProgress != null) {
            callback.onFailure(new CaptureException("Heap capture already in progress."));
            return;
        }
        File file = new File(path + "/capture.json");
        file.delete();
        ReactApplicationContext reactApplicationContextIfActiveOrWarn = getReactApplicationContextIfActiveOrWarn();
        if (reactApplicationContextIfActiveOrWarn != null) {
            HeapCapture heapCapture = (HeapCapture) reactApplicationContextIfActiveOrWarn.getJSModule(HeapCapture.class);
            if (heapCapture == null) {
                callback.onFailure(new CaptureException("Heap capture js module not registered."));
            } else {
                this.mCaptureInProgress = callback;
                heapCapture.captureHeap(file.getPath());
            }
        }
    }

    @Override // com.facebook.fbreact.specs.NativeJSCHeapCaptureSpec
    public synchronized void captureComplete(String path, String error) {
        CaptureCallback captureCallback = this.mCaptureInProgress;
        if (captureCallback != null) {
            if (error == null) {
                captureCallback.onSuccess(new File(path));
            } else {
                captureCallback.onFailure(new CaptureException(error));
            }
            this.mCaptureInProgress = null;
        }
    }
}
