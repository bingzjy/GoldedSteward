package com.ldnet.utility;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.ldnet.entities.User;
import com.ldnet.utility.GSApplication;

import java.util.Map;

//用户信息的存储和获取
public class UserInformation {
    /***************************************************************************
     * 存储和获取用户信息 -- SharedPreference
     ***************************************************************************/
    // 用户信息
    private final static String userInformation = "USER_INFORMATION";
    // 初始化User
    private final static User userInfo = new User();
    // 获取共享信息
    private static SharedPreferences sharedPreferences = GSApplication
            .getInstance().getSharedPreferences(userInformation,
                    GSApplication.MODE_PRIVATE);


    private static SharedPreferences sharedPreferences2 = GSApplication
            .getInstance().getSharedPreferences("PUSH_INFO",
                    GSApplication.MODE_PRIVATE);
    // 获取当前存储的用户信息
    public static User getUserInfo() {
        // 获取存储的信息
        Map<String, ?> objects = sharedPreferences.getAll();
        userInfo.UserId = (String) objects.get("UserId");
        userInfo.UserName = (String) objects.get("UserName");
        userInfo.UserPhone = (String) objects.get("UserPhone");
        userInfo.UserPassword = (String) objects.get("UserPassword");
        userInfo.UserThumbnail = (String) objects.get("UserThumbnail");
        userInfo.PropertyId = (String) objects.get("PropertyId");
        userInfo.PropertyName = (String) objects.get("PropertyName");
        userInfo.PropertyThumbnail = (String) objects.get("PropertyThumbnail");
        userInfo.PropertyPhone = (String) objects.get("PropertyPhone");
        userInfo.HouseId = (String) objects.get("HouseId");
        userInfo.HouseName = (String) objects.get("HouseName");
        userInfo.CommunityId = (String) objects.get("CommunityId");
        userInfo.CommuntiyName = (String) objects.get("CommuntiyName");
        userInfo.CommuntiyAddress = (String) objects.get("CommuntiyAddress");
        userInfo.CommuntiyLatitude = (String) objects.get("CommuntiyLatitude");
        userInfo.CommuntiyLongitude = (String) objects.get("CommuntiyLongitude");
        userInfo.CommuntiyCityId = (String) objects.get("CommuntiyCityId");
        userInfo.CZAUserId=(String)objects.get("CZAUserId");
        userInfo.CZAID=(String)objects.get("CZAID");
        return userInfo;
    }


    // 修改当前存储的用户信息
    public static void setUserInfo(User user) {
        Editor editor = sharedPreferences.edit();
        // 存储信息
        editor.putString("UserId", user.UserId);
        editor.putString("UserName", user.UserName);
        editor.putString("UserPhone", user.UserPhone);
        editor.putString("UserPassword", user.UserPassword);
        editor.putString("UserThumbnail", user.UserThumbnail);
        editor.putString("PropertyId", user.PropertyId);
        editor.putString("PropertyName", user.PropertyName);
        editor.putString("PropertyThumbnail", user.PropertyThumbnail);
        editor.putString("PropertyPhone", user.PropertyPhone);
        editor.putString("HouseId", user.HouseId);
        editor.putString("HouseName", user.HouseName);
        editor.putString("CommunityId", user.CommunityId);
        editor.putString("CommuntiyName", user.CommuntiyName);
        editor.putString("CommuntiyAddress", user.CommuntiyAddress);
        editor.putString("CommuntiyLatitude", user.CommuntiyLatitude);
        editor.putString("CommuntiyLongitude", user.CommuntiyLongitude);
        editor.putString("CommuntiyCityId", user.CommuntiyCityId);
        editor.putString("CZAUserId",user.CZAUserId);
        editor.putString("CZAID",user.CZAID);
        editor.commit();
    }

    //清除保存的用户信息
    public static void clearUserInfo() {
        Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }


    public static void setPushId(String pushId){
        Editor editor=sharedPreferences2.edit();
        editor.putString("push_id",pushId);
        editor.commit();
    }

    public static String getPushId(){
        return sharedPreferences2.getString("push_id","");
    }
}
