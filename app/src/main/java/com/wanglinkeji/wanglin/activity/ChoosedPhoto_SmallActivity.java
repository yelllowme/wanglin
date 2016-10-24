package com.wanglinkeji.wanglin.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.adapter.GridViewAdapter_ChoosedPhotoSmall;
import com.wanglinkeji.wanglin.adapter.ListViewAdapter_ChoosedPhoto_photoFloderList;
import com.wanglinkeji.wanglin.model.PhotoModel;
import com.wanglinkeji.wanglin.model.PhotofolderModel;
import com.wanglinkeji.wanglin.util.OtherUtil;
import com.wanglinkeji.wanglin.util.WangLinApplication;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Administrator on 2016/9/6.
 * 图片选择器，小图列表Activity
 */
public class ChoosedPhoto_SmallActivity extends Activity implements View.OnClickListener {

    //用来控制GridView是否显示拍照栏的全局变量
    public static boolean isShowTakePhoto = true;

    private ImageView imageView_cancle, imageView_popwindowTakePhoto_cancle, imageView_popwindowTakePhoto_photo;

    private TextView textView_finish, textView_showAlbum, textView_preview, textView_popwindowTakePhoto_finish;

    private LinearLayout layout_bottom, layout_title, layout_folderList, layout_popwindowTakePhoto_title;

    private GridView gridView_photos;

    private ListView listView_photoFolder;

    private ListViewAdapter_ChoosedPhoto_photoFloderList listViewAdapter_choosedPhoto_photoFloderList;

    private GridViewAdapter_ChoosedPhotoSmall gridViewAdapter_choosedPhotoSmall;

    private PopupWindow popupWindow_takePhoto;

    private View rootView, popwindowTakePhoto_contentView;

    //相册layout是否显示
    private boolean isShowAlbum = false;

    //拍照后popwindow是否显示title
    private boolean isShowPopwindowTakePhotoTitle = true;

