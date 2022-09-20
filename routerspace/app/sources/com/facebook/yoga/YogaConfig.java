package com.facebook.yoga;
/* loaded from: classes.dex */
public abstract class YogaConfig {
    public static int SPACING_TYPE = 1;

    public abstract YogaLogger getLogger();

    abstract long getNativePointer();

    public abstract void setExperimentalFeatureEnabled(YogaExperimentalFeature feature, boolean enabled);

    public abstract void setLogger(YogaLogger logger);

    public abstract void setPointScaleFactor(float pixelsInPoint);

    public abstract void setPrintTreeFlag(boolean enable);

    public abstract void setShouldDiffLayoutWithoutLegacyStretchBehaviour(boolean shouldDiffLayoutWithoutLegacyStretchBehaviour);

    public abstract void setUseLegacyStretchBehaviour(boolean useLegacyStretchBehaviour);

    public abstract void setUseWebDefaults(boolean useWebDefaults);
}
