package com.facebook.yoga;

import javax.annotation.Nullable;
/* loaded from: classes.dex */
public abstract class YogaNode implements YogaProps {

    /* loaded from: classes.dex */
    public interface Inputs {
        void freeze(final YogaNode node, @Nullable final YogaNode parent);
    }

    public abstract void addChildAt(YogaNode child, int i);

    public abstract void calculateLayout(float width, float height);

    public abstract YogaNode cloneWithChildren();

    public abstract YogaNode cloneWithoutChildren();

    public abstract void copyStyle(YogaNode srcNode);

    public abstract void dirty();

    @Override // com.facebook.yoga.YogaProps
    public abstract YogaAlign getAlignContent();

    @Override // com.facebook.yoga.YogaProps
    public abstract YogaAlign getAlignItems();

    @Override // com.facebook.yoga.YogaProps
    public abstract YogaAlign getAlignSelf();

    @Override // com.facebook.yoga.YogaProps
    public abstract float getAspectRatio();

    @Override // com.facebook.yoga.YogaProps
    public abstract float getBorder(YogaEdge edge);

    public abstract YogaNode getChildAt(int i);

    public abstract int getChildCount();

    @Nullable
    public abstract Object getData();

    public abstract YogaDisplay getDisplay();

    public abstract float getFlex();

    @Override // com.facebook.yoga.YogaProps
    public abstract YogaValue getFlexBasis();

    @Override // com.facebook.yoga.YogaProps
    public abstract YogaFlexDirection getFlexDirection();

    @Override // com.facebook.yoga.YogaProps
    public abstract float getFlexGrow();

    @Override // com.facebook.yoga.YogaProps
    public abstract float getFlexShrink();

    @Override // com.facebook.yoga.YogaProps
    public abstract YogaValue getHeight();

    @Override // com.facebook.yoga.YogaProps
    public abstract YogaJustify getJustifyContent();

    public abstract float getLayoutBorder(YogaEdge edge);

    public abstract YogaDirection getLayoutDirection();

    public abstract float getLayoutHeight();

    public abstract float getLayoutMargin(YogaEdge edge);

    public abstract float getLayoutPadding(YogaEdge edge);

    public abstract float getLayoutWidth();

    public abstract float getLayoutX();

    public abstract float getLayoutY();

    @Override // com.facebook.yoga.YogaProps
    public abstract YogaValue getMargin(YogaEdge edge);

    @Override // com.facebook.yoga.YogaProps
    public abstract YogaValue getMaxHeight();

    @Override // com.facebook.yoga.YogaProps
    public abstract YogaValue getMaxWidth();

    @Override // com.facebook.yoga.YogaProps
    public abstract YogaValue getMinHeight();

    @Override // com.facebook.yoga.YogaProps
    public abstract YogaValue getMinWidth();

    public abstract YogaOverflow getOverflow();

    @Nullable
    public abstract YogaNode getOwner();

    @Override // com.facebook.yoga.YogaProps
    public abstract YogaValue getPadding(YogaEdge edge);

    @Nullable
    @Deprecated
    public abstract YogaNode getParent();

    @Override // com.facebook.yoga.YogaProps
    public abstract YogaValue getPosition(YogaEdge edge);

    @Override // com.facebook.yoga.YogaProps
    public abstract YogaPositionType getPositionType();

    @Override // com.facebook.yoga.YogaProps
    public abstract YogaDirection getStyleDirection();

    @Override // com.facebook.yoga.YogaProps
    public abstract YogaValue getWidth();

    public abstract YogaWrap getWrap();

    public abstract boolean hasNewLayout();

    public abstract int indexOf(YogaNode child);

    public abstract boolean isBaselineDefined();

    public abstract boolean isDirty();

    public abstract boolean isMeasureDefined();

    public abstract boolean isReferenceBaseline();

    public abstract void markLayoutSeen();

    public abstract void print();

    public abstract YogaNode removeChildAt(int i);

    public abstract void reset();

    @Override // com.facebook.yoga.YogaProps
    public abstract void setAlignContent(YogaAlign alignContent);

    @Override // com.facebook.yoga.YogaProps
    public abstract void setAlignItems(YogaAlign alignItems);

    @Override // com.facebook.yoga.YogaProps
    public abstract void setAlignSelf(YogaAlign alignSelf);

