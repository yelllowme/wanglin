package com.wanglinkeji.wanglin.model;

import android.util.Log;

/**
 * Created by Administrator on 2016/8/29.
 * 定位所在地天气Model
 */
public class LocationWeatherModel {

    private String temperature;

    private String dayPictureUrl;

    private String nightPictureUrl;

    private String weather;

    private String wind;

    private String temperature_scope;

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        String[] temp = temperature.split("：");
        String[] temp_1 = temp[1].split("℃");
        this.temperature = temp_1[0] + "°";
    }

    public String getDayPictureUrl() {
        return dayPictureUrl;
    }

    public void setDayPictureUrl(String dayPictureUrl) {
        this.dayPictureUrl = dayPictureUrl;
    }

    public String getNightPictureUrl() {
        return nightPictureUrl;
    }

    public void setNightPictureUrl(String nightPictureUrl) {
        this.nightPictureUrl = nightPictureUrl;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public String getTemperature_scope() {
        return temperature_scope;
    }

    public void setTemperature_scope(String temperature_scope) {
        this.temperature_scope = temperature_scope;
    }
}
