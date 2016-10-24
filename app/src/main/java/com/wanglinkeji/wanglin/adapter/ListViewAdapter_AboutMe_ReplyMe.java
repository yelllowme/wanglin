package com.wanglinkeji.wanglin.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.activity.AT_UserActivity;
import com.wanglinkeji.wanglin.customerview.CircleImageView;
import com.wanglinkeji.wanglin.customerview.MyGridView;
import com.wanglinkeji.wanglin.model.AboutMeCommentModel;
import com.wanglinkeji.wanglin.model.AtItemModel;
import com.wanglinkeji.wanglin.util.NoLineClickSpan;
import com.wanglinkeji.wanglin.util.NoLineClickSpan_Blog;

import java.util.List;

import io.github.rockerhieu.emojicon.EmojiconTextView;

/**
 * Created by Administrator on 2016/10/13.
 * 关于我的->回复我的
 */

public class ListViewAdapter_AboutMe_ReplyMe extends BaseAdapter {

    private List<AboutMeCommentModel> list_comment;

    private Context context;

    private int resource;

    public ListViewAdapter_AboutMe_ReplyMe(List<AboutMeCommentModel> list_comment, Context context, int resource){
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
            holder.imageView_header = (CircleImageView)view.findViewById(R.id.imageview_listviewItem_aboutMeReplyMe_header);
            holder.textView_userNick = (TextView)view.findViewById(R.id.textview_listviewItem_aboutMeReplyMe_userName);
            holder.textView_industry = (TextView)view.findViewById(R.id.textview_listviewItem_aboutMeReplyMe_industry);
            holder.textView_label = (TextView)view.findViewById(R.id.textview_listviewItem_aboutMeReplyMe_label);
            holder.textView_sendDate = (TextView)view.findViewById(R.id.textview_listviewItem_aboutMeReplyMe_date);
            holder.textView_commentDetails = (EmojiconTextView)view.findViewById(R.id.textview_listviewItem_aboutMeReplyMe_replyContent);
            holder.textView_blogDetails = (EmojiconTextView)view.findViewById(R.id.textview_listviewItem_aboutMeReplyMe_blogDetails);
            holder.gridView_images = (MyGridView)view.findViewById(R.id.gridview_listviewItem_aboutMeReplyMe);
            holder.textView_commentNum = (TextView)view.findViewById(R.id.textview_listviewItem_aboutMeReplyMe_commentCount);
            holder.textView_goodNum = (TextView)view.findViewById(R.id.textview_listviewItem_aboutMeReplyMe_goodCount);
            view.setTag(holder);
        }else {
            holder = (viewHolder)view.getTag();
        }
        holder.textView_userNick.setText(list_comment.get(position).getUserNick());
        holder.textView_industry.setText("行业");
        holder.textView_label.setText("标签");
        holder.textView_sendDate.setText(list_comment.get(position).getSendDate());
        holder.textView_commentDetails.setText(list_comment.get(position).getCommentDetails());
        holder.gridView_images.setAdapter(new GridViewAdapter_housingEstateSpewing_nineImages(list_comment.get(position).getHousingEstateModel().getList_images(),
                context, R.layout.layout_gridview_item_housing_estate_nine_image));
        holder.textView_goodNum.setText(String.valueOf(list_comment.get(position).getHousingEstateModel().getGoodCount()));
        holder.textView_commentNum.setText(String.valueOf(list_comment.get(position).getHousingEstateModel().getReplyCount()));
        if (list_comment.get(position).getHousingEstateModel().getList_At_Item() == null){
            holder.textView_blogDetails.setText(list_comment.get(position).getHousingEstateModel().getComplainContent());
        }else {
            setTextView_afterAt(list_comment.get(position).getHousingEstateModel().getComplainContent(),
                    list_comment.get(position).getHousingEstateModel().getList_At_Item(), holder.textView_blogDetails);
        }
        return view;
    }

    private class viewHolder{
        CircleImageView imageView_header;
        TextView textView_userNick;
        TextView textView_industry;
        TextView textView_label;
        TextView textView_sendDate;
        EmojiconTextView textView_commentDetails;
        EmojiconTextView textView_blogDetails;
        MyGridView gridView_images;
        TextView textView_commentNum;
        TextView textView_goodNum;
    }

    //根据@列表(list_AtItem)，设置EditText的样式
    private void setTextView_afterAt(String current_text, final List<AtItemModel> list_AtItem, EmojiconTextView textView){
        //设置@的内容样式
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(current_text);
        for (int i = 0; i < list_AtItem.size(); i++){
            spannableStringBuilder.setSpan(new NoLineClickSpan(), list_AtItem.get(i).getStartIndex(), list_AtItem.get(i).getEndIndex() + 1,
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        textView.setText(spannableStringBuilder);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
