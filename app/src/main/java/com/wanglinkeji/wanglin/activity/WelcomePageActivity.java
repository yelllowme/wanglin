package com.wanglinkeji.wanglin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.model.LocationInfoModel;
import com.wanglinkeji.wanglin.model.UserIdentityInfoModel;
import com.wanglinkeji.wanglin.util.DBUtil;
import com.wanglinkeji.wanglin.util.HttpUtil;
import com.wanglinkeji.wanglin.util.LocationService;
import com.wanglinkeji.wanglin.util.WangLinApplication;
import com.wanglinkeji.wanglin.util.WanglinHttpResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/25.
 * 欢迎页，加载，登录等作用
 */
public class WelcomePageActivity extends Activity {

    //百度地图定位服务
    private LocationService locationService;

    //当前定位的城市
    private String locationCity = "randomCity_yellowSignature";

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                //登录失败
                case 0:{
                    //登录失败，先要在本地执行注销操作
                    Toast.makeText(WelcomePageActivity.this, "自动登录失败，请手动登录！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(WelcomePageActivity.this, LoginActivity.class);
                    startActivity(intent);
                    WelcomePageActivity.this.finish();
                    break;
                }
                //跳转到主界面
                case 1:{
                    Intent intent = new Intent(WelcomePageActivity.this, MainActivity.class);
                    startActivity(intent);
                    WelcomePageActivity.this.finish();
                    break;
                }
                //跳转到登录界面
                case 2:{
                    Intent intent = new Intent(WelcomePageActivity.this, LoginActivity.class);
                    startActivity(intent);
                    WelcomePageActivity.this.finish();
                    break;
                }
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_activity_welcome_page);
        //隐藏时间栏（状态栏）
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(option);

