package com.jennifer.andy.simple.widget;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v7.widget.LinearSmoothScroller;
import android.util.DisplayMetrics;

/**
 * Author:  andy.xwt
 * Date:    2020/3/28 18:06
 * Description:
 */

public class AdjustLinearSmoothScroller extends LinearSmoothScroller {

    private int scrollType;
    private static float time;
    public static final float DEFAULT_MILLISECONDS_PER_INCH = 25f;


    @IntDef({SNAP_TO_ANY, SNAP_TO_START, SNAP_TO_END})
    public @interface ScrollType {
    }


    public AdjustLinearSmoothScroller(Context context, @ScrollType int scrollType) {
        super(context);
        this.scrollType = scrollType;
    }

    @Override
    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
        return time / displayMetrics.densityDpi;
    }

    public static void setTime(float milliseconds) {
        time = milliseconds;
    }

    @Override
    protected int getVerticalSnapPreference() {
        return scrollType;
    }
}
