package com.wanglinkeji.wanglin.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.activity.AT_UserActivity;
import com.wanglinkeji.wanglin.activity.UserInfoActivity;
import com.wanglinkeji.wanglin.customerview.MyGridView;
import com.wanglinkeji.wanglin.customerview.MyListView;
import com.wanglinkeji.wanglin.model.AtItemModel;
import com.wanglinkeji.wanglin.model.HousingEstateblogModel;
import com.wanglinkeji.wanglin.model.UserIdentityInfoModel;
import com.wanglinkeji.wanglin.util.DBUtil;
import com.wanglinkeji.wanglin.util.HttpUtil;
import com.wanglinkeji.wanglin.util.NoLineClickSpan;
import com.wanglinkeji.wanglin.util.NoLineClickSpan_Blog;
import com.wanglinkeji.wanglin.util.WanglinHttpResponseListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.rockerhieu.emojicon.EmojiconTextView;

/**
 * Created by Administrator on 2016/8/23.
 * 小区吐槽。BlogItem，Adapter
 */
public class ListViewAdapter_HousingEstateBlogItem extends BaseAdapter {

    private List<HousingEstateblogModel> list_blog;

    private Context context;

    private int resource;

    public ListViewAdapter_HousingEstateBlogItem(List<HousingEstateblogModel> list_blog, Context context, int resource){
        this.list_blog = list_blog;
        this.context = context;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        return list_blog.size();
    }

