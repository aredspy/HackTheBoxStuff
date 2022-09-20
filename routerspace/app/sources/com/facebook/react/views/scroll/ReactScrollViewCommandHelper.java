package com.facebook.react.views.scroll;

import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.PixelUtil;
import java.util.Map;
/* loaded from: classes.dex */
public class ReactScrollViewCommandHelper {
    public static final int COMMAND_FLASH_SCROLL_INDICATORS = 3;
    public static final int COMMAND_SCROLL_TO = 1;
    public static final int COMMAND_SCROLL_TO_END = 2;

    /* loaded from: classes.dex */
    public interface ScrollCommandHandler<T> {
        void flashScrollIndicators(T scrollView);

        void scrollTo(T scrollView, ScrollToCommandData data);

        void scrollToEnd(T scrollView, ScrollToEndCommandData data);
    }

    /* loaded from: classes.dex */
    public static class ScrollToCommandData {
        public final boolean mAnimated;
        public final int mDestX;
        public final int mDestY;

        ScrollToCommandData(int destX, int destY, boolean animated) {
            this.mDestX = destX;
            this.mDestY = destY;
            this.mAnimated = animated;
        }
    }

    /* loaded from: classes.dex */
    public static class ScrollToEndCommandData {
        public final boolean mAnimated;

        ScrollToEndCommandData(boolean animated) {
            this.mAnimated = animated;
        }
    }

    public static Map<String, Integer> getCommandsMap() {
        return MapBuilder.of("scrollTo", 1, "scrollToEnd", 2, "flashScrollIndicators", 3);
    }

    public static <T> void receiveCommand(ScrollCommandHandler<T> viewManager, T scrollView, int commandType, ReadableArray args) {
        Assertions.assertNotNull(viewManager);
        Assertions.assertNotNull(scrollView);
        Assertions.assertNotNull(args);
        if (commandType == 1) {
            scrollTo(viewManager, scrollView, args);
        } else if (commandType == 2) {
            scrollToEnd(viewManager, scrollView, args);
        } else if (commandType == 3) {
            viewManager.flashScrollIndicators(scrollView);
        } else {
            throw new IllegalArgumentException(String.format("Unsupported command %d received by %s.", Integer.valueOf(commandType), viewManager.getClass().getSimpleName()));
        }
    }

    public static <T> void receiveCommand(ScrollCommandHandler<T> viewManager, T scrollView, String commandType, ReadableArray args) {
        Assertions.assertNotNull(viewManager);
        Assertions.assertNotNull(scrollView);
        Assertions.assertNotNull(args);
        commandType.hashCode();
        char c = 65535;
        switch (commandType.hashCode()) {
            case -402165208:
                if (commandType.equals("scrollTo")) {
                    c = 0;
                    break;
                }
                break;
            case 28425985:
                if (commandType.equals("flashScrollIndicators")) {
                    c = 1;
                    break;
                }
                break;
            case 2055114131:
                if (commandType.equals("scrollToEnd")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                scrollTo(viewManager, scrollView, args);
                return;
            case 1:
                viewManager.flashScrollIndicators(scrollView);
                return;
            case 2:
                scrollToEnd(viewManager, scrollView, args);
                return;
            default:
                throw new IllegalArgumentException(String.format("Unsupported command %s received by %s.", commandType, viewManager.getClass().getSimpleName()));
        }
    }

    private static <T> void scrollTo(ScrollCommandHandler<T> viewManager, T scrollView, ReadableArray args) {
        viewManager.scrollTo(scrollView, new ScrollToCommandData(Math.round(PixelUtil.toPixelFromDIP(args.getDouble(0))), Math.round(PixelUtil.toPixelFromDIP(args.getDouble(1))), args.getBoolean(2)));
    }

    private static <T> void scrollToEnd(ScrollCommandHandler<T> viewManager, T scrollView, ReadableArray args) {
        viewManager.scrollToEnd(scrollView, new ScrollToEndCommandData(args.getBoolean(0)));
    }
}
