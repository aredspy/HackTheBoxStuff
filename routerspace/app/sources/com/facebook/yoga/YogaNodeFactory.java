package com.facebook.yoga;
/* loaded from: classes.dex */
public abstract class YogaNodeFactory {
    public static YogaNode create() {
        return new YogaNodeJNIFinalizer();
    }

    public static YogaNode create(YogaConfig config) {
        return new YogaNodeJNIFinalizer(config);
    }
}
