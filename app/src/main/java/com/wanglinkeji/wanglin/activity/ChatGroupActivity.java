package com.wanglinkeji.wanglin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.adapter.ListViewAdapter_ChatGroup;
import com.wanglinkeji.wanglin.model.ChatGroupItem_Model;
import com.wanglinkeji.wanglin.model.ChatListItemModel;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/30.
 * 聊天群Activity
 */

public class ChatGroupActivity extends Activity implements View.OnClickListener {

    private ImageView imageView_cancle;

    private ListView listView_groupList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_activity_chat_group);

        viewInit();
        getChatGroup();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imageview_chatGroup_cancle:{
                ChatGroupActivity.this.finish();
                break;
            }
            default:
                break;
        }
    }

    private void viewInit(){
        imageView_cancle = (ImageView)findViewById(R.id.imageview_chatGroup_cancle);
        imageView_cancle.setOnClickListener(this);
        listView_groupList = (ListView)findViewById(R.id.listview_chatGroup_groupList);
        listView_groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ChatGroupActivity.this, ChatActivity.class);
                intent.putExtra("chatName", ChatGroupItem_Model.list_group.get((new Long(l).intValue())).getGroupName());
                startActivity(intent);
            }
        });
    }

    private void getChatGroup(){
        ChatGroupItem_Model.list_group = new ArrayList<>();

        ChatGroupItem_Model chatGroupItem_model = new ChatGroupItem_Model();
        chatGroupItem_model.setGroupName("阿加西们");

        ChatGroupItem_Model chatGroupItem_model1 = new ChatGroupItem_Model();
        chatGroupItem_model1.setGroupName("会计3班");

        ChatGroupItem_Model chatGroupItem_model2 = new ChatGroupItem_Model();
        chatGroupItem_model2.setGroupName("英雄联盟开黑群");

        ChatGroupItem_Model.list_group.add(chatGroupItem_model);
        ChatGroupItem_Model.list_group.add(chatGroupItem_model1);
        ChatGroupItem_Model.list_group.add(chatGroupItem_model2);
        listView_groupList.setAdapter(new ListViewAdapter_ChatGroup(ChatGroupItem_Model.list_group, ChatGroupActivity.this,
                R.layout.layout_listview_item_chat_group));
    }
}
