package com.facebook.yoga;
/* loaded from: classes.dex */
public interface YogaProps {
    YogaAlign getAlignContent();

    YogaAlign getAlignItems();

    YogaAlign getAlignSelf();

    float getAspectRatio();

    float getBorder(YogaEdge edge);

    YogaValue getFlexBasis();

    YogaFlexDirection getFlexDirection();

    float getFlexGrow();

    float getFlexShrink();

    YogaValue getHeight();

    YogaJustify getJustifyContent();

    YogaValue getMargin(YogaEdge edge);

    YogaValue getMaxHeight();

    YogaValue getMaxWidth();

    YogaValue getMinHeight();

    YogaValue getMinWidth();

    YogaValue getPadding(YogaEdge edge);

    YogaValue getPosition(YogaEdge edge);

    YogaPositionType getPositionType();

    YogaDirection getStyleDirection();

    YogaValue getWidth();

    void setAlignContent(YogaAlign alignContent);

    void setAlignItems(YogaAlign alignItems);

    void setAlignSelf(YogaAlign alignSelf);

    void setAspectRatio(float aspectRatio);

    void setBaselineFunction(YogaBaselineFunction yogaBaselineFunction);

    void setBorder(YogaEdge edge, float value);

    void setDirection(YogaDirection direction);

    void setFlex(float flex);

    void setFlexBasis(float flexBasis);

    void setFlexBasisAuto();

    void setFlexBasisPercent(float percent);

    void setFlexDirection(YogaFlexDirection direction);

    void setFlexGrow(float flexGrow);

    void setFlexShrink(float flexShrink);

    void setHeight(float height);

    void setHeightAuto();

    void setHeightPercent(float percent);

    void setIsReferenceBaseline(boolean isReferenceBaseline);

    void setJustifyContent(YogaJustify justifyContent);

    void setMargin(YogaEdge edge, float margin);

    void setMarginAuto(YogaEdge edge);

    void setMarginPercent(YogaEdge edge, float percent);

    void setMaxHeight(float maxHeight);

    void setMaxHeightPercent(float percent);

    void setMaxWidth(float maxWidth);

    void setMaxWidthPercent(float percent);

    void setMeasureFunction(YogaMeasureFunction measureFunction);

    void setMinHeight(float minHeight);

    void setMinHeightPercent(float percent);

    void setMinWidth(float minWidth);

    void setMinWidthPercent(float percent);

    void setPadding(YogaEdge edge, float padding);

    void setPaddingPercent(YogaEdge edge, float percent);

    void setPosition(YogaEdge edge, float position);

    void setPositionPercent(YogaEdge edge, float percent);

    void setPositionType(YogaPositionType positionType);

    void setWidth(float width);

    void setWidthAuto();

    void setWidthPercent(float percent);

    void setWrap(YogaWrap wrap);
}
