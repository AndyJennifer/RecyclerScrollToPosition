package com.jennifer.andy.simple.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import static com.jennifer.andy.simple.widget.AdjustLinearSmoothScroller.DEFAULT_MILLISECONDS_PER_INCH;

/**
 * Author:  andy.xwt
 * Date:    2018/6/20 16:28
 * Description:
 */

public class AdjustLinearLayoutManager extends LinearLayoutManager {

    private int scrollType;
    private float time = DEFAULT_MILLISECONDS_PER_INCH;

    public AdjustLinearLayoutManager(Context context) {
        super(context);
    }

    public AdjustLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public AdjustLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    public void setMillisecondsPerInch(float time) {
        this.time = time;
    }

    public void setScrollType(@AdjustLinearSmoothScroller.ScrollType int scrollType) {
        this.scrollType = scrollType;
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        AdjustLinearSmoothScroller.setTime(time);
        AdjustLinearSmoothScroller scroller = new AdjustLinearSmoothScroller(recyclerView.getContext(), scrollType);
        scroller.setTargetPosition(position);
        startSmoothScroll(scroller);
    }

}
