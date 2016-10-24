package com.wanglinkeji.wanglin.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/10/13.
 * 关于我的->回复Model
 */

public class AboutMeCommentModel {

    private int userId;

    private int commentId;

    private String header_url;

    private String userNick;

    private String label;

    private String industry;

    private String sendDate;

    private String commentDetails;

    private HousingEstateblogModel housingEstateblogModel;

    public String getCommentDetails() {
        return commentDetails;
    }

    public void setCommentDetails(String commentDetails) {
        this.commentDetails = commentDetails;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public String getHeader_url() {
        return header_url;
    }

    public void setHeader_url(String header_url) {
        this.header_url = header_url;
    }

    public String getUserNick() {
        return userNick;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getSendDate() {
        return sendDate;
    }

    public void setSendDate(String sendTime) {
        String[] temp = sendTime.split("\\)|\\(");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M-dd  HH:mm");
        Date date1 = new Date(Long.parseLong(temp[1]));
        sendTime = simpleDateFormat.format(date1);
        this.sendDate = sendTime;
        sendDate = sendTime;
    }

    public HousingEstateblogModel getHousingEstateModel() {
        return housingEstateblogModel;
    }

    public void setHousingEstateModel(HousingEstateblogModel housingEstateModel) {
        this.housingEstateblogModel = housingEstateModel;
    }
}
