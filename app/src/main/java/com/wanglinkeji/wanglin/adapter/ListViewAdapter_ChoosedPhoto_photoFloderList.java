package com.wanglinkeji.wanglin.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.model.PhotofolderModel;

import java.util.List;

/**
 * Created by Administrator on 2016/9/12.
 * 图片选择器，小图界面，相册ListView_Adapter
 */
public class ListViewAdapter_ChoosedPhoto_photoFloderList extends BaseAdapter {

    private List<PhotofolderModel> list_photoFloder;

    private Context context;

    private int resource;

    //显示图片的配置
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.shape_rectangle_white_noborder_nocorner)
            .showImageOnFail(R.drawable.shape_rectangle_gray_noborder_nocorner)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    public ListViewAdapter_ChoosedPhoto_photoFloderList(List<PhotofolderModel> list_photoFloder, Context context, int resource){
        this.list_photoFloder= list_photoFloder;
        this.context = context;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        return list_photoFloder.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        viewHolder holder;
        if (view == null){
            view = LayoutInflater.from(context).inflate(resource, null);
            holder = new viewHolder();
            holder.imageView_header = (ImageView)view.findViewById(R.id.imageview_choosedPhotoSmallFolderItem_header);
            holder.imageView_isChoosed = (ImageView)view.findViewById(R.id.imageview_choosedPhotoSmallFolderItem_isChoosed);
            holder.textView_folderName = (TextView)view.findViewById(R.id.textview_choosedPhotoSmallFolderItem_folderName);
            holder.textView_photoNum = (TextView)view.findViewById(R.id.textview_choosedPhotoSmallFolderItem_photoNum);
            view.setTag(holder);
        }else {
            holder = (viewHolder)view.getTag();
        }
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(ImageDownloader.Scheme.FILE.wrap(list_photoFloder.get(position).getFirstImagePath()), holder.imageView_header,options);
        holder.textView_folderName.setText(list_photoFloder.get(position).getName());
        holder.textView_photoNum.setText(list_photoFloder.get(position).getCount() + "张");
        if (list_photoFloder.get(position).isChoosed() == true){
            holder.imageView_isChoosed.setVisibility(View.VISIBLE);
        }else {
            holder.imageView_isChoosed.setVisibility(View.INVISIBLE);
        }
        return view;
    }

    private class viewHolder{
        ImageView imageView_header;
        ImageView imageView_isChoosed;
        TextView textView_folderName;
        TextView textView_photoNum;
    }
}
