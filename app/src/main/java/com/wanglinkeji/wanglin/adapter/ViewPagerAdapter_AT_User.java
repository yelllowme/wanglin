package com.wanglinkeji.wanglin.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.activity.AT_UserActivity;
import com.wanglinkeji.wanglin.customerview.SideBar;
import com.wanglinkeji.wanglin.model.AtItemModel;
import com.wanglinkeji.wanglin.model.UserFriendModel;
import com.wanglinkeji.wanglin.model.UserIdentityInfoModel;
import com.wanglinkeji.wanglin.util.CharacterParser;
import com.wanglinkeji.wanglin.util.DBUtil;
import com.wanglinkeji.wanglin.util.HttpUtil;
import com.wanglinkeji.wanglin.util.PinyinComparator;
import com.wanglinkeji.wanglin.util.WanglinHttpResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/22.
 * @好友页ViewPage Adapter
 */

public class ViewPagerAdapter_AT_User extends PagerAdapter {

    private List<View> list_views;

    private Context context;

    private PopupWindow loading_page;

    private View rootView;

    private ListView listView_userFrinds;

    private ListViewAdapter_AtUser_AtHouseEstate listViewAdapter_atUser_atHouseEstate;

    private int blockNum, unitNum, floorNum;

    private String roomNum;

