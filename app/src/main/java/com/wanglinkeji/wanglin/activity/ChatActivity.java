package com.wanglinkeji.wanglin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.adapter.ListViewAdapter_Chat;
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
import com.yuntongxun.ecsdk.im.ECMessageNotify;
import com.yuntongxun.ecsdk.im.ECTextMessageBody;
import com.yuntongxun.ecsdk.im.group.ECGroupNoticeMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/10/2.
 * 聊天界面
 */

public class ChatActivity extends Activity implements View.OnClickListener {

    private ImageView imageView_cancle, imageView_voice, imageView_face, imageView_picture, imageView_redBag;

    private TextView textView_chatName, textView_send;

    private EditText editText_content;

    private ListView listView_ChatList;

    private List<ChatItemMoeld> list_chatItem = new ArrayList<>();

    private ListViewAdapter_Chat listViewAdapter_chat = new ListViewAdapter_Chat(list_chatItem, ChatActivity.this, R.layout.layout_listview_item_chat);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_activity_chat);

        setChatReceiveListener();
        viewInit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case OtherUtil.REQUEST_CODE_CHAT_TO_CHOOSED_PHOTO:{
                if (resultCode == RESULT_OK){

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
                sendTextMessage(editText_content.getText().toString());
                editText_content.setText("");
                break;
            }
            //语音按钮
            case R.id.imageview_chat_voice:{
                break;
            }
            //表情按钮
            case R.id.imageview_chat_face:{
                Toast.makeText(ChatActivity.this, "目前只支持输入法自带Emoji表情", Toast.LENGTH_SHORT).show();
                break;
            }
            //图片按钮
            case R.id.imageview_chat_picture:{
                PhotoModel.photoCount = 1;
                PhotoModel.finishText = "上传";
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
        listView_ChatList = (ListView)findViewById(R.id.listview_chat_chatList);
        listView_ChatList.setAdapter(listViewAdapter_chat);
        editText_content = (EditText)findViewById(R.id.edittext_chat_content);
    }

    private void setChatReceiveListener(){
        //IM接收消息监听，使用IM功能的开发者需要设置。
        ECDevice.setOnChatReceiveListener(new OnChatReceiveListener() {
            @Override
            public void OnReceivedMessage(ECMessage msg) {
                //Log.d("yellow_IM", "OnReceiveMessage()--->ChatActivity");
                if(msg == null) {
                    return ;
                }
                ECMessage.Type type = msg.getType();
                if(type == ECMessage.Type.TXT) {
                    // 在这里处理文本消息
                    ECTextMessageBody textMessageBody = (ECTextMessageBody) msg.getBody();
                    String content = textMessageBody.getMessage();
                    //Log.d("yellow_IM", "（ChatActivity）接收消息：" + content);

                    ChatItemMoeld chatItemMoeld = new ChatItemMoeld();
                    chatItemMoeld.setMessageFrom(ChatItemMoeld.MESSAGE_FROM_FRIEND);
                    chatItemMoeld.setFriendNickName(UserFriendModel.list_friends.get(UserFriendModel.chatPosition).getName());
                    chatItemMoeld.setFriendContent(content);
                    Date date = new Date();
                    chatItemMoeld.setRealDate(date);
                    chatItemMoeld.setShowDate(OtherUtil.getCurrentTime(date));
                    list_chatItem.add(chatItemMoeld);
                    //listView_ChatList.setAdapter(new ListViewAdapter_Chat(list_chatItem, ChatActivity.this, R.layout.layout_listview_item_chat));
                    listViewAdapter_chat.notifyDataSetChanged();
                    listView_ChatList.setSelection(list_chatItem.size() - 1);
                }
                // 根据不同类型处理完消息之后，将消息序列化到本地存储（sqlite）
                // 通知UI有新消息到达
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
        chatItemMoeld.setMessageFrom(ChatItemMoeld.MESSAGE_FROM_ME);
        chatItemMoeld.setMessageSendState(ChatItemMoeld.MESSAGE_SEND_STATE_ING);
        chatItemMoeld.setMyNickName(UserIdentityInfoModel.userName);
        chatItemMoeld.setMyContent(content);
        Date date = new Date();
        chatItemMoeld.setRealDate(date);
        chatItemMoeld.setShowDate(OtherUtil.getCurrentTime(date));
        list_chatItem.add(chatItemMoeld);
        //listView_ChatList.setAdapter(new ListViewAdapter_Chat(list_chatItem, ChatActivity.this, R.layout.layout_listview_item_chat));
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
                // 将发送的消息更新到本地数据库并刷新UI
                Log.d("yellow_IM", "发送成功：" + content);
                chatItemMoeld.setMessageSendState(ChatItemMoeld.MESSAGE_SEND_STATE_FINISH);
                //listView_ChatList.setAdapter(new ListViewAdapter_Chat(list_chatItem, ChatActivity.this, R.layout.layout_listview_item_chat));
                listViewAdapter_chat.notifyDataSetChanged();
                listView_ChatList.setSelection(list_chatItem.size() - 1);
            }
            @Override
            public void onProgress(String msgId, int totalByte, int progressByte) {
                // 处理文件发送上传进度（尽上传文件、图片时候SDK回调该方法）
            }
        });
    }
}
