package com.wanglinkeji.wanglin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.customerview.CircleImageView;
import com.wanglinkeji.wanglin.model.AppUser;

import java.util.List;

/**
 * Created by Administrator on 2016/9/28.
 * 吐槽详情，点赞ListViewAdapter
 */

public class ListViewAdapter_ViewPaderItem_BlogDetails_GoodList extends BaseAdapter {

    private List<AppUser> list_goodUsers;

    private Context context;

    private int resource;

    public ListViewAdapter_ViewPaderItem_BlogDetails_GoodList(List<AppUser> list_goodUsers, Context context, int resource){
        this.list_goodUsers = list_goodUsers;
        this.context = context;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        return list_goodUsers.size();
    }

    @Override
    public Object getItem(int i) {
        return list_goodUsers.get(i);
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
            holder.imageView_header = (CircleImageView)view.findViewById(R.id.imageview_listviewItem_blogDetails_goodList_header);
            holder.textView_name = (TextView)view.findViewById(R.id.textview_listviewItem_blogDetails_goodList_name);
            view.setTag(holder);
        }else {
            holder = (viewHolder)view.getTag();
        }
        holder.textView_name.setText(list_goodUsers.get(position).getNickName());
        return view;
    }

    private class viewHolder{
        CircleImageView imageView_header;
        TextView textView_name;
    }
}
