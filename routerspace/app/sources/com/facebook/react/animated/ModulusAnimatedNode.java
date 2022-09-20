package com.facebook.react.animated;

import com.facebook.react.bridge.JSApplicationCausedNativeException;
import com.facebook.react.bridge.ReadableMap;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class ModulusAnimatedNode extends ValueAnimatedNode {
    private final int mInputNode;
    private final double mModulus;
    private final NativeAnimatedNodesManager mNativeAnimatedNodesManager;

    public ModulusAnimatedNode(ReadableMap config, NativeAnimatedNodesManager nativeAnimatedNodesManager) {
        this.mNativeAnimatedNodesManager = nativeAnimatedNodesManager;
        this.mInputNode = config.getInt("input");
        this.mModulus = config.getDouble("modulus");
    }

    @Override // com.facebook.react.animated.AnimatedNode
    public void update() {
        AnimatedNode nodeById = this.mNativeAnimatedNodesManager.getNodeById(this.mInputNode);
        if (nodeById != null && (nodeById instanceof ValueAnimatedNode)) {
            double value = ((ValueAnimatedNode) nodeById).getValue();
            double d = this.mModulus;
            this.mValue = ((value % d) + d) % d;
            return;
        }
        throw new JSApplicationCausedNativeException("Illegal node ID set as an input for Animated.modulus node");
    }

    @Override // com.facebook.react.animated.ValueAnimatedNode, com.facebook.react.animated.AnimatedNode
    public String prettyPrint() {
        return "NativeAnimatedNodesManager[" + this.mTag + "] inputNode: " + this.mInputNode + " modulus: " + this.mModulus + " super: " + super.prettyPrint();
    }
}
