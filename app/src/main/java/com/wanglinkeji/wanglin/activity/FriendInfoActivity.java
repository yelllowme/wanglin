package com.wanglinkeji.wanglin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.wanglinkeji.wanglin.R;

/**
 * Created by Administrator on 2016/11/8.
 * 点击用户好友列表Item的用户好友信息Activity
 */

public class FriendInfoActivity extends Activity implements View.OnClickListener{

    private Button button_sendMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_activity_friend_info);

        viewInit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_friendInfo_sendMessage:{
                Intent intent = new Intent(FriendInfoActivity.this, ChatActivity.class);
                startActivity(intent);
                FriendInfoActivity.this.finish();
                break;
            }
            default:
                break;
        }
    }

    private void viewInit(){
        button_sendMessage = (Button)findViewById(R.id.button_friendInfo_sendMessage);
        button_sendMessage.setOnClickListener(this);
    }
}
