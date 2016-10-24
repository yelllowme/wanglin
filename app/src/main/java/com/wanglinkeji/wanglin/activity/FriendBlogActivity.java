package com.wanglinkeji.wanglin.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.adapter.ListViewAdapter_HousingEstateBlogItem;
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
 * 好友吐槽
 */

public class FriendBlogActivity extends Activity implements View.OnClickListener {

    private ImageView imageView_cancle;

    private PullToRefreshListView listView_blogList;

    private PopupWindow loading_page;

    private View rootView;

    //好友吐槽List
    private List<HousingEstateblogModel> list_friendBlog;

    //好友吐槽Adapter
    private ListViewAdapter_HousingEstateBlogItem listViewAdapter_housingEstateBlogItem;

    //请求页数，好友吐槽总条数
    private int pageNum, totleNum;

    //用来控制一开始弹出的加载页只显示一次
    private boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_activity_friend_blog);

        viewInit();
        getFirstPageFriendBlog(FriendBlogActivity.this);
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
            case R.id.imageview_friendBlog_cancle:{
                FriendBlogActivity.this.finish();
            }
            default:
                break;
        }
    }

    private void viewInit(){
        loading_page = OtherUtil.getLoadingPage(FriendBlogActivity.this);
        rootView = LayoutInflater.from(FriendBlogActivity.this).inflate(R.layout.layout_activity_friend_blog, null);
        imageView_cancle = (ImageView)findViewById(R.id.imageview_friendBlog_cancle);
        imageView_cancle.setOnClickListener(this);
        listView_blogList = (PullToRefreshListView)findViewById(R.id.listview_friendBlog_blogList);
        listView_blogList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(FriendBlogActivity.this, BlogDetails_NormalActivity.class);
                intent.putExtra("BlogId", list_friendBlog.get((new Long(l)).intValue()).getId());
                startActivity(intent);
            }
        });
        listView_blogList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                getMorePageFriendBlog(FriendBlogActivity.this);
            }
        });
        listView_blogList.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                if ((totleNum - pageNum * HttpUtil.PAGE_SIZE) > -HttpUtil.PAGE_SIZE){
                    getMorePageFriendBlog(FriendBlogActivity.this);
                }
            }
        });
    }

    private void getFirstPageFriendBlog(final Context context){
        pageNum = 2;
        Map<String, String> params = new HashMap<>();
        params.put("pageIndex", String.valueOf(1));
        params.put("pageSize", String.valueOf(HttpUtil.PAGE_SIZE));
        HttpUtil.sendVolleyStringRequest_Post(context, HttpUtil.aboutMe_AtMe_url, params, DBUtil.getLoginUser().getToken(), "yellow_getFirstPageAtMe",
                new WanglinHttpResponseListener() {
                    @Override
                    public void onSuccessResponse(JSONObject jsonObject_response) {
                        try {
                            totleNum = jsonObject_response.getInt("Total");
                            JSONArray jsonArray_blog = jsonObject_response.getJSONArray("Data");
                            list_friendBlog = new ArrayList<HousingEstateblogModel>();
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
                                list_friendBlog.add(housingEstateblogModel);
                            }
                            listViewAdapter_housingEstateBlogItem = new ListViewAdapter_HousingEstateBlogItem(list_friendBlog, context,
                                    R.layout.layout_listview_item_housing_estate_spewing);
                            listView_blogList.setAdapter(listViewAdapter_housingEstateBlogItem);
                            listView_blogList.onRefreshComplete();
                            loading_page.dismiss();
                        } catch (JSONException e) {
                            listView_blogList.onRefreshComplete();
                            loading_page.dismiss();
                            Toast.makeText(context, "解析返回数据失败，请反馈！", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onConnectingError() {
                        listView_blogList.onRefreshComplete();
                        loading_page.dismiss();
                    }

                    @Override
                    public void onDisconnectError() {
                        listView_blogList.onRefreshComplete();
                        loading_page.dismiss();
                    }
                });
    }

    private void getMorePageFriendBlog(final Context context){
        Map<String, String> params = new HashMap<>();
        params.put("pageIndex", String.valueOf(pageNum));
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
                                list_friendBlog.add(housingEstateblogModel);
                            }
                            pageNum++;
                            listViewAdapter_housingEstateBlogItem.notifyDataSetChanged();
                            listView_blogList.setVisibility(View.INVISIBLE);
                            listView_blogList.setVisibility(View.VISIBLE);
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
}
