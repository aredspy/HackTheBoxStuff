package androidx.autofill.inline;

import android.app.PendingIntent;
import android.app.slice.Slice;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.autofill.inline.common.SlicedContent;
import androidx.autofill.inline.v1.InlineSuggestionUi;
/* loaded from: classes.dex */
public final class Renderer {
    private static final String TAG = "Renderer";

    public static Bundle getSupportedInlineUiVersionsAsBundle() {
        Bundle bundle = new Bundle();
        VersionUtils.writeSupportedVersions(bundle);
        return bundle;
    }

    public static View render(Context context, Slice slice, Bundle bundle) {
        String version = SlicedContent.getVersion(slice);
        if (!VersionUtils.isVersionSupported(version)) {
            Log.w(TAG, "Content version unsupported.");
            return null;
        }
        Bundle readStyleByVersion = VersionUtils.readStyleByVersion(bundle, version);
        if (readStyleByVersion == null) {
            Log.w(TAG, "Cannot find a style with the same version as the slice.");
            return null;
        }
        version.hashCode();
        if (version.equals(UiVersions.INLINE_UI_VERSION_1)) {
            InlineSuggestionUi.Style fromBundle = InlineSuggestionUi.fromBundle(readStyleByVersion);
            InlineSuggestionUi.Content fromSlice = InlineSuggestionUi.fromSlice(slice);
            if (fromBundle != null && slice != null) {
                return InlineSuggestionUi.render(context, fromSlice, fromBundle);
            }
            return null;
        }
        Log.w(TAG, "Renderer does not support the style/content version: " + version);
        return null;
    }

    public static PendingIntent getAttributionIntent(Slice slice) {
        String version = SlicedContent.getVersion(slice);
        if (!VersionUtils.isVersionSupported(version)) {
            Log.w(TAG, "Content version unsupported.");
            return null;
        }
        version.hashCode();
        if (version.equals(UiVersions.INLINE_UI_VERSION_1)) {
            InlineSuggestionUi.Content fromSlice = InlineSuggestionUi.fromSlice(slice);
            if (fromSlice != null) {
                return InlineSuggestionUi.getAttributionIntent(fromSlice);
            }
            return null;
        }
        Log.w(TAG, "Renderer does not support the content version: " + version);
        return null;
    }

    private Renderer() {
    }
}
