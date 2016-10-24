package com.wanglinkeji.wanglin.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.adapter.ViewPagerAdapter_AT_User;
import com.wanglinkeji.wanglin.util.OtherUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/22.
 * 点击@过后出现的好友列表界面
 */

public class AT_UserActivity extends Activity implements View.OnClickListener {

    public static final int FROM_WHAT_AT_USER = 0, FROM_WHAT_AT_NEIGHBOR = 1;

    private TextView textView_AtHouseEstate, textView_AtNeighbor;

    private ImageView imageView_cancle, imageView_AtHouseEstate_point, imageView_AtNeighbor_point;

    private LinearLayout layout_AtHouseEstate, layout_AtNeighbor;

    private ViewPager viewPager;

    private PopupWindow loading_page;

    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_activity_at_user);

        viewInit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imageview_ATUser_cancle:{
                AT_UserActivity.this.finish();
                break;
            }
            case R.id.layout_ATUser_ATHouseEstate:{
                setView_ChoosedAtHouseEstate();
                viewPager.setCurrentItem(0);
                break;
            }
            case R.id.layout_ATUser_ATNeighbor:{
                setView_choosedAtNeighbor();
                viewPager.setCurrentItem(1);
                break;
            }
            default:
                break;
        }
    }

    private void viewInit(){
        loading_page = OtherUtil.getLoadingPage(AT_UserActivity.this);
        rootView = LayoutInflater.from(AT_UserActivity.this).inflate(R.layout.layout_activity_at_user, null);
        imageView_cancle = (ImageView)findViewById(R.id.imageview_ATUser_cancle);
        imageView_cancle.setOnClickListener(this);
        layout_AtHouseEstate = (LinearLayout)findViewById(R.id.layout_ATUser_ATHouseEstate);
        layout_AtHouseEstate.setOnClickListener(this);
        layout_AtNeighbor = (LinearLayout)findViewById(R.id.layout_ATUser_ATNeighbor);
        layout_AtNeighbor.setOnClickListener(this);
        imageView_AtHouseEstate_point = (ImageView)findViewById(R.id.imageview_ATUser_AtHouseEstate_point);
        imageView_AtNeighbor_point = (ImageView)findViewById(R.id.imageview_ATUser_AtNeighbor_point);
        textView_AtHouseEstate = (TextView)findViewById(R.id.textview_AtUser_AtHouseEstate);
        textView_AtNeighbor = (TextView)findViewById(R.id.textview_AtUser_AtNeighbor);
        textView_AtHouseEstate.setText("@小区好友");
        textView_AtNeighbor.setText("@邻居");
        viewPager = (ViewPager)findViewById(R.id.viewpager_aitUser);
        List<View> list_views = new ArrayList<>();
        View view_atHouseEstate = LayoutInflater.from(AT_UserActivity.this).inflate(R.layout.layout_viewpager_item_atuser_at_houseestate, null);
        View view_atNeighbor = LayoutInflater.from(AT_UserActivity.this).inflate(R.layout.layout_viewpager_item_atuser_at_neighbor, null);
        list_views.add(view_atHouseEstate);
        list_views.add(view_atNeighbor);
        viewPager.setAdapter(new ViewPagerAdapter_AT_User(list_views, AT_UserActivity.this, loading_page, rootView));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageScrollStateChanged(int state) {}
            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:{
                        setView_ChoosedAtHouseEstate();
                        break;
                    }
                    case 1:{
                        setView_choosedAtNeighbor();
                        break;
                    }
                    default:
                        break;
                }
            }
        });
    }

    private void setView_ChoosedAtHouseEstate(){
        imageView_AtHouseEstate_point.setVisibility(View.VISIBLE);
        imageView_AtNeighbor_point.setVisibility(View.INVISIBLE);
    }

    private void setView_choosedAtNeighbor(){
        imageView_AtHouseEstate_point.setVisibility(View.INVISIBLE);
        imageView_AtNeighbor_point.setVisibility(View.VISIBLE);
    }
}
