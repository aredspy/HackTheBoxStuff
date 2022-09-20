package com.facebook.react.uimanager;

import com.facebook.react.uimanager.ReactShadowNode;
import com.facebook.yoga.YogaAlign;
import com.facebook.yoga.YogaBaselineFunction;
import com.facebook.yoga.YogaDirection;
import com.facebook.yoga.YogaDisplay;
import com.facebook.yoga.YogaFlexDirection;
import com.facebook.yoga.YogaJustify;
import com.facebook.yoga.YogaMeasureFunction;
import com.facebook.yoga.YogaOverflow;
import com.facebook.yoga.YogaPositionType;
import com.facebook.yoga.YogaValue;
import com.facebook.yoga.YogaWrap;
/* loaded from: classes.dex */
public interface ReactShadowNode<T extends ReactShadowNode> {
    void addChildAt(T child, int i);

    void addNativeChildAt(T child, int nativeIndex);

    void calculateLayout();

    void calculateLayout(float width, float height);

    Iterable<? extends ReactShadowNode> calculateLayoutOnChildren();

    void dirty();

    boolean dispatchUpdates(float absoluteX, float absoluteY, UIViewOperationQueue uiViewOperationQueue, NativeViewHierarchyOptimizer nativeViewHierarchyOptimizer);

    void dispose();

    T getChildAt(int i);

    int getChildCount();

    float getFlex();

    Integer getHeightMeasureSpec();

    String getHierarchyInfo();

    YogaDirection getLayoutDirection();

    float getLayoutHeight();

    T getLayoutParent();

    float getLayoutWidth();

    float getLayoutX();

    float getLayoutY();

    int getNativeChildCount();

    NativeKind getNativeKind();

    int getNativeOffsetForChild(T child);

    T getNativeParent();

    float getPadding(int spacingType);

    T getParent();

    int getReactTag();

    int getRootTag();

    int getScreenHeight();

    int getScreenWidth();

    int getScreenX();

    int getScreenY();

    YogaValue getStyleHeight();

    YogaValue getStylePadding(int spacingType);

    YogaValue getStyleWidth();

    ThemedReactContext getThemedContext();

    int getTotalNativeChildren();

    String getViewClass();

    Integer getWidthMeasureSpec();

    boolean hasNewLayout();

    boolean hasUnseenUpdates();

    boolean hasUpdates();

    boolean hoistNativeChildren();

    int indexOf(T child);

    int indexOfNativeChild(T nativeChild);

    boolean isDescendantOf(T ancestorNode);

    boolean isDirty();

    boolean isLayoutOnly();

    boolean isMeasureDefined();

    boolean isVirtual();

    boolean isVirtualAnchor();

    boolean isYogaLeafNode();

    void markLayoutSeen();

    void markUpdateSeen();

    void markUpdated();

    void onAfterUpdateTransaction();

    void onBeforeLayout(NativeViewHierarchyOptimizer nativeViewHierarchyOptimizer);

    void onCollectExtraUpdates(UIViewOperationQueue uiViewOperationQueue);

    void removeAllNativeChildren();

    void removeAndDisposeAllChildren();

    T removeChildAt(int i);

    T removeNativeChildAt(int i);

    void setAlignContent(YogaAlign alignContent);

    void setAlignItems(YogaAlign alignItems);

    void setAlignSelf(YogaAlign alignSelf);

    void setBaselineFunction(YogaBaselineFunction baselineFunction);

    void setBorder(int spacingType, float borderWidth);

    void setDefaultPadding(int spacingType, float padding);

    void setDisplay(YogaDisplay display);

    void setFlex(float flex);

    void setFlexBasis(float flexBasis);

    void setFlexBasisAuto();

    void setFlexBasisPercent(float percent);

    void setFlexDirection(YogaFlexDirection flexDirection);

    void setFlexGrow(float flexGrow);

    void setFlexShrink(float flexShrink);

    void setFlexWrap(YogaWrap wrap);

    void setIsLayoutOnly(boolean isLayoutOnly);

    void setJustifyContent(YogaJustify justifyContent);

    void setLayoutDirection(YogaDirection direction);

    void setLayoutParent(T layoutParent);

    void setLocalData(Object data);

    void setMargin(int spacingType, float margin);

    void setMarginAuto(int spacingType);

    void setMarginPercent(int spacingType, float percent);

    void setMeasureFunction(YogaMeasureFunction measureFunction);

    void setMeasureSpecs(int widthMeasureSpec, int heightMeasureSpec);

    void setOverflow(YogaOverflow overflow);

    void setPadding(int spacingType, float padding);

    void setPaddingPercent(int spacingType, float percent);

    void setPosition(int spacingType, float position);

    void setPositionPercent(int spacingType, float percent);

    void setPositionType(YogaPositionType positionType);

    void setReactTag(int reactTag);

    void setRootTag(int rootTag);

    void setShouldNotifyOnLayout(boolean shouldNotifyOnLayout);

    void setStyleAspectRatio(float aspectRatio);

    void setStyleHeight(float heightPx);

    void setStyleHeightAuto();

    void setStyleHeightPercent(float percent);

    void setStyleMaxHeight(float widthPx);

    void setStyleMaxHeightPercent(float percent);

    void setStyleMaxWidth(float widthPx);

    void setStyleMaxWidthPercent(float percent);

    void setStyleMinHeight(float widthPx);

    void setStyleMinHeightPercent(float percent);

    void setStyleMinWidth(float widthPx);

    void setStyleMinWidthPercent(float percent);

    void setStyleWidth(float widthPx);

    void setStyleWidthAuto();

    void setStyleWidthPercent(float percent);

    void setThemedContext(ThemedReactContext themedContext);

    void setViewClassName(String viewClassName);

    boolean shouldNotifyOnLayout();

    void updateProperties(ReactStylesDiffMap props);
}
