package com.wanglinkeji.wanglin.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.model.HousingEstateModel;
import com.wanglinkeji.wanglin.util.DBUtil;
import com.wanglinkeji.wanglin.util.HttpUtil;
import com.wanglinkeji.wanglin.util.OtherUtil;
import com.wanglinkeji.wanglin.util.WangLinApplication;
import com.wanglinkeji.wanglin.util.WanglinHttpResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/5.
 * 绑定小区Activity
 */
public class Register_BindingHousingEstateActivity extends Activity implements View.OnClickListener {

    private ImageView imageView_searchIcon;

    private EditText editText_realName, editText_searchHousingEstate, editText_ridgepoleNum, editText_unitNum, editText_floorNum, editText_roomNum;

    private TextView textView_ensureBinding, textView_skip;

    private PopupWindow loading_page;

    private View rootView;

    //选择小区的ID
    private int houseEstateId = -2747;

    //栋数和单元数
    private int ridgepoleNum, unitNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_activity_binding_housing_estate);

        viewInit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case OtherUtil.REQUEST_CODE_BINDING_HOUSE_TO_SEARCH_HOUSE:{
                if (resultCode == RESULT_OK){
                    editText_searchHousingEstate.setText(data.getStringExtra("houseEstateName"));
                    houseEstateId = data.getIntExtra("houseEstateId", -2747);
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
            //搜索按钮
            case R.id.imageview_bindingHousingEstate_searchIcon:{
                if (editText_searchHousingEstate.getText().length() == 0){
                    Toast.makeText(Register_BindingHousingEstateActivity.this, "搜索内容不能为空哦！", Toast.LENGTH_SHORT).show();
                }else {
                    loading_page.showAtLocation(rootView, Gravity.CENTER, 0 , 0);
                    searchHouseEstateByKeyWord(editText_searchHousingEstate.getText().toString());
                    //收起软键盘
                    ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editText_searchHousingEstate.getWindowToken(), 0);
                }
                break;
            }
            //认证身份按钮
            case R.id.textview_bindingHousingEstate_ensureBinding:{
                if (editText_realName.getText().length() == 0){
                    Toast.makeText(Register_BindingHousingEstateActivity.this, "真实姓名不能为空哦！", Toast.LENGTH_SHORT).show();
                }else {
                    if (houseEstateId == -2747){
                        Toast.makeText(Register_BindingHousingEstateActivity.this, "请搜索并选择绑定小区！", Toast.LENGTH_SHORT).show();
                    }else {
                        if (editText_ridgepoleNum.getText().length() == 0 && editText_unitNum.getText().length() == 0){
                            Toast.makeText(Register_BindingHousingEstateActivity.this, "栋和单元请至少填一个哦！", Toast.LENGTH_SHORT).show();
                        }else {
                            if (editText_ridgepoleNum.getText().length() == 0){
                                ridgepoleNum = -1;
                            }else {
                                ridgepoleNum = Integer.valueOf(editText_ridgepoleNum.getText().toString());
                            }
                            if (editText_unitNum.getText().length() == 0){
                                unitNum = -1;
                            }else {
                                unitNum = Integer.valueOf(editText_unitNum.getText().toString());
                            }
                            if (editText_floorNum.getText().length() == 0){
                                Toast.makeText(Register_BindingHousingEstateActivity.this, "楼数不能为空哦！", Toast.LENGTH_SHORT).show();
                            }else {
                                if (editText_roomNum.getText().length() == 0){
                                    Toast.makeText(Register_BindingHousingEstateActivity.this, "房号不能为空！", Toast.LENGTH_SHORT).show();
                                }else {
                                    loading_page.showAtLocation(rootView, Gravity.CENTER, 0 ,0);
                                    bindingHouseEstate(editText_realName.getText().toString(), houseEstateId, ridgepoleNum, unitNum,
                                            Integer.valueOf(editText_floorNum.getText().toString()), editText_roomNum.getText().toString());
                                }
                            }
                        }
                    }
                }
                break;
            }
            //跳过按钮
            case R.id.textview_bindingHousingEstate_skip:{
                Intent intent = new Intent(Register_BindingHousingEstateActivity.this, MainActivity.class);
                startActivity(intent);
                Register_BindingHousingEstateActivity.this.finish();
                break;
            }
            default:
                break;
        }
    }

    private void viewInit(){
        loading_page = OtherUtil.getLoadingPage(Register_BindingHousingEstateActivity.this);
        rootView = LayoutInflater.from(Register_BindingHousingEstateActivity.this).inflate(R.layout.layout_activity_binding_housing_estate, null);

        imageView_searchIcon = (ImageView)findViewById(R.id.imageview_bindingHousingEstate_searchIcon);
        imageView_searchIcon.setOnClickListener(this);
        editText_realName = (EditText)findViewById(R.id.edittext_bindingHousingEstate_realName);
        editText_searchHousingEstate = (EditText)findViewById(R.id.edittext_bindingHousingEstate_searchHousingEstate);
        editText_ridgepoleNum = (EditText)findViewById(R.id.edittext_bindingHousingEstate_ridgepoleNum);
        editText_unitNum = (EditText)findViewById(R.id.edittext_bindingHousingEstate_unitNum);
        editText_floorNum = (EditText)findViewById(R.id.edittext_bindingHousingEstate_floorNum);
        editText_roomNum = (EditText)findViewById(R.id.edittext_bindingHousingEstate_roomNum);
        textView_ensureBinding = (TextView)findViewById(R.id.textview_bindingHousingEstate_ensureBinding);
        textView_ensureBinding.setOnClickListener(this);
        textView_skip = (TextView)findViewById(R.id.textview_bindingHousingEstate_skip);
        textView_skip.setOnClickListener(this);
        //捕获软键盘回车键,并重写回车事件
        editText_searchHousingEstate.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (editText_searchHousingEstate.getText().length() == 0){
                    Toast.makeText(Register_BindingHousingEstateActivity.this, "搜索内容不能为空哦！", Toast.LENGTH_SHORT).show();
                }else {
                    loading_page.showAtLocation(rootView, Gravity.CENTER, 0 , 0);
                    searchHouseEstateByKeyWord(editText_searchHousingEstate.getText().toString());
                    //收起软键盘
                    ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editText_searchHousingEstate.getWindowToken(), 0);
                }
                return true;
            }
        });
    }

    private void searchHouseEstateByKeyWord(String keyword){
        Map<String, String> params = new HashMap<>();
        params.put("Lng", String.valueOf(WangLinApplication.locationInfoModel.getLongitude()));
        params.put("Lat", String.valueOf(WangLinApplication.locationInfoModel.getLatitude()));
        params.put("RegionId", String.valueOf(WangLinApplication.locationInfoModel.getLocationCityId()));
        params.put("Key", keyword);
        HttpUtil.sendVolleyStringRequest_Post(Register_BindingHousingEstateActivity.this, HttpUtil.seatchHouseEstateByKeyWork_url, params, DBUtil.getLoginUser().getToken(),
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
                            Intent intent = new Intent(Register_BindingHousingEstateActivity.this, SearchHouseEstateActivity.class);
                            intent.putExtra("keyWord", editText_searchHousingEstate.getText().toString());
                            startActivityForResult(intent, OtherUtil.REQUEST_CODE_BINDING_HOUSE_TO_SEARCH_HOUSE);
                            loading_page.dismiss();
                        } catch (JSONException e) {
                            loading_page.dismiss();
                            Toast.makeText(Register_BindingHousingEstateActivity.this, "搜索失败，失败原因：解析返回结果出错，请重试！", Toast.LENGTH_SHORT).show();
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

    private void bindingHouseEstate(String trueName, int houseEstateId, int blockNum, int unitNum, int floorNum, String roomNum){
        Map<String, String> params = new HashMap<>();
        params.put("TrueName", trueName);
        params.put("VillageId", String.valueOf(houseEstateId));
        params.put("Block", String.valueOf(blockNum));
        params.put("Unit", String.valueOf(unitNum));
        params.put("Floor", String.valueOf(floorNum));
        params.put("Number", roomNum);
        //Log.d("yellow_temp", trueName + "---" + houseEstateId + "---" + blockNum + "---" + unitNum + "---" + floorNum + "---" + roomNum);
        HttpUtil.sendVolleyStringRequest_Post(Register_BindingHousingEstateActivity.this, HttpUtil.bindingHouseEstate_url, params, DBUtil.getLoginUser().getToken(),
                "yellow_bingdingHouseEstate", new WanglinHttpResponseListener(){
                    @Override
                    public void onSuccessResponse(JSONObject jsonObject_response) {
                        try {
                            Intent intent = new Intent(Register_BindingHousingEstateActivity.this, Register_UploadIdentityImageActivity.class);
                            intent.putExtra("houseId", jsonObject_response.getInt("Data"));
                            loading_page.dismiss();
                            startActivity(intent);
                            Register_BindingHousingEstateActivity.this.finish();
                        } catch (JSONException e) {
                            loading_page.dismiss();
                            Toast.makeText(Register_BindingHousingEstateActivity.this, "绑定失败，失败原因：解析返回数据失败。请重试！", Toast.LENGTH_SHORT).show();
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

}
