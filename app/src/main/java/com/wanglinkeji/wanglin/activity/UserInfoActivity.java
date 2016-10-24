package com.wanglinkeji.wanglin.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.wanglinkeji.wanglin.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/29.
 * 点击头像或名字进入的个人详情页
 */

public class UserInfoActivity extends Activity implements View.OnClickListener {

    private Button btn_addFriend;

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_activity_user_userinfo);

        userId = getIntent().getIntExtra("userId", -1);
        if (userId < 0){
            Toast.makeText(UserInfoActivity.this, "参数传递错误，请反馈！", Toast.LENGTH_SHORT).show();
            UserInfoActivity.this.finish();
        }

        btn_addFriend = (Button)findViewById(R.id.button_userInfo_addFriend);
        btn_addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserInfoActivity.this, ApplyNewFriendActivity.class);
                intent.putExtra("friendId", userId);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            default:
                break;
        }
    }

}
