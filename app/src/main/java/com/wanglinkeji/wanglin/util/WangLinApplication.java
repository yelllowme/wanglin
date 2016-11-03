package com.wanglinkeji.wanglin.util;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.SDKInitializer;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.activity.MainActivity;
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

    /**
     * 通知 builder Manager
     */
    public static NotificationCompat.Builder notificationBuilder;

    public static NotificationManager notificationManager;

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

        //初始化通知builder,Manager
        //在顶部常驻:Notification.FLAG_ONGOING_EVENT
        //点击去除： Notification.FLAG_AUTO_CANCEL
        PendingIntent pendingIntent= PendingIntent.getActivity(this, 1, new Intent(), Notification.FLAG_AUTO_CANCEL);
        notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setContentTitle("网邻")
                .setContentText("您有新的消息")
                .setContentIntent(pendingIntent)
                //.setNumber(number)//显示数量
                .setTicker("测试通知来啦")//通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示
                .setPriority(Notification.PRIORITY_DEFAULT)//设置该通知优先级
                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
                //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(R.mipmap.ic_launcher);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }
}
