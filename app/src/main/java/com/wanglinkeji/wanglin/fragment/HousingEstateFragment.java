package com.wanglinkeji.wanglin.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.adapter.ListViewAdapter_NeighborHouseEstate;
import com.wanglinkeji.wanglin.adapter.ViewPagerAdapter_HousingEstateFragment;
import com.wanglinkeji.wanglin.customerview.RotateAnimation;
import com.wanglinkeji.wanglin.model.HousingEstateModel;
import com.wanglinkeji.wanglin.model.UserHouseModel;
import com.wanglinkeji.wanglin.model.UserIdentityInfoModel;
import com.wanglinkeji.wanglin.model.UserRoomModel;
import com.wanglinkeji.wanglin.util.DBUtil;
import com.wanglinkeji.wanglin.util.HttpUtil;
import com.wanglinkeji.wanglin.util.WangLinApplication;
import com.wanglinkeji.wanglin.util.WanglinHttpResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2016/8/16.
 * 首页->小区Fragment
 *
 */
public class HousingEstateFragment extends Fragment implements View.OnClickListener{

    private View view;

    private TextView textView_houseingEstateName, textView_title_housingEstateSpewing, textView_title_citySpewing, textView_title_news,
            textView_title_weather, textView_choosedNeighborHouseEstate_myHouseEstate;

    private ImageView imageView_title_housingEstateSpewingPoint,imageView_title_citySpewingPoint, imageView_title_newsPoint, imageView_search,
            imageView_title_weather, imageView_choosedNeighborHouseEstate_cancle;

    private LinearLayout layout_loading_page, layout_choosedNeighborHouseEstate, layout_choosedNeighborHouseEstate_content,
            layout_choosedNeighborHouseEstate_myHouseEstate, layout_choosedNeighborHouseEstate_myHouseEstate_title;

    private EditText editText_choosedNeighborHouseEstate_searchText;

    private ListView listView_choosedNeighborHouseEstate_houseEstateList;

    private ViewPager viewPager;

    private List<View> list_views;

