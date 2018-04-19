package com.ldnet.utility.sharepreferencedata;

import android.content.SharedPreferences;
import com.ldnet.entities.KeyChain;
import com.ldnet.activity.commen.GSApplication;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lee on 2017/8/25.
 */
public class KeyCache {
    private static String keyCache = "KEY_CACHE";
    private static SharedPreferences sharedPreferences = GSApplication
            .getInstance().getSharedPreferences(keyCache,
                    GSApplication.MODE_PRIVATE);

    public static void saveKey(List<KeyChain> keyChains,String houseInfo) {
        String key = null;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> keySet = new HashSet<>();
        if (keyChains != null) {
            for (KeyChain keyChain : keyChains) {
                keySet.add(keyChain.getId() + "," + keyChain.getPassword());
            }
            editor.putStringSet("key", keySet);
        } else {
            editor.putStringSet("key", null);
        }
        editor.putString("houseInfo",houseInfo);
        editor.commit();
    }


    public static Set<String> getKeyCache() {
        return sharedPreferences.getStringSet("key", null);
    }

    public static String getCurrentHouse(){
        return sharedPreferences.getString("houseInfo","");
    }
}
