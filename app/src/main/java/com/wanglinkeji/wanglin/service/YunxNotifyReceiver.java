package com.wanglinkeji.wanglin.service;

import android.content.Context;

import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.booter.ECNotifyReceiver;
import com.yuntongxun.ecsdk.im.group.ECGroupNoticeMessage;

/**
 * Created by Administrator on 2016/10/21.
 * IM通讯的通知Receiver
 */

public class YunxNotifyReceiver extends ECNotifyReceiver {
    @Override
    public void onReceivedMessage(Context context, ECMessage ecMessage) {

    }

    @Override
    public void onReceiveGroupNoticeMessage(Context context, ECGroupNoticeMessage ecGroupNoticeMessage) {

    }

    @Override
    public void onNotificationClicked(Context context, int i, String s) {

    }
}