        DBUtil.select_userInfoDB();//打印本地用户表
    }

    @Override
    protected void onStart() {
        super.onStart();
        //开启百度定位服务
        locationService = ((WangLinApplication)getApplication()).locationService;
        locationService.registerListener(mListener);
        locationService.start();
    }

    @Override
    protected void onStop() {
        //停止百度定位服务
        locationService.unregisterListener(mListener);
        locationService.stop();
        super.onStop();
    }

    private BDLocationListener mListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (null != bdLocation && bdLocation.getLocType() != BDLocation.TypeServerError) {
                if (!bdLocation.getCity().equals(locationCity)){
                    locationCity = bdLocation.getCity();
                    WangLinApplication.locationInfoModel = new LocationInfoModel();
                    WangLinApplication.locationInfoModel.setLocatonCity(locationCity);
                    WangLinApplication.locationInfoModel.setLongitude(bdLocation.getLongitude());
                    WangLinApplication.locationInfoModel.setLatitude(bdLocation.getLatitude());
                    WangLinApplication.locationInfoModel.setAddress(bdLocation.getAddress().address);
                    Log.d("yellow_locationInfo", "locationInfo--->" + locationCity + "：" + bdLocation.getAddress().address +"，Longitude--->" +
                            bdLocation.getLongitude() + "，Latitude--->" + bdLocation.getLatitude());
                    getWeatherInfo();
                }
            }
        }
    };

    private void getWeatherInfo(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://api.map.baidu.com/telematics/v3/weather?location=" + WangLinApplication.locationInfoModel.getLongitude() + ","
                 + WangLinApplication.locationInfoModel.getLatitude() + "&output=json&ak=" + HttpUtil.BAIDU_WEATHER_AK + "&qq-pf-to=pcqq.c2c",
                new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.d("yellow_weatherInfo", s);
                //解析返回数据
                try {
                    JSONObject jsonObject_response = new JSONObject(s);
                    int errorCode = jsonObject_response.getInt("error");
                    if (errorCode == 0){
                        JSONArray jsonArray_result = jsonObject_response.getJSONArray("results");
                        JSONObject jsonObject_temp = jsonArray_result.getJSONObject(0);
                        JSONArray jsonArray_weatherInfo = jsonObject_temp.getJSONArray("weather_data");
                        JSONObject jsonObject_todayWeatherInfo = jsonArray_weatherInfo.getJSONObject(0);
                        WangLinApplication.locationWeatherModel.setTemperature(jsonObject_todayWeatherInfo.getString("date"));//设置实时温度
                        WangLinApplication.locationWeatherModel.setWeather(jsonObject_todayWeatherInfo.getString("weather"));//设置天气（小雨，晴..）
                        WangLinApplication.locationWeatherModel.setDayPictureUrl(jsonObject_todayWeatherInfo.getString("dayPictureUrl"));//设置白天天气图片URL
                        WangLinApplication.locationWeatherModel.setNightPictureUrl(jsonObject_todayWeatherInfo.getString("nightPictureUrl"));//设置夜晚天气图片URL
                        WangLinApplication.locationWeatherModel.setTemperature_scope(jsonObject_todayWeatherInfo.getString("temperature"));//设置今天温度范围
                        WangLinApplication.locationWeatherModel.setWind(jsonObject_todayWeatherInfo.getString("wind"));//设置风向
                        //在请求天气接口之后请求所在城市ID（定位—>请求天气—>请求所在城市ID—>登录）
                        getCityId_ByCityName(WangLinApplication.locationInfoModel.getLocatonCity());
                    }else {
                        Toast.makeText(WelcomePageActivity.this, "获取天气信息失败，失败原因：" + jsonObject_response.getString("status") + jsonObject_response.getString("message"), Toast.LENGTH_SHORT).show();
                        //在请求天气接口之后请求所在城市ID（定位—>请求天气—>请求所在城市ID—>登录）
                        getCityId_ByCityName(WangLinApplication.locationInfoModel.getLocatonCity());
                    }
                } catch (JSONException e) {
                    Toast.makeText(WelcomePageActivity.this, "获取天气信息失败，失败原因：解析返回结果出错", Toast.LENGTH_SHORT).show();
                    //在请求天气接口之后请求所在城市ID（定位—>请求天气—>请求所在城市ID—>登录）
                    getCityId_ByCityName(WangLinApplication.locationInfoModel.getLocatonCity());
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(WelcomePageActivity.this, HttpUtil.parseVolleyError(volleyError), Toast.LENGTH_SHORT).show();
                //在请求天气接口之后请求所在城市ID（定位—>请求天气—>请求所在城市ID—>登录）
                getCityId_ByCityName(WangLinApplication.locationInfoModel.getLocatonCity());
            }
        }){
            //这里重写这个方法是对返回数据进行"utf-8"重新编码
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String str = null;
                try {
                    str = new String(response.data,"utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return Response.success(str, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        WangLinApplication.requestQueue.add(stringRequest);
    }

    //登录获取token生成Sign
    private void login(final String phone, final String password){
        Map<String, String> params = new HashMap<>();
        params.put("UserName", phone);
        params.put("UserPassword", password);
        HttpUtil.sendVolleyStringRequest_Post(WelcomePageActivity.this, HttpUtil.login_url, params, "", "yellow_login_response_WelcomePageActivity",
                new WanglinHttpResponseListener() {
                    @Override
                    public void onSuccessResponse(JSONObject jsonObject_response) {
                        try {
                            JSONObject jsonObject_data = jsonObject_response.getJSONObject("Data");
                            String token = jsonObject_data.getString("Token");
                            UserIdentityInfoModel.userName = jsonObject_data.getString("UserRealName");//获取用户昵称
                            //这里先执行本地登录操作（这里既是保存token和sign的值），再执行跳转操作
                            DBUtil.login(phone, token, password, HttpUtil.getSign(token));

                            //这里留1.5s的加载时间给广告页，再执行跳转操作
                            handler.sendEmptyMessageDelayed(1, 0);
                        } catch (JSONException e) {
                            handler.sendEmptyMessage(0);
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onConnectingError() {
                        handler.sendEmptyMessage(0);
                    }

                    @Override
                    public void onDisconnectError() {
                        handler.sendEmptyMessage(0);
                    }
                });
    }

    private void getCityId_ByCityName(final String cityName){
        Map<String, String> params = new HashMap<>();
        params.put("Name", cityName);
        HttpUtil.sendVolleyStringRequest_Post(WelcomePageActivity.this, HttpUtil.getCityId_ByCityName_url, params, "", "yellow_getCityIdByCityName",
                new WanglinHttpResponseListener() {
                    @Override
                    public void onSuccessResponse(JSONObject jsonObject_response) {
                        try {
                            JSONObject jsonObject_data = jsonObject_response.getJSONObject("Data");
                            WangLinApplication.locationInfoModel.setLocationCityId(jsonObject_data.getInt("RegionId")); //设置定位城市ID
                            //获取城市ID后判断是否有登录用户（定位—>请求天气—>请求所在城市ID—>登录）
                            if (DBUtil.hasUserLogin() == true){
                                login(DBUtil.getLoginUser().getPhone(), DBUtil.getLoginUser().getPassword());
                            }else {
                                //这里留1.5s的加载时间给广告页，再执行跳转操作
                                handler.sendEmptyMessageDelayed(2, 1500);
                            }
                        } catch (JSONException e) {
                            setFailedGetCityId();
                            //获取城市ID后判断是否有登录用户（定位—>请求天气—>请求所在城市ID—>登录）
                            if (DBUtil.hasUserLogin() == true){
                                login(DBUtil.getLoginUser().getPhone(), DBUtil.getLoginUser().getPassword());
                            }else {
                                //这里留1.5s的加载时间给广告页，再执行跳转操作
                                handler.sendEmptyMessageDelayed(2, 1500);
                            }
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onConnectingError() {
                        setFailedGetCityId();
                        //获取城市ID后判断是否有登录用户（定位—>请求天气—>请求所在城市ID—>登录）
                        if (DBUtil.hasUserLogin() == true){
                            login(DBUtil.getLoginUser().getPhone(), DBUtil.getLoginUser().getPassword());
                        }else {
                            //这里留1.5s的加载时间给广告页，再执行跳转操作
                            handler.sendEmptyMessageDelayed(2, 500);
                        }
                    }

                    @Override
                    public void onDisconnectError() {
                        setFailedGetCityId();
                        //获取城市ID后判断是否有登录用户（定位—>请求天气—>请求所在城市ID—>登录）
                        if (DBUtil.hasUserLogin() == true){
                            login(DBUtil.getLoginUser().getPhone(), DBUtil.getLoginUser().getPassword());
                        }else {
                            //这里留1.5s的加载时间给广告页，再执行跳转操作
                            handler.sendEmptyMessageDelayed(2, 1500);
                        }
                    }
                });
    }

    //根据城市名获取城市ID方法失败时调用方法
    private void setFailedGetCityId(){
        WangLinApplication.locationInfoModel.setLocationCityId(WangLinApplication.DEFAULT_CITY_ID);
        WangLinApplication.locationInfoModel.setLocatonCity(WangLinApplication.DEFAULT_CITY_NAME);
    }
}
