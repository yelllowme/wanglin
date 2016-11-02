package com.wanglinkeji.wanglin.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.wanglinkeji.wanglin.R;

import java.io.File;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by Administrator on 2016/11/2.
 * 聊天界面，点击小图查看大图，“大图”界面
 */

public class BigImage_ChatActivity extends Activity implements View.OnClickListener {

    private PhotoView imageView_bigImage;

    private String localPath;

    //显示图片的配置
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.shape_rectangle_black_lighter_noborder_nocorner)
            .showImageOnFail(R.drawable.shape_rectangle_gray_noborder_nocorner)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_activity_big_image_chat);

        viewInit();
    }

    private void viewInit(){
        localPath = getIntent().getStringExtra("path");
        imageView_bigImage = (PhotoView)findViewById(R.id.imageView_bigImage_chat);

        //判断是否是本地图片，
        File file = new File(localPath);
        if (file.isFile() && file.exists()){
            //用ImageLoader显示照片
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(ImageDownloader.Scheme.FILE.wrap(localPath), imageView_bigImage,options);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imageView_bigImage_chat:{
                BigImage_ChatActivity.this.finish();
                break;
            }
            default:
                break;
        }
    }
}
