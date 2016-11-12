package com.wanglinkeji.wanglin.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.wanglinkeji.wanglin.model.ChatItemMoeld;
import com.wanglinkeji.wanglin.model.ChatListItemModel;
import com.wanglinkeji.wanglin.model.UserFriendModel;
import com.wanglinkeji.wanglin.model.UserModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/9/5.
 * 本地数据库工具Util
 */
public class DBUtil {

    /**
     * 是否登录状态：0—未登录， 1—已登录
     */
    public static int LOGIN_STATE_LOGOUT = 0;

    public static int LOGIN_STATE_LOGIN = 1;

    /**
     * 聊天记录分页，每页大小
     */
    public static int CHAT_PAGE_SIZE = 25;

    //判断是否有用户在线
    public static boolean hasUserLogin(){
        Cursor cursor = WangLinApplication.user_db.query("user_info", null, "islogin=?" ,
                new String[]{String.valueOf(LOGIN_STATE_LOGIN)}, null, null, null);
        if (cursor.moveToNext()){
            return true;
        }
        return false;
    }

    //添加用户
    public static void addUser(String phone, String password, String token, String sign,int isLogin){
        ContentValues contentValues = new ContentValues();
        contentValues.put("phone",phone);
        contentValues.put("password", password);
        contentValues.put("token", token);
        contentValues.put("sign", sign);
        contentValues.put("islogin", isLogin);
        WangLinApplication.user_db.insert("user_info", null, contentValues);
    }

    //获取登录用户
    public static UserModel getLoginUser(){
        Cursor cursor = WangLinApplication.user_db.query("user_info", null, "islogin=?" ,
                new String[]{String.valueOf(LOGIN_STATE_LOGIN)}, null, null, null);
        if (cursor.moveToNext()){
            UserModel user = new UserModel();
            user.setId(cursor.getInt(cursor.getColumnIndex("id")));
            user.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
            user.setPassword(cursor.getString(cursor.getColumnIndex("password")));
            user.setToken(cursor.getString(cursor.getColumnIndex("token")));
            user.setIsLogin(cursor.getInt(cursor.getColumnIndex("islogin")));
            user.setSign(cursor.getString(cursor.getColumnIndex("sign")));
            cursor.close();
            return user;
        }
        return null;
    }

    //注销
    public static void logout(){
        Cursor cursor = WangLinApplication.user_db.query("user_info", null, "islogin=?" ,
                new String[]{String.valueOf(LOGIN_STATE_LOGIN)}, null, null, null);
        while (cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            ContentValues contentValues = new ContentValues();
            contentValues.put("islogin", LOGIN_STATE_LOGOUT);
            WangLinApplication.user_db.update("user_info", contentValues, "id=?", new String[]{String.valueOf(id)});
        }
        cursor.close();
    }

    //判断本地是否已经保存此用户
    public static boolean hasThisUser(String phone){
        Cursor cursor = WangLinApplication.user_db.query("user_info", null, "phone=?", new String[]{phone}, null, null, null);
        if (cursor.moveToNext()){
            cursor.close();
            return true;
        }
        return false;
    }

    //本地登录
    public static void login(String phone, String token,String password, String sign){
        Cursor cursor = WangLinApplication.user_db.query("user_info", null, "phone=?", new String[]{phone}, null, null, null);
        if (cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            ContentValues contentValues = new ContentValues();
            contentValues.put("token", token);
            contentValues.put("password", password);
            contentValues.put("sign", sign);
            contentValues.put("islogin", LOGIN_STATE_LOGIN);
            WangLinApplication.user_db.update("user_info", contentValues, "id=?", new String[]{String.valueOf(id)});
            cursor.close();
        }
    }

    //Log打印本地数据库(User表)信息`1
    public static void select_userInfoDB(){
        Cursor cursor = WangLinApplication.user_db.query("user_info", null, null, null , null, null, null);
        Log.d("yellow_user_infoDB", "---------------------------------select_userInfoDB_start------------------------------------------");
        while (cursor.moveToNext()){
            Log.d("yellow_user_infoDB", "---------------------------------row_start------------------------------------------");
            Log.d("yellow_user_infoDB", "id--->" + String.valueOf(cursor.getInt(cursor.getColumnIndex("id"))));
            Log.d("yellow_user_infoDB", "phone--->" + cursor.getString(cursor.getColumnIndex("phone")));
            Log.d("yellow_user_infoDB", "password--->" + cursor.getString(cursor.getColumnIndex("password")));
            Log.d("yellow_user_infoDB", "token--->" + cursor.getString(cursor.getColumnIndex("token")));
            Log.d("yellow_user_infoDB", "sign--->" + String.valueOf(cursor.getInt(cursor.getColumnIndex("sign"))));
            Log.d("yellow_user_infoDB", "islogin--->" + String.valueOf(cursor.getInt(cursor.getColumnIndex("islogin"))));
            Log.d("yellow_user_infoDB", "---------------------------------row_end------------------------------------------");
        }
        cursor.close();
        Log.d("yellow_user_infoDB", "---------------------------------select_userInfoDB_end------------------------------------------");
    }

