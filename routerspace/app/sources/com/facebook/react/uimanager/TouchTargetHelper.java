package com.facebook.react.uimanager;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.touch.ReactHitSlopView;
import java.util.EnumSet;
/* loaded from: classes.dex */
public class TouchTargetHelper {
    private static final float[] mEventCoords = new float[2];
    private static final PointF mTempPoint = new PointF();
    private static final float[] mMatrixTransformCoords = new float[2];
    private static final Matrix mInverseMatrix = new Matrix();

    /* loaded from: classes.dex */
    public enum TouchTargetReturnType {
        SELF,
        CHILD
    }

    public static int findTargetTagForTouch(float eventX, float eventY, ViewGroup viewGroup) {
        return findTargetTagAndCoordinatesForTouch(eventX, eventY, viewGroup, mEventCoords, null);
    }

    public static int findTargetTagForTouch(float eventX, float eventY, ViewGroup viewGroup, int[] nativeViewId) {
        return findTargetTagAndCoordinatesForTouch(eventX, eventY, viewGroup, mEventCoords, nativeViewId);
    }

    public static int findTargetTagAndCoordinatesForTouch(float eventX, float eventY, ViewGroup viewGroup, float[] viewCoords, int[] nativeViewTag) {
        View findClosestReactAncestor;
        UiThreadUtil.assertOnUiThread();
        int id = viewGroup.getId();
        viewCoords[0] = eventX;
        viewCoords[1] = eventY;
        View findTouchTargetViewWithPointerEvents = findTouchTargetViewWithPointerEvents(viewCoords, viewGroup);
        if (findTouchTargetViewWithPointerEvents == null || (findClosestReactAncestor = findClosestReactAncestor(findTouchTargetViewWithPointerEvents)) == null) {
            return id;
        }
        if (nativeViewTag != null) {
            nativeViewTag[0] = findClosestReactAncestor.getId();
        }
        return getTouchTargetForView(findClosestReactAncestor, viewCoords[0], viewCoords[1]);
    }

    private static View findClosestReactAncestor(View view) {
        while (view != null && view.getId() <= 0) {
            view = (View) view.getParent();
        }
        return view;
    }

