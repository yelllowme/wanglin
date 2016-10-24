package com.wanglinkeji.wanglin.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.adapter.ListViewAdapter_NeighborHouseEstate;
import com.wanglinkeji.wanglin.model.HousingEstateModel;
import com.wanglinkeji.wanglin.model.HousingEstateblogModel;
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
 * Created by Administrator on 2016/9/10.
 * 搜索小区Acitivity
 */
public class SearchHouseEstateActivity extends Activity implements View.OnClickListener {

    private ImageView imageView_cancle, imageView_searchIcon;

    private ListView listView_houseList;

    private EditText editText_keyWord;

    private PopupWindow loading_page;

    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_activity_search_house_estate);

        viewInit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //返回按钮
            case R.id.imageview_searchHouseEstate_cancle:{
                SearchHouseEstateActivity.this.finish();
                break;
            }
            case R.id.imageview_searchHouseEstate_searchIncon:{
                if (editText_keyWord.getText().length() == 0){
                    Toast.makeText(SearchHouseEstateActivity.this, "搜索内容不能为空哦！", Toast.LENGTH_SHORT).show();
                }else {
                    loading_page.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                    searchHouseEstateByKeyWord(editText_keyWord.getText().toString());
                    //收起软键盘
                    ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editText_keyWord.getWindowToken(), 0);
                }
                break;
            }
            default:
                break;
        }
    }

    private void viewInit() {
        loading_page = OtherUtil.getLoadingPage(SearchHouseEstateActivity.this);
        rootView = LayoutInflater.from(SearchHouseEstateActivity.this).inflate(R.layout.layout_activity_search_house_estate, null);
        imageView_cancle = (ImageView) findViewById(R.id.imageview_searchHouseEstate_cancle);
        imageView_cancle.setOnClickListener(this);
        imageView_searchIcon = (ImageView) findViewById(R.id.imageview_searchHouseEstate_searchIncon);
        imageView_searchIcon.setOnClickListener(this);
        listView_houseList = (ListView) findViewById(R.id.listview_searchHousEstate_houseList);
        listView_houseList.setAdapter(new ArrayAdapter<HousingEstateModel>(SearchHouseEstateActivity.this, android.R.layout.simple_expandable_list_item_1,
                HousingEstateModel.list_housingEstate_neighbor));
        listView_houseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra("houseEstateName", HousingEstateModel.list_housingEstate_neighbor.get((new Long(l).intValue())).getName());
                intent.putExtra("houseEstateId", HousingEstateModel.list_housingEstate_neighbor.get(new Long(l).intValue()).getId());
                setResult(RESULT_OK, intent);
                SearchHouseEstateActivity.this.finish();
            }
        });
        editText_keyWord = (EditText) findViewById(R.id.edittext_searchHousingEstate_searchHousingEstate);
        editText_keyWord.setText(getIntent().getStringExtra("keyWord"));
        editText_keyWord.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (editText_keyWord.getText().length() == 0){
                    Toast.makeText(SearchHouseEstateActivity.this, "搜索内容不能为空哦！", Toast.LENGTH_SHORT).show();
                }else {
                    loading_page.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                    searchHouseEstateByKeyWord(editText_keyWord.getText().toString());
                    //收起软键盘
                    ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editText_keyWord.getWindowToken(), 0);
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
        HttpUtil.sendVolleyStringRequest_Post(SearchHouseEstateActivity.this, HttpUtil.seatchHouseEstateByKeyWork_url, params, DBUtil.getLoginUser().getToken(),
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
                            listView_houseList.setAdapter(new ArrayAdapter<HousingEstateModel>(SearchHouseEstateActivity.this, android.R.layout.simple_expandable_list_item_1,
                                    HousingEstateModel.list_housingEstate_neighbor));
                            loading_page.dismiss();
                        } catch (JSONException e) {
                            loading_page.dismiss();
                            Toast.makeText(SearchHouseEstateActivity.this, "搜索失败，失败原因：解析返回结果出错，请重试！", Toast.LENGTH_SHORT).show();
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