    //聊天记录表分页查询
    public static List<ChatItemMoeld> getChatRecord_limit(int pageSize, int pageNum, String friendPhone){
        List<ChatItemMoeld> list_chat = new ArrayList<>();
        int offset = (pageNum - 1) * pageSize;
        String sql = "select * from chat_record where friend_phone=" + friendPhone + " order by id desc" + " limit " + pageSize + " offset " + offset;
        Cursor cursor = WangLinApplication.user_db.rawQuery(sql, null);
        while (cursor.moveToNext()){
            ChatItemMoeld chatItemMoeld = new ChatItemMoeld();
            chatItemMoeld.setLocalDB_id(cursor.getInt(cursor.getColumnIndex("id")));
            chatItemMoeld.setFriendPhone(cursor.getString(cursor.getColumnIndex("friend_phone")));
            if (cursor.getInt(cursor.getColumnIndex("is_show_date")) == ChatItemMoeld.IS_SHOW_DATE_HAS_SHOW){
                chatItemMoeld.setShowDate(true);
            }else if (cursor.getInt(cursor.getColumnIndex("is_show_date")) == ChatItemMoeld.IS_SHOW_DATE_NOT_SHOW){
                chatItemMoeld.setShowDate(false);
            }
            chatItemMoeld.setRealDate(new Date(Long.valueOf(cursor.getString(cursor.getColumnIndex("real_date")))));
            chatItemMoeld.setMessageFrom(cursor.getInt(cursor.getColumnIndex("what_from")));
            chatItemMoeld.setMessageType(cursor.getInt(cursor.getColumnIndex("message_type")));
            chatItemMoeld.setIsRead(cursor.getInt(cursor.getColumnIndex("is_read")));
            chatItemMoeld.setMyContent(cursor.getString(cursor.getColumnIndex("my_text_content")));
            chatItemMoeld.setFriendContent(cursor.getString(cursor.getColumnIndex("friend_text_content")));
            chatItemMoeld.setVoiceLength(cursor.getInt(cursor.getColumnIndex("voice_length")));
            chatItemMoeld.setLocalVoicePath(cursor.getString(cursor.getColumnIndex("voice_local_path")));
            chatItemMoeld.setVoiceReadState(cursor.getInt(cursor.getColumnIndex("voice_read_state")));
            chatItemMoeld.setVoiceUrl(cursor.getString(cursor.getColumnIndex("voice_url")));
            chatItemMoeld.setImageLocalPath(cursor.getString(cursor.getColumnIndex("image_local_path")));
            chatItemMoeld.setImageUrl(cursor.getString(cursor.getColumnIndex("image_url")));
            list_chat.add(chatItemMoeld);
        }
        Collections.reverse(list_chat);
        return list_chat;
    }

