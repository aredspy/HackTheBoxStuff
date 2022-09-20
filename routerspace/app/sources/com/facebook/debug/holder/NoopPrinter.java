package com.facebook.debug.holder;

import com.facebook.debug.debugoverlay.model.DebugOverlayTag;
/* loaded from: classes.dex */
public class NoopPrinter implements Printer {
    public static final NoopPrinter INSTANCE = new NoopPrinter();

    @Override // com.facebook.debug.holder.Printer
    public void logMessage(DebugOverlayTag tag, String message) {
    }

    @Override // com.facebook.debug.holder.Printer
    public void logMessage(DebugOverlayTag tag, String message, Object... args) {
    }

    @Override // com.facebook.debug.holder.Printer
    public boolean shouldDisplayLogMessage(final DebugOverlayTag tag) {
        return false;
    }

    private NoopPrinter() {
    }
}
