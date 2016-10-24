package com.wanglinkeji.wanglin.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/9.
 * 退出登录时，退出所有Activity
 */

public class LogoutActivityCollector {

    public static List<Activity> list_logoutActivity = new ArrayList<>();

    public static void addActivity(Activity activity){
        list_logoutActivity.add(activity);
    }

    public static void removeActivity(Activity activity){
        list_logoutActivity.remove(activity);
    }

    public static void removeAllActivity(){
        for (Activity activity : list_logoutActivity){
            if (!activity.isFinishing()){
                activity.finish();
            }
        }
    }

}
