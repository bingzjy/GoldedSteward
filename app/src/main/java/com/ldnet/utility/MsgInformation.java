package com.ldnet.utility;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.ldnet.entities.Msg;
import com.ldnet.entities.User;

import java.util.Map;

//用户信息的存储和获取
public class MsgInformation {
    /***************************************************************************
     * 存储和获取用户信息 -- SharedPreference
     ***************************************************************************/
    // 用户信息
    private final static String msgInformation = "MSG_INFORMATION";
    // 初始化User
    private final static Msg msg = new Msg();
    // 获取共享信息
    private static SharedPreferences sharedPreferences = GSApplication
            .getInstance().getSharedPreferences(msgInformation,
                    GSApplication.MODE_PRIVATE);

    // 获取当前存储的用户信息
    public static Msg getMsg() {
        // 获取存储的信息
        Map<String, ?> objects = sharedPreferences.getAll();
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
        msg.callbackId = sharedPreferences.getInt("callbackId",0);
        return msg;
    }


    // 修改当前存储的用户信息
    public static void setMsgInfo(Msg msg) {
        Editor editor = sharedPreferences.edit();
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
        editor.putInt("callbackId", msg.callbackId);
        editor.commit();
    }

    //清除保存的用户信息
    public static void clearMsgInfo() {
        Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}
