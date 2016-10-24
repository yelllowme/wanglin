package com.wanglinkeji.wanglin.model;

import java.util.List;

/**
 * Created by Administrator on 2016/9/30.
 * 聊天列表，最近聊天Item Model
 */

public class ChatListItemModel {

    public static List<ChatListItemModel> list_chatList;

    private String friendName;

    private String lastTalk;

    private String headerUrl;

    public String getLastTalk() {
        return lastTalk;
    }

    public void setLastTalk(String lastTalk) {
        this.lastTalk = lastTalk;
    }

    public String getHeaderUrl() {
        return headerUrl;
    }

    public void setHeaderUrl(String headerUrl) {
        this.headerUrl = headerUrl;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }
}
