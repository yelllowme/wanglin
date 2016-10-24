package com.wanglinkeji.wanglin.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;

import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.adapter.ListViewAdapter_NewFriend;
import com.wanglinkeji.wanglin.model.NewFriendInfoModel;
import com.wanglinkeji.wanglin.model.NewsModel;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/30.
 * 新的朋友Activity
 */

public class NewFriendActivity extends Activity implements View.OnClickListener {

    private ImageView imageView_cancle;

    private ListView listView_newFriendList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_activity_new_friend);

        viewInit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imageview_newFriend_cancle:{
                NewFriendActivity.this.finish();
                break;
            }
            default:
                break;
        }
    }

    private void viewInit(){
        imageView_cancle = (ImageView)findViewById(R.id.imageview_newFriend_cancle);
        imageView_cancle.setOnClickListener(this);
        listView_newFriendList = (ListView)findViewById(R.id.listview_newFriend_newFriendList);
        listView_newFriendList.setAdapter(new ListViewAdapter_NewFriend(NewFriendInfoModel.list_newFriends, NewFriendActivity.this,
                R.layout.layout_listview_item_new_friend));
    }

}
