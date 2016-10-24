package com.wanglinkeji.wanglin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.wanglinkeji.wanglin.R;
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
 * 注册Activity
 */
public class RegisterActivity extends Activity implements View.OnClickListener {

    private EditText editText_nickName, editText_phoneNum, editText_verificationCede, editText_password;

    private LinearLayout layout_sexChoosed_man, layout_sexChoosed_woman;

    private ImageView imageView_sexChoosed_man, imageView_sexChoosed_women, imageView_toSeePasssord;

    private TextView textView_getVerificationCode, textView_register, textView_cancle, textView_sexChoosed_man, textView_sexChoosed_woman;

    private PopupWindow loading_page;

    private View rootView;

    //密码是否可见
    private boolean isSeePasseord = false;

    //性别选择
    private String sex = "未选择";

    //倒计时类
    private class MyCountDownTimer extends CountDownTimer{

        //millisInFuture：总时间， countDownInterval：间隔时间
        public MyCountDownTimer(long millisInFuture, long countDownInterval){
            super(millisInFuture, countDownInterval);
        }

        //没减一次间隔时间所执行的操作
        @Override
        public void onTick(long l) {
            textView_getVerificationCode.setEnabled(false);
            textView_getVerificationCode.setTextColor(0XAAEFF0F0);
            textView_getVerificationCode.setText("重新获取" + l/1000);
        }

        //倒计时完成所执行的操作
        @Override
        public void onFinish() {
            textView_getVerificationCode.setEnabled(true);
            textView_getVerificationCode.setTextColor(0XFFEFF0F0);
            textView_getVerificationCode.setText("获取验证码");
            //cancle()方法停止计时
            MyCountDownTimer.this.cancel();
        }
    }

