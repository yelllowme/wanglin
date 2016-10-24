package com.wanglinkeji.wanglin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.util.DBUtil;
import com.wanglinkeji.wanglin.util.HttpUtil;
import com.wanglinkeji.wanglin.util.LogoutActivityCollector;
import com.wanglinkeji.wanglin.util.WanglinHttpResponseListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/2.
 * 设置
 */

public class SettingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_activity_setting);
        LogoutActivityCollector.addActivity(SettingActivity.this);

        viewInit();
    }

    @Override
    protected void onDestroy() {
        LogoutActivityCollector.removeActivity(SettingActivity.this);
        super.onDestroy();
    }

    private void viewInit(){
        Button button_logout = (Button)findViewById(R.id.tuichudenglu);
        button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout(DBUtil.getLoginUser().getToken());
                DBUtil.logout();
                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                startActivity(intent);
                LogoutActivityCollector.removeAllActivity();
            }
        });
        Button button_binding = (Button)findViewById(R.id.renzhengshenfen);
        button_binding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, MyHouseEstateActivity.class);
                startActivity(intent);
            }
        });
    }

    private void logout(String token){
        Map<String, String> params = new HashMap<>();
        HttpUtil.sendVolleyStringRequest_Post(SettingActivity.this, HttpUtil.logout_url, params, token, "yellow_logoutResponse",
                new WanglinHttpResponseListener() {
                    @Override
                    public void onSuccessResponse(JSONObject jsonObject_response) {}

                    @Override
                    public void onConnectingError() {}
                    @Override
                    public void onDisconnectError() {}
                });
    }
}
