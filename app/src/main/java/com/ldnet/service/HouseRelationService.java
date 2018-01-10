package com.ldnet.service;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.entities.InfoBarType;
import com.ldnet.entities.OwnerRoom;
import com.ldnet.entities.OwnerRoomRelation;
import com.ldnet.utility.CookieInformation;
import com.ldnet.utility.DataCallBack;
import com.ldnet.utility.Services;
import com.ldnet.utility.UserInformation;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;

import static android.R.string.ok;

/**
 * @author zhangjinye
 * @name GoldedSteward2
 * @class name：com.ldnet.service
 * @time 2018/1/3 10:11
 * @class 我的家属相关接口
 */

public class HouseRelationService extends BaseService {

    private static final String TAG = "HouseRelationService";

    public HouseRelationService(Context context) {
        this.mContext = context;
    }

    //获取业主的房屋绑定信息
    public void getMyRoomBindRelation(final Handler handler) {
        String url = Services.mHost + "API/Resident/GetMyRoomBindResident/" + UserInformation.getUserInfo().getUserId();
        OkHttpService.get(url).execute(new DataCallBack(mContext, handler) {
            @Override
            public void onResponse(String s, int i) {
                super.onResponse(s, i);
                Log.e(TAG, "getMyRoomBindRelation:" + s);
                try {
                    if (checkJsonDataSuccess(s, handler)) {
                        JSONObject obj = new JSONObject(s);
                        JSONObject jsonData = new JSONObject(obj.getString("Data"));

                        Gson gson = new Gson();
                        Type type = new TypeToken<List<OwnerRoomRelation>>() {
                        }.getType();
                        List<InfoBarType> list = gson.fromJson(jsonData.optString("Obj"), type);

                        if (list != null && list.size() > 0) {
                            Message msg = handler.obtainMessage();
                            msg.what = DATA_SUCCESS;
                            msg.obj = list;
                            handler.sendMessage(msg);
                        } else {
                            handler.sendEmptyMessage(DATA_SUCCESS_OTHER);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    //业主添加绑定关系
    public void addMyRoomBindRelation(final String name, final String tel, final String residentType, final String roomID,
                                      final String sDate, final String eDate, final Handler handler) {
        String url = Services.mHost + "API/Resident/SetMyRoomBindResident";
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("Name", name);
        hashMap.put("Tel", tel);
        hashMap.put("ResidentType", residentType);
        hashMap.put("RoomId", roomID);
        hashMap.put("Leasedates", sDate);
        hashMap.put("Leasedatee", eDate);
        hashMap.put("CurrentLoginUserId", UserInformation.getUserInfo().UserId);

        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";

        Log.e(TAG, "addMyRoomBindRelation--params:" + Services.json(hashMap));
        String md5 = UserInformation.getUserInfo().getUserPhone() +
                aa + aa1 + Services.json(hashMap) + Services.TOKEN;

        OkHttpUtils.post().url(url)
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .addParams("Name", name)
                .addParams("Tel", tel)
                .addParams("ResidentType", residentType)
                .addParams("RoomId", roomID)
                .addParams("Leasedates", sDate)
                .addParams("Leasedatee", eDate)
                .addParams("CurrentLoginUserId", UserInformation.getUserInfo().UserId)
                .build()
                .execute(new DataCallBack(mContext, handler) {
                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        Log.e(TAG, "addMyRoomBindRelation:" + s);
                        if (checkJsonDataSuccess(s, handler)) {
                            handler.sendEmptyMessage(DATA_SUCCESS);
                        }
                    }
                });
    }


    //租户续约  API/Resident/SetRenewRoomResident
    public void SetRenewRoomResident(final String residentID, final String roomID, final String sdate, final String eDate, final Handler handler) {
        String url = Services.mHost + "API/Resident/SetRenewRoomResident";
        HashMap<String, String> params = new HashMap<>();
        params.put("ResidentId", residentID);
        params.put("RoomId", roomID);
        params.put("Leasedates", sdate);
        params.put("Leasedatee", eDate);
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";

        Log.e(TAG, "SetRenewRoomResident--params:" + Services.json(params));
        String md5 = UserInformation.getUserInfo().getUserPhone() +
                aa + aa1 + Services.json(params) + Services.TOKEN;

        OkHttpUtils.post().url(url)
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .addParams("ResidentId", residentID)
                .addParams("RoomId", roomID)
                .addParams("Leasedates", sdate)
                .addParams("Leasedatee", eDate)
                .build()
                .execute(new DataCallBack(mContext, handler) {
                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        Log.e(TAG, "SetRenewRoomResident:" + s);

                        if (checkJsonDataSuccess(s, handler)) {
                            handler.sendEmptyMessage(DATA_SUCCESS);
                        }
                    }
                });
    }


    //获取业主的所有房产
    public void getOwnerRooms(final Handler handler) {
        String url = Services.mHost + "API/Resident/GetResidetnOwnerRoom/" + UserInformation.getUserInfo().getUserId();
        OkHttpService.get(url).execute(new DataCallBack(mContext, handler) {
            @Override
            public void onResponse(String s, int i) {
                super.onResponse(s, i);
                Log.e(TAG, "getOwnerRooms:" + s);
                if (checkJsonDataSuccess(s, handler)) {
                    try {
                        JSONObject object = new JSONObject(s);
                        JSONObject jsonObject = new JSONObject(object.getString("Data"));
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<OwnerRoom>>() {
                        }.getType();
                        List<OwnerRoom> list = gson.fromJson(jsonObject.getString("Obj"), type);

                        if (list != null && list.size() > 0) {
                            Message msg = handler.obtainMessage();
                            msg.what = DATA_SUCCESS;
                            msg.obj = list;
                            handler.sendMessage(msg);
                        } else {
                            handler.sendEmptyMessage(DATA_SUCCESS_OTHER);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }
}
