package com.wanglinkeji.wanglin.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.model.PhotoModel;
import com.wanglinkeji.wanglin.util.OtherUtil;
import com.wanglinkeji.wanglin.util.WangLinApplication;

import java.util.List;

/**
 * Created by Administrator on 2016/9/13.
 * 选择图片界面，浏览大图ViewPager_Adapter
 */
public class ViewPagerAdapter_ChoosedPhotoBig_bigPhotos extends PagerAdapter {

    private List<View> list_views;

    private List<PhotoModel> list_photos;

    private Context context;

    private LinearLayout layout_title;

    private TextView textView_finishBtn;

    private boolean isShowBottomTitle;

    //显示图片的配置
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.mipmap.image_loading_onload)
            .showImageOnFail(R.mipmap.image_loading_failed)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    public ViewPagerAdapter_ChoosedPhotoBig_bigPhotos(List<View> list_views, List<PhotoModel> list_photos,Context context,LinearLayout layout_title,
            TextView textView_finishBtn ,boolean isShowBottomTitle){
        this.list_views = list_views;
        this.context = context;
        this.layout_title = layout_title;
        this.textView_finishBtn = textView_finishBtn;
        this.list_photos = list_photos;
        this.isShowBottomTitle = isShowBottomTitle;
    }

    @Override
    public int getCount() {
        return list_views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container,final int position) {
        final LinearLayout layout_bottom = (LinearLayout)list_views.get(position).findViewById(R.id.layout_choosedPhoto_Big_bottom);
        OtherUtil.setViewLayoutParams(layout_bottom, false, 12.5f, 1);
        LinearLayout layout_isChoosed = (LinearLayout)list_views.get(position).findViewById(R.id.layout_choosedPhoto_Big_bottom_isChoosed);
        final ImageView imageView_isChoosed = (ImageView)list_views.get(position).findViewById(R.id.imageview_viewPagerItem_choosedPhotoBig_isChoosed);
        ImageView imageView_photo = (ImageView)list_views.get(position).findViewById(R.id.imageview_viewPagerItem_choosedPhotoBig_photo);
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    //底部顶部标题消失
                    case -666:{
                        layout_title.setVisibility(View.GONE);
                        layout_bottom.setVisibility(View.GONE);
                        break;
                    }
                    default:
                        break;
                }
            }
        };
        /**
         * 设置初始值
         */
        if (isShowBottomTitle == true){
            layout_title.setVisibility(View.VISIBLE);
            layout_bottom.setVisibility(View.VISIBLE);
        }else {
            layout_title.setVisibility(View.GONE);
            layout_bottom.setVisibility(View.GONE);
        }
        if (list_photos.get(position).isChoosed() == true){
            imageView_isChoosed.setImageResource(R.mipmap.photo_choosed_icon);
        }else {
            imageView_isChoosed.setImageResource(R.mipmap.photo_notchoosed_icon);
        }
        //用ImageLoader显示照片
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(ImageDownloader.Scheme.FILE.wrap(list_photos.get(position).getPath()), imageView_photo,options);

        /**
         * 设置点击事件
         */
        layout_isChoosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (list_photos.get(position).isChoosed() == true){
                    imageView_isChoosed.setImageResource(R.mipmap.photo_notchoosed_icon);
                    //改变list_big中的属性
                    list_photos.get(position).setChoosed(false);
                    //改变list_all中的属性
                    for (int i = 0; i < PhotoModel.list_allPhotos.size(); i++) {
                        if (PhotoModel.list_allPhotos.get(i).getPath().equals(list_photos.get(position).getPath())) {
                            PhotoModel.list_allPhotos.get(i).setChoosed(false);
                        }
                    }
                    setViewAfterChoosed();
                }else {
                    if (PhotoModel.getNumChoosedPhoto(PhotoModel.list_allPhotos) < PhotoModel.photoCount){
                        imageView_isChoosed.setImageResource(R.mipmap.photo_choosed_icon);
                        //改变list_big中的属性
                        list_photos.get(position).setChoosed(true);
                        //改变list_all中的属性
                        for (int i = 0; i < PhotoModel.list_allPhotos.size(); i++){
                            if (PhotoModel.list_allPhotos.get(i).getPath().equals(list_photos.get(position).getPath())){
                                PhotoModel.list_allPhotos.get(i).setChoosed(true);
                            }
                        }
                        setViewAfterChoosed();
                    }
                }
            }
        });
        imageView_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isShowBottomTitle == true){
                    isShowBottomTitle = false;
                    layout_bottom.startAnimation(OtherUtil.get_Translate_Anim(400, 0, 0, 0, WangLinApplication.screen_Height/12.5f));
                    layout_title.startAnimation(OtherUtil.get_Translate_Anim(400, 0, 0, 0, -WangLinApplication.screen_Height/12.5f));
                    handler.sendEmptyMessageDelayed(-666, 400);
                }else {
                    isShowBottomTitle = true;
                    layout_bottom.setVisibility(View.VISIBLE);
                    layout_title.setVisibility(View.VISIBLE);
                    layout_bottom.startAnimation(OtherUtil.get_Translate_Anim(400, 0, 0, WangLinApplication.screen_Height/12.5f, 0));
                    layout_title.startAnimation(OtherUtil.get_Translate_Anim(400, 0, 0, -WangLinApplication.screen_Height/12.5f, 0));
                }
            }
        });

        container.addView(list_views.get(position));
        return list_views.get(position);
    }

    private void setViewAfterChoosed(){
        if (PhotoModel.getNumChoosedPhoto(PhotoModel.list_allPhotos) == 0){
            //完成按钮
            textView_finishBtn.setTextColor(0X99FFFFFF);
            textView_finishBtn.setBackgroundResource(R.drawable.shape_rectangle_blue_lighter_er_noborder_allcorner);
            textView_finishBtn.setText(PhotoModel.finishText);
            textView_finishBtn.setEnabled(false);
        }else {
            //完成按钮
            textView_finishBtn.setTextColor(0XFFFFFFFF);
            textView_finishBtn.setBackgroundResource(R.drawable.shape_rectangle_blue_lighter_noborder_allcorner);
            String text = PhotoModel.finishText + "(" + PhotoModel.getNumChoosedPhoto(PhotoModel.list_allPhotos) + "/" + PhotoModel.photoCount + ")";
            textView_finishBtn.setText(text);
            textView_finishBtn.setEnabled(true);
        }
    }
}
