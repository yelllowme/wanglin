package com.wanglinkeji.wanglin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.model.HousingEstateModel;
import com.wanglinkeji.wanglin.model.UserIdentityInfoModel;
import com.wanglinkeji.wanglin.util.DBUtil;
import com.wanglinkeji.wanglin.util.HttpUtil;
import com.wanglinkeji.wanglin.util.OtherUtil;
import com.wanglinkeji.wanglin.util.WanglinHttpResponseListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SimplePostInfoActivity extends Activity implements View.OnClickListener {
    private ImageView imageViewBack;
    private TextView textViewTitle;
    private EditText editTextInput;
    private Button btnCommit;
    private Handler mHandler;

    private PopupWindow loading_page;//进度条
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_simple_post_info);
        initViews();
        initData();
    }

    private void initData() {
        Intent data =getIntent();
        if(data.getStringExtra("type")==null){
            Log.e("info","非法打开SimplePostInfoActivity");
            finish();
        }
        textViewTitle.setText(data.getStringExtra("title"));
        editTextInput.setHint(data.getStringExtra("hint"));
        btnCommit.setText(data.getStringExtra("btnTxt"));
    }

    private void initViews() {
        mHandler = new Handler();

        imageViewBack= (ImageView) findViewById(R.id.imageview_back);
        textViewTitle=(TextView)findViewById(R.id.textview_title);
        editTextInput=(EditText)findViewById(R.id.editText_input);
        btnCommit=(Button)findViewById(R.id.btn_post);
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_simple_post_info,null);
        loading_page = OtherUtil.getLoadingPage(this);

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnCommit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_post){
            String content =editTextInput.getText().toString();
            if(content.length()<1){
                Toast.makeText(this,"输入内容不能为空",Toast.LENGTH_SHORT).show();
                return;
            }
            commitContent(getIntent().getStringExtra("type"),content);
        }
    }

    private void commitContent(String type, String content) {
        HousingEstateModel defaultHouseEstate= UserIdentityInfoModel.getDefaultHouseEstate();
        Log.e("info","defaultHouseEstate"+defaultHouseEstate);
        if(defaultHouseEstate==null){
                OtherUtil.notifyBindEstate(this);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                },500);
                return;
        }
        String url ="";
        final String sendType =getIntent().getStringExtra("type");
        if("myRepair".equals(sendType)){
            url= HttpUtil.getRepair_url;
        }else if("suggestions".equals(sendType)){
            url = HttpUtil.getSuggestion_url;
        }else if("bossAccess".equals(sendType)){
            url = HttpUtil.getBossStraight_url;
        }else{
            return;
        }

        Map<String,String> params = new HashMap<String, String>();
        params.put("Content",content);
        params.put("VillageId",defaultHouseEstate.getId()+"");
        loading_page.showAtLocation(rootView, Gravity.CENTER, 0, 0);
        HttpUtil.sendVolleyStringRequest_Post(this,url,params, DBUtil.getLoginUser().getToken(),"yellow_SimplePostInfoActivity",new WanglinHttpResponseListener(){
            @Override
            public void onSuccessResponse(JSONObject jsonObject_response) {
                try {
                    loading_page.dismiss();
                    if("myRepair".equals(sendType)){
                        //获取报修ID并处理
                        Log.e("info",jsonObject_response.toString());
                    }
                    Toast.makeText(SimplePostInfoActivity.this,"发送成功.",Toast.LENGTH_SHORT).show();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    },500);
                }catch (Exception e){
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
