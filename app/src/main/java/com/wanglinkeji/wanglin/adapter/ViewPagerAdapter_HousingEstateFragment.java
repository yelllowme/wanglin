package com.wanglinkeji.wanglin.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.activity.BlogDetails_NormalActivity;
import com.wanglinkeji.wanglin.model.AtItemModel;
import com.wanglinkeji.wanglin.model.CommentModel;
import com.wanglinkeji.wanglin.model.HousingEstateblogModel;
import com.wanglinkeji.wanglin.model.NewsModel;
import com.wanglinkeji.wanglin.model.SwpeingImageModel;
import com.wanglinkeji.wanglin.model.UserIdentityInfoModel;
import com.wanglinkeji.wanglin.util.DBUtil;
import com.wanglinkeji.wanglin.util.HttpUtil;
import com.wanglinkeji.wanglin.util.WangLinApplication;
import com.wanglinkeji.wanglin.util.WanglinHttpResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/22.
 * 首页->小区的ViewPager的Adapter
 */
public class ViewPagerAdapter_HousingEstateFragment extends PagerAdapter {

    private LinearLayout layout_loading_page;

    private List<View> list_views;

    private Context context;

    //三个内容为空的提示图
    private ImageView imageview_EmptyContent_houseEstate,imageView_EmptyContent_City;

    //三个ListView
    private PullToRefreshListView listView_housingEstate, listView_City;

    //三个Adapter
    private ListViewAdapter_HousingEstateBlogItem listViewAdapter_housingEstateBlogItem, listViewAdapter_housingEstateBlogItem_city;

    private int pageNum_housingEstate = 2, pageNum_city = 2;

    public ViewPagerAdapter_HousingEstateFragment(List<View> list_views, Context context, LinearLayout layout_loading_page){
        this.list_views = list_views;
        this.context = context;
        this.layout_loading_page = layout_loading_page;
    }

