package com.wanglinkeji.wanglin.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.adapter.GridViewAdapter_housingEstateSpewing_nineImages;
import com.wanglinkeji.wanglin.adapter.ListViewAdapter_ViewPaderItem_BlogDetails_CommentList;
import com.wanglinkeji.wanglin.adapter.ListViewAdapter_ViewPaderItem_BlogDetails_GoodList;
import com.wanglinkeji.wanglin.customerview.MyListView;
import com.wanglinkeji.wanglin.model.AppUser;
import com.wanglinkeji.wanglin.model.AtItemModel;
import com.wanglinkeji.wanglin.model.BlogDetailsModel;
import com.wanglinkeji.wanglin.model.CommentModel;
import com.wanglinkeji.wanglin.model.SwpeingImageModel;
import com.wanglinkeji.wanglin.util.DBUtil;
import com.wanglinkeji.wanglin.util.HttpUtil;
import com.wanglinkeji.wanglin.util.NoLineClickSpan;
import com.wanglinkeji.wanglin.util.NoLineClickSpan_Blog;
import com.wanglinkeji.wanglin.util.OtherUtil;
import com.wanglinkeji.wanglin.util.WanglinHttpResponseListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.rockerhieu.emojicon.EmojiconEditText;
import io.github.rockerhieu.emojicon.EmojiconTextView;

/**
 * Created by Administrator on 2016/9/1.
 * 普通吐槽详情界面
 */
public class BlogDetails_NormalActivity extends Activity implements View.OnClickListener {

    private ImageView imageView_cancle, imageView_header, imageView_commentPoint, imageView_goodPoint, imageView_addGood, imageView_addCommetn_faceImg,
                        imageView_addComment_send;

    private TextView textView_userName, textView_label, textView_industry, textView_date, textView_commentCount, textView_goodCount,
                        textView_date_anony, textView_addGood, textView_addComment_AtUserName;

    private EmojiconTextView textView_blogDetails, textView_blogDetails_anony;

    private EmojiconEditText editText_comment;

    private LinearLayout layout_commentCount, layout_goodCount, layout_addComment, layout_addGood, layout_notAnony, layout_anony, layout_addComment_commentDetails;

    private GridView gridView_nineImage, gridView_nineImage_anony;

    private MyListView listView_comments, listView_goodList;

    private int BlogId;

    private PopupWindow loading_page;

    private View rootView;

    //回复时，被回复的评论Id，如果回复整个吐槽，则为0
    private int replyId = 0;

