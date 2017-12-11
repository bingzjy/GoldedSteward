package com.ldnet.utility;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lee on 2017/10/24
 */

public class ActivityUtil {
    public static List<Activity> activityList=new ArrayList<>();

    public static void addActivity(Activity activity){
        activityList.add(activity);
    }

    public static void removeActivty(Activity activity) {
        activityList.remove(activity);
    }

    public static Activity getTopActivity(){
        if (activityList.isEmpty()){
            return null;
        }
        return activityList.get(activityList.size()-1);
    }

    public static void finishAllActivity(){
        for (Activity activity:activityList){
            if (!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