    @Override
    public int getCount() {
        return list_views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(list_views.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        switch (position){
            //小区吐槽
            case 0:{
                imageview_EmptyContent_houseEstate = (ImageView)list_views.get(position).findViewById(R.id.imageview_housingEstateSpewing_noContent);
                listView_housingEstate = (PullToRefreshListView) list_views.get(position).findViewById(R.id.listview_housingEstateSpewing);
                listView_housingEstate.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
                    @Override
                    public void onRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                        if (UserIdentityInfoModel.current_housingEstate != null){
                            getFirstPageHousigEstateSwpeing(String.valueOf(UserIdentityInfoModel.current_housingEstate.getId()));
                        }
                    }
                });
                listView_housingEstate.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
                    @Override
                    public void onLastItemVisible() {
                        getMorePageHousigEstateSwpeing(String.valueOf(UserIdentityInfoModel.current_housingEstate.getId()));
                    }
                });
                listView_housingEstate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(context, BlogDetails_NormalActivity.class);
                        intent.putExtra("BlogId", HousingEstateblogModel.list_housingEstateBolg.get((new Long(l)).intValue()).getId());
                        context.startActivity(intent);
                    }
                });
                //获取第一页小区吐槽列表
                if (UserIdentityInfoModel.current_housingEstate != null){
                    getFirstPageHousigEstateSwpeing(String.valueOf(UserIdentityInfoModel.current_housingEstate.getId()));
                }
                break;
            }
            //同城吐槽
            case 1:{
                imageView_EmptyContent_City = (ImageView)list_views.get(position).findViewById(R.id.imageview_CitySpewing_noContent);
                listView_City = (PullToRefreshListView)list_views.get(position).findViewById(R.id.listview_CitySpewing);
                listView_City.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
                    @Override
                    public void onRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                        //根据当前用户是否绑定小区，来决定同城吐槽的城市
                        if (UserIdentityInfoModel.current_housingEstate == null){
                            getFirstPageCitySwpeing(String.valueOf(WangLinApplication.locationInfoModel.getLocationCityId()));
                        }else {
                            getFirstPageCitySwpeing(String.valueOf(UserIdentityInfoModel.current_housingEstate.getCityId()));
                        }
                    }
                });
                listView_City.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
                    @Override
                    public void onLastItemVisible() {
                        //根据当前用户是否绑定小区，来决定同城吐槽的城市
                        if (UserIdentityInfoModel.current_housingEstate == null){
                            getMorePageCirySwpeing(String.valueOf(WangLinApplication.locationInfoModel.getLocationCityId()));
                        }else {
                            getMorePageCirySwpeing(String.valueOf(UserIdentityInfoModel.current_housingEstate.getCityId()));
                        }
                    }
                });
                listView_City.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(context, BlogDetails_NormalActivity.class);
                        intent.putExtra("BlogId", HousingEstateblogModel.list_cityBlog.get((new Long(l)).intValue()).getId());
                        context.startActivity(intent);
                    }
                });
                //获取第一页同城吐槽列表
                //根据当前用户是否绑定小区，来决定同城吐槽的城市
                if (UserIdentityInfoModel.current_housingEstate == null){
                    getFirstPageCitySwpeing(String.valueOf(WangLinApplication.locationInfoModel.getLocationCityId()));
                }else {
                    getFirstPageCitySwpeing(String.valueOf(UserIdentityInfoModel.current_housingEstate.getCityId()));
                }
                break;
            }
            //大事件（新闻）
            case 2:{
                PullToRefreshListView pullToRefreshListView = (PullToRefreshListView)list_views.get(position).findViewById(R.id.listview_houseEstate_news);
                getFirstPageNews();
                pullToRefreshListView.setAdapter(new ListViewAdapter_HouseEstateFragment_news(NewsModel.list_news, context,
                        R.layout.layout_listview_item_housing_estate_news));

                break;
            }
            default:
                break;
        }
        container.addView(list_views.get(position));
        return list_views.get(position);
    }

    private void getFirstPageHousigEstateSwpeing(final String houseEstateId){
        //第一次获取的时候让PageNum为2，因为已经正在请求第一页的数据
        pageNum_housingEstate = 2;
        Map<String, String> params = new HashMap<>();
        params.put("VillageId", houseEstateId);
        params.put("Start", "0");
        params.put("Limit", String.valueOf(HttpUtil.PAGE_SIZE));
        HttpUtil.sendVolleyStringRequest_Post(context, HttpUtil.housingEstateSwpeingList_url, params, DBUtil.getLoginUser().getToken(),
                "yellow_housingEstateSwpingList_FirstPage", new WanglinHttpResponseListener() {
                    @Override
                    public void onSuccessResponse(JSONObject jsonObject_response) {
                        listView_housingEstate.onRefreshComplete();
                        HousingEstateblogModel.list_housingEstateBolg = new ArrayList<>();
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
                                JSONArray jsonArray_comments = jsonObject_blog.getJSONArray("ComplainReply");
                                int length;
                                if (jsonArray_comments.length() > 2){
                                    length = 2;
                                }else {
                                    length = jsonArray_comments.length();
                                }
                                for (int j = 0; j < length; j++) {
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
                                HousingEstateblogModel.list_housingEstateBolg.add(housingEstateblogModel);
                            }
                            if (HousingEstateblogModel.list_housingEstateBolg.size() == 0){
                                imageview_EmptyContent_houseEstate.setVisibility(View.VISIBLE);
                                listView_housingEstate.setVisibility(View.GONE);
                            }else {
                                imageview_EmptyContent_houseEstate.setVisibility(View.GONE);
                                listView_housingEstate.setVisibility(View.VISIBLE);
                                listViewAdapter_housingEstateBlogItem = new ListViewAdapter_HousingEstateBlogItem(HousingEstateblogModel.list_housingEstateBolg, context,
                                        R.layout.layout_listview_item_housing_estate_spewing);
                                listView_housingEstate.setAdapter(listViewAdapter_housingEstateBlogItem);
                            }
                            layout_loading_page.setVisibility(View.GONE);
                        }catch (JSONException e){
                            imageview_EmptyContent_houseEstate.setVisibility(View.VISIBLE);
                            listView_housingEstate.setVisibility(View.GONE);
                            layout_loading_page.setVisibility(View.GONE);
                            Toast.makeText(context, "解析返回结果出错，请反馈！", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onConnectingError() {
                        imageview_EmptyContent_houseEstate.setVisibility(View.VISIBLE);
                        listView_housingEstate.setVisibility(View.GONE);
                        layout_loading_page.setVisibility(View.GONE);
                        listView_housingEstate.onRefreshComplete();
                    }
                    @Override
                    public void onDisconnectError() {
                        imageview_EmptyContent_houseEstate.setVisibility(View.VISIBLE);
                        listView_housingEstate.setVisibility(View.GONE);
                        layout_loading_page.setVisibility(View.GONE);
                        listView_housingEstate.onRefreshComplete();
                    }
                });
    }

    private void getMorePageHousigEstateSwpeing(String houseEstateId){
        Map<String, String> params = new HashMap<>();
        params.put("VillageId", houseEstateId); //测试小区ID
        params.put("Start", String.valueOf((pageNum_housingEstate - 1) * HttpUtil.PAGE_SIZE));
        params.put("Limit", String.valueOf(HttpUtil.PAGE_SIZE));
        HttpUtil.sendVolleyStringRequest_Post(context, HttpUtil.housingEstateSwpeingList_url, params, DBUtil.getLoginUser().getToken(),
                "yellow_housingEstateSwpingList_MorePage", new WanglinHttpResponseListener() {
                    @Override
                    public void onSuccessResponse(JSONObject jsonObject_response) {
                        try {
                            JSONArray jsonArray_blog = jsonObject_response.getJSONArray("Data");
                            //只有在获取的页数中有数据时才Page++
                            if (jsonArray_blog.length() > 0){
                                pageNum_housingEstate++;
                            }
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
                                JSONArray jsonArray_comments = jsonObject_blog.getJSONArray("ComplainReply");
                                int length;
                                if (jsonArray_comments.length() > 2){
                                    length = 2;
                                }else {
                                    length = jsonArray_comments.length();
                                }
                                for (int j = 0; j < length; j++) {
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
                                HousingEstateblogModel.list_housingEstateBolg.add(housingEstateblogModel);
                            }
                            listViewAdapter_housingEstateBlogItem.notifyDataSetChanged();
                            listView_housingEstate.setVisibility(View.INVISIBLE);
                            listView_housingEstate.setVisibility(View.VISIBLE);
                        }catch (JSONException e){
                            Toast.makeText(context, "解析返回结果出错，请反馈！", Toast.LENGTH_SHORT).show();
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

    private void getFirstPageCitySwpeing(String cityId){
        //第一次获取的时候让PageNum为2，因为已经正在请求第一页的数据
        pageNum_city = 2;
        Map<String, String> params = new HashMap<>();
        params.put("CityId", cityId);
        params.put("Start", "0");
        params.put("Limit", String.valueOf(HttpUtil.PAGE_SIZE));
        HttpUtil.sendVolleyStringRequest_Post(context, HttpUtil.citySwpeingList_url, params, DBUtil.getLoginUser().getToken(),
                "yellow_citySwpeingList_firstPage", new WanglinHttpResponseListener() {
                    @Override
                    public void onSuccessResponse(JSONObject jsonObject_response) {
                        listView_City.onRefreshComplete();
                        HousingEstateblogModel.list_cityBlog = new ArrayList<>();
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
                                JSONArray jsonArray_comments = jsonObject_blog.getJSONArray("ComplainReply");
                                int length;
                                if (jsonArray_comments.length() > 2){
                                    length = 2;
                                }else {
                                    length = jsonArray_comments.length();
                                }
                                for (int j = 0; j < length; j++) {
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
                                HousingEstateblogModel.list_cityBlog.add(housingEstateblogModel);
                            }
                            if (HousingEstateblogModel.list_cityBlog.size() == 0){
                                imageView_EmptyContent_City.setVisibility(View.VISIBLE);
                                listView_City.setVisibility(View.GONE);
                            }else {
                                imageView_EmptyContent_City.setVisibility(View.GONE);
                                listView_City.setVisibility(View.VISIBLE);
                                listViewAdapter_housingEstateBlogItem_city = new ListViewAdapter_HousingEstateBlogItem(HousingEstateblogModel.list_cityBlog, context,
                                        R.layout.layout_listview_item_housing_estate_spewing);
                                listView_City.setAdapter(listViewAdapter_housingEstateBlogItem_city);
                            }
                            layout_loading_page.setVisibility(View.GONE);
                        }catch (JSONException e){
                            layout_loading_page.setVisibility(View.GONE);
                            imageView_EmptyContent_City.setVisibility(View.VISIBLE);
                            listView_City.setVisibility(View.GONE);
                            Toast.makeText(context, "解析返回结果出错，请反馈！", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onConnectingError() {
                        imageView_EmptyContent_City.setVisibility(View.VISIBLE);
                        listView_City.setVisibility(View.GONE);
                        layout_loading_page.setVisibility(View.GONE);
                        listView_City.onRefreshComplete();
                    }
                    @Override
                    public void onDisconnectError() {
                        imageView_EmptyContent_City.setVisibility(View.VISIBLE);
                        listView_City.setVisibility(View.GONE);
                        layout_loading_page.setVisibility(View.GONE);
                        listView_City.onRefreshComplete();
                    }
                });
    }

    private void getMorePageCirySwpeing(String cityId){
        Map<String, String> params = new HashMap<>();
        params.put("CityId",cityId);
        params.put("Start", String.valueOf((pageNum_city - 1) * HttpUtil.PAGE_SIZE));
        params.put("Limit", String.valueOf(HttpUtil.PAGE_SIZE));
        HttpUtil.sendVolleyStringRequest_Post(context, HttpUtil.citySwpeingList_url, params, DBUtil.getLoginUser().getToken(),
                "yellow_citySwpeingList_morePage", new WanglinHttpResponseListener() {
                    @Override
                    public void onSuccessResponse(JSONObject jsonObject_response) {
                        try {
                            JSONArray jsonArray_blog = jsonObject_response.getJSONArray("Data");
                            for (int i = 0; i < jsonArray_blog.length(); i++){
                                JSONObject jsonObject_blog = jsonArray_blog.getJSONObject(i);
                                //只有在获取到的页数中有数据是才PageNum++
                                if (jsonArray_blog.length() > 0){
                                    pageNum_city++;
                                }
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
                                JSONArray jsonArray_comments = jsonObject_blog.getJSONArray("ComplainReply");
                                int length;
                                if (jsonArray_comments.length() > 2){
                                    length = 2;
                                }else {
                                    length = jsonArray_comments.length();
                                }
                                for (int j = 0; j < length; j++) {
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
                                HousingEstateblogModel.list_cityBlog.add(housingEstateblogModel);
                            }
                            listViewAdapter_housingEstateBlogItem_city.notifyDataSetChanged();
                            listView_City.setVisibility(View.INVISIBLE);
                            listView_City.setVisibility(View.VISIBLE);
                        }catch (JSONException e){
                            Toast.makeText(context, "解析返回结果出错，请反馈！", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onConnectingError() {
                        listView_City.onRefreshComplete();
                    }
                    @Override
                    public void onDisconnectError() {
                        listView_City.onRefreshComplete();
                    }
                });
    }

    private void getFirstPageNews(){
        NewsModel.list_news = new ArrayList<>();
        NewsModel newsModel = new NewsModel();
        newsModel.setNewsTitle("生活不止眼前的苟且");
        newsModel.setNewsPreview("还有远方的苟且");
        newsModel.setImageUrl("http://fun.youth.cn/yl24xs/201609/W020160929371649467843.png");
        newsModel.setNewsDate("今天 10:30");
        newsModel.setNewsFromWhat("东拉西扯");
        newsModel.setNewsSeeCount(123);

        NewsModel newsModel1 = new NewsModel();
        newsModel1.setNewsTitle("S6全球总决赛今日开赛");
        newsModel1.setNewsPreview("反正3:0我上我也行");
        newsModel1.setImageUrl("http://image.uuu9.com/pcgame/lol/UploadFiles/201511/201511091132199171.jpg");
        newsModel1.setNewsDate("今天 10:30");
        newsModel1.setNewsFromWhat("东拉西扯");
        newsModel1.setNewsSeeCount(123);

        NewsModel newsModel2 = new NewsModel();
        newsModel2.setNewsTitle("中共中央学习《胡锦涛文选》");
        newsModel2.setNewsPreview("习近平发表重要讲话");
        newsModel2.setImageUrl("http://img2.imgtn.bdimg.com/it/u=2699428268,1223202576&fm=21&gp=0.jpg");
        newsModel2.setNewsDate("今天 10:30");
        newsModel2.setNewsFromWhat("东拉西扯");
        newsModel2.setNewsSeeCount(123);

        NewsModel newsModel3 = new NewsModel();
        newsModel3.setNewsTitle("山东纵火犯被执行死刑");
        newsModel3.setNewsPreview("法网恢恢 疏而不漏");
        newsModel3.setImageUrl("http://img2.itiexue.net/1986/19867334.jpg");
        newsModel3.setNewsDate("今天 10:30");
        newsModel3.setNewsFromWhat("东拉西扯");
        newsModel3.setNewsSeeCount(123);

        NewsModel newsModel4 = new NewsModel();
        newsModel4.setNewsTitle("国庆期间去哪里");
        newsModel4.setNewsPreview("还是哪里都不要去的好");
        newsModel4.setImageUrl("http://img.hexun.com/20150615/70222734-63334200.jpg");
        newsModel4.setNewsDate("今天 10:30");
        newsModel4.setNewsFromWhat("东拉西扯");
        newsModel4.setNewsSeeCount(123);

        NewsModel.list_news.add(newsModel);
        NewsModel.list_news.add(newsModel1);
        NewsModel.list_news.add(newsModel2);
        NewsModel.list_news.add(newsModel3);
        NewsModel.list_news.add(newsModel4);
    }

}
