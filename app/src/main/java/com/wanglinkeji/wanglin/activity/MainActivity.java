package com.wanglinkeji.wanglin.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.model.NewFriendInfoModel;
import com.wanglinkeji.wanglin.model.UserIdentityInfoModel;
import com.wanglinkeji.wanglin.util.DBUtil;
import com.wanglinkeji.wanglin.util.HttpUtil;
import com.wanglinkeji.wanglin.util.LogoutActivityCollector;
import com.wanglinkeji.wanglin.util.OtherUtil;
import com.wanglinkeji.wanglin.util.WangLinApplication;
import com.wanglinkeji.wanglin.util.WanglinHttpResponseListener;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECInitParams;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.OnChatReceiveListener;
import com.yuntongxun.ecsdk.SdkErrorCode;
import com.yuntongxun.ecsdk.im.ECImageMessageBody;
import com.yuntongxun.ecsdk.im.ECMessageNotify;
import com.yuntongxun.ecsdk.im.ECTextMessageBody;
import com.yuntongxun.ecsdk.im.ECVoiceMessageBody;
import com.yuntongxun.ecsdk.im.group.ECGroupNoticeMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by yellow on 2016/8/15.
 * main_activity(入口页)
 */
public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private LinearLayout layout_addPage, layout_issueActivity, layout_issueCityBlog, layout_issueHouseEstateBlog;

    private RelativeLayout  layout_bottomBtn_housingEstate, layout_bottomBtn_neighbor, layout_bottomBtn_CertifiedProperty, layout_bottomBtn_userCenter;

    private TextView textView_bottomBtn_housingEstate, textView_bottomBtn_neighbor, textView_bottomBtn_CertifiedProperty, textView_bottomBtn_userCenter,
                        textView_bottom_messageCount,textView_fragmentNeighbor_newFriendsMessageCount;

    private ImageView imageView_bottomBtn_housingEstate, imageView_bottomBtn_neighbor, imageView_bottomBtn_CertifiedProperty, imageView_bottomBtn_userCenter,
                        imageView_bottomBtn_addBlog, imageView_addPage_cancle, imageView_newFriendRightGo;

    private Fragment[] fragmens;

    private FragmentManager fragmentManager;

    private FragmentTransaction fragmentTransaction;

    //获取消息线程是否运行
    private boolean isThreadRunning = true;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                //加号发表页消失
                case -111:{
                    layout_addPage.setVisibility(View.GONE);
                    break;
                }
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_activity_main);
        LogoutActivityCollector.addActivity(MainActivity.this);

        viewInit();
        //初始化IMSDK
        initIMSDK();
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isThreadRunning){
                    try {
                        getNotRead_NewFriendList(MainActivity.this, 1000, 0, "", 1, 10000);
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        isThreadRunning = false;
        LogoutActivityCollector.removeActivity(MainActivity.this);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.hide(fragmens[0]).hide(fragmens[1]).hide(fragmens[2]).hide(fragmens[3]);
        switch (view.getId()){
            //底部“小区”按钮
            case R.id.layout_homePage_bottomBtn_housingEstate:{
                fragmentTransaction.show(fragmens[0]).commit();
                imageView_bottomBtn_housingEstate.setImageResource(R.mipmap.housing_estate_blue);
                textView_bottomBtn_housingEstate.setTextColor(getResources().getColor(R.color.blue));
                imageView_bottomBtn_neighbor.setImageResource(R.mipmap.neighbor_icon_white);
                textView_bottomBtn_neighbor.setTextColor(getResources().getColor(R.color.gray));
                imageView_bottomBtn_CertifiedProperty.setImageResource(R.mipmap.certified_property_icon_white);
                textView_bottomBtn_CertifiedProperty.setTextColor(getResources().getColor(R.color.gray));
                imageView_bottomBtn_userCenter.setImageResource(R.mipmap.user_center_icon_white);
                textView_bottomBtn_userCenter.setTextColor(getResources().getColor(R.color.gray));
                break;
            }
            //底部“邻居”按钮
            case R.id.layout_homePage_bottomBtn_neighbor:{
                fragmentTransaction.show(fragmens[1]).commit();
                imageView_bottomBtn_housingEstate.setImageResource(R.mipmap.housing_estate_white);
                textView_bottomBtn_housingEstate.setTextColor(getResources().getColor(R.color.gray));
                imageView_bottomBtn_neighbor.setImageResource(R.mipmap.neighbor_blue);
                textView_bottomBtn_neighbor.setTextColor(getResources().getColor(R.color.blue));
                imageView_bottomBtn_CertifiedProperty.setImageResource(R.mipmap.certified_property_icon_white);
                textView_bottomBtn_CertifiedProperty.setTextColor(getResources().getColor(R.color.gray));
                imageView_bottomBtn_userCenter.setImageResource(R.mipmap.user_center_icon_white);
                textView_bottomBtn_userCenter.setTextColor(getResources().getColor(R.color.gray));
                break;
            }
            //底部“加号”按钮
            case R.id.imageview_homePage_bottomBtn_addBlog:{
                layout_addPage.setVisibility(View.VISIBLE);
                addPageAppearAnim();
                break;
            }
            //底部“物业”按钮
            case R.id.layout_homePage_bottomBtn_certifiedProperty:{
                fragmentTransaction.show(fragmens[2]).commit();
                imageView_bottomBtn_housingEstate.setImageResource(R.mipmap.housing_estate_white);
                textView_bottomBtn_housingEstate.setTextColor(getResources().getColor(R.color.gray));
                imageView_bottomBtn_neighbor.setImageResource(R.mipmap.neighbor_icon_white);
                textView_bottomBtn_neighbor.setTextColor(getResources().getColor(R.color.gray));
                imageView_bottomBtn_CertifiedProperty.setImageResource(R.mipmap.certified_property_icon_blue);
                textView_bottomBtn_CertifiedProperty.setTextColor(getResources().getColor(R.color.blue));
                imageView_bottomBtn_userCenter.setImageResource(R.mipmap.user_center_icon_white);
                textView_bottomBtn_userCenter.setTextColor(getResources().getColor(R.color.gray));
                break;
            }
            //底部“我的”按钮
            case R.id.layout_homePage_bottomBtn_userCenter:{
                fragmentTransaction.show(fragmens[3]).commit();
                imageView_bottomBtn_housingEstate.setImageResource(R.mipmap.housing_estate_white);
                textView_bottomBtn_housingEstate.setTextColor(getResources().getColor(R.color.gray));
                imageView_bottomBtn_neighbor.setImageResource(R.mipmap.neighbor_icon_white);
                textView_bottomBtn_neighbor.setTextColor(getResources().getColor(R.color.gray));
                imageView_bottomBtn_CertifiedProperty.setImageResource(R.mipmap.certified_property_icon_white);
                textView_bottomBtn_CertifiedProperty.setTextColor(getResources().getColor(R.color.gray));
                imageView_bottomBtn_userCenter.setImageResource(R.mipmap.user_center_icon_blue);
                textView_bottomBtn_userCenter.setTextColor(getResources().getColor(R.color.blue));
                break;
            }
            //加号发表页返回按钮
            case R.id.imageview_popwindow_homePageAddPage_cancle:{
                addPageGoneAnim();
                break;
            }
            //发起活动
            case R.id.layout_homePage_issue_activity:{
                break;
            }
            //发表同城吐槽
            case R.id.layout_homePage_issue_cityBlog:{
                Intent intent = new Intent(MainActivity.this, IssueBlogActivity.class);
                intent.putExtra("whatFrom", IssueBlogActivity.WHAT_FROM_CITY);
                startActivity(intent);
                layout_addPage.setVisibility(View.GONE);
                break;
            }
            //发表小区吐槽
            case R.id.layout_homePage_issue_houseEstateBlog:{
                if (UserIdentityInfoModel.getDefaultHouseEstate() == null){
                    Toast.makeText(MainActivity.this, "您还没有设置默认小区哦！", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(MainActivity.this, IssueBlogActivity.class);
                    intent.putExtra("whatFrom", IssueBlogActivity.WHAT_FROM_HOUSE_ESTATE);
                    startActivity(intent);
                    layout_addPage.setVisibility(View.GONE);
                }
                break;
            }
            default:
                break;
        }
    }

    private void viewInit(){
        //初始化控件
        imageView_bottomBtn_addBlog = (ImageView)findViewById(R.id.imageview_homePage_bottomBtn_addBlog);
        imageView_bottomBtn_addBlog.setOnClickListener(this);
        layout_bottomBtn_housingEstate = (RelativeLayout) findViewById(R.id.layout_homePage_bottomBtn_housingEstate);
        layout_bottomBtn_housingEstate.setOnClickListener(this);
        layout_bottomBtn_neighbor = (RelativeLayout) findViewById(R.id.layout_homePage_bottomBtn_neighbor);
        layout_bottomBtn_neighbor.setOnClickListener(this);
        layout_bottomBtn_CertifiedProperty = (RelativeLayout) findViewById(R.id.layout_homePage_bottomBtn_certifiedProperty);
        layout_bottomBtn_CertifiedProperty.setOnClickListener(this);
        layout_bottomBtn_userCenter = (RelativeLayout) findViewById(R.id.layout_homePage_bottomBtn_userCenter);
        layout_bottomBtn_userCenter.setOnClickListener(this);
        textView_bottomBtn_housingEstate = (TextView)findViewById(R.id.textview_homePage_bottomBtn_housingEstate);
        textView_bottomBtn_neighbor = (TextView)findViewById(R.id.textview_homePage_bottomBtn_neighbor);
        textView_bottomBtn_CertifiedProperty = (TextView)findViewById(R.id.textview_homePage_bottomBtn_certifiedProperty);
        textView_bottomBtn_userCenter = (TextView)findViewById(R.id.textview_homePage_bottomBtn_userCenter);
        imageView_bottomBtn_housingEstate = (ImageView)findViewById(R.id.imageview_homePage_bottomBtn_housingEstate);
        imageView_bottomBtn_neighbor = (ImageView)findViewById(R.id.imageview_homePage_bottomBtn_neighbor);
        imageView_bottomBtn_CertifiedProperty = (ImageView)findViewById(R.id.imageview_homePage_bottomBtn_certifiedProperty);
        imageView_bottomBtn_userCenter = (ImageView)findViewById(R.id.imageview_homePage_bottomBtn_userCenter);

        //初始化加号发表页
        layout_addPage = (LinearLayout)findViewById(R.id.layout_homePage_addPage);
        layout_addPage.setOnClickListener(this);
        imageView_addPage_cancle = (ImageView)findViewById(R.id.imageview_popwindow_homePageAddPage_cancle);
        imageView_addPage_cancle.setOnClickListener(this);
        layout_issueActivity = (LinearLayout)findViewById(R.id.layout_homePage_issue_activity);
        layout_issueActivity.setOnClickListener(this);
        layout_issueCityBlog = (LinearLayout)findViewById(R.id.layout_homePage_issue_cityBlog);
        layout_issueCityBlog.setOnClickListener(this);
        layout_issueHouseEstateBlog = (LinearLayout)findViewById(R.id.layout_homePage_issue_houseEstateBlog);
        layout_issueHouseEstateBlog.setOnClickListener(this);

        //初始化Fragment
        fragmentManager = getSupportFragmentManager();
        fragmens = new Fragment[4];
        fragmens[0] = fragmentManager.findFragmentById(R.id.fragment_homePage_housingEstate);
        fragmens[1] = fragmentManager.findFragmentById(R.id.fragment_homePage_neighbor);
        fragmens[2] = fragmentManager.findFragmentById(R.id.fragment_homePage_certifiedProperty);
        fragmens[3] = fragmentManager.findFragmentById(R.id.fragment_homePage_userCenter);
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.hide(fragmens[0]).hide(fragmens[1]).hide(fragmens[2]).hide(fragmens[3]).show(fragmens[0]).commit();

        textView_bottom_messageCount = (TextView)findViewById(R.id.textview_homePage_bottomNeighbor_messageNum);
        textView_fragmentNeighbor_newFriendsMessageCount = (TextView)findViewById(R.id.textview_fragmentNeighbor_newFriendMessageNum);
        imageView_newFriendRightGo = (ImageView)findViewById(R.id.imageview_fragmentNeighbor_newFriendRightGo);
    }

    private void addPageAppearAnim(){
        layout_issueActivity.startAnimation(OtherUtil.get_Translate_Anim(400, -1200, 0, 0, 0));
        layout_issueCityBlog.startAnimation(OtherUtil.get_Translate_Anim(300, -900, 0, 0, 0));
        layout_issueHouseEstateBlog.startAnimation(OtherUtil.get_Translate_Anim(200, -600, 0, 0, 0));
        imageView_addPage_cancle.startAnimation(OtherUtil.get_Rotate_Anim(300, -90, 0, 0.5f, 0.5f, true));
    }

    private void addPageGoneAnim(){
        layout_issueActivity.startAnimation(OtherUtil.get_Translate_Anim(500, 0, 1200, 0, 0));
        layout_issueCityBlog.startAnimation(OtherUtil.get_Translate_Anim(400, 0, 1200, 0, 0));
        layout_issueHouseEstateBlog.startAnimation(OtherUtil.get_Translate_Anim(300, 0, 1200, 0, 0));
        imageView_addPage_cancle.startAnimation(OtherUtil.get_Rotate_Anim(300, 0, 90, 0.5f, 0.5f, true));
        handler.sendEmptyMessageDelayed(-111, 300);
    }

    private int getNotReadNewFriendMessage(List<NewFriendInfoModel> list_message){
        int count = 0;
        for (int i = 0; i < list_message.size(); i++){
            if (list_message.get(i).getInfoStatus() == NewFriendInfoModel.INFO_STATUS_NOT_READ){
                count++;
            }
        }
        return count;
    }

    private int getNotReadMessage(){
        int count = 0;
        count += getNotReadNewFriendMessage(NewFriendInfoModel.list_newFriends);
        return count;
    }

    private void getNotRead_NewFriendList(Context context, int msgType, int msgState, String key, int pageIndex, int pageSize){
        Map<String, String> params = new HashMap<>();
        params.put("MsgType", String.valueOf(msgType));
        params.put("MsgStatus", String.valueOf(msgState));
        params.put("Key", key);
        params.put("PageIndex", String.valueOf(pageIndex));
        params.put("PageSize", String.valueOf(pageSize));
        HttpUtil.sendVolleyStringRequest_Post(context, HttpUtil.getMessage_url, params, DBUtil.getLoginUser().getToken(), "yellow_getNotRead_NewFriendList",
                new WanglinHttpResponseListener() {
                    @Override
                    public void onSuccessResponse(JSONObject jsonObject_response) {
                        try {
                            JSONArray jsonArray_data = jsonObject_response.getJSONArray("Data");
                            NewFriendInfoModel.list_newFriends = new ArrayList<NewFriendInfoModel>();
                            for (int i = 0; i < jsonArray_data.length(); i++){
                                JSONObject jsonObject_info = jsonArray_data.getJSONObject(i);
                                NewFriendInfoModel newFriendInfoModel = new NewFriendInfoModel();
                                newFriendInfoModel.setInfoId(jsonObject_info.getInt("Id"));//设置消息Id
                                newFriendInfoModel.setNewFriendId(jsonObject_info.getInt("FromUserId"));//设置添加好友的Id
                                newFriendInfoModel.setNewFriendName(jsonObject_info.getString("FromUserNick"));//设置添加好友的昵称
                                newFriendInfoModel.setInfoStatus(jsonObject_info.getInt("Status"));//设置消息状态
                                newFriendInfoModel.setNewFriendMessage(jsonObject_info.getString("Msg"));//设置验证消息
                                //判断，如果是未处理的消息加入到队列中
                                if (jsonObject_info.getInt("Status") == 0){
                                    NewFriendInfoModel.list_newFriends.add(newFriendInfoModel);
                                }
                            }
                            if (getNotReadNewFriendMessage(NewFriendInfoModel.list_newFriends) > 0){
                                imageView_newFriendRightGo.setVisibility(View.GONE);
                                textView_fragmentNeighbor_newFriendsMessageCount.setVisibility(View.VISIBLE);
                                textView_fragmentNeighbor_newFriendsMessageCount.setText(String.valueOf(getNotReadNewFriendMessage(NewFriendInfoModel.list_newFriends)));
                            }else {
                                textView_fragmentNeighbor_newFriendsMessageCount.setVisibility(View.GONE);
                                imageView_newFriendRightGo.setVisibility(View.VISIBLE);
                            }
                            if (getNotReadMessage() > 0){
                                textView_bottom_messageCount.setVisibility(View.VISIBLE);
                                textView_bottom_messageCount.setText(String.valueOf(getNotReadMessage()));
                            }else {
                                textView_bottom_messageCount.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onConnectingError() {

                    }

                    @Override
                    public void onDisconnectError() {

                    }
                });
    }

    private void initIMSDK(){
        if (!ECDevice.isInitialized()){
            ECDevice.initial(MainActivity.this, new ECDevice.InitListener() {
                @Override
                public void onInitialized() {
                    Log.d("yellow_IM", "初始化IM_SDK成功");
                    //登录
                    ECInitParams params = ECInitParams.createParams();
                    //IM用户账号（这里为网邻账号：手机号）
                    params.setUserid(DBUtil.getLoginUser().getPhone());
                    //IM_APP_ID
                    params.setAppKey(HttpUtil.IM_APP_ID);
                    //IM_APP_TOKEN
                    params.setToken(HttpUtil.IM_APP_TOKEN);
                    //设置登陆验证模式：自定义登录方式
                    params.setAuthType(ECInitParams.LoginAuthType.NORMAL_AUTH);
                    //LoginMode（强制上线：FORCE_LOGIN  默认登录：AUTO。使用方式详见注意事项）
                    params.setMode(ECInitParams.LoginMode.FORCE_LOGIN);

                    //设置登录回调监听
                    ECDevice.setOnDeviceConnectListener(new ECDevice.OnECDeviceConnectListener() {
                        public void onConnect() {
                            //兼容旧版本的方法，不必处理
                        }

                        @Override
                        public void onDisconnect(ECError error) {
                            //兼容旧版本的方法，不必处理
                        }

                        @Override
                        public void onConnectState(ECDevice.ECConnectState state, ECError error) {
                            if(state == ECDevice.ECConnectState.CONNECT_FAILED ){
                                if(error.errorCode == SdkErrorCode.SDK_KICKED_OFF) {
                                    Log.i("yellow_IM","loginState:帐号异地登陆");
                                }
                                else {
                                    Log.i("yellow_IM","loginState:其他登录失败,错误码："+ error.errorCode);
                                }
                                return ;
                            }
                            else if(state == ECDevice.ECConnectState.CONNECT_SUCCESS) {
                                Log.i("yellow_IM","loginState:登陆成功");
                            }
                        }
                    });

                    //设置消息接受监听
                    setIM_OnChatReceiveListener();

                    //验证参数是否正确
                    if(params.validate()) {
                        // 登录函数
                        ECDevice.login(params);
                    }
                }

                @Override
                public void onError(Exception e) {
                    Log.d("yellow_IM","初始化SDK失败--->" + e.getMessage());
                }
            });
        }
    }

    public static void setIM_OnChatReceiveListener(){
        //IM接收消息监听，使用IM功能的开发者需要设置。
        ECDevice.setOnChatReceiveListener(new OnChatReceiveListener() {
            @Override
            public void OnReceivedMessage(ECMessage msg) {
                //Log.d("yellow_IM", "MainActivity---OnReceiveMessage()");
                if(msg == null) {
                    return ;
                }
                //发出通知
                WangLinApplication.notificationManager.notify((new Long((new Date()).getTime())).intValue(), WangLinApplication.notificationBuilder.build());

                //处理消息
                ECMessage.Type type = msg.getType();
                if(type == ECMessage.Type.TXT) {
                    ECTextMessageBody textMessageBody = (ECTextMessageBody) msg.getBody();
                }else if (type == ECMessage.Type.VOICE){
                    ECVoiceMessageBody voiceMsgBody = (ECVoiceMessageBody) msg.getBody();

                }else if (type == ECMessage.Type.IMAGE){
                    ECImageMessageBody imageMsgBody = (ECImageMessageBody) msg.getBody();

                }
                // 根据不同类型处理完消息之后，将消息序列化到本地存储（sqlite）
                // 通知UI有新消息到达
            }

            @Override
            public void onReceiveMessageNotify(ECMessageNotify ecMessageNotify) {}
            @Override
            public void OnReceiveGroupNoticeMessage(ECGroupNoticeMessage ecGroupNoticeMessage) {}
            @Override
            public void onOfflineMessageCount(int i) {
                if (i > 0){
                    WangLinApplication.notificationManager.notify((new Long((new Date()).getTime())).intValue(), WangLinApplication.notificationBuilder.build());
                }
            }
            @Override
            public int onGetOfflineMessage() {
                return -1;
            }
            @Override
            public void onReceiveOfflineMessage(List<ECMessage> list) {
                for (int i = 0; i < list.size(); i++){
                    ECMessage.Type type = list.get(i).getType();
                    if (type == ECMessage.Type.TXT){
                        ECTextMessageBody textMessageBody = (ECTextMessageBody) list.get(i).getBody();
                    }else if (type == ECMessage.Type.VOICE){
                        ECVoiceMessageBody voiceMsgBody = (ECVoiceMessageBody) list.get(i).getBody();
                    }else if (type == ECMessage.Type.IMAGE){
                        ECImageMessageBody imageMsgBody = (ECImageMessageBody) list.get(i).getBody();
                    }
                }
            }
            @Override
            public void onReceiveOfflineMessageCompletion() {}
            @Override
            public void onServicePersonVersion(int i) {}
            @Override
            public void onReceiveDeskMessage(ECMessage ecMessage) {}
            @Override
            public void onSoftVersion(String s, int i) {}
        });
    }
}
