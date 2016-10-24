package com.wanglinkeji.wanglin.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/23.
 * 用户好友Model
 */

public class UserFriendModel {

    public static int chatPosition;

    public static List<UserFriendModel> list_friends;

    //好友Id
    private int friendId;

    //好友昵称
    private String name;

    //好友电话号码
    private String phone;

    //好友备注
    private String remark;

    //显示数据拼音的首字母
    private String sortLetters;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }
}