    //向聊天记录表中插入一条聊天记录
    public static void insertChatMessage(ChatItemMoeld chatItemMoeld, int from, int messageType){
        ContentValues contentValues = new ContentValues();
        contentValues.put("friend_phone", chatItemMoeld.getFriendPhone());
        contentValues.put("real_date", String.valueOf(chatItemMoeld.getRealDate().getTime()));
        if (chatItemMoeld.isShowDate() == true){
            contentValues.put("is_show_date", ChatItemMoeld.IS_SHOW_DATE_HAS_SHOW);
        }else {
            contentValues.put("is_show_date", ChatItemMoeld.IS_SHOW_DATE_NOT_SHOW);
        }
        contentValues.put("what_from", chatItemMoeld.getMessageFrom());
        contentValues.put("message_type", chatItemMoeld.getMessageType());
        contentValues.put("is_read", chatItemMoeld.getIsRead());
        if (messageType == ChatItemMoeld.MESSAGE_TYPE_TEXT){
            if (from == ChatItemMoeld.MESSAGE_FROM_FRIEND){
                contentValues.put("my_text_content", "");
                contentValues.put("friend_text_content", chatItemMoeld.getFriendContent());
                contentValues.put("voice_length", 0);
                contentValues.put("voice_read_state", ChatItemMoeld.VOICE_READ_STATE_NOT_READ);
                contentValues.put("voice_local_path", "");
                contentValues.put("voice_url", "");
                contentValues.put("image_local_path", "");
                contentValues.put("image_url", "");
            }else if (from == ChatItemMoeld.MESSAGE_FROM_ME){
                contentValues.put("my_text_content", chatItemMoeld.getMyContent());
                contentValues.put("friend_text_content", "");
                contentValues.put("voice_length", 0);
                contentValues.put("voice_read_state", ChatItemMoeld.VOICE_READ_STATE_NOT_READ);
                contentValues.put("voice_local_path", "");
                contentValues.put("voice_url", "");
                contentValues.put("image_local_path", "");
                contentValues.put("image_url", "");
            }
        }else if (messageType == ChatItemMoeld.MESSAGE_TYPE_VOICE){
            if (from == ChatItemMoeld.MESSAGE_FROM_FRIEND){
                contentValues.put("my_text_content", "");
                contentValues.put("friend_text_content", "");
                contentValues.put("voice_length", chatItemMoeld.getVoiceLength());
                contentValues.put("voice_read_state", chatItemMoeld.getVoiceReadState());
                contentValues.put("voice_local_path", "");
                contentValues.put("voice_url", chatItemMoeld.getVoiceUrl());
                contentValues.put("image_local_path", "");
                contentValues.put("image_url", "");
            }else if (from == ChatItemMoeld.MESSAGE_FROM_ME){
                contentValues.put("my_text_content", "");
                contentValues.put("friend_text_content", "");
                contentValues.put("voice_length", chatItemMoeld.getVoiceLength());
                contentValues.put("voice_read_state", ChatItemMoeld.VOICE_READ_STATE_HAS_READ);
                contentValues.put("voice_local_path", chatItemMoeld.getLocalVoicePath());
                contentValues.put("voice_url", chatItemMoeld.getVoiceUrl());
                contentValues.put("image_local_path", "");
                contentValues.put("image_url", "");
            }
        }else if (messageType == ChatItemMoeld.MESSAGE_TYPE_IMAGE){
            if (from == ChatItemMoeld.MESSAGE_FROM_FRIEND){
                contentValues.put("my_text_content", "");
                contentValues.put("friend_text_content", "");
                contentValues.put("voice_length", 0);
                contentValues.put("voice_read_state", ChatItemMoeld.VOICE_READ_STATE_NOT_READ);
                contentValues.put("voice_local_path", "");
                contentValues.put("voice_url", "");
                contentValues.put("image_local_path", "");
                contentValues.put("image_url", chatItemMoeld.getImageUrl());
            }else if (from == ChatItemMoeld.MESSAGE_FROM_ME){
                contentValues.put("my_text_content", chatItemMoeld.getMyContent());
                contentValues.put("friend_text_content", "");
                contentValues.put("voice_length", 0);
                contentValues.put("voice_read_state", ChatItemMoeld.VOICE_READ_STATE_NOT_READ);
                contentValues.put("voice_local_path", "");
                contentValues.put("voice_url", "");
                contentValues.put("image_local_path", chatItemMoeld.getImageLocalPath());
                contentValues.put("image_url", chatItemMoeld.getImageUrl());
            }
        }
        WangLinApplication.user_db.insert("chat_record", null, contentValues);
    }

    //设置语音消息已读
    public static void readVoice(String voiceUrl){
        ContentValues contentValues = new ContentValues();
        contentValues.put("voice_read_state", ChatItemMoeld.VOICE_READ_STATE_HAS_READ);
        WangLinApplication.user_db.update("chat_record", contentValues, "voice_url=?", new String[]{voiceUrl});
    }

    //设置所有消息已读
    public static void readAllMessage(String friendPhone){
        String sql = "select * from chat_record where friend_phone=" + friendPhone + " order by id desc";
        Cursor cursor = WangLinApplication.user_db.rawQuery(sql, null);
        while (cursor.moveToNext()){
            if (cursor.getInt(cursor.getColumnIndex("is_read")) == ChatItemMoeld.MESSAGE_READ_STATE_NOT_READ){
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                ContentValues contentValues = new ContentValues();
                contentValues.put("is_read", ChatItemMoeld.VOICE_READ_STATE_HAS_READ);
                WangLinApplication.user_db.update("chat_record", contentValues, "id=?", new String[]{String.valueOf(id)});
            }else if(cursor.getInt(cursor.getColumnIndex("is_read")) == ChatItemMoeld.MESSAGE_READ_STATE_HAS_READ) {
                return;
            }
        }
    }

    //聊天列表中是否有此好友Item,有返回Item的ID没有则返回-1
    public static int hasThisChatListItem(String friendPhone){
        int id = -1;
        String sql = "select * from chat_list where friend_phone=" + friendPhone;
        Cursor cursor = WangLinApplication.user_db.rawQuery(sql, null);
        if (cursor.moveToNext()){
            id = cursor.getInt(cursor.getColumnIndex("id"));
        }
        return id;
    }

