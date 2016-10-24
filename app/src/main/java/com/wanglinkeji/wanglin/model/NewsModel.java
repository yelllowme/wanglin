package com.wanglinkeji.wanglin.model;

import java.util.List;

/**
 * Created by Administrator on 2016/9/30.
 * 资讯（大事件）Model
 */

public class NewsModel {

    public static List<NewsModel> list_news;

    //标题
    private String newsTitle;

    //概览
    private String newsPreview;

    //来源
    private String newsFromWhat;

    //时间
    private String newsDate;

    //浏览数
    private int newsSeeCount;

    //图片URL
    private String imageUrl;

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewsPreview() {
        return newsPreview;
    }

    public void setNewsPreview(String newsPreview) {
        this.newsPreview = newsPreview;
    }

    public String getNewsFromWhat() {
        return newsFromWhat;
    }

    public void setNewsFromWhat(String newsFromWhat) {
        this.newsFromWhat = newsFromWhat;
    }

    public String getNewsDate() {
        return newsDate;
    }

    public void setNewsDate(String newsDate) {
        this.newsDate = newsDate;
    }

    public int getNewsSeeCount() {
        return newsSeeCount;
    }

    public void setNewsSeeCount(int newsSeeCount) {
        this.newsSeeCount = newsSeeCount;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
