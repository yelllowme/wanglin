package com.wanglinkeji.wanglin.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.model.HousingEstateModel;
import com.wanglinkeji.wanglin.model.PhotoModel;
import com.wanglinkeji.wanglin.model.UserIdentityInfoModel;
import com.wanglinkeji.wanglin.util.DBUtil;
import com.wanglinkeji.wanglin.util.HttpUtil;
import com.wanglinkeji.wanglin.util.OtherUtil;
import com.wanglinkeji.wanglin.util.WanglinHttpResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/6.
 * 上传认证身份图片Activity
 */
public class Register_UploadIdentityImageActivity extends Activity implements View.OnClickListener {

    private ImageView imageView_cancle,imageView_IDCard_front, imageView_IDCard_reserveSide, imageView_HouseProprietaryCertificate, imageView_RentContract;

    private TextView textView_finish, textView_whyUpload;

    private LinearLayout layout_IDCard_front_notFinish, layout_IDCard_reserveSide_notFinish, layout_HouseProprietaryCertificate_notFinish, layout_RentContract_notFinish;

    private RelativeLayout layout_IDCard_front_finish, layout_IDCard_reserveSide_finish, layout_HouseProprietaryCertificate_finish, layout_RentContract_finish;

    private PopupWindow loading_page;

    private View rootView;

