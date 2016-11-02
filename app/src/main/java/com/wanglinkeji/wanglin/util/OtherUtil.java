package com.wanglinkeji.wanglin.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.model.PhotoModel;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Created by Administrator on 2016/8/16.
 * 工具类
 */
public class OtherUtil {

    /**
     * 活动跳转RequestCode
     */

    //Register_BindingHousingEstateActivity--->SearchHouseEstateActivity
    public static final int REQUEST_CODE_BINDING_HOUSE_TO_SEARCH_HOUSE = 1;

    //Register_UploadIdentityImageActivity--->ChoosedPhoto_SmallActivity
    //上传身份证正面照片
    public static final int REQUEST_CODE_UPLOAD_INDENTITY_IMAGE_TO_CHOOSED_PHOTO_ID_CARD_FRONT = 2;

    //上传身份证背面照
    public static final int REQUEST_CODE_UPLOAD_INDENTITY_IMAGE_TO_CHOOSED_PHOTO_ID_CARD_REVERSE = 3;

    //上传房产证
    public static final int REQUEST_CODE_UPLOAD_INDENTITY_IMAGE_TO_CHOOSED_PHOTO_HOUSE_PROPRIETARY_CERTIFICATE = 4;

    //上传租赁合同
    public static final int REQUEST_CODE_UPLOAD_INDENTITY_IMAGE_TO_CHOOSED_PHOTO_RENT_CONTRACT = 5;

    //ChoosedPhoto_SmallActivity -> ChoosedPhoto_BigActivity
    public static final int REQUEST_CODE_CHOOSED_PHOTO_SMALL_TO_BIG = 6;

    //ChoosedPhoto_SmallActivity -> 拍照
    public static final int REQUEST_CODE_CHOODEDPHOTO_SMALL_TO_TAKE_PHOTO = 7;

    //AddHouseEstate_HouseProCerActivity -> SearchHouseEstateActivity
    public static final int REQUEST_CODE_ADD_HOUSE_ESTATE_TO_SEARCH_HOUSE_ESTATE = 8;

    //AddHouseEstate_HouseProCerActivity -> ChoosedPhoto_SmallActivity
    public static final int REQUEST_CODE_ADD_HOUSE_ESTATE_TO_CHOODED_PHOTO = 9;

    //IssueBlogActivity -> ChoosedPhoto_SmallActivity
    public static final int REQUEST_CODE_ISSUE_BLOG_TO_CHOOSED_PHOTO = 10;

    //IssueBlogActivity ->AT_UserActivity
    public static final int REQUEST_CODE_ISSUE_BLOG_TO_AT_USER = 11;

    //ChatActivity -> ChoosedPhoto_SmallActivity
    public static final int REQUEST_CODE_CHAT_TO_CHOOSED_PHOTO = 12;

    /**
     * 设置TextView的字体
    */
    public static void setTextFace(Context context, TextView textView){
        Typeface typeface = Typeface.createFromAsset(context.getAssets(),"fonts/source_hansans.ttf");
        textView.setTypeface(typeface);
    }

    /**
     * dp转换为px,以及px转换为dp
    */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 设置控件的大小
     */
    public  static void setViewLayoutParams(View view, boolean isSquare, float height, float width){
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (isSquare == true){
            params.width = (int)(WangLinApplication.screen_Width/width);
            params.height = params.width;
        }else{
            params.height = (int)(WangLinApplication.screen_Height/height);
            params.width = (int)(WangLinApplication.screen_Width/width);
        }
    }