    //向聊天列表中插入一条数据或者修改已有数据
    public static void insertChatListItem(ChatItemMoeld chatItemMoeld){
        int id = hasThisChatListItem(chatItemMoeld.getFriendPhone());
        if (id == -1){
            ContentValues contentValues = new ContentValues();
            contentValues.put("friend_phone", chatItemMoeld.getFriendPhone());
            if (chatItemMoeld.getMessageType() == ChatItemMoeld.MESSAGE_TYPE_TEXT){
                if (chatItemMoeld.getMessageFrom() == ChatItemMoeld.MESSAGE_FROM_ME){
                    contentValues.put("show_content", chatItemMoeld.getMyContent());
                }else if (chatItemMoeld.getMessageFrom() == ChatItemMoeld.MESSAGE_FROM_FRIEND){
                    contentValues.put("show_content", chatItemMoeld.getFriendContent());
                }
            }else if (chatItemMoeld.getMessageType() == ChatItemMoeld.MESSAGE_TYPE_VOICE){
                contentValues.put("show_content", "[语音]");
            }else if (chatItemMoeld.getMessageType() == ChatItemMoeld.MESSAGE_TYPE_IMAGE){
                contentValues.put("show_content", "[图片]");
            }
            contentValues.put("friend_show_name", chatItemMoeld.getFriendNickName());
            contentValues.put("new_get_date", (new Long(chatItemMoeld.getRealDate().getTime())).intValue());
            WangLinApplication.user_db.insert("chat_list", null, contentValues);
        }else {
            ContentValues contentValues = new ContentValues();
            if (chatItemMoeld.getMessageType() == ChatItemMoeld.MESSAGE_TYPE_TEXT){
                if (chatItemMoeld.getMessageFrom() == ChatItemMoeld.MESSAGE_FROM_ME){
                    contentValues.put("show_content", chatItemMoeld.getMyContent());
                }else if (chatItemMoeld.getMessageFrom() == ChatItemMoeld.MESSAGE_FROM_FRIEND){
                    contentValues.put("show_content", chatItemMoeld.getFriendContent());
                }
            }else if (chatItemMoeld.getMessageType() == ChatItemMoeld.MESSAGE_TYPE_VOICE){
                contentValues.put("show_content", "[语音]");
            }else if (chatItemMoeld.getMessageType() == ChatItemMoeld.MESSAGE_TYPE_IMAGE){
                contentValues.put("show_content", "[图片]");
            }
            WangLinApplication.user_db.update("chat_list", contentValues, "id=?", new String[]{String.valueOf(id)});
        }
    }

    //获取所有未读的聊天消息
    public static int getAll_NumOfNotReadMessage(){
        int num = 0;
        for (int i = 0; i < ChatListItemModel.list_chatList.size(); i++){
            num += ChatListItemModel.list_chatList.get(i).getNotReadMessage_count();
        }
        return num;
    }

    //根据好友电话号码获取未读消息数
    public static int getNumNotReadMessage(String friendPhone){
        int num = 0;
        String sql = "select * from chat_record where friend_phone=" + friendPhone + " order by id desc";
        Cursor cursor = WangLinApplication.user_db.rawQuery(sql, null);
        while (cursor.moveToNext()){
            if (cursor.getInt(cursor.getColumnIndex("is_read")) == ChatItemMoeld.MESSAGE_READ_STATE_NOT_READ){
                num++;
            }else if(cursor.getInt(cursor.getColumnIndex("is_read")) == ChatItemMoeld.MESSAGE_READ_STATE_HAS_READ){
                return num;
            }
        }
        return num;
    }

    //根据好友电话号码获取好友昵称
    public static String getFriendNickNameByPhone(String phone){
        String nickName = "好友名";
        for (int i = 0; i < UserFriendModel.list_friends.size(); i++){
            if (UserFriendModel.list_friends.get(i).getPhone().equals(phone)){
                return UserFriendModel.list_friends.get(i).getName();
            }
        }
        return nickName;
    }

    //获取聊天列表
    public static List<ChatListItemModel> getChatList(){
        String sql = "select * from chat_list order by new_get_date desc";
        Cursor cursor = WangLinApplication.user_db.rawQuery(sql, null);
        List<ChatListItemModel> list_chatList = new ArrayList<>();
        while (cursor.moveToNext()){
            ChatListItemModel chatListItemModel = new ChatListItemModel();
            chatListItemModel.setId(cursor.getInt(cursor.getColumnIndex("id")));
            chatListItemModel.setFriendPhone(cursor.getString(cursor.getColumnIndex("friend_phone")));
            chatListItemModel.setFriendName(getFriendNickNameByPhone(cursor.getString(cursor.getColumnIndex("friend_phone"))));
            chatListItemModel.setLastTalk(cursor.getString(cursor.getColumnIndex("show_content")));
            chatListItemModel.setNotReadMessage_count(getNumNotReadMessage(cursor.getString(cursor.getColumnIndex("friend_phone"))));
            list_chatList.add(chatListItemModel);
        }
        return list_chatList;
    }

}
