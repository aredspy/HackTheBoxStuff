package com.facebook.react.fabric.mounting.mountitems;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.fabric.mounting.MountingManager;
/* loaded from: classes.dex */
public class DispatchStringCommandMountItem extends DispatchCommandMountItem {
    private final ReadableArray mCommandArgs;
    private final String mCommandId;
    private final int mReactTag;
    private final int mSurfaceId;

    public DispatchStringCommandMountItem(int surfaceId, int reactTag, String commandId, ReadableArray commandArgs) {
        this.mSurfaceId = surfaceId;
        this.mReactTag = reactTag;
        this.mCommandId = commandId;
        this.mCommandArgs = commandArgs;
    }

    @Override // com.facebook.react.fabric.mounting.mountitems.MountItem
    public int getSurfaceId() {
        return this.mSurfaceId;
    }

    @Override // com.facebook.react.fabric.mounting.mountitems.MountItem
    public void execute(MountingManager mountingManager) {
        mountingManager.receiveCommand(this.mSurfaceId, this.mReactTag, this.mCommandId, this.mCommandArgs);
    }

    public String toString() {
        return "DispatchStringCommandMountItem [" + this.mReactTag + "] " + this.mCommandId;
    }
}