    @Override
    public Object getItem(int i) {
        return list_blog.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        //初始化控件
         final viewHolder holder;
        if (view == null){
        //普通吐槽
            view = LayoutInflater.from(context).inflate(resource, null);
            holder = new viewHolder();
            holder.layout_blogNormal = (LinearLayout)view.findViewById(R.id.layout_listviewItem_housingEstate_normal);
            holder.layout_blogAnony = (LinearLayout)view.findViewById(R.id.layout_listviewItem_housingEstate_anony);

            holder.layout_comments = (LinearLayout)view.findViewById(R.id.layout_listviewIntem_housingEstateBlog_comments);
            holder.imageView_header = (ImageView)view.findViewById(R.id.imageview_listviewItem_housingEstate_header);
            holder.textView_userName = (TextView)view.findViewById(R.id.textview_listviewItem_housingEstate_userName);
            holder.textView_label = (TextView)view.findViewById(R.id.textview_listviewItem_housingEstate_label);
            holder.textView_date = (TextView) view.findViewById(R.id.textview_listviewItem_housingEstate_date);
            holder.textView_industry = (TextView)view.findViewById(R.id.textview_listviewItem_housingEstate_industry);
            holder.textView_blogDetails = (EmojiconTextView)view.findViewById(R.id.textview_listviewItem_housingEstate_blogDetails);
            holder.textView_commentCount = (TextView)view.findViewById(R.id.textview_listviewItem_housingEstate_commentCount);
            holder.textView_goodCount = (TextView)view.findViewById(R.id.textview_listviewItem_housingEstate_goodContent);
            holder.gridView_images = (MyGridView)view.findViewById(R.id.gridview_listviewItem_housingEstateBlog);
            holder.listView_comments = (MyListView)view.findViewById(R.id.listview_listviewIntem_housingEstateBlog);
            holder.imageView_addGood = (ImageView)view.findViewById(R.id.imageview_listviewItem_housingEstate_addGood);
        //匿名吐槽
            holder.layout_comments_anony = (LinearLayout)view.findViewById(R.id.layout_listviewIntem_housingEstateBlog_Anonymity_comments);
            holder.textView_date_anony = (TextView) view.findViewById(R.id.textview_listviewIntem_housingEstateBlog_Anonymity_Date);
            holder.textView_blogDetails_anony = (EmojiconTextView) view.findViewById(R.id.textview_listviewIntem_housingEstateBlog_Anonymity_blogDetails);
            holder.textView_commentCount_anony = (TextView)view.findViewById(R.id.textview_listviewIntem_housingEstateBlog_Anonymity_commentCount);
            holder.textView_goodCount_anony = (TextView)view.findViewById(R.id.textview_listviewIntem_housingEstateBlog_Anonymity_goodCount);
            holder.gridView_images_anony = (MyGridView)view.findViewById(R.id.gridview_listviewItem_housingEstateBlog_Anonymity);
            holder.listView_comments_anony = (MyListView)view.findViewById(R.id.listview_listviewIntem_housingEstateBlog_Anonymity);
            holder.imageView_addGood_anony = (ImageView)view.findViewById(R.id.imageview_listviewItem_housingEstate_addGood_anony);
            view.setTag(holder);
        }else {
            holder = (viewHolder) view.getTag();
        }
        //设置资源
        //普通吐槽
        if (list_blog.get(position).isAnonymous() == false){
            holder.layout_blogAnony.setVisibility(View.GONE);
            holder.layout_blogNormal.setVisibility(View.VISIBLE);
            //评论为0则评论区不可见
            if (list_blog.get(position).getList_comments().size() > 0){
                holder.layout_comments.setVisibility(View.VISIBLE);
            }else {
                holder.layout_comments.setVisibility(View.GONE);
            }
            //根据是否点赞设置点赞图标
            if (list_blog.get(position).isAddGood() == true){
                holder.imageView_addGood.setImageResource(R.mipmap.praise_icon_red);
            }else {
                holder.imageView_addGood.setImageResource(R.mipmap.praise_icon_gray);
            }
            //设置正文内容
            if (list_blog.get(position).getList_At_Item() == null){
                holder.textView_blogDetails.setText(list_blog.get(position).getComplainContent());
            }else {
                setTextView_afterAt(list_blog.get(position).getComplainContent(), list_blog.get(position).getList_At_Item(), holder.textView_blogDetails);
            }
            holder.imageView_header.setImageResource(R.mipmap.pdd);
            holder.textView_userName.setText(list_blog.get(position).getUserNick());
            holder.textView_label.setText("标签");
            holder.textView_industry.setText("行业");
            holder.textView_date.setText(list_blog.get(position).getSendTime());
            holder.textView_commentCount.setText(String.valueOf(list_blog.get(position).getReplyCount()));
            holder.textView_goodCount.setText(String.valueOf(list_blog.get(position).getGoodCount()));
            holder.gridView_images.setAdapter(new GridViewAdapter_housingEstateSpewing_nineImages(list_blog.get(position).getList_images(), context,
                    R.layout.layout_gridview_item_housing_estate_nine_image));
            holder.listView_comments.setAdapter(new ListViewAdapter_housingEstateSpewing_Comments(list_blog.get(position).getList_comments(), context,
                    R.layout.layout_listview_item_housing_estate_comment));
        //匿名吐槽
        }else {
            holder.layout_blogAnony.setVisibility(View.VISIBLE);
            holder.layout_blogNormal.setVisibility(View.GONE);
            //评论为0则评论区不可见
            if (list_blog.get(position).getList_comments().size() > 0){
                holder.layout_comments_anony.setVisibility(View.VISIBLE);
            }else {
                holder.layout_comments_anony.setVisibility(View.GONE);
            }
            //根据是否点赞设置点赞图标
            if (list_blog.get(position).isAddGood() == true){
                holder.imageView_addGood_anony.setImageResource(R.mipmap.praise_icon_red);
            }else {
                holder.imageView_addGood_anony.setImageResource(R.mipmap.praise_icon_gray);
            }
            //设置正文内容
            if (list_blog.get(position).getList_At_Item() == null){
                holder.textView_blogDetails_anony.setText(list_blog.get(position).getComplainContent());
            }else {
                setTextView_afterAt(list_blog.get(position).getComplainContent(), list_blog.get(position).getList_At_Item(), holder.textView_blogDetails_anony);
            }
            holder.textView_date_anony.setText(list_blog.get(position).getSendTime());
            holder.textView_commentCount_anony.setText(String.valueOf(list_blog.get(position).getReplyCount()));
            holder.textView_goodCount_anony.setText(String.valueOf(list_blog.get(position).getGoodCount()));
            holder.gridView_images_anony.setAdapter(new GridViewAdapter_housingEstateSpewing_nineImages(list_blog.get(position).getList_images(), context,
                    R.layout.layout_gridview_item_housing_estate_nine_image));
            holder.listView_comments_anony.setAdapter(new ListViewAdapter_housingEstateSpewing_Comments(list_blog.get(position).getList_comments(), context,
                    R.layout.layout_listview_item_housing_estate_comment));

        }

        /**
         *点击事件
         */
        //点赞
        //普通吐槽
        if (list_blog.get(position).isAnonymous() == false){
            holder.imageView_addGood.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (list_blog.get(position).isAddGood() == false){
                        addGood(position, holder.imageView_addGood, holder.textView_goodCount);
                    }else {
                        subGood(position, holder.imageView_addGood, holder.textView_goodCount);
                    }
                }
            });
        }else {
            holder.imageView_addGood_anony.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (list_blog.get(position).isAddGood() == false){
                        addGood(position, holder.imageView_addGood_anony, holder.textView_goodCount_anony);
                    }else {
                        subGood(position, holder.imageView_addGood_anony, holder.textView_goodCount_anony);
                    }
                }
            });
        }
        //点击头像进入个人主页
        holder.imageView_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (list_blog.get(position).getUserId() != UserIdentityInfoModel.userIdentityInfoModel.getUserId()){
                    Intent intent = new Intent(context, UserInfoActivity.class);
                    intent.putExtra("userId", list_blog.get(position).getUserId());
                    context.startActivity(intent);
                }
            }
        });

        return view;
    }

    private class viewHolder{
        LinearLayout layout_blogNormal;
        LinearLayout layout_blogAnony;
        LinearLayout layout_comments;
        LinearLayout layout_comments_anony;
        ImageView imageView_header;
        TextView textView_userName;
        TextView textView_label;
        TextView textView_industry;
        TextView textView_date;
        TextView textView_date_anony;
        EmojiconTextView textView_blogDetails;
        EmojiconTextView textView_blogDetails_anony;
        TextView textView_commentCount;
        TextView textView_commentCount_anony;
        TextView textView_goodCount;
        TextView textView_goodCount_anony;
        ImageView imageView_addGood;
        ImageView imageView_addGood_anony;
        MyGridView gridView_images;
        MyGridView gridView_images_anony;
        MyListView listView_comments;
        MyListView listView_comments_anony;
    }

    //根据@列表(list_AtItem)，设置EditText的样式
    private void setTextView_afterAt(String current_text, final List<AtItemModel> list_AtItem, EmojiconTextView textView){
        //设置@的内容样式
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(current_text);
        for (int i = 0; i < list_AtItem.size(); i++){
            if (list_AtItem.get(i).getFromWhat() == AT_UserActivity.FROM_WHAT_AT_USER){
                spannableStringBuilder.setSpan(new NoLineClickSpan_Blog(list_AtItem.get(i).getAtId(), context), list_AtItem.get(i).getStartIndex(),
                        list_AtItem.get(i).getEndIndex() + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }else if (list_AtItem.get(i).getFromWhat() == AT_UserActivity.FROM_WHAT_AT_NEIGHBOR){
                spannableStringBuilder.setSpan(new NoLineClickSpan(), list_AtItem.get(i).getStartIndex(), list_AtItem.get(i).getEndIndex() + 1,
                        Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }
        textView.setText(spannableStringBuilder);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void addGood(final int position, final ImageView imageView_goodIcon, final TextView textView_goodCount){
        Map<String, String> params = new HashMap<>();
        params.put("ComplainId" , String.valueOf(list_blog.get(position).getId()));
        HttpUtil.sendVolleyStringRequest_Post(context, HttpUtil.blog_addGood_url, params, DBUtil.getLoginUser().getToken(),
                "yellow_addGoodInfo", new WanglinHttpResponseListener() {
                    @Override
                    public void onSuccessResponse(JSONObject jsonObject_data) {
                        imageView_goodIcon.setImageResource(R.mipmap.praise_icon_red);
                        list_blog.get(position).setGoodCount(list_blog.get(position).getGoodCount() + 1);
                        textView_goodCount.setText(String.valueOf(list_blog.get(position).getGoodCount()));
                        list_blog.get(position).setAddGood(true);
                    }
                    @Override
                    public void onConnectingError() {
                    }
                    @Override
                    public void onDisconnectError() {
                    }
                });
    }

    private void subGood(final int position, final ImageView imageView_goodIcon, final TextView textView_goodCount){
        Map<String, String> params = new HashMap<>();
        params.put("ComplainId" , String.valueOf(list_blog.get(position).getId()));
        HttpUtil.sendVolleyStringRequest_Post(context, HttpUtil.blog_subGood_url, params, DBUtil.getLoginUser().getToken(),
                "yellow_subGoodInfo", new WanglinHttpResponseListener() {
                    @Override
                    public void onSuccessResponse(JSONObject jsonObject_data) {
                        imageView_goodIcon.setImageResource(R.mipmap.praise_icon_gray);
                        list_blog.get(position).setGoodCount(list_blog.get(position).getGoodCount() - 1);
                        textView_goodCount.setText(String.valueOf(list_blog.get(position).getGoodCount()));
                        list_blog.get(position).setAddGood(false);
                    }
                    @Override
                    public void onConnectingError() {
                    }
                    @Override
                    public void onDisconnectError() {
                    }
                });
    }

}
