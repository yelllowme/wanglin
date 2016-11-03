package com.wanglinkeji.wanglin.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.adapter.ListViewAdapter_Chat;
import com.wanglinkeji.wanglin.customerview.recordbutton.AudioRecorder;
import com.wanglinkeji.wanglin.customerview.recordbutton.RecordButton;
import com.wanglinkeji.wanglin.customerview.xcpulltoloadmorelistview.XCPullToLoadMoreListView;
import com.wanglinkeji.wanglin.model.ChatItemMoeld;
import com.wanglinkeji.wanglin.model.PhotoModel;
import com.wanglinkeji.wanglin.model.UserFriendModel;
import com.wanglinkeji.wanglin.model.UserIdentityInfoModel;
import com.wanglinkeji.wanglin.util.OtherUtil;
import com.yuntongxun.ecsdk.ECChatManager;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.OnChatReceiveListener;
import com.yuntongxun.ecsdk.im.ECImageMessageBody;
import com.yuntongxun.ecsdk.im.ECMessageNotify;
import com.yuntongxun.ecsdk.im.ECTextMessageBody;
import com.yuntongxun.ecsdk.im.ECVoiceMessageBody;
import com.yuntongxun.ecsdk.im.group.ECGroupNoticeMessage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/10/2.
 * 聊天界面
 */

public class ChatActivity extends Activity implements View.OnClickListener {

    private ImageView imageView_cancle, imageView_voice, imageView_face, imageView_picture, imageView_redBag;

    private LinearLayout layout_record, layout_text;

    private RecordButton recordButton;

    private TextView textView_chatName, textView_send, textView_changeToText;

    private EditText editText_content;

    private XCPullToLoadMoreListView xcPullToLoadMoreListView;

    private ListView listView_ChatList;

    private List<ChatItemMoeld> list_chatItem = new ArrayList<>();

