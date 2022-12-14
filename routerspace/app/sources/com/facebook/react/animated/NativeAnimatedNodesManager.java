package com.facebook.react.animated;

import android.util.SparseArray;
import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.JSApplicationCausedNativeException;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactNoCrashSoftException;
import com.facebook.react.bridge.ReactSoftExceptionLogger;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.UIManager;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.uimanager.events.EventDispatcherListener;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
/* loaded from: classes.dex */
public class NativeAnimatedNodesManager implements EventDispatcherListener {
    private static final String TAG = "NativeAnimatedNodesManager";
    private final ReactApplicationContext mReactApplicationContext;
    private final SparseArray<AnimatedNode> mAnimatedNodes = new SparseArray<>();
    private final SparseArray<AnimationDriver> mActiveAnimations = new SparseArray<>();
    private final SparseArray<AnimatedNode> mUpdatedNodes = new SparseArray<>();
    private final Map<String, List<EventAnimationDriver>> mEventDrivers = new HashMap();
    private int mAnimatedGraphBFSColor = 0;
    private final List<AnimatedNode> mRunUpdateNodeList = new LinkedList();
    private boolean mEventListenerInitializedForFabric = false;
    private boolean mEventListenerInitializedForNonFabric = false;
    private boolean mWarnedAboutGraphTraversal = false;

    public NativeAnimatedNodesManager(ReactApplicationContext reactApplicationContext) {
        this.mReactApplicationContext = reactApplicationContext;
    }

    public void initializeEventListenerForUIManagerType(final int uiManagerType) {
        if (uiManagerType != 2 || !this.mEventListenerInitializedForFabric) {
            if (uiManagerType == 1 && this.mEventListenerInitializedForNonFabric) {
                return;
            }
            this.mReactApplicationContext.runOnUiQueueThread(new Runnable() { // from class: com.facebook.react.animated.NativeAnimatedNodesManager.1
                @Override // java.lang.Runnable
                public void run() {
                    UIManager uIManager = UIManagerHelper.getUIManager(NativeAnimatedNodesManager.this.mReactApplicationContext, uiManagerType);
                    if (uIManager != null) {
                        ((EventDispatcher) uIManager.getEventDispatcher()).addListener(this);
                        if (uiManagerType == 2) {
                            NativeAnimatedNodesManager.this.mEventListenerInitializedForFabric = true;
                        } else {
                            NativeAnimatedNodesManager.this.mEventListenerInitializedForNonFabric = true;
                        }
                    }
                }
            });
        }
    }

    public AnimatedNode getNodeById(int id) {
        return this.mAnimatedNodes.get(id);
    }

    public boolean hasActiveAnimations() {
        return this.mActiveAnimations.size() > 0 || this.mUpdatedNodes.size() > 0;
    }

    public void createAnimatedNode(int tag, ReadableMap config) {
        AnimatedNode animatedNode;
        if (this.mAnimatedNodes.get(tag) != null) {
            throw new JSApplicationIllegalArgumentException("createAnimatedNode: Animated node [" + tag + "] already exists");
        }
        String string = config.getString("type");
        if ("style".equals(string)) {
            animatedNode = new StyleAnimatedNode(config, this);
        } else if ("value".equals(string)) {
            animatedNode = new ValueAnimatedNode(config);
        } else if ("props".equals(string)) {
            animatedNode = new PropsAnimatedNode(config, this);
        } else if ("interpolation".equals(string)) {
            animatedNode = new InterpolationAnimatedNode(config);
        } else if ("addition".equals(string)) {
            animatedNode = new AdditionAnimatedNode(config, this);
        } else if ("subtraction".equals(string)) {
            animatedNode = new SubtractionAnimatedNode(config, this);
        } else if ("division".equals(string)) {
            animatedNode = new DivisionAnimatedNode(config, this);
        } else if ("multiplication".equals(string)) {
            animatedNode = new MultiplicationAnimatedNode(config, this);
        } else if ("modulus".equals(string)) {
            animatedNode = new ModulusAnimatedNode(config, this);
        } else if ("diffclamp".equals(string)) {
            animatedNode = new DiffClampAnimatedNode(config, this);
        } else if (ViewProps.TRANSFORM.equals(string)) {
            animatedNode = new TransformAnimatedNode(config, this);
        } else if ("tracking".equals(string)) {
            animatedNode = new TrackingAnimatedNode(config, this);
        } else {
            throw new JSApplicationIllegalArgumentException("Unsupported node type: " + string);
        }
        animatedNode.mTag = tag;
        this.mAnimatedNodes.put(tag, animatedNode);
        this.mUpdatedNodes.put(tag, animatedNode);
    }

