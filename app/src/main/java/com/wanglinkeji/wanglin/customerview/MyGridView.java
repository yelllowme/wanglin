package com.wanglinkeji.wanglin.customerview;

import android.view.View;
import android.widget.GridView;

/**
 * Created by Administrator on 2016/6/15.
 * 自定义GridView，可以嵌套在ListView中，自适应高度
 */
public class MyGridView extends GridView {

    public MyGridView(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                View.MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
