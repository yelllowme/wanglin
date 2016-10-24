package com.wanglinkeji.wanglin.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.model.PhotoModel;
import com.wanglinkeji.wanglin.util.OtherUtil;
import com.wanglinkeji.wanglin.util.WangLinApplication;

import java.util.List;

/**
 * Created by Administrator on 2016/9/22.
 * 发表吐槽界面，图片List Adapter
 */

public class GridViewAdapter_IssueBlog_ImageList extends BaseAdapter {

    private List<PhotoModel> list_photos;

    private Context context;

    private int resource;

    //显示图片的配置
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.shape_rectangle_black_lighter_noborder_nocorner)
            .showImageOnFail(R.drawable.shape_rectangle_gray_noborder_nocorner)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    public GridViewAdapter_IssueBlog_ImageList(List<PhotoModel> list_photos, Context context, int resource){
        this.list_photos = list_photos;
        this.context = context;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        return list_photos.size();
    }

    @Override
    public Object getItem(int i) {
        return list_photos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(resource, null);
        ImageView imageView_photo = (ImageView) view.findViewById(R.id.imageview_gridviewItem_housingEstate_nineImage);
        //设置ImageView的大小
        ViewGroup.LayoutParams params = imageView_photo.getLayoutParams();
        params.width = (WangLinApplication.screen_Width - OtherUtil.dip2px(context, 30))/3;
        params.height = params.width;
        //用ImageLoader显示照片
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(ImageDownloader.Scheme.FILE.wrap(list_photos.get(position).getPath()), imageView_photo,options);
        return view;
    }
}
