package com.wanglinkeji.wanglin.model;

import java.util.List;

/**
 * Created by Administrator on 2016/9/6.
 * 用户身份信息Model（包括：是否绑定小区，是否上传身份证，是否上传房产证等）
 */
public class UserIdentityInfoModel {
    /**
     * 身份验证状态
     * 0：未验证
     * 1：已上传
     * 2：验证中
     * 10：已验证
     */
    public static final int VERIFY_STATE_NOT_VERIFY = 0;

    public static final int VERIFY_STATE_UPLOADED = 1;

    public static final int VERIFY_STATE_VERIFY_ING = 2;

    public static final int VERIFY_STATE_HAS_VERIVY = 10;

    public static UserIdentityInfoModel userIdentityInfoModel;

    //用户当前选择的小区
    public static HousingEstateModel current_housingEstate;

    //登录用户昵称
    public static String userName;

    //获取身份信息失败时调用方法
    public static void setFailedGetInfo(UserIdentityInfoModel userIdentityInfoModel){
        current_housingEstate = null;
    }

    //用户ID
    private int userId;

    //绑定小区数量
    private int housingEstateNum;

    //绑定的小区List
    private List<HousingEstateModel> list_housingEstate;

    //身份证正面照URL
    private String IDCardImage_front;

    //身份证背面照URL
    private String IDCardImage_reserveSide;

    //身份验证状态
    private int verifyState;

    //根据用户获取的身份信息判定用户是否有默认的小区
    public static boolean hasDefultHouseEstate(){
        for (int i = 0; i < userIdentityInfoModel.getList_housingEstate().size(); i++){
            List<UserHouseModel> list_house_temp = userIdentityInfoModel.getList_housingEstate().get(i).getList_house();
            for (int j = 0; j < list_house_temp.size(); j++){
                if (list_house_temp.get(j).isDefault() == true){
                    return true;
                }
            }
        }
        return false;
    }

    //获得当前用户的默认小区
    public static HousingEstateModel getDefaultHouseEstate(){
        for (int i = 0; i < userIdentityInfoModel.getList_housingEstate().size(); i++){
            List<UserHouseModel> list_house_temp = userIdentityInfoModel.getList_housingEstate().get(i).getList_house();
            for (int j = 0; j < list_house_temp.size(); j++){
                if (list_house_temp.get(j).isDefault() == true){
                    return userIdentityInfoModel.getList_housingEstate().get(i);
                }
            }
        }
        return null;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getHousingEstateNum() {
        return housingEstateNum;
    }

    public void setHousingEstateNum(int housingEstateNum) {
        this.housingEstateNum = housingEstateNum;
        //如果用户绑定的小区数为0，则表示当前没有选择的小区
        if (housingEstateNum == 0){
            current_housingEstate = null;
        }
    }

    public List<HousingEstateModel> getList_housingEstate() {
        return list_housingEstate;
    }

    public void setList_housingEstate(List<HousingEstateModel> list_housingEstate) {
        this.list_housingEstate = list_housingEstate;
        //这里根据得到的绑定小区List来设置当前小区，如果没有则为null
    }

    public String getIDCardImage_front() {
        return IDCardImage_front;
    }

    public void setIDCardImage_front(String IDCardImage_front) {
        this.IDCardImage_front = IDCardImage_front;
    }

    public String getIDCardImage_reserveSide() {
        return IDCardImage_reserveSide;
    }

    public void setIDCardImage_reserveSide(String IDCardImage_reserveSide) {
        this.IDCardImage_reserveSide = IDCardImage_reserveSide;
    }

    public int getVerifyState() {
        return verifyState;
    }

    public void setVerifyState(int verifyState) {
        this.verifyState = verifyState;
    }
}
