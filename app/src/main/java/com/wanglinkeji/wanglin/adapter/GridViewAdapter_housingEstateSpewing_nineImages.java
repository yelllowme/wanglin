package com.wanglinkeji.wanglin.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.activity.NineImage_BigActivity;
import com.wanglinkeji.wanglin.model.SwpeingImageModel;
import com.wanglinkeji.wanglin.util.OtherUtil;
import com.wanglinkeji.wanglin.util.WangLinApplication;

import java.util.List;

/**
 * Created by Administrator on 2016/8/23.
 * 九宫格的GridViewAdapter
 */
public class GridViewAdapter_housingEstateSpewing_nineImages extends BaseAdapter {

    private List<SwpeingImageModel> list_images;

    private Context context;

    private int resource;

    //显示图片的配置
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.shape_rectangle_black_noborder_nocorner)
            .showImageOnFail(R.drawable.shape_rectangle_gray_noborder_nocorner)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    public GridViewAdapter_housingEstateSpewing_nineImages(List<SwpeingImageModel> list_images, Context context, int resource){
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
    public View getView(final int position, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(resource, null);
        ImageView imageView = (ImageView)view.findViewById(R.id.imageview_gridviewItem_housingEstate_nineImage);
        //设置ImageView的大小
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.width = (WangLinApplication.screen_Width - OtherUtil.dip2px(context, 50))/3;
        params.height = params.width;
        ImageLoader.getInstance().displayImage(list_images.get(position).getUrl(), imageView, options);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SwpeingImageModel.list_nineImage = list_images;
                SwpeingImageModel.current_position = position;
                Intent intent = new Intent(context, NineImage_BigActivity.class);
                context.startActivity(intent);
            }
        });
        return view;
    }
}
