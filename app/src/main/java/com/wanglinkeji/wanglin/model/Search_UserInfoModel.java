package com.wanglinkeji.wanglin.model;

/**
 * Created by Administrator on 2016/10/2.
 * 添加好友，搜索出的好友Model
 */

public class Search_UserInfoModel {

    private int user_id;

    private String header_url;

    private String userName;

    private String industry;

    private String label;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getHeader_url() {
        return header_url;
    }

    public void setHeader_url(String header_url) {
        this.header_url = header_url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
