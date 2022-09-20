package com.th3rdwave.safeareacontext;

import java.util.EnumSet;
/* loaded from: classes.dex */
public class SafeAreaViewLocalData {
    private EnumSet<SafeAreaViewEdges> mEdges;
    private EdgeInsets mInsets;
    private SafeAreaViewMode mMode;

    public SafeAreaViewLocalData(EdgeInsets insets, SafeAreaViewMode mode, EnumSet<SafeAreaViewEdges> edges) {
        this.mInsets = insets;
        this.mMode = mode;
        this.mEdges = edges;
    }

    public EdgeInsets getInsets() {
        return this.mInsets;
    }

    public SafeAreaViewMode getMode() {
        return this.mMode;
    }

    public EnumSet<SafeAreaViewEdges> getEdges() {
        return this.mEdges;
    }
}
