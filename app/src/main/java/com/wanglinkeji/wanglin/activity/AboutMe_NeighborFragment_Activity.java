package com.wanglinkeji.wanglin.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.adapter.ListViewAdapter_AboutMe_ReplyMe;
import com.wanglinkeji.wanglin.adapter.ListViewAdapter_HousingEstateBlogItem;
import com.wanglinkeji.wanglin.model.AboutMeCommentModel;
import com.wanglinkeji.wanglin.model.AtItemModel;
import com.wanglinkeji.wanglin.model.CommentModel;
import com.wanglinkeji.wanglin.model.HousingEstateblogModel;
import com.wanglinkeji.wanglin.model.SwpeingImageModel;
import com.wanglinkeji.wanglin.util.DBUtil;
import com.wanglinkeji.wanglin.util.HttpUtil;
import com.wanglinkeji.wanglin.util.OtherUtil;
import com.wanglinkeji.wanglin.util.WanglinHttpResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/30.
 * 邻居-->消息--->与我相关
 */

public class AboutMe_NeighborFragment_Activity extends Activity implements View.OnClickListener {

    private ImageView imageView_cancle, imageView_AtMePoint, imageView_commentMePoint, imageView_goodMePoint;

    private TextView textView_title_AtMe, textView_title_commentMe, textView_title_goodMe;

    private LinearLayout layout_AtMe, layout_commentMe, layout_goodMe;

    private PullToRefreshListView listView_AtMe, listView_commentMe, listView_goodMe;

    private PopupWindow loading_page;

    private View rootView;

    //用来控制一开始弹出的加载页只显示一次
    private boolean flag = true;

    //三个ListView Adapter
    private ListViewAdapter_AboutMe_ReplyMe listViewAdapter_aboutMe_replyMe, listViewAdapter_aboutMe_goodMe;

    private ListViewAdapter_HousingEstateBlogItem listViewAdapter_housingEstateBlogItem;

    //三个接口的请求分页数
    private int pageNum_AtMe, pageNum_ReplyMe, pageNum_GoodMe;

    //三个接口的数据总量
    private int totle_AtMe = 0, totle_ReplyMe = 0, totle_GoodMe = 0;

    //三个接口数据List
    private List<AboutMeCommentModel> list_comment, list_good;