    public void dropAnimatedNode(int tag) {
        this.mAnimatedNodes.remove(tag);
        this.mUpdatedNodes.remove(tag);
    }

    public void startListeningToAnimatedNodeValue(int tag, AnimatedNodeValueListener listener) {
        AnimatedNode animatedNode = this.mAnimatedNodes.get(tag);
        if (animatedNode == null || !(animatedNode instanceof ValueAnimatedNode)) {
            throw new JSApplicationIllegalArgumentException("startListeningToAnimatedNodeValue: Animated node [" + tag + "] does not exist, or is not a 'value' node");
        }
        ((ValueAnimatedNode) animatedNode).setValueListener(listener);
    }

    public void stopListeningToAnimatedNodeValue(int tag) {
        AnimatedNode animatedNode = this.mAnimatedNodes.get(tag);
        if (animatedNode == null || !(animatedNode instanceof ValueAnimatedNode)) {
            throw new JSApplicationIllegalArgumentException("startListeningToAnimatedNodeValue: Animated node [" + tag + "] does not exist, or is not a 'value' node");
        }
        ((ValueAnimatedNode) animatedNode).setValueListener(null);
    }

    public void setAnimatedNodeValue(int tag, double value) {
        AnimatedNode animatedNode = this.mAnimatedNodes.get(tag);
        if (animatedNode == null || !(animatedNode instanceof ValueAnimatedNode)) {
            throw new JSApplicationIllegalArgumentException("setAnimatedNodeValue: Animated node [" + tag + "] does not exist, or is not a 'value' node");
        }
        stopAnimationsForNode(animatedNode);
        ((ValueAnimatedNode) animatedNode).mValue = value;
        this.mUpdatedNodes.put(tag, animatedNode);
    }

    public void setAnimatedNodeOffset(int tag, double offset) {
        AnimatedNode animatedNode = this.mAnimatedNodes.get(tag);
        if (animatedNode == null || !(animatedNode instanceof ValueAnimatedNode)) {
            throw new JSApplicationIllegalArgumentException("setAnimatedNodeOffset: Animated node [" + tag + "] does not exist, or is not a 'value' node");
        }
        ((ValueAnimatedNode) animatedNode).mOffset = offset;
        this.mUpdatedNodes.put(tag, animatedNode);
    }

    public void flattenAnimatedNodeOffset(int tag) {
        AnimatedNode animatedNode = this.mAnimatedNodes.get(tag);
        if (animatedNode == null || !(animatedNode instanceof ValueAnimatedNode)) {
            throw new JSApplicationIllegalArgumentException("flattenAnimatedNodeOffset: Animated node [" + tag + "] does not exist, or is not a 'value' node");
        }
        ((ValueAnimatedNode) animatedNode).flattenOffset();
    }

    public void extractAnimatedNodeOffset(int tag) {
        AnimatedNode animatedNode = this.mAnimatedNodes.get(tag);
        if (animatedNode == null || !(animatedNode instanceof ValueAnimatedNode)) {
            throw new JSApplicationIllegalArgumentException("extractAnimatedNodeOffset: Animated node [" + tag + "] does not exist, or is not a 'value' node");
        }
        ((ValueAnimatedNode) animatedNode).extractOffset();
    }

