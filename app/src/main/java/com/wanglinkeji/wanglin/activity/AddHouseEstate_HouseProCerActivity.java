package com.wanglinkeji.wanglin.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.model.HousingEstateModel;
import com.wanglinkeji.wanglin.model.PhotoModel;
import com.wanglinkeji.wanglin.util.DBUtil;
import com.wanglinkeji.wanglin.util.HttpUtil;
import com.wanglinkeji.wanglin.util.OtherUtil;
import com.wanglinkeji.wanglin.util.WangLinApplication;
import com.wanglinkeji.wanglin.util.WanglinHttpResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/19.
 * 新增绑定小区Activity
 */
public class AddHouseEstate_HouseProCerActivity extends Activity implements View.OnClickListener {

    public static final int WHAT_FROM_ADD = 1, WHAT_FROM_HOUSE_ESTATE = 2;

    private ImageView imageView_cancle, imageView_search, imageView_rentContract, imageView_whatIdentity_proprietor, imageView_whatIdentity_tentment;

    private EditText editText_searchText, editText_blockNum, editText_unitNum, editText_floorNum, editText_roomNum;

    private TextView textView_finish;

    private LinearLayout layout_uploadImage_notFinish, layout_uploadImage_finish, layout_whatIdentity_proprietor, layout_whatIdentity_tentment;

    private PopupWindow loading_page;

    private View rootView;

    //选择的附近小区ID
    private int houseEstateId = -2747;

    //绑定后的小区ID
    private int bindingHousedEstateId;

    //栋和单元数
    private int blockNum, unitNum;

    //身份选择：true—业主，false—租客
    private boolean isChosoedProprietor = true;

