package com.facebook.react.views.text;

import android.content.Context;
import android.text.Spannable;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableNativeMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.common.mapbuffer.ReadableMapBuffer;
import com.facebook.react.config.ReactFeatureFlags;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.IViewManagerWithChildren;
import com.facebook.react.uimanager.ReactStylesDiffMap;
import com.facebook.react.uimanager.StateWrapper;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.yoga.YogaMeasureMode;
import java.util.Map;
@ReactModule(name = ReactTextViewManager.REACT_CLASS)
/* loaded from: classes.dex */
public class ReactTextViewManager extends ReactTextAnchorViewManager<ReactTextView, ReactTextShadowNode> implements IViewManagerWithChildren {
    public static final String REACT_CLASS = "RCTText";
    private static final short TX_STATE_KEY_ATTRIBUTED_STRING = 0;
    private static final short TX_STATE_KEY_HASH = 2;
    private static final short TX_STATE_KEY_MOST_RECENT_EVENT_COUNT = 3;
    private static final short TX_STATE_KEY_PARAGRAPH_ATTRIBUTES = 1;
    protected ReactTextViewManagerCallback mReactTextViewManagerCallback;

    @Override // com.facebook.react.uimanager.ViewManager, com.facebook.react.bridge.NativeModule
    public String getName() {
        return REACT_CLASS;
    }

    @Override // com.facebook.react.uimanager.IViewManagerWithChildren
    public boolean needsCustomLayoutForChildren() {
        return true;
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public ReactTextView createViewInstance(ThemedReactContext context) {
        return new ReactTextView(context);
    }

    public void updateExtraData(ReactTextView view, Object extraData) {
        ReactTextUpdate reactTextUpdate = (ReactTextUpdate) extraData;
        if (reactTextUpdate.containsImages()) {
            TextInlineImageSpan.possiblyUpdateInlineImageSpans(reactTextUpdate.getText(), view);
        }
        view.setText(reactTextUpdate);
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public ReactTextShadowNode createShadowNodeInstance() {
        return new ReactTextShadowNode();
    }

    public ReactTextShadowNode createShadowNodeInstance(ReactTextViewManagerCallback reactTextViewManagerCallback) {
        return new ReactTextShadowNode(reactTextViewManagerCallback);
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public Class<ReactTextShadowNode> getShadowNodeClass() {
        return ReactTextShadowNode.class;
    }

    public void onAfterUpdateTransaction(ReactTextView view) {
        super.onAfterUpdateTransaction((ReactTextViewManager) view);
        view.updateView();
    }

    public Object updateState(ReactTextView view, ReactStylesDiffMap props, StateWrapper stateWrapper) {
        ReadableMapBuffer statDataMapBuffer;
        if (stateWrapper == null) {
            return null;
        }
        if (ReactFeatureFlags.isMapBufferSerializationEnabled() && (statDataMapBuffer = stateWrapper.getStatDataMapBuffer()) != null) {
            return getReactTextUpdate(view, props, statDataMapBuffer);
        }
        ReadableNativeMap stateData = stateWrapper.getStateData();
        if (stateData == null) {
            return null;
        }
        ReadableNativeMap map = stateData.getMap("attributedString");
        ReadableNativeMap map2 = stateData.getMap("paragraphAttributes");
        Spannable orCreateSpannableForText = TextLayoutManager.getOrCreateSpannableForText(view.getContext(), map, this.mReactTextViewManagerCallback);
        view.setSpanned(orCreateSpannableForText);
        return new ReactTextUpdate(orCreateSpannableForText, stateData.hasKey("mostRecentEventCount") ? stateData.getInt("mostRecentEventCount") : -1, false, TextAttributeProps.getTextAlignment(props, TextLayoutManager.isRTL(map)), TextAttributeProps.getTextBreakStrategy(map2.getString(ViewProps.TEXT_BREAK_STRATEGY)), TextAttributeProps.getJustificationMode(props));
    }

    private Object getReactTextUpdate(ReactTextView view, ReactStylesDiffMap props, ReadableMapBuffer state) {
        ReadableMapBuffer mapBuffer = state.getMapBuffer((short) 0);
        ReadableMapBuffer mapBuffer2 = state.getMapBuffer((short) 1);
        Spannable orCreateSpannableForText = TextLayoutManagerMapBuffer.getOrCreateSpannableForText(view.getContext(), mapBuffer, this.mReactTextViewManagerCallback);
        view.setSpanned(orCreateSpannableForText);
        return new ReactTextUpdate(orCreateSpannableForText, -1, false, TextAttributeProps.getTextAlignment(props, TextLayoutManagerMapBuffer.isRTL(mapBuffer)), TextAttributeProps.getTextBreakStrategy(mapBuffer2.getString((short) 2)), TextAttributeProps.getJustificationMode(props));
    }

    @Override // com.facebook.react.uimanager.BaseViewManager, com.facebook.react.uimanager.ViewManager
    public Map getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.of("topTextLayout", MapBuilder.of("registrationName", "onTextLayout"), "topInlineViewLayout", MapBuilder.of("registrationName", "onInlineViewLayout"));
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public long measure(Context context, ReadableMap localData, ReadableMap props, ReadableMap state, float width, YogaMeasureMode widthMode, float height, YogaMeasureMode heightMode, float[] attachmentsPositions) {
        return TextLayoutManager.measureText(context, localData, props, width, widthMode, height, heightMode, this.mReactTextViewManagerCallback, attachmentsPositions);
    }

    public void setPadding(ReactTextView view, int left, int top, int right, int bottom) {
        view.setPadding(left, top, right, bottom);
    }
}
