package com.wanglinkeji.wanglin.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.fragment.CertifiedPropertyFragment;

import java.util.List;

/**
 * Created by Administrator on 2016/10/18.
 * “物业”图片轮播器Adapter
 */

public class ViewPagerAdapter_CertifilerProperty_ScrollImages extends PagerAdapter {

    private List<Integer> list_image;

    private List<View> list_view;

    public ViewPagerAdapter_CertifilerProperty_ScrollImages(List<Integer> list_image, List<View> list_view){
        this.list_image = list_image;
        this.list_view = list_view;
    }

    @Override
    public int getCount() {
        return list_view.size() * CertifiedPropertyFragment.IMAGE_COUNT;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //container.removeView((View)list.get(position));
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //对ViewPager页号求模取出View列表中要显示的项
        position %= list_view.size();

        ImageView imageView = (ImageView)list_view.get(position).findViewById(R.id.imageview_CertifiedPoperty_ImageCarousel_image);
        imageView.setImageResource(list_image.get(position));

        //如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。
        ViewParent vp = list_view.get(position).getParent();
        if (vp != null){
            ViewGroup parent = (ViewGroup)vp;
            parent.removeView(list_view.get(position));
        }
        container.addView(list_view.get(position));
        return list_view.get(position);
    }
}
