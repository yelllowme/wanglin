package com.wanglinkeji.wanglin.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.customerview.LeftRightTxtPrecentView;
import com.wanglinkeji.wanglin.customerview.MaterialDialog;
import com.wanglinkeji.wanglin.model.HousingEstateModel;
import com.wanglinkeji.wanglin.model.UserIdentityInfoModel;
import com.wanglinkeji.wanglin.util.DBUtil;
import com.wanglinkeji.wanglin.util.HttpUtil;
import com.wanglinkeji.wanglin.util.OtherUtil;
import com.wanglinkeji.wanglin.util.WanglinHttpResponseListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class PraiseCriticizeActivity extends Activity implements View.OnClickListener {
    private LeftRightTxtPrecentView rpb;
    private View popView;
    private boolean isDialogShow;
    private MaterialDialog dialog;

    private ImageView imageview_back;
    private TextView textview_praise,textview_criticize;
    private LinearLayout ll_zan,ll_cai;

    private PopupWindow loading_page;//进度条
    private View rootView;

    //‘踩’ 对话框
    private EditText etContent;
    private TextView tvCommit;

    private int zanCount,caiCount;

    public static final int TYPE_ZAN=1;
    public static final int TYPE_CAI=2;

    private HousingEstateModel defaultHouseEstate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_praise_criticize);
        initViews();
        setListener();
        initData();
    }

    private void initData() {
        defaultHouseEstate= UserIdentityInfoModel.getDefaultHouseEstate();
        Log.e("info","defaultHouseEstate"+defaultHouseEstate);
        if(defaultHouseEstate==null){
            OtherUtil.notifyBindEstate(this);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 500);
            return;
        }
        String url = HttpUtil.getFindCompany;
        Map<String,String> params = new HashMap<String, String>();
        params.put("villageId",defaultHouseEstate.getId()+"");
        HttpUtil.sendVolleyStringRequest_Post(this,url,params, DBUtil.getLoginUser().getToken(),"yellow_getFindCompanyInfo",new WanglinHttpResponseListener(){
            @Override
            public void onSuccessResponse(JSONObject jsonObject_response) {
                try {
                    JSONObject data =jsonObject_response.getJSONObject("Data");
                    zanCount =data.getInt("GoodCount");
                    caiCount =data.getInt("BadCount");
                    double total =zanCount+caiCount;
                    float caiPrecent = ((float) ((int)(caiCount/total *1000)))/10;
                    rpb.setRightPrecent(caiPrecent);
                    textview_praise.setText((100-caiPrecent)+"%");
                    textview_criticize.setText(caiPrecent+"%");
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                }
            }
            @Override
            public void onConnectingError(){
            }
            @Override
            public void onDisconnectError() {
            }
        });
    }

    private void setListener() {
        imageview_back.setOnClickListener(this);
        ll_zan.setOnClickListener(this);
        ll_cai.setOnClickListener(this);
    }

    private void showDialog() {
        WindowManager windowManager =  getWindowManager();
        int height = (int) (windowManager.getDefaultDisplay().getHeight() *0.34);
        ViewGroup.LayoutParams viewParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height);
        popView = LayoutInflater.from(this).inflate(R.layout.collection_criticize,null);

        etContent = (EditText) popView.findViewById(R.id.collect_criticize);
        tvCommit =(TextView)popView.findViewById(R.id.criticize_send);
        tvCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postZanOrCai(TYPE_CAI);
            }
        });

        popView.setLayoutParams(viewParams);
        dialog = new MaterialDialog(this);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isDialogShow = false;
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.setView(popView);
        dialog.show();
        isDialogShow = true;
    }

    private void initViews() {
        rpb = (LeftRightTxtPrecentView) findViewById(R.id.rpb);

        imageview_back= (ImageView)findViewById(R.id.imageview_back);
        ll_zan =(LinearLayout)findViewById(R.id.ll_zan);
        ll_cai =(LinearLayout)findViewById(R.id.ll_cai);
        textview_criticize=(TextView)findViewById(R.id.textview_criticize);
        textview_praise=(TextView)findViewById(R.id.textview_praise);

        loading_page = OtherUtil.getLoadingPage(this);
        rootView = LayoutInflater.from(this).inflate(R.layout.collection_criticize,null);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(isDialogShow){
                isDialogShow=false;
                dialog.dismiss();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case  R.id.imageview_back:
                finish();
                break;
            case R.id.ll_zan:
                postZanOrCai(TYPE_ZAN);
                break;
            case R.id.ll_cai:
                showDialog();
                break;
            default:
                break;
        }
    }

    private void postZanOrCai(final int type) {
        String url = type==TYPE_ZAN?HttpUtil.getAddGood:HttpUtil.getAddBad;
        Map<String,String> params = new HashMap<String, String>();
        params.put("villageId",defaultHouseEstate.getId()+"");
        params.put("content",type==TYPE_ZAN?"":etContent.getText().toString());
        View v = rootView;
        if(isDialogShow){
            v = popView;
        }
        loading_page.showAtLocation(v, Gravity.CENTER,0,0);
        HttpUtil.sendVolleyStringRequest_Post(this,url,params, DBUtil.getLoginUser().getToken(),"yellow_PraiseCriticizeInfo",new WanglinHttpResponseListener(){
            @Override
            public void onSuccessResponse(JSONObject jsonObject_response) {
                try {
                    Log.e("info",jsonObject_response.toString());
                    Toast.makeText(PraiseCriticizeActivity.this,type==TYPE_ZAN?"点赞成功!":"点踩成功!",Toast.LENGTH_SHORT).show();
                    if(isDialogShow){
                        dialog.dismiss();
                    }
                    //更新踩赞数据
                    updateCaiZan(type);
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(PraiseCriticizeActivity.this,"错误:"+e.getMessage(),Toast.LENGTH_SHORT).show();
                }finally {
                    loading_page.dismiss();
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


    private void updateCaiZan(int type) {
        if(type==TYPE_CAI){
            caiCount+=1;
        }else {
            zanCount+=1;
        }
        double total =zanCount+caiCount;
        float caiPrecent = ((float) ((int)(caiCount/total *1000)))/10;
        rpb.setRightPrecent(caiPrecent);
        textview_praise.setText((100-caiPrecent)+"%");
        textview_criticize.setText(caiPrecent+"%");
    }
}