    /* JADX WARN: Removed duplicated region for block: B:37:0x006f A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:39:0x0070 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static android.view.View findTouchTargetView(float[] r11, android.view.View r12, java.util.EnumSet<com.facebook.react.uimanager.TouchTargetHelper.TouchTargetReturnType> r13) {
        /*
            com.facebook.react.uimanager.TouchTargetHelper$TouchTargetReturnType r0 = com.facebook.react.uimanager.TouchTargetHelper.TouchTargetReturnType.CHILD
            boolean r0 = r13.contains(r0)
            r1 = 0
            r2 = 0
            r3 = 1
            if (r0 == 0) goto L77
            boolean r0 = r12 instanceof android.view.ViewGroup
            if (r0 == 0) goto L77
            r0 = r12
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            int r4 = r0.getChildCount()
            boolean r5 = r0 instanceof com.facebook.react.uimanager.ReactZIndexedViewGroup
            if (r5 == 0) goto L1e
            r5 = r0
            com.facebook.react.uimanager.ReactZIndexedViewGroup r5 = (com.facebook.react.uimanager.ReactZIndexedViewGroup) r5
            goto L1f
        L1e:
            r5 = r1
        L1f:
            int r4 = r4 - r3
        L20:
            if (r4 < 0) goto L77
            if (r5 == 0) goto L29
            int r6 = r5.getZIndexMappedChildIndex(r4)
            goto L2a
        L29:
            r6 = r4
        L2a:
            android.view.View r6 = r0.getChildAt(r6)
            android.graphics.PointF r7 = com.facebook.react.uimanager.TouchTargetHelper.mTempPoint
            r8 = r11[r2]
            r9 = r11[r3]
            getChildPoint(r8, r9, r0, r6, r7)
            r8 = r11[r2]
            r9 = r11[r3]
            float r10 = r7.x
            r11[r2] = r10
            float r7 = r7.y
            r11[r3] = r7
            android.view.View r6 = findTouchTargetViewWithPointerEvents(r11, r6)
            if (r6 == 0) goto L70
            boolean r7 = r0 instanceof com.facebook.react.uimanager.ReactOverflowView
            if (r7 == 0) goto L6c
            r7 = r0
            com.facebook.react.uimanager.ReactOverflowView r7 = (com.facebook.react.uimanager.ReactOverflowView) r7
            java.lang.String r7 = r7.getOverflow()
            java.lang.String r10 = "hidden"
            boolean r10 = r10.equals(r7)
            if (r10 != 0) goto L64
            java.lang.String r10 = "scroll"
            boolean r7 = r10.equals(r7)
            if (r7 == 0) goto L6c
        L64:
            boolean r7 = isTouchPointInView(r8, r9, r12)
            if (r7 != 0) goto L6c
            r7 = 0
            goto L6d
        L6c:
            r7 = 1
        L6d:
            if (r7 == 0) goto L70
            return r6
        L70:
            r11[r2] = r8
            r11[r3] = r9
            int r4 = r4 + (-1)
            goto L20
        L77:
            com.facebook.react.uimanager.TouchTargetHelper$TouchTargetReturnType r0 = com.facebook.react.uimanager.TouchTargetHelper.TouchTargetReturnType.SELF
            boolean r13 = r13.contains(r0)
            if (r13 == 0) goto L8a
            r13 = r11[r2]
            r11 = r11[r3]
            boolean r11 = isTouchPointInView(r13, r11, r12)
            if (r11 == 0) goto L8a
            return r12
        L8a:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.facebook.react.uimanager.TouchTargetHelper.findTouchTargetView(float[], android.view.View, java.util.EnumSet):android.view.View");
    }

    private static boolean isTouchPointInView(float x, float y, View view) {
        if (view instanceof ReactHitSlopView) {
            ReactHitSlopView reactHitSlopView = (ReactHitSlopView) view;
            if (reactHitSlopView.getHitSlopRect() != null) {
                Rect hitSlopRect = reactHitSlopView.getHitSlopRect();
                return x >= ((float) (-hitSlopRect.left)) && x < ((float) (view.getWidth() + hitSlopRect.right)) && y >= ((float) (-hitSlopRect.top)) && y < ((float) (view.getHeight() + hitSlopRect.bottom));
            }
        }
        return x >= 0.0f && x < ((float) view.getWidth()) && y >= 0.0f && y < ((float) view.getHeight());
    }

    private static void getChildPoint(float x, float y, ViewGroup parent, View child, PointF outLocalPoint) {
        float scrollX = (x + parent.getScrollX()) - child.getLeft();
        float scrollY = (y + parent.getScrollY()) - child.getTop();
        Matrix matrix = child.getMatrix();
        if (!matrix.isIdentity()) {
            float[] fArr = mMatrixTransformCoords;
            fArr[0] = scrollX;
            fArr[1] = scrollY;
            Matrix matrix2 = mInverseMatrix;
            matrix.invert(matrix2);
            matrix2.mapPoints(fArr);
            float f = fArr[0];
            scrollY = fArr[1];
            scrollX = f;
        }
        outLocalPoint.set(scrollX, scrollY);
    }

    private static View findTouchTargetViewWithPointerEvents(float[] eventCoords, View view) {
        PointerEvents pointerEvents = view instanceof ReactPointerEventsView ? ((ReactPointerEventsView) view).getPointerEvents() : PointerEvents.AUTO;
        if (!view.isEnabled()) {
            if (pointerEvents == PointerEvents.AUTO) {
                pointerEvents = PointerEvents.BOX_NONE;
            } else if (pointerEvents == PointerEvents.BOX_ONLY) {
                pointerEvents = PointerEvents.NONE;
            }
        }
        if (pointerEvents == PointerEvents.NONE) {
            return null;
        }
        if (pointerEvents == PointerEvents.BOX_ONLY) {
            return findTouchTargetView(eventCoords, view, EnumSet.of(TouchTargetReturnType.SELF));
        }
        if (pointerEvents == PointerEvents.BOX_NONE) {
            View findTouchTargetView = findTouchTargetView(eventCoords, view, EnumSet.of(TouchTargetReturnType.CHILD));
            if (findTouchTargetView != null) {
                return findTouchTargetView;
            }
            if ((view instanceof ReactCompoundView) && isTouchPointInView(eventCoords[0], eventCoords[1], view) && ((ReactCompoundView) view).reactTagForTouch(eventCoords[0], eventCoords[1]) != view.getId()) {
                return view;
            }
            return null;
        } else if (pointerEvents == PointerEvents.AUTO) {
            return (!(view instanceof ReactCompoundViewGroup) || !isTouchPointInView(eventCoords[0], eventCoords[1], view) || !((ReactCompoundViewGroup) view).interceptsTouchEvent(eventCoords[0], eventCoords[1])) ? findTouchTargetView(eventCoords, view, EnumSet.of(TouchTargetReturnType.SELF, TouchTargetReturnType.CHILD)) : view;
        } else {
            throw new JSApplicationIllegalArgumentException("Unknown pointer event type: " + pointerEvents.toString());
        }
    }

    private static int getTouchTargetForView(View targetView, float eventX, float eventY) {
        if (targetView instanceof ReactCompoundView) {
            return ((ReactCompoundView) targetView).reactTagForTouch(eventX, eventY);
        }
        return targetView.getId();
    }
}
