package com.wanglinkeji.wanglin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.customerview.CircleImageView;
import com.wanglinkeji.wanglin.model.ChatItemMoeld;

import java.util.List;

/**
 * Created by Administrator on 2016/10/21.
 * 聊天界面ListViewAdapter
 */

public class ListViewAdapter_Chat extends BaseAdapter {

    private List<ChatItemMoeld> list_chatItem;

    private Context context;

    private int resource;

    public ListViewAdapter_Chat(List<ChatItemMoeld> list_chatItem, Context context, int resource){
        this.list_chatItem = list_chatItem;
        this.context = context;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        return list_chatItem.size();
    }

    @Override
    public Object getItem(int i) {
        return list_chatItem.get(i);
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
            holder.textView_date = (TextView)view.findViewById(R.id.textview_chatItem_date);
            holder.layout_friend = (LinearLayout)view.findViewById(R.id.layout_chatItem_fromFriend);
            holder.layout_me = (LinearLayout)view.findViewById(R.id.layout_chatItem_fromMe);
            holder.imageView_friendHeader = (CircleImageView)view.findViewById(R.id.imageview_chatItem_friendHeader);
            holder.textView_friendNickName = (TextView)view.findViewById(R.id.textview_chatItem_friendNickName);
            holder.textView_friendContent = (TextView)view.findViewById(R.id.textview_chatItem_friendContent);
            holder.imageView_myHeader = (CircleImageView)view.findViewById(R.id.imageview_chatItem_myHeader);
            holder.textView_myNickName = (TextView)view.findViewById(R.id.textview_chatItem_myNickName);
            holder.textView_myContent = (TextView)view.findViewById(R.id.textview_chatItem_myContent);
            holder.progressBar_messageState = (ProgressBar) view.findViewById(R.id.progressBar_loading_chatItem_myContent);
            view.setTag(holder);
        }else {
            holder = (viewHolder)view.getTag();
        }
        //如果消息来自好友
        if (list_chatItem.get(position).getMessageFrom() == ChatItemMoeld.MESSAGE_FROM_FRIEND){
            holder.layout_friend.setVisibility(View.VISIBLE);
            holder.layout_me.setVisibility(View.GONE);
            holder.textView_friendNickName.setText(list_chatItem.get(position).getFriendNickName());
            holder.textView_friendContent.setText(list_chatItem.get(position).getFriendContent());
        //如果消息来自我
        }else if (list_chatItem.get(position).getMessageFrom() == ChatItemMoeld.MESSAGE_FROM_ME){
            holder.layout_friend.setVisibility(View.GONE);
            holder.layout_me.setVisibility(View.VISIBLE);
            holder.textView_myNickName.setText(list_chatItem.get(position).getMyNickName());
            holder.textView_myContent.setText(list_chatItem.get(position).getMyContent());
            if (list_chatItem.get(position).getMessageSendState() == ChatItemMoeld.MESSAGE_SEND_STATE_ING){
                holder.progressBar_messageState.setVisibility(View.VISIBLE);
            }else if (list_chatItem.get(position).getMessageSendState() == ChatItemMoeld.MESSAGE_SEND_STATE_FINISH){
                holder.progressBar_messageState.setVisibility(View.GONE);
            }
        }
        holder.textView_date.setText(list_chatItem.get(position).getDate());
        return view;
    }

    private class viewHolder{
        TextView textView_date;
        LinearLayout layout_friend;
        LinearLayout layout_me;
        CircleImageView imageView_friendHeader;
        TextView textView_friendNickName;
        TextView textView_friendContent;
        CircleImageView imageView_myHeader;
        TextView textView_myNickName;
        TextView textView_myContent;
        ProgressBar progressBar_messageState;
    }
}
