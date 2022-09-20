package com.facebook.fresco.ui.common;

import android.net.Uri;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public interface ControllerListener2<INFO> {
    void onFailure(String id, @Nullable Throwable throwable, @Nullable Extras extraData);

    void onFinalImageSet(String id, @Nullable INFO imageInfo, @Nullable Extras extraData);

    void onIntermediateImageFailed(String id);

    void onIntermediateImageSet(String id, @Nullable INFO imageInfo);

    void onRelease(String id, @Nullable Extras extraData);

    void onSubmit(String id, @Nullable Object callerContext, @Nullable Extras extraData);

    /* loaded from: classes.dex */
    public static class Extras {
        @Nullable
        public Object callerContext;
        @Nullable
        public Map<String, Object> componentExtras;
        @Nullable
        public Map<String, Object> datasourceExtras;
        @Nullable
        public Map<String, Object> imageExtras;
        @Nullable
        public Uri mainUri;
        @Nullable
        public Object scaleType;
        @Nullable
        public Map<String, Object> shortcutExtras;
        public int viewportWidth = -1;
        public int viewportHeight = -1;
        public float focusX = -1.0f;
        public float focusY = -1.0f;

        public static Extras of(@Nullable Map<String, Object> componentExtras) {
            Extras extras = new Extras();
            extras.componentExtras = componentExtras;
            return extras;
        }

        public Extras makeExtrasCopy() {
            Extras extras = new Extras();
            extras.componentExtras = copyMap(this.componentExtras);
            extras.shortcutExtras = copyMap(this.shortcutExtras);
            extras.datasourceExtras = copyMap(this.datasourceExtras);
            extras.imageExtras = copyMap(this.imageExtras);
            extras.callerContext = this.callerContext;
            extras.mainUri = this.mainUri;
            extras.viewportWidth = this.viewportWidth;
            extras.viewportHeight = this.viewportHeight;
            extras.scaleType = this.scaleType;
            extras.focusX = this.focusX;
            extras.focusY = this.focusY;
            return extras;
        }

        private static Map<String, Object> copyMap(Map<String, Object> map) {
            if (map == null) {
                return null;
            }
            return new ConcurrentHashMap(map);
        }
    }
}
