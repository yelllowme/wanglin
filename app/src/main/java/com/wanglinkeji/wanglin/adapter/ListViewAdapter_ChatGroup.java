package com.wanglinkeji.wanglin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.customerview.CircleImageView;
import com.wanglinkeji.wanglin.model.ChatGroupItem_Model;

import java.util.List;

/**
 * Created by Administrator on 2016/10/2.
 * 聊天群ListViewAdapter
 */

public class ListViewAdapter_ChatGroup extends BaseAdapter {

    private List<ChatGroupItem_Model> list_group;

    private Context context;

    private int resource;

    public ListViewAdapter_ChatGroup(List<ChatGroupItem_Model> list_group, Context context, int resource){
        this.list_group = list_group;
        this.context = context;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        return list_group.size();
    }

    @Override
    public Object getItem(int i) {
        return list_group.get(i);
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
            holder.imageView_header = (CircleImageView)view.findViewById(R.id.imageview_chatGroupItem_header);
            holder.textView_groupName = (TextView)view.findViewById(R.id.textview_chatGroupItem_groupName);
            view.setTag(holder);
        }else {
            holder = (viewHolder)view.getTag();
        }
        holder.textView_groupName.setText(list_group.get(position).getGroupName());
        return view;
    }

    private class viewHolder{
        CircleImageView imageView_header;
        TextView textView_groupName;
    }
}
