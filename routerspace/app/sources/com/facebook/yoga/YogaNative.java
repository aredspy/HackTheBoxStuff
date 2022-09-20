package com.facebook.yoga;

import com.facebook.soloader.SoLoader;
/* loaded from: classes.dex */
public class YogaNative {
    public static native void jni_YGConfigFreeJNI(long nativePointer);

    public static native long jni_YGConfigNewJNI();

    public static native void jni_YGConfigSetExperimentalFeatureEnabledJNI(long nativePointer, int feature, boolean enabled);

    public static native void jni_YGConfigSetLoggerJNI(long nativePointer, YogaLogger logger);

    public static native void jni_YGConfigSetPointScaleFactorJNI(long nativePointer, float pixelsInPoint);

    public static native void jni_YGConfigSetPrintTreeFlagJNI(long nativePointer, boolean enable);

    public static native void jni_YGConfigSetShouldDiffLayoutWithoutLegacyStretchBehaviourJNI(long nativePointer, boolean shouldDiffLayoutWithoutLegacyStretchBehaviour);

    public static native void jni_YGConfigSetUseLegacyStretchBehaviourJNI(long nativePointer, boolean useLegacyStretchBehaviour);

    public static native void jni_YGConfigSetUseWebDefaultsJNI(long nativePointer, boolean useWebDefaults);

    public static native void jni_YGNodeCalculateLayoutJNI(long nativePointer, float width, float height, long[] nativePointers, YogaNodeJNIBase[] nodes);

    public static native void jni_YGNodeClearChildrenJNI(long nativePointer);

    public static native long jni_YGNodeCloneJNI(long nativePointer);

    public static native void jni_YGNodeCopyStyleJNI(long dstNativePointer, long srcNativePointer);

    public static native void jni_YGNodeFreeJNI(long nativePointer);

    public static native void jni_YGNodeInsertChildJNI(long nativePointer, long childPointer, int index);

    public static native boolean jni_YGNodeIsDirtyJNI(long nativePointer);

    public static native boolean jni_YGNodeIsReferenceBaselineJNI(long nativePointer);

    public static native void jni_YGNodeMarkDirtyAndPropogateToDescendantsJNI(long nativePointer);

    public static native void jni_YGNodeMarkDirtyJNI(long nativePointer);

    public static native long jni_YGNodeNewJNI();

    public static native long jni_YGNodeNewWithConfigJNI(long configPointer);

    public static native void jni_YGNodePrintJNI(long nativePointer);

    public static native void jni_YGNodeRemoveChildJNI(long nativePointer, long childPointer);

    public static native void jni_YGNodeResetJNI(long nativePointer);

    public static native void jni_YGNodeSetHasBaselineFuncJNI(long nativePointer, boolean hasMeasureFunc);

    public static native void jni_YGNodeSetHasMeasureFuncJNI(long nativePointer, boolean hasMeasureFunc);

    public static native void jni_YGNodeSetIsReferenceBaselineJNI(long nativePointer, boolean isReferenceBaseline);

    static native void jni_YGNodeSetStyleInputsJNI(long nativePointer, float[] styleInputsArray, int size);

    public static native int jni_YGNodeStyleGetAlignContentJNI(long nativePointer);

    public static native int jni_YGNodeStyleGetAlignItemsJNI(long nativePointer);

    public static native int jni_YGNodeStyleGetAlignSelfJNI(long nativePointer);

    public static native float jni_YGNodeStyleGetAspectRatioJNI(long nativePointer);

    public static native float jni_YGNodeStyleGetBorderJNI(long nativePointer, int edge);

    public static native int jni_YGNodeStyleGetDirectionJNI(long nativePointer);

    public static native int jni_YGNodeStyleGetDisplayJNI(long nativePointer);

    public static native long jni_YGNodeStyleGetFlexBasisJNI(long nativePointer);

    public static native int jni_YGNodeStyleGetFlexDirectionJNI(long nativePointer);

    public static native float jni_YGNodeStyleGetFlexGrowJNI(long nativePointer);

    public static native float jni_YGNodeStyleGetFlexJNI(long nativePointer);

    public static native float jni_YGNodeStyleGetFlexShrinkJNI(long nativePointer);

    public static native int jni_YGNodeStyleGetFlexWrapJNI(long nativePointer);

    public static native long jni_YGNodeStyleGetHeightJNI(long nativePointer);

    public static native int jni_YGNodeStyleGetJustifyContentJNI(long nativePointer);

    public static native long jni_YGNodeStyleGetMarginJNI(long nativePointer, int edge);

    public static native long jni_YGNodeStyleGetMaxHeightJNI(long nativePointer);

    public static native long jni_YGNodeStyleGetMaxWidthJNI(long nativePointer);

