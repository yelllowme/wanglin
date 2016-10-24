package com.wanglinkeji.wanglin.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lidroid.xutils.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/24.
 * 网络请求工具类
 */
public class HttpUtil {

    //APP应用ID
    public static final int APP_Id = 1;

    //APP_Key
    public static final String APP_KEY = "PxProj_2016@08";

    //访问天气AK
    public static final String BAIDU_WEATHER_AK = "61fc5332c6496fdfdc735447aa07ad39";

    //天气访问Mcode
    public static final String BAIDU_WEATHER_MCODE = "6B:F9:40:9C:20:D7:EA:18:29:85:C8:9E:06:FB:C6:C3:BA:AA:7F:39:com.wanglinkeji.wanglin";

    //IM_APP_ID
    public static final String IM_APP_ID = "8aaf070857dc0e780157e5bf0d350e02";

    //IM_APP_TOKEN
    public static final String IM_APP_TOKEN = "794d998a6868ee93ad6159f8488f6b28";

    /**
     * Http请求返回状态值
     * 200：成功
     * 300：权限不足
     * 301：签名错误
     * 302：用户未登录
     * 400：服务器内部错误(程序出错，Msg调试用)
     * 500：业务逻辑错误（如用户名不存在等，可将Msg原样呈现给用户）
     */
    public static final int HTTP_RESPONSE_STATE_OK = 200;

    public static final int HTTP_RESPONSE_STATE_PERMISSION_REFUSE = 300;

    public static final int HTTP_RESPONSE_STATE_SIGN_ERROR = 301;

    public static final int HTTP_REQUEST_STATE_LOGIN_ERROR = 302;

    public static final int HTTP_RESPONSE_STATE_SERVER_WRONG = 400;

    public static final int HTTP_RESPONSE_STATE_CLIENT_ERROR = 500;

    /**
     * 一些全局固定值
     */
    //分页请求每页大小
    public static final int PAGE_SIZE = 15;

    //附近小区范围（单位：千米）
    public static final int NEIGHBOR_LENGTH = 5;

    /**
     * URL
     */
    //域名
    public static String url_header = "http://api.px.ujlol.com";

    /*路径*/
    //登录
    public static String login_url = url_header + "/Users/login";

    //附近小区列表
    public static String neighbor_housingEstate_url = url_header + "/Village/Nearby";

    //城市天气
    public static String locationWeather_url = url_header + "/tool/Weather";

    //小区吐槽列表
    public static String housingEstateSwpeingList_url = url_header + "/Complain/SelectByVillage";

    //同城吐槽列表
    public static String citySwpeingList_url = url_header + "/Complain/SelectByCity";

    //点赞
    public static String blog_addGood_url = url_header + "/Complain/Good";

    //取消点赞
    public static String blog_subGood_url = url_header + "/Complain/CancelGood";

    //获取吐槽详情
    public static String getBlogDetails_url = url_header + "/Complain/Find";

    //发送短信验证码
    public static String getverifivationCode_url = url_header + "/tool/SendMobileCode";

    //注册
    public static String register_url = url_header + "/Users/Register";

    //退出登录
    public static String logout_url = url_header + "/Users/LoginOut";

    //获取用户详细信息
    public static String getUserIdentityInfo_url = url_header + "/Users/LoginUserInfo";

    //根据城市名获取城市ID
    public static String getCityId_ByCityName_url = url_header + "/tool/RegionFind";

    //根据关键字搜索同城小区
    public static String seatchHouseEstateByKeyWork_url = url_header + "/Village/Select";

    //注册第二步
    public static String bindingHouseEstate_url = url_header + "/Users/RegisterSetup2";

    //上传身份证正面
    public static String uploadIDCardFront_url = url_header + "/Users/UpdateIDZMImg";

    //上传身份证反面
    public static String uploadIDCardResever_url = url_header + "/Users/UpdateIDFMImg";

    //上传房产证
    public static String uploadHouseProprietaryCertificate_url = url_header + "/Village/UpdateHouseCertificateImg";

    //上传租赁合同
    public static String uploadRentContract_url = url_header + "/Village/UpdateRentHouseImg";

    //增加绑定小区
    public static String addBindingHouseEstate_url = url_header + "/Users/UserInVillage";

    //设置默认小区
    public static String setDefaultHouseEstate_url = url_header + "/Village/SetDefaultVillage";

    //设置默认房屋
    public static String setDefaultHouse_url = url_header + "/Village/SetDefaultHouse";

    //删除住房
    public static String deleteHouse_url = url_header + "/Users/LeaveHouse";

    //发表吐槽
    public static String issueBlog_url = url_header + "/Complain/Send";

    //吐槽上传图片
    public static String issBlog_uploadImage_url = url_header + "/Tool/UpImg";

    //回复吐槽
    public static String addComment_url = url_header + "/Complain/Reply";

    //消息接口
    public static String getMessage_url = url_header + "/Users/SelectUserMsg";

    //获取好友列表
    public static String getUserFriendList_url = url_header + "/Users/SelectUserFriendGroup";

    //添加好友
    public static String addFriend_url = url_header + "/Users/ApplyAddUserFriend";

    //接受添加好友
    public static String acceptNewFriend_url = url_header + "/Users/AgreeAddUserFriend";

    //拒绝添加好友
    public static String refuseNewFriend_url = url_header + "/Users/RefuseAddUserFriend";

    //@我的
    public static String aboutMe_AtMe_url = url_header + "/Complain/AtMe";

    //回复我的
    public static String aboutMe_ReplyMe_url = url_header + "/Complain/ReplyMe";

    //赞我的
    public static String aboutMe_GoodMe_url = url_header + "/Complain/GoodMe";

