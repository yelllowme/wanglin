package com.wanglinkeji.wanglin.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/8/23.
 * 小区吐槽BlogModel
 */
public class HousingEstateblogModel {

    public static List<HousingEstateblogModel> list_housingEstateBolg;

    public static List<HousingEstateblogModel> list_cityBlog;

    //吐槽的ID
    private int Id;

    //是否匿名
    private boolean IsAnonymous;

    //吐槽用户的昵称
    private String UserNick;

    //用户的标签
    private String UserLabel;

    //用户的行业
    private String UserIndustry;

    //吐槽的内容
    private String ComplainContent;

    //吐槽的发布时间
    private String SendTime;

    //吐槽用户的ID
    private int UserId;

    //所发表的小区的ID
    private int VillageId;

    //回复总数
    private int ReplyCount;

    //点赞总数
    private int GoodCount;

    //当前用户是否对这条吐槽进行点赞
    private boolean isAddGood;

    //图片List
    private List<SwpeingImageModel> list_images;

    //评论List
    private List<CommentModel> list_comments;

    //@List
    private List<AtItemModel> list_At_Item;

    public List<AtItemModel> getList_At_Item() {
        return list_At_Item;
    }

    public void setList_At_Item(List<AtItemModel> list_At_Item) {
        this.list_At_Item = list_At_Item;
    }

    public boolean isAddGood() {
        return isAddGood;
    }

    public void setAddGood(boolean addGood) {
        isAddGood = addGood;
    }

    public boolean isAnonymous() {
        return IsAnonymous;
    }

    public void setAnonymous(boolean anonymous) {
        IsAnonymous = anonymous;
    }

    public String getUserLabel() {
        return UserLabel;
    }

    public void setUserLabel(String userLabel) {
        UserLabel = userLabel;
    }

    public String getUserIndustry() {
        return UserIndustry;
    }

    public void setUserIndustry(String userIndustry) {
        UserIndustry = userIndustry;
    }

    public String getUserNick() {
        return UserNick;
    }

    public void setUserNick(String userNick) {
        UserNick = userNick;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getComplainContent() {
        return ComplainContent;
    }

    public void setComplainContent(String complainContent) {
        ComplainContent = complainContent;
    }

    public String getSendTime() {
        return SendTime;
    }

    public void setSendTime(String sendTime) {
        String[] temp = sendTime.split("\\)|\\(");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M-dd  HH:mm");
        Date date1 = new Date(Long.parseLong(temp[1]));
        sendTime = simpleDateFormat.format(date1);
        this.SendTime = sendTime;
        SendTime = sendTime;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public int getVillageId() {
        return VillageId;
    }

    public void setVillageId(int villageId) {
        VillageId = villageId;
    }

    public int getReplyCount() {
        return ReplyCount;
    }

    public void setReplyCount(int replyCount) {
        ReplyCount = replyCount;
    }

    public int getGoodCount() {
        return GoodCount;
    }

    public void setGoodCount(int goodCount) {
        GoodCount = goodCount;
    }

    public List<SwpeingImageModel> getList_images() {
        return list_images;
    }

    public void setList_images(List<SwpeingImageModel> list_images) {
        this.list_images = list_images;
    }

    public List<CommentModel> getList_comments() {
        return list_comments;
    }

    public void setList_comments(List<CommentModel> list_comments) {
        this.list_comments = list_comments;
    }
}
