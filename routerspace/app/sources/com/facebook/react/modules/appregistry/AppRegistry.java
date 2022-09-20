package com.facebook.react.modules.appregistry;

import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.WritableMap;
/* loaded from: classes.dex */
public interface AppRegistry extends JavaScriptModule {
    void runApplication(String appKey, WritableMap appParameters);

    void startHeadlessTask(int taskId, String taskKey, WritableMap data);

    void unmountApplicationComponentAtRootTag(int rootNodeTag);
}
