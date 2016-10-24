package com.wanglinkeji.wanglin.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/7.
 * “登录”“注册”活动管理器
 */
public class LoginRegisterActivityCollector {

    public static List<Activity> list_activity = new ArrayList<>();

    public static void addActivity(Activity activity){
        list_activity.add(activity);
    }

    public static void removeActivity(Activity activity){
        list_activity.remove(activity);
    }

    public static void removeAll(){
        for (Activity activity : list_activity){
            if (!activity.isFinishing()){
                activity.finish();
            }
        }
    }

}
