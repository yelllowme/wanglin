package com.wanglinkeji.wanglin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.customerview.CircleImageView;
import com.wanglinkeji.wanglin.model.Search_UserInfoModel;

import java.util.List;

/**
 * Created by Administrator on 2016/10/2.
 * 添加好友，搜索ListViewAdapter
 */

public class ListViewAdapter_addFriend_searchList extends BaseAdapter {

    private List<Search_UserInfoModel> list_user;

    private Context context;

    private int resource;

    public ListViewAdapter_addFriend_searchList(List<Search_UserInfoModel> list_user, Context context, int resource){
        this.list_user = list_user;
        this.context = context;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        return list_user.size();
    }

    @Override
    public Object getItem(int i) {
        return list_user.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        viewHolder holder;
        if (view == null){
            view = LayoutInflater.from(context).inflate(resource, null);
            holder = new viewHolder();
            holder.imageview_header = (CircleImageView)view.findViewById(R.id.imageview_addFriendSearchListItem_header);
            holder.textView_name = (TextView)view.findViewById(R.id.textview_addFriendSearchListItem_name);
            holder.textView_industry = (TextView)view.findViewById(R.id.textview_addFriendSearchListItem_industry);
            holder.textView_label = (TextView)view.findViewById(R.id.textview_addFriendSearchListItem_label);
            view.setTag(holder);
        }else {
            holder = (viewHolder) view.getTag();
        }
        holder.textView_name.setText(list_user.get(position).getUserName());
        return view;
    }

    private class viewHolder{
        CircleImageView imageview_header;
        TextView textView_name;
        TextView textView_industry;
        TextView textView_label;
    }
}
