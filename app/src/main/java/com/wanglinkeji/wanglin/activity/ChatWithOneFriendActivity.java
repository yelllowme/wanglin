package com.wanglinkeji.wanglin.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.wanglinkeji.wanglin.R;

/**
 * Created by Administrator on 2016/9/30.
 * 和单人好友聊天界面
 */

public class ChatWithOneFriendActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_activity_chat_with_one_friend);
    }

    @Override
    public void onClick(View view) {

    }
}
