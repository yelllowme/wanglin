package com.wanglinkeji.wanglin.model;

/**
 * Created by Administrator on 2016/9/8.
 * 用户房间Model
 */
public class UserRoomModel {

    /**
     * 房屋租赁合同证验证状态
     * 0：未上传
     * 1：已上传
     * 2：验证中
     * 3：未通过
     * 10：已验证
     */

    public static final int VERIFY_ROOM_CERRIFICATE_STATE_NOT_UPLOAD = 0;

    public static final int VERIFY_ROOM_CERRIFICATE_STATE_UPLOAD = 1;

    public static final int VERIFY_ROOM_CERRIFICATE_STATE_ING = 2;

    public static final int VERIFY_ROOM_CERRIFICATE_STATE_NOT_PASS = 3;

    public static final int VERIFY_ROOM_CERRIFICATE_STATE_HAS_PASS = 10;

    //租房合同URL
    private String rentRoomImage;

    //租房合同验证状态
    private int RentHouseImgVerify;

    //租户ID
    private int userId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getRentRoomImage() {
        return rentRoomImage;
    }

    public void setRentRoomImage(String rentRoomImage) {
        this.rentRoomImage = rentRoomImage;
    }

    public int getRentHouseImgVerify() {
        return RentHouseImgVerify;
    }

    public void setRentHouseImgVerify(int rentHouseImgVerify) {
        RentHouseImgVerify = rentHouseImgVerify;
    }
}
