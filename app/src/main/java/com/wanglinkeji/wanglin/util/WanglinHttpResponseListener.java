package com.wanglinkeji.wanglin.util;

import org.json.JSONObject;

/**
 * Created by Administrator on 2016/9/2.
 * 通用Http请求回调接口
 */
public interface WanglinHttpResponseListener {

    //和服务器通信成功，并且获取状态为成功
    public void onSuccessResponse(JSONObject jsonObject_response);

    //和服务器通信成功，但是获取状态为不成功
    public void onConnectingError();

    //和服务器建立连接错误
    public void onDisconnectError();

}