    //显示图片的配置
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.shape_rectangle_black_lighter_noborder_nocorner)
            .showImageOnFail(R.drawable.shape_rectangle_gray_noborder_nocorner)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                //保证相册List消失动画后，相册layout才GONE
                case -666:{
                    layout_folderList.setVisibility(View.GONE);
                    break;
                }
                //拍照后的popwindow标题栏消失
                case -777:{
                    layout_popwindowTakePhoto_title.setVisibility(View.GONE);
                }
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_activity_choosed_photo_small);

        getallPhoto();
        //刚开始时，显示的就是所有图片
        PhotoModel.list_showPhotos = PhotoModel.list_allPhotos;
        getPhotoFloder();
        viewInit();
        setViewAftergetPhoto();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            //从浏览大图返回
            case OtherUtil.REQUEST_CODE_CHOOSED_PHOTO_SMALL_TO_BIG:{
                if (resultCode == RESULT_OK){
                    //判断返回时按下的哪一个按钮，cancle还是ensure
                    if (data.getStringExtra("fromWhat").equals("cancle")) {
                        //设置numOfChoosed的显示数量
                        setViewAftergetPhoto();
                        //根据allPhoto，刷新showPhoto
                        for (int i = 0; i < PhotoModel.list_allPhotos.size(); i++) {
                            for (int j = 0; j < PhotoModel.list_showPhotos.size(); j++) {
                                if (PhotoModel.list_showPhotos.get(j).getPath().equals(PhotoModel.list_allPhotos.get(i).getPath())) {
                                    PhotoModel.list_showPhotos.get(j).setChoosed(PhotoModel.list_allPhotos.get(i).isChoosed());
                                }
                            }
                        }
                        //手动刷新GridView的选中图标,就不用notify GridView
                        //如果GridView没有显示拍照栏
                        if (isShowTakePhoto == false){
                            for (int i = 0; i < PhotoModel.list_showPhotos.size(); i++){
                                if (i >= gridView_photos.getFirstVisiblePosition() && i < gridView_photos.getFirstVisiblePosition() + gridView_photos.getChildCount()){
                                    if (PhotoModel.list_showPhotos.get(i).isChoosed() == true ){
                                        View view = (View)gridView_photos.getChildAt(i - gridView_photos.getFirstVisiblePosition());
                                        ImageView image = (ImageView)view.findViewById(R.id.imageview_choosedPhotoSmalItem_isChoosed);
                                        image.setImageResource(R.mipmap.photo_choosed_icon);
                                    }else if(PhotoModel.list_showPhotos.get(i).isChoosed() == false){
                                        View view = (View)gridView_photos.getChildAt(i - gridView_photos.getFirstVisiblePosition());
                                        ImageView image = (ImageView)view.findViewById(R.id.imageview_choosedPhotoSmalItem_isChoosed);
                                        image.setImageResource(R.mipmap.photo_notchoosed_icon);
                                    }
                                }
                            }
                            //如果GridView显示了拍照栏
                        }else {
                            for (int gridview_position = 0; gridview_position < PhotoModel.list_showPhotos.size() + 1; gridview_position++){
                                if (gridview_position > 0 && gridview_position >= gridView_photos.getFirstVisiblePosition() &&
                                        gridview_position < gridView_photos.getFirstVisiblePosition() + gridView_photos.getChildCount()){
                                    int photo_position = gridview_position - 1;
                                    if (PhotoModel.list_showPhotos.get(photo_position).isChoosed() == true ){
                                        View view = (View)gridView_photos.getChildAt(gridview_position - gridView_photos.getFirstVisiblePosition());
                                        ImageView image = (ImageView)view.findViewById(R.id.imageview_choosedPhotoSmalItem_isChoosed);
                                        image.setImageResource(R.mipmap.photo_choosed_icon);
                                    }else if(PhotoModel.list_showPhotos.get(photo_position).isChoosed() == false){
                                        View view = (View)gridView_photos.getChildAt(gridview_position - gridView_photos.getFirstVisiblePosition());
                                        ImageView image = (ImageView)view.findViewById(R.id.imageview_choosedPhotoSmalItem_isChoosed);
                                        image.setImageResource(R.mipmap.photo_notchoosed_icon);
                                    }
                                }
                            }
                        }
                        //如果是点击的确定，则直接finish
                    }else if(data.getStringExtra("fromWhat").equals("ensure")){
                        finishBtnEvent();
                    }
                }
                break;
            }
            //从拍照返回
            case OtherUtil.REQUEST_CODE_CHOODEDPHOTO_SMALL_TO_TAKE_PHOTO:{
                File file = new File(PhotoModel.takePhoto_path);
                if (file.length() > 0){
                    OtherUtil.requestScanFile(this, file);
                    //用ImageLoader显示照片
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    imageLoader.displayImage(ImageDownloader.Scheme.FILE.wrap(PhotoModel.takePhoto_path), imageView_popwindowTakePhoto_photo,options);
                    popupWindow_takePhoto.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //返回按钮
            case R.id.imageview_choosedPhotoSmall_cancle:{
                ChoosedPhoto_SmallActivity.this.finish();
                break;
            }
            //完成按钮
            case R.id.textview_choosedPhotoSmall_finish:{
                finishBtnEvent();
                break;
            }
            //打开相册按钮
            case R.id.textview_choosedPhotoSmall_showAlbum:{
                if (isShowAlbum == true){
                    folderListGoneAnim();
                }else {
                    folderListVisiableAnim();
                }
                break;
            }
            //预览按钮
            case R.id.textview_choosedPhotoSmall_preview:{
                if (PhotoModel.getNumChoosedPhoto(PhotoModel.list_allPhotos) > 0) {
                    ArrayList<PhotoModel> photos = new ArrayList<PhotoModel>();
                    for (int i = 0; i < PhotoModel.list_allPhotos.size(); i++) {
                        if (PhotoModel.list_allPhotos.get(i).isChoosed() == true) {
                            PhotoModel photo = new PhotoModel();
                            photo.setPath(PhotoModel.list_allPhotos.get(i).getPath());
                            photo.setChoosed(PhotoModel.list_allPhotos.get(i).isChoosed());
                            photos.add(photo);
                        }
                    }
                    PhotoModel.list_bigPhotos = photos;
                    Intent intent = new Intent(ChoosedPhoto_SmallActivity.this, ChoosedPhoto_BigActivity.class);
                    startActivityForResult(intent, OtherUtil.REQUEST_CODE_CHOOSED_PHOTO_SMALL_TO_BIG);
                }
                break;
            }
            //相册layout
            case R.id.layout_choosedPhoto_Small_folderList:{
                folderListGoneAnim();
                break;
            }
            //拍照后的popwindow返回按钮
            case R.id.imageview_choosedPhotoSmall_popwindowTakePhoto_cancle:{
                popupWindow_takePhoto.dismiss();
                break;
            }
            //拍照后的popwindow完成按钮
            case R.id.textview_choosedPhotoSmall_popwindowTakePhoto_finish:{
                PhotoModel photoModel = new PhotoModel();
                photoModel.setChoosed(true);
                photoModel.setPath(PhotoModel.takePhoto_path);
                PhotoModel.list_choosedPhotos.add(photoModel);
                break;
            }
            //拍照后的popwindow照片
            case R.id.imageview_choosedPhotoSmall_popwindowTakePhoto_photo:{
                if (isShowPopwindowTakePhotoTitle == true){
                    isShowPopwindowTakePhotoTitle = false;
                    layout_popwindowTakePhoto_title.startAnimation(OtherUtil.get_Translate_Anim(400, 0, 0, 0, -WangLinApplication.screen_Height/12.5f));
                    handler.sendEmptyMessageDelayed(-777, 400);
                }else {
                    isShowPopwindowTakePhotoTitle = true;
                    layout_popwindowTakePhoto_title.setVisibility(View.VISIBLE);
                    layout_popwindowTakePhoto_title.startAnimation(OtherUtil.get_Translate_Anim(400, 0, 0, -WangLinApplication.screen_Height/12.5f, 0));
                }
                break;
            }
            default:
                break;
        }
    }

    private void viewInit(){
        //默认显示全部图片时要显示“拍照”栏
        isShowTakePhoto = true;
        /**
         * 相片小图界面控件
         */
        imageView_cancle = (ImageView)findViewById(R.id.imageview_choosedPhotoSmall_cancle);
        imageView_cancle.setOnClickListener(this);
        textView_finish = (TextView)findViewById(R.id.textview_choosedPhotoSmall_finish);
        textView_finish.setOnClickListener(this);
        textView_finish.setText(PhotoModel.finishText);
        textView_showAlbum = (TextView)findViewById(R.id.textview_choosedPhotoSmall_showAlbum);
        textView_showAlbum.setOnClickListener(this);
        textView_preview = (TextView)findViewById(R.id.textview_choosedPhotoSmall_preview);
        textView_preview.setOnClickListener(this);
        layout_bottom = (LinearLayout)findViewById(R.id.layout_choosedPhoto_Small_bottom);
        layout_bottom.setOnClickListener(this);
        OtherUtil.setViewLayoutParams(layout_bottom, false, 12.5f, 1);
        layout_title = (LinearLayout)findViewById(R.id.layout_choosedPhoto_Small_title);
        OtherUtil.setViewLayoutParams(layout_title, false, 12.5f, 1);
        gridView_photos = (GridView)findViewById(R.id.gridview_choosedPhoto_small);
        gridViewAdapter_choosedPhotoSmall = new GridViewAdapter_ChoosedPhotoSmall(PhotoModel.list_showPhotos, ChoosedPhoto_SmallActivity.this,
                R.layout.layout_gridview_item_choosed_photo_small, textView_finish, textView_preview);
        gridView_photos.setAdapter(gridViewAdapter_choosedPhotoSmall);

        /**
         * 相片相册选择界面
         */
        layout_folderList = (LinearLayout)findViewById(R.id.layout_choosedPhoto_Small_folderList);
        layout_folderList.setOnClickListener(this);
        OtherUtil.setViewLayoutParams(layout_folderList, false, 1.19f, 1);
        listView_photoFolder = (ListView)findViewById(R.id.listview_choosedPhoto_photoFloderList);
        OtherUtil.setViewLayoutParams(listView_photoFolder, false, 1.5f, 1);
        listViewAdapter_choosedPhoto_photoFloderList = new ListViewAdapter_ChoosedPhoto_photoFloderList(PhotofolderModel.list_photoFolder, ChoosedPhoto_SmallActivity.this,
                R.layout.layout_listview_item_choosed_photo_folder_list);
        listView_photoFolder.setAdapter(listViewAdapter_choosedPhoto_photoFloderList);
        listView_photoFolder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                int clickPosition = (new Long(id)).intValue();
                textView_showAlbum.setText(PhotofolderModel.list_photoFolder.get(clickPosition).getName());
                for (int i = 0; i < PhotofolderModel.list_photoFolder.size(); i++){
                    if (i == clickPosition){
                        PhotofolderModel.list_photoFolder.get(i).setChoosed(true);
                    }else {
                        PhotofolderModel.list_photoFolder.get(i).setChoosed(false);
                    }
                }
                listViewAdapter_choosedPhoto_photoFloderList.notifyDataSetChanged();
                if (clickPosition == 0){
                    //只有在显示全部图片的时候才会有拍照栏
                    isShowTakePhoto = true;
                    PhotoModel.list_showPhotos = PhotoModel.list_allPhotos;
                    gridViewAdapter_choosedPhotoSmall = new GridViewAdapter_ChoosedPhotoSmall(PhotoModel.list_showPhotos, ChoosedPhoto_SmallActivity.this,
                            R.layout.layout_gridview_item_choosed_photo_small, textView_finish, textView_preview);
                    gridView_photos.setAdapter(gridViewAdapter_choosedPhotoSmall);
                }else {
                    //只有在显示全部图片的时候才会有拍照栏
                    isShowTakePhoto = false;
                    PhotoModel.list_showPhotos = OtherUtil.getPicturesByFolderPath(PhotofolderModel.list_photoFolder.get(clickPosition).getDir());
                    gridViewAdapter_choosedPhotoSmall = new GridViewAdapter_ChoosedPhotoSmall(PhotoModel.list_showPhotos, ChoosedPhoto_SmallActivity.this,
                            R.layout.layout_gridview_item_choosed_photo_small, textView_finish, textView_preview);
                    gridView_photos.setAdapter(gridViewAdapter_choosedPhotoSmall);
                }

                folderListGoneAnim();
            }
        });
        /**
         * 拍照后的图片显示Popwindow
         */
        rootView = LayoutInflater.from(ChoosedPhoto_SmallActivity.this).inflate(R.layout.layout_activity_choosed_photo_small, null);
        popwindowTakePhoto_contentView = LayoutInflater.from(ChoosedPhoto_SmallActivity.this).inflate(R.layout.layout_popwindow_takephoto_showphoto, null);
        popupWindow_takePhoto = new PopupWindow(popwindowTakePhoto_contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        imageView_popwindowTakePhoto_cancle = (ImageView)popwindowTakePhoto_contentView.findViewById(R.id.imageview_choosedPhotoSmall_popwindowTakePhoto_cancle);
        imageView_popwindowTakePhoto_cancle.setOnClickListener(this);
        imageView_popwindowTakePhoto_photo = (ImageView)popwindowTakePhoto_contentView.findViewById(R.id.imageview_choosedPhotoSmall_popwindowTakePhoto_photo);
        imageView_popwindowTakePhoto_photo.setOnClickListener(this);
        textView_popwindowTakePhoto_finish = (TextView)popwindowTakePhoto_contentView.findViewById(R.id.textview_choosedPhotoSmall_popwindowTakePhoto_finish);
        textView_popwindowTakePhoto_finish.setOnClickListener(this);
        layout_popwindowTakePhoto_title = (LinearLayout)popwindowTakePhoto_contentView.findViewById(R.id.layout_choosedPhotoSmall_popwindowTakePhoto_title);
    }

    private void finishBtnEvent(){
        List<PhotoModel> list_photos = new ArrayList<>();
        for (int i = 0; i < PhotoModel.list_allPhotos.size(); i++){
            if (PhotoModel.list_allPhotos.get(i).isChoosed() == true){
                PhotoModel photoModel = new PhotoModel();
                photoModel.setChoosed(true);
                photoModel.setPath(PhotoModel.list_allPhotos.get(i).getPath());
                list_photos.add(photoModel);
            }
        }
        PhotoModel.list_choosedPhotos = list_photos;
        setResult(RESULT_OK);
        ChoosedPhoto_SmallActivity.this.finish();
    }

    //在扫描图片完成后设置控件
    private void setViewAftergetPhoto(){
        if (PhotoModel.getNumChoosedPhoto(PhotoModel.list_allPhotos) == 0){
            //完成按钮
            textView_finish.setTextColor(0X99FFFFFF);
            textView_finish.setBackgroundResource(R.drawable.shape_rectangle_blue_lighter_er_noborder_allcorner);
            textView_finish.setText(PhotoModel.finishText);
            textView_finish.setEnabled(false);
            //预览按钮
            textView_preview.setText("预览");
            textView_preview.setTextColor(0X99FFFFFF);
            textView_preview.setEnabled(false);
        }else {
            //完成按钮
            textView_finish.setTextColor(0XFFFFFFFF);
            textView_finish.setBackgroundResource(R.drawable.shape_rectangle_blue_lighter_noborder_allcorner);
            String text = "(" + PhotoModel.getNumChoosedPhoto(PhotoModel.list_allPhotos) + "/" + PhotoModel.photoCount + ")";
            textView_finish.setText(PhotoModel.finishText + text);
            textView_finish.setEnabled(true);
            //预览按钮
            textView_preview.setText("预览" + text);
            textView_preview.setTextColor(0XFFFFFFFF);
            textView_preview.setEnabled(true);
        }
    }

    //相册layout消失动画+属性设置
    private void folderListGoneAnim(){
        isShowAlbum = false;
        listView_photoFolder.startAnimation(OtherUtil.get_Translate_Anim(300, 0, 0, 0, WangLinApplication.screen_Height/1.2f));
        handler.sendEmptyMessageDelayed(-666, 300);
    }

    //相册layout消失动画+属性设置
    private void folderListVisiableAnim(){
        isShowAlbum = true;
        layout_folderList.setVisibility(View.VISIBLE);
        listView_photoFolder.startAnimation(OtherUtil.get_Translate_Anim(300, 0, 0, WangLinApplication.screen_Height/1.2f, 0));
    }

    //扫描本地相册，获取所有图片
    private void getallPhoto(){
        List<PhotoModel> list_photos = new ArrayList<PhotoModel>();
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
            return;
        }
        Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = ChoosedPhoto_SmallActivity.this.getContentResolver();
        Cursor cursor = contentResolver.query(imageUri, null, MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED);
        while (cursor.moveToNext()){
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            PhotoModel photo = new PhotoModel();
            photo.setPath(path);
            photo.setChoosed(false);
            list_photos.add(photo);
        }
        if (!cursor.isClosed()){
            cursor.close();
        }
        Collections.reverse(list_photos);
        PhotoModel.list_allPhotos = list_photos;
        for (int i = 0; i < PhotoModel.list_choosedPhotos.size(); i++){
            for (int j = 0; j < PhotoModel.list_allPhotos.size(); j++){
                if (PhotoModel.list_allPhotos.get(j).getPath().equals(PhotoModel.list_choosedPhotos.get(i).getPath())){
                    PhotoModel.list_allPhotos.get(j).setChoosed(true);
                }
            }
        }
    }

    //扫描SDcard获取所有图片文件夹
    private void getPhotoFloder(){
        PhotofolderModel.list_photoFolder = new ArrayList<>();
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
            return;
        }
        String firstImagePath_all = null;
        HashSet<String> mDirPaths = new HashSet<String>();
        int totalCount = 0;

        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = ChoosedPhoto_SmallActivity.this.getContentResolver();

        // 只查询jpeg和png的图片
        Cursor mCursor = mContentResolver.query(mImageUri, null, MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED);

        PhotofolderModel allPhotoFloder = new PhotofolderModel();

        while (mCursor.moveToNext()) {
            // 获取图片的路径
            String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));

            // 拿到第一张图片的路径
            if (firstImagePath_all == null){
                firstImagePath_all = path;
            }
            // 获取该图片的父路径名
            File parentFile = new File(path).getParentFile();
            if (parentFile == null){
                continue;
            }
            String dirPath = parentFile.getAbsolutePath();
            PhotofolderModel photoFloder = null;
            // 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
            if (mDirPaths.contains(dirPath)) {
                continue;
            } else {
                mDirPaths.add(dirPath);
                // 初始化imageFloder
                photoFloder = new PhotofolderModel();
                photoFloder.setDir(dirPath);
                photoFloder.setFirstImagePath(path);
            }

            int picSize = parentFile.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    if (filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg")){
                        return true;
                    }
                    return false;
                }
            }).length;
            totalCount += picSize;

            photoFloder.setCount(picSize);
            photoFloder.setCount_choosed(0);
            photoFloder.setChoosed(false);
            PhotofolderModel.list_photoFolder.add(photoFloder);
        }
        //将相册倒序
        Collections.reverse(PhotofolderModel.list_photoFolder);

        //加入所有图片选项
        allPhotoFloder.setCount(totalCount);
        allPhotoFloder.setFirstImagePath(firstImagePath_all);
        allPhotoFloder.setDir("/所有图片");
        allPhotoFloder.setCount_choosed(0);
        allPhotoFloder.setChoosed(true);
        PhotofolderModel.list_photoFolder.add(0,allPhotoFloder);
        mCursor.close();
        // 扫描完成，辅助的HashSet也就可以释放内存了
        mDirPaths = null;
    }
}