    public void startAnimatingNode(int animationId, int animatedNodeTag, ReadableMap animationConfig, Callback endCallback) {
        AnimationDriver animationDriver;
        AnimatedNode animatedNode = this.mAnimatedNodes.get(animatedNodeTag);
        if (animatedNode == null) {
            throw new JSApplicationIllegalArgumentException("startAnimatingNode: Animated node [" + animatedNodeTag + "] does not exist");
        } else if (!(animatedNode instanceof ValueAnimatedNode)) {
            throw new JSApplicationIllegalArgumentException("startAnimatingNode: Animated node [" + animatedNodeTag + "] should be of type " + ValueAnimatedNode.class.getName());
        } else {
            AnimationDriver animationDriver2 = this.mActiveAnimations.get(animationId);
            if (animationDriver2 != null) {
                animationDriver2.resetConfig(animationConfig);
                return;
            }
            String string = animationConfig.getString("type");
            if ("frames".equals(string)) {
                animationDriver = new FrameBasedAnimationDriver(animationConfig);
            } else if ("spring".equals(string)) {
                animationDriver = new SpringAnimation(animationConfig);
            } else if ("decay".equals(string)) {
                animationDriver = new DecayAnimation(animationConfig);
            } else {
                throw new JSApplicationIllegalArgumentException("startAnimatingNode: Unsupported animation type [" + animatedNodeTag + "]: " + string);
            }
            animationDriver.mId = animationId;
            animationDriver.mEndCallback = endCallback;
            animationDriver.mAnimatedValue = (ValueAnimatedNode) animatedNode;
            this.mActiveAnimations.put(animationId, animationDriver);
        }
    }

    private void stopAnimationsForNode(AnimatedNode animatedNode) {
        int i = 0;
        while (i < this.mActiveAnimations.size()) {
            AnimationDriver valueAt = this.mActiveAnimations.valueAt(i);
            if (animatedNode.equals(valueAt.mAnimatedValue)) {
                if (valueAt.mEndCallback != null) {
                    WritableMap createMap = Arguments.createMap();
                    createMap.putBoolean("finished", false);
                    valueAt.mEndCallback.invoke(createMap);
                }
                this.mActiveAnimations.removeAt(i);
                i--;
            }
            i++;
        }
    }

    public void stopAnimation(int animationId) {
        for (int i = 0; i < this.mActiveAnimations.size(); i++) {
            AnimationDriver valueAt = this.mActiveAnimations.valueAt(i);
            if (valueAt.mId == animationId) {
                if (valueAt.mEndCallback != null) {
                    WritableMap createMap = Arguments.createMap();
                    createMap.putBoolean("finished", false);
                    valueAt.mEndCallback.invoke(createMap);
                }
                this.mActiveAnimations.removeAt(i);
                return;
            }
        }
    }

    public void connectAnimatedNodes(int parentNodeTag, int childNodeTag) {
        AnimatedNode animatedNode = this.mAnimatedNodes.get(parentNodeTag);
        if (animatedNode == null) {
            throw new JSApplicationIllegalArgumentException("connectAnimatedNodes: Animated node with tag (parent) [" + parentNodeTag + "] does not exist");
        }
        AnimatedNode animatedNode2 = this.mAnimatedNodes.get(childNodeTag);
        if (animatedNode2 == null) {
            throw new JSApplicationIllegalArgumentException("connectAnimatedNodes: Animated node with tag (child) [" + childNodeTag + "] does not exist");
        }
        animatedNode.addChild(animatedNode2);
        this.mUpdatedNodes.put(childNodeTag, animatedNode2);
    }

    public void disconnectAnimatedNodes(int parentNodeTag, int childNodeTag) {
        AnimatedNode animatedNode = this.mAnimatedNodes.get(parentNodeTag);
        if (animatedNode == null) {
            throw new JSApplicationIllegalArgumentException("disconnectAnimatedNodes: Animated node with tag (parent) [" + parentNodeTag + "] does not exist");
        }
        AnimatedNode animatedNode2 = this.mAnimatedNodes.get(childNodeTag);
        if (animatedNode2 == null) {
            throw new JSApplicationIllegalArgumentException("disconnectAnimatedNodes: Animated node with tag (child) [" + childNodeTag + "] does not exist");
        }
        animatedNode.removeChild(animatedNode2);
        this.mUpdatedNodes.put(childNodeTag, animatedNode2);
    }