    //显示图片的配置
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.mipmap.ic_launcher)
            .showImageOnFail(R.mipmap.ic_launcher)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                //小区名—>小区口号
                case -666:{
                    startRotateAnim_Name();
                    break;
                }
                //小区口号—>小区名
                case -777:{
                    startRotateAnim_Slogan();
                    break;
                }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_fragment_housing_estate, container, false);
        viewInit();
        //获取登录用户的身份信息
        getUserIdentityInfo(DBUtil.getLoginUser().getToken());

        return view;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //小区名按钮
            case R.id.textview_homePage_housingEstateFragment_housingEstateName:{
                showNeighborPageStep();
                break;
            }
            //搜索按钮
            case R.id.imageview_homePage_housingEstateFragment_search:{
                break;
            }
            //标题小区吐槽按钮
            case R.id.textview_homePage_housingEstateFragment_housingEstateSpewing:{
                viewPager.setCurrentItem(0);
                setTitleView_ChoosedHouseEstate();
                break;
            }
            //标题同城吐槽按钮
            case R.id.textview_homePage_housingEstateFragment_citySpewing:{
                viewPager.setCurrentItem(1);
                setTitleView_ChoosedCity();
                break;
            }
            //标题大事件按钮
            case R.id.textview_homePage_housingEstateFragment_news:{
                viewPager.setCurrentItem(2);
                setTitleView_ChoosedNews();
                break;
            }
            //退出选择附近小区页面
            case R.id.imageview_housingEstateBlog_choosedNeighborHouseEstate_cancle:{
                if (layout_choosedNeighborHouseEstate.getVisibility() == View.VISIBLE){
                    layout_choosedNeighborHouseEstate.setVisibility(View.GONE);
                    if (UserIdentityInfoModel.current_housingEstate == null){
                        viewPager.setCurrentItem(1);
                        setTitleView_ChoosedCity();
                    }
                }
                break;
            }
            //我的小区按钮
            case R.id.textview_housingEstateBlog_choosedNeighborHouseEstate_myHouseEstate:{
                //加载页显示
                layout_loading_page.setVisibility(View.VISIBLE);
                //设置用户当前选择小区
                UserIdentityInfoModel.current_housingEstate = UserIdentityInfoModel.getDefaultHouseEstate();
                //开始标题切换
                startTitleAnim();
                //选择附近小区页消失
                layout_choosedNeighborHouseEstate.setVisibility(View.GONE);
                //根据用户当前选择的小区设置控件显示
                setViewByUserIdentityInfo(UserIdentityInfoModel.userIdentityInfoModel);
                break;
            }
            default:
                break;
        }
    }



    private void viewInit(){
        /**
         * 选择附近小区弹窗中控件
         */
        layout_choosedNeighborHouseEstate = (LinearLayout)view.findViewById(R.id.layout_housingEstateBlog_choosedNeighborHouseEstate);
        //这里添加点击事件是为了屏蔽弹窗显示时，覆盖之下的控件仍可以操作
        layout_choosedNeighborHouseEstate.setOnClickListener(this);
        layout_choosedNeighborHouseEstate_content = (LinearLayout)view.findViewById(R.id.layout_housingEstateBlog_choosedNeighborHouseEstate_content);
        imageView_choosedNeighborHouseEstate_cancle = (ImageView)view.findViewById(R.id.imageview_housingEstateBlog_choosedNeighborHouseEstate_cancle);
        imageView_choosedNeighborHouseEstate_cancle.setOnClickListener(this);
        layout_choosedNeighborHouseEstate_myHouseEstate_title = (LinearLayout)view.findViewById(R.id.layout_housingEstateBlog_choosedNeighborHouseEstate_myHouseEstate_title);
        layout_choosedNeighborHouseEstate_myHouseEstate = (LinearLayout)view.findViewById(R.id.layout_housingEstateBlog_choosedNeighborHouseEstate_myHouseEstate);
        textView_choosedNeighborHouseEstate_myHouseEstate = (TextView)view.findViewById(R.id.textview_housingEstateBlog_choosedNeighborHouseEstate_myHouseEstate);
        editText_choosedNeighborHouseEstate_searchText = (EditText)view.findViewById(R.id.edittext_housingEstateBlog_choosedNeighborHouseEstate_searchText);
        editText_choosedNeighborHouseEstate_searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (editText_choosedNeighborHouseEstate_searchText.getText().length() == 0){
                    layout_loading_page.setVisibility(View.VISIBLE);
                    getNeighborHosingEstate(false);
                }else {
                    layout_loading_page.setVisibility(View.VISIBLE);
                    searchHouseEstateByKeyWord(editText_choosedNeighborHouseEstate_searchText.getText().toString());
                }
                //收起软键盘
                ((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                        editText_choosedNeighborHouseEstate_searchText.getWindowToken(), 0);
                return true; //这里返回false，点击团键盘回车键时，这里面的方法会执行两次
            }
        });
        listView_choosedNeighborHouseEstate_houseEstateList = (ListView)view.findViewById(R.id.listview_housingEstateBlog_choosedNeighborHouseEstate_houseEstateList);
        listView_choosedNeighborHouseEstate_houseEstateList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //加载页显示
                layout_loading_page.setVisibility(View.VISIBLE);
                //设置用户当前选择小区
                UserIdentityInfoModel.current_housingEstate = HousingEstateModel.list_housingEstate_neighbor.get((new Long(l).intValue()));
                //开始标题切换
                startTitleAnim();
                //选择附近小区页消失
                layout_choosedNeighborHouseEstate.setVisibility(View.GONE);
                //根据用户当前选择的小区设置控件显示
                setViewByUserIdentityInfo(UserIdentityInfoModel.userIdentityInfoModel);
            }
        });
        /**
         * 列表（吐槽列表，新闻列表）以上的控件
         */
        imageView_title_housingEstateSpewingPoint = (ImageView)view.findViewById(R.id.imageview_homePage_housingEstateFragment_housingEstateSpewingPoint);
        imageView_title_citySpewingPoint = (ImageView)view.findViewById(R.id.imageview_homePage_housingEstateFragment_citySpewingPoint);
        imageView_title_newsPoint = (ImageView)view.findViewById(R.id.imageview_homePage_housingEstateFragment_newsPoint);
        imageView_search = (ImageView)view.findViewById(R.id.imageview_homePage_housingEstateFragment_search);
        imageView_search.setOnClickListener(this);
        imageView_title_weather = (ImageView)view.findViewById(R.id.imageview_homePage_housingEstateFragment_weather);
        imageView_title_weather.setBackgroundResource(R.mipmap.common_weather_icon);
        //ImageLoader.getInstance().displayImage(WangLinApplication.locationWeatherModel.getDayPictureUrl(), imageView_title_weather, options);
        textView_title_housingEstateSpewing = (TextView)view.findViewById(R.id.textview_homePage_housingEstateFragment_housingEstateSpewing);
        textView_title_housingEstateSpewing.setOnClickListener(this);
        textView_title_citySpewing = (TextView)view.findViewById(R.id.textview_homePage_housingEstateFragment_citySpewing);
        textView_title_citySpewing.setOnClickListener(this);
        textView_title_weather = (TextView)view.findViewById(R.id.textview_homePage_housingEstateFragment_weather);
        textView_title_weather.setText(WangLinApplication.locationWeatherModel.getTemperature());
        textView_title_news = (TextView)view.findViewById(R.id.textview_homePage_housingEstateFragment_news);
        textView_title_news.setOnClickListener(this);
        textView_houseingEstateName = (TextView)view.findViewById(R.id.textview_homePage_housingEstateFragment_housingEstateName);
        textView_houseingEstateName.setOnClickListener(this);

        /**
         * 初始化ViewPager
         */
        list_views = new ArrayList<>();
        viewPager = (ViewPager)view.findViewById(R.id.viewpager_homePage_housingEstateFragment);
        View view_housingEstateSpewing = LayoutInflater.from(getActivity()).inflate(R.layout.layout_viewpager_item_housing_estate_spewing, null);
        View view_citySpewing = LayoutInflater.from(getActivity()).inflate(R.layout.layout_viewpager_item_city_spewing, null);
        View view_news = LayoutInflater.from(getActivity()).inflate(R.layout.layout_viewpager_item_news, null);
        list_views.add(view_housingEstateSpewing);
        list_views.add(view_citySpewing);
        list_views.add(view_news);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                switch (position){
                    //小区吐槽
                    case 0:{
                        setTitleView_ChoosedHouseEstate();
                        if (UserIdentityInfoModel.current_housingEstate == null){
                            showNeighborPageStep();
                        }else {
                            layout_choosedNeighborHouseEstate.setVisibility(View.GONE);
                        }
                        break;
                    }
                    //同城吐槽
                    case 1:{
                        setTitleView_ChoosedCity();
                        break;
                    }
                    //大事件
                    case 2:{
                        setTitleView_ChoosedNews();
                        break;
                    }
                    default:
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        /**
         * 其他控件
         */
        layout_loading_page = (LinearLayout)view.findViewById(R.id.layout_housingEstateBlog_loadingPage);
        //这里添加点击事件是为了屏蔽弹窗显示时，覆盖之下的控件仍可以操作
        layout_loading_page.setOnClickListener(this);
    }

    //当“小区吐槽”页被选中的时候，标题栏的显示
    private void setTitleView_ChoosedHouseEstate(){
        textView_title_housingEstateSpewing.setTextColor(getActivity().getResources().getColor(R.color.white));
        textView_title_citySpewing.setTextColor(getActivity().getResources().getColor(R.color.gray_dark_one));
        textView_title_news.setTextColor(getActivity().getResources().getColor(R.color.gray_dark_one));
        imageView_title_housingEstateSpewingPoint.setVisibility(View.VISIBLE);
        imageView_title_citySpewingPoint.setVisibility(View.INVISIBLE);
        imageView_title_newsPoint.setVisibility(View.INVISIBLE);
    }

    //当“同城吐槽”页被选中的时候，标题栏的显示
    private void setTitleView_ChoosedCity(){
        textView_title_housingEstateSpewing.setTextColor(getActivity().getResources().getColor(R.color.gray_dark_one));
        textView_title_citySpewing.setTextColor(getActivity().getResources().getColor(R.color.white));
        textView_title_news.setTextColor(getActivity().getResources().getColor(R.color.gray_dark_one));
        imageView_title_housingEstateSpewingPoint.setVisibility(View.INVISIBLE);
        imageView_title_citySpewingPoint.setVisibility(View.VISIBLE);
        imageView_title_newsPoint.setVisibility(View.INVISIBLE);
    }

    //当“大事件”页被选中的时候，标题栏的显示
    private void setTitleView_ChoosedNews(){
        textView_title_housingEstateSpewing.setTextColor(getActivity().getResources().getColor(R.color.gray_dark_one));
        textView_title_citySpewing.setTextColor(getActivity().getResources().getColor(R.color.gray_dark_one));
        textView_title_news.setTextColor(getActivity().getResources().getColor(R.color.white));
        imageView_title_housingEstateSpewingPoint.setVisibility(View.INVISIBLE);
        imageView_title_citySpewingPoint.setVisibility(View.INVISIBLE);
        imageView_title_newsPoint.setVisibility(View.VISIBLE);
    }

    //小区名字和小区口号轮转动画（小区名—>小区口号）
    private void startRotateAnim_Name(){
        if (!UserIdentityInfoModel.current_housingEstate.getSlogan().equals("")){
            textView_houseingEstateName.setText(UserIdentityInfoModel.current_housingEstate.getSlogan());
            handler.sendEmptyMessageDelayed(-777, 5000);
        }
    }

    //小区名字和小区口号轮转动画（小区口号—>小区名）
    private void startRotateAnim_Slogan(){
        textView_houseingEstateName.setText(UserIdentityInfoModel.current_housingEstate.getName());
        handler.sendEmptyMessageDelayed(-666, 6000);
    }

    //开始标题动画（小区名—>小区口号—>小区名...）
    private void startTitleAnim(){
        //设置选择小区名，开始“小区名—>小区口号—>小区名”交替播放
        textView_houseingEstateName.setText(UserIdentityInfoModel.current_housingEstate.getName());
        handler.sendEmptyMessageDelayed(-666, 5000);
    }

    //选择附近小区渐变大动画
    private void startToBigAnim(){
        Animation animation = new ScaleAnimation(0.0f,1.0f,0.0f,1.0f,Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        animation.setDuration(300);
        layout_choosedNeighborHouseEstate_content.clearAnimation();
        layout_choosedNeighborHouseEstate_content.startAnimation(animation);
    }

    //显示搜索附近小区界面（加载页出现->获取附近小区--成功-->显示附近小区界面->加载页消失）
    //                                       |
    //                                       |--失败-->不显示附近小区界面->加载页消失
    private void showNeighborPageStep(){
        layout_loading_page.setVisibility(View.VISIBLE);
        getNeighborHosingEstate(true);
    }

    //根据获取的数据设置用户当前默认的小区
    private void setDefaultHouseEstate(UserIdentityInfoModel userIdentityInfo){
        userIdentityInfo.current_housingEstate = null;
        for (int i = 0; i <userIdentityInfo.getList_housingEstate().size(); i++){
            List<UserHouseModel> list_house_temp = userIdentityInfo.getList_housingEstate().get(i).getList_house();
            for (int j = 0; j < list_house_temp.size(); j++){
                if (list_house_temp.get(j).isDefault() == true){
                    userIdentityInfo.current_housingEstate = userIdentityInfo.getList_housingEstate().get(i);
                    break;
                }
            }
        }
    }

    //根据当前小区设置控件(ViewPager,和title)
    private void setViewByUserIdentityInfo(UserIdentityInfoModel userIdentityInfo){
        viewPager.setAdapter(new ViewPagerAdapter_HousingEstateFragment(list_views, getActivity(), layout_loading_page));
        /**
         * 根据用户当前选择小区来设置控件
         */
        if (userIdentityInfo.current_housingEstate == null){
            viewPager.setCurrentItem(1);
            setTitleView_ChoosedCity();
            //去掉比如“成都市”中的“市”字
            String cityName = WangLinApplication.locationInfoModel.getLocatonCity();
            String[] cityChars = cityName.split("市");
            textView_title_citySpewing.setText(cityChars[0] + "吐槽"); //设置同城吐槽标题
        }else {
            viewPager.setCurrentItem(0);
            setTitleView_ChoosedHouseEstate();
            textView_houseingEstateName.setText(userIdentityInfo.current_housingEstate.getName()); //设置小区标题，小区名
            //去掉比如“成都市”中的“市”字
            String cityName = UserIdentityInfoModel.current_housingEstate.getCity();
            String[] cityChars = cityName.split("市");
            textView_title_citySpewing.setText(cityChars[0] + "吐槽"); //设置同城吐槽标题
            startTitleAnim();
        }
    }

    //根据用户是否有小区通过验证并设置附近小区弹窗中“我的小区”是否显示
    private void isShowMyHouseEstate(UserIdentityInfoModel userIdentityInfoModel){
        if (UserIdentityInfoModel.hasDefultHouseEstate() == true){
            layout_choosedNeighborHouseEstate_myHouseEstate_title.setVisibility(View.VISIBLE);
            layout_choosedNeighborHouseEstate_myHouseEstate.setVisibility(View.VISIBLE);
            textView_choosedNeighborHouseEstate_myHouseEstate.setText(UserIdentityInfoModel.getDefaultHouseEstate().getName());
            textView_choosedNeighborHouseEstate_myHouseEstate.setOnClickListener(this);
        }else {
            layout_choosedNeighborHouseEstate_myHouseEstate_title.setVisibility(View.GONE);
            layout_choosedNeighborHouseEstate_myHouseEstate.setVisibility(View.GONE);
        }
    }

    //获取用户身份
    private void getUserIdentityInfo(String token){
        UserIdentityInfoModel.userIdentityInfoModel = new UserIdentityInfoModel();
        Map<String, String> params = new HashMap<>();
        HttpUtil.sendVolleyStringRequest_Post(getActivity(), HttpUtil.getUserIdentityInfo_url, params, token, "yellow_getUserIdentityInfo",
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
                            setDefaultHouseEstate(UserIdentityInfoModel.userIdentityInfoModel);
                            setViewByUserIdentityInfo(UserIdentityInfoModel.userIdentityInfoModel);
                        } catch (JSONException e) {
                            UserIdentityInfoModel.setFailedGetInfo(UserIdentityInfoModel.userIdentityInfoModel);
                            Toast.makeText(getActivity(), "获取身份信息失败，失败原因：解析返回结果出错，请重试！", Toast.LENGTH_SHORT).show();
                            setViewByUserIdentityInfo(UserIdentityInfoModel.userIdentityInfoModel);
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onConnectingError() {
                        UserIdentityInfoModel.setFailedGetInfo(UserIdentityInfoModel.userIdentityInfoModel);
                        setViewByUserIdentityInfo(UserIdentityInfoModel.userIdentityInfoModel);
                    }
                    @Override
                    public void onDisconnectError() {
                        UserIdentityInfoModel.setFailedGetInfo(UserIdentityInfoModel.userIdentityInfoModel);
                        setViewByUserIdentityInfo(UserIdentityInfoModel.userIdentityInfoModel);
                    }
                });
    }

    //获取附近小区列表
    private void getNeighborHosingEstate(final boolean isAnim) {
        Map<String, String> params = new HashMap<>();
        params.put("Lng", String.valueOf(WangLinApplication.locationInfoModel.getLongitude()));
        params.put("Lat", String.valueOf(WangLinApplication.locationInfoModel.getLatitude()));
        params.put("Len", String.valueOf(HttpUtil.NEIGHBOR_LENGTH));
        HttpUtil.sendVolleyStringRequest_Post(getActivity(), HttpUtil.neighbor_housingEstate_url, params, DBUtil.getLoginUser().getToken(),
                "yellow_neighborHousingEstateResponse", new WanglinHttpResponseListener() {
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
                            isShowMyHouseEstate(UserIdentityInfoModel.userIdentityInfoModel);
                            listView_choosedNeighborHouseEstate_houseEstateList.setAdapter(new ListViewAdapter_NeighborHouseEstate(HousingEstateModel.list_housingEstate_neighbor,
                                    getActivity(), R.layout.layout_listview_item_neighbor_house_estate));
                            //显示搜索附近小区界面（加载页出现->获取附近小区--成功-->显示附近小区界面->加载页消失）
                            //                                       |
                            //                                       |--失败-->不显示附近小区界面->加载页消失
                            layout_choosedNeighborHouseEstate.setVisibility(View.VISIBLE);
                            if (isAnim == true){
                                startToBigAnim();
                            }
                            layout_loading_page.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            layout_loading_page.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "获取附近小区失败，失败原因：解析返回结果出错，请重试！", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onConnectingError() {
                        layout_loading_page.setVisibility(View.GONE);
                    }

                    @Override
                    public void onDisconnectError() {
                        layout_loading_page.setVisibility(View.GONE);
                    }
                });
    }

    private void searchHouseEstateByKeyWord(String keyword){
        Map<String, String> params = new HashMap<>();
        params.put("Lng", String.valueOf(WangLinApplication.locationInfoModel.getLongitude()));
        params.put("Lat", String.valueOf(WangLinApplication.locationInfoModel.getLatitude()));
        params.put("RegionId", String.valueOf(WangLinApplication.locationInfoModel.getLocationCityId()));
        params.put("Key", keyword);
        HttpUtil.sendVolleyStringRequest_Post(getActivity(), HttpUtil.seatchHouseEstateByKeyWork_url, params, DBUtil.getLoginUser().getToken(),
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
                            listView_choosedNeighborHouseEstate_houseEstateList.setAdapter(new ListViewAdapter_NeighborHouseEstate(HousingEstateModel.list_housingEstate_neighbor,
                                    getActivity(), R.layout.layout_listview_item_neighbor_house_estate));
                            layout_loading_page.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            layout_loading_page.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "搜索失败，失败原因：解析返回结果出错，请重试！", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onConnectingError() {
                        layout_loading_page.setVisibility(View.GONE);
                    }
                    @Override
                    public void onDisconnectError() {
                        layout_loading_page.setVisibility(View.GONE);
                    }
                });
    }

}
