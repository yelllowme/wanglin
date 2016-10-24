package com.wanglinkeji.wanglin.customerview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2016/10/19.
 * 为了解决PhotoView + ViewPager 时出现 IllegalArgumentException: pointerIndex out of range
 */

public class MyViewPager_PhotoView extends ViewPager {

    public MyViewPager_PhotoView(Context context) {
        super(context);
    }

    public MyViewPager_PhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return false;
    }

}
