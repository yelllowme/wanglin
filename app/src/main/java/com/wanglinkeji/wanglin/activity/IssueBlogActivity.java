package com.wanglinkeji.wanglin.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.adapter.GridViewAdapter_IssueBlog_ImageList;
import com.wanglinkeji.wanglin.model.AtItemModel;
import com.wanglinkeji.wanglin.model.PhotoModel;
import com.wanglinkeji.wanglin.model.UserIdentityInfoModel;
import com.wanglinkeji.wanglin.util.DBUtil;
import com.wanglinkeji.wanglin.util.HttpUtil;
import com.wanglinkeji.wanglin.util.NoLineClickSpan;
import com.wanglinkeji.wanglin.util.OtherUtil;
import com.wanglinkeji.wanglin.util.WangLinApplication;
import com.wanglinkeji.wanglin.util.WanglinHttpResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.rockerhieu.emojicon.EmojiconEditText;
import io.github.rockerhieu.emojicon.EmojiconGridFragment;
import io.github.rockerhieu.emojicon.EmojiconsFragment;
import io.github.rockerhieu.emojicon.emoji.Emojicon;

/**
 * Created by Administrator on 2016/9/21.
 * 发表吐槽页
 */

public class IssueBlogActivity extends FragmentActivity implements View.OnClickListener,EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {

    public static final int WHAT_FROM_HOUSE_ESTATE = 1, WHAT_FROM_CITY = 2;

    private ImageView imageView_cancle, imageView_isChoosedToIssueCity, imageView_addImage, imageView_aitUser, imageView_addFaceImage, imageView_isAnony;

    private TextView textView_issueBtn, textView_remianingTextNum;

    private EmojiconEditText editText_blogText;

    private LinearLayout layout_isChoosedToIssueCity, layout_isAnony;

    private FrameLayout layout_emojiIcon;

    private GridView gridView_imageList;

    private PopupWindow loading_page;

    private View rootView;

    //上传的图片数组
    private List<String> images = new ArrayList<>();

    //上传图片数组的顺序记录
    private int image_order = 0;

    //发表吐槽时的小区Id和城市Id
    private int houseEstateId, cityId;

    //选择是否发布到同城吐槽
    private boolean isChoosedToIssurCity = false;

    //是否匿名发布
    private boolean isAnony = false;

    //来源：发表小区吐槽或者同城吐槽
    private int whatFrom;

