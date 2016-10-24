package com.wanglinkeji.wanglin.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.activity.AboutMe_NeighborFragment_Activity;
import com.wanglinkeji.wanglin.activity.AddFriendAcivity;
import com.wanglinkeji.wanglin.activity.ChatActivity;
import com.wanglinkeji.wanglin.activity.ChatGroupActivity;
import com.wanglinkeji.wanglin.activity.ChatWithOneFriendActivity;
import com.wanglinkeji.wanglin.activity.FriendBlogActivity;
import com.wanglinkeji.wanglin.activity.NewFriendActivity;
import com.wanglinkeji.wanglin.adapter.ListViewAdapter_AtUser_AtHouseEstate;
import com.wanglinkeji.wanglin.adapter.ListViewAdapter_AtUser_AtHouseEstate_SearchList;
import com.wanglinkeji.wanglin.adapter.ListViewAdapter_NeighborFragment_ChatList;
import com.wanglinkeji.wanglin.customerview.MyListView;
import com.wanglinkeji.wanglin.customerview.SideBar;
import com.wanglinkeji.wanglin.model.ChatListItemModel;
import com.wanglinkeji.wanglin.model.UserFriendModel;
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
 * Created by Administrator on 2016/8/16.
 * 首页->邻居Fragment
 */
public class NeighborFragment extends Fragment implements View.OnClickListener {

    private View view;

    private EditText editText_search;

    private ImageView imageView_title_addFriend;

    private TextView textView_title_message, textView_title_address_list;

    private ScrollView scrollView_message;

    private LinearLayout layout_addressBook, layout_aboutMe, layout_friendBlog, layout_newFriend, layout_chatGroup;

    private SideBar sideBar_addressList;

    private MyListView listView_chatList;

    private ListView listView_friendList, listView_searchFriendList;

