package com.wanglinkeji.wanglin.util;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.SDKInitializer;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.wanglinkeji.wanglin.model.HousingEstateModel;
import com.wanglinkeji.wanglin.model.LocationInfoModel;
import com.wanglinkeji.wanglin.model.LocationWeatherModel;

/**
 * Created by Administrator on 2016/8/23.
 */
public class WangLinApplication extends Application {

    //吐槽最大字数
    public static final int BLOG_MAX_LENGTH = 800;

    //定位失败或者获取城市信息失败时默认的所在城市ID，和名字（默认为成都市）
    public static final int DEFAULT_CITY_ID = 2509;

    public static final String DEFAULT_CITY_NAME = "成都";

    /**
     * 手机屏幕宽高
     */
    public static int screen_Width;

    public static int screen_Height;

    /**
     * Volley Http请求队列
     */
    public static RequestQueue requestQueue;

    /**
     * 百度地图
     */
    public LocationService locationService;

    public Vibrator mVibrator;

    /**
     * 定位信息
     */
    public static LocationInfoModel locationInfoModel;

    /**
     * 天气信息
     */
    public static LocationWeatherModel locationWeatherModel;

    /**
     * 用户数据库
     */
    public static SQLiteDatabase user_db;

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化屏幕的宽高
        WindowManager windowManager =  (WindowManager)this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        screen_Height = outMetrics.heightPixels;
        screen_Width = outMetrics.widthPixels;

        //初始化ImageLoader
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(configuration);

        //初始化Volley Http请求队列
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        //初始化百度地图
        locationService = new LocationService(getApplicationContext());
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        SDKInitializer.initialize(getApplicationContext());

        //初始化用户数据库
        WangLinSQLiteOpenHelper userSQLiteOpenHelper = new WangLinSQLiteOpenHelper(this, "Wanglin.db", null, 1);
        user_db = userSQLiteOpenHelper.getWritableDatabase();

        //初始化定位Model
        locationInfoModel = new LocationInfoModel();

        //初始化天气Model
        locationWeatherModel = new LocationWeatherModel();
    }
}
