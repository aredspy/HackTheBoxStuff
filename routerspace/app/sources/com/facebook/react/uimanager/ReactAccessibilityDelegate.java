package com.facebook.react.uimanager;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.style.URLSpan;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import androidx.autofill.HintConstants;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import com.facebook.react.R;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Dynamic;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactNoCrashSoftException;
import com.facebook.react.bridge.ReactSoftExceptionLogger;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.UIManager;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.EventDispatcher;
import java.util.HashMap;
/* loaded from: classes.dex */
public class ReactAccessibilityDelegate extends AccessibilityDelegateCompat {
    private static final int SEND_EVENT = 1;
    private static final String STATE_CHECKED = "checked";
    private static final String STATE_DISABLED = "disabled";
    private static final String STATE_SELECTED = "selected";
    private static final String TAG = "ReactAccessibilityDelegate";
    private static final int TIMEOUT_SEND_ACCESSIBILITY_EVENT = 200;
    public static final String TOP_ACCESSIBILITY_ACTION_EVENT = "topAccessibilityAction";
    public static final HashMap<String, Integer> sActionIdMap;
    private static int sCounter = 1056964608;
    private final HashMap<Integer, String> mAccessibilityActionsMap = new HashMap<>();
    private Handler mHandler = new Handler() { // from class: com.facebook.react.uimanager.ReactAccessibilityDelegate.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            ((View) msg.obj).sendAccessibilityEvent(4);
        }
    };

    static {
        HashMap<String, Integer> hashMap = new HashMap<>();
        sActionIdMap = hashMap;
        hashMap.put("activate", Integer.valueOf(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK.getId()));
        hashMap.put("longpress", Integer.valueOf(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_LONG_CLICK.getId()));
        hashMap.put("increment", Integer.valueOf(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_FORWARD.getId()));
        hashMap.put("decrement", Integer.valueOf(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_BACKWARD.getId()));
    }

    private void scheduleAccessibilityEventSender(View host) {
        if (this.mHandler.hasMessages(1, host)) {
            this.mHandler.removeMessages(1, host);
        }
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(1, host), 200L);
    }

    /* renamed from: com.facebook.react.uimanager.ReactAccessibilityDelegate$3 */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$com$facebook$react$uimanager$ReactAccessibilityDelegate$AccessibilityRole;

        static {
            int[] iArr = new int[AccessibilityRole.values().length];
            $SwitchMap$com$facebook$react$uimanager$ReactAccessibilityDelegate$AccessibilityRole = iArr;
            try {
                iArr[AccessibilityRole.BUTTON.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$facebook$react$uimanager$ReactAccessibilityDelegate$AccessibilityRole[AccessibilityRole.TOGGLEBUTTON.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$facebook$react$uimanager$ReactAccessibilityDelegate$AccessibilityRole[AccessibilityRole.SEARCH.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$facebook$react$uimanager$ReactAccessibilityDelegate$AccessibilityRole[AccessibilityRole.IMAGE.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$facebook$react$uimanager$ReactAccessibilityDelegate$AccessibilityRole[AccessibilityRole.IMAGEBUTTON.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$facebook$react$uimanager$ReactAccessibilityDelegate$AccessibilityRole[AccessibilityRole.KEYBOARDKEY.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$facebook$react$uimanager$ReactAccessibilityDelegate$AccessibilityRole[AccessibilityRole.TEXT.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$facebook$react$uimanager$ReactAccessibilityDelegate$AccessibilityRole[AccessibilityRole.ADJUSTABLE.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$com$facebook$react$uimanager$ReactAccessibilityDelegate$AccessibilityRole[AccessibilityRole.CHECKBOX.ordinal()] = 9;
            } catch (NoSuchFieldError unused9) {
            }
            try {
                $SwitchMap$com$facebook$react$uimanager$ReactAccessibilityDelegate$AccessibilityRole[AccessibilityRole.RADIO.ordinal()] = 10;
            } catch (NoSuchFieldError unused10) {
            }
            try {
                $SwitchMap$com$facebook$react$uimanager$ReactAccessibilityDelegate$AccessibilityRole[AccessibilityRole.SPINBUTTON.ordinal()] = 11;
            } catch (NoSuchFieldError unused11) {
            }
            try {
                $SwitchMap$com$facebook$react$uimanager$ReactAccessibilityDelegate$AccessibilityRole[AccessibilityRole.SWITCH.ordinal()] = 12;
            } catch (NoSuchFieldError unused12) {
            }
            try {
                $SwitchMap$com$facebook$react$uimanager$ReactAccessibilityDelegate$AccessibilityRole[AccessibilityRole.LIST.ordinal()] = 13;
            } catch (NoSuchFieldError unused13) {
            }
            try {
                $SwitchMap$com$facebook$react$uimanager$ReactAccessibilityDelegate$AccessibilityRole[AccessibilityRole.NONE.ordinal()] = 14;
            } catch (NoSuchFieldError unused14) {
            }
            try {
                $SwitchMap$com$facebook$react$uimanager$ReactAccessibilityDelegate$AccessibilityRole[AccessibilityRole.LINK.ordinal()] = 15;
            } catch (NoSuchFieldError unused15) {
            }
            try {
                $SwitchMap$com$facebook$react$uimanager$ReactAccessibilityDelegate$AccessibilityRole[AccessibilityRole.SUMMARY.ordinal()] = 16;
            } catch (NoSuchFieldError unused16) {
            }
            try {
                $SwitchMap$com$facebook$react$uimanager$ReactAccessibilityDelegate$AccessibilityRole[AccessibilityRole.HEADER.ordinal()] = 17;
            } catch (NoSuchFieldError unused17) {
            }
            try {
                $SwitchMap$com$facebook$react$uimanager$ReactAccessibilityDelegate$AccessibilityRole[AccessibilityRole.ALERT.ordinal()] = 18;
            } catch (NoSuchFieldError unused18) {
            }
            try {
                $SwitchMap$com$facebook$react$uimanager$ReactAccessibilityDelegate$AccessibilityRole[AccessibilityRole.COMBOBOX.ordinal()] = 19;
            } catch (NoSuchFieldError unused19) {
            }
            try {
                $SwitchMap$com$facebook$react$uimanager$ReactAccessibilityDelegate$AccessibilityRole[AccessibilityRole.MENU.ordinal()] = 20;
            } catch (NoSuchFieldError unused20) {
            }
            try {
                $SwitchMap$com$facebook$react$uimanager$ReactAccessibilityDelegate$AccessibilityRole[AccessibilityRole.MENUBAR.ordinal()] = 21;
            } catch (NoSuchFieldError unused21) {
            }
            try {
                $SwitchMap$com$facebook$react$uimanager$ReactAccessibilityDelegate$AccessibilityRole[AccessibilityRole.MENUITEM.ordinal()] = 22;
            } catch (NoSuchFieldError unused22) {
            }
            try {
                $SwitchMap$com$facebook$react$uimanager$ReactAccessibilityDelegate$AccessibilityRole[AccessibilityRole.PROGRESSBAR.ordinal()] = 23;
            } catch (NoSuchFieldError unused23) {
            }
            try {
                $SwitchMap$com$facebook$react$uimanager$ReactAccessibilityDelegate$AccessibilityRole[AccessibilityRole.RADIOGROUP.ordinal()] = 24;
            } catch (NoSuchFieldError unused24) {
            }
            try {
                $SwitchMap$com$facebook$react$uimanager$ReactAccessibilityDelegate$AccessibilityRole[AccessibilityRole.SCROLLBAR.ordinal()] = 25;
            } catch (NoSuchFieldError unused25) {
            }
            try {
                $SwitchMap$com$facebook$react$uimanager$ReactAccessibilityDelegate$AccessibilityRole[AccessibilityRole.TAB.ordinal()] = 26;
            } catch (NoSuchFieldError unused26) {
            }
            try {
                $SwitchMap$com$facebook$react$uimanager$ReactAccessibilityDelegate$AccessibilityRole[AccessibilityRole.TABLIST.ordinal()] = 27;
            } catch (NoSuchFieldError unused27) {
            }
            try {
                $SwitchMap$com$facebook$react$uimanager$ReactAccessibilityDelegate$AccessibilityRole[AccessibilityRole.TIMER.ordinal()] = 28;
            } catch (NoSuchFieldError unused28) {
            }
            try {
                $SwitchMap$com$facebook$react$uimanager$ReactAccessibilityDelegate$AccessibilityRole[AccessibilityRole.TOOLBAR.ordinal()] = 29;
            } catch (NoSuchFieldError unused29) {
            }
        }
    }

    /* loaded from: classes.dex */
    public enum AccessibilityRole {
        NONE,
        BUTTON,
        TOGGLEBUTTON,
        LINK,
        SEARCH,
        IMAGE,
        IMAGEBUTTON,
        KEYBOARDKEY,
        TEXT,
        ADJUSTABLE,
        SUMMARY,
        HEADER,
        ALERT,
        CHECKBOX,
        COMBOBOX,
        MENU,
        MENUBAR,
        MENUITEM,
        PROGRESSBAR,
        RADIO,
        RADIOGROUP,
        SCROLLBAR,
        SPINBUTTON,
        SWITCH,
        TAB,
        TABLIST,
        TIMER,
        LIST,
        TOOLBAR;

        public static String getValue(AccessibilityRole role) {
            switch (AnonymousClass3.$SwitchMap$com$facebook$react$uimanager$ReactAccessibilityDelegate$AccessibilityRole[role.ordinal()]) {
                case 1:
                    return "android.widget.Button";
                case 2:
                    return "android.widget.ToggleButton";
                case 3:
                    return "android.widget.EditText";
                case 4:
                    return "android.widget.ImageView";
                case 5:
                    return "android.widget.ImageButon";
                case 6:
                    return "android.inputmethodservice.Keyboard$Key";
                case 7:
                    return "android.widget.TextView";
                case 8:
                    return "android.widget.SeekBar";
                case 9:
                    return "android.widget.CheckBox";
                case 10:
                    return "android.widget.RadioButton";
                case 11:
                    return "android.widget.SpinButton";
                case 12:
                    return "android.widget.Switch";
                case 13:
                    return "android.widget.AbsListView";
                case 14:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                    return "android.view.View";
                default:
                    throw new IllegalArgumentException("Invalid accessibility role value: " + role);
            }
        }

        public static AccessibilityRole fromValue(String value) {
            AccessibilityRole[] values;
            for (AccessibilityRole accessibilityRole : values()) {
                if (accessibilityRole.name().equalsIgnoreCase(value)) {
                    return accessibilityRole;
                }
            }
            throw new IllegalArgumentException("Invalid accessibility role value: " + value);
        }
    }

    @Override // androidx.core.view.AccessibilityDelegateCompat
    public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
        super.onInitializeAccessibilityNodeInfo(host, info);
        AccessibilityRole accessibilityRole = (AccessibilityRole) host.getTag(R.id.accessibility_role);
        if (accessibilityRole != null) {
            setRole(info, accessibilityRole, host.getContext());
        }
        ReadableMap readableMap = (ReadableMap) host.getTag(R.id.accessibility_state);
        if (readableMap != null) {
            setState(info, readableMap, host.getContext());
        }
        ReadableArray readableArray = (ReadableArray) host.getTag(R.id.accessibility_actions);
        if (readableArray != null) {
            for (int i = 0; i < readableArray.size(); i++) {
                ReadableMap map = readableArray.getMap(i);
                if (!map.hasKey(HintConstants.AUTOFILL_HINT_NAME)) {
                    throw new IllegalArgumentException("Unknown accessibility action.");
                }
                int i2 = sCounter;
                String string = map.hasKey("label") ? map.getString("label") : null;
                HashMap<String, Integer> hashMap = sActionIdMap;
                if (hashMap.containsKey(map.getString(HintConstants.AUTOFILL_HINT_NAME))) {
                    i2 = hashMap.get(map.getString(HintConstants.AUTOFILL_HINT_NAME)).intValue();
                } else {
                    sCounter++;
                }
                this.mAccessibilityActionsMap.put(Integer.valueOf(i2), map.getString(HintConstants.AUTOFILL_HINT_NAME));
                info.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(i2, string));
            }
        }
        ReadableMap readableMap2 = (ReadableMap) host.getTag(R.id.accessibility_value);
        if (readableMap2 != null && readableMap2.hasKey("min") && readableMap2.hasKey("now") && readableMap2.hasKey("max")) {
            Dynamic dynamic = readableMap2.getDynamic("min");
            Dynamic dynamic2 = readableMap2.getDynamic("now");
            Dynamic dynamic3 = readableMap2.getDynamic("max");
            if (dynamic != null && dynamic.getType() == ReadableType.Number && dynamic2 != null && dynamic2.getType() == ReadableType.Number && dynamic3 != null && dynamic3.getType() == ReadableType.Number) {
                int asInt = dynamic.asInt();
                int asInt2 = dynamic2.asInt();
                int asInt3 = dynamic3.asInt();
                if (asInt3 > asInt && asInt2 >= asInt && asInt3 >= asInt2) {
                    info.setRangeInfo(AccessibilityNodeInfoCompat.RangeInfoCompat.obtain(0, asInt, asInt3, asInt2));
                }
            }
        }
        String str = (String) host.getTag(R.id.react_test_id);
        if (str != null) {
            info.setViewIdResourceName(str);
        }
    }

    @Override // androidx.core.view.AccessibilityDelegateCompat
    public void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(host, event);
        ReadableMap readableMap = (ReadableMap) host.getTag(R.id.accessibility_value);
        if (readableMap == null || !readableMap.hasKey("min") || !readableMap.hasKey("now") || !readableMap.hasKey("max")) {
            return;
        }
        Dynamic dynamic = readableMap.getDynamic("min");
        Dynamic dynamic2 = readableMap.getDynamic("now");
        Dynamic dynamic3 = readableMap.getDynamic("max");
        if (dynamic == null || dynamic.getType() != ReadableType.Number || dynamic2 == null || dynamic2.getType() != ReadableType.Number || dynamic3 == null || dynamic3.getType() != ReadableType.Number) {
            return;
        }
        int asInt = dynamic.asInt();
        int asInt2 = dynamic2.asInt();
        int asInt3 = dynamic3.asInt();
        if (asInt3 <= asInt || asInt2 < asInt || asInt3 < asInt2) {
            return;
        }
        event.setItemCount(asInt3 - asInt);
        event.setCurrentItemIndex(asInt2);
    }

    @Override // androidx.core.view.AccessibilityDelegateCompat
    public boolean performAccessibilityAction(View host, int action, Bundle args) {
        if (this.mAccessibilityActionsMap.containsKey(Integer.valueOf(action))) {
            final WritableMap createMap = Arguments.createMap();
            createMap.putString("actionName", this.mAccessibilityActionsMap.get(Integer.valueOf(action)));
            ReactContext reactContext = (ReactContext) host.getContext();
            if (reactContext.hasActiveReactInstance()) {
                int id = host.getId();
                int surfaceId = UIManagerHelper.getSurfaceId(reactContext);
                UIManager uIManager = UIManagerHelper.getUIManager(reactContext, id);
                if (uIManager != null) {
                    ((EventDispatcher) uIManager.getEventDispatcher()).dispatchEvent(new Event(surfaceId, id) { // from class: com.facebook.react.uimanager.ReactAccessibilityDelegate.2
                        @Override // com.facebook.react.uimanager.events.Event
                        public String getEventName() {
                            return ReactAccessibilityDelegate.TOP_ACCESSIBILITY_ACTION_EVENT;
                        }

                        @Override // com.facebook.react.uimanager.events.Event
                        protected WritableMap getEventData() {
                            return createMap;
                        }
                    });
                }
            } else {
                ReactSoftExceptionLogger.logSoftException(TAG, new ReactNoCrashSoftException("Cannot get RCTEventEmitter, no CatalystInstance"));
            }
            AccessibilityRole accessibilityRole = (AccessibilityRole) host.getTag(R.id.accessibility_role);
            ReadableMap readableMap = (ReadableMap) host.getTag(R.id.accessibility_value);
            if (accessibilityRole != AccessibilityRole.ADJUSTABLE) {
                return true;
            }
            if (action != AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_FORWARD.getId() && action != AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_BACKWARD.getId()) {
                return true;
            }
            if (readableMap != null && !readableMap.hasKey("text")) {
                scheduleAccessibilityEventSender(host);
            }
            return super.performAccessibilityAction(host, action, args);
        }
        return super.performAccessibilityAction(host, action, args);
    }

    private static void setState(AccessibilityNodeInfoCompat info, ReadableMap accessibilityState, Context context) {
        ReadableMapKeySetIterator keySetIterator = accessibilityState.keySetIterator();
        while (keySetIterator.hasNextKey()) {
            String nextKey = keySetIterator.nextKey();
            Dynamic dynamic = accessibilityState.getDynamic(nextKey);
            if (nextKey.equals(STATE_SELECTED) && dynamic.getType() == ReadableType.Boolean) {
                info.setSelected(dynamic.asBoolean());
            } else if (nextKey.equals(STATE_DISABLED) && dynamic.getType() == ReadableType.Boolean) {
                info.setEnabled(!dynamic.asBoolean());
            } else if (nextKey.equals(STATE_CHECKED) && dynamic.getType() == ReadableType.Boolean) {
                boolean asBoolean = dynamic.asBoolean();
                info.setCheckable(true);
                info.setChecked(asBoolean);
                if (info.getClassName().equals(AccessibilityRole.getValue(AccessibilityRole.SWITCH))) {
                    info.setText(context.getString(asBoolean ? R.string.state_on_description : R.string.state_off_description));
                }
            }
        }
    }

    public static void setRole(AccessibilityNodeInfoCompat nodeInfo, AccessibilityRole role, final Context context) {
        if (role == null) {
            role = AccessibilityRole.NONE;
        }
        nodeInfo.setClassName(AccessibilityRole.getValue(role));
        if (role.equals(AccessibilityRole.LINK)) {
            nodeInfo.setRoleDescription(context.getString(R.string.link_description));
            if (nodeInfo.getContentDescription() != null) {
                SpannableString spannableString = new SpannableString(nodeInfo.getContentDescription());
                spannableString.setSpan(new URLSpan(""), 0, spannableString.length(), 0);
                nodeInfo.setContentDescription(spannableString);
            }
            if (nodeInfo.getText() == null) {
                return;
            }
            SpannableString spannableString2 = new SpannableString(nodeInfo.getText());
            spannableString2.setSpan(new URLSpan(""), 0, spannableString2.length(), 0);
            nodeInfo.setText(spannableString2);
        } else if (role.equals(AccessibilityRole.IMAGE)) {
            nodeInfo.setRoleDescription(context.getString(R.string.image_description));
        } else if (role.equals(AccessibilityRole.IMAGEBUTTON)) {
            nodeInfo.setRoleDescription(context.getString(R.string.imagebutton_description));
            nodeInfo.setClickable(true);
        } else if (role.equals(AccessibilityRole.BUTTON)) {
            nodeInfo.setClickable(true);
        } else if (role.equals(AccessibilityRole.TOGGLEBUTTON)) {
            nodeInfo.setClickable(true);
            nodeInfo.setCheckable(true);
        } else if (role.equals(AccessibilityRole.SUMMARY)) {
            nodeInfo.setRoleDescription(context.getString(R.string.summary_description));
        } else if (role.equals(AccessibilityRole.HEADER)) {
            nodeInfo.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(0, 1, 0, 1, true));
        } else if (role.equals(AccessibilityRole.ALERT)) {
            nodeInfo.setRoleDescription(context.getString(R.string.alert_description));
        } else if (role.equals(AccessibilityRole.COMBOBOX)) {
            nodeInfo.setRoleDescription(context.getString(R.string.combobox_description));
        } else if (role.equals(AccessibilityRole.MENU)) {
            nodeInfo.setRoleDescription(context.getString(R.string.menu_description));
        } else if (role.equals(AccessibilityRole.MENUBAR)) {
            nodeInfo.setRoleDescription(context.getString(R.string.menubar_description));
        } else if (role.equals(AccessibilityRole.MENUITEM)) {
            nodeInfo.setRoleDescription(context.getString(R.string.menuitem_description));
        } else if (role.equals(AccessibilityRole.PROGRESSBAR)) {
            nodeInfo.setRoleDescription(context.getString(R.string.progressbar_description));
        } else if (role.equals(AccessibilityRole.RADIOGROUP)) {
            nodeInfo.setRoleDescription(context.getString(R.string.radiogroup_description));
        } else if (role.equals(AccessibilityRole.SCROLLBAR)) {
            nodeInfo.setRoleDescription(context.getString(R.string.scrollbar_description));
        } else if (role.equals(AccessibilityRole.SPINBUTTON)) {
            nodeInfo.setRoleDescription(context.getString(R.string.spinbutton_description));
        } else if (role.equals(AccessibilityRole.TAB)) {
            nodeInfo.setRoleDescription(context.getString(R.string.rn_tab_description));
        } else if (role.equals(AccessibilityRole.TABLIST)) {
            nodeInfo.setRoleDescription(context.getString(R.string.tablist_description));
        } else if (role.equals(AccessibilityRole.TIMER)) {
            nodeInfo.setRoleDescription(context.getString(R.string.timer_description));
        } else if (!role.equals(AccessibilityRole.TOOLBAR)) {
        } else {
            nodeInfo.setRoleDescription(context.getString(R.string.toolbar_description));
        }
    }

    public static void setDelegate(final View view) {
        if (!ViewCompat.hasAccessibilityDelegate(view)) {
            if (view.getTag(R.id.accessibility_role) == null && view.getTag(R.id.accessibility_state) == null && view.getTag(R.id.accessibility_actions) == null && view.getTag(R.id.react_test_id) == null) {
                return;
            }
            ViewCompat.setAccessibilityDelegate(view, new ReactAccessibilityDelegate());
        }
    }
}
