package com.facebook.react.uimanager;

import com.facebook.react.common.MapBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public final class ViewManagerRegistry {
    private final ViewManagerResolver mViewManagerResolver;
    private final Map<String, ViewManager> mViewManagers;

    public ViewManagerRegistry(ViewManagerResolver viewManagerResolver) {
        this.mViewManagers = MapBuilder.newHashMap();
        this.mViewManagerResolver = viewManagerResolver;
    }

    public ViewManagerRegistry(List<ViewManager> viewManagerList) {
        HashMap newHashMap = MapBuilder.newHashMap();
        for (ViewManager viewManager : viewManagerList) {
            newHashMap.put(viewManager.getName(), viewManager);
        }
        this.mViewManagers = newHashMap;
        this.mViewManagerResolver = null;
    }

    public ViewManagerRegistry(Map<String, ViewManager> viewManagerMap) {
        this.mViewManagers = viewManagerMap == null ? MapBuilder.newHashMap() : viewManagerMap;
        this.mViewManagerResolver = null;
    }

    public ViewManager get(String className) {
        ViewManager viewManager = this.mViewManagers.get(className);
        if (viewManager != null) {
            return viewManager;
        }
        if (this.mViewManagerResolver != null) {
            ViewManager viewManagerFromResolver = getViewManagerFromResolver(className);
            if (viewManagerFromResolver != null) {
                return viewManagerFromResolver;
            }
            throw new IllegalViewOperationException("ViewManagerResolver returned null for " + className + ", existing names are: " + this.mViewManagerResolver.getViewManagerNames());
        }
        throw new IllegalViewOperationException("No ViewManager found for class " + className);
    }

    private ViewManager getViewManagerFromResolver(String className) {
        ViewManager viewManager = this.mViewManagerResolver.getViewManager(className);
        if (viewManager != null) {
            this.mViewManagers.put(className, viewManager);
        }
        return viewManager;
    }

    public ViewManager getViewManagerIfExists(String className) {
        ViewManager viewManager = this.mViewManagers.get(className);
        if (viewManager != null) {
            return viewManager;
        }
        if (this.mViewManagerResolver == null) {
            return null;
        }
        return getViewManagerFromResolver(className);
    }
}
