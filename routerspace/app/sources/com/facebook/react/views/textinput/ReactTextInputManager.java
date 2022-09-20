package com.facebook.react.views.textinput;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.autofill.HintConstants;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import com.facebook.common.logging.FLog;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.Dynamic;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactSoftExceptionLogger;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableNativeMap;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.BaseViewManager;
import com.facebook.react.uimanager.FabricViewStateManager;
import com.facebook.react.uimanager.LayoutShadowNode;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.ReactStylesDiffMap;
import com.facebook.react.uimanager.StateWrapper;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.ViewDefaults;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.annotations.ReactPropGroup;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.views.imagehelper.ResourceDrawableIdHelper;
import com.facebook.react.views.scroll.ScrollEvent;
import com.facebook.react.views.scroll.ScrollEventType;
import com.facebook.react.views.text.DefaultStyleValuesUtil;
import com.facebook.react.views.text.ReactBaseTextShadowNode;
import com.facebook.react.views.text.ReactTextUpdate;
import com.facebook.react.views.text.ReactTextViewManagerCallback;
import com.facebook.react.views.text.TextAttributeProps;
import com.facebook.react.views.text.TextInlineImageSpan;
import com.facebook.react.views.text.TextLayoutManager;
import com.facebook.react.views.text.TextTransform;
import com.facebook.yoga.YogaConstants;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
@ReactModule(name = ReactTextInputManager.REACT_CLASS)
/* loaded from: classes.dex */
public class ReactTextInputManager extends BaseViewManager<ReactEditText, LayoutShadowNode> {
    private static final int AUTOCAPITALIZE_FLAGS = 28672;
    private static final int BLUR_TEXT_INPUT = 2;
    private static final int FOCUS_TEXT_INPUT = 1;
    private static final int IME_ACTION_ID = 1648;
    private static final int INPUT_TYPE_KEYBOARD_DECIMAL_PAD = 8194;
    private static final int INPUT_TYPE_KEYBOARD_NUMBERED = 12290;
    private static final int INPUT_TYPE_KEYBOARD_NUMBER_PAD = 2;
    private static final String KEYBOARD_TYPE_DECIMAL_PAD = "decimal-pad";
    private static final String KEYBOARD_TYPE_EMAIL_ADDRESS = "email-address";
    private static final String KEYBOARD_TYPE_NUMBER_PAD = "number-pad";
    private static final String KEYBOARD_TYPE_NUMERIC = "numeric";
    private static final String KEYBOARD_TYPE_PHONE_PAD = "phone-pad";
    private static final String KEYBOARD_TYPE_URI = "url";
    private static final String KEYBOARD_TYPE_VISIBLE_PASSWORD = "visible-password";
    private static final int PASSWORD_VISIBILITY_FLAG = 16;
    public static final String REACT_CLASS = "AndroidTextInput";
    private static final int SET_MOST_RECENT_EVENT_COUNT = 3;
    private static final int SET_TEXT_AND_SELECTION = 4;
    public static final String TAG = "ReactTextInputManager";
    private static final int UNSET = -1;
    protected ReactTextViewManagerCallback mReactTextViewManagerCallback;
    private static final int[] SPACING_TYPES = {8, 0, 2, 1, 3};
    private static final Map<String, String> REACT_PROPS_AUTOFILL_HINTS_MAP = new HashMap<String, String>() { // from class: com.facebook.react.views.textinput.ReactTextInputManager.1
        {
            put("birthdate-day", HintConstants.AUTOFILL_HINT_BIRTH_DATE_DAY);
            put("birthdate-full", HintConstants.AUTOFILL_HINT_BIRTH_DATE_FULL);
            put("birthdate-month", HintConstants.AUTOFILL_HINT_BIRTH_DATE_MONTH);
            put("birthdate-year", HintConstants.AUTOFILL_HINT_BIRTH_DATE_YEAR);
            put("cc-csc", HintConstants.AUTOFILL_HINT_CREDIT_CARD_SECURITY_CODE);
            put("cc-exp", HintConstants.AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_DATE);
            put("cc-exp-day", HintConstants.AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_DAY);
            put("cc-exp-month", HintConstants.AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_MONTH);
            put("cc-exp-year", HintConstants.AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_YEAR);
            put("cc-number", HintConstants.AUTOFILL_HINT_CREDIT_CARD_NUMBER);
            put(NotificationCompat.CATEGORY_EMAIL, HintConstants.AUTOFILL_HINT_EMAIL_ADDRESS);
            put(HintConstants.AUTOFILL_HINT_GENDER, HintConstants.AUTOFILL_HINT_GENDER);
            put(HintConstants.AUTOFILL_HINT_NAME, HintConstants.AUTOFILL_HINT_PERSON_NAME);
            put("name-family", HintConstants.AUTOFILL_HINT_PERSON_NAME_FAMILY);
            put("name-given", HintConstants.AUTOFILL_HINT_PERSON_NAME_GIVEN);
            put("name-middle", HintConstants.AUTOFILL_HINT_PERSON_NAME_MIDDLE);
            put("name-middle-initial", HintConstants.AUTOFILL_HINT_PERSON_NAME_MIDDLE_INITIAL);
            put("name-prefix", HintConstants.AUTOFILL_HINT_PERSON_NAME_PREFIX);
            put("name-suffix", HintConstants.AUTOFILL_HINT_PERSON_NAME_SUFFIX);
            put(HintConstants.AUTOFILL_HINT_PASSWORD, HintConstants.AUTOFILL_HINT_PASSWORD);
            put("password-new", HintConstants.AUTOFILL_HINT_NEW_PASSWORD);
            put("postal-address", HintConstants.AUTOFILL_HINT_POSTAL_ADDRESS);
            put("postal-address-country", HintConstants.AUTOFILL_HINT_POSTAL_ADDRESS_COUNTRY);
            put("postal-address-extended", HintConstants.AUTOFILL_HINT_POSTAL_ADDRESS_EXTENDED_ADDRESS);
            put("postal-address-extended-postal-code", HintConstants.AUTOFILL_HINT_POSTAL_ADDRESS_EXTENDED_POSTAL_CODE);
            put("postal-address-locality", HintConstants.AUTOFILL_HINT_POSTAL_ADDRESS_LOCALITY);
            put("postal-address-region", HintConstants.AUTOFILL_HINT_POSTAL_ADDRESS_REGION);
            put("postal-code", HintConstants.AUTOFILL_HINT_POSTAL_CODE);
            put("street-address", HintConstants.AUTOFILL_HINT_POSTAL_ADDRESS_STREET_ADDRESS);
            put("sms-otp", HintConstants.AUTOFILL_HINT_SMS_OTP);
            put("tel", HintConstants.AUTOFILL_HINT_PHONE_NUMBER);
            put("tel-country-code", HintConstants.AUTOFILL_HINT_PHONE_COUNTRY_CODE);
            put("tel-national", HintConstants.AUTOFILL_HINT_PHONE_NATIONAL);
            put("tel-device", HintConstants.AUTOFILL_HINT_PHONE_NUMBER_DEVICE);
            put(HintConstants.AUTOFILL_HINT_USERNAME, HintConstants.AUTOFILL_HINT_USERNAME);
            put("username-new", HintConstants.AUTOFILL_HINT_NEW_USERNAME);
        }
    };
    private static final InputFilter[] EMPTY_FILTERS = new InputFilter[0];
    private static final String[] DRAWABLE_FIELDS = {"mCursorDrawable", "mSelectHandleLeft", "mSelectHandleRight", "mSelectHandleCenter"};
    private static final String[] DRAWABLE_RESOURCES = {"mCursorDrawableRes", "mTextSelectHandleLeftRes", "mTextSelectHandleRightRes", "mTextSelectHandleRes"};

