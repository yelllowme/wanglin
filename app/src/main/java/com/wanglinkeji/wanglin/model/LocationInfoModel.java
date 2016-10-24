package com.wanglinkeji.wanglin.model;

/**
 * Created by Administrator on 2016/8/29.
 * 定位所在地，位置Model
 */
public class LocationInfoModel {

    //定位城市的ID
    private int LocationCityId;

    //定位城市
    private String LocatonCity;

    //定位经度
    private double Longitude;

    //定位纬度
    private double Latitude;

    //定位具体位置
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getLocationCityId() {
        return LocationCityId;
    }

    public void setLocationCityId(int locationCityId) {
        LocationCityId = locationCityId;
    }

    public String getLocatonCity() {
        return LocatonCity;
    }

    public void setLocatonCity(String locatonCity) {
        LocatonCity = locatonCity;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

}
