package com.wanglinkeji.wanglin.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/20.
 * 图片选择器：小图Model
 */
public class PhotoModel {

    //设置总共能选择图片的数量
    public static int photoCount;

    //完成按钮显示文字内容
    public static String finishText = "完成";

    //拍照所得图片路径
    public static String takePhoto_path;

    //所有图片
    public static List<PhotoModel> list_allPhotos;

    //GridView显示的图片
    public static List<PhotoModel> list_showPhotos;

    //大图显示的图片
    public static List<PhotoModel> list_bigPhotos;

    //最终选中的图片
    public static List<PhotoModel> list_choosedPhotos = new ArrayList<>();

    //图片路径
    private String path;

    //是否被选中
    private boolean isChoosed;

    public static int getNumChoosedPhoto(List<PhotoModel> list_phtots){
        int count = 0;
        for (int i = 0; i < list_phtots.size(); i++){
            if (list_phtots.get(i).isChoosed() == true){
                count++;
            }
        }
        return count;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isChoosed() {
        return isChoosed;
    }

    public void setChoosed(boolean choosed) {
        isChoosed = choosed;
    }
}
