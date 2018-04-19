package com.ldnet.utility.sharepreferencedata;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.ldnet.activity.commen.GSApplication;

//用户信息的存储和获取
public class TokenInformation {

    /***************************************************************************
     * 存储和获取用户信息 -- SharedPreference
     ***************************************************************************/
    // 用户信息
    private final static String tokenInformation = "TOKEN_INFORMATION";
    // 初始化User
    private static String token = "";
    // 获取共享信息
    private static SharedPreferences sharedPreferences = GSApplication
            .getInstance().getSharedPreferences(tokenInformation,
                    GSApplication.MODE_PRIVATE);

    // 获取当前存储的用户信息
    public static String getTokenInfo() {
        // 获取存储的信息
        token = sharedPreferences.getString("tokeninfo", "");
        return token;
    }

    // 修改当前存储的用户信息
    public static void setTokenInfo(String cookieinfo) {
        Editor editor = sharedPreferences.edit();
        // 存储信息
        editor.putString("tokeninfo", cookieinfo);
        editor.commit();
    }

    //清除保存的用户信息
    public static void clearTokenInfo() {
        Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}
