package com.facebook.react.views.text;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.views.view.ViewGroupClickEvent;
/* loaded from: classes.dex */
public class ReactClickableSpan extends ClickableSpan implements ReactSpan {
    private final int mForegroundColor;
    private final int mReactTag;

    public ReactClickableSpan(int reactTag, int foregroundColor) {
        this.mReactTag = reactTag;
        this.mForegroundColor = foregroundColor;
    }

    @Override // android.text.style.ClickableSpan
    public void onClick(View view) {
        ReactContext reactContext = (ReactContext) view.getContext();
        EventDispatcher eventDispatcherForReactTag = UIManagerHelper.getEventDispatcherForReactTag(reactContext, this.mReactTag);
        if (eventDispatcherForReactTag != null) {
            eventDispatcherForReactTag.dispatchEvent(new ViewGroupClickEvent(UIManagerHelper.getSurfaceId(reactContext), this.mReactTag));
        }
    }

    @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(this.mForegroundColor);
        ds.setUnderlineText(false);
    }

    public int getReactTag() {
        return this.mReactTag;
    }
}