    public void connectAnimatedNodeToView(int animatedNodeTag, int viewTag) {
        AnimatedNode animatedNode = this.mAnimatedNodes.get(animatedNodeTag);
        if (animatedNode == null) {
            throw new JSApplicationIllegalArgumentException("connectAnimatedNodeToView: Animated node with tag [" + animatedNodeTag + "] does not exist");
        } else if (!(animatedNode instanceof PropsAnimatedNode)) {
            throw new JSApplicationIllegalArgumentException("connectAnimatedNodeToView: Animated node connected to view [" + viewTag + "] should be of type " + PropsAnimatedNode.class.getName());
        } else {
            ReactApplicationContext reactApplicationContext = this.mReactApplicationContext;
            if (reactApplicationContext == null) {
                throw new IllegalStateException("connectAnimatedNodeToView: Animated node could not be connected, no ReactApplicationContext: " + viewTag);
            }
            UIManager uIManagerForReactTag = UIManagerHelper.getUIManagerForReactTag(reactApplicationContext, viewTag);
            if (uIManagerForReactTag == null) {
                ReactSoftExceptionLogger.logSoftException(TAG, new ReactNoCrashSoftException("connectAnimatedNodeToView: Animated node could not be connected to UIManager - uiManager disappeared for tag: " + viewTag));
                return;
            }
            ((PropsAnimatedNode) animatedNode).connectToView(viewTag, uIManagerForReactTag);
            this.mUpdatedNodes.put(animatedNodeTag, animatedNode);
        }
    }

    public void disconnectAnimatedNodeFromView(int animatedNodeTag, int viewTag) {
        AnimatedNode animatedNode = this.mAnimatedNodes.get(animatedNodeTag);
        if (animatedNode == null) {
            throw new JSApplicationIllegalArgumentException("disconnectAnimatedNodeFromView: Animated node with tag [" + animatedNodeTag + "] does not exist");
        } else if (!(animatedNode instanceof PropsAnimatedNode)) {
            throw new JSApplicationIllegalArgumentException("disconnectAnimatedNodeFromView: Animated node connected to view [" + viewTag + "] should be of type " + PropsAnimatedNode.class.getName());
        } else {
            ((PropsAnimatedNode) animatedNode).disconnectFromView(viewTag);
        }
    }

    public void getValue(int tag, Callback callback) {
        AnimatedNode animatedNode = this.mAnimatedNodes.get(tag);
        if (animatedNode != null && (animatedNode instanceof ValueAnimatedNode)) {
            callback.invoke(Double.valueOf(((ValueAnimatedNode) animatedNode).getValue()));
            return;
        }
        throw new JSApplicationIllegalArgumentException("getValue: Animated node with tag [" + tag + "] does not exist or is not a 'value' node");
    }

    public void restoreDefaultValues(int animatedNodeTag) {
        AnimatedNode animatedNode = this.mAnimatedNodes.get(animatedNodeTag);
        if (animatedNode == null) {
            return;
        }
        if (!(animatedNode instanceof PropsAnimatedNode)) {
            throw new JSApplicationIllegalArgumentException("Animated node connected to view [?] should be of type " + PropsAnimatedNode.class.getName());
        }
        ((PropsAnimatedNode) animatedNode).restoreDefaultValues();
    }

    public void addAnimatedEventToView(int viewTag, String eventName, ReadableMap eventMapping) {
        int i = eventMapping.getInt("animatedValueTag");
        AnimatedNode animatedNode = this.mAnimatedNodes.get(i);
        if (animatedNode == null) {
            throw new JSApplicationIllegalArgumentException("addAnimatedEventToView: Animated node with tag [" + i + "] does not exist");
        } else if (!(animatedNode instanceof ValueAnimatedNode)) {
            throw new JSApplicationIllegalArgumentException("addAnimatedEventToView: Animated node on view [" + viewTag + "] connected to event (" + eventName + ") should be of type " + ValueAnimatedNode.class.getName());
        } else {
            ReadableArray array = eventMapping.getArray("nativeEventPath");
            ArrayList arrayList = new ArrayList(array.size());
            for (int i2 = 0; i2 < array.size(); i2++) {
                arrayList.add(array.getString(i2));
            }
            EventAnimationDriver eventAnimationDriver = new EventAnimationDriver(arrayList, (ValueAnimatedNode) animatedNode);
            String str = viewTag + eventName;
            if (this.mEventDrivers.containsKey(str)) {
                this.mEventDrivers.get(str).add(eventAnimationDriver);
                return;
            }
            ArrayList arrayList2 = new ArrayList(1);
            arrayList2.add(eventAnimationDriver);
            this.mEventDrivers.put(str, arrayList2);
        }
    }

