package com.wanglinkeji.wanglin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.model.CommentModel;

import java.util.List;

import io.github.rockerhieu.emojicon.EmojiconTextView;

/**
 * Created by Administrator on 2016/8/23.
 * 小区吐槽_评论ListViewAdapter
 */
public class ListViewAdapter_housingEstateSpewing_Comments extends BaseAdapter {

    private List<CommentModel> list_comments;

    private Context context;

    private int resource;

    public ListViewAdapter_housingEstateSpewing_Comments(List<CommentModel> list_comments, Context context, int resource){
        this.list_comments = list_comments;
        this.context = context;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        return list_comments.size();
    }

    @Override
    public Object getItem(int i) {
        return list_comments.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        viewHolder holder;
        if (view == null){
            holder = new viewHolder();
            view = LayoutInflater.from(context).inflate(resource, null);
            holder.textView_user = (TextView) view.findViewById(R.id.textview_listviewItem_housingEstate_comment_user);
            holder.textView_details = (EmojiconTextView) view.findViewById(R.id.textview_listviewItem_housingEstate_comment_details);
            holder.textView_AtUserName = (TextView)view.findViewById(R.id.textview_listviewItem_housingEstate_comment_AtUserName);
            view.setTag(holder);
        }else {
            holder = (viewHolder)view.getTag();
        }
        holder.textView_user.setText(list_comments.get(position).getUser());
        holder.textView_details.setText(list_comments.get(position).getCommentDetails());
        if (list_comments.get(position).getReplyId() == 0){
            holder.textView_AtUserName.setVisibility(View.GONE);
        }else {
            holder.textView_AtUserName.setVisibility(View.VISIBLE);
            holder.textView_AtUserName.setText("@" + list_comments.get(position).getReplyUserName());
        }
        return view;
    }

    private class viewHolder{
        TextView textView_user;
        EmojiconTextView textView_details;
        TextView textView_AtUserName;
    }
}