    //查找房间Id
    public static String selectHouseEstateId_url = url_header + "/Village/SelectHouse";

    //根据关键字（手机号、小区名）搜索用户
    public static String selectUserByKeyWord_url = url_header + "/users/SearchUser";

    //我的吐槽
    public static String getMyBlog_url = url_header + "/Complain/MyComplain";

    /**
     *Volley Http Post请求通用方法
     */
    public static void sendVolleyStringRequest_Post(final Context context, String url, final Map<String, String> params, final String token, final String annotation, final WanglinHttpResponseListener listener){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.d(annotation, s);
                try {
                    //获取返回状态值Code
                    JSONObject jsonObject_response = new JSONObject(s);
                    int responseCode = jsonObject_response.getInt("Code");//获取返回状态码
                    String reponseStatus = jsonObject_response.getString("Msg"); //获取返回状态内容
                    if (responseCode == HTTP_RESPONSE_STATE_OK){
                        listener.onSuccessResponse(jsonObject_response);
                    }else {
                        listener.onConnectingError();
                        parseResponseCode(context, responseCode, reponseStatus);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "解析返回数据出错，请反馈！", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                listener.onDisconnectError();
                parseVolleyError(volleyError);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                addVolleyHttpParams(params, token);
                return params;
            }
        };
        WangLinApplication.requestQueue.add(stringRequest);
    }

    /**
     *获取当前时间戳
     */
    public static long getTimeStamp(){
        Date date = new Date();
        return Math.abs(date.getTime());
    }

    /**
     * 根据token值，获取当前的签名Sign
     */
    public static String getSign(String token){
        String string = "AppId=" + APP_Id + "&" + "TimeStamp=" + getTimeStamp() + "&" + "PxToken=" + token + "&" + "AppKey=" + APP_KEY;
        //Log.d("yellow_beforeMD5", string);
        return getMD5(string);
    }

    public static void addVolleyHttpParams(Map<String, String> params, String token){
        params.put("AppId", String.valueOf(APP_Id));
        params.put("TimeStamp", String.valueOf(getTimeStamp()));
        params.put("PxToken", token);
        params.put("Sign", getSign(token));
        /*Log.d("yellow_AppId", APP_Id + "");
        Log.d("yellow_TimeStamp", String.valueOf(getTimeStamp()));
        Log.d("yellow_Pxtoken", token);
        Log.d("yellow_Sign", getSign(token));*/
    }

    public static void addXUtilHttpParams(RequestParams params, String token){
        params.addBodyParameter("AppId", String.valueOf(APP_Id));
        params.addBodyParameter("TimeStamp", String.valueOf(getTimeStamp()));
        params.addBodyParameter("PxToken", token);
        params.addBodyParameter("Sign", getSign(token));
    }

    /**
     * 得到传入String的MD5加密后的String
     */
    public static String getMD5(String info) {
        try
        {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(info.getBytes("UTF-8"));
            byte[] encryption = md5.digest();

            StringBuffer strBuf = new StringBuffer();
            for (int i = 0; i < encryption.length; i++)
            {
                if (Integer.toHexString(0xff & encryption[i]).length() == 1)
                {
                    strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
                }
                else
                {
                    strBuf.append(Integer.toHexString(0xff & encryption[i]));
                }
            }

            return strBuf.toString();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Log.d("yellow_temp", "MD5加密NoSuchAlgorithmException异常，加密失败！");
            return "MD5加密NoSuchAlgorithmException异常，加密失败！";
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.d("yellow_temp", "MD5加密UnsupportedEncodingException异常，加密失败！");
            return "MD5加密UnsupportedEncodingException异常，加密失败！";
        }
    }

    /**
     * 解析VolleyError
     */
    public static String parseVolleyError(VolleyError volleyError){
        if (volleyError instanceof AuthFailureError)
            return "Http身份验证错误，请重试！";
        else if (volleyError instanceof NetworkError)
            return "连接服务器超时，请检查网络是否畅通！";
        else if (volleyError instanceof NoConnectionError)
            return "没有授权网络连接，请检查！";
        else if (volleyError instanceof ParseError)
            return "解析返回参数错误，请反馈！";
        else if (volleyError instanceof ServerError)
            return "服务器响应错误，请稍后重试";
        else if (volleyError instanceof TimeoutError)
            return "网络连接超时，请检查网络！";
        return "未知错误，请反馈！";
    }

    /**
     * 解析服务器Response的Code值，包括除了Code=200以外的所有情况
     */
    public static void parseResponseCode(Context context, int responseCode, String responseStatus){
        switch (responseCode){
            //权限不够
            case HTTP_RESPONSE_STATE_PERMISSION_REFUSE:
                Toast.makeText(context, responseStatus + "请确认！", Toast.LENGTH_SHORT).show();
                break;
            //签名错误
            case HTTP_RESPONSE_STATE_SIGN_ERROR:
                Toast.makeText(context, responseStatus + "，请稍后重新登录！", Toast.LENGTH_SHORT).show();
                break;
            //登录状态错误
            case HTTP_REQUEST_STATE_LOGIN_ERROR:
                Toast.makeText(context, responseStatus + "，请重新登录！", Toast.LENGTH_SHORT).show();
                break;
            //服务器程序出粗
            case HTTP_RESPONSE_STATE_SERVER_WRONG:
                Toast.makeText(context, "操作失败，失败原因：" + responseStatus + "，请稍后重试！", Toast.LENGTH_SHORT).show();
                break;
            //业务逻辑出错（如：账号、密码错误）
            case HTTP_RESPONSE_STATE_CLIENT_ERROR:
                Toast.makeText(context, responseStatus, Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(context, "未知错误，请反馈！", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
