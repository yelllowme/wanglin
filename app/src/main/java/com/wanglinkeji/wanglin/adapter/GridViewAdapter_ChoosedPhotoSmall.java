package com.wanglinkeji.wanglin.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.activity.ChoosedPhoto_BigActivity;
import com.wanglinkeji.wanglin.activity.ChoosedPhoto_SmallActivity;
import com.wanglinkeji.wanglin.model.PhotoModel;
import com.wanglinkeji.wanglin.util.OtherUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Administrator on 2016/9/12.
 * 图片选择器，小图GridView_Adapter
 */
public class GridViewAdapter_ChoosedPhotoSmall extends BaseAdapter {

    private List<PhotoModel> list_photos;

    private Context context;

    private int resource;

    private TextView textView_finishBtn, textView_preview;

    //显示图片的配置
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.shape_rectangle_black_lighter_noborder_nocorner)
            .showImageOnFail(R.drawable.shape_rectangle_gray_noborder_nocorner)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    public GridViewAdapter_ChoosedPhotoSmall(List<PhotoModel> list_photos, Context context, int resource, TextView textView_finishBtn,TextView textView_preview){
        this.list_photos = list_photos;
        this.context = context;
        this.resource = resource;
        this.textView_finishBtn = textView_finishBtn;
        this.textView_preview = textView_preview;
    }

    @Override
    public int getCount() {
        if (ChoosedPhoto_SmallActivity.isShowTakePhoto == true){
            return list_photos.size() + 1;
        }else {
            return list_photos.size();
        }

    }

    @Override
    public Object getItem(int i) {
        if (ChoosedPhoto_SmallActivity.isShowTakePhoto == true){
            if (i == 0){
                return null;
            }else {
                return list_photos.get(i);
            }
        }else {
            return list_photos.get(i);
        }
    }

    @Override
    public long getItemId(int i) {
        if (ChoosedPhoto_SmallActivity.isShowTakePhoto == true){
            if (i == 0){
                return i;
            }else {
                return i - 1;
            }
        }else {
            return i;
        }
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final viewHolder holder;
        if (view == null){
            view = LayoutInflater.from(context).inflate(resource, null);
            holder = new viewHolder();
            holder.imageView_photo = (ImageView)view.findViewById(R.id.imageview_choosedPhotoSmalItem_photo);
            OtherUtil.setViewLayoutParams(holder.imageView_photo, true, 0, 3);
            holder.imageView_isChoosed = (ImageView)view.findViewById(R.id.imageview_choosedPhotoSmalItem_isChoosed);
            OtherUtil.setViewLayoutParams(holder.imageView_isChoosed, true, 0, 15);
            holder.imageView_carmeraIcon = (ImageView)view.findViewById(R.id.imageview_choosedPhotoSmalItem_cameraIcon);
            OtherUtil.setViewLayoutParams(holder.imageView_carmeraIcon, true, 0, 7);
            holder.layout_photoItem = (RelativeLayout)view.findViewById(R.id.layout_choosedPhotoSmallItem_photoItem);
            holder.layout_takePhoto = (LinearLayout) view.findViewById(R.id.layout_choosedPhotoSmallItem_takePhoto);
            OtherUtil.setViewLayoutParams(holder.layout_takePhoto, true, 1, 3);
            view.setTag(holder);
        }else {
            holder = (viewHolder)view.getTag();
        }

        //根据是否显示拍照按钮设置逻辑（显示）
        if (ChoosedPhoto_SmallActivity.isShowTakePhoto == true){
            //list_photos的position
            final int position = i - 1;
            //设置Item显示
            if (i == 0){
                //设置布局显示
                holder.layout_takePhoto.setVisibility(View.VISIBLE);
                holder.layout_photoItem.setVisibility(View.GONE);
                //拍照按钮
                holder.layout_takePhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (PhotoModel.getNumChoosedPhoto(PhotoModel.list_allPhotos) < PhotoModel.photoCount){
                            String imageFolder_path;
                            Intent intent = new Intent();
                            intent.setAction("android.media.action.IMAGE_CAPTURE");
                            intent.addCategory("android.intent.category.DEFAULT");
                            //通过获取当前设置拍摄照片的名字
                            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                            String date = sDateFormat.format(new java.util.Date());
                            imageFolder_path = Environment.getExternalStorageDirectory().getPath() + "/wanglin/";
                            File mfile = new File(imageFolder_path);
                            mfile.mkdirs();
                            PhotoModel.takePhoto_path = imageFolder_path + date + ".jpg";
                            File file = new File(PhotoModel.takePhoto_path);
                            Uri uri = Uri.fromFile(file);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                            ((ChoosedPhoto_SmallActivity)context).startActivityForResult(intent, OtherUtil.REQUEST_CODE_CHOODEDPHOTO_SMALL_TO_TAKE_PHOTO);
                        }else {
                            Toast.makeText(context, "图片已满！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else {
                /**
                 * 设置显示
                 */
                //设置布局显示
                holder.layout_takePhoto.setVisibility(View.GONE);
                holder.layout_photoItem.setVisibility(View.VISIBLE);
                //用ImageLoader显示照片
                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.displayImage(ImageDownloader.Scheme.FILE.wrap(list_photos.get(position).getPath()), holder.imageView_photo,options);
                //设置isChoosed的图标显示
                if (list_photos.get(position).isChoosed() == false){
                    holder.imageView_isChoosed.setImageResource(R.mipmap.photo_notchoosed_icon);
                }else if(list_photos.get(position).isChoosed() == true){
                    holder.imageView_isChoosed.setImageResource(R.mipmap.photo_choosed_icon);
                }
                /**
                 *设置点击事件
                 */
                holder.imageView_isChoosed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (list_photos.get(position).isChoosed() == true){
                            holder.imageView_isChoosed.setImageResource(R.mipmap.photo_notchoosed_icon);
                            //改变list_show中的属性
                            list_photos.get(position).setChoosed(false);
                            //改变list_all中的属性
                            for (int i = 0; i < PhotoModel.list_allPhotos.size(); i++) {
                                if (PhotoModel.list_allPhotos.get(i).getPath().equals(list_photos.get(position).getPath())) {
                                    PhotoModel.list_allPhotos.get(i).setChoosed(false);
                                }
                            }
                            setViewAfterChoosed();
                        }else {
                            if (PhotoModel.getNumChoosedPhoto(PhotoModel.list_allPhotos) < PhotoModel.photoCount){
                                holder.imageView_isChoosed.setImageResource(R.mipmap.photo_choosed_icon);
                                //改变list_show中的属性
                                list_photos.get(position).setChoosed(true);
                                //改变list_all中的属性
                                for (int i = 0; i < PhotoModel.list_allPhotos.size(); i++){
                                    if (PhotoModel.list_allPhotos.get(i).getPath().equals(list_photos.get(position).getPath())){
                                        PhotoModel.list_allPhotos.get(i).setChoosed(true);
                                    }
                                }
                                setViewAfterChoosed();
                            }
                        }
                    }
                });
                holder.imageView_photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PhotoModel.list_bigPhotos = list_photos;
                        Intent intent = new Intent(context, ChoosedPhoto_BigActivity.class);
                        intent.putExtra("position", position);
                        ((ChoosedPhoto_SmallActivity)context).startActivityForResult(intent, OtherUtil.REQUEST_CODE_CHOOSED_PHOTO_SMALL_TO_BIG);
                    }
                });
            }
        //根据是否显示拍照按钮设置逻辑（不显示）
        }else {
            /**
             * 设置显示
             */
            //list_photos的position
            final int position = i;
            //设置布局显示
            holder.layout_takePhoto.setVisibility(View.GONE);
            holder.layout_photoItem.setVisibility(View.VISIBLE);
            //用ImageLoader显示照片
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(ImageDownloader.Scheme.FILE.wrap(list_photos.get(position).getPath()), holder.imageView_photo,options);
            //设置isChoosed的图标显示
            if (list_photos.get(position).isChoosed() == false){
                holder.imageView_isChoosed.setImageResource(R.mipmap.photo_notchoosed_icon);
            }else if(list_photos.get(position).isChoosed() == true){
                holder.imageView_isChoosed.setImageResource(R.mipmap.photo_choosed_icon);
            }
            /**
             * 设置点击事件
             */
            holder.imageView_isChoosed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (list_photos.get(position).isChoosed() == true){
                        holder.imageView_isChoosed.setImageResource(R.mipmap.photo_notchoosed_icon);
                        //改变list_show中的属性
                        list_photos.get(position).setChoosed(false);
                        //改变list_all中的属性
                        for (int i = 0; i < PhotoModel.list_allPhotos.size(); i++) {
                            if (PhotoModel.list_allPhotos.get(i).getPath().equals(list_photos.get(position).getPath())) {
                                PhotoModel.list_allPhotos.get(i).setChoosed(false);
                            }
                        }
                        setViewAfterChoosed();
                    }else {
                        if (PhotoModel.getNumChoosedPhoto(PhotoModel.list_allPhotos) < PhotoModel.photoCount){
                            holder.imageView_isChoosed.setImageResource(R.mipmap.photo_choosed_icon);
                            //改变list_show中的属性
                            list_photos.get(position).setChoosed(true);
                            //改变list_all中的属性
                            for (int i = 0; i < PhotoModel.list_allPhotos.size(); i++){
                                if (PhotoModel.list_allPhotos.get(i).getPath().equals(list_photos.get(position).getPath())){
                                    PhotoModel.list_allPhotos.get(i).setChoosed(true);
                                }
                            }
                            setViewAfterChoosed();
                        }
                    }
                }
            });
            holder.imageView_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PhotoModel.list_bigPhotos = list_photos;
                    Intent intent = new Intent(context, ChoosedPhoto_BigActivity.class);
                    intent.putExtra("position", position);
                    ((ChoosedPhoto_SmallActivity)context).startActivityForResult(intent, OtherUtil.REQUEST_CODE_CHOOSED_PHOTO_SMALL_TO_BIG);
                }
            });
        }
        return view;
    }

    private class viewHolder{
        ImageView imageView_photo;
        ImageView imageView_isChoosed;
        ImageView imageView_carmeraIcon;
        RelativeLayout layout_photoItem;
        LinearLayout layout_takePhoto;
    }

    private void setViewAfterChoosed(){
        if (PhotoModel.getNumChoosedPhoto(PhotoModel.list_allPhotos) == 0){
            //完成按钮
            textView_finishBtn.setTextColor(0X99FFFFFF);
            textView_finishBtn.setBackgroundResource(R.drawable.shape_rectangle_blue_lighter_er_noborder_allcorner);
            textView_finishBtn.setText(PhotoModel.finishText);
            textView_finishBtn.setEnabled(false);
            //预览按钮
            textView_preview.setText("预览");
            textView_preview.setTextColor(0X99FFFFFF);
            textView_preview.setEnabled(false);
        }else {
            //完成按钮
            textView_finishBtn.setTextColor(0XFFFFFFFF);
            textView_finishBtn.setBackgroundResource(R.drawable.shape_rectangle_blue_lighter_noborder_allcorner);
            String text = "(" + PhotoModel.getNumChoosedPhoto(PhotoModel.list_allPhotos) + "/" + PhotoModel.photoCount + ")";
            textView_finishBtn.setText(PhotoModel.finishText + text);
            textView_finishBtn.setEnabled(true);
            //预览按钮
            textView_preview.setText("预览" + text);
            textView_preview.setTextColor(0XFFFFFFFF);
            textView_preview.setEnabled(true);
        }
    }
}
