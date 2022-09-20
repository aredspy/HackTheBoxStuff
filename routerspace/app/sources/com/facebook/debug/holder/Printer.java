package com.facebook.debug.holder;

import com.facebook.debug.debugoverlay.model.DebugOverlayTag;
/* loaded from: classes.dex */
public interface Printer {
    void logMessage(final DebugOverlayTag tag, final String message);

    void logMessage(final DebugOverlayTag tag, final String message, Object... args);

    boolean shouldDisplayLogMessage(final DebugOverlayTag tag);
}