    public static native long jni_YGNodeStyleGetMinHeightJNI(long nativePointer);

    public static native long jni_YGNodeStyleGetMinWidthJNI(long nativePointer);

    public static native int jni_YGNodeStyleGetOverflowJNI(long nativePointer);

    public static native long jni_YGNodeStyleGetPaddingJNI(long nativePointer, int edge);

    public static native long jni_YGNodeStyleGetPositionJNI(long nativePointer, int edge);

    public static native int jni_YGNodeStyleGetPositionTypeJNI(long nativePointer);

    public static native long jni_YGNodeStyleGetWidthJNI(long nativePointer);

    public static native void jni_YGNodeStyleSetAlignContentJNI(long nativePointer, int alignContent);

    public static native void jni_YGNodeStyleSetAlignItemsJNI(long nativePointer, int alignItems);

    public static native void jni_YGNodeStyleSetAlignSelfJNI(long nativePointer, int alignSelf);

    public static native void jni_YGNodeStyleSetAspectRatioJNI(long nativePointer, float aspectRatio);

    public static native void jni_YGNodeStyleSetBorderJNI(long nativePointer, int edge, float border);

    public static native void jni_YGNodeStyleSetDirectionJNI(long nativePointer, int direction);

    public static native void jni_YGNodeStyleSetDisplayJNI(long nativePointer, int display);

    public static native void jni_YGNodeStyleSetFlexBasisAutoJNI(long nativePointer);

    public static native void jni_YGNodeStyleSetFlexBasisJNI(long nativePointer, float flexBasis);

    public static native void jni_YGNodeStyleSetFlexBasisPercentJNI(long nativePointer, float percent);

    public static native void jni_YGNodeStyleSetFlexDirectionJNI(long nativePointer, int flexDirection);

    public static native void jni_YGNodeStyleSetFlexGrowJNI(long nativePointer, float flexGrow);

    public static native void jni_YGNodeStyleSetFlexJNI(long nativePointer, float flex);

    public static native void jni_YGNodeStyleSetFlexShrinkJNI(long nativePointer, float flexShrink);

    public static native void jni_YGNodeStyleSetFlexWrapJNI(long nativePointer, int wrapType);

    public static native void jni_YGNodeStyleSetHeightAutoJNI(long nativePointer);

    public static native void jni_YGNodeStyleSetHeightJNI(long nativePointer, float height);

    public static native void jni_YGNodeStyleSetHeightPercentJNI(long nativePointer, float percent);

    public static native void jni_YGNodeStyleSetJustifyContentJNI(long nativePointer, int justifyContent);

    public static native void jni_YGNodeStyleSetMarginAutoJNI(long nativePointer, int edge);

    public static native void jni_YGNodeStyleSetMarginJNI(long nativePointer, int edge, float margin);

    public static native void jni_YGNodeStyleSetMarginPercentJNI(long nativePointer, int edge, float percent);

    public static native void jni_YGNodeStyleSetMaxHeightJNI(long nativePointer, float maxheight);

    public static native void jni_YGNodeStyleSetMaxHeightPercentJNI(long nativePointer, float percent);

    public static native void jni_YGNodeStyleSetMaxWidthJNI(long nativePointer, float maxWidth);

    public static native void jni_YGNodeStyleSetMaxWidthPercentJNI(long nativePointer, float percent);

    public static native void jni_YGNodeStyleSetMinHeightJNI(long nativePointer, float minHeight);

    public static native void jni_YGNodeStyleSetMinHeightPercentJNI(long nativePointer, float percent);

    public static native void jni_YGNodeStyleSetMinWidthJNI(long nativePointer, float minWidth);

    public static native void jni_YGNodeStyleSetMinWidthPercentJNI(long nativePointer, float percent);

    public static native void jni_YGNodeStyleSetOverflowJNI(long nativePointer, int overflow);

    public static native void jni_YGNodeStyleSetPaddingJNI(long nativePointer, int edge, float padding);

    public static native void jni_YGNodeStyleSetPaddingPercentJNI(long nativePointer, int edge, float percent);

    public static native void jni_YGNodeStyleSetPositionJNI(long nativePointer, int edge, float position);

    public static native void jni_YGNodeStyleSetPositionPercentJNI(long nativePointer, int edge, float percent);

    public static native void jni_YGNodeStyleSetPositionTypeJNI(long nativePointer, int positionType);

    public static native void jni_YGNodeStyleSetWidthAutoJNI(long nativePointer);

    public static native void jni_YGNodeStyleSetWidthJNI(long nativePointer, float width);

    public static native void jni_YGNodeStyleSetWidthPercentJNI(long nativePointer, float percent);

    public static native void jni_YGNodeSwapChildJNI(long nativePointer, long childPointer, int index);

    static {
        SoLoader.loadLibrary("yoga");
    }
}