    /**
     * 获取加载页(PopupWindow)
     */
    public static PopupWindow getLoadingPage(Context context){
        View loadingPage_contentview = LayoutInflater.from(context).inflate(R.layout.layout_loading, null);
        PopupWindow popupWindow = new PopupWindow(loadingPage_contentview, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        return popupWindow;
    }

    /**
     *获取一个TranslateAnim(平移)动画
     */
    public static TranslateAnimation get_Translate_Anim(long duration, float fromX, float toX, float fromY, float toY){
        TranslateAnimation translateAnimation_frist = new TranslateAnimation(fromX, toX, fromY, toY);
        translateAnimation_frist.setDuration(duration);
        return  translateAnimation_frist;
    }

    /**
     * 获取一个RotateAnimation（旋转动画）
     */
    public static RotateAnimation get_Rotate_Anim(long duration, float fromAngle, float toAngle, float Xaxis, float Yaxis, boolean fillAfter){
        RotateAnimation rotateAnimation = new RotateAnimation(fromAngle, toAngle, Animation.RELATIVE_TO_SELF, Xaxis, Animation.RELATIVE_TO_SELF, Yaxis);
        rotateAnimation.setDuration(duration);
        rotateAnimation.setFillAfter(fillAfter);
        return rotateAnimation;
    }

    /**
     * 通过文件夹路径获取文件夹中所有图片的路径
     */
    public static ArrayList<PhotoModel> getPicturesByFolderPath(final String strPath) {
        ArrayList<String> list_path = new ArrayList<String>();
        ArrayList<PhotoModel> list_photo = new ArrayList<PhotoModel>();
        File file = new File(strPath);
        File[] allfiles = file.listFiles();
        if (allfiles == null) {
            return null;
        }
        for(int i = 0; i < allfiles.length; i++) {
            final File fi = allfiles[i];
            final PhotoModel photo = new PhotoModel();
            photo.setPath("");
            photo.setChoosed(false);
            if(fi.isFile()) {
                int idx = fi.getPath().lastIndexOf(".");
                if (idx <= 0) {
                    continue;
                }
                String suffix = fi.getPath().substring(idx);
                if (suffix.toLowerCase().equals(".jpg") ||
                        suffix.toLowerCase().equals(".jpeg")||
                        suffix.toLowerCase().equals(".bmp") ||
                        suffix.toLowerCase().equals(".png") ||
                        suffix.toLowerCase().equals(".gif") ) {
                    list_path.add(fi.getPath());
                    photo.setPath(fi.getPath());
                    for (int j = 0; j < PhotoModel.list_allPhotos.size(); j++){
                        if (fi.getPath().equals(PhotoModel.list_allPhotos.get(j).getPath())){
                            photo.setChoosed(PhotoModel.list_allPhotos.get(j).isChoosed());
                        }
                    }
                    list_photo.add(photo);
                }
            }
        }
        Collections.reverse(list_photo);
        return list_photo;
    }

    /**
     *发送广播通知系统更新多媒体数据库
     */
    public static void requestScanFile(Context context, File file) {
        Intent i = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        i.setData(Uri.fromFile(file));
        context.sendBroadcast(i);
    }

    /**
     * 获取系统当前时间（聊天用）
     */
    public static String getCurrentTime(Date getDate){
        SimpleDateFormat format_full = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat format_startDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format_min = new SimpleDateFormat("HH:mm");
        Date nowDate = new Date();
        try {
            String nowStartTime = format_startDate.format(nowDate) + " 00:00:00";
            Date nowStartDate = format_full.parse(nowStartTime);
            long betweenTime = nowStartDate.getTime() - getDate.getTime();
            long day = 24 * 60 * 60 * 1000;
            if (betweenTime < 0){
                return format_min.format(getDate);
            }
            if (betweenTime > 0 && betweenTime < day){
                return "昨天" + format_min.format(getDate);
            }
            if (betweenTime > day && betweenTime < day * 2){
                return "前天" + format_min.format(getDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return format_full.format(getDate);
    }

    /**
     * 根据音频文件的大小计算音频的秒数
     * @param file
     * @return
     */
    public static int getVoiceTimeByFileLength(String file) {
        File _file = new File(file);
        if (!_file.exists()) {
            return 0;
        }
        // 650个字节就是1s
        int duration = (int) Math.ceil(_file.length() / 650);
        if (duration > 60) {
            return 60;
        }
        if (duration < 1) {
            return 1;
        }
        return duration;
    }

    public static int getVoiceTimeByFileLength(long fileLength) {
        // 650个字节就是1s
        int duration = (int) Math.ceil(fileLength / 650);

        if (duration > 60) {
            return 60;
        }
        if (duration < 1) {
            return 1;
        }
        return duration;
    }
}