    private MyCountDownTimer countDownTimer = new MyCountDownTimer(60000, 1000);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_activity_register);
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
            //性别选择：男
            case R.id.layout_register_sexChoosed_man:{
                sex = "男";
                textView_sexChoosed_man.setTextColor(getResources().getColor(R.color.white));
                imageView_sexChoosed_man.setImageResource(R.mipmap.rectangle_choosed_white);
                textView_sexChoosed_woman.setTextColor(0XFF9198A0);
                imageView_sexChoosed_women.setImageResource(R.mipmap.rectangle_notchoosed_white);
                break;
            }
            //性别选择：女
            case R.id.layout_register_sexChoosed_women:{
                sex = "女";
                textView_sexChoosed_man.setTextColor(0XFF9198A0);
                imageView_sexChoosed_man.setImageResource(R.mipmap.rectangle_notchoosed_white);
                textView_sexChoosed_woman.setTextColor(getResources().getColor(R.color.white));
                imageView_sexChoosed_women.setImageResource(R.mipmap.rectangle_choosed_white);
                break;
            }
            //获取验证码
            case R.id.textview_register_getVerifivationCode:{
                if (editText_phoneNum.getText().length() != 11){
                    Toast.makeText(RegisterActivity.this, "请输入正确的手机号哦！", Toast.LENGTH_SHORT).show();
                }else {
                    getverifivationCode(editText_phoneNum.getText().toString());
                    countDownTimer.start();
                }
                break;
            }
            //密码可见
            case R.id.imageview_register_toSeePassword:{
                if (isSeePasseord == false){
                    isSeePasseord = true;
                    //设置密码可见
                    editText_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else {
                    isSeePasseord = false;
                    //设置密码不可见
                    editText_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }

                break;
            }
            //注册按钮时间流程
            //第一步：注册
            //如果注册成功（同时登录成功）
            //第二步：本地添加用户到数据库
            //第三步：本地登录
            //如果前面都成功，跳转到绑定小区页面（这时LoginCollector.removeAll）
            case R.id.textview_register_registerBtn:{
                if (editText_nickName.getText().length() == 0){
                    Toast.makeText(RegisterActivity.this, "昵称不能为空哦！", Toast.LENGTH_SHORT).show();
                }else {
                    if (!sex.equals("男") && !sex.equals("女")){
                        Toast.makeText(RegisterActivity.this, "你还没有选择性别哦！", Toast.LENGTH_SHORT).show();
                    }else {
                        if (editText_phoneNum.getText().length() != 11){
                            Toast.makeText(RegisterActivity.this, "请输入正确的手机号哦！", Toast.LENGTH_SHORT).show();
                        }else {
                            if (editText_verificationCede.getText().length() == 0){
                                Toast.makeText(RegisterActivity.this, "验证按不能为空哦！", Toast.LENGTH_SHORT).show();
                            }else {
                                if (editText_password.getText().length() == 0){
                                    Toast.makeText(RegisterActivity.this, "密码不能为空哦！", Toast.LENGTH_SHORT).show();
                                }else {
                                    loading_page.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                                    register(editText_nickName.getText().toString(), sex, editText_phoneNum.getText().toString(),
                                            editText_verificationCede.getText().toString(), editText_password.getText().toString());
                                }
                            }
                        }
                    }
                }
                break;
            }
            //返回按钮
            case R.id.textview_register_cancle:{
                RegisterActivity.this.finish();
                break;
            }
            default:
                break;
        }
    }

    private void viewInit(){
        loading_page = OtherUtil.getLoadingPage(RegisterActivity.this);
        rootView = LayoutInflater.from(RegisterActivity.this).inflate(R.layout.layout_activity_register, null);
        editText_nickName = (EditText)findViewById(R.id.edittext_register_nickName);
        //昵称栏禁止输入空格和换行
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if(source.equals(" ")||source.toString().contentEquals("\n"))return "";
                else return null;
            }
        };
        editText_nickName.setFilters(new InputFilter[]{filter});

        layout_sexChoosed_man = (LinearLayout)findViewById(R.id.layout_register_sexChoosed_man);
        layout_sexChoosed_man.setOnClickListener(this);
        layout_sexChoosed_woman = (LinearLayout)findViewById(R.id.layout_register_sexChoosed_women);
        layout_sexChoosed_woman.setOnClickListener(this);
        imageView_sexChoosed_man = (ImageView)findViewById(R.id.imageview_register_sexChoosed_man);
        imageView_sexChoosed_women = (ImageView)findViewById(R.id.imageview_register_sexChoosed_women);
        editText_phoneNum = (EditText)findViewById(R.id.edittext_register_phoneNum);
        editText_verificationCede = (EditText)findViewById(R.id.edittext_register_verifivationCode);
        textView_getVerificationCode = (TextView)findViewById(R.id.textview_register_getVerifivationCode);
        textView_getVerificationCode.setOnClickListener(this);
        editText_password = (EditText)findViewById(R.id.edittext_register_password);
        imageView_toSeePasssord = (ImageView)findViewById(R.id.imageview_register_toSeePassword);
        imageView_toSeePasssord.setOnClickListener(this);
        textView_register = (TextView)findViewById(R.id.textview_register_registerBtn);
        textView_register.setOnClickListener(this);
        textView_cancle = (TextView)findViewById(R.id.textview_register_cancle);
        textView_cancle.setOnClickListener(this);
        textView_sexChoosed_man = (TextView)findViewById(R.id.textview_register_sexChoosed_man);
        textView_sexChoosed_woman = (TextView)findViewById(R.id.textview_register_sexChoosed_woman);
    }

    private void getverifivationCode(final String phoneNum){
        Map<String, String> params = new HashMap<>();
        params.put("Mobile", phoneNum);
        HttpUtil.sendVolleyStringRequest_Post(RegisterActivity.this, HttpUtil.getverifivationCode_url, params, "", "yellow_verifivationCodeInfo",
                new WanglinHttpResponseListener() {
                    @Override
                    public void onSuccessResponse(JSONObject jsonObject_response) {
                            Toast.makeText(RegisterActivity.this, "验证码已发送！", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onConnectingError() {}
                    @Override
                    public void onDisconnectError() {}
                });
    }


    //注册按钮时间流程
    //第一步：注册
    //如果注册成功（同时登录成功）
    //第二步：本地添加用户到数据库
    //第三步：本地登录
    //如果前面都成功，跳转到绑定小区页面（这时LoginCollector.removeAll）
    private void register(String nickName, String sex, final String phoneNum, String verificationCode, final String password){
        Map<String, String> params = new HashMap<>();
        params.put("UserName", phoneNum);
        params.put("UserPassword", password);
        params.put("Nick", nickName);
        params.put("Sex", sex);
        params.put("Code", verificationCode);
        HttpUtil.sendVolleyStringRequest_Post(RegisterActivity.this, HttpUtil.register_url, params, "", "yellow_RegisterResponse",
                new WanglinHttpResponseListener() {
                    @Override
                    public void onSuccessResponse(JSONObject jsonObject_response) {
                        try {
                            JSONObject jsonObject_data = jsonObject_response.getJSONObject("Data");
                            String token = jsonObject_data.getString("Token"); //获取token
                            DBUtil.addUser(phoneNum, password, token, HttpUtil.getSign(token), DBUtil.LOGIN_STATE_LOGIN); //新用户添加到本地数据库
                            DBUtil.logout(); //为保证只有当前注册用户一个用户登录，这里先logout
                            DBUtil.login(phoneNum, token, password, HttpUtil.getSign(token)); //本地登录

                            Intent intent = new Intent(RegisterActivity.this, Register_BindingHousingEstateActivity.class);
                            intent.putExtra("isSkipVisible", true);
                            startActivity(intent);
                            loading_page.dismiss();
                            LoginRegisterActivityCollector.removeAll();
                        } catch (JSONException e) {
                            loading_page.dismiss();
                            Toast.makeText(RegisterActivity.this, "解析返回结果失败，请重试！", Toast.LENGTH_SHORT).show();
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
