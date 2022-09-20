package com.facebook.react.animated;

import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.JavaOnlyMap;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.UIManager;
import com.facebook.react.uimanager.common.ViewUtil;
import java.util.HashMap;
import java.util.Map;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class PropsAnimatedNode extends AnimatedNode {
    private final NativeAnimatedNodesManager mNativeAnimatedNodesManager;
    private final JavaOnlyMap mPropMap;
    private UIManager mUIManager;
    private int mConnectedViewTag = -1;
    private final Map<String, Integer> mPropNodeMapping = new HashMap();

    public PropsAnimatedNode(ReadableMap config, NativeAnimatedNodesManager nativeAnimatedNodesManager) {
        ReadableMap map = config.getMap("props");
        ReadableMapKeySetIterator keySetIterator = map.keySetIterator();
        while (keySetIterator.hasNextKey()) {
            String nextKey = keySetIterator.nextKey();
            this.mPropNodeMapping.put(nextKey, Integer.valueOf(map.getInt(nextKey)));
        }
        this.mPropMap = new JavaOnlyMap();
        this.mNativeAnimatedNodesManager = nativeAnimatedNodesManager;
    }

    public void connectToView(int viewTag, UIManager uiManager) {
        if (this.mConnectedViewTag != -1) {
            throw new JSApplicationIllegalArgumentException("Animated node " + this.mTag + " is already attached to a view: " + this.mConnectedViewTag);
        }
        this.mConnectedViewTag = viewTag;
        this.mUIManager = uiManager;
    }

    public void disconnectFromView(int viewTag) {
        int i = this.mConnectedViewTag;
        if (i != viewTag && i != -1) {
            throw new JSApplicationIllegalArgumentException("Attempting to disconnect view that has not been connected with the given animated node: " + viewTag + " but is connected to view " + this.mConnectedViewTag);
        }
        this.mConnectedViewTag = -1;
    }

    public void restoreDefaultValues() {
        int i = this.mConnectedViewTag;
        if (i == -1 || ViewUtil.getUIManagerType(i) == 2) {
            return;
        }
        ReadableMapKeySetIterator keySetIterator = this.mPropMap.keySetIterator();
        while (keySetIterator.hasNextKey()) {
            this.mPropMap.putNull(keySetIterator.nextKey());
        }
        this.mUIManager.synchronouslyUpdateViewOnUIThread(this.mConnectedViewTag, this.mPropMap);
    }

    public final void updateView() {
        if (this.mConnectedViewTag == -1) {
            return;
        }
        for (Map.Entry<String, Integer> entry : this.mPropNodeMapping.entrySet()) {
            AnimatedNode nodeById = this.mNativeAnimatedNodesManager.getNodeById(entry.getValue().intValue());
            if (nodeById == null) {
                throw new IllegalArgumentException("Mapped property node does not exists");
            }
            if (nodeById instanceof StyleAnimatedNode) {
                ((StyleAnimatedNode) nodeById).collectViewUpdates(this.mPropMap);
            } else if (nodeById instanceof ValueAnimatedNode) {
                ValueAnimatedNode valueAnimatedNode = (ValueAnimatedNode) nodeById;
                Object animatedObject = valueAnimatedNode.getAnimatedObject();
                if (animatedObject instanceof String) {
                    this.mPropMap.putString(entry.getKey(), (String) animatedObject);
                } else {
                    this.mPropMap.putDouble(entry.getKey(), valueAnimatedNode.getValue());
                }
            } else {
                throw new IllegalArgumentException("Unsupported type of node used in property node " + nodeById.getClass());
            }
        }
        this.mUIManager.synchronouslyUpdateViewOnUIThread(this.mConnectedViewTag, this.mPropMap);
    }

    @Override // com.facebook.react.animated.AnimatedNode
    public String prettyPrint() {
        StringBuilder sb = new StringBuilder();
        sb.append("PropsAnimatedNode[");
        sb.append(this.mTag);
        sb.append("] connectedViewTag: ");
        sb.append(this.mConnectedViewTag);
        sb.append(" mPropNodeMapping: ");
        Map<String, Integer> map = this.mPropNodeMapping;
        String str = "null";
        sb.append(map != null ? map.toString() : str);
        sb.append(" mPropMap: ");
        JavaOnlyMap javaOnlyMap = this.mPropMap;
        if (javaOnlyMap != null) {
            str = javaOnlyMap.toString();
        }
        sb.append(str);
        return sb.toString();
    }
}