    private List<HousingEstateblogModel> list_blogAtMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_activity_about_me_neighbor_fragment);

        viewInit();
        getFirstPageAtMeBlog(AboutMe_NeighborFragment_Activity.this);
        getFirstPageReplyMe(AboutMe_NeighborFragment_Activity.this);
        getFirstPageGoodMe(AboutMe_NeighborFragment_Activity.this);
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
            case R.id.imageview_aboutme_cancle:{
                AboutMe_NeighborFragment_Activity.this.finish();
                break;
            }
            //标题--->@我的
            case R.id.layout_aboutMe_AtMe:{
                setView_ChoosedAtMe();
                break;
            }
            //标题--->回复我的
            case R.id.layout_aboutMe_commentMe:{
                setView_choosedCommentMe();
                break;
            }
            //标题--->赞我的
            case R.id.layout_aboutMe_goodMe:{
                setView_choosedGoodMe();
                break;
            }
            default:
                break;
        }
    }

    private void viewInit(){
        loading_page = OtherUtil.getLoadingPage(AboutMe_NeighborFragment_Activity.this);
        rootView = LayoutInflater.from(AboutMe_NeighborFragment_Activity.this).inflate(R.layout.layout_activity_about_me_neighbor_fragment, null);
        imageView_cancle = (ImageView)findViewById(R.id.imageview_aboutme_cancle);
        imageView_cancle.setOnClickListener(this);
        imageView_AtMePoint = (ImageView)findViewById(R.id.imageview_aboutMe_AtMePoint);
        imageView_commentMePoint = (ImageView)findViewById(R.id.imageview_aboutMe_commentMePoint);
        imageView_goodMePoint = (ImageView)findViewById(R.id.imageview_aboutMe_goodMePoint);
        layout_AtMe = (LinearLayout)findViewById(R.id.layout_aboutMe_AtMe);
        layout_AtMe.setOnClickListener(this);
        layout_commentMe = (LinearLayout)findViewById(R.id.layout_aboutMe_commentMe);
        layout_commentMe.setOnClickListener(this);
        layout_goodMe = (LinearLayout)findViewById(R.id.layout_aboutMe_goodMe);
        layout_goodMe.setOnClickListener(this);
        textView_title_AtMe = (TextView)findViewById(R.id.textview_aboutMe_AtMeTitle);
        textView_title_AtMe.setText("@我的");
        textView_title_commentMe = (TextView)findViewById(R.id.textview_aboutMe_commentMeTitle);
        textView_title_goodMe = (TextView)findViewById(R.id.textview_aboutMe_goodMeTitle);
        listView_AtMe = (PullToRefreshListView)findViewById(R.id.listview_aboutMe_AtMe);
        listView_commentMe = (PullToRefreshListView)findViewById(R.id.listView_aboutMe_commentMe);
        listView_commentMe.setVisibility(View.GONE);
        listView_goodMe = (PullToRefreshListView)findViewById(R.id.listView_aboutMe_goodMe);
        listView_goodMe.setVisibility(View.GONE);
        listView_AtMe.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                getFirstPageAtMeBlog(AboutMe_NeighborFragment_Activity.this);
            }
        });
        listView_AtMe.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                if ((totle_AtMe - pageNum_AtMe * HttpUtil.PAGE_SIZE) > -HttpUtil.PAGE_SIZE){
                    getMorePageAtMeBlog(AboutMe_NeighborFragment_Activity.this);
                }
            }
        });
        listView_commentMe.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                getFirstPageReplyMe(AboutMe_NeighborFragment_Activity.this);
            }
        });
        listView_commentMe.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                if ((totle_ReplyMe - pageNum_AtMe * HttpUtil.PAGE_SIZE) > -HttpUtil.PAGE_SIZE){
                    getMorePageReplyMe(AboutMe_NeighborFragment_Activity.this);
                }
            }
        });
        listView_goodMe.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                getFirstPageGoodMe(AboutMe_NeighborFragment_Activity.this);
            }
        });
        listView_goodMe.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                if ((totle_GoodMe - pageNum_AtMe * HttpUtil.PAGE_SIZE) > -HttpUtil.PAGE_SIZE){
                    getMorePageGoodMe(AboutMe_NeighborFragment_Activity.this);
                }
            }
        });
    }

    private void setView_ChoosedAtMe(){
        textView_title_AtMe.setTextColor(0XFFFFFFFF);
        textView_title_commentMe.setTextColor(0XFF8A9197);
        textView_title_goodMe.setTextColor(0XFF8A9197);
        imageView_AtMePoint.setVisibility(View.VISIBLE);
        imageView_commentMePoint.setVisibility(View.INVISIBLE);
        imageView_goodMePoint.setVisibility(View.INVISIBLE);
        listView_AtMe.setVisibility(View.VISIBLE);
        listView_commentMe.setVisibility(View.GONE);
        listView_goodMe.setVisibility(View.GONE);
    }

    private void setView_choosedCommentMe(){
        textView_title_AtMe.setTextColor(0XFF8A9197);
        textView_title_commentMe.setTextColor(0XFFFFFFFF);
        textView_title_goodMe.setTextColor(0XFF8A9197);
        imageView_AtMePoint.setVisibility(View.INVISIBLE);
        imageView_commentMePoint.setVisibility(View.VISIBLE);
        imageView_goodMePoint.setVisibility(View.INVISIBLE);
        listView_AtMe.setVisibility(View.GONE);
        listView_commentMe.setVisibility(View.VISIBLE);
        listView_goodMe.setVisibility(View.GONE);
    }

    private void setView_choosedGoodMe(){
        textView_title_AtMe.setTextColor(0XFF8A9197);
        textView_title_commentMe.setTextColor(0XFF8A9197);
        textView_title_goodMe.setTextColor(0XFFFFFFFF);
        imageView_AtMePoint.setVisibility(View.INVISIBLE);
        imageView_commentMePoint.setVisibility(View.INVISIBLE);
        imageView_goodMePoint.setVisibility(View.VISIBLE);
        listView_AtMe.setVisibility(View.GONE);
        listView_commentMe.setVisibility(View.GONE);
        listView_goodMe.setVisibility(View.VISIBLE);
    }

    private void getFirstPageAtMeBlog(final Context context){
        pageNum_AtMe = 2;
        Map<String, String> params = new HashMap<>();
        params.put("pageIndex", String.valueOf(1));
        params.put("pageSize", String.valueOf(HttpUtil.PAGE_SIZE));
        HttpUtil.sendVolleyStringRequest_Post(context, HttpUtil.aboutMe_AtMe_url, params, DBUtil.getLoginUser().getToken(), "yellow_getFirstPageAtMe",
                new WanglinHttpResponseListener() {
                    @Override
                    public void onSuccessResponse(JSONObject jsonObject_response) {
                        try {
                            totle_AtMe = jsonObject_response.getInt("Total");
                            JSONArray jsonArray_blog = jsonObject_response.getJSONArray("Data");
                            list_blogAtMe = new ArrayList<HousingEstateblogModel>();
                            for (int i = 0; i < jsonArray_blog.length(); i++){
                                JSONObject jsonObject_blog = jsonArray_blog.getJSONObject(i);
                                HousingEstateblogModel housingEstateblogModel = new HousingEstateblogModel();
                                housingEstateblogModel.setId(jsonObject_blog.getInt("Id"));//设置ID
                                housingEstateblogModel.setUserId(jsonObject_blog.getInt("UserId"));//设置用户Id
                                housingEstateblogModel.setAnonymous(jsonObject_blog.getBoolean("IsAnonymous")); //设置是否匿名
                                housingEstateblogModel.setComplainContent(jsonObject_blog.getString("ComplainContent"));//设置吐槽内容
                                housingEstateblogModel.setSendTime(jsonObject_blog.getString("SendTime"));//设置吐槽时间
                                housingEstateblogModel.setUserNick(jsonObject_blog.getString("UserNick"));//设置发表用户的昵称
                                housingEstateblogModel.setReplyCount(jsonObject_blog.getInt("ReplyCount"));//设置评论总数
                                housingEstateblogModel.setGoodCount(jsonObject_blog.getInt("GoodCount"));//设置点赞总数
                                housingEstateblogModel.setAddGood(jsonObject_blog.getBoolean("IsUserGood")); //设置当前用户是否点过赞
                                //设置@List
                                if (jsonObject_blog.get("ParmStr1").toString().equals("null")){
                                    housingEstateblogModel.setList_At_Item(null);
                                }else {
                                    List<AtItemModel> list_atItem = (new Gson()).fromJson(jsonObject_blog.getString("ParmStr1"), new TypeToken<List<AtItemModel>>(){}.getType());
                                    housingEstateblogModel.setList_At_Item(list_atItem);
                                }
                                //设置图片List
                                List<SwpeingImageModel> list_images = new ArrayList<>();
                                JSONArray jsonArray_images = jsonObject_blog.getJSONArray("ComplainImg");
                                for (int j = 0; j < jsonArray_images.length(); j++){
                                    JSONObject jsonObject_images = jsonArray_images.getJSONObject(j);
                                    SwpeingImageModel swpeingImageModel = new SwpeingImageModel();
                                    swpeingImageModel.setUrl(jsonObject_images.getString("ImgUrl"));//设置图片URL
                                    swpeingImageModel.setOrders(jsonObject_images.getInt("Orders"));//设置图片顺序
                                    list_images.add(swpeingImageModel);
                                }
                                housingEstateblogModel.setList_images(list_images);
                                //设置评论List
                                List<CommentModel> list_comments = new ArrayList<>();
                                housingEstateblogModel.setList_comments(list_comments);
                                list_blogAtMe.add(housingEstateblogModel);
                            }
                            listViewAdapter_housingEstateBlogItem = new ListViewAdapter_HousingEstateBlogItem(list_blogAtMe, context,
                                    R.layout.layout_listview_item_housing_estate_spewing);
                            listView_AtMe.setAdapter(listViewAdapter_housingEstateBlogItem);
                            listView_AtMe.onRefreshComplete();
                            loading_page.dismiss();
                        } catch (JSONException e) {
                            listView_AtMe.onRefreshComplete();
                            loading_page.dismiss();
                            Toast.makeText(context, "解析返回数据失败，请反馈！", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onConnectingError() {
                        listView_AtMe.onRefreshComplete();
                        loading_page.dismiss();
                    }

                    @Override
                    public void onDisconnectError() {
                        listView_AtMe.onRefreshComplete();
                        loading_page.dismiss();
                    }
                });
    }

    private void getMorePageAtMeBlog(final Context context){
        Map<String, String> params = new HashMap<>();
        params.put("pageIndex", String.valueOf(pageNum_AtMe));
        params.put("pageSize", String.valueOf(HttpUtil.PAGE_SIZE));
        HttpUtil.sendVolleyStringRequest_Post(context, HttpUtil.aboutMe_AtMe_url, params, DBUtil.getLoginUser().getToken(), "yellow_getMorePageAtMe",
                new WanglinHttpResponseListener() {
                    @Override
                    public void onSuccessResponse(JSONObject jsonObject_response) {
                        try {
                            JSONArray jsonArray_blog = jsonObject_response.getJSONArray("Data");
                            for (int i = 0; i < jsonArray_blog.length(); i++){
                                JSONObject jsonObject_blog = jsonArray_blog.getJSONObject(i);
                                HousingEstateblogModel housingEstateblogModel = new HousingEstateblogModel();
                                housingEstateblogModel.setId(jsonObject_blog.getInt("Id"));//设置ID
                                housingEstateblogModel.setUserId(jsonObject_blog.getInt("UserId"));//设置用户Id
                                housingEstateblogModel.setAnonymous(jsonObject_blog.getBoolean("IsAnonymous")); //设置是否匿名
                                housingEstateblogModel.setComplainContent(jsonObject_blog.getString("ComplainContent"));//设置吐槽内容
                                housingEstateblogModel.setSendTime(jsonObject_blog.getString("SendTime"));//设置吐槽时间
                                housingEstateblogModel.setUserNick(jsonObject_blog.getString("UserNick"));//设置发表用户的昵称
                                housingEstateblogModel.setReplyCount(jsonObject_blog.getInt("ReplyCount"));//设置评论总数
                                housingEstateblogModel.setGoodCount(jsonObject_blog.getInt("GoodCount"));//设置点赞总数
                                housingEstateblogModel.setAddGood(jsonObject_blog.getBoolean("IsUserGood")); //设置当前用户是否点过赞
                                //设置@List
                                if (jsonObject_blog.get("ParmStr1").toString().equals("null")){
                                    housingEstateblogModel.setList_At_Item(null);
                                }else {
                                    List<AtItemModel> list_atItem = (new Gson()).fromJson(jsonObject_blog.getString("ParmStr1"), new TypeToken<List<AtItemModel>>(){}.getType());
                                    housingEstateblogModel.setList_At_Item(list_atItem);
                                }
                                //设置图片List
                                List<SwpeingImageModel> list_images = new ArrayList<>();
                                JSONArray jsonArray_images = jsonObject_blog.getJSONArray("ComplainImg");
                                for (int j = 0; j < jsonArray_images.length(); j++){
                                    JSONObject jsonObject_images = jsonArray_images.getJSONObject(j);
                                    SwpeingImageModel swpeingImageModel = new SwpeingImageModel();
                                    swpeingImageModel.setUrl(jsonObject_images.getString("ImgUrl"));//设置图片URL
                                    swpeingImageModel.setOrders(jsonObject_images.getInt("Orders"));//设置图片顺序
                                    list_images.add(swpeingImageModel);
                                }
                                housingEstateblogModel.setList_images(list_images);
                                //设置评论List
                                List<CommentModel> list_comments = new ArrayList<>();
                                housingEstateblogModel.setList_comments(list_comments);
                                list_blogAtMe.add(housingEstateblogModel);
                            }
                            pageNum_AtMe++;
                            listViewAdapter_housingEstateBlogItem.notifyDataSetChanged();
                            listView_AtMe.setVisibility(View.INVISIBLE);
                            listView_AtMe.setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            loading_page.dismiss();
                            Toast.makeText(context, "解析返回数据失败，请反馈！", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onConnectingError() {}

                    @Override
                    public void onDisconnectError() {}
                });
    }

    private void getFirstPageReplyMe(final Context context){
        pageNum_ReplyMe = 2;
        Map<String, String> params = new HashMap<>();
        params.put("pageIndex", String.valueOf(1));
        params.put("pageSize", String.valueOf(HttpUtil.PAGE_SIZE));
        HttpUtil.sendVolleyStringRequest_Post(context, HttpUtil.aboutMe_ReplyMe_url, params, DBUtil.getLoginUser().getToken(), "yellow_getFirstPageReplyMe",
                new WanglinHttpResponseListener() {
                    @Override
                    public void onSuccessResponse(JSONObject jsonObject_response) {
                        try {
                            list_comment = new ArrayList<AboutMeCommentModel>();
                            totle_ReplyMe = jsonObject_response.getInt("Total");
                            JSONArray jsonArray_data = jsonObject_response.getJSONArray("Data");
                            for (int i = 0; i < jsonArray_data.length(); i++){
                                JSONObject jsonObject_temp = jsonArray_data.getJSONObject(i);
                                JSONObject jsonObject_blog = jsonObject_temp.getJSONObject("Complain");
                                HousingEstateblogModel housingEstateblogModel = new HousingEstateblogModel();
                                housingEstateblogModel.setId(jsonObject_blog.getInt("Id"));//设置吐槽Id
                                housingEstateblogModel.setComplainContent(jsonObject_blog.getString("ComplainContent"));//设置吐槽内容
                                housingEstateblogModel.setGoodCount(jsonObject_blog.getInt("GoodCount"));//设置点赞数量
                                housingEstateblogModel.setReplyCount(jsonObject_blog.getInt("ReplyCount"));//设置回复数
                                //设置@List
                                if (jsonObject_blog.get("ParmStr1").toString().equals("null")){
                                    housingEstateblogModel.setList_At_Item(null);
                                }else {
                                    List<AtItemModel> list_atItem = (new Gson()).fromJson(jsonObject_blog.getString("ParmStr1"), new TypeToken<List<AtItemModel>>(){}.getType());
                                    housingEstateblogModel.setList_At_Item(list_atItem);
                                }
                                //设置图片List
                                List<SwpeingImageModel> list_images = new ArrayList<>();
                                JSONArray jsonArray_images = jsonObject_blog.getJSONArray("ComplainImg");
                                for (int j = 0; j < jsonArray_images.length(); j++){
                                    JSONObject jsonObject_images = jsonArray_images.getJSONObject(j);
                                    SwpeingImageModel swpeingImageModel = new SwpeingImageModel();
                                    swpeingImageModel.setUrl(jsonObject_images.getString("ImgUrl"));//设置图片URL
                                    swpeingImageModel.setOrders(jsonObject_images.getInt("Orders"));//设置图片顺序
                                    list_images.add(swpeingImageModel);
                                }
                                housingEstateblogModel.setList_images(list_images);
                                //设置评论
                                AboutMeCommentModel aboutMeCommentModel = new AboutMeCommentModel();
                                aboutMeCommentModel.setCommentId(jsonObject_temp.getInt("Id"));//设置回复Id
                                aboutMeCommentModel.setUserId(jsonObject_temp.getInt("UserId"));//设置回复人的Id
                                aboutMeCommentModel.setUserNick(jsonObject_temp.getString("UserNick"));//设置回复人的昵称
                                aboutMeCommentModel.setSendDate(jsonObject_temp.getString("SendTime"));//设置回复时间
                                aboutMeCommentModel.setCommentDetails(jsonObject_temp.getString("ReplyContent"));//设置回复内容
                                aboutMeCommentModel.setHousingEstateModel(housingEstateblogModel);
                                list_comment.add(aboutMeCommentModel);
                            }
                            listViewAdapter_aboutMe_replyMe = new ListViewAdapter_AboutMe_ReplyMe(list_comment, context,
                                    R.layout.layout_listview_item_about_me_reply_me);
                            listView_commentMe.setAdapter(listViewAdapter_aboutMe_replyMe);
                            listView_commentMe.onRefreshComplete();
                        } catch (JSONException e) {
                            listView_commentMe.onRefreshComplete();
                            Toast.makeText(context, "解析返回数据失败，请反馈！", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onConnectingError() {
                        listView_commentMe.onRefreshComplete();
                    }

                    @Override
                    public void onDisconnectError() {
                        listView_commentMe.onRefreshComplete();
                    }
                });
    }

    private void getMorePageReplyMe(final Context context){
        //Log.d("yellow_temp", "pageNum：" + pageNum_ReplyMe + "   pageSize：" + HttpUtil.PAGE_SIZE);
        Map<String, String> params = new HashMap<>();
        params.put("pageIndex", String.valueOf(pageNum_ReplyMe));
        params.put("pageSize", String.valueOf(HttpUtil.PAGE_SIZE));
        HttpUtil.sendVolleyStringRequest_Post(context, HttpUtil.aboutMe_ReplyMe_url, params, DBUtil.getLoginUser().getToken(), "yellow_getMorePageReplyMe",
                new WanglinHttpResponseListener() {
                    @Override
                    public void onSuccessResponse(JSONObject jsonObject_response) {
                        try {
                            JSONArray jsonArray_data = jsonObject_response.getJSONArray("Data");
                            for (int i = 0; i < jsonArray_data.length(); i++){
                                JSONObject jsonObject_temp = jsonArray_data.getJSONObject(i);
                                JSONObject jsonObject_blog = jsonObject_temp.getJSONObject("Complain");
                                HousingEstateblogModel housingEstateblogModel = new HousingEstateblogModel();
                                housingEstateblogModel.setId(jsonObject_blog.getInt("Id"));//设置吐槽Id
                                housingEstateblogModel.setComplainContent(jsonObject_blog.getString("ComplainContent"));//设置吐槽内容
                                housingEstateblogModel.setGoodCount(jsonObject_blog.getInt("GoodCount"));//设置点赞数量
                                housingEstateblogModel.setReplyCount(jsonObject_blog.getInt("ReplyCount"));//设置回复数
                                //设置@List
                                if (jsonObject_blog.get("ParmStr1").toString().equals("null")){
                                    housingEstateblogModel.setList_At_Item(null);
                                }else {
                                    List<AtItemModel> list_atItem = (new Gson()).fromJson(jsonObject_blog.getString("ParmStr1"), new TypeToken<List<AtItemModel>>(){}.getType());
                                    housingEstateblogModel.setList_At_Item(list_atItem);
                                }
                                //设置图片List
                                List<SwpeingImageModel> list_images = new ArrayList<>();
                                JSONArray jsonArray_images = jsonObject_blog.getJSONArray("ComplainImg");
                                for (int j = 0; j < jsonArray_images.length(); j++){
                                    JSONObject jsonObject_images = jsonArray_images.getJSONObject(j);
                                    SwpeingImageModel swpeingImageModel = new SwpeingImageModel();
                                    swpeingImageModel.setUrl(jsonObject_images.getString("ImgUrl"));//设置图片URL
                                    swpeingImageModel.setOrders(jsonObject_images.getInt("Orders"));//设置图片顺序
                                    list_images.add(swpeingImageModel);
                                }
                                housingEstateblogModel.setList_images(list_images);
                                //设置评论
                                AboutMeCommentModel aboutMeCommentModel = new AboutMeCommentModel();
                                aboutMeCommentModel.setCommentId(jsonObject_temp.getInt("Id"));//设置回复Id
                                aboutMeCommentModel.setUserId(jsonObject_temp.getInt("UserId"));//设置回复人的Id
                                aboutMeCommentModel.setUserNick(jsonObject_temp.getString("UserNick"));//设置回复人的昵称
                                aboutMeCommentModel.setSendDate(jsonObject_temp.getString("SendTime"));//设置回复时间
                                aboutMeCommentModel.setCommentDetails(jsonObject_temp.getString("ReplyContent"));//设置回复内容
                                aboutMeCommentModel.setHousingEstateModel(housingEstateblogModel);
                                list_comment.add(aboutMeCommentModel);
                            }
                            pageNum_ReplyMe++;
                            listViewAdapter_aboutMe_replyMe.notifyDataSetChanged();
                            listView_commentMe.setVisibility(View.INVISIBLE);
                            listView_commentMe.setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            Toast.makeText(context, "解析返回数据失败，请反馈！", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onConnectingError() {
                    }

                    @Override
                    public void onDisconnectError() {
                    }
                });
    }

    private void getFirstPageGoodMe(final Context context){
        pageNum_GoodMe = 2;
        Map<String, String> params = new HashMap<>();
        params.put("pageIndex", String.valueOf(1));
        params.put("pageSize", String.valueOf(HttpUtil.PAGE_SIZE));
        HttpUtil.sendVolleyStringRequest_Post(context, HttpUtil.aboutMe_GoodMe_url, params, DBUtil.getLoginUser().getToken(), "yellow_getFirstPageGoodMe",
                new WanglinHttpResponseListener() {
                    @Override
                    public void onSuccessResponse(JSONObject jsonObject_response) {
                        try {
                            list_good = new ArrayList<AboutMeCommentModel>();
                            totle_GoodMe = jsonObject_response.getInt("Total");
                            JSONArray jsonArray_data = jsonObject_response.getJSONArray("Data");
                            for (int i = 0; i < jsonArray_data.length(); i++){
                                JSONObject jsonObject_temp = jsonArray_data.getJSONObject(i);
                                JSONObject jsonObject_blog = jsonObject_temp.getJSONObject("Complain");
                                HousingEstateblogModel housingEstateblogModel = new HousingEstateblogModel();
                                housingEstateblogModel.setId(jsonObject_blog.getInt("Id"));//设置吐槽Id
                                housingEstateblogModel.setComplainContent(jsonObject_blog.getString("ComplainContent"));//设置吐槽内容
                                housingEstateblogModel.setGoodCount(jsonObject_blog.getInt("GoodCount"));//设置点赞数量
                                housingEstateblogModel.setReplyCount(jsonObject_blog.getInt("ReplyCount"));//设置回复数
                                //设置@List
                                if (jsonObject_blog.get("ParmStr1").toString().equals("null")){
                                    housingEstateblogModel.setList_At_Item(null);
                                }else {
                                    List<AtItemModel> list_atItem = (new Gson()).fromJson(jsonObject_blog.getString("ParmStr1"), new TypeToken<List<AtItemModel>>(){}.getType());
                                    housingEstateblogModel.setList_At_Item(list_atItem);
                                }
                                //设置图片List
                                List<SwpeingImageModel> list_images = new ArrayList<>();
                                JSONArray jsonArray_images = jsonObject_blog.getJSONArray("ComplainImg");
                                for (int j = 0; j < jsonArray_images.length(); j++){
                                    JSONObject jsonObject_images = jsonArray_images.getJSONObject(j);
                                    SwpeingImageModel swpeingImageModel = new SwpeingImageModel();
                                    swpeingImageModel.setUrl(jsonObject_images.getString("ImgUrl"));//设置图片URL
                                    swpeingImageModel.setOrders(jsonObject_images.getInt("Orders"));//设置图片顺序
                                    list_images.add(swpeingImageModel);
                                }
                                housingEstateblogModel.setList_images(list_images);
                                AboutMeCommentModel aboutMeCommentModel = new AboutMeCommentModel();
                                aboutMeCommentModel.setUserId(jsonObject_temp.getInt("UserId"));//设置点赞人的Id
                                aboutMeCommentModel.setUserNick(jsonObject_temp.getString("UserNick"));//设置点赞人的昵称
                                aboutMeCommentModel.setSendDate(jsonObject_temp.getString("AddTime"));//设置点赞时间
                                aboutMeCommentModel.setCommentDetails("赞了这条吐槽");
                                aboutMeCommentModel.setHousingEstateModel(housingEstateblogModel);
                                list_good.add(aboutMeCommentModel);
                            }
                            listViewAdapter_aboutMe_goodMe = new ListViewAdapter_AboutMe_ReplyMe(list_good, context,
                                    R.layout.layout_listview_item_about_me_reply_me);
                            listView_goodMe.setAdapter(listViewAdapter_aboutMe_goodMe);
                            listView_goodMe.onRefreshComplete();
                        } catch (JSONException e) {
                            listView_goodMe.onRefreshComplete();
                            Toast.makeText(context, "解析返回数据失败，请反馈！", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onConnectingError() {
                        listView_goodMe.onRefreshComplete();
                    }

                    @Override
                    public void onDisconnectError() {
                        listView_goodMe.onRefreshComplete();
                    }
                });
    }

    private void getMorePageGoodMe(final Context context){
        Map<String, String> params = new HashMap<>();
        params.put("pageIndex", String.valueOf(pageNum_GoodMe));
        params.put("pageSize", String.valueOf(HttpUtil.PAGE_SIZE));
        HttpUtil.sendVolleyStringRequest_Post(context, HttpUtil.aboutMe_GoodMe_url, params, DBUtil.getLoginUser().getToken(), "yellow_getMorePageGoodMe",
                new WanglinHttpResponseListener() {
                    @Override
                    public void onSuccessResponse(JSONObject jsonObject_response) {
                        try {
                            JSONArray jsonArray_data = jsonObject_response.getJSONArray("Data");
                            for (int i = 0; i < jsonArray_data.length(); i++){
                                JSONObject jsonObject_temp = jsonArray_data.getJSONObject(i);
                                JSONObject jsonObject_blog = jsonObject_temp.getJSONObject("Complain");
                                HousingEstateblogModel housingEstateblogModel = new HousingEstateblogModel();
                                housingEstateblogModel.setId(jsonObject_blog.getInt("Id"));//设置吐槽Id
                                housingEstateblogModel.setComplainContent(jsonObject_blog.getString("ComplainContent"));//设置吐槽内容
                                housingEstateblogModel.setGoodCount(jsonObject_blog.getInt("GoodCount"));//设置点赞数量
                                housingEstateblogModel.setReplyCount(jsonObject_blog.getInt("ReplyCount"));//设置回复数
                                //设置@List
                                if (jsonObject_blog.get("ParmStr1").toString().equals("null")){
                                    housingEstateblogModel.setList_At_Item(null);
                                }else {
                                    List<AtItemModel> list_atItem = (new Gson()).fromJson(jsonObject_blog.getString("ParmStr1"), new TypeToken<List<AtItemModel>>(){}.getType());
                                    housingEstateblogModel.setList_At_Item(list_atItem);
                                }
                                //设置图片List
                                List<SwpeingImageModel> list_images = new ArrayList<>();
                                JSONArray jsonArray_images = jsonObject_blog.getJSONArray("ComplainImg");
                                for (int j = 0; j < jsonArray_images.length(); j++){
                                    JSONObject jsonObject_images = jsonArray_images.getJSONObject(j);
                                    SwpeingImageModel swpeingImageModel = new SwpeingImageModel();
                                    swpeingImageModel.setUrl(jsonObject_images.getString("ImgUrl"));//设置图片URL
                                    swpeingImageModel.setOrders(jsonObject_images.getInt("Orders"));//设置图片顺序
                                    list_images.add(swpeingImageModel);
                                }
                                housingEstateblogModel.setList_images(list_images);
                                AboutMeCommentModel aboutMeCommentModel = new AboutMeCommentModel();
                                aboutMeCommentModel.setUserId(jsonObject_temp.getInt("UserId"));//设置点赞人的Id
                                aboutMeCommentModel.setUserNick(jsonObject_temp.getString("UserNick"));//设置点赞人的昵称
                                aboutMeCommentModel.setSendDate(jsonObject_temp.getString("AddTime"));//设置点赞时间
                                aboutMeCommentModel.setCommentDetails("赞了这条吐槽");
                                aboutMeCommentModel.setHousingEstateModel(housingEstateblogModel);
                                list_good.add(aboutMeCommentModel);
                            }
                            pageNum_GoodMe++;
                            listViewAdapter_aboutMe_goodMe.notifyDataSetChanged();
                            listView_goodMe.setVisibility(View.INVISIBLE);
                            listView_goodMe.setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            Toast.makeText(context, "解析返回数据失败，请反馈！", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
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
