package com.facebook.react.fabric.mounting;

import android.text.Spannable;
import android.view.View;
import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactSoftExceptionLogger;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.RetryableMountingLayerException;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.common.mapbuffer.ReadableMapBuffer;
import com.facebook.react.fabric.events.EventEmitterWrapper;
import com.facebook.react.fabric.mounting.mountitems.MountItem;
import com.facebook.react.touch.JSResponderHandler;
import com.facebook.react.uimanager.RootViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewManagerRegistry;
import com.facebook.react.views.text.ReactTextViewManagerCallback;
import com.facebook.react.views.text.TextLayoutManagerMapBuffer;
import com.facebook.yoga.YogaMeasureMode;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
/* loaded from: classes.dex */
public class MountingManager {
    private static final int MAX_STOPPED_SURFACE_IDS_LENGTH = 15;
    public static final String TAG = "MountingManager";
    private SurfaceMountingManager mLastQueriedSurfaceMountingManager;
    private SurfaceMountingManager mMostRecentSurfaceMountingManager;
    private final MountItemExecutor mMountItemExecutor;
    private final ViewManagerRegistry mViewManagerRegistry;
    private final ConcurrentHashMap<Integer, SurfaceMountingManager> mSurfaceIdToManager = new ConcurrentHashMap<>();
    private final CopyOnWriteArrayList<Integer> mStoppedSurfaceIds = new CopyOnWriteArrayList<>();
    private final JSResponderHandler mJSResponderHandler = new JSResponderHandler();
    private final RootViewManager mRootViewManager = new RootViewManager();

    /* loaded from: classes.dex */
    public interface MountItemExecutor {
        void executeItems(Queue<MountItem> items);
    }

    public MountingManager(ViewManagerRegistry viewManagerRegistry, MountItemExecutor mountItemExecutor) {
        this.mViewManagerRegistry = viewManagerRegistry;
        this.mMountItemExecutor = mountItemExecutor;
    }

    public void startSurface(final int surfaceId, final View rootView, ThemedReactContext themedReactContext) {
        startSurface(surfaceId).attachRootView(rootView, themedReactContext);
    }

    public SurfaceMountingManager startSurface(final int surfaceId) {
        SurfaceMountingManager surfaceMountingManager = new SurfaceMountingManager(surfaceId, this.mJSResponderHandler, this.mViewManagerRegistry, this.mRootViewManager, this.mMountItemExecutor);
        this.mSurfaceIdToManager.putIfAbsent(Integer.valueOf(surfaceId), surfaceMountingManager);
        if (this.mSurfaceIdToManager.get(Integer.valueOf(surfaceId)) != surfaceMountingManager) {
            String str = TAG;
            ReactSoftExceptionLogger.logSoftException(str, new IllegalStateException("Called startSurface more than once for the SurfaceId [" + surfaceId + "]"));
        }
        this.mMostRecentSurfaceMountingManager = this.mSurfaceIdToManager.get(Integer.valueOf(surfaceId));
        return surfaceMountingManager;
    }

    public void attachRootView(final int surfaceId, final View rootView, ThemedReactContext themedReactContext) {
        SurfaceMountingManager surfaceManagerEnforced = getSurfaceManagerEnforced(surfaceId, "attachView");
        if (surfaceManagerEnforced.isStopped()) {
            ReactSoftExceptionLogger.logSoftException(TAG, new IllegalStateException("Trying to attach a view to a stopped surface"));
        } else {
            surfaceManagerEnforced.attachRootView(rootView, themedReactContext);
        }
    }

    public void stopSurface(final int surfaceId) {
        SurfaceMountingManager surfaceMountingManager = this.mSurfaceIdToManager.get(Integer.valueOf(surfaceId));
        if (surfaceMountingManager != null) {
            while (this.mStoppedSurfaceIds.size() >= 15) {
                Integer num = this.mStoppedSurfaceIds.get(0);
                this.mSurfaceIdToManager.remove(Integer.valueOf(num.intValue()));
                this.mStoppedSurfaceIds.remove(num);
                FLog.d(TAG, "Removing stale SurfaceMountingManager: [%d]", Integer.valueOf(num.intValue()));
            }
            this.mStoppedSurfaceIds.add(Integer.valueOf(surfaceId));
            surfaceMountingManager.stopSurface();
            if (surfaceMountingManager != this.mMostRecentSurfaceMountingManager) {
                return;
            }
            this.mMostRecentSurfaceMountingManager = null;
            return;
        }
        String str = TAG;
        ReactSoftExceptionLogger.logSoftException(str, new IllegalStateException("Cannot call stopSurface on non-existent surface: [" + surfaceId + "]"));
    }

    public SurfaceMountingManager getSurfaceManager(int surfaceId) {
        SurfaceMountingManager surfaceMountingManager = this.mLastQueriedSurfaceMountingManager;
        if (surfaceMountingManager != null && surfaceMountingManager.getSurfaceId() == surfaceId) {
            return this.mLastQueriedSurfaceMountingManager;
        }
        SurfaceMountingManager surfaceMountingManager2 = this.mMostRecentSurfaceMountingManager;
        if (surfaceMountingManager2 != null && surfaceMountingManager2.getSurfaceId() == surfaceId) {
            return this.mMostRecentSurfaceMountingManager;
        }
        SurfaceMountingManager surfaceMountingManager3 = this.mSurfaceIdToManager.get(Integer.valueOf(surfaceId));
        this.mLastQueriedSurfaceMountingManager = surfaceMountingManager3;
        return surfaceMountingManager3;
    }

