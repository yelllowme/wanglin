package com.wanglinkeji.wanglin.util;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * Created by Administrator on 2016/10/10.
 * 没有下划线的
 */

public class NoLineClickSpan extends ClickableSpan {

    @Override
    public void onClick(View view){

    };

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(0XFF5D8FA6);
        ds.setUnderlineText(false);
    }
}
