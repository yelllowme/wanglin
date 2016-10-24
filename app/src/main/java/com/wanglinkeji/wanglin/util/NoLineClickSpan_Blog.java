package com.wanglinkeji.wanglin.util;

import android.content.Context;
import android.content.Intent;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.wanglinkeji.wanglin.activity.UserInfoActivity;

/**
 * Created by Administrator on 2016/10/12.
 * 吐槽中，@Item可点击
 */

public class NoLineClickSpan_Blog extends ClickableSpan {

    private int userId;

    private Context context;

    public NoLineClickSpan_Blog(int userId, Context context){
        this.userId = userId;
        this.context = context;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(context, UserInfoActivity.class);
        intent.putExtra("userId", userId);
        context.startActivity(intent);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(0XFF5D8FA6);
        ds.setUnderlineText(false);
    }
}
