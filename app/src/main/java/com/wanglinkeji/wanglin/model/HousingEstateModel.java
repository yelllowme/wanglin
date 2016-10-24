package com.wanglinkeji.wanglin.model;

import java.util.List;

/**
 * Created by Administrator on 2016/8/31.
 * 小区Model
 */
public class HousingEstateModel {

    public static List<HousingEstateModel> list_housingEstate_neighbor;

    //小区Id
    private int Id;

    //小区名
    private String name;

    //小区口号
    private String slogan;

    //所在省份
    private String province;

    //所在城市
    private String city;

    //所在区域
    private String region;

    //小区logoURL
    private String logoURL;

    //小区地址
    private String address;

    //小区开发商
    private String developers;

    //小区位置经度
    private double lng;

    //小区位置纬度
    private double lat;

    //所在区域ID
    private int regionId;

    //所在城市ID
    private int cityId;

    //用户在所在小区拥有的住房数量
    private int houseNum;

    //是否是默认小区
    private boolean isDefault;

    //所在小区拥有的住房List
    private List<UserHouseModel> list_house;

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public int getHouseNum() {
        return houseNum;
    }

    public void setHouseNum(int houseNum) {
        this.houseNum = houseNum;
    }

    public List<UserHouseModel> getList_house() {
        return list_house;
    }

    public void setList_house(List<UserHouseModel> list_house) {
        this.list_house = list_house;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getLogoURL() {
        return logoURL;
    }

    public void setLogoURL(String logoURL) {
        this.logoURL = logoURL;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDevelopers() {
        return developers;
    }

    public void setDevelopers(String developers) {
        this.developers = developers;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    @Override
    public String toString() {
        return name;
    }
}