    public SurfaceMountingManager getSurfaceManagerEnforced(int surfaceId, String context) {
        SurfaceMountingManager surfaceManager = getSurfaceManager(surfaceId);
        if (surfaceManager != null) {
            return surfaceManager;
        }
        throw new RetryableMountingLayerException("Unable to find SurfaceMountingManager for surfaceId: [" + surfaceId + "]. Context: " + context);
    }

    public boolean surfaceIsStopped(int surfaceId) {
        if (this.mStoppedSurfaceIds.contains(Integer.valueOf(surfaceId))) {
            return true;
        }
        SurfaceMountingManager surfaceManager = getSurfaceManager(surfaceId);
        return surfaceManager != null && surfaceManager.isStopped();
    }

    public boolean isWaitingForViewAttach(int surfaceId) {
        SurfaceMountingManager surfaceManager = getSurfaceManager(surfaceId);
        if (surfaceManager != null && !surfaceManager.isStopped()) {
            return !surfaceManager.isRootViewAttached();
        }
        return false;
    }

    public SurfaceMountingManager getSurfaceManagerForView(int reactTag) {
        SurfaceMountingManager surfaceMountingManager = this.mMostRecentSurfaceMountingManager;
        if (surfaceMountingManager != null && surfaceMountingManager.getViewExists(reactTag)) {
            return this.mMostRecentSurfaceMountingManager;
        }
        for (Map.Entry<Integer, SurfaceMountingManager> entry : this.mSurfaceIdToManager.entrySet()) {
            SurfaceMountingManager value = entry.getValue();
            if (value != this.mMostRecentSurfaceMountingManager && value.getViewExists(reactTag)) {
                if (this.mMostRecentSurfaceMountingManager == null) {
                    this.mMostRecentSurfaceMountingManager = value;
                }
                return value;
            }
        }
        return null;
    }

    public SurfaceMountingManager getSurfaceManagerForViewEnforced(int reactTag) {
        SurfaceMountingManager surfaceManagerForView = getSurfaceManagerForView(reactTag);
        if (surfaceManagerForView != null) {
            return surfaceManagerForView;
        }
        throw new RetryableMountingLayerException("Unable to find SurfaceMountingManager for tag: [" + reactTag + "]");
    }

    public boolean getViewExists(int reactTag) {
        return getSurfaceManagerForView(reactTag) != null;
    }

    @Deprecated
    public void receiveCommand(int surfaceId, int reactTag, int commandId, ReadableArray commandArgs) {
        UiThreadUtil.assertOnUiThread();
        getSurfaceManagerEnforced(surfaceId, "receiveCommand:int").receiveCommand(reactTag, commandId, commandArgs);
    }

    public void receiveCommand(int surfaceId, int reactTag, String commandId, ReadableArray commandArgs) {
        UiThreadUtil.assertOnUiThread();
        getSurfaceManagerEnforced(surfaceId, "receiveCommand:string").receiveCommand(reactTag, commandId, commandArgs);
    }

    public void sendAccessibilityEvent(int surfaceId, int reactTag, int eventType) {
        UiThreadUtil.assertOnUiThread();
        if (surfaceId == -1) {
            getSurfaceManagerForViewEnforced(reactTag).sendAccessibilityEvent(reactTag, eventType);
        } else {
            getSurfaceManagerEnforced(surfaceId, "sendAccessibilityEvent").sendAccessibilityEvent(reactTag, eventType);
        }
    }

    public void updateProps(int reactTag, ReadableMap props) {
        UiThreadUtil.assertOnUiThread();
        if (props == null) {
            return;
        }
        getSurfaceManagerForViewEnforced(reactTag).updateProps(reactTag, props);
    }

    public void clearJSResponder() {
        this.mJSResponderHandler.clearJSResponder();
    }

    public EventEmitterWrapper getEventEmitter(int surfaceId, int reactTag) {
        SurfaceMountingManager surfaceManagerForView = surfaceId == -1 ? getSurfaceManagerForView(reactTag) : getSurfaceManager(surfaceId);
        if (surfaceManagerForView == null) {
            return null;
        }
        return surfaceManagerForView.getEventEmitter(reactTag);
    }

    public long measure(ReactContext context, String componentName, ReadableMap localData, ReadableMap props, ReadableMap state, float width, YogaMeasureMode widthMode, float height, YogaMeasureMode heightMode, float[] attachmentsPositions) {
        return this.mViewManagerRegistry.get(componentName).measure(context, localData, props, state, width, widthMode, height, heightMode, attachmentsPositions);
    }

    public long measureTextMapBuffer(ReactContext context, String componentName, ReadableMapBuffer attributedString, ReadableMapBuffer paragraphAttributes, float width, YogaMeasureMode widthMode, float height, YogaMeasureMode heightMode, float[] attachmentsPositions) {
        return TextLayoutManagerMapBuffer.measureText(context, attributedString, paragraphAttributes, width, widthMode, height, heightMode, new ReactTextViewManagerCallback() { // from class: com.facebook.react.fabric.mounting.MountingManager.1
            @Override // com.facebook.react.views.text.ReactTextViewManagerCallback
            public void onPostProcessSpannable(Spannable text) {
            }
        }, attachmentsPositions);
    }

    public void initializeViewManager(String componentName) {
        this.mViewManagerRegistry.get(componentName);
    }
}
