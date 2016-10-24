package com.wanglinkeji.wanglin.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.adapter.ListViewAdapter_MyHouseEstate_houseList;
import com.wanglinkeji.wanglin.model.HousingEstateModel;
import com.wanglinkeji.wanglin.model.UserHouseModel;
import com.wanglinkeji.wanglin.model.UserIdentityInfoModel;
import com.wanglinkeji.wanglin.util.DBUtil;
import com.wanglinkeji.wanglin.util.HttpUtil;
import com.wanglinkeji.wanglin.util.OtherUtil;
import com.wanglinkeji.wanglin.util.WanglinHttpResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/14.
 * 我的小区管理Activity
 */
public class MyHouseEstateActivity extends Activity implements View.OnClickListener {

    private ImageView imageView_cancle;

    private LinearLayout layout_add;

    private ListView listView_houseList;

    private PopupWindow loading_page;

    private View rootView;

    //用来控制一开始弹出的加载页只显示一次
    private boolean flag = true;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                //设置默认小区
                case -111:{
                    loading_page.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                    int houseId = Integer.valueOf(msg.obj.toString());
                    setDefaultHouseEstate(MyHouseEstateActivity.this, houseId);
                    break;
                }
                //删除住房
                case -222:{
                    loading_page.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                    int houseId = Integer.valueOf(msg.obj.toString());
                    deleteHouse(MyHouseEstateActivity.this, houseId);
                    break;
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
        setContentView(R.layout.layout_activity_my_house_estate);

        viewInit();
    }

