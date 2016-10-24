package com.wanglinkeji.wanglin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.model.UserIdentityInfoModel;
import com.wanglinkeji.wanglin.util.DBUtil;
import com.wanglinkeji.wanglin.util.HttpUtil;
import com.wanglinkeji.wanglin.util.LoginRegisterActivityCollector;
import com.wanglinkeji.wanglin.util.OtherUtil;
import com.wanglinkeji.wanglin.util.WanglinHttpResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/3.
 * 登录Activity
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    private EditText editText_phoneNum, editText_password;

    private TextView textView_login, textView_toRegister;

    private PopupWindow loading_page;

    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_activity_login);
        LoginRegisterActivityCollector.addActivity(this);

        viewInit();
    }

    @Override
    protected void onDestroy() {
        LoginRegisterActivityCollector.removeActivity(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.textview_login_loginBtn:{
                if (editText_phoneNum.getText().length() != 11){
                    Toast.makeText(LoginActivity.this, "请输入正确的手机号！", Toast.LENGTH_SHORT).show();
                }else {
                    if (editText_password.getText().length() == 0){
                        Toast.makeText(LoginActivity.this, "密码不能为空哦！", Toast.LENGTH_SHORT).show();
                    }else {
                        loading_page.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                        login(editText_phoneNum.getText().toString(), editText_password.getText().toString());
                    }
                }
                break;
            }
            case R.id.textview_login_registerNewUser:{
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            }
            default:
                break;
        }
    }

    private void viewInit(){
        editText_password = (EditText)findViewById(R.id.edittext_login_password);
        editText_phoneNum = (EditText)findViewById(R.id.edittext_login_phoneNumber);
        textView_login = (TextView)findViewById(R.id.textview_login_loginBtn);
        textView_login.setOnClickListener(this);
        textView_toRegister = (TextView)findViewById(R.id.textview_login_registerNewUser);
        textView_toRegister.setOnClickListener(this);
        loading_page = OtherUtil.getLoadingPage(LoginActivity.this);
        rootView = LayoutInflater.from(LoginActivity.this).inflate(R.layout.layout_activity_login, null);
    }

    private void login(final String phoneNum, final String password){
        Map<String, String> params = new HashMap<>();
        params.put("UserName", phoneNum);
        params.put("UserPassword", password);
        HttpUtil.sendVolleyStringRequest_Post(LoginActivity.this, HttpUtil.login_url, params, "", "yellow_loginResponseInfo",
                new WanglinHttpResponseListener() {
                    @Override
                    public void onSuccessResponse(JSONObject jsonObject_response) {
                        try {
                            JSONObject jsonObject_data = jsonObject_response.getJSONObject("Data");
                            String token = jsonObject_data.getString("Token");
                            UserIdentityInfoModel.userName = jsonObject_data.getString("UserRealName");//设置用户名
                            //先判断本地是否有此用户
                            if (DBUtil.hasThisUser(phoneNum) == true){
                                //先本地登录，再跳转
                                DBUtil.login(phoneNum, token,password, HttpUtil.getSign(token));
                            }else {
                                //本地添加用户，并本地登录
                                DBUtil.addUser(phoneNum, password, token, HttpUtil.getSign(token), DBUtil.LOGIN_STATE_LOGIN);
                            }
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            loading_page.dismiss();
                            LoginActivity.this.finish();
                        } catch (JSONException e) {
                            loading_page.dismiss();
                            Toast.makeText(LoginActivity.this, "登录失败，失败原因：解析返回结果出错，请反馈！", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onConnectingError() {
                        loading_page.dismiss();
                    }
                    @Override
                    public void onDisconnectError() {
                        loading_page.dismiss();
                    }
                });
    }
}
