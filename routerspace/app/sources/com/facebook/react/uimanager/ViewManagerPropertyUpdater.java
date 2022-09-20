package com.facebook.react.uimanager;

import android.view.View;
import com.facebook.common.logging.FLog;
import com.facebook.react.uimanager.ViewManagersPropertyCache;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
/* loaded from: classes.dex */
public class ViewManagerPropertyUpdater {
    private static final String TAG = "ViewManagerPropertyUpdater";
    private static final Map<Class<?>, ViewManagerSetter<?, ?>> VIEW_MANAGER_SETTER_MAP = new HashMap();
    private static final Map<Class<?>, ShadowNodeSetter<?>> SHADOW_NODE_SETTER_MAP = new HashMap();

    /* loaded from: classes.dex */
    public interface Settable {
        void getProperties(Map<String, String> props);
    }

    /* loaded from: classes.dex */
    public interface ShadowNodeSetter<T extends ReactShadowNode> extends Settable {
        void setProperty(T node, String name, Object value);
    }

    /* loaded from: classes.dex */
    public interface ViewManagerSetter<T extends ViewManager, V extends View> extends Settable {
        void setProperty(T manager, V view, String name, Object value);
    }

    public static void clear() {
        ViewManagersPropertyCache.clear();
        VIEW_MANAGER_SETTER_MAP.clear();
        SHADOW_NODE_SETTER_MAP.clear();
    }

    public static <T extends ViewManagerDelegate<V>, V extends View> void updateProps(T delegate, V v, ReactStylesDiffMap props) {
        Iterator<Map.Entry<String, Object>> entryIterator = props.mBackingMap.getEntryIterator();
        while (entryIterator.hasNext()) {
            Map.Entry<String, Object> next = entryIterator.next();
            delegate.setProperty(v, next.getKey(), next.getValue());
        }
    }

    public static <T extends ViewManager, V extends View> void updateProps(T manager, V v, ReactStylesDiffMap props) {
        ViewManagerSetter findManagerSetter = findManagerSetter(manager.getClass());
        Iterator<Map.Entry<String, Object>> entryIterator = props.mBackingMap.getEntryIterator();
        while (entryIterator.hasNext()) {
            Map.Entry<String, Object> next = entryIterator.next();
            findManagerSetter.setProperty(manager, v, next.getKey(), next.getValue());
        }
    }

    public static <T extends ReactShadowNode> void updateProps(T node, ReactStylesDiffMap props) {
        ShadowNodeSetter findNodeSetter = findNodeSetter(node.getClass());
        Iterator<Map.Entry<String, Object>> entryIterator = props.mBackingMap.getEntryIterator();
        while (entryIterator.hasNext()) {
            Map.Entry<String, Object> next = entryIterator.next();
            findNodeSetter.setProperty(node, next.getKey(), next.getValue());
        }
    }

    public static Map<String, String> getNativeProps(Class<? extends ViewManager> viewManagerTopClass, Class<? extends ReactShadowNode> shadowNodeTopClass) {
        HashMap hashMap = new HashMap();
        findManagerSetter(viewManagerTopClass).getProperties(hashMap);
        findNodeSetter(shadowNodeTopClass).getProperties(hashMap);
        return hashMap;
    }

    private static <T extends ViewManager, V extends View> ViewManagerSetter<T, V> findManagerSetter(Class<? extends ViewManager> managerClass) {
        Map<Class<?>, ViewManagerSetter<?, ?>> map = VIEW_MANAGER_SETTER_MAP;
        ViewManagerSetter<?, ?> viewManagerSetter = map.get(managerClass);
        if (viewManagerSetter == null) {
            viewManagerSetter = (ViewManagerSetter) findGeneratedSetter(managerClass);
            if (viewManagerSetter == null) {
                viewManagerSetter = new FallbackViewManagerSetter<>(managerClass);
            }
            map.put(managerClass, viewManagerSetter);
        }
        return viewManagerSetter;
    }

    private static <T extends ReactShadowNode> ShadowNodeSetter<T> findNodeSetter(Class<? extends ReactShadowNode> nodeClass) {
        Map<Class<?>, ShadowNodeSetter<?>> map = SHADOW_NODE_SETTER_MAP;
        ShadowNodeSetter<?> shadowNodeSetter = map.get(nodeClass);
        if (shadowNodeSetter == null) {
            shadowNodeSetter = (ShadowNodeSetter) findGeneratedSetter(nodeClass);
            if (shadowNodeSetter == null) {
                shadowNodeSetter = new FallbackShadowNodeSetter<>(nodeClass);
            }
            map.put(nodeClass, shadowNodeSetter);
        }
        return shadowNodeSetter;
    }

    private static <T> T findGeneratedSetter(Class<?> cls) {
        Throwable e;
        String name = cls.getName();
        try {
            return (T) Class.forName(name + "$$PropsSetter").newInstance();
        } catch (ClassNotFoundException unused) {
            FLog.w(TAG, "Could not find generated setter for " + cls);
            return null;
        } catch (IllegalAccessException e2) {
            e = e2;
            throw new RuntimeException("Unable to instantiate methods getter for " + name, e);
        } catch (InstantiationException e3) {
            e = e3;
            throw new RuntimeException("Unable to instantiate methods getter for " + name, e);
        }
    }

    /* loaded from: classes.dex */
    public static class FallbackViewManagerSetter<T extends ViewManager, V extends View> implements ViewManagerSetter<T, V> {
        private final Map<String, ViewManagersPropertyCache.PropSetter> mPropSetters;

        private FallbackViewManagerSetter(Class<? extends ViewManager> viewManagerClass) {
            this.mPropSetters = ViewManagersPropertyCache.getNativePropSettersForViewManagerClass(viewManagerClass);
        }

        @Override // com.facebook.react.uimanager.ViewManagerPropertyUpdater.ViewManagerSetter
        public void setProperty(T manager, V v, String name, Object value) {
            ViewManagersPropertyCache.PropSetter propSetter = this.mPropSetters.get(name);
            if (propSetter != null) {
                propSetter.updateViewProp(manager, v, value);
            }
        }

        @Override // com.facebook.react.uimanager.ViewManagerPropertyUpdater.Settable
        public void getProperties(Map<String, String> props) {
            for (ViewManagersPropertyCache.PropSetter propSetter : this.mPropSetters.values()) {
                props.put(propSetter.getPropName(), propSetter.getPropType());
            }
        }
    }

    /* loaded from: classes.dex */
    public static class FallbackShadowNodeSetter<T extends ReactShadowNode> implements ShadowNodeSetter<T> {
        private final Map<String, ViewManagersPropertyCache.PropSetter> mPropSetters;

        private FallbackShadowNodeSetter(Class<? extends ReactShadowNode> shadowNodeClass) {
            this.mPropSetters = ViewManagersPropertyCache.getNativePropSettersForShadowNodeClass(shadowNodeClass);
        }

        @Override // com.facebook.react.uimanager.ViewManagerPropertyUpdater.ShadowNodeSetter
        public void setProperty(ReactShadowNode node, String name, Object value) {
            ViewManagersPropertyCache.PropSetter propSetter = this.mPropSetters.get(name);
            if (propSetter != null) {
                propSetter.updateShadowNodeProp(node, value);
            }
        }

        @Override // com.facebook.react.uimanager.ViewManagerPropertyUpdater.Settable
        public void getProperties(Map<String, String> props) {
            for (ViewManagersPropertyCache.PropSetter propSetter : this.mPropSetters.values()) {
                props.put(propSetter.getPropName(), propSetter.getPropType());
            }
        }
    }
}
