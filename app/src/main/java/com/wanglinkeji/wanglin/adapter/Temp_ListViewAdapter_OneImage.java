package com.wanglinkeji.wanglin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.wanglinkeji.wanglin.R;

import java.util.List;

/**
 * Created by Administrator on 2016/9/30.
 * 临时填充界面所有
 */

public class Temp_ListViewAdapter_OneImage extends BaseAdapter {

    private List<Integer> list_images;

    private Context context;

    private int resource;

    public Temp_ListViewAdapter_OneImage(List<Integer> list_images, Context context, int resource){
        this.list_images = list_images;
        this.context = context;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        return list_images.size();
    }

    @Override
    public Object getItem(int i) {
        return list_images.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(resource, null);
        ImageView imageView = (ImageView)view.findViewById(R.id.temp_imageview_oneImage);
        imageView.setBackgroundResource(list_images.get(position).intValue());
        return view;
    }
}
