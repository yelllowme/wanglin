package com.wanglinkeji.wanglin.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Administrator on 2016/9/5.
 * 用户数据库SQLiteOpenHelper
 */
public class WangLinSQLiteOpenHelper extends SQLiteOpenHelper {

    public static final String CREATE_USER_INFO = "create table user_info ("
            + "id integer primary key autoincrement,"
            + "phone text,"
            + "password text,"
            + "token text,"
            + "sign text,"
            + "islogin integer)";

    public static final String CREATE_CHAT_RECORD = "create table chat_record ("
            + "id integer primary key autoincrement,"
            + "friend_phone text,"
            + "real_date text,"
            + "is_show_date integer,"
            + "what_from integer,"
            + "message_type integer,"
            + "is_read integer,"
            + "my_text_content text,"
            + "friend_text_content text,"
            + "voice_length integer,"
            + "voice_read_state integer,"
            + "voice_local_path text,"
            + "voice_url text,"
            + "image_local_path text,"
            + "image_url text)";

    public static final String CREATE_CHAT_LIST = "create table chat_list ("
            + "id integer primary key autoincrement,"
            + "friend_phone text,"
            + "show_content text,"
            + "friend_show_name text,"
            + "new_get_date integer)";

    public WangLinSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_USER_INFO); //创建用户信息表
        sqLiteDatabase.execSQL(CREATE_CHAT_RECORD);//创建聊天记录表
        sqLiteDatabase.execSQL(CREATE_CHAT_LIST);//创建聊天列表
        Log.d("yellow_DataBaseInfo", "CreateUserInfoDB_Success");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
