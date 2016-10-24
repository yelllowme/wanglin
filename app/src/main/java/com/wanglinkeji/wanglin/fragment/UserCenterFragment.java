package com.wanglinkeji.wanglin.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.activity.AddHouseEstate_HouseProCerActivity;
import com.wanglinkeji.wanglin.activity.BlogDetails_NormalActivity;
import com.wanglinkeji.wanglin.activity.ChoosedPhoto_SmallActivity;
import com.wanglinkeji.wanglin.activity.LoginActivity;
import com.wanglinkeji.wanglin.activity.MyHouseEstateActivity;
import com.wanglinkeji.wanglin.activity.SettingActivity;
import com.wanglinkeji.wanglin.adapter.ListViewAdapter_HousingEstateBlogItem;
import com.wanglinkeji.wanglin.customerview.MyListView;
import com.wanglinkeji.wanglin.model.AtItemModel;
import com.wanglinkeji.wanglin.model.CommentModel;
import com.wanglinkeji.wanglin.model.HousingEstateblogModel;
import com.wanglinkeji.wanglin.model.PhotoModel;
import com.wanglinkeji.wanglin.model.SwpeingImageModel;
import com.wanglinkeji.wanglin.model.UserIdentityInfoModel;
import com.wanglinkeji.wanglin.util.DBUtil;
import com.wanglinkeji.wanglin.util.HttpUtil;
import com.wanglinkeji.wanglin.util.OtherUtil;
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
 * Created by Administrator on 2016/8/16.
 * 首页->我的Fragment
 */
public class UserCenterFragment extends Fragment implements View.OnClickListener {

    private View view;

    private LinearLayout layout_personalInfo, layout_title_userInfo, layout_title_userBlog, layout_title_userLife;

    private RelativeLayout layout_emptyPhotoWall;

    private MyListView listView_myBlog;

    private TextView textView_title_userInfo, textView_title_userBlog, textView_title_userLife, textView_userName, textView_phoneNum;

    private ImageView imageView_title_userInfo_point, imageView_title_userBlog_point, imageView_title_userLife_point, imageView_setting;

    private List<HousingEstateblogModel> list_myBlog;

