package com.ldnet.utility;

import android.content.SharedPreferences;
import com.ldnet.entities.User;

/**
 * Created by lee on 2017/5/4.
 */
public class SmsSendRecord {
    private static String smsSendRecord="smsSendRecord";
    private static SharedPreferences sharedPreferences = GSApplication
            .getInstance().getSharedPreferences(smsSendRecord,
                    GSApplication.MODE_PRIVATE);


    // 修改当前存储的用户信息
    public static void setUserInfo(User user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
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
        editor.commit();
    }

}
