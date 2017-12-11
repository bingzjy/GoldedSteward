package com.ldnet.utility;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.ldnet.entities.Msg;
import com.ldnet.entities.MsgCenter;

import java.util.Map;

//用户信息的存储和获取
public class MsgCenterInformation {
    /***************************************************************************
     * 存储和获取用户信息 -- SharedPreference
     ***************************************************************************/
    // 用户信息
    private final static String msgCenterInformation = "MSG_CENTER_INFORMATION";
    // 初始化User
    private final static MsgCenter msg = new MsgCenter();
    // 获取共享信息
    private static SharedPreferences sharedPreferences = GSApplication
            .getInstance().getSharedPreferences(msgCenterInformation,
                    GSApplication.MODE_PRIVATE);

    // 获取当前存储的用户信息
    public static MsgCenter getMsg() {
        // 获取存储的信息
        Map<String, ?> objects = sharedPreferences.getAll();
        msg.msg = (String) objects.get("msgCenter");
        return msg;
    }

    // 修改当前存储的用户信息
    public static void setMsgInfo(MsgCenter msg) {
        Editor editor = sharedPreferences.edit();
        // 存储信息
        editor.putString("msgCenter", msg.msg);
        editor.commit();
    }

    //清除保存的用户信息
    public static void clearMsgInfo() {
        Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}
