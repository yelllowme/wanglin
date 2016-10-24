package com.wanglinkeji.wanglin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.customerview.CircleImageView;
import com.wanglinkeji.wanglin.model.CommentModel;

import java.util.List;

import io.github.rockerhieu.emojicon.EmojiconTextView;

/**
 * Created by Administrator on 2016/9/28.
 * 吐槽详情，评论ListViewAdapter
 */

public class ListViewAdapter_ViewPaderItem_BlogDetails_CommentList extends BaseAdapter {

    private List<CommentModel> list_comment;

    private Context context;

    private int resource;

    public ListViewAdapter_ViewPaderItem_BlogDetails_CommentList(List<CommentModel> list_comment, Context context, int resource){
        this.list_comment = list_comment;
        this.context = context;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        return list_comment.size();
    }

    @Override
    public Object getItem(int i) {
        return list_comment.get(i);
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
            holder.imageView_header = (CircleImageView)view.findViewById(R.id.imageview_listViewItem_blogDetails_commentList_header);
            holder.textView_name = (TextView)view.findViewById(R.id.textview_listviewItem_blogDetails_commentList_Name);
            holder.textView_date = (TextView)view.findViewById(R.id.textview_listviewItem_blogDetails_commentList_date);
            holder.textView_content = (EmojiconTextView)view.findViewById(R.id.textview_listviewItem_blogDetails_commentList_content);
            holder.textView_AtUserName = (TextView)view.findViewById(R.id.textview_listviewItem_blogDetails_commentList_AtUserName);
            view.setTag(holder);
        }else {
            holder = (viewHolder)view.getTag();
        }
        holder.textView_name.setText(list_comment.get(position).getUser());
        holder.textView_date.setText(list_comment.get(position).getDate());
        holder.textView_content.setText(list_comment.get(position).getCommentDetails());
        if (list_comment.get(position).getReplyId() == 0){
            holder.textView_AtUserName.setVisibility(View.GONE);
        }else {
            holder.textView_AtUserName.setVisibility(View.VISIBLE);
            holder.textView_AtUserName.setText("@" + list_comment.get(position).getReplyUserName());
        }
        return view;
    }

    private class viewHolder{
        CircleImageView imageView_header;
        TextView textView_name;
        TextView textView_date;
        TextView textView_AtUserName;
        EmojiconTextView textView_content;
    }
}