    public ViewPagerAdapter_AT_User(List<View> list_views, Context context, PopupWindow loading_page, View rootView){
        this.list_views = list_views;
        this.context = context;
        this.loading_page = loading_page;
        this.rootView = rootView;
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
        container.removeView((View)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        switch (position){
            //@小区好友
            case 0:{
                final EditText editText_searchText = (EditText)list_views.get(position).findViewById(R.id.edittext_AtUserViewPager_AtHouseEstate_searchText);
                listView_userFrinds = (ListView)list_views.get(position).findViewById(R.id.listview_AtUserViewPager_AtHouseEstate_userList);
                SideBar sideBar = (SideBar)list_views.get(position).findViewById(R.id.sidebar_AtUserViewPager_AtHouseEstate);
                final ListView listView_search = (ListView)list_views.get(position).findViewById(R.id.listview_AtUserViewPager_AtHouseEstate_searchUserList);
                //设置右侧触摸监听
                sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
                    @Override
                    public void onTouchingLetterChanged(String s) {
                        //该字母首次出现的位置
                        int position = listViewAdapter_atUser_atHouseEstate.getPositionForSection(s.charAt(0));
                        if(position != -1){
                            listView_userFrinds.setSelection(position);
                        }
                    }
                });
                editText_searchText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (editText_searchText.getText().length() == 0){
                            listView_search.setVisibility(View.GONE);
                        }else {
                            listView_search.setVisibility(View.VISIBLE);
                            List<UserFriendModel> list_friends = new ArrayList<UserFriendModel>();
                            for (int i = 0; i < UserFriendModel.list_friends.size(); i++){
                                if (UserFriendModel.list_friends.get(i).getName().contains(editText_searchText.getText().toString())){
                                    list_friends.add(UserFriendModel.list_friends.get(i));
                                }
                            }
                            listView_search.setAdapter(new ListViewAdapter_AtUser_AtHouseEstate_SearchList(list_friends, context,
                                    R.layout.layout_listview_item_at_user_at_houseestate));
                        }
                    }
                });
                listView_userFrinds.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        AtItemModel atItemModel = new AtItemModel();
                        atItemModel.setFromWhat(AT_UserActivity.FROM_WHAT_AT_USER);
                        atItemModel.setAtId(UserFriendModel.list_friends.get((new Long(l).intValue())).getFriendId());
                        atItemModel.setContent(UserFriendModel.list_friends.get((new Long(l).intValue())).getName());
                        AtItemModel.current_AtItem = atItemModel;
                        ((AT_UserActivity)context).setResult(Activity.RESULT_OK);
                        ((AT_UserActivity)context).finish();
                    }
                });
                listView_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        AtItemModel atItemModel = new AtItemModel();
                        atItemModel.setFromWhat(AT_UserActivity.FROM_WHAT_AT_USER);
                        atItemModel.setAtId(UserFriendModel.list_friends.get((new Long(l).intValue())).getFriendId());
                        atItemModel.setContent(UserFriendModel.list_friends.get((new Long(l).intValue())).getName());
                        AtItemModel.current_AtItem = atItemModel;
                        ((AT_UserActivity)context).setResult(Activity.RESULT_OK);
                        ((AT_UserActivity)context).finish();
                    }
                });

                loading_page.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                getUserFriendList(context);
                break;
            }
            //@邻居
            case 1:{
                final EditText editText_blockNum = (EditText)list_views.get(position).findViewById(R.id.edittext_AtUserViewPager_AtNeighbor_blockNum);
                final EditText editText_unitNum = (EditText)list_views.get(position).findViewById(R.id.edittext_AtUserViewPager_AtNeighbor_unitNum);
                final EditText editText_floorNum = (EditText)list_views.get(position).findViewById(R.id.edittext_AtUserViewPager_AtNeighbor_floorNum);
                final EditText editText_roomNum = (EditText)list_views.get(position).findViewById(R.id.edittext_AtUserViewPager_AtNeighbor_roomNum);
                TextView textView_ensure = (TextView)list_views.get(position).findViewById(R.id.textview_AtUserViewPager_AtNeighbor_finish);
                textView_ensure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (UserIdentityInfoModel.getDefaultHouseEstate() == null){
                            Toast.makeText(context, "您还没有绑定小区或者没有选择默认小区哦！", Toast.LENGTH_SHORT).show();
                        }else {
                            if (editText_blockNum.getText().length() == 0 && editText_unitNum.getText().length() == 0){
                                Toast.makeText(context, "栋和单元请至少填一个哦！", Toast.LENGTH_SHORT).show();
                            }else {
                                if (editText_blockNum.getText().length() == 0){
                                    blockNum = -1;
                                }else {
                                    blockNum = Integer.valueOf(editText_blockNum.getText().toString());
                                }
                                if (editText_unitNum.getText().length() == 0){
                                    unitNum = -1;
                                }else {
                                    unitNum = Integer.valueOf(editText_unitNum.getText().toString());
                                }
                                if (editText_floorNum.getText().length() == 0){
                                    Toast.makeText(context, "楼层不能为空哦！", Toast.LENGTH_SHORT).show();
                                }else {
                                    floorNum = Integer.valueOf(editText_floorNum.getText().toString());
                                    if (editText_roomNum.getText().length() == 0){
                                        Toast.makeText(context, "房间号不能为空哦！", Toast.LENGTH_SHORT).show();
                                    }else {
                                        roomNum = editText_roomNum.getText().toString();
                                        loading_page.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                                        getHouseId(context, UserIdentityInfoModel.getDefaultHouseEstate().getId(), blockNum, unitNum, floorNum, roomNum);
                                    }
                                }
                            }
                        }
                    }
                });
                break;
            }
            default:
                break;
        }
        container.addView(list_views.get(position));
        return list_views.get(position);
    }

    private void getUserFriendList(final Context context){
        Map<String, String> params = new HashMap<>();
        UserFriendModel.list_friends = new ArrayList<>();
        HttpUtil.sendVolleyStringRequest_Post(context, HttpUtil.getUserFriendList_url, params, DBUtil.getLoginUser().getToken(), "yellow_userFriendList",
                new WanglinHttpResponseListener() {
                    @Override
                    public void onSuccessResponse(JSONObject jsonObject_response) {
                        try {
                            JSONArray jsonArray_group = jsonObject_response.getJSONArray("Data");
                            for (int i = 0; i < jsonArray_group.length(); i++){
                                JSONObject jsonObject_group = jsonArray_group.getJSONObject(i);
                                JSONArray jsonArray_friend = jsonObject_group.getJSONArray("UserFriendList");
                                for (int j = 0; j < jsonArray_friend.length(); j++){
                                    JSONObject jsonObject_friend = jsonArray_friend.getJSONObject(j);
                                    UserFriendModel userFriendModel = new UserFriendModel();
                                    userFriendModel.setFriendId(jsonObject_friend.getInt("FriendUserId")); //设置好友ID
                                    userFriendModel.setName(jsonObject_friend.getString("UserRealName"));//设置好友昵称
                                    userFriendModel.setRemark(jsonObject_friend.getString("Remarks"));//设置好友备注
                                    //汉字转换成拼音
                                    CharacterParser characterParser = CharacterParser.getInstance();
                                    String pinyin = characterParser.getSelling(userFriendModel.getName());
                                    if (pinyin.length() > 0){
                                        String sortString = pinyin.substring(0, 1).toUpperCase();
                                        // 正则表达式，判断首字母是否是英文字母
                                        if(sortString.matches("[A-Z]")){
                                            userFriendModel.setSortLetters(sortString.toUpperCase());
                                        }else{
                                            userFriendModel.setSortLetters("#");
                                        }
                                    }else {
                                        userFriendModel.setSortLetters("#");
                                    }
                                    UserFriendModel.list_friends.add(userFriendModel);
                                }
                            }
                            Collections.sort(UserFriendModel.list_friends, new PinyinComparator());
                            listViewAdapter_atUser_atHouseEstate = new ListViewAdapter_AtUser_AtHouseEstate(UserFriendModel.list_friends, context,
                                    R.layout.layout_listview_item_at_user_at_houseestate);
                            listView_userFrinds.setAdapter(listViewAdapter_atUser_atHouseEstate);
                            loading_page.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "解析返回结果出错，请反馈！", Toast.LENGTH_SHORT).show();
                            loading_page.dismiss();
                        }
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

    private void getHouseId(final Context context, int houseEstateId, final int blockNum, final int unitNum, final int floorNum,final String roomNum){
        //Log.d("yellow_temp", "houseEstateId：" + houseEstateId + "---blockNum：" + blockNum + "---unitNum：" + unitNum + "---floorNum：" + floorNum + "---roomNum：" + roomNum);
        Map<String, String> params = new HashMap<>();
        params.put("VillageId", String.valueOf(houseEstateId));
        params.put("Block", String.valueOf(blockNum));
        params.put("Unit", String.valueOf(unitNum));
        params.put("Floor", String.valueOf(floorNum));
        params.put("Number", roomNum);
        HttpUtil.sendVolleyStringRequest_Post(context, HttpUtil.selectHouseEstateId_url, params, DBUtil.getLoginUser().getToken(), "yellow_selectHouseEstateId",
                new WanglinHttpResponseListener() {
                    @Override
                    public void onSuccessResponse(JSONObject jsonObject_response) {
                        try {
                            if (jsonObject_response.get("Data").toString().equals("null")){
                                Toast.makeText(context, "这个房间还没有网邻用户哦！", Toast.LENGTH_SHORT).show();
                            }else {
                                String currentChoosedAt = "";
                                if (blockNum != -1){
                                    currentChoosedAt += (blockNum + "栋");
                                }
                                if (unitNum != -1){
                                    currentChoosedAt += (unitNum + "单元");
                                }
                                currentChoosedAt += (floorNum + "楼");
                                currentChoosedAt += (roomNum + "号");
                                JSONObject jsonObject_data = jsonObject_response.getJSONObject("Data");
                                AtItemModel atItemModel = new AtItemModel();
                                atItemModel.setAtId(jsonObject_data.getInt("Id"));
                                atItemModel.setFromWhat(AT_UserActivity.FROM_WHAT_AT_NEIGHBOR);
                                atItemModel.setContent(currentChoosedAt);
                                AtItemModel.current_AtItem = atItemModel;
                                ((AT_UserActivity)context).setResult(Activity.RESULT_OK);
                                ((AT_UserActivity)context).finish();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(context, "解析返回结果出错，请反馈！", Toast.LENGTH_SHORT).show();
                            loading_page.dismiss();
                            e.printStackTrace();
                        }
                        loading_page.dismiss();
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
