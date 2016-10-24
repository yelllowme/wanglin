package com.wanglinkeji.wanglin.model;

import java.util.List;

/**
 * Created by Administrator on 2016/8/31.
 * 吐槽的图片Model
 */
public class SwpeingImageModel {

    public static List<SwpeingImageModel> list_nineImage;

    public static int current_position;

    //图片的URL
    private String url;

    //图片的序号（顺序号）
    private int Orders;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getOrders() {
        return Orders;
    }

    public void setOrders(int orders) {
        Orders = orders;
    }
}
