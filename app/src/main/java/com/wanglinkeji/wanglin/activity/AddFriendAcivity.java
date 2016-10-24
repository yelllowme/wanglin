package com.wanglinkeji.wanglin.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.adapter.ListViewAdapter_addFriend_searchList;
import com.wanglinkeji.wanglin.model.Search_UserInfoModel;
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
 * Created by Administrator on 2016/10/2.
 * 添加好友
 */

public class AddFriendAcivity extends Activity implements View.OnClickListener {

    private ImageView imageView_cancle, imageView_search;

    private EditText editText_phoneNum;

    private PullToRefreshListView listView_searchList;

    private PopupWindow loading_page;

    private View rootView;

    private ListViewAdapter_addFriend_searchList listViewAdapter_addFriend_searchList;

    private String keyWord;

    private int pageNum, totleNum;

    private List<Search_UserInfoModel> list_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_activity_add_friend);

        viewInit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imageview_addFriend_cancle:{
                AddFriendAcivity.this.finish();
                break;
            }
            case R.id.imageview_addFriend_search:{
                if (editText_phoneNum.getText().length() == 0){
                    Toast.makeText(AddFriendAcivity.this, "搜索内容不能为空哦！", Toast.LENGTH_SHORT).show();
                }else {
                    keyWord = editText_phoneNum.getText().toString();
                    loading_page.showAtLocation(rootView, Gravity.CENTER, 0 ,0);
                    getFirstPageSearchFriend(AddFriendAcivity.this, keyWord);
                    //收起软键盘
                    ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editText_phoneNum.getWindowToken(), 0);
                }
                break;
            }
            default:
                break;
        }
    }

    private void viewInit(){
        loading_page = OtherUtil.getLoadingPage(AddFriendAcivity.this);
        rootView = LayoutInflater.from(AddFriendAcivity.this).inflate(R.layout.layout_activity_add_friend, null);
        imageView_cancle = (ImageView)findViewById(R.id.imageview_addFriend_cancle);
        imageView_cancle.setOnClickListener(this);
        imageView_search = (ImageView)findViewById(R.id.imageview_addFriend_search);
        imageView_search.setOnClickListener(this);
        editText_phoneNum = (EditText)findViewById(R.id.edittext_addFriend_phoneNum);
        editText_phoneNum.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (editText_phoneNum.getText().length() == 0){
                    Toast.makeText(AddFriendAcivity.this, "搜索内容不能为空哦！", Toast.LENGTH_SHORT).show();
                }else {
                    keyWord = editText_phoneNum.getText().toString();
                    loading_page.showAtLocation(rootView, Gravity.CENTER, 0 ,0);
                    getFirstPageSearchFriend(AddFriendAcivity.this, keyWord);
                    //收起软键盘
                    ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editText_phoneNum.getWindowToken(), 0);
                }
                return true;
            }
        });
        editText_phoneNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                listView_searchList.setVisibility(View.INVISIBLE);
            }
        });
        listView_searchList = (PullToRefreshListView) findViewById(R.id.listview_addFriend_friendList);
        listView_searchList.setVisibility(View.INVISIBLE);
        listView_searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(AddFriendAcivity.this, UserInfoActivity.class);
                intent.putExtra("userId", list_user.get((new Long(l)).intValue()).getUser_id());
                startActivity(intent);
            }
        });
        listView_searchList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                getFirstPageSearchFriend(AddFriendAcivity.this, keyWord);
            }
        });
        listView_searchList.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                if ((totleNum - pageNum * HttpUtil.PAGE_SIZE) > -HttpUtil.PAGE_SIZE){
                    getMorePageSearchFriend(AddFriendAcivity.this, keyWord);
                }
            }
        });
    }

    private void getFirstPageSearchFriend(final Context context, String keyWord){
        pageNum = 2;
        Map<String, String> params = new HashMap<>();
        params.put("Key", keyWord);
        params.put("pageIndex", String.valueOf(1));
        params.put("pageSize", String.valueOf(HttpUtil.PAGE_SIZE));
        HttpUtil.sendVolleyStringRequest_Post(context, HttpUtil.selectUserByKeyWord_url, params, DBUtil.getLoginUser().getToken(), "yellow_getFistPageSearchUserByKeyWord",
                new WanglinHttpResponseListener() {
                    @Override
                    public void onSuccessResponse(JSONObject jsonObject_response) {
                        try {
                            totleNum = jsonObject_response.getInt("Total");
                            list_user = new ArrayList<Search_UserInfoModel>();
                            JSONArray jsonArray_user = jsonObject_response.getJSONArray("Data");
                            for (int i = 0; i < jsonArray_user.length(); i++){
                                JSONObject jsonObject_user = jsonArray_user.getJSONObject(i);
                                Search_UserInfoModel search_userInfoModel = new Search_UserInfoModel();
                                search_userInfoModel.setUser_id(jsonObject_user.getInt("Id"));//获取用户Id
                                search_userInfoModel.setUserName(jsonObject_user.getString("UserRealName"));//获取用户昵称
                                list_user.add(search_userInfoModel);
                            }
                            listView_searchList.setVisibility(View.VISIBLE);
                            listViewAdapter_addFriend_searchList = new ListViewAdapter_addFriend_searchList(list_user, context,
                                    R.layout.layout_listview_item_add_friend_seatch_list);
                            listView_searchList.setAdapter(listViewAdapter_addFriend_searchList);
                            listView_searchList.onRefreshComplete();
                            loading_page.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            listView_searchList.onRefreshComplete();
                            loading_page.dismiss();
                            Toast.makeText(context, "解析返回数据失败，请反馈！", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onConnectingError() {
                        loading_page.dismiss();
                        listView_searchList.onRefreshComplete();
                    }

                    @Override
                    public void onDisconnectError() {
                        loading_page.dismiss();
                        listView_searchList.onRefreshComplete();
                    }
                });
    }

    private void getMorePageSearchFriend(final Context context, String keyWord){
        Map<String, String> params = new HashMap<>();
        params.put("Key", keyWord);
        params.put("pageIndex", String.valueOf(pageNum));
        params.put("pageSize", String.valueOf(HttpUtil.PAGE_SIZE));
        HttpUtil.sendVolleyStringRequest_Post(context, HttpUtil.selectUserByKeyWord_url, params, DBUtil.getLoginUser().getToken(), "yellow_getMorePageSearchUserByKeyWord",
                new WanglinHttpResponseListener() {
                    @Override
                    public void onSuccessResponse(JSONObject jsonObject_response) {
                        try {
                            JSONArray jsonArray_user = jsonObject_response.getJSONArray("Data");
                            for (int i = 0; i < jsonArray_user.length(); i++){
                                JSONObject jsonObject_user = jsonArray_user.getJSONObject(i);
                                Search_UserInfoModel search_userInfoModel = new Search_UserInfoModel();
                                search_userInfoModel.setUser_id(jsonObject_user.getInt("Id"));//获取用户Id
                                search_userInfoModel.setUserName(jsonObject_user.getString("UserRealName"));//获取用户昵称
                                list_user.add(search_userInfoModel);
                            }
                            pageNum++;
                            listViewAdapter_addFriend_searchList.notifyDataSetChanged();
                            listView_searchList.setVisibility(View.INVISIBLE);
                            listView_searchList.setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            loading_page.dismiss();
                            Toast.makeText(context, "解析返回数据失败，请反馈！", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onConnectingError() {}

                    @Override
                    public void onDisconnectError() {}
                });
    }
}
