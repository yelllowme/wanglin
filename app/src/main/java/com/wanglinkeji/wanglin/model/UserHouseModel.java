package com.wanglinkeji.wanglin.model;

import java.util.List;

/**
 * Created by Administrator on 2016/9/7.
 * 用户所拥有的住房Model
 */
public class UserHouseModel {
    /**
     * 身份信息
     * -1：未知
     * 0：业主
     * 1：租户
     */
    public static final int USER_IDENTITY_UNKNOW = -1;

    public static final int USER_IDENTITY_PROPRIETOR = 0;

    public static final int USER_IDENTITY_RENTMENT = 1;

    /**
     * 房产证验证状态
     * 0：未上传
     * 1：已上传
     * 2：验证中
     * 3：验证失败
     * 10：已验证
     */

    public static final int VERIFY_HOUSE_CERRIFICATE_STATE_NOT_UPLOAD = 0;

    public static final int VERIFY_HOUSE_CERRIFICATE_STATE_UPLOAD = 1;

    public static final int VERIFY_HOUSE_CERRIFICATE_STATE_ING = 2;

    public static final int VERIFY_HOUSE_CERRIFICATE_STATE_NOT_PASS = 3;

    public static final int VERIFY_HOUSE_CERRIFICATE_STATE_HAS_PASS = 10;

    //住房ID
    private int houseId;

    //所在小区ID
    private int houseEstateId;

    //住房用户ID
    private int houseUserId;

    //住房用户身份
    private int userIdentity;

    //住房所在栋数
    private int ridgepoleNum;

    //住房所在单元
    private int unitNum;

    //住房所在楼层
    private int floorNum;

    //住房所在房号
    private String roomNum;

    //住房房产证URL
    private String HouseCertificateImg;

    //是否验证房产证
    private int IsVerifyHouseCertificate;

    //租赁合同图片Url
    private String rentContractImg;

    //租赁合同验证状态
    private int IsVerifyRentContract;

    //所在小区是否是默认小区
    private boolean isDefault;

    //我的小区列表中是否显示侧拉框
    private boolean isShowDialog;

    //住房所在小区名
    private String houseEstateName;

    public int getUserIdentity() {
        return userIdentity;
    }

    public void setUserIdentity(int userIdentity) {
        this.userIdentity = userIdentity;
    }

    public String getRentContractImg() {
        return rentContractImg;
    }

    public void setRentContractImg(String rentContractImg) {
        this.rentContractImg = rentContractImg;
    }

    public int getIsVerifyRentContract() {
        return IsVerifyRentContract;
    }

    public void setIsVerifyRentContract(int isVerifyRentContract) {
        IsVerifyRentContract = isVerifyRentContract;
    }

    public int getHouseUserId() {
        return houseUserId;
    }

    public void setHouseUserId(int houseUserId) {
        this.houseUserId = houseUserId;
    }

    public int getHouseEstateId() {
        return houseEstateId;
    }

    public void setHouseEstateId(int houseEstateId) {
        this.houseEstateId = houseEstateId;
    }

    public String getHouseEstateName() {
        return houseEstateName;
    }

    public void setHouseEstateName(String houseEstateName) {
        this.houseEstateName = houseEstateName;
    }

    public boolean isShowDialog() {
        return isShowDialog;
    }

    public void setShowDialog(boolean showDialog) {
        isShowDialog = showDialog;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public int getHouseId() {
        return houseId;
    }

    public void setHouseId(int houseId) {
        this.houseId = houseId;
    }

    public int getRidgepoleNum() {
        return ridgepoleNum;
    }

    public void setRidgepoleNum(int ridgepoleNum) {
        this.ridgepoleNum = ridgepoleNum;
    }

    public int getUnitNum() {
        return unitNum;
    }

    public void setUnitNum(int unitNum) {
        this.unitNum = unitNum;
    }

    public int getFloorNum() {
        return floorNum;
    }

    public void setFloorNum(int floorNum) {
        this.floorNum = floorNum;
    }

    public String getRoomNum() {
        return roomNum;
    }

    public void setRoomNum(String roomNum) {
        this.roomNum = roomNum;
    }

    public String getHouseCertificateImg() {
        return HouseCertificateImg;
    }

    public void setHouseCertificateImg(String houseCertificateImg) {
        HouseCertificateImg = houseCertificateImg;
    }

    public int getIsVerifyHouseCertificate() {
        return IsVerifyHouseCertificate;
    }

    public void setIsVerifyHouseCertificate(int isVerifyHouseCertificate) {
        IsVerifyHouseCertificate = isVerifyHouseCertificate;
    }
}
