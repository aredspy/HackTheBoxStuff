package com.facebook.react.fabric.mounting.mountitems;

import com.facebook.react.fabric.mounting.MountingManager;
/* loaded from: classes.dex */
public interface MountItem {
    void execute(MountingManager mountingManager);

    int getSurfaceId();
}