    public void removeAnimatedEventFromView(int viewTag, String eventName, int animatedValueTag) {
        String str = viewTag + eventName;
        if (this.mEventDrivers.containsKey(str)) {
            List<EventAnimationDriver> list = this.mEventDrivers.get(str);
            if (list.size() == 1) {
                this.mEventDrivers.remove(viewTag + eventName);
                return;
            }
            ListIterator<EventAnimationDriver> listIterator = list.listIterator();
            while (listIterator.hasNext()) {
                if (listIterator.next().mValueNode.mTag == animatedValueTag) {
                    listIterator.remove();
                    return;
                }
            }
        }
    }

    @Override // com.facebook.react.uimanager.events.EventDispatcherListener
    public void onEventDispatch(final Event event) {
        if (UiThreadUtil.isOnUiThread()) {
            handleEvent(event);
        } else {
            UiThreadUtil.runOnUiThread(new Runnable() { // from class: com.facebook.react.animated.NativeAnimatedNodesManager.2
                @Override // java.lang.Runnable
                public void run() {
                    NativeAnimatedNodesManager.this.handleEvent(event);
                }
            });
        }
    }

    public void handleEvent(Event event) {
        ReactApplicationContext reactApplicationContext;
        UIManager uIManager;
        if (this.mEventDrivers.isEmpty() || (reactApplicationContext = this.mReactApplicationContext) == null || (uIManager = UIManagerHelper.getUIManager(reactApplicationContext, event.getUIManagerType())) == null) {
            return;
        }
        String resolveCustomDirectEventName = uIManager.resolveCustomDirectEventName(event.getEventName());
        if (resolveCustomDirectEventName == null) {
            resolveCustomDirectEventName = "";
        }
        Map<String, List<EventAnimationDriver>> map = this.mEventDrivers;
        List<EventAnimationDriver> list = map.get(event.getViewTag() + resolveCustomDirectEventName);
        if (list == null) {
            return;
        }
        for (EventAnimationDriver eventAnimationDriver : list) {
            stopAnimationsForNode(eventAnimationDriver.mValueNode);
            event.dispatch(eventAnimationDriver);
            this.mRunUpdateNodeList.add(eventAnimationDriver.mValueNode);
        }
        updateNodes(this.mRunUpdateNodeList);
        this.mRunUpdateNodeList.clear();
    }

    public void runUpdates(long frameTimeNanos) {
        UiThreadUtil.assertOnUiThread();
        for (int i = 0; i < this.mUpdatedNodes.size(); i++) {
            this.mRunUpdateNodeList.add(this.mUpdatedNodes.valueAt(i));
        }
        this.mUpdatedNodes.clear();
        boolean z = false;
        for (int i2 = 0; i2 < this.mActiveAnimations.size(); i2++) {
            AnimationDriver valueAt = this.mActiveAnimations.valueAt(i2);
            valueAt.runAnimationStep(frameTimeNanos);
            this.mRunUpdateNodeList.add(valueAt.mAnimatedValue);
            if (valueAt.mHasFinished) {
                z = true;
            }
        }
        updateNodes(this.mRunUpdateNodeList);
        this.mRunUpdateNodeList.clear();
        if (z) {
            for (int size = this.mActiveAnimations.size() - 1; size >= 0; size--) {
                AnimationDriver valueAt2 = this.mActiveAnimations.valueAt(size);
                if (valueAt2.mHasFinished) {
                    if (valueAt2.mEndCallback != null) {
                        WritableMap createMap = Arguments.createMap();
                        createMap.putBoolean("finished", true);
                        valueAt2.mEndCallback.invoke(createMap);
                    }
                    this.mActiveAnimations.removeAt(size);
                }
            }
        }
    }