    private ListViewAdapter_Chat listViewAdapter_chat = new ListViewAdapter_Chat(list_chatItem, ChatActivity.this, R.layout.layout_listview_item_chat);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_activity_chat);

        viewInit();
    }

    @Override
    protected void onResume() {
        setOnThisActivityChatReceiveListener();
        super.onResume();
    }

    @Override
    protected void onPause() {
        setOnOhterActivityChatReceiveListener();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        setOnOhterActivityChatReceiveListener();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case OtherUtil.REQUEST_CODE_CHAT_TO_CHOOSED_PHOTO:{
                if (resultCode == RESULT_OK){
                    for (int i = 0; i < PhotoModel.list_choosedPhotos.size(); i++){
                        sendImageMessage(PhotoModel.list_choosedPhotos.get(i));
                    }
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //返回按钮
            case R.id.imageview_chat_cancle:{
                ChatActivity.this.finish();
                break;
            }
            //发送按钮
            case R.id.textview_chat_send:{
                if (editText_content.getText().length() == 0){
                    Toast.makeText(ChatActivity.this, "发送内容不能为空哦！", Toast.LENGTH_SHORT).show();
                }else {
                    sendTextMessage(editText_content.getText().toString());
                    editText_content.setText("");
                }
                break;
            }
            //语音按钮
            case R.id.imageview_chat_voice:{
                layout_record.setVisibility(View.VISIBLE);
                layout_text.setVisibility(View.GONE);
                //强制隐藏键盘
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(view,InputMethodManager.SHOW_FORCED);
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
                break;
            }
            //"文字"按钮
            case R.id.textview_chat_changeToText:{
                layout_record.setVisibility(View.GONE);
                layout_text.setVisibility(View.VISIBLE);
                break;
            }
            //表情按钮
            case R.id.imageview_chat_face:{
                Toast.makeText(ChatActivity.this, "目前只支持输入法自带Emoji表情", Toast.LENGTH_SHORT).show();
                break;
            }
            //图片按钮
            case R.id.imageview_chat_picture:{
                PhotoModel.photoCount = 9;
                PhotoModel.finishText = "发送";
                PhotoModel.list_choosedPhotos = new ArrayList<>();
                Intent intent = new Intent(ChatActivity.this, ChoosedPhoto_SmallActivity.class);
                startActivityForResult(intent, OtherUtil.REQUEST_CODE_CHAT_TO_CHOOSED_PHOTO);
                break;
            }
            //红包按钮
            case R.id.imageview_chat_red_bag:{
                break;
            }
            default:
                break;
        }
    }

    private void viewInit(){
        layout_record = (LinearLayout)findViewById(R.id.layout_chat_record);
        layout_text = (LinearLayout)findViewById(R.id.layout_chat_text);
        textView_changeToText = (TextView)findViewById(R.id.textview_chat_changeToText);
        textView_changeToText.setOnClickListener(this);
        imageView_cancle = (ImageView)findViewById(R.id.imageview_chat_cancle);
        imageView_cancle.setOnClickListener(this);
        textView_chatName = (TextView)findViewById(R.id.textview_chat_chatName);
        textView_chatName.setText(UserFriendModel.list_friends.get(UserFriendModel.chatPosition).getName());
        textView_send = (TextView)findViewById(R.id.textview_chat_send);
        textView_send.setOnClickListener(this);
        imageView_voice = (ImageView)findViewById(R.id.imageview_chat_voice);
        imageView_voice.setOnClickListener(this);
        imageView_face = (ImageView)findViewById(R.id.imageview_chat_face);
        imageView_face.setOnClickListener(this);
        imageView_picture = (ImageView)findViewById(R.id.imageview_chat_picture);
        imageView_picture.setOnClickListener(this);
        imageView_redBag = (ImageView)findViewById(R.id.imageview_chat_red_bag);
        imageView_redBag.setOnClickListener(this);
        xcPullToLoadMoreListView = (XCPullToLoadMoreListView) findViewById(R.id.listview_chat_chatList);
        listView_ChatList = xcPullToLoadMoreListView.getListView();
        listView_ChatList.setAdapter(listViewAdapter_chat);
        editText_content = (EditText)findViewById(R.id.edittext_chat_content);
        editText_content.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                //获得焦点的时候
                if (b == true){
                    listView_ChatList.setSelection(list_chatItem.size() - 1);
                }
            }
        });
        xcPullToLoadMoreListView.setOnRefreshListener(new XCPullToLoadMoreListView.OnRefreshListener() {
            @Override
            public void onPullDownLoadMore() {
                xcPullToLoadMoreListView.onRefreshComplete();
            }
        });
        recordButton = (RecordButton)findViewById(R.id.button_chat_recordButton);
        recordButton.setAudioRecord(new AudioRecorder());
        recordButton.setRecordListener(new RecordButton.RecordListener() {
            @Override
            public void recordEnd(String filePath) {
                sendVoiceMessage(filePath);
            }
        });
    }

    private boolean setChatItemIsShowDate(List<ChatItemMoeld> list_chatItem, Date getDate){
        if (list_chatItem.size() == 0){
            return true;
        }
        Date lastShowDate = getLastShowDate(list_chatItem);
        if (lastShowDate != null){
            if ((getDate.getTime() - lastShowDate.getTime())/(1000 * 60 * 5) > 0){
                return true;
            }
        }
        return false;
    }

    private Date getLastShowDate(List<ChatItemMoeld> list_chatItem){
        for (int i = list_chatItem.size() - 1; i >= 0; i--){
            if (list_chatItem.get(i).isShowDate() == true){
                return list_chatItem.get(i).getRealDate();
            }
        }
        return null;
    }

    private void setOnOhterActivityChatReceiveListener(){
        MainActivity.setIM_OnChatReceiveListener();
    }

    private void setOnThisActivityChatReceiveListener(){
        //IM接收消息监听，使用IM功能的开发者需要设置。
        ECDevice.setOnChatReceiveListener(new OnChatReceiveListener() {
            @Override
            public void OnReceivedMessage(ECMessage msg) {
                //Log.d("yellow_IM", "OnReceiveMessage()--->ChatActivity");
                if(msg == null) {
                    return ;
                }
                //Log.d("yellow_temp", "from--->" + msg.getForm());
                if (msg.getForm().equals(UserFriendModel.list_friends.get(UserFriendModel.chatPosition).getPhone())){
                    ECMessage.Type type = msg.getType();
                    if(type == ECMessage.Type.TXT) {
                        // 在这里处理文本消息
                        ECTextMessageBody textMessageBody = (ECTextMessageBody) msg.getBody();
                        String content = textMessageBody.getMessage();
                        //Log.d("yellow_IM", "（ChatActivity）接收消息：" + content);

                        ChatItemMoeld chatItemMoeld = new ChatItemMoeld();
                        chatItemMoeld.setMessageFrom(ChatItemMoeld.MESSAGE_FROM_FRIEND);
                        chatItemMoeld.setFriendNickName(UserFriendModel.list_friends.get(UserFriendModel.chatPosition).getName());
                        chatItemMoeld.setMessageType(ChatItemMoeld.MESSAGE_TYPE_TEXT);
                        chatItemMoeld.setFriendContent(content);
                        Date date = new Date();
                        chatItemMoeld.setRealDate(date);
                        chatItemMoeld.setShowDate(setChatItemIsShowDate(list_chatItem, date));
                        list_chatItem.add(chatItemMoeld);
                        listViewAdapter_chat.notifyDataSetChanged();
                        listView_ChatList.setSelection(list_chatItem.size() - 1);
                    }else if(type == ECMessage.Type.VOICE){
                        // 在这里处理语音消息
                        ECVoiceMessageBody voiceMsgBody = (ECVoiceMessageBody) msg.getBody();
                        ChatItemMoeld chatItemMoeld = new ChatItemMoeld();
                        chatItemMoeld.setMessageType(ChatItemMoeld.MESSAGE_TYPE_VOICE);
                        chatItemMoeld.setMessageFrom(ChatItemMoeld.MESSAGE_FROM_FRIEND);
                        chatItemMoeld.setFriendNickName(UserFriendModel.list_friends.get(UserFriendModel.chatPosition).getName());
                        chatItemMoeld.setVoicePlayState(ChatItemMoeld.VOICE_PLAY_STATE_STOP);
                        chatItemMoeld.setVoiceReadState(ChatItemMoeld.VOICE_READ_STATE_NOT_READ);
                        chatItemMoeld.setVoiceUrl(voiceMsgBody.getRemoteUrl());
                        chatItemMoeld.setVoiceLength(OtherUtil.getVoiceTimeByFileLength(voiceMsgBody.getLength()));
                        Date date = new Date();
                        chatItemMoeld.setRealDate(date);
                        chatItemMoeld.setShowDate(setChatItemIsShowDate(list_chatItem, date));
                        list_chatItem.add(chatItemMoeld);
                        listViewAdapter_chat.notifyDataSetChanged();
                        listView_ChatList.setSelection(list_chatItem.size() - 1);
                    }else if (type == ECMessage.Type.IMAGE){
                        ChatItemMoeld chatItemMoeld = new ChatItemMoeld();
                        chatItemMoeld.setMessageType(ChatItemMoeld.MESSAGE_TYPE_IMAGE);
                        Date date = new Date();
                        chatItemMoeld.setRealDate(date);
                        chatItemMoeld.setShowDate(setChatItemIsShowDate(list_chatItem, date));
                        chatItemMoeld.setMessageFrom(ChatItemMoeld.MESSAGE_FROM_FRIEND);
                        chatItemMoeld.setFriendNickName(UserFriendModel.list_friends.get(UserFriendModel.chatPosition).getName());
                        ECImageMessageBody imageMsgBody = (ECImageMessageBody) msg.getBody();
                        chatItemMoeld.setImageUrl(imageMsgBody.getThumbnailFileUrl());
                        list_chatItem.add(chatItemMoeld);
                        listViewAdapter_chat.notifyDataSetChanged();
                        listView_ChatList.setSelection(list_chatItem.size() - 1);
                    }
                }
                // 根据不同类型处理完消息之后，将消息序列化到本地存储（sqlite）
            }

            @Override
            public void onReceiveMessageNotify(ECMessageNotify ecMessageNotify) {}
            @Override
            public void OnReceiveGroupNoticeMessage(ECGroupNoticeMessage ecGroupNoticeMessage) {}
            @Override
            public void onOfflineMessageCount(int i) {}
            @Override
            public int onGetOfflineMessage() {
                return 0;
            }
            @Override
            public void onReceiveOfflineMessage(List<ECMessage> list) {}
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

    private void sendTextMessage(final String content){
        final ChatItemMoeld chatItemMoeld = new ChatItemMoeld();
        chatItemMoeld.setMessageType(ChatItemMoeld.MESSAGE_TYPE_TEXT);
        chatItemMoeld.setMessageFrom(ChatItemMoeld.MESSAGE_FROM_ME);
        chatItemMoeld.setMessageSendState(ChatItemMoeld.MESSAGE_SEND_STATE_ING);
        chatItemMoeld.setMyNickName(UserIdentityInfoModel.userName);
        chatItemMoeld.setMyContent(content);
        Date date = new Date();
        chatItemMoeld.setRealDate(date);
        chatItemMoeld.setShowDate(setChatItemIsShowDate(list_chatItem, date));
        list_chatItem.add(chatItemMoeld);
        listViewAdapter_chat.notifyDataSetChanged();
        listView_ChatList.setSelection(list_chatItem.size() - 1);

        // 组建一个待发送的ECMessage
        //消息类型
        ECMessage msg = ECMessage.createECMessage(ECMessage.Type.TXT);
        //对方账号
        msg.setTo(UserFriendModel.list_friends.get(UserFriendModel.chatPosition).getPhone());
        // 创建一个文本消息体，并添加到消息对象中
        ECTextMessageBody msgBody = new ECTextMessageBody(content);
        // 将消息体存放到ECMessage中
        msg.setBody(msgBody);
        // 调用SDK发送接口发送消息到服务器
        ECChatManager manager = ECDevice.getECChatManager();
        manager.sendMessage(msg, new ECChatManager.OnSendMessageListener() {
            @Override
            public void onSendMessageComplete(ECError error, ECMessage message) {
                // 处理消息发送结果
                if(message == null) {
                    return ;
                }
                if (error.errorCode == 200){
                    // 将发送的消息更新到本地数据库并刷新UI
                    //Log.d("yellow_IM", "发送成功：" + content);
                    chatItemMoeld.setMessageSendState(ChatItemMoeld.MESSAGE_SEND_STATE_FINISH);
                    listViewAdapter_chat.notifyDataSetChanged();
                }else {
                    chatItemMoeld.setMessageSendState(ChatItemMoeld.MESSAGE_SEND_STATE_FAILED);
                    listViewAdapter_chat.notifyDataSetChanged();
                }
            }
            @Override
            public void onProgress(String msgId, int totalByte, int progressByte) {
                // 处理文件发送上传进度（尽上传文件、图片时候SDK回调该方法）
            }
        });
    }

    private void sendVoiceMessage(String filePath){
        final ChatItemMoeld chatItemMoeld = new ChatItemMoeld();
        chatItemMoeld.setMessageType(ChatItemMoeld.MESSAGE_TYPE_VOICE);
        chatItemMoeld.setMessageFrom(ChatItemMoeld.MESSAGE_FROM_ME);
        chatItemMoeld.setMessageSendState(ChatItemMoeld.MESSAGE_SEND_STATE_ING);
        chatItemMoeld.setMyNickName(UserIdentityInfoModel.userName);
        chatItemMoeld.setVoicePlayState(ChatItemMoeld.VOICE_PLAY_STATE_STOP);
        chatItemMoeld.setVoiceLength(OtherUtil.getVoiceTimeByFileLength(filePath));
        chatItemMoeld.setLocalVoicePath(filePath);
        Date date = new Date();
        chatItemMoeld.setRealDate(date);
        chatItemMoeld.setShowDate(setChatItemIsShowDate(list_chatItem, date));
        list_chatItem.add(chatItemMoeld);
        listViewAdapter_chat.notifyDataSetChanged();
        listView_ChatList.setSelection(list_chatItem.size() - 1);

        // 组建一个待发送的ECMessage
        ECMessage message = ECMessage.createECMessage(ECMessage.Type.VOICE);
        // 设置消息接收者
        message.setTo(UserFriendModel.list_friends.get(UserFriendModel.chatPosition).getPhone());
        // 设置语音包体,语音录制文件需要保存的目录
        ECVoiceMessageBody messageBody = new ECVoiceMessageBody(new File(filePath), 0);
        message.setBody(messageBody);
        // 调用SDK发送接口发送消息到服务器
        ECChatManager manager = ECDevice.getECChatManager();
        manager.sendMessage(message, new ECChatManager.OnSendMessageListener() {
            @Override
            public void onSendMessageComplete(ECError error, ECMessage message) {
                // 处理消息发送结果
                if(message == null) {
                    return ;
                }
                if (error.errorCode == 200){
                    // 将发送的消息更新到本地数据库并刷新UI
                    //Log.d("yellow_IM", "发送成功：" + content);
                    //获取语音发送成功之后的URL
                    // 在这里处理语音消息
                    ECVoiceMessageBody voiceMsgBody = (ECVoiceMessageBody) message.getBody();
                    chatItemMoeld.setVoiceUrl(voiceMsgBody.getRemoteUrl());
                    chatItemMoeld.setMessageSendState(ChatItemMoeld.MESSAGE_SEND_STATE_FINISH);
                    listViewAdapter_chat.notifyDataSetChanged();
                    //Log.d("yellow_temp", "voiceLength = " + voiceMsgBody.getDuration() + "   voiceUrl = " + voiceMsgBody.getRemoteUrl());
                }else {
                    chatItemMoeld.setMessageSendState(ChatItemMoeld.MESSAGE_SEND_STATE_FAILED);
                    listViewAdapter_chat.notifyDataSetChanged();
                }
            }
            @Override
            public void onProgress(String msgId, int totalByte, int progressByte) {
                // 处理文件发送上传进度（尽上传文件、图片时候SDK回调该方法）
            }
        });
    }

    private void sendImageMessage(PhotoModel image){
        final ChatItemMoeld chatItemMoeld = new ChatItemMoeld();
        chatItemMoeld.setMessageType(ChatItemMoeld.MESSAGE_TYPE_IMAGE);
        chatItemMoeld.setImageLocalPath(image.getPath());
        Date date = new Date();
        chatItemMoeld.setRealDate(date);
        chatItemMoeld.setShowDate(setChatItemIsShowDate(list_chatItem, date));
        chatItemMoeld.setMessageFrom(ChatItemMoeld.MESSAGE_FROM_ME);
        chatItemMoeld.setMessageSendState(ChatItemMoeld.MESSAGE_SEND_STATE_ING);
        chatItemMoeld.setMyNickName(UserIdentityInfoModel.userName);
        list_chatItem.add(chatItemMoeld);
        listViewAdapter_chat.notifyDataSetChanged();
        listView_ChatList.setSelection(list_chatItem.size() - 1);

        // 组建一个待发送的ECMessage
        final ECMessage msg = ECMessage.createECMessage(ECMessage.Type.IMAGE);
        //对方账号
        msg.setTo(UserFriendModel.list_friends.get(UserFriendModel.chatPosition).getPhone());
        //消息实体
        ECImageMessageBody msgBody  = new ECImageMessageBody();
        // 设置附件名
        msgBody.setFileName(date.getTime() + ".jpg");
        // 设置附件扩展名
        msgBody.setFileExt("jpg");
        // 设置附件本地路径
        msgBody.setLocalUrl(image.getPath());
        // 将消息体存放到ECMessage中
        msg.setBody(msgBody);
        // 调用SDK发送接口发送消息到服务器
        ECChatManager manager = ECDevice.getECChatManager();
        manager.sendMessage(msg, new ECChatManager.OnSendMessageListener() {
            @Override
            public void onSendMessageComplete(ECError error, ECMessage message) {
                // 处理消息发送结果
                if(message == null) {
                    return ;
                }
                if (error.errorCode == 200){
                    // 将发送的消息更新到本地数据库并刷新UI
                    chatItemMoeld.setMessageSendState(ChatItemMoeld.MESSAGE_SEND_STATE_FINISH);
                    ECImageMessageBody imageMsgBody = (ECImageMessageBody) msg.getBody();
                    chatItemMoeld.setImageUrl(imageMsgBody.getThumbnailFileUrl());
                    listViewAdapter_chat.notifyDataSetChanged();
                    //Log.d("yellow_temp", "imageUrl--->" + imageMsgBody.getThumbnailFileUrl());
                }else {
                    chatItemMoeld.setMessageSendState(ChatItemMoeld.MESSAGE_SEND_STATE_FAILED);
                    listViewAdapter_chat.notifyDataSetChanged();
                }
            }
            @Override
            public void onProgress(String msgId, int totalByte, int progressByte) {
                // 处理文件发送上传进度（尽上传文件、图片时候SDK回调该方法）
            }
        });
    }
}
