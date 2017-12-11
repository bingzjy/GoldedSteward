package com.ldnet.utility;

import android.content.SharedPreferences;

import com.ldnet.entities.Msg;
import com.ldnet.entities.Type;

import java.util.List;
import java.util.Map;

/**
 * Created by lee on 2017/11/14
 */

public class PushMessage {

    private static final String pushInformation="PUSH_INFOMATION";
    private static SharedPreferences sharedPreferences=GSApplication.getInstance().getSharedPreferences(pushInformation,GSApplication.MODE_PRIVATE);
    // 初始化User
    private final static Msg msg = new Msg();

    //获取小红点信息
    public static Msg getPushInfo(){
        Map<String,?> object=sharedPreferences.getAll();
        msg.NOTICE = sharedPreferences.getBoolean("NOTICE",false);
        msg.COMMUNICATION = sharedPreferences.getBoolean("COMMUNICATION",false);
        msg.REPAIRS = sharedPreferences.getBoolean("REPAIRS",false);
        msg.COMPLAIN = sharedPreferences.getBoolean("COMPLAIN",false);
        msg.FEEDBACK = sharedPreferences.getBoolean("FEEDBACK",false);
        msg.FEE = sharedPreferences.getBoolean("FEE",false);
        msg.MESSAGE = sharedPreferences.getBoolean("MESSAGE",false);
        msg.PAGE = sharedPreferences.getBoolean("PAGE",false);
        msg.ORDER = sharedPreferences.getBoolean("ORDER",false);
        msg.OTHER = sharedPreferences.getBoolean("OTHER",false);
        msg.PROPERTY_MSG=sharedPreferences.getBoolean("PROPERTY_MSG",false);
        return msg;
    }

    // 修改当前存储的用户信息
    public static void setPushInformation(Msg msg) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // 存储信息
        editor.putBoolean("NOTICE", msg.NOTICE);
        editor.putBoolean("COMMUNICATION", msg.COMMUNICATION);
        editor.putBoolean("REPAIRS", msg.REPAIRS);
        editor.putBoolean("COMPLAIN", msg.COMPLAIN);
        editor.putBoolean("FEEDBACK", msg.FEEDBACK);
        editor.putBoolean("FEE", msg.FEE);
        editor.putBoolean("MESSAGE", msg.MESSAGE);
        editor.putBoolean("PAGE", msg.PAGE);
        editor.putBoolean("ORDER", msg.ORDER);
        editor.putBoolean("OTHER", msg.OTHER);
        editor.putBoolean("PROPERTY_MSG",msg.PROPERTY_MSG);
        editor.commit();
    }


    //清除保存的用户信息
    public static void clearMsgInfo() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }


    //（0 通知公告,1 投诉,2 报修,3 沟通,4 缴费,5 网页,6 意见反馈,7 订单,8 其他,9物业消息）
    public static Msg setMsg(List<Type> list){
        Msg msg=new Msg();
        String allTypes=Utility.ListToString2(list);

        if (allTypes.contains("0")){
            msg.NOTICE=true;
        }

        if (allTypes.contains("1")){
            msg.COMPLAIN=true;
        }

        if (allTypes.contains("2")){
            msg.REPAIRS=true;
        }

        if (allTypes.contains("3")){
            msg.COMMUNICATION=true;
        }

        if (allTypes.contains("4")){
            msg.FEE=true;
        }

        if (allTypes.contains("5")){
            msg.PAGE=true;
        }

        if (allTypes.contains("6")){
            msg.FEEDBACK=true;
        }

        if (allTypes.contains("7")){
            msg.ORDER=true;
        }

        if (allTypes.contains("8")){
            msg.OTHER=true;
        }

        if(allTypes.contains("9")){
            msg.PROPERTY_MSG=true;
        }
        return msg;
    }
}
