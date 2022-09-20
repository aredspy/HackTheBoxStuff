package com.facebook.react.uimanager;

import java.util.List;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public interface ViewManagerResolver {
    @Nullable
    ViewManager getViewManager(String viewManagerName);

    List<String> getViewManagerNames();
}
