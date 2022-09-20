package com.facebook.react.uimanager;

import com.facebook.react.bridge.Dynamic;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.devsupport.StackTraceHelper;
import com.facebook.react.modules.i18nmanager.I18nUtil;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.annotations.ReactPropGroup;
import com.facebook.yoga.YogaAlign;
import com.facebook.yoga.YogaDisplay;
import com.facebook.yoga.YogaFlexDirection;
import com.facebook.yoga.YogaJustify;
import com.facebook.yoga.YogaOverflow;
import com.facebook.yoga.YogaPositionType;
import com.facebook.yoga.YogaUnit;
import com.facebook.yoga.YogaWrap;
/* loaded from: classes.dex */
public class LayoutShadowNode extends ReactShadowNodeImpl {
    boolean mCollapsable;
    private final MutableYogaValue mTempYogaValue = new MutableYogaValue((AnonymousClass1) null);

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class MutableYogaValue {
        YogaUnit unit;
        float value;

        /* synthetic */ MutableYogaValue(AnonymousClass1 anonymousClass1) {
            this();
        }

        private MutableYogaValue() {
        }

        private MutableYogaValue(MutableYogaValue mutableYogaValue) {
            this.value = mutableYogaValue.value;
            this.unit = mutableYogaValue.unit;
        }

        void setFromDynamic(Dynamic dynamic) {
            if (dynamic.isNull()) {
                this.unit = YogaUnit.UNDEFINED;
                this.value = Float.NaN;
            } else if (dynamic.getType() == ReadableType.String) {
                String asString = dynamic.asString();
                if (asString.equals("auto")) {
                    this.unit = YogaUnit.AUTO;
                    this.value = Float.NaN;
                } else if (asString.endsWith("%")) {
                    this.unit = YogaUnit.PERCENT;
                    this.value = Float.parseFloat(asString.substring(0, asString.length() - 1));
                } else {
                    throw new IllegalArgumentException("Unknown value: " + asString);
                }
            } else {
                this.unit = YogaUnit.POINT;
                this.value = PixelUtil.toPixelFromDIP(dynamic.asDouble());
            }
        }
    }

