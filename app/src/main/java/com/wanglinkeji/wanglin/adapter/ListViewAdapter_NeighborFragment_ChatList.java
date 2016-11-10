package com.wanglinkeji.wanglin.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.customerview.CircleImageView;
import com.wanglinkeji.wanglin.model.ChatListItemModel;

import java.util.List;

/**
 * Created by Administrator on 2016/9/30.
 * 邻居栏，最近聊天列表
 */

public class ListViewAdapter_NeighborFragment_ChatList extends BaseAdapter{

    private List<ChatListItemModel> list_chat;

    private Context context;

    private int resource;

    //显示图片的配置
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.shape_rectangle_black_noborder_nocorner)
            .showImageOnFail(R.drawable.shape_rectangle_gray_noborder_nocorner)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    public ListViewAdapter_NeighborFragment_ChatList(List<ChatListItemModel> list_chat, Context context, int resource){
        this.list_chat = list_chat;
        this.context = context;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        return list_chat.size();
    }

    @Override
    public Object getItem(int i) {
        return list_chat.get(i);
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
            holder.imageView_header = (CircleImageView)view.findViewById(R.id.imageview_listviewItem_neighborFragment_chatListItem_header);
            holder.textView_friendName = (TextView)view.findViewById(R.id.textview_listviewItem_neighborFragment_chatListItem_friendName);
            holder.textView_lastTalk = (TextView)view.findViewById(R.id.textview_listviewItem_neighborFragment_chatListItem_lastTalk);
            holder.textView_numOfNotReadMessage = (TextView)view.findViewById(R.id.textview_listviewItem_neighborFragment_chatListItem_numOfNotReadMessage);
            view.setTag(holder);
        }else {
            holder = (viewHolder)view.getTag();
        }
        if (list_chat.get(position).getNotReadMessage_count() == 0){
            holder.textView_numOfNotReadMessage.setVisibility(View.INVISIBLE);
        }else {
            holder.textView_numOfNotReadMessage.setVisibility(View.VISIBLE);
            holder.textView_numOfNotReadMessage.setText(String.valueOf(list_chat.get(position).getNotReadMessage_count()));
        }
        //ImageLoader.getInstance().displayImage(list_chat.get(position).getHeaderUrl(), holder.imageView_header, options);
        holder.textView_lastTalk.setText(list_chat.get(position).getLastTalk());
        holder.textView_friendName.setText(list_chat.get(position).getFriendName());
        return view;
    }

    private class viewHolder{
        CircleImageView imageView_header;
        TextView textView_friendName;
        TextView textView_lastTalk;
        TextView textView_numOfNotReadMessage;
    }
}
