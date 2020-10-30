package com.tokenbank.base;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;

/**
 * 关闭所有页面，退出app
 */
public class SysApplication {
    private static List<Activity> listActivity = new ArrayList<>();

    public static void addActivity(Activity activity){
        listActivity.add(activity);
    }

    public static void removeActivity(Activity activity){
        listActivity.remove(activity);
    }

    public static  void finish(){
        for (Activity activity: listActivity){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
//