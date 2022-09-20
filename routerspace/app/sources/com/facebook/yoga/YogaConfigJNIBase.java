package com.facebook.yoga;
/* loaded from: classes.dex */
public abstract class YogaConfigJNIBase extends YogaConfig {
    private YogaLogger mLogger;
    long mNativePointer;

    private YogaConfigJNIBase(long nativePointer) {
        if (nativePointer == 0) {
            throw new IllegalStateException("Failed to allocate native memory");
        }
        this.mNativePointer = nativePointer;
    }

    public YogaConfigJNIBase() {
        this(YogaNative.jni_YGConfigNewJNI());
    }

    YogaConfigJNIBase(boolean useVanillaJNI) {
        this(YogaNative.jni_YGConfigNewJNI());
    }

    @Override // com.facebook.yoga.YogaConfig
    public void setExperimentalFeatureEnabled(YogaExperimentalFeature feature, boolean enabled) {
        YogaNative.jni_YGConfigSetExperimentalFeatureEnabledJNI(this.mNativePointer, feature.intValue(), enabled);
    }

    @Override // com.facebook.yoga.YogaConfig
    public void setUseWebDefaults(boolean useWebDefaults) {
        YogaNative.jni_YGConfigSetUseWebDefaultsJNI(this.mNativePointer, useWebDefaults);
    }

    @Override // com.facebook.yoga.YogaConfig
    public void setPrintTreeFlag(boolean enable) {
        YogaNative.jni_YGConfigSetPrintTreeFlagJNI(this.mNativePointer, enable);
    }

    @Override // com.facebook.yoga.YogaConfig
    public void setPointScaleFactor(float pixelsInPoint) {
        YogaNative.jni_YGConfigSetPointScaleFactorJNI(this.mNativePointer, pixelsInPoint);
    }

    @Override // com.facebook.yoga.YogaConfig
    public void setUseLegacyStretchBehaviour(boolean useLegacyStretchBehaviour) {
        YogaNative.jni_YGConfigSetUseLegacyStretchBehaviourJNI(this.mNativePointer, useLegacyStretchBehaviour);
    }

    @Override // com.facebook.yoga.YogaConfig
    public void setShouldDiffLayoutWithoutLegacyStretchBehaviour(boolean shouldDiffLayoutWithoutLegacyStretchBehaviour) {
        YogaNative.jni_YGConfigSetShouldDiffLayoutWithoutLegacyStretchBehaviourJNI(this.mNativePointer, shouldDiffLayoutWithoutLegacyStretchBehaviour);
    }

    @Override // com.facebook.yoga.YogaConfig
    public void setLogger(YogaLogger logger) {
        this.mLogger = logger;
        YogaNative.jni_YGConfigSetLoggerJNI(this.mNativePointer, logger);
    }

    @Override // com.facebook.yoga.YogaConfig
    public YogaLogger getLogger() {
        return this.mLogger;
    }

    @Override // com.facebook.yoga.YogaConfig
    long getNativePointer() {
        return this.mNativePointer;
    }
}
