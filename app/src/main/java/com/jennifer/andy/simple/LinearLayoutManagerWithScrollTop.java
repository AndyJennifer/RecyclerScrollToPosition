package com.jennifer.andy.simple;

import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

/**
 * Author:  andy.xwt
 * Date:    2018/6/20 16:28
 * Description:
 */

public class LinearLayoutManagerWithScrollTop extends LinearLayoutManager {

    public LinearLayoutManagerWithScrollTop(Context context) {
        super(context);
    }

    public LinearLayoutManagerWithScrollTop(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public LinearLayoutManagerWithScrollTop(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        TopSnappedSmoothScroller topSnappedSmoothScroller = new TopSnappedSmoothScroller(recyclerView.getContext());
        topSnappedSmoothScroller.setTargetPosition(position);
        startSmoothScroll(topSnappedSmoothScroller);
    }

    class TopSnappedSmoothScroller extends LinearSmoothScroller {

        public TopSnappedSmoothScroller(Context context) {
            super(context);
        }

        @Nullable
        @Override
        public PointF computeScrollVectorForPosition(int targetPosition) {
            return LinearLayoutManagerWithScrollTop.this.computeScrollVectorForPosition(targetPosition);
        }

        /**
         * MILLISECONDS_PER_INCH 默认为25，及移动每英寸需要花费25ms，如果你要速度变快一点，就直接设置设置小一点，注意这里的单位是f
         */
        protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
            return 15f / displayMetrics.densityDpi;
        }


        @Override
        protected int getVerticalSnapPreference() {
            return SNAP_TO_START;
        }
    }
}