    //一开始就弹出加载匡
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //利用flag来让加载页只出现一次
        if (flag){
            loading_page.showAtLocation(rootView, Gravity.CENTER, 0, 0);
        }
        flag = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserIdentityInfo(DBUtil.getLoginUser().getToken());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //返回按钮
            case R.id.imageview_myHouseEstate_cancle:{
                MyHouseEstateActivity.this.finish();
                break;
            }
            //添加小区按钮
            case R.id.layout_myHouseEstate_bottom_add:{
                Intent intent = new Intent(MyHouseEstateActivity.this, AddHouseEstate_HouseProCerActivity.class);
                intent.putExtra("whatFrom", AddHouseEstate_HouseProCerActivity.WHAT_FROM_ADD);
                startActivity(intent);
                break;
            }
            default:
                break;
        }
    }

    private void viewInit(){
        loading_page = OtherUtil.getLoadingPage(MyHouseEstateActivity.this);
        rootView = LayoutInflater.from(MyHouseEstateActivity.this).inflate(R.layout.layout_activity_my_house_estate, null);
        imageView_cancle = (ImageView)findViewById(R.id.imageview_myHouseEstate_cancle);
        imageView_cancle.setOnClickListener(this);
        layout_add = (LinearLayout)findViewById(R.id.layout_myHouseEstate_bottom_add);
        layout_add.setOnClickListener(this);
        listView_houseList = (ListView)findViewById(R.id.listview_myHouseEstate_houseEstateList);
    }

    //获取小区列表成功后设置控件显示
    private void setViewAfterGetHouseEstate(List<HousingEstateModel> list_houseEstate){
        //获取住房List
        final List<UserHouseModel> list_house = new ArrayList<>();
        for (int i = 0; i < list_houseEstate.size(); i++){
            List<UserHouseModel> list_house_temp = list_houseEstate.get(i).getList_house();
            for (int j = 0; j < list_house_temp.size(); j++){
                list_house_temp.get(j).setShowDialog(false); //设置右下角对话框是否显示
                list_house_temp.get(j).setHouseEstateName(list_houseEstate.get(i).getName()); //设置所在小区名字
                list_house.add(list_house_temp.get(j));
            }
        }
        listView_houseList.setAdapter(new ListViewAdapter_MyHouseEstate_houseList(list_house, MyHouseEstateActivity.this,
                R.layout.layout_listview_item_my_house_estate, handler));
    }

    //获取用户身份
    private void getUserIdentityInfo(String token){
        UserIdentityInfoModel.userIdentityInfoModel = new UserIdentityInfoModel();
        Map<String, String> params = new HashMap<>();
        HttpUtil.sendVolleyStringRequest_Post(MyHouseEstateActivity.this, HttpUtil.getUserIdentityInfo_url, params, token, "yellow_getUserIdentityInfo",
                new WanglinHttpResponseListener() {
                    @Override
                    public void onSuccessResponse(JSONObject jsonObject_response) {
                        try {
                            JSONObject jsonObject_data = jsonObject_response.getJSONObject("Data");
                            UserIdentityInfoModel.userIdentityInfoModel.setUserId(jsonObject_data.getInt("UserId")); //设置用户ID
                            UserIdentityInfoModel.userIdentityInfoModel.setVerifyState(jsonObject_data.getInt("IDIsVerify")); //设置用户身份状态
                            UserIdentityInfoModel.userIdentityInfoModel.setIDCardImage_front(jsonObject_data.getString("IDZMImg"));//设置身份证正面照
                            UserIdentityInfoModel.userIdentityInfoModel.setIDCardImage_reserveSide(jsonObject_data.getString("IDFMImg"));//设置身份证反面照
                            //获取我的小区列表
                            JSONArray jsonArray_houseEstate = jsonObject_data.getJSONArray("VillageList");
                            List<HousingEstateModel> list_houseEstate = new ArrayList<HousingEstateModel>();
                            for (int i = 0; i < jsonArray_houseEstate.length(); i++){
                                HousingEstateModel housingEstateModel = new HousingEstateModel();
                                JSONObject jsonObject_houseEstate = jsonArray_houseEstate.getJSONObject(i);
                                housingEstateModel.setId(jsonObject_houseEstate.getInt("Id"));//设置小区ID
                                housingEstateModel.setDefault(jsonObject_houseEstate.getBoolean("IsDefault"));//设置是否是默认小区
                                housingEstateModel.setCity(jsonObject_houseEstate.getString("City"));//设置所在城市
                                housingEstateModel.setCityId(jsonObject_houseEstate.getInt("CityId"));//设置所在城市ID
                                housingEstateModel.setLng(jsonObject_houseEstate.getDouble("Lng"));//设置小区经度
                                housingEstateModel.setLat(jsonObject_houseEstate.getDouble("Lat"));//设置小区纬度
                                housingEstateModel.setName(jsonObject_houseEstate.getString("Name"));//设置小区名
                                housingEstateModel.setLogoURL(jsonObject_houseEstate.getString("Logo"));//设置小区logoURL
                                //设置小区口号
                                housingEstateModel.setSlogan(jsonObject_houseEstate.getString("Slogan"));
                                if (housingEstateModel.getSlogan().equals("null")) {
                                    housingEstateModel.setSlogan("");
                                }
                                //获取并设置小区住房列表
                                JSONArray jsonArray_house = jsonObject_houseEstate.getJSONArray("HouseList");
                                List<UserHouseModel> list_house = new ArrayList<UserHouseModel>();
                                for (int j = 0; j < jsonArray_house.length(); j++){
                                    JSONObject jsonObject_house = jsonArray_house.getJSONObject(j);
                                    UserHouseModel userHouseModel = new UserHouseModel();
                                    userHouseModel.setHouseId(jsonObject_house.getInt("Id"));//设置住房ID
                                    userHouseModel.setHouseEstateId(jsonObject_house.getInt("VillageId"));//设置住房所在小区Id
                                    userHouseModel.setHouseUserId(jsonObject_house.getInt("UserId"));//设置住房用户ID
                                    userHouseModel.setRidgepoleNum(jsonObject_house.getInt("Block"));//设置住房栋数
                                    userHouseModel.setUnitNum(jsonObject_house.getInt("Unit"));//设置单元数
                                    userHouseModel.setFloorNum(jsonObject_house.getInt("Floor"));//设置楼层数
                                    userHouseModel.setRoomNum(jsonObject_house.getString("Number"));//设置房间号
                                    userHouseModel.setHouseCertificateImg(jsonObject_house.getString("HouseCertificateImg"));//设置房产证URL
                                    userHouseModel.setIsVerifyHouseCertificate(jsonObject_house.getInt("CertificateImgVerify"));//设置住房验证状态
                                    userHouseModel.setRentContractImg(jsonObject_house.getString("RentHouseImg"));//设置租赁合同URL
                                    userHouseModel.setIsVerifyRentContract(jsonObject_house.getInt("RentHouseImgVerify"));//设置租赁合同验证状态
                                    userHouseModel.setUserIdentity(jsonObject_house.getInt("Identity"));//设置住房用户身份
                                    userHouseModel.setDefault(jsonObject_house.getBoolean("IsDefault"));//设置是否是默认住房
                                    list_house.add(userHouseModel);
                                }
                                housingEstateModel.setList_house(list_house);
                                list_houseEstate.add(housingEstateModel);
                            }
                            UserIdentityInfoModel.userIdentityInfoModel.setList_housingEstate(list_houseEstate);
                            setViewAfterGetHouseEstate(UserIdentityInfoModel.userIdentityInfoModel.getList_housingEstate());
                            loading_page.dismiss();
                        } catch (JSONException e) {
                            loading_page.dismiss();
                            Toast.makeText(MyHouseEstateActivity.this, "获取身份信息失败，失败原因：解析返回结果出错，请重试！", Toast.LENGTH_SHORT).show();
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

    private void setDefaultHouseEstate(Context context, int houseId){
        Log.d("yellow_temp", "houseId" + houseId);
        Map<String, String> params = new HashMap<>();
        params.put("HouseId", String.valueOf(houseId));
        HttpUtil.sendVolleyStringRequest_Post(context, HttpUtil.setDefaultHouse_url, params, DBUtil.getLoginUser().getToken(), "yellow_setDefaultHouse",
                new WanglinHttpResponseListener() {
                    @Override
                    public void onSuccessResponse(JSONObject jsonObject_response) {
                        getUserIdentityInfo(DBUtil.getLoginUser().getToken());
                        loading_page.dismiss();
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

    private void deleteHouse(Context context,int houseId){
        Map<String, String> params = new HashMap<>();
        params.put("HouseId", String.valueOf(houseId));
        HttpUtil.sendVolleyStringRequest_Post(context, HttpUtil.deleteHouse_url, params, DBUtil.getLoginUser().getToken(), "yellow_deleteHouse",
                new WanglinHttpResponseListener() {
                    @Override
                    public void onSuccessResponse(JSONObject jsonObject_response) {
                        getUserIdentityInfo(DBUtil.getLoginUser().getToken());
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