    private ListViewAdapter_HousingEstateBlogItem listViewAdapter_housingEstateBlogItem;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_fragment_usercenter, container, false);

        viewInit();
        getFirstPageMyBlog(getActivity());
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imageview_userCenter_setting:{
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.layout_userCenter_title_userInfo:{
                setView_choosedUserInfo();
                break;
            }
            case R.id.layout_userCenter_title_userBlog:{
                setView_choosedUserBlog();
                break;
            }
            case R.id.layout_userCenter_title_userLife:{
                setView_choosedUserLife();
                break;
            }
            case R.id.layout_userCenter_emptyPhotoWall:{
                PhotoModel.finishText = "上传";
                PhotoModel.photoCount = 9;
                PhotoModel.list_choosedPhotos = new ArrayList<>();
                Intent intent = new Intent(getActivity(), ChoosedPhoto_SmallActivity.class);
                startActivity(intent);
                break;
            }
            default:
                break;
        }
    }

    private void viewInit(){
        layout_title_userBlog = (LinearLayout)view.findViewById(R.id.layout_userCenter_title_userBlog);
        layout_title_userBlog.setOnClickListener(this);
        layout_title_userInfo = (LinearLayout)view.findViewById(R.id.layout_userCenter_title_userInfo);
        layout_title_userInfo.setOnClickListener(this);
        layout_title_userLife = (LinearLayout)view.findViewById(R.id.layout_userCenter_title_userLife);
        layout_title_userLife.setOnClickListener(this);
        textView_title_userBlog = (TextView)view.findViewById(R.id.textview_userCenter_title_userBlog);
        textView_title_userInfo = (TextView)view.findViewById(R.id.textview_userCenter_title_userInfo);
        textView_title_userLife = (TextView)view.findViewById(R.id.textview_userCenter_title_userLife);
        imageView_title_userBlog_point = (ImageView)view.findViewById(R.id.imageview_userCenter_title_userBlog_point);
        imageView_title_userInfo_point = (ImageView)view.findViewById(R.id.imageview_userCenter_title_userInfo_point);
        imageView_title_userLife_point = (ImageView)view.findViewById(R.id.imageview_userCenter_title_userLife_point);
        imageView_setting = (ImageView)view.findViewById(R.id.imageview_userCenter_setting);
        imageView_setting.setOnClickListener(this);
        textView_userName = (TextView)view.findViewById(R.id.textview_userCenter_userName);
        textView_userName.setText(UserIdentityInfoModel.userName);
        textView_phoneNum = (TextView)view.findViewById(R.id.textview_userCenter_phoneNum);
        textView_phoneNum.setText(DBUtil.getLoginUser().getPhone());
        layout_emptyPhotoWall = (RelativeLayout)view.findViewById(R.id.layout_userCenter_emptyPhotoWall);
        layout_emptyPhotoWall.setOnClickListener(this);
        layout_personalInfo = (LinearLayout)view.findViewById(R.id.layout_userCenter_personalInfo);
        listView_myBlog = (MyListView)view.findViewById(R.id.listview_userCenter_myBlog);
        listView_myBlog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), BlogDetails_NormalActivity.class);
                intent.putExtra("BlogId", list_myBlog.get((new Long(l)).intValue()).getId());
                getActivity().startActivity(intent);
            }
        });
    }

    private void setView_choosedUserInfo(){
        textView_title_userInfo.setTextColor(0XFF008DD4);
        textView_title_userBlog.setTextColor(0XFF2F2F2F);
        textView_title_userLife.setTextColor(0XFF2F2F2F);
        imageView_title_userInfo_point.setVisibility(View.VISIBLE);
        imageView_title_userBlog_point.setVisibility(View.INVISIBLE);
        imageView_title_userLife_point.setVisibility(View.INVISIBLE);
        layout_personalInfo.setVisibility(View.VISIBLE);
        listView_myBlog.setVisibility(View.GONE);
    }

    private void setView_choosedUserBlog(){
        textView_title_userInfo.setTextColor(0XFF2F2F2F);
        textView_title_userBlog.setTextColor(0XFF008DD4);
        textView_title_userLife.setTextColor(0XFF2F2F2F);
        imageView_title_userInfo_point.setVisibility(View.INVISIBLE);
        imageView_title_userBlog_point.setVisibility(View.VISIBLE);
        imageView_title_userLife_point.setVisibility(View.INVISIBLE);
        layout_personalInfo.setVisibility(View.GONE);
        listView_myBlog.setVisibility(View.VISIBLE);
    }

    private void setView_choosedUserLife(){
        textView_title_userInfo.setTextColor(0XFF2F2F2F);
        textView_title_userBlog.setTextColor(0XFF2F2F2F);
        textView_title_userLife.setTextColor(0XFF008DD4);
        imageView_title_userInfo_point.setVisibility(View.INVISIBLE);
        imageView_title_userBlog_point.setVisibility(View.INVISIBLE);
        imageView_title_userLife_point.setVisibility(View.VISIBLE);
        layout_personalInfo.setVisibility(View.GONE);
        listView_myBlog.setVisibility(View.GONE);
    }

    private void getFirstPageMyBlog(final Context context){
        Map<String, String> params = new HashMap<>();
        params.put("pageIndex", String.valueOf(1));
        params.put("pageSize", String.valueOf(HttpUtil.PAGE_SIZE));
        HttpUtil.sendVolleyStringRequest_Post(context, HttpUtil.getMyBlog_url, params, DBUtil.getLoginUser().getToken(), "yellow_getFirstPageMyBlog",
                new WanglinHttpResponseListener() {
                    @Override
                    public void onSuccessResponse(JSONObject jsonObject_response) {
                        list_myBlog = new ArrayList<HousingEstateblogModel>();
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
                                list_myBlog.add(housingEstateblogModel);
                            }
                            listViewAdapter_housingEstateBlogItem = new ListViewAdapter_HousingEstateBlogItem(list_myBlog, context,
                                    R.layout.layout_listview_item_housing_estate_spewing);
                            listView_myBlog.setAdapter(listViewAdapter_housingEstateBlogItem);
                        }catch (JSONException e){
                            Toast.makeText(context, "解析返回结果出错，请反馈！", Toast.LENGTH_SHORT).show();
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