    private ListViewAdapter_AtUser_AtHouseEstate listViewAdapter_atUser_atHouseEstate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_fragment_neighbor, container, false);

        viewInit();
        getChatList();
        return view;
    }

    @Override
    public void onResume() {
        getUserFriendList(getActivity());
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //标题，消息按钮
            case R.id.textview_fragment_neighbor_title_message:{
                setView_choosedMessage();
                break;
            }
            //标题，通讯录按钮
            case R.id.textview_fragment_neighbor_title_addressList:{
                setView_choosedAdressList();
                break;
            }
            case R.id.layout_framgent_neighbor_aboutMe:{
                Intent intent = new Intent(getActivity(), AboutMe_NeighborFragment_Activity.class);
                startActivity(intent);
                break;
            }
            case R.id.layout_framgent_neighbor_friendBlog:{
                Intent intent = new Intent(getActivity(), FriendBlogActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.layout_framgent_neighbor_newFriend:{
                Intent intent = new Intent(getActivity(), NewFriendActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.layout_framgent_neighbor_chatGroup:{
                Intent intent = new Intent(getActivity(), ChatGroupActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.imageview_fragment_neighbor_title_addFriend:{
                Intent intent = new Intent(getActivity(), AddFriendAcivity.class);
                startActivity(intent);
                break;
            }
            default:
                break;
        }
    }

    private void viewInit(){
        imageView_title_addFriend = (ImageView)view.findViewById(R.id.imageview_fragment_neighbor_title_addFriend);
        imageView_title_addFriend.setOnClickListener(this);
        layout_aboutMe = (LinearLayout)view.findViewById(R.id.layout_framgent_neighbor_aboutMe);
        layout_aboutMe.setOnClickListener(this);
        layout_friendBlog = (LinearLayout)view.findViewById(R.id.layout_framgent_neighbor_friendBlog);
        layout_friendBlog.setOnClickListener(this);
        layout_newFriend = (LinearLayout)view.findViewById(R.id.layout_framgent_neighbor_newFriend);
        layout_newFriend.setOnClickListener(this);
        layout_chatGroup = (LinearLayout)view.findViewById(R.id.layout_framgent_neighbor_chatGroup);
        layout_chatGroup.setOnClickListener(this);
        textView_title_message = (TextView)view.findViewById(R.id.textview_fragment_neighbor_title_message);
        textView_title_message.setOnClickListener(this);
        textView_title_address_list = (TextView)view.findViewById(R.id.textview_fragment_neighbor_title_addressList);
        textView_title_address_list.setOnClickListener(this);
        scrollView_message = (ScrollView)view.findViewById(R.id.scrollView_framgent_neighbor_message);
        layout_addressBook = (LinearLayout) view.findViewById(R.id.layout_framgent_neighbor_addressBook);
        sideBar_addressList = (SideBar)view.findViewById(R.id.sideBar_fragment_neighbor_address_list);
        listView_chatList = (MyListView)view.findViewById(R.id.listview_framgent_neighbor_chatList);
        listView_friendList = (ListView)view.findViewById(R.id.listview_framgent_neighbor_friendList);
        listView_friendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                UserFriendModel.chatPosition = (new Long(l)).intValue();
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                startActivity(intent);
            }
        });
        listView_searchFriendList = (ListView)view.findViewById(R.id.listview_framgent_neighbor_searchList);
        editText_search = (EditText)view.findViewById(R.id.edittext_framgent_neighbor_search);
        editText_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if (editText_search.getText().length() == 0){
                    listView_searchFriendList.setVisibility(View.GONE);
                }else {
                    listView_searchFriendList.setVisibility(View.VISIBLE);
                    List<UserFriendModel> list_friends = new ArrayList<UserFriendModel>();
                    for (int i = 0; i < UserFriendModel.list_friends.size(); i++){
                        if (UserFriendModel.list_friends.get(i).getName().contains(editText_search.getText().toString())){
                            list_friends.add(UserFriendModel.list_friends.get(i));
                        }
                    }
                    listView_searchFriendList.setAdapter(new ListViewAdapter_AtUser_AtHouseEstate_SearchList(list_friends, getActivity(),
                            R.layout.layout_listview_item_at_user_at_houseestate));
                }
            }
        });
        sideBar_addressList.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = listViewAdapter_atUser_atHouseEstate.getPositionForSection(s.charAt(0));
                if(position != -1){
                    listView_friendList.setSelection(position);
                }
            }
        });
        listView_chatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                /*Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("chatName", ChatListItemModel.list_chatList.get((new Long(l).intValue())).getFriendName());
                startActivity(intent);*/
            }
        });
    }

    private void setView_choosedMessage(){
        textView_title_message.setTextColor(0XFFFFFFFF);
        textView_title_address_list.setTextColor(0XFF7C7D81);
        scrollView_message.setVisibility(View.VISIBLE);
        layout_addressBook.setVisibility(View.GONE);
        sideBar_addressList.setVisibility(View.GONE);
    }

    private void setView_choosedAdressList(){
        textView_title_message.setTextColor(0XFF7C7D81);
        textView_title_address_list.setTextColor(0XFFFFFFFF);
        scrollView_message.setVisibility(View.GONE);
        layout_addressBook.setVisibility(View.VISIBLE);
        sideBar_addressList.setVisibility(View.VISIBLE);
    }

    private void getChatList(){
        ChatListItemModel.list_chatList = new ArrayList<>();

        ChatListItemModel chatListItemModel = new ChatListItemModel();
        chatListItemModel.setHeaderUrl("http://tupian.qqjay.com/tou3/2016/0726/8529f425cf23fd5afaa376c166b58e29.jpg");
        chatListItemModel.setLastTalk("我良辰有一百种方法...");
        chatListItemModel.setFriendName("叶良辰");

        ChatListItemModel chatListItemModel1 = new ChatListItemModel();
        chatListItemModel1.setHeaderUrl("http://img2.imgtn.bdimg.com/it/u=3880564472,3811979152&fm=21&gp=0.jpg");
        chatListItemModel1.setLastTalk("我是全英雄联盟最骚的骚猪");
        chatListItemModel1.setFriendName("PDD");

        ChatListItemModel chatListItemModel2 = new ChatListItemModel();
        chatListItemModel2.setHeaderUrl("http://img2.imgtn.bdimg.com/it/u=1133364395,3970760305&fm=21&gp=0.jpg");
        chatListItemModel2.setLastTalk("PDD真的喜欢乱JB黑我");
        chatListItemModel2.setFriendName("笑笑");

        ChatListItemModel.list_chatList.add(chatListItemModel);
        ChatListItemModel.list_chatList.add(chatListItemModel1);
        ChatListItemModel.list_chatList.add(chatListItemModel2);

        listView_chatList.setAdapter(new ListViewAdapter_NeighborFragment_ChatList(ChatListItemModel.list_chatList, getActivity(),
                R.layout.layout_listview_item_neighbor_fragment_chatlist));
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
                                    userFriendModel.setPhone(jsonObject_friend.getString("UserName"));//设置好友电话
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
                            listView_friendList.setAdapter(listViewAdapter_atUser_atHouseEstate);
                        } catch (JSONException e) {
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
