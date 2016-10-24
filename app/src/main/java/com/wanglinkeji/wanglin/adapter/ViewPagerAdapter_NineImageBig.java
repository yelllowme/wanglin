package com.wanglinkeji.wanglin.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.activity.NineImage_BigActivity;
import com.wanglinkeji.wanglin.model.SwpeingImageModel;
import com.wanglinkeji.wanglin.util.OtherUtil;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Administrator on 2016/10/18.
 * 九宫格大图预览
 */

public class ViewPagerAdapter_NineImageBig extends PagerAdapter {

    private List<View> list_views;

    private List<SwpeingImageModel> list_images;

    private Context context;

    private LinearLayout layout_currentWindow;

    private LinearLayout current_loading_page;

    private Bitmap bitmap;

    //显示图片的配置
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.mipmap.image_loading_onload)
            .showImageOnFail(R.mipmap.image_loading_failed)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                //取消
                case -111:{
                    layout_currentWindow.setVisibility(View.GONE);
                    break;
                }
                //保存图片
                case -222:{
                    layout_currentWindow.setVisibility(View.GONE);
                    current_loading_page.setVisibility(View.VISIBLE);
                    saveImage((String)msg.obj);
                    break;
                }
                //保存图片成功或失败
                case -333:{
                    current_loading_page.setVisibility(View.GONE);
                    Toast.makeText(context, (String)msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                }
                default:
                    break;
            }
        }
    };

    public ViewPagerAdapter_NineImageBig(List<View> list_views, List<SwpeingImageModel> list_images, Context context){
        this.list_views = list_views;
        this.list_images = list_images;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list_views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_viewpager_item_nine_image_big, container, false);
        PhotoView imageView = (PhotoView) view.findViewById(R.id.imageview_nineImageBigItem);
        final LinearLayout layout_window = (LinearLayout)view.findViewById(R.id.layout_nineImageItem_window);
        final LinearLayout layout_bottom = (LinearLayout)view.findViewById(R.id.layout_nineImageItem_bottom);
        final LinearLayout loading_page = (LinearLayout)view.findViewById(R.id.layout_NineImageItem_loadingPage);
        TextView textView_preservation = (TextView)view.findViewById(R.id.textview_nineImageItem_preservation);
        TextView textView_cancle = (TextView)view.findViewById(R.id.textview_nineImageItem_cancle);

        ImageLoader.getInstance().displayImage(list_images.get(position).getUrl(), imageView, options);

        layout_window.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {}
        });
        loading_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {}
        });
        imageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float v, float v1) {
                ((NineImage_BigActivity)context).finish();
            }

            @Override
            public void onOutsidePhotoTap() {
            }
        });
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                layout_window.setVisibility(View.VISIBLE);
                layout_bottom.startAnimation(OtherUtil.get_Translate_Anim(300, 0, 0, 500, 0));
                return true; //返回false时，长按会触发短按的点击事件
            }
        });
        textView_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout_currentWindow = layout_window;
                layout_bottom.startAnimation(OtherUtil.get_Translate_Anim(300, 0, 0, 0, 500));
                handler.sendEmptyMessageDelayed(-111, 180);
            }
        });
        textView_preservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                current_loading_page = loading_page;
                layout_currentWindow = layout_window;
                layout_bottom.startAnimation(OtherUtil.get_Translate_Anim(300, 0, 0, 0, 500));
                Message message = new Message();
                message.what = -222;
                message.obj = list_images.get(position).getUrl();
                handler.sendMessageDelayed(message, 180);
            }
        });

        container.addView(view);
        return view;
    }

    private void saveImage(final String url){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String fileName = System.currentTimeMillis() + ".jpg";

                    //取得的是byte数组, 从byte数组生成bitmap
                    byte[] data = getImage(url);
                    if(data != null){
                        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap
                        saveFile(bitmap, fileName);
                    }else{
                        Message message = new Message();
                        message.what = -333;
                        message.obj = "保存失败，请稍后重试！";
                        handler.sendEmptyMessage(-333);
                    }
                } catch (Exception e) {
                    Message message = new Message();
                    message.what = -333;
                    message.obj = "保存失败！失败原因：无法链接网络！";
                    handler.sendEmptyMessage(-333);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void saveFile(Bitmap bm, String fileName) throws IOException {
        String imageFolderPath = Environment.getExternalStorageDirectory().getPath() + "/wanglin/";
        File dirFile = new File(imageFolderPath);
        if(!dirFile.exists()){
            dirFile.mkdir();
        }
        File myCaptureFile = new File(imageFolderPath + fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
        Message message = new Message();
        message.what = -333;
        message.obj = "保存成功！";
        handler.sendMessage(message);
        //刷新本地多媒体数据库
        OtherUtil.requestScanFile(context, myCaptureFile);
    }

    private byte[] getImage(String path) throws Exception{
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        InputStream inStream = conn.getInputStream();
        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
            return readStream(inStream);
        }
        return null;
    }

    private byte[] readStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while( (len=inStream.read(buffer)) != -1){
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }
}