    private void updateNodes(List<AnimatedNode> nodes) {
        int i = this.mAnimatedGraphBFSColor + 1;
        this.mAnimatedGraphBFSColor = i;
        if (i == 0) {
            this.mAnimatedGraphBFSColor = i + 1;
        }
        ArrayDeque arrayDeque = new ArrayDeque();
        int i2 = 0;
        for (AnimatedNode animatedNode : nodes) {
            int i3 = animatedNode.mBFSColor;
            int i4 = this.mAnimatedGraphBFSColor;
            if (i3 != i4) {
                animatedNode.mBFSColor = i4;
                i2++;
                arrayDeque.add(animatedNode);
            }
        }
        while (!arrayDeque.isEmpty()) {
            AnimatedNode animatedNode2 = (AnimatedNode) arrayDeque.poll();
            if (animatedNode2.mChildren != null) {
                for (int i5 = 0; i5 < animatedNode2.mChildren.size(); i5++) {
                    AnimatedNode animatedNode3 = animatedNode2.mChildren.get(i5);
                    animatedNode3.mActiveIncomingNodes++;
                    int i6 = animatedNode3.mBFSColor;
                    int i7 = this.mAnimatedGraphBFSColor;
                    if (i6 != i7) {
                        animatedNode3.mBFSColor = i7;
                        i2++;
                        arrayDeque.add(animatedNode3);
                    }
                }
            }
        }
        int i8 = this.mAnimatedGraphBFSColor + 1;
        this.mAnimatedGraphBFSColor = i8;
        if (i8 == 0) {
            this.mAnimatedGraphBFSColor = i8 + 1;
        }
        int i9 = 0;
        for (AnimatedNode animatedNode4 : nodes) {
            if (animatedNode4.mActiveIncomingNodes == 0) {
                int i10 = animatedNode4.mBFSColor;
                int i11 = this.mAnimatedGraphBFSColor;
                if (i10 != i11) {
                    animatedNode4.mBFSColor = i11;
                    i9++;
                    arrayDeque.add(animatedNode4);
                }
            }
        }
        int i12 = 0;
        while (!arrayDeque.isEmpty()) {
            AnimatedNode animatedNode5 = (AnimatedNode) arrayDeque.poll();
            try {
                animatedNode5.update();
                if (animatedNode5 instanceof PropsAnimatedNode) {
                    ((PropsAnimatedNode) animatedNode5).updateView();
                }
            } catch (JSApplicationCausedNativeException e) {
                FLog.e(TAG, "Native animation workaround, frame lost as result of race condition", e);
            }
            if (animatedNode5 instanceof ValueAnimatedNode) {
                ((ValueAnimatedNode) animatedNode5).onValueUpdate();
            }
            if (animatedNode5.mChildren != null) {
                for (int i13 = 0; i13 < animatedNode5.mChildren.size(); i13++) {
                    AnimatedNode animatedNode6 = animatedNode5.mChildren.get(i13);
                    animatedNode6.mActiveIncomingNodes--;
                    if (animatedNode6.mBFSColor != this.mAnimatedGraphBFSColor && animatedNode6.mActiveIncomingNodes == 0) {
                        animatedNode6.mBFSColor = this.mAnimatedGraphBFSColor;
                        i9++;
                        arrayDeque.add(animatedNode6);
                    } else if (animatedNode6.mBFSColor == this.mAnimatedGraphBFSColor) {
                        i12++;
                    }
                }
            }
        }
        if (i2 != i9) {
            if (this.mWarnedAboutGraphTraversal) {
                return;
            }
            this.mWarnedAboutGraphTraversal = true;
            FLog.e(TAG, "Detected animation cycle or disconnected graph. ");
            for (AnimatedNode animatedNode7 : nodes) {
                FLog.e(TAG, animatedNode7.prettyPrintWithChildren());
            }
            IllegalStateException illegalStateException = new IllegalStateException("Looks like animated nodes graph has " + (i12 > 0 ? "cycles (" + i12 + ")" : "disconnected regions") + ", there are " + i2 + " but toposort visited only " + i9);
            boolean z = this.mEventListenerInitializedForFabric;
            if (z && i12 == 0) {
                ReactSoftExceptionLogger.logSoftException(TAG, new ReactNoCrashSoftException(illegalStateException));
                return;
            } else if (z) {
                ReactSoftExceptionLogger.logSoftException(TAG, new ReactNoCrashSoftException(illegalStateException));
                return;
            } else {
                throw illegalStateException;
            }
        }
        this.mWarnedAboutGraphTraversal = false;
    }
}
