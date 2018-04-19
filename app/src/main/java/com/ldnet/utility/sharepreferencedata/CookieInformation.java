package com.ldnet.utility.sharepreferencedata;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.ldnet.entities.Cookies;
import com.ldnet.activity.commen.GSApplication;

import java.util.Map;

//用户信息的存储和获取
public class CookieInformation {
    /***************************************************************************
     * 存储和获取用户信息 -- SharedPreference
     ***************************************************************************/
    // 用户信息
    private final static String cookieInformation = "COOKIE_INFORMATION";
    // 初始化User
    private final static Cookies cookieInfo = new Cookies();
    // 获取共享信息
    private static SharedPreferences sharedPreferences = GSApplication
            .getInstance().getSharedPreferences(cookieInformation,
                    GSApplication.MODE_PRIVATE);

    // 获取当前存储的用户信息
    public static Cookies getUserInfo() {
        // 获取存储的信息
        Map<String, ?> objects = sharedPreferences.getAll();
        cookieInfo.setDomain((String) objects.get("domain"));
        cookieInfo.setCookieinfo((String)objects.get("cookieinfo"));

        return cookieInfo;
    }

    // 修改当前存储的用户信息
    public static void setCookieInfo(String domain,String cookieinfo) {
        Editor editor = sharedPreferences.edit();
        // 存储信息
        editor.putString("domain", domain);
        editor.putString("cookieinfo", cookieinfo);
        editor.commit();
    }

    //清除保存的用户信息
    public static void clearCookieInfo() {
        Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}
