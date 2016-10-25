package com.wanglinkeji.wanglin.model;

import com.wanglinkeji.wanglin.util.OtherUtil;

import java.util.Date;

/**
 * Created by Administrator on 2016/10/21.
 * 聊天界面item
 */

public class ChatItemMoeld {

    public static final int MESSAGE_SEND_STATE_ING = 0;

    public static final int MESSAGE_SEND_STATE_FINISH = 1;

    public static final int MESSAGE_FROM_FRIEND = 0;

    public static final int MESSAGE_FROM_ME = 1;

    private Date realDate;

    private boolean isShowDate;

    private int messageFrom;

    private int messageSendState;

    private String friendHeader_url;

    private String friendNickName;

    private String friendContent;

    private String myHeader_url;

    private String myNickName;

    private String myContent;

    public boolean isShowDate() {
        return isShowDate;
    }

    public void setShowDate(boolean showDate) {
        isShowDate = showDate;
    }

    public int getMessageFrom() {
        return messageFrom;
    }

    public void setMessageFrom(int messageFrom) {
        this.messageFrom = messageFrom;
    }

    public int getMessageSendState() {
        return messageSendState;
    }

    public void setMessageSendState(int messageSendState) {
        this.messageSendState = messageSendState;
    }

    public String getShowDate() {
        return OtherUtil.getCurrentTime(realDate);
    }

    public Date getRealDate() {
        return realDate;
    }

    public void setRealDate(Date realDate) {
        this.realDate = realDate;
    }

    public String getFriendHeader_url() {
        return friendHeader_url;
    }

    public void setFriendHeader_url(String friendHeader_url) {
        this.friendHeader_url = friendHeader_url;
    }

    public String getFriendNickName() {
        return friendNickName;
    }

    public void setFriendNickName(String friendNickName) {
        this.friendNickName = friendNickName;
    }

    public String getFriendContent() {
        return friendContent;
    }

    public void setFriendContent(String friendContent) {
        this.friendContent = friendContent;
    }

    public String getMyHeader_url() {
        return myHeader_url;
    }

    public void setMyHeader_url(String myHeader_url) {
        this.myHeader_url = myHeader_url;
    }

    public String getMyNickName() {
        return myNickName;
    }

    public void setMyNickName(String myNickName) {
        this.myNickName = myNickName;
    }

    public String getMyContent() {
        return myContent;
    }

    public void setMyContent(String myContent) {
        this.myContent = myContent;
    }
}
