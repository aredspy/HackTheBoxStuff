package com.facebook.react.bridge;

import com.facebook.infer.annotation.Assertions;
import com.facebook.react.config.ReactFeatureFlags;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public class JSIModuleRegistry {
    private final Map<JSIModuleType, JSIModuleHolder> mModules = new HashMap();

    public JSIModule getModule(JSIModuleType moduleType) {
        JSIModuleHolder jSIModuleHolder = this.mModules.get(moduleType);
        if (jSIModuleHolder == null) {
            throw new IllegalArgumentException("Unable to find JSIModule for class " + moduleType);
        }
        return (JSIModule) Assertions.assertNotNull(jSIModuleHolder.getJSIModule());
    }

    public void registerModules(List<JSIModuleSpec> jsiModules) {
        for (JSIModuleSpec jSIModuleSpec : jsiModules) {
            this.mModules.put(jSIModuleSpec.getJSIModuleType(), new JSIModuleHolder(jSIModuleSpec));
        }
    }

    public void notifyJSInstanceDestroy() {
        for (Map.Entry<JSIModuleType, JSIModuleHolder> entry : this.mModules.entrySet()) {
            if (entry.getKey() != JSIModuleType.TurboModuleManager) {
                entry.getValue().notifyJSInstanceDestroy();
            }
        }
        if (ReactFeatureFlags.enableReactContextCleanupFix) {
            this.mModules.clear();
        }
    }
}
