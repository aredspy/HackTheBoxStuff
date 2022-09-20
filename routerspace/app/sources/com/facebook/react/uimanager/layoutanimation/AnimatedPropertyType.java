package com.facebook.react.uimanager.layoutanimation;

import com.facebook.react.uimanager.ViewProps;
/* loaded from: classes.dex */
enum AnimatedPropertyType {
    OPACITY,
    SCALE_X,
    SCALE_Y,
    SCALE_XY;

    public static AnimatedPropertyType fromString(String name) {
        name.hashCode();
        char c = 65535;
        switch (name.hashCode()) {
            case -1267206133:
                if (name.equals(ViewProps.OPACITY)) {
                    c = 0;
                    break;
                }
                break;
            case -908189618:
                if (name.equals(ViewProps.SCALE_X)) {
                    c = 1;
                    break;
                }
                break;
            case -908189617:
                if (name.equals(ViewProps.SCALE_Y)) {
                    c = 2;
                    break;
                }
                break;
            case 1910893003:
                if (name.equals("scaleXY")) {
                    c = 3;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return OPACITY;
            case 1:
                return SCALE_X;
            case 2:
                return SCALE_Y;
            case 3:
                return SCALE_XY;
            default:
                throw new IllegalArgumentException("Unsupported animated property: " + name);
        }
    }
}
