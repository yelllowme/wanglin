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
import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.model.NewsModel;

import java.util.List;

/**
 * Created by Administrator on 2016/10/18.
 * “物业”新闻Adapter
 */

public class ListViewAdapter_CertifilerProperty_news extends BaseAdapter {

    private List<NewsModel> list_news;

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

    public ListViewAdapter_CertifilerProperty_news(List<NewsModel> list_news, Context context, int resource){
        this.list_news = list_news;
        this.context = context;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        return list_news.size();
    }

    @Override
    public Object getItem(int i) {
        return list_news.get(i);
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
            holder.imageView_header = (ImageView)view.findViewById(R.id.imageview_certifilerPropertyNewsItem_header);
            holder.textView_title = (TextView)view.findViewById(R.id.textview_certifilerPropertyNewsItem_title);
            holder.textView_browseCount = (TextView)view.findViewById(R.id.textview_certifilerPropertyNewsItem_browseCount);
            holder.textView_date = (TextView)view.findViewById(R.id.textview_certifilerPropertyNewsItem_date);
            view.setTag(holder);
        }else {
            holder = (viewHolder)view.getTag();
        }
        ImageLoader.getInstance().displayImage(list_news.get(position).getImageUrl(), holder.imageView_header, options);
        holder.textView_title.setText(list_news.get(position).getNewsTitle());
        holder.textView_browseCount.setText(String.valueOf(list_news.get(position).getNewsSeeCount()));
        return view;
    }

    private class viewHolder{
        ImageView imageView_header;
        TextView textView_title;
        TextView textView_browseCount;
        TextView textView_date;
    }
}
