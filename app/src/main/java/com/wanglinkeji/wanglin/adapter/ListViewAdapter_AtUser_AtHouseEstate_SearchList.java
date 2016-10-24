package com.wanglinkeji.wanglin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.model.UserFriendModel;

import java.util.List;

/**
 * Created by Administrator on 2016/9/23.
 * @ 好友界面， @ 小区好友，搜索ListView Adapter
 */

public class ListViewAdapter_AtUser_AtHouseEstate_SearchList extends BaseAdapter {

    private List<UserFriendModel> list_friends;

    private Context context;

    private int resource;

    public ListViewAdapter_AtUser_AtHouseEstate_SearchList(List<UserFriendModel> list_friends, Context context, int resource){
        this.list_friends = list_friends;
        this.context = context;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        return list_friends.size();
    }

    @Override
    public Object getItem(int i) {
        return list_friends.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(resource, null);
        ImageView imageView_header = (ImageView)view.findViewById(R.id.imageview_listviewItem_AtHouseEstate_header);
        TextView textView_name = (TextView)view.findViewById(R.id.textview_listviewItem_AtHouseEstate_name);
        textView_name.setText(list_friends.get(position).getName());
        return view;
    }
}
