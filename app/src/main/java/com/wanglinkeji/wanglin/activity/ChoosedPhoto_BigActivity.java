package com.wanglinkeji.wanglin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.adapter.ViewPagerAdapter_ChoosedPhotoBig_bigPhotos;
import com.wanglinkeji.wanglin.model.PhotoModel;
import com.wanglinkeji.wanglin.model.PhotofolderModel;
import com.wanglinkeji.wanglin.util.OtherUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/6.
 * 图片选择器大图Activity
 */
public class ChoosedPhoto_BigActivity extends Activity implements View.OnClickListener {

    private LinearLayout layout_title;

    private ImageView imageView_cancle;

    private TextView textView_photoOrder, textView_finishBtn;

    private ViewPager viewPager_bigPhoto;

    private List<View> list_views;

    //是否显示标题底部栏
    private boolean isShowBottomTitle = true;

    //大图显示的图片总数
    private int photoCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_activity_choosed_photo_big);

        viewInit();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("fromWhat", "cancle");
        setResult(RESULT_OK, intent);
        ChoosedPhoto_BigActivity.this.finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //返回按钮
            case R.id.imageview_choosedPhotoBig_cancle:{
                Intent intent = new Intent();
                intent.putExtra("fromWhat", "cancle");
                setResult(RESULT_OK, intent);
                ChoosedPhoto_BigActivity.this.finish();
                break;
            }
            //完成按钮
            case R.id.textview_choosedPhotoBig_finish:{
                Intent intent  = new Intent();
                intent.putExtra("fromWhat", "ensure");
                setResult(RESULT_OK, intent);
                ChoosedPhoto_BigActivity.this.finish();
                break;
            }
            default:
                break;
        }
    }

    private void viewInit(){
        //获得当前相册的照片数
        for (int i = 0; i < PhotofolderModel.list_photoFolder.size(); i++){
            if (PhotofolderModel.list_photoFolder.get(i).isChoosed() == true){
                photoCount = PhotofolderModel.list_photoFolder.get(i).getCount();
                break;
            }
        }
        /**
         * title控件
         */
        layout_title = (LinearLayout)findViewById(R.id.layout_choosedPhoto_Big_title);
        OtherUtil.setViewLayoutParams(layout_title, false, 12.5f, 1);
        imageView_cancle = (ImageView)findViewById(R.id.imageview_choosedPhotoBig_cancle);
        imageView_cancle.setOnClickListener(this);
        textView_photoOrder = (TextView)findViewById(R.id.textview_choosedPhotoBig_numOfChoosed);
        textView_photoOrder.setText(((getIntent().getIntExtra("position", 0)) + 1) +"/" +  photoCount);
        textView_finishBtn = (TextView)findViewById(R.id.textview_choosedPhotoBig_finish);
        textView_finishBtn.setOnClickListener(this);
        setViewAfterChoosed();
        /**
         * 初始ViewPager
         */
        list_views = new ArrayList<>();
        viewPager_bigPhoto = (ViewPager)findViewById(R.id.viewpager_choosedPhoto_Big);
        for (int i = 0; i < PhotoModel.list_bigPhotos.size(); i++){
            View view = LayoutInflater.from(ChoosedPhoto_BigActivity.this).inflate(R.layout.layout_viewpager_item_choosed_photo_big, null);
            list_views.add(view);
        }
        viewPager_bigPhoto.setAdapter(new ViewPagerAdapter_ChoosedPhotoBig_bigPhotos(list_views, PhotoModel.list_bigPhotos, ChoosedPhoto_BigActivity.this,
                layout_title, textView_finishBtn, isShowBottomTitle));
        viewPager_bigPhoto.setCurrentItem(getIntent().getIntExtra("position", 0));
        viewPager_bigPhoto.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageScrollStateChanged(int state) {}
            @Override
            public void onPageSelected(int position) {
                textView_photoOrder.setText((position + 1) + "/" + photoCount);
            }
        });
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
