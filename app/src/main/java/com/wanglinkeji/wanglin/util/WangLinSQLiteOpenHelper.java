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

    public WangLinSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_USER_INFO); //创建用户信息表
        Log.d("yellow_DataBaseInfo", "CreateUserInfoDB_Success");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
