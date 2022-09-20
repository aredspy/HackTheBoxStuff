package com.facebook.react.bridge;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
/* loaded from: classes.dex */
public final class JavaScriptModuleRegistry {
    private final HashMap<Class<? extends JavaScriptModule>, JavaScriptModule> mModuleInstances = new HashMap<>();

    public synchronized <T extends JavaScriptModule> T getJavaScriptModule(CatalystInstance instance, Class<T> moduleInterface) {
        T t = (T) this.mModuleInstances.get(moduleInterface);
        if (t != null) {
            return t;
        }
        T t2 = (T) Proxy.newProxyInstance(moduleInterface.getClassLoader(), new Class[]{moduleInterface}, new JavaScriptModuleInvocationHandler(instance, moduleInterface));
        this.mModuleInstances.put(moduleInterface, t2);
        return t2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class JavaScriptModuleInvocationHandler implements InvocationHandler {
        private final CatalystInstance mCatalystInstance;
        private final Class<? extends JavaScriptModule> mModuleInterface;
        private String mName;

        public JavaScriptModuleInvocationHandler(CatalystInstance catalystInstance, Class<? extends JavaScriptModule> moduleInterface) {
            this.mCatalystInstance = catalystInstance;
            this.mModuleInterface = moduleInterface;
        }

        private String getJSModuleName() {
            if (this.mName == null) {
                this.mName = JavaScriptModuleRegistry.getJSModuleName(this.mModuleInterface);
            }
            return this.mName;
        }

        @Override // java.lang.reflect.InvocationHandler
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            this.mCatalystInstance.callFunction(getJSModuleName(), method.getName(), args != null ? Arguments.fromJavaArgs(args) : new WritableNativeArray());
            return null;
        }
    }

    public static String getJSModuleName(Class<? extends JavaScriptModule> jsModuleInterface) {
        String simpleName = jsModuleInterface.getSimpleName();
        int lastIndexOf = simpleName.lastIndexOf(36);
        return lastIndexOf != -1 ? simpleName.substring(lastIndexOf + 1) : simpleName;
    }
}
