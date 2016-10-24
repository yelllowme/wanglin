package com.wanglinkeji.wanglin.model;

import java.util.List;

/**
 * Created by Administrator on 2016/9/30.
 * 添加好友列表，Item信息Model
 */

public class NewFriendInfoModel {

    public static final int INFO_STATUS_NOT_READ = 0;

    public static final int INFO_STATUS_HAS_READ = 1;

    public static List<NewFriendInfoModel> list_newFriends;

    private int infoId;

    private int newFriendId;

    private String header_url;

    private String newFriendName;

    private String newFriendMessage;

    private int infoStatus;

    public int getInfoId() {
        return infoId;
    }

    public void setInfoId(int infoId) {
        this.infoId = infoId;
    }

    public int getNewFriendId() {
        return newFriendId;
    }

    public void setNewFriendId(int newFriendId) {
        this.newFriendId = newFriendId;
    }

    public int getInfoStatus() {
        return infoStatus;
    }

    public void setInfoStatus(int infoStatus) {
        this.infoStatus = infoStatus;
    }

    public String getHeader_url() {
        return header_url;
    }

    public void setHeader_url(String header_url) {
        this.header_url = header_url;
    }

    public String getNewFriendName() {
        return newFriendName;
    }

    public void setNewFriendName(String newFriendName) {
        this.newFriendName = newFriendName;
    }

    public String getNewFriendMessage() {
        return newFriendMessage;
    }

    public void setNewFriendMessage(String newFriendMessage) {
        this.newFriendMessage = newFriendMessage;
    }
}
