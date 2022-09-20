package com.facebook.react.jstasks;

import android.os.Handler;
import android.util.SparseArray;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactSoftExceptionLogger;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.modules.appregistry.AppRegistry;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
/* loaded from: classes.dex */
public class HeadlessJsTaskContext {
    private static final WeakHashMap<ReactContext, HeadlessJsTaskContext> INSTANCES = new WeakHashMap<>();
    private final WeakReference<ReactContext> mReactContext;
    private final Set<HeadlessJsTaskEventListener> mHeadlessJsTaskEventListeners = new CopyOnWriteArraySet();
    private final AtomicInteger mLastTaskId = new AtomicInteger(0);
    private final Handler mHandler = new Handler();
    private final Set<Integer> mActiveTasks = new CopyOnWriteArraySet();
    private final Map<Integer, HeadlessJsTaskConfig> mActiveTaskConfigs = new ConcurrentHashMap();
    private final SparseArray<Runnable> mTaskTimeouts = new SparseArray<>();

    public static HeadlessJsTaskContext getInstance(ReactContext context) {
        WeakHashMap<ReactContext, HeadlessJsTaskContext> weakHashMap = INSTANCES;
        HeadlessJsTaskContext headlessJsTaskContext = weakHashMap.get(context);
        if (headlessJsTaskContext == null) {
            HeadlessJsTaskContext headlessJsTaskContext2 = new HeadlessJsTaskContext(context);
            weakHashMap.put(context, headlessJsTaskContext2);
            return headlessJsTaskContext2;
        }
        return headlessJsTaskContext;
    }

    private HeadlessJsTaskContext(ReactContext reactContext) {
        this.mReactContext = new WeakReference<>(reactContext);
    }

    public void addTaskEventListener(HeadlessJsTaskEventListener listener) {
        this.mHeadlessJsTaskEventListeners.add(listener);
    }

    public void removeTaskEventListener(HeadlessJsTaskEventListener listener) {
        this.mHeadlessJsTaskEventListeners.remove(listener);
    }

    public boolean hasActiveTasks() {
        return this.mActiveTasks.size() > 0;
    }

    public synchronized int startTask(final HeadlessJsTaskConfig taskConfig) {
        int incrementAndGet;
        incrementAndGet = this.mLastTaskId.incrementAndGet();
        startTask(taskConfig, incrementAndGet);
        return incrementAndGet;
    }

    public synchronized void startTask(final HeadlessJsTaskConfig taskConfig, int taskId) {
        UiThreadUtil.assertOnUiThread();
        ReactContext reactContext = (ReactContext) Assertions.assertNotNull(this.mReactContext.get(), "Tried to start a task on a react context that has already been destroyed");
        if (reactContext.getLifecycleState() == LifecycleState.RESUMED && !taskConfig.isAllowedInForeground()) {
            throw new IllegalStateException("Tried to start task " + taskConfig.getTaskKey() + " while in foreground, but this is not allowed.");
        }
        this.mActiveTasks.add(Integer.valueOf(taskId));
        this.mActiveTaskConfigs.put(Integer.valueOf(taskId), new HeadlessJsTaskConfig(taskConfig));
        if (reactContext.hasActiveReactInstance()) {
            ((AppRegistry) reactContext.getJSModule(AppRegistry.class)).startHeadlessTask(taskId, taskConfig.getTaskKey(), taskConfig.getData());
        } else {
            ReactSoftExceptionLogger.logSoftException("HeadlessJsTaskContext", new RuntimeException("Cannot start headless task, CatalystInstance not available"));
        }
        if (taskConfig.getTimeout() > 0) {
            scheduleTaskTimeout(taskId, taskConfig.getTimeout());
        }
        for (HeadlessJsTaskEventListener headlessJsTaskEventListener : this.mHeadlessJsTaskEventListeners) {
            headlessJsTaskEventListener.onHeadlessJsTaskStart(taskId);
        }
    }

    public synchronized boolean retryTask(final int taskId) {
        HeadlessJsTaskConfig headlessJsTaskConfig = this.mActiveTaskConfigs.get(Integer.valueOf(taskId));
        boolean z = headlessJsTaskConfig != null;
        Assertions.assertCondition(z, "Tried to retrieve non-existent task config with id " + taskId + ".");
        HeadlessJsTaskRetryPolicy retryPolicy = headlessJsTaskConfig.getRetryPolicy();
        if (!retryPolicy.canRetry()) {
            return false;
        }
        removeTimeout(taskId);
        final HeadlessJsTaskConfig headlessJsTaskConfig2 = new HeadlessJsTaskConfig(headlessJsTaskConfig.getTaskKey(), headlessJsTaskConfig.getData(), headlessJsTaskConfig.getTimeout(), headlessJsTaskConfig.isAllowedInForeground(), retryPolicy.update());
        UiThreadUtil.runOnUiThread(new Runnable() { // from class: com.facebook.react.jstasks.HeadlessJsTaskContext.1
            @Override // java.lang.Runnable
            public void run() {
                HeadlessJsTaskContext.this.startTask(headlessJsTaskConfig2, taskId);
            }
        }, retryPolicy.getDelay());
        return true;
    }

    public synchronized void finishTask(final int taskId) {
        boolean remove = this.mActiveTasks.remove(Integer.valueOf(taskId));
        Assertions.assertCondition(remove, "Tried to finish non-existent task with id " + taskId + ".");
        boolean z = this.mActiveTaskConfigs.remove(Integer.valueOf(taskId)) != null;
        Assertions.assertCondition(z, "Tried to remove non-existent task config with id " + taskId + ".");
        removeTimeout(taskId);
        UiThreadUtil.runOnUiThread(new Runnable() { // from class: com.facebook.react.jstasks.HeadlessJsTaskContext.2
            @Override // java.lang.Runnable
            public void run() {
                for (HeadlessJsTaskEventListener headlessJsTaskEventListener : HeadlessJsTaskContext.this.mHeadlessJsTaskEventListeners) {
                    headlessJsTaskEventListener.onHeadlessJsTaskFinish(taskId);
                }
            }
        });
    }

    private void removeTimeout(int taskId) {
        Runnable runnable = this.mTaskTimeouts.get(taskId);
        if (runnable != null) {
            this.mHandler.removeCallbacks(runnable);
            this.mTaskTimeouts.remove(taskId);
        }
    }

    public synchronized boolean isTaskRunning(final int taskId) {
        return this.mActiveTasks.contains(Integer.valueOf(taskId));
    }

    private void scheduleTaskTimeout(final int taskId, long timeout) {
        Runnable runnable = new Runnable() { // from class: com.facebook.react.jstasks.HeadlessJsTaskContext.3
            @Override // java.lang.Runnable
            public void run() {
                HeadlessJsTaskContext.this.finishTask(taskId);
            }
        };
        this.mTaskTimeouts.append(taskId, runnable);
        this.mHandler.postDelayed(runnable, timeout);
    }
}
