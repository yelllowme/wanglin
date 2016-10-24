package com.wanglinkeji.wanglin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.adapter.ViewPagerAdapter_NineImageBig;
import com.wanglinkeji.wanglin.model.SwpeingImageModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/18.
 * 大图预览
 */

public class NineImage_BigActivity extends Activity{

    private ViewPager viewPager_nineImage;

    private List<View> list_views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_activity_nine_image_big);

        viewInit();
    }

    private void viewInit(){
        list_views = new ArrayList<>();
        for (int i = 0; i < SwpeingImageModel.list_nineImage.size(); i++){
            View view = LayoutInflater.from(NineImage_BigActivity.this).inflate(R.layout.layout_viewpager_item_nine_image_big, null);
            list_views.add(view);
        }
        viewPager_nineImage = (ViewPager)findViewById(R.id.viewPager_nineImageBig);
        viewPager_nineImage.setAdapter(new ViewPagerAdapter_NineImageBig(list_views, SwpeingImageModel.list_nineImage, NineImage_BigActivity.this));
        viewPager_nineImage.setCurrentItem(SwpeingImageModel.current_position);
    }
}
