package com.wanglinkeji.wanglin.model;

import java.util.List;

/**
 * Created by Administrator on 2016/10/2.
 * 聊天群列表Item
 */

public class ChatGroupItem_Model {

    public static List<ChatGroupItem_Model> list_group;

    private String header_url;

    private String groupName;

    public String getHeader_url() {
        return header_url;
    }

    public void setHeader_url(String header_url) {
        this.header_url = header_url;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
