package com.ldnet.utility;

import android.content.Context;
import android.content.SharedPreferences;

import com.dh.bluelock.object.LEDevice;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangjinye
 * @name GoldedSteward2
 * @class name：com.ldnet.utility
 * @class describe
 * @time 2018/3/16 16:30
 * @change
 * @chang time
 * @class describe
 */

public class SPUtil {
    private static SharedPreferences sp = GSApplication.getInstance().getSharedPreferences("KEY_LEDVICE", Context.MODE_PRIVATE);

    public static void saveLedvice(LEDevice leDevice) {
        List<LEDevice> list = getOpenedLedives();
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(leDevice);

        String json = new Gson().toJson(list);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("LEDVICE", json);
    }

    public static List<LEDevice> getOpenedLedives() {
        String json = sp.getString("LEDVICE", "");
        List<LEDevice> list = new Gson().fromJson(json, new TypeToken<List<LEDevice>>() {
        }.getType());
        return list;
    }

//    public LEDevice getConnectedLedvice(){
//
//    }
}