    @Override // com.facebook.yoga.YogaProps
    public abstract void setAspectRatio(float aspectRatio);

    @Override // com.facebook.yoga.YogaProps
    public abstract void setBaselineFunction(YogaBaselineFunction baselineFunction);

    @Override // com.facebook.yoga.YogaProps
    public abstract void setBorder(YogaEdge edge, float border);

    public abstract void setData(Object data);

    @Override // com.facebook.yoga.YogaProps
    public abstract void setDirection(YogaDirection direction);

    public abstract void setDisplay(YogaDisplay display);

    @Override // com.facebook.yoga.YogaProps
    public abstract void setFlex(float flex);

    @Override // com.facebook.yoga.YogaProps
    public abstract void setFlexBasis(float flexBasis);

    @Override // com.facebook.yoga.YogaProps
    public abstract void setFlexBasisAuto();

    @Override // com.facebook.yoga.YogaProps
    public abstract void setFlexBasisPercent(float percent);

    @Override // com.facebook.yoga.YogaProps
    public abstract void setFlexDirection(YogaFlexDirection flexDirection);

    @Override // com.facebook.yoga.YogaProps
    public abstract void setFlexGrow(float flexGrow);

    @Override // com.facebook.yoga.YogaProps
    public abstract void setFlexShrink(float flexShrink);

    @Override // com.facebook.yoga.YogaProps
    public abstract void setHeight(float height);

    @Override // com.facebook.yoga.YogaProps
    public abstract void setHeightAuto();

    @Override // com.facebook.yoga.YogaProps
    public abstract void setHeightPercent(float percent);

    @Override // com.facebook.yoga.YogaProps
    public abstract void setIsReferenceBaseline(boolean isReferenceBaseline);

    @Override // com.facebook.yoga.YogaProps
    public abstract void setJustifyContent(YogaJustify justifyContent);

    @Override // com.facebook.yoga.YogaProps
    public abstract void setMargin(YogaEdge edge, float margin);

    @Override // com.facebook.yoga.YogaProps
    public abstract void setMarginAuto(YogaEdge edge);

    @Override // com.facebook.yoga.YogaProps
    public abstract void setMarginPercent(YogaEdge edge, float percent);

    @Override // com.facebook.yoga.YogaProps
    public abstract void setMaxHeight(float maxheight);

    @Override // com.facebook.yoga.YogaProps
    public abstract void setMaxHeightPercent(float percent);

    @Override // com.facebook.yoga.YogaProps
    public abstract void setMaxWidth(float maxWidth);

    @Override // com.facebook.yoga.YogaProps
    public abstract void setMaxWidthPercent(float percent);

    @Override // com.facebook.yoga.YogaProps
    public abstract void setMeasureFunction(YogaMeasureFunction measureFunction);

    @Override // com.facebook.yoga.YogaProps
    public abstract void setMinHeight(float minHeight);

    @Override // com.facebook.yoga.YogaProps
    public abstract void setMinHeightPercent(float percent);

    @Override // com.facebook.yoga.YogaProps
    public abstract void setMinWidth(float minWidth);

    @Override // com.facebook.yoga.YogaProps
    public abstract void setMinWidthPercent(float percent);

    public abstract void setOverflow(YogaOverflow overflow);

    @Override // com.facebook.yoga.YogaProps
    public abstract void setPadding(YogaEdge edge, float padding);

    @Override // com.facebook.yoga.YogaProps
    public abstract void setPaddingPercent(YogaEdge edge, float percent);

    @Override // com.facebook.yoga.YogaProps
    public abstract void setPosition(YogaEdge edge, float position);

    @Override // com.facebook.yoga.YogaProps
    public abstract void setPositionPercent(YogaEdge edge, float percent);

    @Override // com.facebook.yoga.YogaProps
    public abstract void setPositionType(YogaPositionType positionType);

    @Override // com.facebook.yoga.YogaProps
    public abstract void setWidth(float width);

    @Override // com.facebook.yoga.YogaProps
    public abstract void setWidthAuto();

    @Override // com.facebook.yoga.YogaProps
    public abstract void setWidthPercent(float percent);

    @Override // com.facebook.yoga.YogaProps
    public abstract void setWrap(YogaWrap flexWrap);
}
