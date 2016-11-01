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

    public static final int MESSAGE_SEND_STATE_FAILED = -1;

    public static final int MESSAGE_FROM_FRIEND = 0;

    public static final int MESSAGE_FROM_ME = 1;

    public static final int MESSAGE_TYPE_TEXT = 1;

    public static final int MESSAGE_TYPE_VOICE = 2;

    public static final int MESSAGE_TYPE_IMAGE = 3;

    public static final int VOICE_PLAY_STATE_START = 1;

    public static final int VOICE_PLAY_STATE_STOP = 2;

    public static final int VOICE_READ_STATE_HAS_READ = 1;

    public static final int VOICE_READ_STATE_NOT_READ = 2;

    private Date realDate;

    private boolean isShowDate;

    private int messageFrom;

    private int messageSendState;

    private String friendHeader_url;

    private String friendNickName;

    private int messageType;

    private String friendContent;

    private String myHeader_url;

    private String myNickName;

    private String myContent;

    private String localVoicePath;

    private String voiceUrl;

    private String imageUrl;

    private int voicePlayState;

    private int voiceLength;

    private int voiceReadState;

    public int getVoiceReadState() {
        return voiceReadState;
    }

    public void setVoiceReadState(int voiceReadState) {
        this.voiceReadState = voiceReadState;
    }

    public int getVoiceLength() {
        return voiceLength;
    }

    public void setVoiceLength(int voiceLength) {
        this.voiceLength = voiceLength;
    }

    public int getVoicePlayState() {
        return voicePlayState;
    }

    public void setVoicePlayState(int voicePlayState) {
        this.voicePlayState = voicePlayState;
    }

    public String getLocalVoicePath() {
        return localVoicePath;
    }

    public void setLocalVoicePath(String localVoicePath) {
        this.localVoicePath = localVoicePath;
    }

    public String getVoiceUrl() {
        return voiceUrl;
    }

    public void setVoiceUrl(String voiceUrl) {
        this.voiceUrl = voiceUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

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