    //显示图片的配置
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.shape_rectangle_gray_noborder_allcorner)
            .showImageOnFail(R.drawable.shape_rectangle_gray_noborder_allcorner)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    //绑定小区住房的ID
    private int houseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_activity_upload_indentity_image);

        /**
         * 获取要上传图片的住房ID，如果获取失败则不能上传图片
         */
        houseId = getIntent().getIntExtra("houseId", -1);
        Log.d("yellow_bindingHouseId", "houseId = " + houseId);
        if (houseId < 0){
            Toast.makeText(Register_UploadIdentityImageActivity.this, "获取住房信息失败，请反馈！", Toast.LENGTH_SHORT).show();
            Register_UploadIdentityImageActivity.this.finish();
        }

        viewInit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            switch (requestCode){
                //上传身份证正面
                case OtherUtil.REQUEST_CODE_UPLOAD_INDENTITY_IMAGE_TO_CHOOSED_PHOTO_ID_CARD_FRONT:{
                    loading_page.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                    if (PhotoModel.list_choosedPhotos.size() > 0){
                        String path = PhotoModel.list_choosedPhotos.get(0).getPath();
                        RequestParams requestParams = new RequestParams();
                        requestParams.addBodyParameter("File", new File(path));
                        HttpUtil.addXUtilHttpParams(requestParams, DBUtil.getLoginUser().getToken());
                        uploadImage(requestParams, HttpUtil.uploadIDCardFront_url, layout_IDCard_front_notFinish,
                                layout_IDCard_front_finish, imageView_IDCard_front);
                    }
                    break;
                }
                //上传身份证反面
                case OtherUtil.REQUEST_CODE_UPLOAD_INDENTITY_IMAGE_TO_CHOOSED_PHOTO_ID_CARD_REVERSE:{
                    loading_page.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                    if (PhotoModel.list_choosedPhotos.size() > 0){
                        String path = PhotoModel.list_choosedPhotos.get(0).getPath();
                        RequestParams requestParams = new RequestParams();
                        requestParams.addBodyParameter("File", new File(path));
                        HttpUtil.addXUtilHttpParams(requestParams, DBUtil.getLoginUser().getToken());
                        uploadImage(requestParams, HttpUtil.uploadIDCardResever_url, layout_IDCard_reserveSide_notFinish,
                                layout_IDCard_reserveSide_finish, imageView_IDCard_reserveSide);
                    }
                    break;
                }
                //上传房产证
                case OtherUtil.REQUEST_CODE_UPLOAD_INDENTITY_IMAGE_TO_CHOOSED_PHOTO_HOUSE_PROPRIETARY_CERTIFICATE:{
                    loading_page.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                    if (PhotoModel.list_choosedPhotos.size() > 0){
                        String path = PhotoModel.list_choosedPhotos.get(0).getPath();
                        RequestParams requestParams = new RequestParams();
                        requestParams.addBodyParameter("HouseId", String.valueOf(houseId));
                        requestParams.addBodyParameter("File", new File(path));
                        HttpUtil.addXUtilHttpParams(requestParams, DBUtil.getLoginUser().getToken());
                        uploadImage(requestParams, HttpUtil.uploadHouseProprietaryCertificate_url, layout_HouseProprietaryCertificate_notFinish,
                                layout_HouseProprietaryCertificate_finish, imageView_HouseProprietaryCertificate);
                    }
                    break;
                }
                //上传租赁合同
                case OtherUtil.REQUEST_CODE_UPLOAD_INDENTITY_IMAGE_TO_CHOOSED_PHOTO_RENT_CONTRACT:{
                    loading_page.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                    if (PhotoModel.list_choosedPhotos.size() > 0){
                        String path = PhotoModel.list_choosedPhotos.get(0).getPath();
                        RequestParams requestParams = new RequestParams();
                        requestParams.addBodyParameter("File", new File(path));
                        requestParams.addBodyParameter("HouseId", String.valueOf(houseId));
                        HttpUtil.addXUtilHttpParams(requestParams, DBUtil.getLoginUser().getToken());
                        uploadImage(requestParams, HttpUtil.uploadRentContract_url, layout_RentContract_notFinish, layout_RentContract_finish,
                                imageView_RentContract);
                    }
                    break;
                }
                default:
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        cancleEvent();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //返回
            case R.id.imageview_uploadIdentityImage_cancle:{
                cancleEvent();
                break;
            }
            //保存按钮（最终完成）
            case R.id.textview_uploadIdentityImage_finish:{
                Intent intent = new Intent(Register_UploadIdentityImageActivity.this, MainActivity.class);
                startActivity(intent);
                Register_UploadIdentityImageActivity.this.finish();
                break;
            }
            //为什么上传按钮
            case R.id.textview_uploadIdentityImage_whyUpload:{
                break;
            }
            //上传身份证正面
            case R.id.layout_uploadIdentityImage_uploadIDCardImage_front_finish:
            case R.id.layout_uploadIdentityImage_uploadIDCardImage_front_notFinish:{
                PhotoModel.photoCount = 1;
                PhotoModel.finishText = "上传";
                PhotoModel.list_choosedPhotos = new ArrayList<>();
                Intent intent = new Intent(Register_UploadIdentityImageActivity.this, ChoosedPhoto_SmallActivity.class);
                startActivityForResult(intent, OtherUtil.REQUEST_CODE_UPLOAD_INDENTITY_IMAGE_TO_CHOOSED_PHOTO_ID_CARD_FRONT);
                break;
            }
            //上传身份证背面
            case R.id.layout_uploadIdentityImage_uploadIDCardImage_reverseSide_finish:
            case R.id.layout_uploadIdentityImage_uploadIDCardImage_reverseSide_notFinish:{
                PhotoModel.photoCount = 1;
                PhotoModel.finishText = "上传";
                PhotoModel.list_choosedPhotos = new ArrayList<>();
                Intent intent = new Intent(Register_UploadIdentityImageActivity.this, ChoosedPhoto_SmallActivity.class);
                startActivityForResult(intent, OtherUtil.REQUEST_CODE_UPLOAD_INDENTITY_IMAGE_TO_CHOOSED_PHOTO_ID_CARD_REVERSE);
                break;
            }
            //上传房产证
            case R.id.layout_uploadIdentityImage_uploadHouseProprietaryCertificate_finish:
            case R.id.layout_uploadIdentityImage_uploadHouseProprietaryCertificate_notFinish:{
                PhotoModel.photoCount = 1;
                PhotoModel.finishText = "上传";
                PhotoModel.list_choosedPhotos = new ArrayList<>();
                Intent intent = new Intent(Register_UploadIdentityImageActivity.this, ChoosedPhoto_SmallActivity.class);
                startActivityForResult(intent, OtherUtil.REQUEST_CODE_UPLOAD_INDENTITY_IMAGE_TO_CHOOSED_PHOTO_HOUSE_PROPRIETARY_CERTIFICATE);
                break;
            }
            //上传租赁合同
            case R.id.layout_uploadIdentityImage_uploadRentContract_finish:
            case R.id.layout_uploadIdentityImage_uploadRentContract_notFinish:{
                PhotoModel.photoCount = 1;
                PhotoModel.finishText = "上传";
                PhotoModel.list_choosedPhotos = new ArrayList<>();
                Intent intent = new Intent(Register_UploadIdentityImageActivity.this, ChoosedPhoto_SmallActivity.class);
                startActivityForResult(intent, OtherUtil.REQUEST_CODE_UPLOAD_INDENTITY_IMAGE_TO_CHOOSED_PHOTO_RENT_CONTRACT);
                break;
            }
            default:
                break;
        }
    }

    private void viewInit(){
        loading_page = OtherUtil.getLoadingPage(Register_UploadIdentityImageActivity.this);
        rootView = LayoutInflater.from(Register_UploadIdentityImageActivity.this).inflate(R.layout.layout_activity_upload_indentity_image, null);
        imageView_cancle = (ImageView)findViewById(R.id.imageview_uploadIdentityImage_cancle);
        imageView_cancle.setOnClickListener(this);
        imageView_IDCard_front = (ImageView)findViewById(R.id.imageview_uploadIdentityImage_uploadIDCardImage_front_finish);
        imageView_IDCard_reserveSide = (ImageView)findViewById(R.id.imageview_uploadIdentityImage_uploadIDCardImage_reverseSide_finish);
        imageView_HouseProprietaryCertificate = (ImageView)findViewById(R.id.imageview_uploadIdentityImage_uploadHouseProprietaryCertificate_finish);
        imageView_RentContract = (ImageView)findViewById(R.id.imageview_uploadIdentityImage_uploadRentContract_finish);
        textView_finish = (TextView)findViewById(R.id.textview_uploadIdentityImage_finish);
        textView_finish.setOnClickListener(this);
        textView_whyUpload = (TextView)findViewById(R.id.textview_uploadIdentityImage_whyUpload);
        textView_whyUpload.setOnClickListener(this);
        layout_IDCard_front_notFinish = (LinearLayout)findViewById(R.id.layout_uploadIdentityImage_uploadIDCardImage_front_notFinish);
        layout_IDCard_front_notFinish.setOnClickListener(this);
        layout_IDCard_front_finish = (RelativeLayout)findViewById(R.id.layout_uploadIdentityImage_uploadIDCardImage_front_finish);
        layout_IDCard_front_finish.setOnClickListener(this);
        layout_IDCard_reserveSide_notFinish = (LinearLayout)findViewById(R.id.layout_uploadIdentityImage_uploadIDCardImage_reverseSide_notFinish);
        layout_IDCard_reserveSide_notFinish.setOnClickListener(this);
        layout_IDCard_reserveSide_finish = (RelativeLayout)findViewById(R.id.layout_uploadIdentityImage_uploadIDCardImage_reverseSide_finish);
        layout_IDCard_reserveSide_finish.setOnClickListener(this);
        layout_HouseProprietaryCertificate_notFinish = (LinearLayout)findViewById(R.id.layout_uploadIdentityImage_uploadHouseProprietaryCertificate_notFinish);
        layout_HouseProprietaryCertificate_notFinish.setOnClickListener(this);
        layout_HouseProprietaryCertificate_finish= (RelativeLayout)findViewById(R.id.layout_uploadIdentityImage_uploadHouseProprietaryCertificate_finish);
        layout_HouseProprietaryCertificate_finish.setOnClickListener(this);
        layout_RentContract_notFinish = (LinearLayout)findViewById(R.id.layout_uploadIdentityImage_uploadRentContract_notFinish);
        layout_RentContract_notFinish.setOnClickListener(this);
        layout_RentContract_finish = (RelativeLayout)findViewById(R.id.layout_uploadIdentityImage_uploadRentContract_finish);
        layout_RentContract_finish.setOnClickListener(this);
    }

    private void cancleEvent(){
        Intent intent = new Intent(Register_UploadIdentityImageActivity.this, MainActivity.class);
        startActivity(intent);
        Register_UploadIdentityImageActivity.this.finish();
    }

    private void uploadImage(RequestParams requestParams, String url, final LinearLayout layout_notFinish,
                             final RelativeLayout layout_finish, final ImageView imageview_photo){
        HttpUtils httpUtils = new HttpUtils();

        httpUtils.send(HttpRequest.HttpMethod.POST, url, requestParams, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.d("yellow_uploadImage", responseInfo.result);
                try {
                    JSONObject jsonObject_response = new JSONObject(responseInfo.result);
                    int requestCode = jsonObject_response.getInt("Code");
                    if (requestCode == 200){
                        layout_notFinish.setVisibility(View.GONE);
                        layout_finish.setVisibility(View.VISIBLE);
                        String imageUrl = jsonObject_response.getString("Data");
                        ImageLoader.getInstance().displayImage(imageUrl, imageview_photo, options);
                        loading_page.dismiss();
                    }else {
                        layout_notFinish.setVisibility(View.VISIBLE);
                        layout_finish.setVisibility(View.GONE);
                        String msg = jsonObject_response.getString("Msg");
                        HttpUtil.parseResponseCode(Register_UploadIdentityImageActivity.this, requestCode, msg);
                        loading_page.dismiss();
                    }
                } catch (JSONException e) {
                    layout_notFinish.setVisibility(View.VISIBLE);
                    layout_finish.setVisibility(View.GONE);
                    Toast.makeText(Register_UploadIdentityImageActivity.this, "解析返回数据失败，请反馈！", Toast.LENGTH_SHORT).show();
                    loading_page.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                e.printStackTrace();
                Toast.makeText(Register_UploadIdentityImageActivity.this, "操作失败，请稍后重试！", Toast.LENGTH_SHORT).show();
                loading_page.dismiss();
            }
        });
    }
}