    //显示图片的配置
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.shape_rectangle_gray_noborder_allcorner)
            .showImageOnFail(R.drawable.shape_rectangle_gray_noborder_allcorner)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_activity_add_house_estate);

        //这里让选中图片List为空，避免有未清空的图片上传作为房产证照片
        PhotoModel.list_choosedPhotos = new ArrayList<>();
        viewInit();
        setViewAfterGetData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case OtherUtil.REQUEST_CODE_ADD_HOUSE_ESTATE_TO_SEARCH_HOUSE_ESTATE:{
                if (resultCode == RESULT_OK){
                    editText_searchText.setText(data.getStringExtra("houseEstateName"));
                    houseEstateId = data.getIntExtra("houseEstateId", -2747);
                }
                break;
            }
            case OtherUtil.REQUEST_CODE_ADD_HOUSE_ESTATE_TO_CHOODED_PHOTO:{
                if (resultCode == RESULT_OK){
                    if (PhotoModel.list_choosedPhotos.size() > 0){
                        layout_uploadImage_finish.setVisibility(View.VISIBLE);
                        layout_uploadImage_notFinish.setVisibility(View.GONE);
                        String path = PhotoModel.list_choosedPhotos.get(0).getPath();
                        //用ImageLoader显示照片
                        ImageLoader imageLoader = ImageLoader.getInstance();
                        imageLoader.displayImage(ImageDownloader.Scheme.FILE.wrap(path), imageView_rentContract);
                    }else {
                        layout_uploadImage_finish.setVisibility(View.GONE);
                        layout_uploadImage_notFinish.setVisibility(View.VISIBLE);
                    }
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
            case R.id.imageview_addHouseEstate_cancle:{
                AddHouseEstate_HouseProCerActivity.this.finish();
                break;
            }
            //选择上传房产证
            case R.id.layout_addHouseEstate_whatidentity_proprietor:{
                isChosoedProprietor = true;
                imageView_whatIdentity_proprietor.setBackgroundResource(R.drawable.shape_circle_blue);
                imageView_whatIdentity_tentment.setBackgroundResource(R.drawable.shape_circle_gray);
                break;
            }
            //选择上传租赁合同
            case R.id.layout_addHouseEstate_whatidentity_tentment:{
                isChosoedProprietor = false;
                imageView_whatIdentity_proprietor.setBackgroundResource(R.drawable.shape_circle_gray);
                imageView_whatIdentity_tentment.setBackgroundResource(R.drawable.shape_circle_blue);
                break;
            }
            //搜索按钮
            case R.id.imageview_addHouseEstate_seatchIcon:{
                searchBtnEvent();
                break;
            }
            //添加照片按钮
            case R.id.layout_addHouseEstate_finish_image:
            case R.id.layout_addHouseEstate_notfinish_image:{
                PhotoModel.photoCount = 1;
                PhotoModel.finishText = "上传";
                PhotoModel.list_choosedPhotos = new ArrayList<>();
                Intent intent = new Intent(AddHouseEstate_HouseProCerActivity.this, ChoosedPhoto_SmallActivity.class);
                startActivityForResult(intent, OtherUtil.REQUEST_CODE_ADD_HOUSE_ESTATE_TO_CHOODED_PHOTO);
                break;
            }
            default:
                break;
        }
    }

    private void viewInit(){
        loading_page = OtherUtil.getLoadingPage(AddHouseEstate_HouseProCerActivity.this);
        rootView = LayoutInflater.from(AddHouseEstate_HouseProCerActivity.this).inflate(R.layout.layout_activity_add_house_estate, null);
        imageView_cancle = (ImageView)findViewById(R.id.imageview_addHouseEstate_cancle);
        imageView_cancle.setOnClickListener(this);
        imageView_rentContract = (ImageView)findViewById(R.id.imageview_addHouseEstate_rentContractImage);
        layout_whatIdentity_proprietor = (LinearLayout)findViewById(R.id.layout_addHouseEstate_whatidentity_proprietor);
        layout_whatIdentity_proprietor.setOnClickListener(this);
        layout_whatIdentity_tentment = (LinearLayout)findViewById(R.id.layout_addHouseEstate_whatidentity_tentment);
        layout_whatIdentity_tentment.setOnClickListener(this);
        imageView_whatIdentity_proprietor = (ImageView)findViewById(R.id.imageview_addHouseEstate_whatIdentity_proprietor);
        imageView_whatIdentity_tentment = (ImageView)findViewById(R.id.imageview_addHouseEstate_whatIdentity_tentment);
        if (isChosoedProprietor == true){
            imageView_whatIdentity_proprietor.setBackgroundResource(R.drawable.shape_circle_blue);
            imageView_whatIdentity_tentment.setBackgroundResource(R.drawable.shape_circle_gray);
        }else {
            imageView_whatIdentity_proprietor.setBackgroundResource(R.drawable.shape_circle_gray);
            imageView_whatIdentity_tentment.setBackgroundResource(R.drawable.shape_circle_blue);
        }
        imageView_search = (ImageView)findViewById(R.id.imageview_addHouseEstate_seatchIcon);
        imageView_search.setOnClickListener(this);
        textView_finish = (TextView)findViewById(R.id.textview_addhouseEstate_finish);
        layout_uploadImage_finish = (LinearLayout)findViewById(R.id.layout_addHouseEstate_finish_image);
        layout_uploadImage_finish.setOnClickListener(this);
        layout_uploadImage_notFinish = (LinearLayout)findViewById(R.id.layout_addHouseEstate_notfinish_image);
        layout_uploadImage_notFinish.setOnClickListener(this);
        editText_blockNum = (EditText)findViewById(R.id.edittext_addHouseEstate_blockNum);
        editText_unitNum = (EditText)findViewById(R.id.edittext_addHouseEstate_unitNum);
        editText_floorNum = (EditText)findViewById(R.id.edittext_addHouseEstate_floorNum);
        editText_roomNum = (EditText)findViewById(R.id.edittext_addHouseEstate_roomNum);
        editText_searchText = (EditText)findViewById(R.id.edittext_addHouseEstate_searchText);
        editText_searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                searchBtnEvent();
                return true;
            }
        });
    }

    private void searchBtnEvent(){
        if (editText_searchText.getText().length() == 0){
            Toast.makeText(AddHouseEstate_HouseProCerActivity.this, "搜索内容不能为空哦！", Toast.LENGTH_SHORT).show();
        }else {
            loading_page.showAtLocation(rootView, Gravity.CENTER, 0, 0);
            searchHouseEstateByKeyWord(editText_searchText.getText().toString());
            //收起软键盘
            ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editText_searchText.getWindowToken(), 0);
        }
    }

    //提交审核按钮事件
    private void addOperationFinishEvent(String url){
        if (houseEstateId == -2747){
            Toast.makeText(AddHouseEstate_HouseProCerActivity.this, "请搜索并选择绑定小区！", Toast.LENGTH_SHORT).show();
        }else {
            if (editText_blockNum.getText().length() == 0 && editText_unitNum.getText().length() == 0){
                Toast.makeText(AddHouseEstate_HouseProCerActivity.this, "栋和单元请至少填一个哦！", Toast.LENGTH_SHORT).show();
            }else {
                if (editText_blockNum.getText().length() == 0){
                    blockNum = -1;
                }else {
                    blockNum= Integer.valueOf(editText_blockNum.getText().toString());
                }
                if (editText_unitNum.getText().length() == 0){
                    unitNum = -1;
                }else {
                    unitNum = Integer.valueOf(editText_unitNum.getText().toString());
                }
                if (editText_floorNum.getText().length() == 0){
                    Toast.makeText(AddHouseEstate_HouseProCerActivity.this, "楼数不能为空哦！", Toast.LENGTH_SHORT).show();
                }else {
                    if (editText_roomNum.getText().length() == 0){
                        Toast.makeText(AddHouseEstate_HouseProCerActivity.this, "房号不能为空！", Toast.LENGTH_SHORT).show();
                    }else {
                        loading_page.showAtLocation(rootView, Gravity.CENTER, 0 ,0);
                        bindingHouseEstate(url, houseEstateId, blockNum, unitNum, Integer.valueOf(editText_floorNum.getText().toString()),
                                editText_roomNum.getText().toString());
                    }
                }
            }
        }
    }

    private void setViewAfterGetData(){
        int fromWhat = getIntent().getIntExtra("whatFrom", WHAT_FROM_ADD);
        //来源是：添加按钮
        if (fromWhat == WHAT_FROM_ADD){
            layout_uploadImage_notFinish.setVisibility(View.VISIBLE);
            layout_uploadImage_finish.setVisibility(View.GONE);
            editText_searchText.setEnabled(true);
            imageView_search.setEnabled(true);
            editText_blockNum.setEnabled(true);
            editText_unitNum.setEnabled(true);
            editText_floorNum.setEnabled(true);
            editText_roomNum.setEnabled(true);
            textView_finish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url;
                    if (isChosoedProprietor == true){
                        url = HttpUtil.uploadHouseProprietaryCertificate_url;
                    }else {
                        url = HttpUtil.uploadRentContract_url;
                    }
                    addOperationFinishEvent(url);
                }
            });
        //来源是小区卡片点击
        }else if (fromWhat == WHAT_FROM_HOUSE_ESTATE){
            int blockNum = getIntent().getIntExtra("blockNum", -10);
            int unitNum = getIntent().getIntExtra("unitNum", -10);
            int floorNum = getIntent().getIntExtra("floorNum", -10);
            String roomNum = getIntent().getStringExtra("roomNum");
            String houseEstateName = getIntent().getStringExtra("houseEstateName");
            final String pictureUrl = getIntent().getStringExtra("imageUrl");
            final int getHouseId = getIntent().getIntExtra("houseId", -2747);
            editText_searchText.setEnabled(false);
            imageView_search.setEnabled(false);
            editText_blockNum.setEnabled(false);
            editText_unitNum.setEnabled(false);
            editText_floorNum.setEnabled(false);
            editText_roomNum.setEnabled(false);
            editText_searchText.setText(houseEstateName);
            if (Integer.valueOf(blockNum) <= 0){
                editText_blockNum.setText("");
            }else {
                editText_blockNum.setText(String.valueOf(blockNum));
            }
            if (Integer.valueOf(unitNum) <= 0){
                editText_unitNum.setText("");
            }else {
                editText_unitNum.setText(String.valueOf(unitNum));
            }
            editText_floorNum.setText(String.valueOf(floorNum));
            editText_roomNum.setText(roomNum);
            //身份为业主或者租户时，说明上传过房产证或租赁合同
            if (pictureUrl != null && !pictureUrl.equals("") && !pictureUrl.equals("null")){
                layout_uploadImage_finish.setVisibility(View.VISIBLE);
                layout_uploadImage_notFinish.setVisibility(View.GONE);
                ImageLoader.getInstance().displayImage(pictureUrl, imageView_rentContract, options);
                textView_finish.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (PhotoModel.list_choosedPhotos.size() == 0){
                            Toast.makeText(AddHouseEstate_HouseProCerActivity.this, "请选择图片！", Toast.LENGTH_SHORT).show();
                        }else {
                            String path = PhotoModel.list_choosedPhotos.get(0).getPath();
                            RequestParams requestParams = new RequestParams();
                            requestParams.addBodyParameter("File", new File(path));
                            requestParams.addBodyParameter("HouseId", String.valueOf(getHouseId));
                            HttpUtil.addXUtilHttpParams(requestParams, DBUtil.getLoginUser().getToken());
                            String url;
                            if (isChosoedProprietor == true){
                                url = HttpUtil.uploadHouseProprietaryCertificate_url;
                            }else {
                                url = HttpUtil.uploadRentContract_url;
                            }
                            loading_page.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                            updateImage(requestParams, url,imageView_rentContract, pictureUrl);
                        }
                    }
                });
            //身份是未知时：说明没有上传过房产证或租赁合同
            }else {
                layout_uploadImage_finish.setVisibility(View.GONE);
                layout_uploadImage_notFinish.setVisibility(View.VISIBLE);
                textView_finish.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (PhotoModel.list_choosedPhotos.size() == 0){
                            Toast.makeText(AddHouseEstate_HouseProCerActivity.this, "请选择图片！", Toast.LENGTH_SHORT).show();
                        }else {
                            String path = PhotoModel.list_choosedPhotos.get(0).getPath();
                            RequestParams requestParams = new RequestParams();
                            requestParams.addBodyParameter("File", new File(path));
                            requestParams.addBodyParameter("HouseId", String.valueOf(getHouseId));
                            HttpUtil.addXUtilHttpParams(requestParams, DBUtil.getLoginUser().getToken());
                            String url;
                            if (isChosoedProprietor == true){
                                url = HttpUtil.uploadHouseProprietaryCertificate_url;
                            }else {
                                url = HttpUtil.uploadRentContract_url;
                            }
                            loading_page.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                            uploadImage(requestParams, url, layout_uploadImage_notFinish, layout_uploadImage_finish, imageView_rentContract);
                        }
                    }
                });
            }
        }
    }

    private void searchHouseEstateByKeyWord(String keyword){
        Map<String, String> params = new HashMap<>();
        params.put("Lng", String.valueOf(WangLinApplication.locationInfoModel.getLongitude()));
        params.put("Lat", String.valueOf(WangLinApplication.locationInfoModel.getLatitude()));
        params.put("RegionId", String.valueOf(WangLinApplication.locationInfoModel.getLocationCityId()));
        params.put("Key", keyword);
        HttpUtil.sendVolleyStringRequest_Post(AddHouseEstate_HouseProCerActivity.this, HttpUtil.seatchHouseEstateByKeyWork_url, params, DBUtil.getLoginUser().getToken(),
                "yellow_searchHouseEstateByKeyWord", new WanglinHttpResponseListener() {
                    @Override
                    public void onSuccessResponse(JSONObject jsonObject_response) {
                        try {
                            JSONArray jsonArray_housingEstatelist = jsonObject_response.getJSONArray("Data");
                            HousingEstateModel.list_housingEstate_neighbor = new ArrayList<>();
                            for (int i = 0; i < jsonArray_housingEstatelist.length(); i++) {
                                JSONObject jsonObject_housingEstate = jsonArray_housingEstatelist.getJSONObject(i);
                                HousingEstateModel housingEstateModel = new HousingEstateModel();
                                housingEstateModel.setId(jsonObject_housingEstate.getInt("Id")); //设置小区ID
                                housingEstateModel.setName(jsonObject_housingEstate.getString("Name")); //设置小区名
                                housingEstateModel.setCity(jsonObject_housingEstate.getString("City"));//设置小区城市
                                housingEstateModel.setRegionId(jsonObject_housingEstate.getInt("RegionId")); //设置区域ID
                                housingEstateModel.setCityId(jsonObject_housingEstate.getInt("CityId"));//设置城市ID
                                //设置小区口号
                                housingEstateModel.setSlogan(jsonObject_housingEstate.getString("Slogan"));
                                if (housingEstateModel.getSlogan().equals("null")) {
                                    housingEstateModel.setSlogan("");
                                }
                                HousingEstateModel.list_housingEstate_neighbor.add(housingEstateModel);
                            }
                            Intent intent = new Intent(AddHouseEstate_HouseProCerActivity.this, SearchHouseEstateActivity.class);
                            intent.putExtra("keyWord", editText_searchText.getText().toString());
                            startActivityForResult(intent, OtherUtil.REQUEST_CODE_ADD_HOUSE_ESTATE_TO_SEARCH_HOUSE_ESTATE);
                            loading_page.dismiss();
                        } catch (JSONException e) {
                            loading_page.dismiss();
                            Toast.makeText(AddHouseEstate_HouseProCerActivity.this, "搜索失败，失败原因：解析返回结果出错，请重试！", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onConnectingError() {
                        loading_page.dismiss();
                    }
                    @Override
                    public void onDisconnectError() {
                        loading_page.dismiss();
                    }
                });
    }

    private void bindingHouseEstate(final String url, int houseEstateId, int blockNum, int unitNum, int floorNum, String roomNum){
        Map<String, String> params = new HashMap<>();
        params.put("VillageId", String.valueOf(houseEstateId));
        params.put("Block", String.valueOf(blockNum));
        params.put("Unit", String.valueOf(unitNum));
        params.put("Floor", String.valueOf(floorNum));
        params.put("Number", roomNum);
        //Log.d("yellow_temp","---" + houseEstateId + "---" + blockNum + "---" + unitNum + "---" + floorNum + "---" + roomNum);
        HttpUtil.sendVolleyStringRequest_Post(AddHouseEstate_HouseProCerActivity.this, HttpUtil.addBindingHouseEstate_url, params, DBUtil.getLoginUser().getToken(),
                "yellow_bingdingHouseEstate", new WanglinHttpResponseListener(){
                    @Override
                    public void onSuccessResponse(JSONObject jsonObject_response) {
                        try {
                            bindingHousedEstateId = jsonObject_response.getInt("Data");
                            if (PhotoModel.list_choosedPhotos.size() > 0){
                                String path = PhotoModel.list_choosedPhotos.get(0).getPath();
                                RequestParams requestParams = new RequestParams();
                                requestParams.addBodyParameter("File", new File(path));
                                requestParams.addBodyParameter("HouseId", String.valueOf(bindingHousedEstateId));
                                HttpUtil.addXUtilHttpParams(requestParams, DBUtil.getLoginUser().getToken());
                                uploadImage(requestParams, url, layout_uploadImage_notFinish, layout_uploadImage_finish, imageView_rentContract);
                            }else {
                                Toast.makeText(AddHouseEstate_HouseProCerActivity.this, "绑定成功，请等待审核！", Toast.LENGTH_SHORT).show();
                                loading_page.dismiss();
                            }
                        } catch (JSONException e) {
                            loading_page.dismiss();
                            Toast.makeText(AddHouseEstate_HouseProCerActivity.this, "绑定失败，失败原因：解析返回数据失败。请重试！", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onConnectingError() {
                        loading_page.dismiss();
                    }

                    @Override
                    public void onDisconnectError() {
                        loading_page.dismiss();
                    }
                });
    }

    private void uploadImage(RequestParams requestParams, String url, final LinearLayout layout_notFinish,
                             final LinearLayout layout_finish, final ImageView imageview_photo){
        Log.d("yellow_upload", url);
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
                        Toast.makeText(AddHouseEstate_HouseProCerActivity.this, "操作成功，请等待审核！", Toast.LENGTH_SHORT).show();
                    }else {
                        layout_notFinish.setVisibility(View.VISIBLE);
                        layout_finish.setVisibility(View.GONE);
                        String msg = jsonObject_response.getString("Msg");
                        HttpUtil.parseResponseCode(AddHouseEstate_HouseProCerActivity.this, requestCode, msg);
                        loading_page.dismiss();
                    }
                } catch (JSONException e) {
                    layout_notFinish.setVisibility(View.VISIBLE);
                    layout_finish.setVisibility(View.GONE);
                    Toast.makeText(AddHouseEstate_HouseProCerActivity.this, "解析返回数据失败，请反馈！", Toast.LENGTH_SHORT).show();
                    loading_page.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                e.printStackTrace();
                layout_notFinish.setVisibility(View.VISIBLE);
                layout_finish.setVisibility(View.GONE);
                Toast.makeText(AddHouseEstate_HouseProCerActivity.this, "操作失败，请稍后重试！", Toast.LENGTH_SHORT).show();
                loading_page.dismiss();
            }
        });
    }

    private void updateImage(RequestParams requestParams, String url, final ImageView imageview_photo, final String oldImageUrl){
        Log.d("yellow_update", url);
        HttpUtils httpUtils = new HttpUtils();

        httpUtils.send(HttpRequest.HttpMethod.POST, url, requestParams, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.d("yellow_updateImage", responseInfo.result);
                try {
                    JSONObject jsonObject_response = new JSONObject(responseInfo.result);
                    int requestCode = jsonObject_response.getInt("Code");
                    if (requestCode == 200){
                        String imageUrl = jsonObject_response.getString("Data");
                        ImageLoader.getInstance().displayImage(imageUrl, imageview_photo, options);
                        loading_page.dismiss();
                        Toast.makeText(AddHouseEstate_HouseProCerActivity.this, "操作成功，请等待审核！", Toast.LENGTH_SHORT).show();
                    }else {
                        ImageLoader.getInstance().displayImage(oldImageUrl, imageview_photo, options);
                        String msg = jsonObject_response.getString("Msg");
                        HttpUtil.parseResponseCode(AddHouseEstate_HouseProCerActivity.this, requestCode, msg);
                        loading_page.dismiss();
                    }
                } catch (JSONException e) {
                    ImageLoader.getInstance().displayImage(oldImageUrl, imageview_photo, options);
                    Toast.makeText(AddHouseEstate_HouseProCerActivity.this, "解析返回数据失败，请反馈！", Toast.LENGTH_SHORT).show();
                    loading_page.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                e.printStackTrace();
                ImageLoader.getInstance().displayImage(oldImageUrl, imageview_photo, options);
                Toast.makeText(AddHouseEstate_HouseProCerActivity.this, "操作失败，请稍后重试！", Toast.LENGTH_SHORT).show();
                loading_page.dismiss();
            }
        });
    }
}