    private boolean isSetTextChangeListener = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_activity_issue_blog);

        //刚进入这个界面时，选中图片List为空
        PhotoModel.list_choosedPhotos = new ArrayList<>();
        //@Item List也为空
        AtItemModel.list_AtItem = new ArrayList<>();

        viewInit();
        setView_EmojiNotShow();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //此处为了获取软键盘高度，手动弹出一次软键盘，并马上消失
        editText_blogText.setFocusable(true);
        editText_blogText.setFocusableInTouchMode(true);
        editText_blogText.requestFocus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            //从选择图片返回
            case OtherUtil.REQUEST_CODE_ISSUE_BLOG_TO_CHOOSED_PHOTO:{
                if (resultCode == RESULT_OK){
                    gridView_imageList.setAdapter(new GridViewAdapter_IssueBlog_ImageList(PhotoModel.list_choosedPhotos, IssueBlogActivity.this,
                            R.layout.layout_gridview_item_housing_estate_nine_image));
                }
                break;
            }
            //从@好友返回
            case OtherUtil.REQUEST_CODE_ISSUE_BLOG_TO_AT_USER:{
                if (resultCode == RESULT_OK){
                    //@之后的Edittext内容
                    String blog_text = editText_blogText.getText().toString();
                    int selectionStart = editText_blogText.getSelectionStart();
                    String[] temp = new String[]{blog_text.substring(0, selectionStart), blog_text.substring(selectionStart, blog_text.length())};
                    String current_text = temp[0] + "@" + AtItemModel.current_AtItem.getContent() + temp[1];
                    //添加Item之前改变其他Item
                    for (int i = 0; i < AtItemModel.list_AtItem.size(); i++){
                        if (AtItemModel.list_AtItem.get(i).getStartIndex() >= selectionStart){
                            AtItemModel.list_AtItem.get(i).setStartIndex(AtItemModel.list_AtItem.get(i).getStartIndex() + AtItemModel.current_AtItem.getContent().length() + 1);
                            AtItemModel.list_AtItem.get(i).setEndIndex(AtItemModel.list_AtItem.get(i).getEndIndex() + AtItemModel.current_AtItem.getContent().length() + 1);
                        }
                    }
                    //向list_AtItem中添加元素
                    AtItemModel atItem = new AtItemModel(selectionStart, selectionStart + AtItemModel.current_AtItem.getContent().length(), "@" + AtItemModel.current_AtItem.getContent());
                    atItem.setAtId(AtItemModel.current_AtItem.getAtId());
                    atItem.setFromWhat(AtItemModel.current_AtItem.getFromWhat());
                    AtItemModel.list_AtItem.add(atItem);
                    setEditText_afterAt(current_text, AtItemModel.list_AtItem, editText_blogText, editText_blogText.getSelectionStart() + atItem.getContent().length());
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
            case R.id.imageview_issueBlog_cancle:{
                IssueBlogActivity.this.finish();
                break;
            }
            //发表按钮
            case R.id.textview_issueBlog_issueBtn:{
                if (PhotoModel.list_choosedPhotos.size() == 0 && editText_blogText.getText().length() == 0){
                    Toast.makeText(IssueBlogActivity.this, "吐槽不能为空哦！", Toast.LENGTH_SHORT).show();
                }else {
                    if (whatFrom == WHAT_FROM_HOUSE_ESTATE){
                        if (isChoosedToIssurCity == true){
                            cityId = UserIdentityInfoModel.getDefaultHouseEstate().getCityId();
                            houseEstateId = UserIdentityInfoModel.getDefaultHouseEstate().getId();
                        }else {
                            houseEstateId = UserIdentityInfoModel.getDefaultHouseEstate().getId();
                            cityId = 0;
                        }
                    }else if (whatFrom == WHAT_FROM_CITY){
                        houseEstateId = 0;
                        cityId = WangLinApplication.locationInfoModel.getLocationCityId();
                    }else {
                        houseEstateId = 0;
                        cityId = WangLinApplication.locationInfoModel.getLocationCityId();
                    }
                    loading_page.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(view,InputMethodManager.SHOW_FORCED);
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘

                    //判断是否有上传的图片
                    if (PhotoModel.list_choosedPhotos.size() > 0){
                        RequestParams requestParams = new RequestParams();
                        requestParams.addBodyParameter("File", new File(PhotoModel.list_choosedPhotos.get(image_order).getPath()));
                        uploadFirstImage(requestParams, HttpUtil.issBlog_uploadImage_url);
                    }else {
                        issueBlog(IssueBlogActivity.this, editText_blogText.getText().toString(), houseEstateId,
                                WangLinApplication.locationInfoModel.getLocatonCity(), cityId, WangLinApplication.locationInfoModel.getLongitude(),
                                WangLinApplication.locationInfoModel.getLatitude(), WangLinApplication.locationInfoModel.getAddress(), isAnony, images);
                    }
                }
                break;
            }
            //同时发表到同城吐槽
            case R.id.layout_issueBlog_issueToCityBlog:{
                if (isChoosedToIssurCity == true){
                    isChoosedToIssurCity = false;
                    imageView_isChoosedToIssueCity.setBackgroundResource(R.mipmap.rectangle_not_choosed_gray);
                }else {
                    isChoosedToIssurCity = true;
                    imageView_isChoosedToIssueCity.setBackgroundResource(R.mipmap.photo_choosed_icon);
                }
                break;
            }
            //是否匿名
            case R.id.layout_issueBlog_isAnony:{
                if (isAnony == true){
                    isAnony = false;
                    imageView_isAnony.setBackgroundResource(R.mipmap.rectangle_not_choosed_gray);
                }else {
                    isAnony = true;
                    imageView_isAnony.setBackgroundResource(R.mipmap.photo_choosed_icon);
                }
                break;
            }
            //选择图片
            case R.id.imageview_issueBlog_addImage:{
                PhotoModel.finishText = "完成";
                PhotoModel.photoCount = 9;
                Intent intent = new Intent(IssueBlogActivity.this, ChoosedPhoto_SmallActivity.class);
                startActivityForResult(intent, OtherUtil.REQUEST_CODE_ISSUE_BLOG_TO_CHOOSED_PHOTO);
                break;
            }
            //@好友
            case R.id.imageview_issueBlog_aitUser:{
                if (isChangeAtList_Add(editText_blogText.getSelectionStart(), AtItemModel.list_AtItem) >= 0){
                    Toast.makeText(IssueBlogActivity.this, "此处不能@，请将光标移到其他地方！", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(IssueBlogActivity.this, AT_UserActivity.class);
                    startActivityForResult(intent, OtherUtil.REQUEST_CODE_ISSUE_BLOG_TO_AT_USER);
                }
                break;
            }
            //选择表情
            case R.id.imageview_issueBlog_addFaceImage:{
                Toast.makeText(IssueBlogActivity.this, "目前只支持输入法自带Emoji表情！", Toast.LENGTH_SHORT).show();
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(editText_blogText, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(editText_blogText);
    }

    private void viewInit(){
        whatFrom = getIntent().getIntExtra("whatFrom", WHAT_FROM_CITY);

        //设置EmojiIcon
        layout_emojiIcon = (FrameLayout) findViewById(R.id.layout_isssueBlog_emojiIcon);
        OtherUtil.setViewLayoutParams(layout_emojiIcon, false, 2.5f, 1);
        getSupportFragmentManager().beginTransaction().replace(R.id.layout_isssueBlog_emojiIcon, EmojiconsFragment.newInstance(false)).commit();

        loading_page = OtherUtil.getLoadingPage(IssueBlogActivity.this);
        rootView = LayoutInflater.from(IssueBlogActivity.this).inflate(R.layout.layout_activity_issue_blog, null);
        imageView_isAnony = (ImageView)findViewById(R.id.imageview_issueBlog_isAnony);
        layout_isAnony = (LinearLayout)findViewById(R.id.layout_issueBlog_isAnony);
        layout_isAnony.setOnClickListener(this);
        imageView_cancle = (ImageView)findViewById(R.id.imageview_issueBlog_cancle);
        imageView_cancle.setOnClickListener(this);
        imageView_isChoosedToIssueCity = (ImageView)findViewById(R.id.imageview_issueBlog_isChoosedToIssueCityBlog);
        imageView_addImage = (ImageView)findViewById(R.id.imageview_issueBlog_addImage);
        imageView_addImage.setOnClickListener(this);
        imageView_aitUser = (ImageView)findViewById(R.id.imageview_issueBlog_aitUser);
        imageView_aitUser.setOnClickListener(this);
        imageView_addFaceImage = (ImageView)findViewById(R.id.imageview_issueBlog_addFaceImage);
        imageView_addFaceImage.setOnClickListener(this);
        textView_issueBtn = (TextView)findViewById(R.id.textview_issueBlog_issueBtn);
        textView_issueBtn.setOnClickListener(this);
        textView_remianingTextNum = (TextView)findViewById(R.id.textview_issueBlog_remainingTextNum);
        textView_remianingTextNum.setText("剩余" + WangLinApplication.BLOG_MAX_LENGTH +"字");
        layout_isChoosedToIssueCity = (LinearLayout)findViewById(R.id.layout_issueBlog_issueToCityBlog);
        layout_isChoosedToIssueCity.setOnClickListener(this);
        gridView_imageList = (GridView)findViewById(R.id.gridview_issueBlog_imageList);
        editText_blogText = (EmojiconEditText) findViewById(R.id.edittext_issueBlog_blogText);
        OtherUtil.setViewLayoutParams(editText_blogText, false, 3, 1);
        editText_blogText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(WangLinApplication.BLOG_MAX_LENGTH)});
        editText_blogText.addTextChangedListener(new TextWatcher() {
            int selection;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int changeStartIndex, int deleteCount, int addCount) {
                //charSequence--未改变之前的内容
                //changeStartIndex--内容被改变的开始位置
                //deleteCount--原始文字被删除的个数
                //addCount--新添加的内容的个数
                if (isSetTextChangeListener == true){
                    //Log.d("yellow_temp_before", "charSequence：" + charSequence + "---changeStartIndex：" + changeStartIndex + "---deleteCount：" + deleteCount + "---addCount：" + addCount) ;
                    //根据用户是增加还是删除内容
                    //如果是删除内容
                    if (deleteCount > 0){
                        selection = changeStartIndex;
                        isChangeAtList_Delete(changeStartIndex, AtItemModel.list_AtItem, deleteCount);
                        //修改被编辑@Item后面的Item
                        for (int i = 0; i < AtItemModel.list_AtItem.size(); i++){
                            if (AtItemModel.list_AtItem.get(i).getStartIndex() > changeStartIndex){
                                AtItemModel.list_AtItem.get(i).setStartIndex(AtItemModel.list_AtItem.get(i).getStartIndex() - deleteCount);
                                AtItemModel.list_AtItem.get(i).setEndIndex(AtItemModel.list_AtItem.get(i).getEndIndex() - deleteCount);
                            }
                        }
                        //如果是增加内容
                    }
                    if (addCount > 0){
                        selection = changeStartIndex + addCount;
                        int position = isChangeAtList_Add(changeStartIndex, AtItemModel.list_AtItem);
                        //position >=0 表示编辑了@列表
                        if (position >= 0){
                            AtItemModel.list_AtItem.remove(position);
                        }
                        //修改被编辑@Item后面的Item
                        for (int i = 0; i < AtItemModel.list_AtItem.size(); i++){
                            if (AtItemModel.list_AtItem.get(i).getStartIndex() >= changeStartIndex){
                                AtItemModel.list_AtItem.get(i).setStartIndex(AtItemModel.list_AtItem.get(i).getStartIndex() + addCount);
                                AtItemModel.list_AtItem.get(i).setEndIndex(AtItemModel.list_AtItem.get(i).getEndIndex() + addCount);
                            }
                        }
                    }
                }
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int changeStartIndex, int deleteCount, int addCount) {
                //charSequence--改变之后的新内容
                //changeStartIndex--内容被改变的开始位置
                //deleteCount--原始文字被删除的个数
                //addCount--新添加的内容的个数
                if (isSetTextChangeListener == true){
                    //Log.d("yellow_temp_on", "charSequence：" + charSequence + "---changeStartIndex：" + changeStartIndex + "---deleteCount：" + deleteCount + "---addCount：" + addCount) ;
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                //editable最终内容
                if (isSetTextChangeListener == true){
                    //Log.d("yellow_temp_after", editable.toString());
                    setEditText_afterAt(editable.toString(), AtItemModel.list_AtItem, editText_blogText, selection);
                }
                //统计字数
                textView_remianingTextNum.setText("剩余" + (WangLinApplication.BLOG_MAX_LENGTH - editText_blogText.getText().length()) +"字");
            }
        });
    }

    //被修改的位置是否在@列表中的某个Item中，如果是，返回这个Item的postion，如果不是返回-1
    private void isChangeAtList_Delete(int changeStartIndex,List<AtItemModel> list_AtItem, int deleteCount){
        List<AtItemModel> list_AtItem_temp = new ArrayList<>();
        for (int i = 0; i < list_AtItem.size(); i++){
            for (int j = 0; j < deleteCount; j++){
                if (changeStartIndex + j >= list_AtItem.get(i).getStartIndex() && changeStartIndex + j <= list_AtItem.get(i).getEndIndex()){
                    list_AtItem_temp.add(list_AtItem.get(i));
                    break;
                }
            }
        }
        for (int i = 0; i < list_AtItem_temp.size(); i++){
            list_AtItem.remove(list_AtItem_temp.get(i));
        }
    }

    //被修改的位置是否在@列表中的某个Item中，如果是，返回这个Item的postion，如果不是返回-1
    private int isChangeAtList_Add(int changeStartIndex,List<AtItemModel> list_AtItem){
        int position = -1;
        for (int i = 0; i < list_AtItem.size(); i++){
            if (changeStartIndex > list_AtItem.get(i).getStartIndex() && changeStartIndex <= list_AtItem.get(i).getEndIndex()){
                return i;
            }
        }
        return position;
    }

    //根据@列表(list_AtItem)，设置EditText的样式
    private void setEditText_afterAt(String current_text, List<AtItemModel> list_AtItem, EditText editText, int selection){
        //设置@的内容样式
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(current_text);
        for (int i = 0; i < list_AtItem.size(); i++){
            spannableStringBuilder.setSpan(new NoLineClickSpan(), list_AtItem.get(i).getStartIndex(), list_AtItem.get(i).getEndIndex() + 1,
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        isSetTextChangeListener = false;
        editText.setText(spannableStringBuilder);
        isSetTextChangeListener = true;
        editText.setSelection(selection);
    }

    private void setView_EmojiNotShow(){
        //设置“匿名发布”和“同时发布到同城吐槽”
        if (whatFrom == WHAT_FROM_HOUSE_ESTATE){
            layout_isChoosedToIssueCity.setVisibility(View.VISIBLE);
        }else if (whatFrom == WHAT_FROM_CITY){
            RelativeLayout.LayoutParams params_isAnony = (RelativeLayout.LayoutParams)layout_isAnony.getLayoutParams();
            params_isAnony.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params_isAnony.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layout_isAnony.setLayoutParams(params_isAnony);
            layout_isChoosedToIssueCity.setVisibility(View.GONE);
        }
    }

    private void issueBlog(final Context context, String content, int houseEstateId, String cityName, int regionId, double Lng, double Lat, String Address, boolean isAnony,
                           List<String> imageList){
        Log.d("yellow_temp", content + "---" + houseEstateId + "---" + cityName + "---" + regionId + "---" + Lng + "---" + Lat + "---" + Address + "---" + isAnony);
        Map<String, String> params = new HashMap<>();
        params.put("Content", content);
        params.put("VillageId", String.valueOf(houseEstateId));
        params.put("CityName", cityName);
        params.put("RegionId", String.valueOf(regionId)); //如果选择发布到同城吐槽则填真实区域ID，反之填0
        params.put("Lng", String.valueOf(Lng));
        params.put("Lat", String.valueOf(Lat));
        params.put("Address", Address);
        params.put("IsAnonymous", String.valueOf(isAnony));
        for (int i = 0; i < imageList.size(); i++){
            params.put("Imgs" + "[" + i + "]", imageList.get(i));
        }
        List<AtItemModel> list_temp_atUser = new ArrayList<>();
        List<AtItemModel> list_temp_atNeighbor = new ArrayList<>();
        for (int i = 0; i < AtItemModel.list_AtItem.size(); i++){
           if (AtItemModel.list_AtItem.get(i).getFromWhat() == AT_UserActivity.FROM_WHAT_AT_USER){
               list_temp_atUser.add(AtItemModel.list_AtItem.get(i));
           }else if (AtItemModel.list_AtItem.get(i).getFromWhat() == AT_UserActivity.FROM_WHAT_AT_NEIGHBOR){
               list_temp_atNeighbor.add(AtItemModel.list_AtItem.get(i));
            }
        }
        for (int i = 0; i < list_temp_atUser.size(); i++){
            params.put("AtUserId" + "[" + i + "]", String.valueOf(list_temp_atUser.get(i).getAtId()));
            params.put("AtUser" + "[" + i + "]", list_temp_atUser.get(i).getContent());
            //Log.d("yellow_temp", "AtUserId" + "[" + i + "]：" + String.valueOf(list_temp_atUser.get(i).getAtId()) + "  AtUser" + "[" + i + "]：" + list_temp_atUser.get(i).getContent());
        }
        for (int i = 0; i < list_temp_atNeighbor.size(); i++){
            params.put("HouseId" + "[" + i + "]", String.valueOf(list_temp_atUser.get(i).getAtId()));
            params.put("House" + "[" + i + "]", list_temp_atUser.get(i).getContent());
        }
        if (AtItemModel.list_AtItem.size() > 0){
            params.put("ParmStr1", ((new Gson()).toJson(AtItemModel.list_AtItem)).toString());
            Log.d("yellow_temp", "ParmStr1：" + ((new Gson()).toJson(AtItemModel.list_AtItem)).toString());
        }
        HttpUtil.sendVolleyStringRequest_Post(context, HttpUtil.issueBlog_url, params, DBUtil.getLoginUser().getToken(), "yellow_issueBlog", new WanglinHttpResponseListener() {
            @Override
            public void onSuccessResponse(JSONObject jsonObject_response) {
                loading_page.dismiss();
                Toast.makeText(context, "发布成功，请手动刷新", Toast.LENGTH_SHORT).show();
                IssueBlogActivity.this.finish();
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

    private void uploadFirstImage(RequestParams requestParams, final String url){
        HttpUtils httpUtils = new HttpUtils();

        httpUtils.send(HttpRequest.HttpMethod.POST, url, requestParams, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.d("yellow_uploadImage_first", responseInfo.result);
                try {
                    JSONObject jsonObject_response = new JSONObject(responseInfo.result);
                    int requestCode = jsonObject_response.getInt("Code");
                    if (requestCode == 200){
                        image_order++;
                        images.add(jsonObject_response.getString("Data"));
                        if (PhotoModel.list_choosedPhotos.size() > 1){
                            RequestParams requestParams_other = new RequestParams();
                            requestParams_other.addBodyParameter("File", new File(PhotoModel.list_choosedPhotos.get(image_order).getPath()));
                            uploadOtherImage(requestParams_other, url);
                        }else {
                            issueBlog(IssueBlogActivity.this, editText_blogText.getText().toString(), houseEstateId,
                                    UserIdentityInfoModel.getDefaultHouseEstate().getCity(), cityId, UserIdentityInfoModel.getDefaultHouseEstate().getLng(),
                                    UserIdentityInfoModel.getDefaultHouseEstate().getLat(), WangLinApplication.locationInfoModel.getAddress(), isAnony, images);
                        }
                    }else {
                        String msg = jsonObject_response.getString("Msg");
                        HttpUtil.parseResponseCode(IssueBlogActivity.this, requestCode, msg);
                        loading_page.dismiss();
                    }
                } catch (JSONException e) {
                    Toast.makeText(IssueBlogActivity.this, "上传图片失败，失败原因：解析返回数据失败，请反馈！", Toast.LENGTH_SHORT).show();
                    loading_page.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                e.printStackTrace();
                Toast.makeText(IssueBlogActivity.this, "上传图片失败，请稍后重试！", Toast.LENGTH_SHORT).show();
                loading_page.dismiss();
            }
        });
    }

    private void uploadOtherImage(RequestParams requestParams, final String url){
        HttpUtils httpUtils = new HttpUtils();

        httpUtils.send(HttpRequest.HttpMethod.POST, url, requestParams, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.d("yellow_uploadImage_other", responseInfo.result);
                try {
                    JSONObject jsonObject_response = new JSONObject(responseInfo.result);
                    int requestCode = jsonObject_response.getInt("Code");
                    if (requestCode == 200){
                        image_order++;
                        images.add(jsonObject_response.getString("Data"));
                        if (image_order < PhotoModel.list_choosedPhotos.size()){
                            RequestParams requestParams_other = new RequestParams();
                            requestParams_other.addBodyParameter("File", new File(PhotoModel.list_choosedPhotos.get(image_order).getPath()));
                            uploadOtherImage(requestParams_other, url);
                        }else {
                            issueBlog(IssueBlogActivity.this, editText_blogText.getText().toString(), houseEstateId,
                                    UserIdentityInfoModel.getDefaultHouseEstate().getCity(), cityId, UserIdentityInfoModel.getDefaultHouseEstate().getLng(),
                                    UserIdentityInfoModel.getDefaultHouseEstate().getLat(), WangLinApplication.locationInfoModel.getAddress(), isAnony, images);
                        }
                    }else {
                        loading_page.dismiss();
                        String msg = jsonObject_response.getString("Msg");
                        HttpUtil.parseResponseCode(IssueBlogActivity.this, requestCode, msg);
                    }
                } catch (JSONException e) {
                    Toast.makeText(IssueBlogActivity.this, "上传图片失败，失败原因：解析返回数据失败，请反馈！", Toast.LENGTH_SHORT).show();
                    loading_page.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                e.printStackTrace();
                loading_page.dismiss();
                Toast.makeText(IssueBlogActivity.this, "上传图片失败，请稍后重试！", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