    @ReactProp(name = "width")
    public void setWidth(Dynamic width) {
        if (isVirtual()) {
            return;
        }
        this.mTempYogaValue.setFromDynamic(width);
        int i = AnonymousClass1.$SwitchMap$com$facebook$yoga$YogaUnit[this.mTempYogaValue.unit.ordinal()];
        if (i == 1 || i == 2) {
            setStyleWidth(this.mTempYogaValue.value);
        } else if (i == 3) {
            setStyleWidthAuto();
        } else if (i == 4) {
            setStyleWidthPercent(this.mTempYogaValue.value);
        }
        width.recycle();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.facebook.react.uimanager.LayoutShadowNode$1 */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$facebook$yoga$YogaUnit;

        static {
            int[] iArr = new int[YogaUnit.values().length];
            $SwitchMap$com$facebook$yoga$YogaUnit = iArr;
            try {
                iArr[YogaUnit.POINT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$facebook$yoga$YogaUnit[YogaUnit.UNDEFINED.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$facebook$yoga$YogaUnit[YogaUnit.AUTO.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$facebook$yoga$YogaUnit[YogaUnit.PERCENT.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    @ReactProp(name = ViewProps.MIN_WIDTH)
    public void setMinWidth(Dynamic minWidth) {
        if (isVirtual()) {
            return;
        }
        this.mTempYogaValue.setFromDynamic(minWidth);
        int i = AnonymousClass1.$SwitchMap$com$facebook$yoga$YogaUnit[this.mTempYogaValue.unit.ordinal()];
        if (i == 1 || i == 2) {
            setStyleMinWidth(this.mTempYogaValue.value);
        } else if (i == 4) {
            setStyleMinWidthPercent(this.mTempYogaValue.value);
        }
        minWidth.recycle();
    }

    @ReactProp(name = ViewProps.COLLAPSABLE)
    public void setCollapsable(boolean collapsable) {
        this.mCollapsable = collapsable;
    }

    @ReactProp(name = ViewProps.MAX_WIDTH)
    public void setMaxWidth(Dynamic maxWidth) {
        if (isVirtual()) {
            return;
        }
        this.mTempYogaValue.setFromDynamic(maxWidth);
        int i = AnonymousClass1.$SwitchMap$com$facebook$yoga$YogaUnit[this.mTempYogaValue.unit.ordinal()];
        if (i == 1 || i == 2) {
            setStyleMaxWidth(this.mTempYogaValue.value);
        } else if (i == 4) {
            setStyleMaxWidthPercent(this.mTempYogaValue.value);
        }
        maxWidth.recycle();
    }

    @ReactProp(name = "height")
    public void setHeight(Dynamic height) {
        if (isVirtual()) {
            return;
        }
        this.mTempYogaValue.setFromDynamic(height);
        int i = AnonymousClass1.$SwitchMap$com$facebook$yoga$YogaUnit[this.mTempYogaValue.unit.ordinal()];
        if (i == 1 || i == 2) {
            setStyleHeight(this.mTempYogaValue.value);
        } else if (i == 3) {
            setStyleHeightAuto();
        } else if (i == 4) {
            setStyleHeightPercent(this.mTempYogaValue.value);
        }
        height.recycle();
    }

    @ReactProp(name = ViewProps.MIN_HEIGHT)
    public void setMinHeight(Dynamic minHeight) {
        if (isVirtual()) {
            return;
        }
        this.mTempYogaValue.setFromDynamic(minHeight);
        int i = AnonymousClass1.$SwitchMap$com$facebook$yoga$YogaUnit[this.mTempYogaValue.unit.ordinal()];
        if (i == 1 || i == 2) {
            setStyleMinHeight(this.mTempYogaValue.value);
        } else if (i == 4) {
            setStyleMinHeightPercent(this.mTempYogaValue.value);
        }
        minHeight.recycle();
    }

    @ReactProp(name = ViewProps.MAX_HEIGHT)
    public void setMaxHeight(Dynamic maxHeight) {
        if (isVirtual()) {
            return;
        }
        this.mTempYogaValue.setFromDynamic(maxHeight);
        int i = AnonymousClass1.$SwitchMap$com$facebook$yoga$YogaUnit[this.mTempYogaValue.unit.ordinal()];
        if (i == 1 || i == 2) {
            setStyleMaxHeight(this.mTempYogaValue.value);
        } else if (i == 4) {
            setStyleMaxHeightPercent(this.mTempYogaValue.value);
        }
        maxHeight.recycle();
    }

    @Override // com.facebook.react.uimanager.ReactShadowNodeImpl, com.facebook.react.uimanager.ReactShadowNode
    @ReactProp(defaultFloat = 0.0f, name = ViewProps.FLEX)
    public void setFlex(float flex) {
        if (isVirtual()) {
            return;
        }
        super.setFlex(flex);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNodeImpl, com.facebook.react.uimanager.ReactShadowNode
    @ReactProp(defaultFloat = 0.0f, name = ViewProps.FLEX_GROW)
    public void setFlexGrow(float flexGrow) {
        if (isVirtual()) {
            return;
        }
        super.setFlexGrow(flexGrow);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNodeImpl, com.facebook.react.uimanager.ReactShadowNode
    @ReactProp(defaultFloat = 0.0f, name = ViewProps.FLEX_SHRINK)
    public void setFlexShrink(float flexShrink) {
        if (isVirtual()) {
            return;
        }
        super.setFlexShrink(flexShrink);
    }

    @ReactProp(name = ViewProps.FLEX_BASIS)
    public void setFlexBasis(Dynamic flexBasis) {
        if (isVirtual()) {
            return;
        }
        this.mTempYogaValue.setFromDynamic(flexBasis);
        int i = AnonymousClass1.$SwitchMap$com$facebook$yoga$YogaUnit[this.mTempYogaValue.unit.ordinal()];
        if (i == 1 || i == 2) {
            setFlexBasis(this.mTempYogaValue.value);
        } else if (i == 3) {
            setFlexBasisAuto();
        } else if (i == 4) {
            setFlexBasisPercent(this.mTempYogaValue.value);
        }
        flexBasis.recycle();
    }

    @ReactProp(defaultFloat = Float.NaN, name = ViewProps.ASPECT_RATIO)
    public void setAspectRatio(float aspectRatio) {
        setStyleAspectRatio(aspectRatio);
    }

    @ReactProp(name = ViewProps.FLEX_DIRECTION)
    public void setFlexDirection(String flexDirection) {
        if (isVirtual()) {
            return;
        }
        if (flexDirection == null) {
            setFlexDirection(YogaFlexDirection.COLUMN);
            return;
        }
        flexDirection.hashCode();
        char c = 65535;
        switch (flexDirection.hashCode()) {
            case -1448970769:
                if (flexDirection.equals("row-reverse")) {
                    c = 0;
                    break;
                }
                break;
            case -1354837162:
                if (flexDirection.equals(StackTraceHelper.COLUMN_KEY)) {
                    c = 1;
                    break;
                }
                break;
            case 113114:
                if (flexDirection.equals("row")) {
                    c = 2;
                    break;
                }
                break;
            case 1272730475:
                if (flexDirection.equals("column-reverse")) {
                    c = 3;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                setFlexDirection(YogaFlexDirection.ROW_REVERSE);
                return;
            case 1:
                setFlexDirection(YogaFlexDirection.COLUMN);
                return;
            case 2:
                setFlexDirection(YogaFlexDirection.ROW);
                return;
            case 3:
                setFlexDirection(YogaFlexDirection.COLUMN_REVERSE);
                return;
            default:
                throw new JSApplicationIllegalArgumentException("invalid value for flexDirection: " + flexDirection);
        }
    }

    @ReactProp(name = ViewProps.FLEX_WRAP)
    public void setFlexWrap(String flexWrap) {
        if (isVirtual()) {
            return;
        }
        if (flexWrap == null) {
            setFlexWrap(YogaWrap.NO_WRAP);
            return;
        }
        flexWrap.hashCode();
        char c = 65535;
        switch (flexWrap.hashCode()) {
            case -1039592053:
                if (flexWrap.equals("nowrap")) {
                    c = 0;
                    break;
                }
                break;
            case -749527969:
                if (flexWrap.equals("wrap-reverse")) {
                    c = 1;
                    break;
                }
                break;
            case 3657802:
                if (flexWrap.equals("wrap")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                setFlexWrap(YogaWrap.NO_WRAP);
                return;
            case 1:
                setFlexWrap(YogaWrap.WRAP_REVERSE);
                return;
            case 2:
                setFlexWrap(YogaWrap.WRAP);
                return;
            default:
                throw new JSApplicationIllegalArgumentException("invalid value for flexWrap: " + flexWrap);
        }
    }

    @ReactProp(name = ViewProps.ALIGN_SELF)
    public void setAlignSelf(String alignSelf) {
        if (isVirtual()) {
            return;
        }
        if (alignSelf == null) {
            setAlignSelf(YogaAlign.AUTO);
            return;
        }
        alignSelf.hashCode();
        char c = 65535;
        switch (alignSelf.hashCode()) {
            case -1881872635:
                if (alignSelf.equals("stretch")) {
                    c = 0;
                    break;
                }
                break;
            case -1720785339:
                if (alignSelf.equals("baseline")) {
                    c = 1;
                    break;
                }
                break;
            case -1364013995:
                if (alignSelf.equals("center")) {
                    c = 2;
                    break;
                }
                break;
            case -46581362:
                if (alignSelf.equals("flex-start")) {
                    c = 3;
                    break;
                }
                break;
            case 3005871:
                if (alignSelf.equals("auto")) {
                    c = 4;
                    break;
                }
                break;
            case 441309761:
                if (alignSelf.equals("space-between")) {
                    c = 5;
                    break;
                }
                break;
            case 1742952711:
                if (alignSelf.equals("flex-end")) {
                    c = 6;
                    break;
                }
                break;
            case 1937124468:
                if (alignSelf.equals("space-around")) {
                    c = 7;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                setAlignSelf(YogaAlign.STRETCH);
                return;
            case 1:
                setAlignSelf(YogaAlign.BASELINE);
                return;
            case 2:
                setAlignSelf(YogaAlign.CENTER);
                return;
            case 3:
                setAlignSelf(YogaAlign.FLEX_START);
                return;
            case 4:
                setAlignSelf(YogaAlign.AUTO);
                return;
            case 5:
                setAlignSelf(YogaAlign.SPACE_BETWEEN);
                return;
            case 6:
                setAlignSelf(YogaAlign.FLEX_END);
                return;
            case 7:
                setAlignSelf(YogaAlign.SPACE_AROUND);
                return;
            default:
                throw new JSApplicationIllegalArgumentException("invalid value for alignSelf: " + alignSelf);
        }
    }

    @ReactProp(name = ViewProps.ALIGN_ITEMS)
    public void setAlignItems(String alignItems) {
        if (isVirtual()) {
            return;
        }
        if (alignItems == null) {
            setAlignItems(YogaAlign.STRETCH);
            return;
        }
        alignItems.hashCode();
        char c = 65535;
        switch (alignItems.hashCode()) {
            case -1881872635:
                if (alignItems.equals("stretch")) {
                    c = 0;
                    break;
                }
                break;
            case -1720785339:
                if (alignItems.equals("baseline")) {
                    c = 1;
                    break;
                }
                break;
            case -1364013995:
                if (alignItems.equals("center")) {
                    c = 2;
                    break;
                }
                break;
            case -46581362:
                if (alignItems.equals("flex-start")) {
                    c = 3;
                    break;
                }
                break;
            case 3005871:
                if (alignItems.equals("auto")) {
                    c = 4;
                    break;
                }
                break;
            case 441309761:
                if (alignItems.equals("space-between")) {
                    c = 5;
                    break;
                }
                break;
            case 1742952711:
                if (alignItems.equals("flex-end")) {
                    c = 6;
                    break;
                }
                break;
            case 1937124468:
                if (alignItems.equals("space-around")) {
                    c = 7;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                setAlignItems(YogaAlign.STRETCH);
                return;
            case 1:
                setAlignItems(YogaAlign.BASELINE);
                return;
            case 2:
                setAlignItems(YogaAlign.CENTER);
                return;
            case 3:
                setAlignItems(YogaAlign.FLEX_START);
                return;
            case 4:
                setAlignItems(YogaAlign.AUTO);
                return;
            case 5:
                setAlignItems(YogaAlign.SPACE_BETWEEN);
                return;
            case 6:
                setAlignItems(YogaAlign.FLEX_END);
                return;
            case 7:
                setAlignItems(YogaAlign.SPACE_AROUND);
                return;
            default:
                throw new JSApplicationIllegalArgumentException("invalid value for alignItems: " + alignItems);
        }
    }

    @ReactProp(name = ViewProps.ALIGN_CONTENT)
    public void setAlignContent(String alignContent) {
        if (isVirtual()) {
            return;
        }
        if (alignContent == null) {
            setAlignContent(YogaAlign.FLEX_START);
            return;
        }
        alignContent.hashCode();
        char c = 65535;
        switch (alignContent.hashCode()) {
            case -1881872635:
                if (alignContent.equals("stretch")) {
                    c = 0;
                    break;
                }
                break;
            case -1720785339:
                if (alignContent.equals("baseline")) {
                    c = 1;
                    break;
                }
                break;
            case -1364013995:
                if (alignContent.equals("center")) {
                    c = 2;
                    break;
                }
                break;
            case -46581362:
                if (alignContent.equals("flex-start")) {
                    c = 3;
                    break;
                }
                break;
            case 3005871:
                if (alignContent.equals("auto")) {
                    c = 4;
                    break;
                }
                break;
            case 441309761:
                if (alignContent.equals("space-between")) {
                    c = 5;
                    break;
                }
                break;
            case 1742952711:
                if (alignContent.equals("flex-end")) {
                    c = 6;
                    break;
                }
                break;
            case 1937124468:
                if (alignContent.equals("space-around")) {
                    c = 7;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                setAlignContent(YogaAlign.STRETCH);
                return;
            case 1:
                setAlignContent(YogaAlign.BASELINE);
                return;
            case 2:
                setAlignContent(YogaAlign.CENTER);
                return;
            case 3:
                setAlignContent(YogaAlign.FLEX_START);
                return;
            case 4:
                setAlignContent(YogaAlign.AUTO);
                return;
            case 5:
                setAlignContent(YogaAlign.SPACE_BETWEEN);
                return;
            case 6:
                setAlignContent(YogaAlign.FLEX_END);
                return;
            case 7:
                setAlignContent(YogaAlign.SPACE_AROUND);
                return;
            default:
                throw new JSApplicationIllegalArgumentException("invalid value for alignContent: " + alignContent);
        }
    }

    @ReactProp(name = ViewProps.JUSTIFY_CONTENT)
    public void setJustifyContent(String justifyContent) {
        if (isVirtual()) {
            return;
        }
        if (justifyContent == null) {
            setJustifyContent(YogaJustify.FLEX_START);
            return;
        }
        justifyContent.hashCode();
        char c = 65535;
        switch (justifyContent.hashCode()) {
            case -1364013995:
                if (justifyContent.equals("center")) {
                    c = 0;
                    break;
                }
                break;
            case -46581362:
                if (justifyContent.equals("flex-start")) {
                    c = 1;
                    break;
                }
                break;
            case 441309761:
                if (justifyContent.equals("space-between")) {
                    c = 2;
                    break;
                }
                break;
            case 1742952711:
                if (justifyContent.equals("flex-end")) {
                    c = 3;
                    break;
                }
                break;
            case 1937124468:
                if (justifyContent.equals("space-around")) {
                    c = 4;
                    break;
                }
                break;
            case 2055030478:
                if (justifyContent.equals("space-evenly")) {
                    c = 5;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                setJustifyContent(YogaJustify.CENTER);
                return;
            case 1:
                setJustifyContent(YogaJustify.FLEX_START);
                return;
            case 2:
                setJustifyContent(YogaJustify.SPACE_BETWEEN);
                return;
            case 3:
                setJustifyContent(YogaJustify.FLEX_END);
                return;
            case 4:
                setJustifyContent(YogaJustify.SPACE_AROUND);
                return;
            case 5:
                setJustifyContent(YogaJustify.SPACE_EVENLY);
                return;
            default:
                throw new JSApplicationIllegalArgumentException("invalid value for justifyContent: " + justifyContent);
        }
    }

    @ReactProp(name = ViewProps.OVERFLOW)
    public void setOverflow(String overflow) {
        if (isVirtual()) {
            return;
        }
        if (overflow == null) {
            setOverflow(YogaOverflow.VISIBLE);
            return;
        }
        overflow.hashCode();
        char c = 65535;
        switch (overflow.hashCode()) {
            case -1217487446:
                if (overflow.equals(ViewProps.HIDDEN)) {
                    c = 0;
                    break;
                }
                break;
            case -907680051:
                if (overflow.equals(ViewProps.SCROLL)) {
                    c = 1;
                    break;
                }
                break;
            case 466743410:
                if (overflow.equals(ViewProps.VISIBLE)) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                setOverflow(YogaOverflow.HIDDEN);
                return;
            case 1:
                setOverflow(YogaOverflow.SCROLL);
                return;
            case 2:
                setOverflow(YogaOverflow.VISIBLE);
                return;
            default:
                throw new JSApplicationIllegalArgumentException("invalid value for overflow: " + overflow);
        }
    }

    @ReactProp(name = ViewProps.DISPLAY)
    public void setDisplay(String display) {
        if (isVirtual()) {
            return;
        }
        if (display == null) {
            setDisplay(YogaDisplay.FLEX);
            return;
        }
        display.hashCode();
        if (display.equals(ViewProps.FLEX)) {
            setDisplay(YogaDisplay.FLEX);
        } else if (display.equals(ViewProps.NONE)) {
            setDisplay(YogaDisplay.NONE);
        } else {
            throw new JSApplicationIllegalArgumentException("invalid value for display: " + display);
        }
    }

    @ReactPropGroup(names = {ViewProps.MARGIN, ViewProps.MARGIN_VERTICAL, ViewProps.MARGIN_HORIZONTAL, ViewProps.MARGIN_START, ViewProps.MARGIN_END, ViewProps.MARGIN_TOP, ViewProps.MARGIN_BOTTOM, ViewProps.MARGIN_LEFT, ViewProps.MARGIN_RIGHT})
    public void setMargins(int index, Dynamic margin) {
        if (isVirtual()) {
            return;
        }
        int maybeTransformLeftRightToStartEnd = maybeTransformLeftRightToStartEnd(ViewProps.PADDING_MARGIN_SPACING_TYPES[index]);
        this.mTempYogaValue.setFromDynamic(margin);
        int i = AnonymousClass1.$SwitchMap$com$facebook$yoga$YogaUnit[this.mTempYogaValue.unit.ordinal()];
        if (i == 1 || i == 2) {
            setMargin(maybeTransformLeftRightToStartEnd, this.mTempYogaValue.value);
        } else if (i == 3) {
            setMarginAuto(maybeTransformLeftRightToStartEnd);
        } else if (i == 4) {
            setMarginPercent(maybeTransformLeftRightToStartEnd, this.mTempYogaValue.value);
        }
        margin.recycle();
    }

    @ReactPropGroup(names = {ViewProps.PADDING, ViewProps.PADDING_VERTICAL, ViewProps.PADDING_HORIZONTAL, ViewProps.PADDING_START, ViewProps.PADDING_END, ViewProps.PADDING_TOP, ViewProps.PADDING_BOTTOM, ViewProps.PADDING_LEFT, ViewProps.PADDING_RIGHT})
    public void setPaddings(int index, Dynamic padding) {
        if (isVirtual()) {
            return;
        }
        int maybeTransformLeftRightToStartEnd = maybeTransformLeftRightToStartEnd(ViewProps.PADDING_MARGIN_SPACING_TYPES[index]);
        this.mTempYogaValue.setFromDynamic(padding);
        int i = AnonymousClass1.$SwitchMap$com$facebook$yoga$YogaUnit[this.mTempYogaValue.unit.ordinal()];
        if (i == 1 || i == 2) {
            setPadding(maybeTransformLeftRightToStartEnd, this.mTempYogaValue.value);
        } else if (i == 4) {
            setPaddingPercent(maybeTransformLeftRightToStartEnd, this.mTempYogaValue.value);
        }
        padding.recycle();
    }

    @ReactPropGroup(defaultFloat = Float.NaN, names = {ViewProps.BORDER_WIDTH, ViewProps.BORDER_START_WIDTH, ViewProps.BORDER_END_WIDTH, ViewProps.BORDER_TOP_WIDTH, ViewProps.BORDER_BOTTOM_WIDTH, ViewProps.BORDER_LEFT_WIDTH, ViewProps.BORDER_RIGHT_WIDTH})
    public void setBorderWidths(int index, float borderWidth) {
        if (isVirtual()) {
            return;
        }
        setBorder(maybeTransformLeftRightToStartEnd(ViewProps.BORDER_SPACING_TYPES[index]), PixelUtil.toPixelFromDIP(borderWidth));
    }

    @ReactPropGroup(names = {ViewProps.START, ViewProps.END, ViewProps.LEFT, ViewProps.RIGHT, ViewProps.TOP, ViewProps.BOTTOM})
    public void setPositionValues(int index, Dynamic position) {
        if (isVirtual()) {
            return;
        }
        int maybeTransformLeftRightToStartEnd = maybeTransformLeftRightToStartEnd(new int[]{4, 5, 0, 2, 1, 3}[index]);
        this.mTempYogaValue.setFromDynamic(position);
        int i = AnonymousClass1.$SwitchMap$com$facebook$yoga$YogaUnit[this.mTempYogaValue.unit.ordinal()];
        if (i == 1 || i == 2) {
            setPosition(maybeTransformLeftRightToStartEnd, this.mTempYogaValue.value);
        } else if (i == 4) {
            setPositionPercent(maybeTransformLeftRightToStartEnd, this.mTempYogaValue.value);
        }
        position.recycle();
    }

    private int maybeTransformLeftRightToStartEnd(int spacingType) {
        if (!I18nUtil.getInstance().doLeftAndRightSwapInRTL(getThemedContext())) {
            return spacingType;
        }
        if (spacingType == 0) {
            return 4;
        }
        if (spacingType == 2) {
            return 5;
        }
        return spacingType;
    }

    @ReactProp(name = ViewProps.POSITION)
    public void setPosition(String position) {
        if (isVirtual()) {
            return;
        }
        if (position == null) {
            setPositionType(YogaPositionType.RELATIVE);
            return;
        }
        position.hashCode();
        char c = 65535;
        switch (position.hashCode()) {
            case -892481938:
                if (position.equals("static")) {
                    c = 0;
                    break;
                }
                break;
            case -554435892:
                if (position.equals("relative")) {
                    c = 1;
                    break;
                }
                break;
            case 1728122231:
                if (position.equals("absolute")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                setPositionType(YogaPositionType.STATIC);
                return;
            case 1:
                setPositionType(YogaPositionType.RELATIVE);
                return;
            case 2:
                setPositionType(YogaPositionType.ABSOLUTE);
                return;
            default:
                throw new JSApplicationIllegalArgumentException("invalid value for position: " + position);
        }
    }

    @Override // com.facebook.react.uimanager.ReactShadowNodeImpl, com.facebook.react.uimanager.ReactShadowNode
    @ReactProp(name = ViewProps.ON_LAYOUT)
    public void setShouldNotifyOnLayout(boolean shouldNotifyOnLayout) {
        super.setShouldNotifyOnLayout(shouldNotifyOnLayout);
    }
}
