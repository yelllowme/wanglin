package com.wanglinkeji.wanglin.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.util.DBUtil;
import com.wanglinkeji.wanglin.util.HttpUtil;
import com.wanglinkeji.wanglin.util.WanglinHttpResponseListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/9.
 * 申请添加好友
 */

public class ApplyNewFriendActivity extends Activity implements View.OnClickListener {

    //被添加好友的ID
    private int friendId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_activity_apply_new_friend);

        friendId = getIntent().getIntExtra("friendId", -1);
        if (friendId < 0){
            Toast.makeText(ApplyNewFriendActivity.this, "参数传递错误，请反馈！", Toast.LENGTH_SHORT).show();
            ApplyNewFriendActivity.this.finish();
        }

        final EditText editText = (EditText)findViewById(R.id.edittext_applyNewFriend_applyMsg);
        Button button = (Button)findViewById(R.id.button_applyNewFriend_finish);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFriend(ApplyNewFriendActivity.this, friendId, "", -1, editText.getText().toString());
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

    private void addFriend(final Context context, int friendId, String remarks, int groupId, String msg){
        Map<String, String> params = new HashMap<>();
        params.put("FriendUserId", String.valueOf(friendId));
        params.put("Remarks", remarks);
        params.put("GroupId", String.valueOf(groupId));
        params.put("Msg", msg);
        HttpUtil.sendVolleyStringRequest_Post(context, HttpUtil.addFriend_url, params, DBUtil.getLoginUser().getToken(), "yellow_applyNewFriend",
                new WanglinHttpResponseListener() {
                    @Override
                    public void onSuccessResponse(JSONObject jsonObject_response) {
                        Toast.makeText(context, "发送成功！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onConnectingError() {

                    }

                    @Override
                    public void onDisconnectError() {

                    }
                });
    }
}