    //用来控制一开始弹出的加载页只显示一次
    private boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_activity_blog_details_normal);

        //获取查询详情的Blog的Id
        BlogId = getIntent().getIntExtra("BlogId", -1);
        viewInit();
        getBlogDetails();
    }

    //一开始就弹出加载匡
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //利用flag来让加载页只出现一次
        if (flag){
            loading_page.showAtLocation(rootView, Gravity.CENTER, 0, 0);
        }
        flag = false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //返回按钮
            case R.id.imageview_blogDetailsNormal_cancle:{
                BlogDetails_NormalActivity.this.finish();
                break;
            }
            //“评论”+ “点赞”标题栏，“评论”按钮
            case R.id.layout_blogDetails_commentCount:{
                setViewChoosedComment();
                break;
            }
            //“评论”+ “点赞”标题栏，“点赞”按钮
            case R.id.layout_blogDetails_goodCount:{
                setViewChoosedGood();
                break;
            }
            //新增评论按钮
            case R.id.layout_blogDetails_addComment:{
                replyId = 0;
                setViewAddComment();
                break;
            }
            //回复时添加表情按钮
            case R.id.imageview_blogDetailsNormal_addComment_addFaceImage:{
                Toast.makeText(BlogDetails_NormalActivity.this, "目前只支持舒服法自带EMoji表情", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.imageview_blogDetailsNormal_addComment_send:{
                if (editText_comment.getText().length() == 0){
                    layout_addComment_commentDetails.setVisibility(View.GONE);
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(view,InputMethodManager.SHOW_FORCED);
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
                }else {
                    loading_page.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                    addComment(BlogDetails_NormalActivity.this, BlogId, editText_comment.getText().toString(), BlogDetailsModel.blogDetailsModel.getVillageId(),
                            BlogDetailsModel.blogDetailsModel.getCityName(), BlogDetailsModel.blogDetailsModel.getCityId(), BlogDetailsModel.blogDetailsModel.getLng(),
                            BlogDetailsModel.blogDetailsModel.getLat(), BlogDetailsModel.blogDetailsModel.getAddress(), false, replyId);
                }
                break;
            }
            //新增点赞按钮
            case R.id.layout_blogDetails_addGood:{
                loading_page.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                if (BlogDetailsModel.blogDetailsModel.isAddGood() == true){
                    subGood(BlogDetails_NormalActivity.this);
                }else {
                    addGood(BlogDetails_NormalActivity.this);
                }
                break;
            }
            default:
                break;
        }
    }

    private void viewInit(){
        rootView = LayoutInflater.from(BlogDetails_NormalActivity.this).inflate(R.layout.layout_activity_blog_details_normal, null);
        loading_page = OtherUtil.getLoadingPage(BlogDetails_NormalActivity.this);

        layout_addComment_commentDetails = (LinearLayout)findViewById(R.id.layout_blogDetailsNormal_addComment);
        textView_addComment_AtUserName = (TextView)findViewById(R.id.textview_blogDetailsNormal_addComment_AtUserName);
        editText_comment = (EmojiconEditText)findViewById(R.id.edittext_blogDetailsNormal_addComment_content);
        imageView_addCommetn_faceImg = (ImageView)findViewById(R.id.imageview_blogDetailsNormal_addComment_addFaceImage);
        imageView_addCommetn_faceImg.setOnClickListener(this);
        imageView_addComment_send = (ImageView)findViewById(R.id.imageview_blogDetailsNormal_addComment_send);
        imageView_addComment_send.setOnClickListener(this);
        layout_anony = (LinearLayout)findViewById(R.id.layout_blogDetails_anony);
        layout_notAnony = (LinearLayout)findViewById(R.id.layout_blogDetails_notAnony);
        imageView_cancle = (ImageView)findViewById(R.id.imageview_blogDetailsNormal_cancle);
        imageView_cancle.setOnClickListener(this);
        imageView_header = (ImageView)findViewById(R.id.imageview_blogDetailsNormal_header);
        imageView_commentPoint = (ImageView)findViewById(R.id.imageview_blogDetailsNormal_commentPoint);
        imageView_goodPoint = (ImageView)findViewById(R.id.imageview_blogDetailsNormal_goodPoint);
        textView_userName = (TextView)findViewById(R.id.textview_blogDetailsNormal_userName);
        textView_label = (TextView)findViewById(R.id.textview_blogDetailsNormal_label);
        textView_industry = (TextView)findViewById(R.id.textview_blogDetailsNormal_industry);
        textView_date = (TextView)findViewById(R.id.textview_blogDetailsNormal_date);
        textView_date_anony = (TextView)findViewById(R.id.textview_blogDetailsNormal_date_anony);
        textView_blogDetails = (EmojiconTextView)findViewById(R.id.textview_blogDetailsNormal_blogDetails);
        textView_blogDetails_anony = (EmojiconTextView)findViewById(R.id.textview_blogDetailsNormal_blogDetails_anony);
        textView_commentCount = (TextView)findViewById(R.id.textview_blogDetailsNormal_commentCount);
        textView_goodCount = (TextView)findViewById(R.id.textview_blogDetailsNormal_goodCount);
        layout_commentCount = (LinearLayout)findViewById(R.id.layout_blogDetails_commentCount);
        layout_commentCount.setOnClickListener(this);
        layout_goodCount = (LinearLayout)findViewById(R.id.layout_blogDetails_goodCount);
        layout_goodCount.setOnClickListener(this);
        gridView_nineImage = (GridView)findViewById(R.id.gridview_blogDetailsNormal_nineImage);
        gridView_nineImage_anony = (GridView)findViewById(R.id.gridview_blogDetailsNormal_nineImage_anony);
        listView_comments = (MyListView)findViewById(R.id.listview_viewPagerItem_blogDetails_commentList);
        listView_goodList = (MyListView)findViewById(R.id.listview_viewPagerItem_blogDetails_goodList);
        layout_addComment = (LinearLayout)findViewById(R.id.layout_blogDetails_addComment);
        layout_addComment.setOnClickListener(this);
        layout_addGood = (LinearLayout)findViewById(R.id.layout_blogDetails_addGood);
        layout_addGood.setOnClickListener(this);
        imageView_addGood = (ImageView)findViewById(R.id.imageview_blogDetailsNormal_addGood);
        textView_addGood = (TextView)findViewById(R.id.textview_blogDetailsNormal_addGood);
    }

    //控件更新
    private void setViewAfterResponse(){
        if (BlogDetailsModel.blogDetailsModel.isAnonymous() == true){
            layout_anony.setVisibility(View.VISIBLE);
            layout_notAnony.setVisibility(View.GONE);
        }else {
            layout_anony.setVisibility(View.GONE);
            layout_notAnony.setVisibility(View.VISIBLE);
        }
        if (BlogDetailsModel.blogDetailsModel.isAddGood() == true){
            imageView_addGood.setImageResource(R.mipmap.praise_icon_red);
            textView_addGood.setText("已赞");
        }else {
            imageView_addGood.setImageResource(R.mipmap.good_icon_white);
            textView_addGood.setText("点赞");
        }
        textView_userName.setText(BlogDetailsModel.blogDetailsModel.getUserNick());
        /*textView_label.setText(BlogDetailsModel.blogDetailsModel.getUserLabel());
        textView_industry.setText(BlogDetailsModel.blogDetailsModel.getUserIndustry());*/
        textView_label.setText("标签");
        textView_industry.setText("行业");
        textView_date.setText(BlogDetailsModel.blogDetailsModel.getSendTime());
        textView_date_anony.setText(BlogDetailsModel.blogDetailsModel.getSendTime());
        if (BlogDetailsModel.blogDetailsModel.getList_At_Item()== null){
            textView_blogDetails.setText(BlogDetailsModel.blogDetailsModel.getComplainContent());
        }else {
            setTextView_afterAt(BlogDetails_NormalActivity.this, BlogDetailsModel.blogDetailsModel.getComplainContent(),
                    BlogDetailsModel.blogDetailsModel.getList_At_Item(), textView_blogDetails);
        }
        textView_blogDetails_anony.setText(BlogDetailsModel.blogDetailsModel.getComplainContent());
        gridView_nineImage.setAdapter(new GridViewAdapter_housingEstateSpewing_nineImages(BlogDetailsModel.blogDetailsModel.getList_images(),
                BlogDetails_NormalActivity.this, R.layout.layout_gridview_item_housing_estate_nine_image));
        gridView_nineImage_anony.setAdapter(new GridViewAdapter_housingEstateSpewing_nineImages(BlogDetailsModel.blogDetailsModel.getList_images(),
                BlogDetails_NormalActivity.this, R.layout.layout_gridview_item_housing_estate_nine_image));
        textView_commentCount.setText("评论" + BlogDetailsModel.blogDetailsModel.getList_comments().size());
        textView_goodCount.setText("点赞" + BlogDetailsModel.blogDetailsModel.getList_goodUsers().size());
        listView_comments.setAdapter(new ListViewAdapter_ViewPaderItem_BlogDetails_CommentList(BlogDetailsModel.blogDetailsModel.getList_comments()
                , BlogDetails_NormalActivity.this,R.layout.layout_listview_item_blogdetails_comment_list));
        listView_goodList.setAdapter(new ListViewAdapter_ViewPaderItem_BlogDetails_GoodList(BlogDetailsModel.blogDetailsModel.getList_goodUsers(),
                BlogDetails_NormalActivity.this, R.layout.layout_listview_item_blogdetails_good_list));
        listView_comments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                replyId = BlogDetailsModel.blogDetailsModel.getList_comments().get((new Long(l).intValue())).getId();
                setViewAddComment_AtUser(BlogDetailsModel.blogDetailsModel.getList_comments().get((new Long(l).intValue())).getUser());
            }
        });
    }

    //根据@列表(list_AtItem)，设置EditText的样式
    private void setTextView_afterAt(final Context context ,String current_text, final List<AtItemModel> list_AtItem, EmojiconTextView textView){
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

    private void setViewChoosedComment(){
        textView_commentCount.setTextColor(getResources().getColor(R.color.blue_two));
        textView_goodCount.setTextColor(getResources().getColor(R.color.gray_seven));
        imageView_commentPoint.setVisibility(View.VISIBLE);
        imageView_goodPoint.setVisibility(View.INVISIBLE);
        listView_comments.setVisibility(View.VISIBLE);
        listView_goodList.setVisibility(View.GONE);
    }

    private void setViewChoosedGood(){
        textView_commentCount.setTextColor(getResources().getColor(R.color.gray_seven));
        textView_goodCount.setTextColor(getResources().getColor(R.color.blue_two));
        imageView_commentPoint.setVisibility(View.INVISIBLE);
        imageView_goodPoint.setVisibility(View.VISIBLE);
        listView_comments.setVisibility(View.GONE);
        listView_goodList.setVisibility(View.VISIBLE);
    }

    private void setViewAddComment(){
        layout_addComment_commentDetails.setVisibility(View.VISIBLE);
        textView_addComment_AtUserName.setVisibility(View.GONE);
        editText_comment.setFocusable(true);
        editText_comment.setFocusableInTouchMode(true);
        editText_comment.requestFocus();
    }

    private void setViewAddComment_AtUser(String name){
        layout_addComment_commentDetails.setVisibility(View.VISIBLE);
        textView_addComment_AtUserName.setVisibility(View.VISIBLE);
        textView_addComment_AtUserName.setText("@" + name);
        editText_comment.setFocusable(true);
        editText_comment.setFocusableInTouchMode(true);
        editText_comment.requestFocus();
    }

    private void setViewAfterAddComment(){
        layout_addComment_commentDetails.setVisibility(View.GONE);
    }

    private void getBlogDetails() {
        Map<String, String> params = new HashMap<>();
        params.put("ComplainId", String.valueOf(BlogId));
        HttpUtil.sendVolleyStringRequest_Post(BlogDetails_NormalActivity.this, HttpUtil.getBlogDetails_url, params, DBUtil.getLoginUser().getToken(),
                "yellow_blogDetails_Normal", new WanglinHttpResponseListener() {
                    @Override
                    public void onSuccessResponse(JSONObject jsonObject_response) {
                        try {
                            JSONObject jsonObject_data = jsonObject_response.getJSONObject("Data");
                            BlogDetailsModel housingEstateblogModel = new BlogDetailsModel();
                            housingEstateblogModel.setId(jsonObject_data.getInt("Id"));//设置ID
                            housingEstateblogModel.setAnonymous(jsonObject_data.getBoolean("IsAnonymous")); //设置是否匿名
                            housingEstateblogModel.setComplainContent(jsonObject_data.getString("ComplainContent"));//设置吐槽内容
                            housingEstateblogModel.setSendTime(jsonObject_data.getString("SendTime"));//设置吐槽时间
                            housingEstateblogModel.setUserNick(jsonObject_data.getString("UserNick"));//设置发表用户的昵称
                            housingEstateblogModel.setReplyCount(jsonObject_data.getInt("ReplyCount"));//设置评论总数
                            housingEstateblogModel.setGoodCount(jsonObject_data.getInt("GoodCount"));//设置点赞总数
                            housingEstateblogModel.setAddGood(jsonObject_data.getBoolean("IsUserGood")); //设置当前用户是否点过赞
                            housingEstateblogModel.setVillageId(jsonObject_data.getInt("VillageId"));//设置所在小区Id
                            housingEstateblogModel.setCityName(jsonObject_data.getString("CityName"));//设置发布城市名
                            housingEstateblogModel.setCityId(jsonObject_data.getInt("RegionId"));//设置发布城市ID
                            housingEstateblogModel.setLng(jsonObject_data.getDouble("Lng"));//设置经度
                            housingEstateblogModel.setLat(jsonObject_data.getDouble("Lat"));//设置纬度
                            housingEstateblogModel.setAddress(jsonObject_data.getString("Address"));//设置发布时地址
                            //设置@List
                            if (jsonObject_data.get("ParmStr1").toString().equals("null")){
                                housingEstateblogModel.setList_At_Item(null);
                            }else {
                                List<AtItemModel> list_atItem = (new Gson()).fromJson(jsonObject_data.getString("ParmStr1"), new TypeToken<List<AtItemModel>>(){}.getType());
                                housingEstateblogModel.setList_At_Item(list_atItem);
                            }
                            //设置图片List
                            List<SwpeingImageModel> list_images = new ArrayList<>();
                            JSONArray jsonArray_images = jsonObject_data.getJSONArray("ComplainImg");
                            for (int j = 0; j < jsonArray_images.length(); j++) {
                                JSONObject jsonObject_images = jsonArray_images.getJSONObject(j);
                                SwpeingImageModel swpeingImageModel = new SwpeingImageModel();
                                swpeingImageModel.setUrl(jsonObject_images.getString("ImgUrl"));//设置图片URL
                                swpeingImageModel.setOrders(jsonObject_images.getInt("Orders"));//设置图片顺序
                                list_images.add(swpeingImageModel);
                            }
                            housingEstateblogModel.setList_images(list_images);
                            //设置评论List
                            List<CommentModel> list_comments = new ArrayList<>();
                            JSONArray jsonArray_comments = jsonObject_data.getJSONArray("ComplainReply");
                            for (int j = 0; j < jsonArray_comments.length(); j++) {
                                JSONObject jsonObject_comment = jsonArray_comments.getJSONObject(j);
                                CommentModel commentModel = new CommentModel();
                                commentModel.setId(jsonObject_comment.getInt("Id"));//设置评论Id
                                commentModel.setUser(jsonObject_comment.getString("UserNick"));//设置评论人昵称
                                commentModel.setCommentDetails(jsonObject_comment.getString("ReplyContent"));//设置评论内容
                                if (jsonObject_comment.get("ReplyId").toString().equals("null")){
                                    commentModel.setReplyId(0);//被评论的“评论”Id
                                }else {
                                    commentModel.setReplyId(jsonObject_comment.getInt("ReplyId"));//被评论的“评论”Id
                                }
                                if (jsonObject_comment.get("ReplyUserId").toString().equals("null")){
                                    commentModel.setReplyUserId(0);//被评论的“评论”Id
                                }else {
                                    commentModel.setReplyUserId(jsonObject_comment.getInt("ReplyUserId"));//被评论的“评论”的用户Id
                                }
                                commentModel.setReplyUserName(jsonObject_comment.getString("ReplyUserNick"));//被评论的“评论”的用户名
                                list_comments.add(commentModel);
                            }
                            housingEstateblogModel.setList_comments(list_comments);
                            //设置点赞List
                            List<AppUser> list_goodUsers = new ArrayList<AppUser>();
                            JSONArray jsonArray_user = jsonObject_data.getJSONArray("ComplainGood");
                            for (int i = 0; i < jsonArray_user.length(); i++){
                                JSONObject jsonObject_user = jsonArray_user.getJSONObject(i);
                                AppUser appUser = new AppUser();
                                appUser.setUserId(jsonObject_user.getInt("UserId"));//设置点赞用户ID
                                appUser.setNickName(jsonObject_user.getString("UserNick")); //设置用户昵称
                                list_goodUsers.add(appUser);
                            }
                            Collections.reverse(list_goodUsers);
                            housingEstateblogModel.setList_goodUsers(list_goodUsers);

                            BlogDetailsModel.blogDetailsModel = housingEstateblogModel;
                            //获取数据后更新控件
                            setViewAfterResponse();
                            loading_page.dismiss();
                        } catch (Exception e) {
                            loading_page.dismiss();
                            Toast.makeText(BlogDetails_NormalActivity.this, "解析返回结果出错，请反馈！", Toast.LENGTH_SHORT).show();
                            BlogDetails_NormalActivity.this.finish();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onConnectingError() {
                        loading_page.dismiss();
                        BlogDetails_NormalActivity.this.finish();
                    }

                    @Override
                    public void onDisconnectError() {
                        loading_page.dismiss();
                        BlogDetails_NormalActivity.this.finish();
                    }
                });
    }

    private void addGood(Context context){
        Map<String, String> params = new HashMap<>();
        params.put("ComplainId" , String.valueOf(BlogId));
        HttpUtil.sendVolleyStringRequest_Post(context, HttpUtil.blog_addGood_url, params, DBUtil.getLoginUser().getToken(),
                "yellow_addGoodInfo", new WanglinHttpResponseListener() {
                    @Override
                    public void onSuccessResponse(JSONObject jsonObject_data) {
                        getBlogDetails();
                    }
                    @Override
                    public void onConnectingError() {
                    }
                    @Override
                    public void onDisconnectError() {
                    }
                });
    }

    private void subGood(Context context){
        Map<String, String> params = new HashMap<>();
        params.put("ComplainId" , String.valueOf(BlogId));
        HttpUtil.sendVolleyStringRequest_Post(context, HttpUtil.blog_subGood_url, params, DBUtil.getLoginUser().getToken(),
                "yellow_subGoodInfo", new WanglinHttpResponseListener() {
                    @Override
                    public void onSuccessResponse(JSONObject jsonObject_data) {
                        getBlogDetails();
                    }
                    @Override
                    public void onConnectingError() {
                    }
                    @Override
                    public void onDisconnectError() {
                    }
                });
    }

    private void addComment(Context context, int blogId, String content, int houseEstateId, String cityName, int cityId, double lng, double lat,
                            String address, boolean isAnony, int replyId){
        Map<String, String> params = new HashMap<>();
        params.put("ComplainId", String.valueOf(blogId));
        params.put("Content", content);
        params.put("VillageId", String.valueOf(houseEstateId));
        params.put("CityName", cityName);
        params.put("RegionId", String.valueOf(cityId));
        params.put("Lng", String.valueOf(lng));
        params.put("Lat", String.valueOf(lat));
        params.put("Address", address);
        params.put("IsAnonymous", String.valueOf(isAnony));
        params.put("ReplyId", String.valueOf(replyId));
        HttpUtil.sendVolleyStringRequest_Post(context, HttpUtil.addComment_url, params, DBUtil.getLoginUser().getToken(), "yellow_addComment",
                new WanglinHttpResponseListener() {
                    @Override
                    public void onSuccessResponse(JSONObject jsonObject_response) {
                        setViewAfterAddComment();
                        getBlogDetails();
                    }

                    @Override
                    public void onConnectingError() {
                        loading_page.dismiss();
                    }

                    @Override
                    public void onDisconnectError() {
                        loading_page.dismiss();
                    }
                });
    }
}
