package com.wanglinkeji.wanglin.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.wanglinkeji.wanglin.model.ChatItemMoeld;
import com.wanglinkeji.wanglin.model.UserModel;

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

    //Log打印本地数据库(User表)信息
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

    //向聊天记录表中插入一条聊天记录
    public static void insertChatMessage(ChatItemMoeld chatItemMoeld){

    }

}
