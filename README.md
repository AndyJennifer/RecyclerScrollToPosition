
### 前言
最近开发中遇到了一个需求，需要RecyclerView滚动到指定位置后置顶显示，当时遇到这个问题的时候，心里第一反应是直接使用RecyclerView的smoothScrollToPosition()方法，实现对应位置的平滑滚动。但是在实际使用中发现并没有到底自己想要的效果。本想着偷懒直接从网上Copy下，但是发现效果并不是很好。于是就自己去研究源码。

该系列文章分为两篇文章。

- 如果你想解决通过smoothScrollToPosition滚动到顶部，或者滚动加速，请观看本篇文章，
- 如果你想了解其内部实现，请看[RecyclerView.smoothScrollToPosition了解一下](https://www.jianshu.com/p/a5cd3cff2f1b)

>注意！！！注意！！！注意！！！
>这是使用的LinearLayoutManager且是竖直方向上的，横向的思路是一样的，只是修改的方法不一样，大家一定要注意前提条件。

### 如何使用smoothScrollToPosition滚动到顶部？
如果你看了我的另一篇文章[RecyclerView.smoothScrollToPosition了解一下](https://www.jianshu.com/p/a5cd3cff2f1b),大家应该会清楚，其实在你设定目标位置后，当找到目标视图后，最后让RecyclerView进行滚动的方法是其对应LinearLayoutManager中的LinearSmoothScroller的calculateDtToFit()方法。

```
 public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int
            snapPreference) {
        switch (snapPreference) {
            case SNAP_TO_START:
                return boxStart - viewStart;
            case SNAP_TO_END:
                return boxEnd - viewEnd;
            case SNAP_TO_ANY:
                final int dtStart = boxStart - viewStart;
                if (dtStart > 0) {
                    return dtStart;
                }
                final int dtEnd = boxEnd - viewEnd;
                if (dtEnd < 0) {
                    return dtEnd;
                }
                break;
            default:
                throw new IllegalArgumentException("snap preference should be one of the"
                        + " constants defined in SmoothScroller, starting with SNAP_");
        }
        return 0;
    }

```

也就是说在LinerlayoutManager为竖直的情况下，snapPreference默认为SNAP_ANY,那么我们就可以得到，下面三种情况。

- 当滚动位置在可见范围之内时
滚动距离为0，故不会滚动。
- 当滚动位置在可见范围之前时
内容向上滚动且只能滚动到顶部。
- 当滚动位置在可见范围距离之外时
内容向下滚动，且只能滚动到底部。

同时snapPreference的值是通过LinearSmoothScroller中的getVerticalSnapPreference（）与getHorizontalSnapPreference() 来设定的。

所以为了使滚动位置对应的目标视图在顶部显示，那么我们创建一个新类并继承LinearLayoutManager。同时创建TopSnappedSmoothScroller继承LinearSmoothScroller，并重写它的getVerticalSnapPreference（）方法就行了。（如果你是横向的，请修改getHorizontalSnapPreference方法）

```

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

        @Override
        protected int getVerticalSnapPreference() {
            return SNAP_TO_START;//设置滚动位置
        }
    }
}
```
创建该类后，我们接下来就只用给RecyclerView设置对应的新的布局管理器，并调用smoothScrollToPosition()方法就行了。

### 如何设置smoothScrollToPosition滚动的速度？
其实在RecyclerView中，滚动到指定位置是分为了两个部分，第一个是没有找到目标位置对应的视图之前的速度，一种是找到目标位置对应的视图之后滚动的速度。

#### 没有找到目标位置之前


```
 action.update((int) (mInterimTargetDx * TARGET_SEEK_EXTRA_SCROLL_RATIO),
                (int) (mInterimTargetDy * TARGET_SEEK_EXTRA_SCROLL_RATIO),
                (int) (time * TARGET_SEEK_EXTRA_SCROLL_RATIO), mLinearInterpolator);

```
在开始寻找目标位置时，默认的开始距离是12000（单位:px),且这里大家注意，我们使用了LinearInterpolator，也就是说在没有找到目标位置之前，我们的RecyclerView速度是恒定的。

#### 找到目标位置之后

```
action.update(-dx, -dy, time, mDecelerateInterpolator);

```

这里我们使用了DecelerateInterpolator。也就是说，找到目标位置之后，RecyclerView是速度是慢慢减小。

所以现在就提供了一个思路，我们可以去修改两个部分的插值器，来改变RecyclerView的滚动速度，当然我这里并没有给实例代码，因为我发现Google并没有想让我们去修改插值器的想法，因为在其LinearSmoothScroller中，他直接把两个插值器用protected修饰。（所以我觉得这样改，感觉不优雅）如果有兴趣的小伙伴，可以去修改。

#### 那现在怎么修改速度呢？
既然以修改插值器的方式比较麻烦，那么我们可以修改滚动时间啊!!!!!!希望大家还记得，我们在调用Action的update方法时，我们不仅保存了RecyclerView需要滚动的距离，我们还保存了滑动总共需要的时间。

滑动所需要的时间是通过calculateTimeForScrolling（）这个方法来进行计算的。

```
    protected int calculateTimeForScrolling(int dx) {
        //这里对时间进行了四舍五入操作。 
        return (int) Math.ceil(Math.abs(dx) * MILLISECONDS_PER_PX);
    }

```

其中MILLISECONDS_PER_PX 会在LinearSmoothScroller初始化的时候创建。

```
public LinearSmoothScroller(Context context) {
      MILLISECONDS_PER_PX = calculateSpeedPerPixel(context.getResources().getDisplayMetrics());
    }
```
查看calculateSpeedPerPixel()方法

```
    private static final float MILLISECONDS_PER_INCH = 25f;// 默认为移动一英寸需要花费25ms
    //
    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
        return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
    }
```

也就是说，当前滚动的速度是与屏幕的像素密度相关， 通过获取当前手机屏幕每英寸的像素密度，与每英寸移动所需要花费的时间，用每英寸移动所需要花费的时间除以像素密度就能计算出移动一个像素密度需要花费的时间。

那么现在，就可以通过两个方法来修改RecyclerView的滚动速度，要么我们修改calculateSpeedPerPixel方法修改移动一个像素需要花费的时间。要么我们修改calculateTimeForScrolling方法。

这里我采用修改calculateSpeedPerPixel方法来改变速度。这里我修改移动一英寸需要花费为10ms，那代表着滚动速度加快了。那么对应的滚动时间就变小了

```
  protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {   
      return 10f / displayMetrics.densityDpi;                                                      
  }               
                                                            
```

到了这里我相信大家已经明白了，怎么去修改速度与滚动位置了。好啦好啦，先睡了太困了。

对了对了，[源码在这里](https://github.com/AndyJennifer/RecyclerScrollToPosition)。大家如果有兴趣，可以去研究一下。

### 最后
最后，附上我写的一个基于Kotlin 仿开眼的项目[SimpleEyes](https://github.com/AndyJennifer/SimpleEyes)(ps: 其实在我之前，已经有很多小朋友开始仿这款应用了，但是我觉得要做就做好。所以我的项目和其他的人应该不同，不仅仅是简单的一个应用。但是，但是。但是。重要的话说三遍。还在开发阶段，不要打我)，欢迎大家follow和start.