    @Override // com.facebook.react.uimanager.ViewManager, com.facebook.react.bridge.NativeModule
    public String getName() {
        return REACT_CLASS;
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public ReactEditText createViewInstance(ThemedReactContext context) {
        ReactEditText reactEditText = new ReactEditText(context);
        reactEditText.setInputType(reactEditText.getInputType() & (-131073));
        reactEditText.setReturnKeyType("done");
        return reactEditText;
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public ReactBaseTextShadowNode createShadowNodeInstance() {
        return new ReactTextInputShadowNode();
    }

    public ReactBaseTextShadowNode createShadowNodeInstance(ReactTextViewManagerCallback reactTextViewManagerCallback) {
        return new ReactTextInputShadowNode(reactTextViewManagerCallback);
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public Class<? extends LayoutShadowNode> getShadowNodeClass() {
        return ReactTextInputShadowNode.class;
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public Map<String, Object> getExportedCustomBubblingEventTypeConstants() {
        return MapBuilder.builder().put("topSubmitEditing", MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", "onSubmitEditing", "captured", "onSubmitEditingCapture"))).put("topEndEditing", MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", "onEndEditing", "captured", "onEndEditingCapture"))).put(ReactTextInputEvent.EVENT_NAME, MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", "onTextInput", "captured", "onTextInputCapture"))).put("topFocus", MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", "onFocus", "captured", "onFocusCapture"))).put("topBlur", MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", "onBlur", "captured", "onBlurCapture"))).put(ReactTextInputKeyPressEvent.EVENT_NAME, MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", "onKeyPress", "captured", "onKeyPressCapture"))).build();
    }

    @Override // com.facebook.react.uimanager.BaseViewManager, com.facebook.react.uimanager.ViewManager
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.builder().put(ScrollEventType.getJSEventName(ScrollEventType.SCROLL), MapBuilder.of("registrationName", "onScroll")).build();
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public Map<String, Integer> getCommandsMap() {
        return MapBuilder.of("focusTextInput", 1, "blurTextInput", 2);
    }

    public void receiveCommand(ReactEditText reactEditText, int commandId, ReadableArray args) {
        if (commandId == 1) {
            receiveCommand(reactEditText, "focus", args);
        } else if (commandId == 2) {
            receiveCommand(reactEditText, "blur", args);
        } else if (commandId != 4) {
        } else {
            receiveCommand(reactEditText, "setTextAndSelection", args);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public void receiveCommand(ReactEditText reactEditText, String commandId, ReadableArray args) {
        char c;
        commandId.hashCode();
        switch (commandId.hashCode()) {
            case -1699362314:
                if (commandId.equals("blurTextInput")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 3027047:
                if (commandId.equals("blur")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 97604824:
                if (commandId.equals("focus")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 1427010500:
                if (commandId.equals("setTextAndSelection")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 1690703013:
                if (commandId.equals("focusTextInput")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
            case 1:
                reactEditText.clearFocusFromJS();
                return;
            case 2:
            case 4:
                reactEditText.requestFocusFromJS();
                return;
            case 3:
                int i = args.getInt(0);
                if (i == -1) {
                    return;
                }
                String string = args.getString(1);
                int i2 = args.getInt(2);
                int i3 = args.getInt(3);
                if (i3 == -1) {
                    i3 = i2;
                }
                reactEditText.maybeSetTextFromJS(getReactTextUpdate(string, i, i2, i3));
                reactEditText.maybeSetSelection(i, i2, i3);
                return;
            default:
                return;
        }
    }

    private ReactTextUpdate getReactTextUpdate(String text, int mostRecentEventCount, int start, int end) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append((CharSequence) TextTransform.apply(text, TextTransform.UNSET));
        return new ReactTextUpdate(spannableStringBuilder, mostRecentEventCount, false, 0.0f, 0.0f, 0.0f, 0.0f, 0, 0, 0, start, end);
    }

    public void updateExtraData(ReactEditText view, Object extraData) {
        if (extraData instanceof ReactTextUpdate) {
            ReactTextUpdate reactTextUpdate = (ReactTextUpdate) extraData;
            int paddingLeft = (int) reactTextUpdate.getPaddingLeft();
            int paddingTop = (int) reactTextUpdate.getPaddingTop();
            int paddingRight = (int) reactTextUpdate.getPaddingRight();
            int paddingBottom = (int) reactTextUpdate.getPaddingBottom();
            if (paddingLeft != -1 || paddingTop != -1 || paddingRight != -1 || paddingBottom != -1) {
                if (paddingLeft == -1) {
                    paddingLeft = view.getPaddingLeft();
                }
                if (paddingTop == -1) {
                    paddingTop = view.getPaddingTop();
                }
                if (paddingRight == -1) {
                    paddingRight = view.getPaddingRight();
                }
                if (paddingBottom == -1) {
                    paddingBottom = view.getPaddingBottom();
                }
                view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
            }
            if (reactTextUpdate.containsImages()) {
                TextInlineImageSpan.possiblyUpdateInlineImageSpans(reactTextUpdate.getText(), view);
            }
            int i = 0;
            boolean z = view.getSelectionStart() == view.getSelectionEnd();
            int selectionStart = reactTextUpdate.getSelectionStart();
            int selectionEnd = reactTextUpdate.getSelectionEnd();
            if ((selectionStart == -1 || selectionEnd == -1) && z) {
                if (view.getText() != null) {
                    i = view.getText().length();
                }
                selectionStart = reactTextUpdate.getText().length() - (i - view.getSelectionStart());
                selectionEnd = selectionStart;
            }
            view.maybeSetTextFromState(reactTextUpdate);
            view.maybeSetSelection(reactTextUpdate.getJsEventCounter(), selectionStart, selectionEnd);
        }
    }

    @ReactProp(defaultFloat = ViewDefaults.FONT_SIZE_SP, name = ViewProps.FONT_SIZE)
    public void setFontSize(ReactEditText view, float fontSize) {
        view.setFontSize(fontSize);
    }

    @ReactProp(name = ViewProps.FONT_FAMILY)
    public void setFontFamily(ReactEditText view, String fontFamily) {
        view.setFontFamily(fontFamily);
    }

    @ReactProp(defaultFloat = Float.NaN, name = ViewProps.MAX_FONT_SIZE_MULTIPLIER)
    public void setMaxFontSizeMultiplier(ReactEditText view, float maxFontSizeMultiplier) {
        view.setMaxFontSizeMultiplier(maxFontSizeMultiplier);
    }

    @ReactProp(name = ViewProps.FONT_WEIGHT)
    public void setFontWeight(ReactEditText view, String fontWeight) {
        view.setFontWeight(fontWeight);
    }

    @ReactProp(name = ViewProps.FONT_STYLE)
    public void setFontStyle(ReactEditText view, String fontStyle) {
        view.setFontStyle(fontStyle);
    }

    @ReactProp(defaultBoolean = true, name = ViewProps.INCLUDE_FONT_PADDING)
    public void setIncludeFontPadding(ReactEditText view, boolean includepad) {
        view.setIncludeFontPadding(includepad);
    }

    @ReactProp(name = "importantForAutofill")
    public void setImportantForAutofill(ReactEditText view, String value) {
        int i;
        if ("no".equals(value)) {
            i = 2;
        } else if ("noExcludeDescendants".equals(value)) {
            i = 8;
        } else if ("yes".equals(value)) {
            i = 1;
        } else {
            i = "yesExcludeDescendants".equals(value) ? 4 : 0;
        }
        setImportantForAutofill(view, i);
    }

    private void setImportantForAutofill(ReactEditText view, int mode) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        view.setImportantForAutofill(mode);
    }

    private void setAutofillHints(ReactEditText view, String... hints) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        view.setAutofillHints(hints);
    }

    @ReactProp(defaultBoolean = false, name = "onSelectionChange")
    public void setOnSelectionChange(final ReactEditText view, boolean onSelectionChange) {
        if (onSelectionChange) {
            view.setSelectionWatcher(new ReactSelectionWatcher(view));
        } else {
            view.setSelectionWatcher(null);
        }
    }

    @ReactProp(name = "blurOnSubmit")
    public void setBlurOnSubmit(ReactEditText view, Boolean blurOnSubmit) {
        view.setBlurOnSubmit(blurOnSubmit);
    }

    @ReactProp(defaultBoolean = false, name = "onContentSizeChange")
    public void setOnContentSizeChange(final ReactEditText view, boolean onContentSizeChange) {
        if (onContentSizeChange) {
            view.setContentSizeWatcher(new ReactContentSizeWatcher(view));
        } else {
            view.setContentSizeWatcher(null);
        }
    }

    @ReactProp(defaultBoolean = false, name = "onScroll")
    public void setOnScroll(final ReactEditText view, boolean onScroll) {
        if (onScroll) {
            view.setScrollWatcher(new ReactScrollWatcher(view));
        } else {
            view.setScrollWatcher(null);
        }
    }

    @ReactProp(defaultBoolean = false, name = "onKeyPress")
    public void setOnKeyPress(final ReactEditText view, boolean onKeyPress) {
        view.setOnKeyPress(onKeyPress);
    }

    @ReactProp(defaultFloat = 0.0f, name = ViewProps.LETTER_SPACING)
    public void setLetterSpacing(ReactEditText view, float letterSpacing) {
        view.setLetterSpacingPt(letterSpacing);
    }

    @ReactProp(defaultBoolean = true, name = ViewProps.ALLOW_FONT_SCALING)
    public void setAllowFontScaling(ReactEditText view, boolean allowFontScaling) {
        view.setAllowFontScaling(allowFontScaling);
    }

    @ReactProp(name = ReactTextInputShadowNode.PROP_PLACEHOLDER)
    public void setPlaceholder(ReactEditText view, String placeholder) {
        view.setHint(placeholder);
    }

    @ReactProp(customType = "Color", name = "placeholderTextColor")
    public void setPlaceholderTextColor(ReactEditText view, Integer color) {
        if (color == null) {
            view.setHintTextColor(DefaultStyleValuesUtil.getDefaultTextColorHint(view.getContext()));
        } else {
            view.setHintTextColor(color.intValue());
        }
    }

    @ReactProp(customType = "Color", name = "selectionColor")
    public void setSelectionColor(ReactEditText view, Integer color) {
        if (color == null) {
            view.setHighlightColor(DefaultStyleValuesUtil.getDefaultTextColorHighlight(view.getContext()));
        } else {
            view.setHighlightColor(color.intValue());
        }
        setCursorColor(view, color);
    }

    @ReactProp(customType = "Color", name = "cursorColor")
    public void setCursorColor(ReactEditText view, Integer color) {
        int i;
        if (color == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 29) {
            Drawable textCursorDrawable = view.getTextCursorDrawable();
            if (textCursorDrawable == null) {
                return;
            }
            textCursorDrawable.setColorFilter(new BlendModeColorFilter(color.intValue(), BlendMode.SRC_IN));
            view.setTextCursorDrawable(textCursorDrawable);
        } else if (Build.VERSION.SDK_INT == 28) {
        } else {
            int i2 = 0;
            while (true) {
                String[] strArr = DRAWABLE_RESOURCES;
                if (i2 >= strArr.length) {
                    return;
                }
                try {
                    Field declaredField = TextView.class.getDeclaredField(strArr[i2]);
                    declaredField.setAccessible(true);
                    i = declaredField.getInt(view);
                } catch (IllegalAccessException | NoSuchFieldException unused) {
                }
                if (i == 0) {
                    return;
                }
                Drawable mutate = ContextCompat.getDrawable(view.getContext(), i).mutate();
                mutate.setColorFilter(color.intValue(), PorterDuff.Mode.SRC_IN);
                Field declaredField2 = TextView.class.getDeclaredField("mEditor");
                declaredField2.setAccessible(true);
                Object obj = declaredField2.get(view);
                Field declaredField3 = obj.getClass().getDeclaredField(DRAWABLE_FIELDS[i2]);
                declaredField3.setAccessible(true);
                if (strArr[i2] == "mCursorDrawableRes") {
                    declaredField3.set(obj, new Drawable[]{mutate, mutate});
                } else {
                    declaredField3.set(obj, mutate);
                }
                i2++;
            }
        }
    }

    private static boolean shouldHideCursorForEmailTextInput() {
        return Build.VERSION.SDK_INT == 29 && Build.MANUFACTURER.toLowerCase().contains("xiaomi");
    }

    @ReactProp(defaultBoolean = false, name = "caretHidden")
    public void setCaretHidden(ReactEditText view, boolean caretHidden) {
        if (view.getStagedInputType() != 32 || !shouldHideCursorForEmailTextInput()) {
            view.setCursorVisible(!caretHidden);
        }
    }

    @ReactProp(defaultBoolean = false, name = "contextMenuHidden")
    public void setContextMenuHidden(ReactEditText view, final boolean contextMenuHidden) {
        view.setOnLongClickListener(new View.OnLongClickListener() { // from class: com.facebook.react.views.textinput.ReactTextInputManager.2
            @Override // android.view.View.OnLongClickListener
            public boolean onLongClick(View v) {
                return contextMenuHidden;
            }
        });
    }

    @ReactProp(defaultBoolean = false, name = "selectTextOnFocus")
    public void setSelectTextOnFocus(ReactEditText view, boolean selectTextOnFocus) {
        view.setSelectAllOnFocus(selectTextOnFocus);
    }

    @ReactProp(customType = "Color", name = ViewProps.COLOR)
    public void setColor(ReactEditText view, Integer color) {
        if (color == null) {
            ColorStateList defaultTextColor = DefaultStyleValuesUtil.getDefaultTextColor(view.getContext());
            if (defaultTextColor != null) {
                view.setTextColor(defaultTextColor);
                return;
            }
            Context context = view.getContext();
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Could not get default text color from View Context: ");
            sb.append(context != null ? context.getClass().getCanonicalName() : "null");
            ReactSoftExceptionLogger.logSoftException(str, new IllegalStateException(sb.toString()));
            return;
        }
        view.setTextColor(color.intValue());
    }

    @ReactProp(customType = "Color", name = "underlineColorAndroid")
    public void setUnderlineColor(ReactEditText view, Integer underlineColor) {
        Drawable background = view.getBackground();
        if (background.getConstantState() != null) {
            try {
                background = background.mutate();
            } catch (NullPointerException e) {
                FLog.e(TAG, "NullPointerException when setting underlineColorAndroid for TextInput", e);
            }
        }
        if (underlineColor == null) {
            background.clearColorFilter();
        } else {
            background.setColorFilter(underlineColor.intValue(), PorterDuff.Mode.SRC_IN);
        }
    }

    @ReactProp(name = ViewProps.TEXT_ALIGN)
    public void setTextAlign(ReactEditText view, String textAlign) {
        if ("justify".equals(textAlign)) {
            if (Build.VERSION.SDK_INT >= 26) {
                view.setJustificationMode(1);
            }
            view.setGravityHorizontal(3);
            return;
        }
        if (Build.VERSION.SDK_INT >= 26) {
            view.setJustificationMode(0);
        }
        if (textAlign == null || "auto".equals(textAlign)) {
            view.setGravityHorizontal(0);
        } else if (ViewProps.LEFT.equals(textAlign)) {
            view.setGravityHorizontal(3);
        } else if (ViewProps.RIGHT.equals(textAlign)) {
            view.setGravityHorizontal(5);
        } else if ("center".equals(textAlign)) {
            view.setGravityHorizontal(1);
        } else {
            throw new JSApplicationIllegalArgumentException("Invalid textAlign: " + textAlign);
        }
    }

    @ReactProp(name = ViewProps.TEXT_ALIGN_VERTICAL)
    public void setTextAlignVertical(ReactEditText view, String textAlignVertical) {
        if (textAlignVertical == null || "auto".equals(textAlignVertical)) {
            view.setGravityVertical(0);
        } else if (ViewProps.TOP.equals(textAlignVertical)) {
            view.setGravityVertical(48);
        } else if (ViewProps.BOTTOM.equals(textAlignVertical)) {
            view.setGravityVertical(80);
        } else if ("center".equals(textAlignVertical)) {
            view.setGravityVertical(16);
        } else {
            throw new JSApplicationIllegalArgumentException("Invalid textAlignVertical: " + textAlignVertical);
        }
    }

    @ReactProp(name = "inlineImageLeft")
    public void setInlineImageLeft(ReactEditText view, String resource) {
        view.setCompoundDrawablesWithIntrinsicBounds(ResourceDrawableIdHelper.getInstance().getResourceDrawableId(view.getContext(), resource), 0, 0, 0);
    }

    @ReactProp(name = "inlineImagePadding")
    public void setInlineImagePadding(ReactEditText view, int padding) {
        view.setCompoundDrawablePadding(padding);
    }

    @ReactProp(defaultBoolean = true, name = "editable")
    public void setEditable(ReactEditText view, boolean editable) {
        view.setEnabled(editable);
    }

    @ReactProp(defaultInt = 1, name = ViewProps.NUMBER_OF_LINES)
    public void setNumLines(ReactEditText view, int numLines) {
        view.setLines(numLines);
    }

    @ReactProp(name = "maxLength")
    public void setMaxLength(ReactEditText view, Integer maxLength) {
        InputFilter[] filters = view.getFilters();
        InputFilter[] inputFilterArr = EMPTY_FILTERS;
        if (maxLength == null) {
            if (filters.length > 0) {
                LinkedList linkedList = new LinkedList();
                for (int i = 0; i < filters.length; i++) {
                    if (!(filters[i] instanceof InputFilter.LengthFilter)) {
                        linkedList.add(filters[i]);
                    }
                }
                if (!linkedList.isEmpty()) {
                    inputFilterArr = (InputFilter[]) linkedList.toArray(new InputFilter[linkedList.size()]);
                }
            }
        } else if (filters.length > 0) {
            boolean z = false;
            for (int i2 = 0; i2 < filters.length; i2++) {
                if (filters[i2] instanceof InputFilter.LengthFilter) {
                    filters[i2] = new InputFilter.LengthFilter(maxLength.intValue());
                    z = true;
                }
            }
            if (!z) {
                InputFilter[] inputFilterArr2 = new InputFilter[filters.length + 1];
                System.arraycopy(filters, 0, inputFilterArr2, 0, filters.length);
                filters[filters.length] = new InputFilter.LengthFilter(maxLength.intValue());
                filters = inputFilterArr2;
            }
            inputFilterArr = filters;
        } else {
            inputFilterArr = new InputFilter[]{new InputFilter.LengthFilter(maxLength.intValue())};
        }
        view.setFilters(inputFilterArr);
    }

    @ReactProp(name = "autoComplete")
    public void setTextContentType(ReactEditText view, String autoComplete) {
        if (autoComplete == null) {
            setImportantForAutofill(view, 2);
        } else if ("off".equals(autoComplete)) {
            setImportantForAutofill(view, 2);
        } else {
            Map<String, String> map = REACT_PROPS_AUTOFILL_HINTS_MAP;
            if (map.containsKey(autoComplete)) {
                setAutofillHints(view, map.get(autoComplete));
                return;
            }
            throw new JSApplicationIllegalArgumentException("Invalid autoComplete: " + autoComplete);
        }
    }

    @ReactProp(name = "autoCorrect")
    public void setAutoCorrect(ReactEditText view, Boolean autoCorrect) {
        int i;
        if (autoCorrect != null) {
            i = autoCorrect.booleanValue() ? 32768 : 524288;
        } else {
            i = 0;
        }
        updateStagedInputTypeFlag(view, 557056, i);
    }

    @ReactProp(defaultBoolean = false, name = "multiline")
    public void setMultiline(ReactEditText view, boolean multiline) {
        int i = 0;
        int i2 = multiline ? 0 : 131072;
        if (multiline) {
            i = 131072;
        }
        updateStagedInputTypeFlag(view, i2, i);
    }

    @ReactProp(defaultBoolean = false, name = "secureTextEntry")
    public void setSecureTextEntry(ReactEditText view, boolean password) {
        updateStagedInputTypeFlag(view, 144, password ? 128 : 0);
        checkPasswordType(view);
    }

    @ReactProp(name = "autoCapitalize")
    public void setAutoCapitalize(ReactEditText view, Dynamic autoCapitalize) {
        int i = 16384;
        if (autoCapitalize.getType() == ReadableType.Number) {
            i = autoCapitalize.asInt();
        } else if (autoCapitalize.getType() == ReadableType.String) {
            String asString = autoCapitalize.asString();
            if (asString.equals(ViewProps.NONE)) {
                i = 0;
            } else if (asString.equals("characters")) {
                i = 4096;
            } else if (asString.equals("words")) {
                i = 8192;
            } else {
                asString.equals("sentences");
            }
        }
        updateStagedInputTypeFlag(view, AUTOCAPITALIZE_FLAGS, i);
    }

    @ReactProp(name = "keyboardType")
    public void setKeyboardType(ReactEditText view, String keyboardType) {
        int i;
        if (KEYBOARD_TYPE_NUMERIC.equalsIgnoreCase(keyboardType)) {
            i = INPUT_TYPE_KEYBOARD_NUMBERED;
        } else if (KEYBOARD_TYPE_NUMBER_PAD.equalsIgnoreCase(keyboardType)) {
            i = 2;
        } else if (KEYBOARD_TYPE_DECIMAL_PAD.equalsIgnoreCase(keyboardType)) {
            i = 8194;
        } else if (KEYBOARD_TYPE_EMAIL_ADDRESS.equalsIgnoreCase(keyboardType)) {
            i = 33;
            if (shouldHideCursorForEmailTextInput()) {
                view.setCursorVisible(false);
            }
        } else if (KEYBOARD_TYPE_PHONE_PAD.equalsIgnoreCase(keyboardType)) {
            i = 3;
        } else if (KEYBOARD_TYPE_VISIBLE_PASSWORD.equalsIgnoreCase(keyboardType)) {
            i = 144;
        } else {
            i = KEYBOARD_TYPE_URI.equalsIgnoreCase(keyboardType) ? 16 : 1;
        }
        updateStagedInputTypeFlag(view, 15, i);
        checkPasswordType(view);
    }

    @ReactProp(name = "returnKeyType")
    public void setReturnKeyType(ReactEditText view, String returnKeyType) {
        view.setReturnKeyType(returnKeyType);
    }

    @ReactProp(defaultBoolean = false, name = "disableFullscreenUI")
    public void setDisableFullscreenUI(ReactEditText view, boolean disableFullscreenUI) {
        view.setDisableFullscreenUI(disableFullscreenUI);
    }

    @ReactProp(name = "returnKeyLabel")
    public void setReturnKeyLabel(ReactEditText view, String returnKeyLabel) {
        view.setImeActionLabel(returnKeyLabel, IME_ACTION_ID);
    }

    @ReactPropGroup(defaultFloat = Float.NaN, names = {ViewProps.BORDER_RADIUS, ViewProps.BORDER_TOP_LEFT_RADIUS, ViewProps.BORDER_TOP_RIGHT_RADIUS, ViewProps.BORDER_BOTTOM_RIGHT_RADIUS, ViewProps.BORDER_BOTTOM_LEFT_RADIUS})
    public void setBorderRadius(ReactEditText view, int index, float borderRadius) {
        if (!YogaConstants.isUndefined(borderRadius)) {
            borderRadius = PixelUtil.toPixelFromDIP(borderRadius);
        }
        if (index == 0) {
            view.setBorderRadius(borderRadius);
        } else {
            view.setBorderRadius(borderRadius, index - 1);
        }
    }

    @ReactProp(name = "borderStyle")
    public void setBorderStyle(ReactEditText view, String borderStyle) {
        view.setBorderStyle(borderStyle);
    }

    @ReactProp(defaultBoolean = true, name = "showSoftInputOnFocus")
    public void showKeyboardOnFocus(ReactEditText view, boolean showKeyboardOnFocus) {
        view.setShowSoftInputOnFocus(showKeyboardOnFocus);
    }

    @ReactProp(defaultBoolean = false, name = "autoFocus")
    public void setAutoFocus(ReactEditText view, boolean autoFocus) {
        view.setAutoFocus(autoFocus);
    }

    @ReactPropGroup(defaultFloat = Float.NaN, names = {ViewProps.BORDER_WIDTH, ViewProps.BORDER_LEFT_WIDTH, ViewProps.BORDER_RIGHT_WIDTH, ViewProps.BORDER_TOP_WIDTH, ViewProps.BORDER_BOTTOM_WIDTH})
    public void setBorderWidth(ReactEditText view, int index, float width) {
        if (!YogaConstants.isUndefined(width)) {
            width = PixelUtil.toPixelFromDIP(width);
        }
        view.setBorderWidth(SPACING_TYPES[index], width);
    }

    @ReactPropGroup(customType = "Color", names = {ViewProps.BORDER_COLOR, ViewProps.BORDER_LEFT_COLOR, ViewProps.BORDER_RIGHT_COLOR, ViewProps.BORDER_TOP_COLOR, ViewProps.BORDER_BOTTOM_COLOR})
    public void setBorderColor(ReactEditText view, int index, Integer color) {
        float f = Float.NaN;
        float intValue = color == null ? Float.NaN : color.intValue() & ViewCompat.MEASURED_SIZE_MASK;
        if (color != null) {
            f = color.intValue() >>> 24;
        }
        view.setBorderColor(SPACING_TYPES[index], intValue, f);
    }

    public void onAfterUpdateTransaction(ReactEditText view) {
        super.onAfterUpdateTransaction((ReactTextInputManager) view);
        view.maybeUpdateTypeface();
        view.commitStagedInputType();
    }

    private static void checkPasswordType(ReactEditText view) {
        if ((view.getStagedInputType() & INPUT_TYPE_KEYBOARD_NUMBERED) == 0 || (view.getStagedInputType() & 128) == 0) {
            return;
        }
        updateStagedInputTypeFlag(view, 128, 16);
    }

    private static void updateStagedInputTypeFlag(ReactEditText view, int flagsToUnset, int flagsToSet) {
        view.setStagedInputType(((~flagsToUnset) & view.getStagedInputType()) | flagsToSet);
    }

    public static EventDispatcher getEventDispatcher(ReactContext reactContext, ReactEditText editText) {
        return UIManagerHelper.getEventDispatcherForReactTag(reactContext, editText.getId());
    }

    /* loaded from: classes.dex */
    public class ReactTextInputTextWatcher implements TextWatcher {
        private ReactEditText mEditText;
        private EventDispatcher mEventDispatcher;
        private String mPreviousText = null;
        private int mSurfaceId;

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable s) {
        }

        public ReactTextInputTextWatcher(final ReactContext reactContext, final ReactEditText editText) {
            ReactTextInputManager.this = this$0;
            this.mEventDispatcher = ReactTextInputManager.getEventDispatcher(reactContext, editText);
            this.mEditText = editText;
            this.mSurfaceId = UIManagerHelper.getSurfaceId(reactContext);
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            this.mPreviousText = s.toString();
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (this.mEditText.mDisableTextDiffing) {
                return;
            }
            if (count == 0 && before == 0) {
                return;
            }
            Assertions.assertNotNull(this.mPreviousText);
            String substring = s.toString().substring(start, start + count);
            int i = start + before;
            String substring2 = this.mPreviousText.substring(start, i);
            if (count == before && substring.equals(substring2)) {
                return;
            }
            if (this.mEditText.getFabricViewStateManager().hasStateWrapper()) {
                this.mEditText.getFabricViewStateManager().setState(new FabricViewStateManager.StateUpdateCallback() { // from class: com.facebook.react.views.textinput.ReactTextInputManager.ReactTextInputTextWatcher.1
                    @Override // com.facebook.react.uimanager.FabricViewStateManager.StateUpdateCallback
                    public WritableMap getStateUpdate() {
                        WritableNativeMap writableNativeMap = new WritableNativeMap();
                        writableNativeMap.putInt("mostRecentEventCount", ReactTextInputTextWatcher.this.mEditText.incrementAndGetEventCounter());
                        writableNativeMap.putInt("opaqueCacheId", ReactTextInputTextWatcher.this.mEditText.getId());
                        return writableNativeMap;
                    }
                });
            }
            this.mEventDispatcher.dispatchEvent(new ReactTextChangedEvent(this.mSurfaceId, this.mEditText.getId(), s.toString(), this.mEditText.incrementAndGetEventCounter()));
            this.mEventDispatcher.dispatchEvent(new ReactTextInputEvent(this.mSurfaceId, this.mEditText.getId(), substring, substring2, start, i));
        }
    }

    public void addEventEmitters(final ThemedReactContext reactContext, final ReactEditText editText) {
        editText.setEventDispatcher(getEventDispatcher(reactContext, editText));
        editText.addTextChangedListener(new ReactTextInputTextWatcher(reactContext, editText));
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.facebook.react.views.textinput.ReactTextInputManager.3
            @Override // android.view.View.OnFocusChangeListener
            public void onFocusChange(View v, boolean hasFocus) {
                int surfaceId = reactContext.getSurfaceId();
                EventDispatcher eventDispatcher = ReactTextInputManager.getEventDispatcher(reactContext, editText);
                if (hasFocus) {
                    eventDispatcher.dispatchEvent(new ReactTextInputFocusEvent(surfaceId, editText.getId()));
                    return;
                }
                eventDispatcher.dispatchEvent(new ReactTextInputBlurEvent(surfaceId, editText.getId()));
                eventDispatcher.dispatchEvent(new ReactTextInputEndEditingEvent(surfaceId, editText.getId(), editText.getText().toString()));
            }
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: com.facebook.react.views.textinput.ReactTextInputManager.4
            @Override // android.widget.TextView.OnEditorActionListener
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
                if ((actionId & 255) != 0 || actionId == 0) {
                    boolean blurOnSubmit = editText.getBlurOnSubmit();
                    boolean isMultiline = editText.isMultiline();
                    ReactTextInputManager.getEventDispatcher(reactContext, editText).dispatchEvent(new ReactTextInputSubmitEditingEvent(reactContext.getSurfaceId(), editText.getId(), editText.getText().toString()));
                    if (blurOnSubmit) {
                        editText.clearFocus();
                    }
                    return blurOnSubmit || !isMultiline || actionId == 5 || actionId == 7;
                }
                return true;
            }
        });
    }

    /* loaded from: classes.dex */
    private static class ReactContentSizeWatcher implements ContentSizeWatcher {
        private ReactEditText mEditText;
        private EventDispatcher mEventDispatcher;
        private int mSurfaceId;
        private int mPreviousContentWidth = 0;
        private int mPreviousContentHeight = 0;

        public ReactContentSizeWatcher(ReactEditText editText) {
            this.mEditText = editText;
            ReactContext reactContext = UIManagerHelper.getReactContext(editText);
            this.mEventDispatcher = ReactTextInputManager.getEventDispatcher(reactContext, editText);
            this.mSurfaceId = UIManagerHelper.getSurfaceId(reactContext);
        }

        @Override // com.facebook.react.views.textinput.ContentSizeWatcher
        public void onLayout() {
            if (this.mEventDispatcher == null) {
                return;
            }
            int width = this.mEditText.getWidth();
            int height = this.mEditText.getHeight();
            if (this.mEditText.getLayout() != null) {
                width = this.mEditText.getCompoundPaddingLeft() + this.mEditText.getLayout().getWidth() + this.mEditText.getCompoundPaddingRight();
                height = this.mEditText.getCompoundPaddingTop() + this.mEditText.getLayout().getHeight() + this.mEditText.getCompoundPaddingBottom();
            }
            if (width == this.mPreviousContentWidth && height == this.mPreviousContentHeight) {
                return;
            }
            this.mPreviousContentHeight = height;
            this.mPreviousContentWidth = width;
            this.mEventDispatcher.dispatchEvent(new ReactContentSizeChangedEvent(this.mSurfaceId, this.mEditText.getId(), PixelUtil.toDIPFromPixel(width), PixelUtil.toDIPFromPixel(height)));
        }
    }

    /* loaded from: classes.dex */
    private class ReactSelectionWatcher implements SelectionWatcher {
        private EventDispatcher mEventDispatcher;
        private int mPreviousSelectionEnd;
        private int mPreviousSelectionStart;
        private ReactEditText mReactEditText;
        private int mSurfaceId;

        public ReactSelectionWatcher(ReactEditText editText) {
            ReactTextInputManager.this = this$0;
            this.mReactEditText = editText;
            ReactContext reactContext = UIManagerHelper.getReactContext(editText);
            this.mEventDispatcher = ReactTextInputManager.getEventDispatcher(reactContext, editText);
            this.mSurfaceId = UIManagerHelper.getSurfaceId(reactContext);
        }

        @Override // com.facebook.react.views.textinput.SelectionWatcher
        public void onSelectionChanged(int start, int end) {
            int min = Math.min(start, end);
            int max = Math.max(start, end);
            if (this.mPreviousSelectionStart == min && this.mPreviousSelectionEnd == max) {
                return;
            }
            this.mEventDispatcher.dispatchEvent(new ReactTextInputSelectionEvent(this.mSurfaceId, this.mReactEditText.getId(), min, max));
            this.mPreviousSelectionStart = min;
            this.mPreviousSelectionEnd = max;
        }
    }

    /* loaded from: classes.dex */
    private static class ReactScrollWatcher implements ScrollWatcher {
        private EventDispatcher mEventDispatcher;
        private int mPreviousHoriz;
        private int mPreviousVert;
        private ReactEditText mReactEditText;
        private int mSurfaceId;

        public ReactScrollWatcher(ReactEditText editText) {
            this.mReactEditText = editText;
            ReactContext reactContext = UIManagerHelper.getReactContext(editText);
            this.mEventDispatcher = ReactTextInputManager.getEventDispatcher(reactContext, editText);
            this.mSurfaceId = UIManagerHelper.getSurfaceId(reactContext);
        }

        @Override // com.facebook.react.views.textinput.ScrollWatcher
        public void onScrollChanged(int horiz, int vert, int oldHoriz, int oldVert) {
            if (this.mPreviousHoriz == horiz && this.mPreviousVert == vert) {
                return;
            }
            this.mEventDispatcher.dispatchEvent(ScrollEvent.obtain(this.mSurfaceId, this.mReactEditText.getId(), ScrollEventType.SCROLL, horiz, vert, 0.0f, 0.0f, 0, 0, this.mReactEditText.getWidth(), this.mReactEditText.getHeight()));
            this.mPreviousHoriz = horiz;
            this.mPreviousVert = vert;
        }
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public Map getExportedViewConstants() {
        return MapBuilder.of("AutoCapitalizationType", MapBuilder.of(ViewProps.NONE, 0, "characters", 4096, "words", 8192, "sentences", 16384));
    }

    public void setPadding(ReactEditText view, int left, int top, int right, int bottom) {
        view.setPadding(left, top, right, bottom);
    }

    protected EditText createInternalEditText(ThemedReactContext themedReactContext) {
        return new EditText(themedReactContext);
    }

    public Object updateState(ReactEditText view, ReactStylesDiffMap props, StateWrapper stateWrapper) {
        ReadableNativeMap stateData;
        view.getFabricViewStateManager().setStateWrapper(stateWrapper);
        if (stateWrapper == null || (stateData = stateWrapper.getStateData()) == null || !stateData.hasKey("attributedString")) {
            return null;
        }
        ReadableNativeMap map = stateData.getMap("attributedString");
        ReadableNativeMap map2 = stateData.getMap("paragraphAttributes");
        if (map == null || map2 == null) {
            throw new IllegalArgumentException("Invalid TextInput State was received as a parameters");
        }
        return ReactTextUpdate.buildReactTextUpdateFromState(TextLayoutManager.getOrCreateSpannableForText(view.getContext(), map, this.mReactTextViewManagerCallback), stateData.getInt("mostRecentEventCount"), TextAttributeProps.getTextAlignment(props, TextLayoutManager.isRTL(map)), TextAttributeProps.getTextBreakStrategy(map2.getString(ViewProps.TEXT_BREAK_STRATEGY)), TextAttributeProps.getJustificationMode(props), map.getArray("fragments").toArrayList().size() > 1);
    }
}
